package com.deji.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.mapper.MerchantSkuMapper;
import com.deji.demo.mapper.MerchantSkuRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoEsService {

    final ElasticsearchRestTemplate esRestTemplate;

    final MerchantSkuMapper mapper;

    final MerchantSkuRepository merchantRepository;

    public List<MerchantSku> findAll() {

        QueryWrapper<MerchantSku> wrapper = new QueryWrapper<>();
        return mapper.selectList(wrapper);
    }

    public List<MerchantSku> findByMerchantName(MerchantSkuReq req) {

//        Sort sort = Sort.by("createTime").descending();
//        Pageable pageable = PageRequest.of(req.getPageNo(), req.getOnePageNum(), sort);
        Pageable pageable = PageRequest.of(req.getPageNo() - 1, req.getOnePageNum(), Sort.Direction.DESC, "createTime");
        List<MerchantSku> list = merchantRepository.findBySkuName(req.getSkuName(), pageable);

        return list;
    }

    /**
     * @param req
     * @description: 模糊纠错查询
     * @return: java.util.List<com.deji.demo.bean.entity.MerchantSku>
     * @author: sj
     * @time: 2021/5/31 7:14 下午
     */
    public List<MerchantSku> fuzzyQuery(MerchantSkuReq req) {

        //fuzziness 即为最多纠正两个字母然后去匹配，默认为 auto（2）
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("sku_name", req.getSkuName());
        System.out.println(fuzzyQueryBuilder);

        Pageable pageable = PageRequest.of(req.getPageNo() - 1, req.getOnePageNum(), Sort.Direction.DESC, "createTime");
        List<MerchantSku> content = merchantRepository.search(fuzzyQueryBuilder, pageable).getContent();

        return content;
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

        ResultDto resultDto = new ResultDto();
        resultDto.setRecordList(list).setTotal(search.getTotalElements()).setCostTime("12s");

        return resultDto;
    }


    public List<MerchantSku> nativeQuery(MerchantSkuReq req) {
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

    /**
     * @param req
     * @description:分页并按照xx倒序
     * @return: java.util.List<com.deji.demo.bean.rsp.MerchantSkuRsp>
     * @author: sj
     * @time: 2021/5/31 5:17 下午
     */
    public ResultDto searchSourceQuery(MerchantSkuReq req) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("sku_name", "删"))
                .filter(QueryBuilders.termQuery("create_user", "admin"))
        );

        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.DESC, "create_time");
        Page<MerchantSku> search = merchantRepository.search(searchSourceBuilder.query(), pageable);

        System.out.println(search);
        List<MerchantSku> list = search.getContent();

        ResultDto resultDto = new ResultDto();
        resultDto.setRecordList(list).setTotal(search.getTotalElements());

        return resultDto;
    }

}
