package com.bitian.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "br")
public class BaseForm implements Serializable {
    private Long id;
    private List<Long> ids;
    private String key;
    private Integer pn=1;
    private Integer ps=20;
    private String sortName;
    private String sortType;
    private ExportParams exportParams=new ExportParams();
    private List<QueryGroup> _groups=new ArrayList<>();
    private Map<String,Object> _sql_data;
    private String _sql;
    /**
     * 分页查询总数量
     */
    private Integer _total;
    /**
     * 是否开启分页
     */
    private Boolean pagination=false;

    public <T> Page<T> toPage(List<T> list){
        return new Page<>(this.pn,this.ps,this._total,list);
    }

    public BaseForm pagination(boolean param){
        this.pagination=param;
        return this;
    }
}
