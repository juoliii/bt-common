package com.bitian.common.dto;

import com.bitian.common.enums.QueryConditionType;
import com.bitian.common.enums.SuperQueryCondition;
import com.bitian.common.enums.SuperQueryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryGroup {

    private SuperQueryCondition condition;

    private List<QueryDetail> details=new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
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
        //右侧类型
        private QueryConditionType conditionType=QueryConditionType.specificValue;

    }

}
