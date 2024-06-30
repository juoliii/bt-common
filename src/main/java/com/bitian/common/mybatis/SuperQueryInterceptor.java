package com.bitian.common.mybatis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONValidator;
import com.bitian.common.dto.BaseForm;
import com.bitian.common.dto.QueryGroup;
import com.bitian.common.enums.SuperQueryCondition;
import com.bitian.common.util.PrimaryKeyUtil;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author admin
 */
@Intercepts({
        @Signature(type = Executor.class, method = "queryCursor", args = { MappedStatement.class,Object.class, RowBounds.class}),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class SuperQueryInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object param=args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        BaseForm form=null;
        if( param instanceof BaseForm){
            form= (BaseForm) param;
        }else if(param instanceof Map){
            for (Object value : ((Map<?, ?>) param).values()) {
                if(value instanceof BaseForm){
                    form=((BaseForm) value);
                    break;
                }
            }
        }
        if(args.length == 3){
            return invocation.proceed();
        } else if(args.length==4){
            //4 个参数时
            ResultHandler resultHandler = (ResultHandler) args[3];
            boundSql = ms.getBoundSql(param);
            if(form!=null)this.handleSql(boundSql,form,ms.getConfiguration());
            cacheKey = executor.createCacheKey(ms, param, rowBounds, boundSql);
            return executor.query(ms, param, rowBounds, resultHandler, cacheKey, boundSql);
        } else {
            //6 个参数时
            ResultHandler resultHandler = (ResultHandler) args[3];
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
            if(form!=null)this.handleSql(boundSql,form,ms.getConfiguration());
            return executor.query(ms, param, rowBounds, resultHandler, cacheKey, boundSql);
        }

    }

    private boolean hasWhere(String sql) throws Exception {
        PlainSelect select= (PlainSelect)CCJSqlParserUtil.parse(sql);
        return select.getWhere()!=null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    private void handleSql(BoundSql boundSql, BaseForm form, Configuration configuration) throws Exception {
        boolean hasWhere=this.hasWhere(boundSql.getSql());
        List<QueryGroup> groups=form.get_groups();
        if(groups.size()>0){
            groups.get(0).setCondition(hasWhere?SuperQueryCondition.and:null);
        }else{
            return;
        }
        List<ParameterMapping> parameterMappings=new ArrayList<>();
        parameterMappings.addAll(boundSql.getParameterMappings());
        StringBuffer sb=new StringBuffer();
        for (QueryGroup group : groups) {
            StringBuffer dt=new StringBuffer();
            for (QueryGroup.QueryDetail detail : group.getDetails()) {
                if(detail.getValue()==null||detail.getValue().toString().length()==0){
                    continue;
                }
                dt.append(" "+(detail.getCondition()==null?"":detail.getCondition().toString())+" ");
                if(StringUtils.isNotBlank(detail.getAlias())){
                    dt.append(" "+detail.getAlias()+".");
                }
                if(detail.getDynamic()){
                    dt.append("data->>'$."+detail.getKey()+"' ");
                }else{
                    dt.append(detail.getKey()+" ");
                }

                String key=detail.getKey()+"_"+ PrimaryKeyUtil.getUUID();

                switch (detail.getType()){
                    case eq:{
                        //等于
                        dt.append(" = ?");
                        parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                        boundSql.setAdditionalParameter(key,detail.getValue());
                        break;
                    }
                    case in:{
                        //多值等于
                        List<?> strs=null;
                        if(detail.getValue() instanceof List){
                            strs= (List<?>) detail.getValue();
                        }else if(detail.getValue() instanceof String){
                            String value=detail.getValue().toString();
                            if(StringUtils.isNotBlank(value)){
                                if(JSONValidator.from(value).validate()){
                                    strs= JSONArray.parseArray(value);
                                }else{
                                    strs=Arrays.asList(StringUtils.split(value,"\n"));
                                }
                            }
                        }
                        dt.append(" in (");
                        for (int i = 0; i < strs.size(); i++) {
                            dt.append(" ? ");
                            if(i!=strs.size()-1){
                                dt.append(",");
                            }
                            parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                            boundSql.setAdditionalParameter(key,strs.get(i));
                        }
                        dt.append(")");
                        break;
                    }
                    case ne:{
                        //不等于
                        dt.append(" != ? ");
                        parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                        boundSql.setAdditionalParameter(key,detail.getValue());
                        break;
                    }
                    case like:{
                        // like
                        dt.append(" like ? ");
                        parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                        boundSql.setAdditionalParameter(key,"%"+detail.getValue()+"%");
                        break;
                    }
                    case lt:{
                        //小于
                        dt.append(" < ? ");
                        parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                        boundSql.setAdditionalParameter(key,detail.getValue());
                        break;
                    }
                    case gt:{
                        //大于
                        dt.append(" > ? ");
                        parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                        boundSql.setAdditionalParameter(key,detail.getValue());
                        break;
                    }
                    case lte:{
                        //小于等于
                        dt.append(" <= ? ");
                        parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                        boundSql.setAdditionalParameter(key,detail.getValue());
                        break;
                    }
                    case gte:{
                        //大于等于
                        dt.append(" >= ? ");
                        parameterMappings.add(new ParameterMapping.Builder(configuration,key,Object.class).build());
                        boundSql.setAdditionalParameter(key,detail.getValue());
                        break;
                    }

                }
            }
            if(dt.length()>0){
                sb.append(" "+(group.getCondition()==null?"":group.getCondition().toString())+" (");
                sb.append(dt);
                sb.append(" ) ");
            }

        }
        String modifiedSql = boundSql.getSql() + (hasWhere?sb.toString():(" where "+sb.toString()));
        Field sqlField = BoundSql.class.getDeclaredField("sql");
        sqlField.setAccessible(true);
        sqlField.set(boundSql, modifiedSql);
        Field parameterMappingsField=BoundSql.class.getDeclaredField("parameterMappings");
        parameterMappingsField.setAccessible(true);
        parameterMappingsField.set(boundSql,parameterMappings);
    }

}
