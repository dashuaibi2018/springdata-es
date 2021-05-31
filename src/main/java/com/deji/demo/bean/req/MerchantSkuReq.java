package com.deji.demo.bean.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantSkuReq extends BaseQueryReq{

    private String skuName;

    private Integer pageNo = 0;
    private Integer onePageNum = 10;

}