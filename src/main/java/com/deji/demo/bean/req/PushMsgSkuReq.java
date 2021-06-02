package com.deji.demo.bean.req;

import lombok.Data;

import java.util.List;

@Data
public class PushMsgSkuReq {

    List<String> receivedList;
    List<String> msgTypeList;
    String userId;
    String appName;
    String pushMode;
    String cleanFlag;
    int pageFrom;
    int pageSize;


}