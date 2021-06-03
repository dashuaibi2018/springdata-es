package com.deji.demo.bean.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantSkuReq extends BaseQueryReq {

    private String merchantSkuId;
    private String skuName;
    private String skuSn;
    private String skuMngCode;
    private String brandId;
    private String supplierId;
    private String approveStatus;
    private String ncSyncStatus;
    private String activeStatus;
    private String toponSkuCode;

    private List<String> categoryId;


}