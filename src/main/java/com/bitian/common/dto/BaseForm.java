package com.bitian.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
@Data
public class BaseForm implements Serializable {
    private Long id;
    private List<Long> ids;
    private String key;
    private Integer pn=1;
    private Integer ps=20;
    private String sortName;
    private String sortType;
    private List<QueryGroup> _groups=new ArrayList<>();
    private Map<String,Object> _sql_data;
}
