package com.bitian.common.dto;

import com.bitian.common.enums.SuperQueryCondition;
import com.bitian.common.enums.SuperQueryType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
@Data
public class QueryGroup {

    private SuperQueryCondition condition;

    private List<QueryDetail> details=new ArrayList<>();

    @Data
    public static class QueryDetail {

        //是否自定义字段
        private Boolean dynamic=false;
        //关系 and/or
        private SuperQueryCondition condition;
        //表别名
        private String alias;
        //字段名
        private String key;
        //字段关系 <，>，≤，≥，＝，≠，包含，
        private SuperQueryType type;
        //值、数据集
        private Object value;
        //日期类型
        private String dateType;

    }

}
