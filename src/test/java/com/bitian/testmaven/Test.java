package com.bitian.testmaven;

import com.bitian.common.dto.BaseForm;
import com.bitian.common.dto.QueryGroup;
import com.bitian.common.enums.SuperQueryType;
import com.bitian.testmaven.mapper.TestMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * @author admin
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TestMapper mapper = session.getMapper(TestMapper.class);
            BaseForm form=new BaseForm();
            QueryGroup group=new QueryGroup();
            QueryGroup.QueryDetail detail=new QueryGroup.QueryDetail();
            detail.setType(SuperQueryType.eq);
            detail.setKey("status");
            detail.setValue(1);
            group.setDetails(Arrays.asList(detail));
            form.set_groups(Arrays.asList(group));
            form.setKey("管理员");
            System.out.println(mapper.query(form));
        }
    }
}
