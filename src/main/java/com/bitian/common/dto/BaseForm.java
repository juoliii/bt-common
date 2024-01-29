package com.bitian.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
}
