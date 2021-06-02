package com.deji.demo.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)

public class ResultDto implements Serializable {
//    private String costTime;
    //    private String resultCode;
//    private String resultMessage;
//    private String tag;

    private Object data;
    private Long total; //总条数
    private long pages; //总页数
    private List<?> records;//记录
    private long currentPage;//当前页号
    private long pageSize;//一页记录数目

//    Map<String, Object> categoryMap = new HashMap<>();
}