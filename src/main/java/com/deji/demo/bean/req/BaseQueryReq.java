package com.deji.demo.bean.req;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseQueryReq implements Serializable {

    private Integer pageNo = 1;
    private Integer onePageNum = 10;

    public Integer getPageNo() {
        return pageNo - 1;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getOnePageNum() {
        return onePageNum;
    }

    public void setOnePageNum(Integer onePageNum) {
        this.onePageNum = onePageNum;
    }

}