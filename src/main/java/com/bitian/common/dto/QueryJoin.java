package com.bitian.common.dto;

import com.bitian.common.enums.QueryJoinType;
import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class QueryJoin {
    private String name;
    private QueryJoinType joinType=QueryJoinType.left_join;
    private List<QueryGroup> conditions;
}
