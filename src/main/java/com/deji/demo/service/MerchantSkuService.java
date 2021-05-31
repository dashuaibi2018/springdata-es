package com.deji.demo.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.bean.rsp.MerchantSkuRsp;
import com.deji.demo.dao.MerchantSkuRepository;
import com.deji.demo.mapper.MerchantSkuDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantSkuService {

    final ElasticsearchRestTemplate esRestTemplate;

    final MerchantSkuDBMapper mapper;

    final MerchantSkuRepository merchantRepository;


    public List<MerchantSku> findAll() {

        QueryWrapper<MerchantSku> wrapper = new QueryWrapper<>();
        return mapper.selectList(wrapper);
    }


    public List<MerchantSkuRsp> findByMerchantName(MerchantSkuReq req) {

        System.out.println(req);
        Sort sort = Sort.by("createTime").descending();
        Pageable pageable = PageRequest.of(req.getPageNo(), req.getOnePageNum(), sort);
        List<MerchantSku> list = merchantRepository.findBySkuName(req.getSkuName(), pageable);

        List<MerchantSkuRsp> reslist = list.stream().map(a -> merchantSkuToRsp(a)).collect(Collectors.toList());
        return reslist;
    }


    private MerchantSkuRsp merchantSkuToRsp(MerchantSku merchantSku){
        MerchantSkuRsp skuRsp = new MerchantSkuRsp();
        BeanUtil.copyProperties(merchantSku,skuRsp);
        skuRsp.setCreateTimeStr(LocalDateTimeUtil.format(skuRsp.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
        skuRsp.setUpdateTimeStr(LocalDateTimeUtil.format(skuRsp.getUpdateTime(),"yyyy-MM-dd HH:mm:ss"));
        skuRsp.setCreateTimeLong(Timestamp.valueOf(skuRsp.getCreateTime()).getTime());
        skuRsp.setUpdateTimeLong(Timestamp.valueOf(skuRsp.getUpdateTime()).getTime());

        return skuRsp;
    }
}
