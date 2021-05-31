package com.deji.demo.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.bean.rsp.MerchantSkuRsp;
import com.deji.demo.dao.MerchantSkuMapper;
import com.deji.demo.dao.MerchantSkuRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantSkuService {

    final ElasticsearchRestTemplate esRestTemplate;

    final MerchantSkuMapper mapper;

    final MerchantSkuRepository merchantRepository;


    public List<MerchantSku> findAll() {

        QueryWrapper<MerchantSku> wrapper = new QueryWrapper<>();
        return mapper.selectList(wrapper);
    }


    public List<MerchantSkuRsp> findByMerchantName(MerchantSkuReq req) {

//        Sort sort = Sort.by("createTime").descending();
//        Pageable pageable = PageRequest.of(req.getPageNo(), req.getOnePageNum(), sort);
        Pageable pageable = PageRequest.of(req.getPageNo() - 1, req.getOnePageNum(), Sort.Direction.DESC, "createTime");
        List<MerchantSku> list = merchantRepository.findBySkuName(req.getSkuName(), pageable);

        System.out.println(merchantRepository.count());

        List<MerchantSkuRsp> reslist = list.stream().map(a -> merchantSkuToRsp(a)).collect(Collectors.toList());
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
        queryBuilders.must(QueryBuilders.matchPhraseQuery("sku_name", "删"));
        System.out.println(queryBuilders.toString());
        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.DESC, "create_time");
        Page<MerchantSku> search = merchantRepository.search(queryBuilders, pageable);
//        System.out.println(search);
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

        List<MerchantSkuRsp> reslist = list.stream().map(a -> merchantSkuToRsp(a)).collect(Collectors.toList());

        ResultDto resultDto = new ResultDto();
        resultDto.setRecordList(reslist).setTotal(search.getTotalElements()).setCostTime("12s");

        return resultDto;
    }


    public List<MerchantSku> findSkuNameOwn1(MerchantSkuReq req) {
        //查询操作
        MatchQueryBuilder lastUpdateUser = QueryBuilders.matchQuery("sku_name", "删");
//        MatchQueryBuilder deleteflag = QueryBuilders.matchQuery("deleteFlag", BaseEntity.DEL_FLAG_DELETE);
        //创建bool多条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder mustQuery = boolQueryBuilder.must(lastUpdateUser);//.must(deleteflag)
        //嵌套索引，需要使用nest查询
//        mustQuery.must(QueryBuilders.nestedQuery("entityNodes", QueryBuilders.termQuery("entityNodes.node_type", recyclePaperDTO.getNodeType()), ScoreMode.None));
        //可以使用should查询，不是必需条件
//        BoolQueryBuilder nodeQueryBuilder = QueryBuilders.boolQuery();
//        nodeQueryBuilder.should(QueryBuilders.nestedQuery("entityNodes", QueryBuilders.wildcardQuery("entityNodes.parent_ids", "*," + recyclePaperDTO.getNodeId() + "*"), ScoreMode.None));
//        mustQuery.must(nodeQueryBuilder);
        //查询使用排序
        SortBuilder order = new FieldSortBuilder("create_time").order(SortOrder.DESC);
        //可以使用高亮显示，就是html标签
        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.preTags("<span class='highlighted'>")
//                .postTags(</span>)
//                .field("paperBaseName");//哪个字段高亮
        //使用分页查询
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(mustQuery).withSort(order).withHighlightBuilder(highlightBuilder)
                .withPageable(PageRequest.of(0, 10)).build();
        //进行查询，entityMapper使用默认的也可，EsPaperBase.class是需要自己映射的查询类

        IndexCoordinates of = IndexCoordinates.of("merchant_sku");
        AggregatedPage<MerchantSku> merchantSkus = esRestTemplate.queryForPage(query, MerchantSku.class, of);
        List<MerchantSku> content = merchantSkus.getContent();

        return content;

    }


    private MerchantSkuRsp merchantSkuToRsp(MerchantSku merchantSku) {
        MerchantSkuRsp skuRsp = new MerchantSkuRsp();
        BeanUtil.copyProperties(merchantSku, skuRsp);
//        skuRsp.setCreateTimeStr(LocalDateTimeUtil.format(skuRsp.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
//        skuRsp.setUpdateTimeStr(LocalDateTimeUtil.format(skuRsp.getUpdateTime(),"yyyy-MM-dd HH:mm:ss"));
        skuRsp.setCreateTimeLong(Timestamp.valueOf(skuRsp.getCreateTime()).getTime());
        skuRsp.setUpdateTimeLong(Timestamp.valueOf(skuRsp.getUpdateTime()).getTime());

        return skuRsp;
    }
}
