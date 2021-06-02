package com.deji.demo.bean.rsp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 基本的分页返回记录集基类
 * @author
 */
@Data
public class BasePageQueryResp<T> implements Serializable {

    @JSONField(serialize = false)
    private long total;//总记录数
    @JSONField(serialize = false)
    private long pages;//总页数
    private List<T> records;//记录
    @JSONField(serialize = false)
    private long current;//当前页号
    @JSONField(serialize = false)
    private long size;//一页记录数目
}
