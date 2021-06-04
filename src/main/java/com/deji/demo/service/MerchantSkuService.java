package com.deji.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.bean.rsp.MerchantSkuRsp;
import com.deji.demo.mapper.MerchantSkuRepository;
import com.deji.demo.util.DataUtils;
import com.deji.demo.util.ESUtils;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
        Pageable pageable = PageRequest.of(req.getPageNum(), req.getPageSize(), Sort.Direction.DESC, "createTime");
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

        Pageable pageable = PageRequest.of(req.getPageNum(), req.getPageSize(), Sort.Direction.DESC, "create_time");
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

//    public List<MerchantSku> skuQuery(MerchantSkuReq req) {
//    }


    /**
     * @param req
     * @description: 获取商品列表
     * @return: java.util.List<com.deji.demo.bean.entity.MerchantSku>
     * @author: sj
     * @time: 2021/6/3 5:33 下午
     */
    public List<MerchantSku> getMerchantSkulist(MerchantSkuReq req) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (StrUtil.isNotEmpty(req.getMerchantSkuId())) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery(DataUtils.camelOrUnderline("merchantSkuId"), "*" + req.getMerchantSkuId() + "*"));//.must(deleteflag)
        }
        if (StrUtil.isNotEmpty(req.getSkuName())) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery(DataUtils.camelOrUnderline("skuName"), req.getSkuName()));
        }
        if (StrUtil.isNotEmpty(req.getSkuSn())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("skuSn"), req.getSkuSn()));
        }
        if (StrUtil.isNotEmpty(req.getSkuMngCode())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("skuMngCode"), req.getSkuMngCode()));
        }
        if (StrUtil.isNotEmpty(req.getBrandId())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("brandId"), req.getBrandId()));
        }
        if (StrUtil.isNotEmpty(req.getSupplierId())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("supplierId"), req.getSupplierId()));
        }
        if (StrUtil.isNotEmpty(req.getApproveStatus())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("approveStatus"), req.getApproveStatus()));
        }
        if (StrUtil.isNotEmpty(req.getActiveStatus())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("activeStatus"), req.getActiveStatus()));
        }
        if (StrUtil.isNotEmpty(req.getToponSkuCode())) {
            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("toponSkuCode"), req.getToponSkuCode()));
        }
        if (CollectionUtil.isNotEmpty(req.getCategoryId())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery(DataUtils.camelOrUnderline("categoryId"), req.getCategoryId()));
        }
        if (StrUtil.isNotEmpty(req.getNcSyncStatus())) {  //and (ms.nc_sync_status in (3, 4) or ms.topon_sync_status in (3, 4))
//            boolQueryBuilder.must(QueryBuilders.termQuery(DataUtils.camelOrUnderline("ncSyncStatus"), req.getNcSyncStatus()));
            boolQueryBuilder.should(QueryBuilders.termsQuery(DataUtils.camelOrUnderline("ncSyncStatus"), CollectionUtil.newArrayList(3, 4)))
                    .should(QueryBuilders.termsQuery(DataUtils.camelOrUnderline("toponSyncStatus"), CollectionUtil.newArrayList(3, 4)))
                    .minimumShouldMatch(1);
        }

        //可以使用高亮显示，就是html标签
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span class='highlighted'>")
                .postTags("</span>")
                .field("sku_name");//哪个字段高亮

        //使用分页查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightFields(
                        new HighlightBuilder.Field("sku_name"),
                        new HighlightBuilder.Field("supplier_name"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"))
                .withPageable(PageRequest.of(req.getPageNum(), req.getPageSize(), Sort.Direction.DESC, "update_time"));
//                .withFields("create_time", "sku_name", "supplier_name");  //返回特定字段

        System.out.println(queryBuilder.toString());
        //
        SearchHits<MerchantSku> hits = esRestTemplate.search(queryBuilder.build(), MerchantSku.class, IndexCoordinates.of("merchant_sku"));
        List<MerchantSku> skuList = DataUtils.hitsToResList(hits);

        // 标红字段处理
//        for (SearchHit<MerchantSku> hit : hits.getSearchHits()) {
//            MerchantSku merchantSku = hit.getContent();
//
//            System.out.println(hit.getHighlightField("skuName"));
//            String skuName = CollectionUtil.isEmpty(hit.getHighlightField("skuName")) ? "" : String.valueOf(hit.getHighlightField("skuName").get(0));
//
//            if(StrUtil.isNotEmpty(skuName)){
//                merchantSku.setSkuName(skuName);
//            }
//        }

        return skuList;
    }
}
