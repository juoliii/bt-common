package com.bitian.common.dto;

import com.bitian.common.enums.QueryJoinType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryJoin {
    private String name;
    private QueryJoinType joinType=QueryJoinType.left_join;
    private List<QueryGroup> conditions;
}
