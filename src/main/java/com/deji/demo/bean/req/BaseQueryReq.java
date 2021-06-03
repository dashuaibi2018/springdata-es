package com.deji.demo.bean.req;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseQueryReq implements Serializable {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum - 1 ;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}