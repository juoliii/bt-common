package com.bitian.common.dto;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author admin
 */
@Data
public class SubQuery {
    private String name;
    private List<QueryJoin> joins;
    private List<String> columns= Arrays.asList("1");
    private List<QueryGroup> conditions;
}
