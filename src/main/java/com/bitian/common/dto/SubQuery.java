package com.bitian.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubQuery {
    private String name;
    private List<QueryJoin> joins;
    private List<String> columns= Arrays.asList("1");
    private List<QueryGroup> conditions;
}
