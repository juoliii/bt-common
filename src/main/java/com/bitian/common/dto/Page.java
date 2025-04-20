package com.bitian.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author admin
 */
@Data
public class Page<T> implements Serializable {

    private int pn;

    private int ps;

    private int pages;

    private int total;

    private List<T> list;

    public Page(int pn,int ps,Integer total,List<T> list){
        this.pn=pn;
        this.ps=ps;
        this.total=total==null?0:total;
        this.list=list;
        this.pages = this.total / this.ps + ((total % this.ps == 0) ? 0 : 1);
    }


}
