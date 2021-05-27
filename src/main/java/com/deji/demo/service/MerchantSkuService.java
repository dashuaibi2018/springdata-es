package com.deji.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deji.demo.entity.MerchantSkuDB;
import com.deji.demo.mapper.MerchantSkuDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantSkuService {

    @Autowired
    MerchantSkuDBMapper mapper;

    public List<MerchantSkuDB> findAll() {

        QueryWrapper<MerchantSkuDB> wrapper = new QueryWrapper<>();
        return mapper.selectList(wrapper);
    }




}
