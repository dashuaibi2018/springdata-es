package com.deji.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.entity.PushMsg;
import com.deji.demo.mapper.MerchantSkuMapper;
import com.deji.demo.mapper.PushMsgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DBService {

    final MerchantSkuMapper merchantSkuMapper;

    final PushMsgMapper pushMsgMapper;

    /**
     * @description: mysql
     * @param
     * @return: java.util.List<com.deji.demo.bean.entity.MerchantSku>
     * @author: sj
     * @time: 2021/5/31 7:02 下午
     */
    public List<MerchantSku> findAllMerchantSku() {

        QueryWrapper<MerchantSku> wrapper = new QueryWrapper<>();
        return merchantSkuMapper.selectList(wrapper);
    }

    public List<PushMsg> findAllPushMsg() {

        QueryWrapper<PushMsg> wrapper = new QueryWrapper<>();
        return pushMsgMapper.selectList(wrapper);
    }
}