package com.deji.demo.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)

public class ResultDto implements Serializable {
    private Long total;
    private String costTime;
    private Object data;
    //    private String resultCode;
//    private String resultMessage;
    private String tag;
    List recordList = new ArrayList<>();
    Map<String, Object> categoryMap = new HashMap<>();

}