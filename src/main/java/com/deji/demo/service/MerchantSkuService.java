package com.deji.demo.service;

import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.bean.rsp.MerchantSkuRsp;
import com.deji.demo.mapper.MerchantSkuRepository;
import com.deji.demo.util.ESUtils;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantSkuService {

    final ElasticsearchRestTemplate esRestTemplate;

    final MerchantSkuRepository merchantRepository;


    public List<MerchantSkuRsp> findByMerchantName(MerchantSkuReq req) {

//        Sort sort = Sort.by("createTime").descending();
//        Pageable pageable = PageRequest.of(req.getPageNo(), req.getOnePageNum(), sort);
        Pageable pageable = PageRequest.of(req.getPageNo(), req.getOnePageNum(), Sort.Direction.DESC, "createTime");
        List<MerchantSku> list = merchantRepository.findBySkuName(req.getSkuName(), pageable);

        System.out.println(merchantRepository.count());

        List<MerchantSkuRsp> reslist = list.stream().map(a -> ESUtils.merchantSkuToRsp(a)).collect(Collectors.toList());
        return reslist;
    }

    /**
     * @param req
     * @description:分页并按照xx倒序
     * @return: java.util.List<com.deji.demo.bean.rsp.MerchantSkuRsp>
     * @author: sj
     * @time: 2021/5/31 5:17 下午
     */
    public ResultDto findSkuNameOwn(MerchantSkuReq req) {

        BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
        queryBuilders.must(QueryBuilders.matchQuery("sku_name", req.getSkuName()));
        System.out.println(queryBuilders.toString());

        Pageable pageable = PageRequest.of(req.getPageNo(), req.getOnePageNum(), Sort.Direction.DESC, "create_time");
        Page<MerchantSku> search = merchantRepository.search(queryBuilders, pageable);

        List<MerchantSku> list = search.getContent();

        System.out.println("当前索引总条数: " + merchantRepository.count());
        System.out.println("查询结果总条数: " + search.getTotalElements());
        System.out.println("查询结果页数: " + search.getTotalPages());
        System.out.println("每页记录数: " + search.getSize());
        System.out.println("当前页: " + search.getNumber());

//        System.err.println("==============================================");
//        list.forEach(x -> {
//            System.out.println(x);
//        });

        List<MerchantSkuRsp> reslist = list.stream().map(a -> ESUtils.merchantSkuToRsp(a)).collect(Collectors.toList());
        ResultDto resultDto = ESUtils.transToResDto(search);

        return resultDto;
    }







}
