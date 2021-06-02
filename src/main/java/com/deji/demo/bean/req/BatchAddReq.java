package com.deji.demo.bean.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BatchAddReq {

    @NotBlank(message = "indexName不允许为空")
    String indexName;
}