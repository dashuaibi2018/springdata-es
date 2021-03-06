package com.deji.demo.service;

import cn.hutool.core.util.StrUtil;
import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.entity.PushMsg;
import com.deji.demo.bean.req.BatchAddReq;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.bean.req.PushMsgSkuReq;
import com.deji.demo.mapper.MerchantSkuRepository;
import com.deji.demo.mapper.PushMsgRepository;
import com.deji.demo.util.DataUtils;
import com.deji.demo.util.ESUtils;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
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
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoEsService {

    final DBService dbService;

    final ElasticsearchRestTemplate esRestTemplate;

    final MerchantSkuRepository merchantRepository;

    final PushMsgRepository pushMsgRepository;

    final ESUtils esUtils;


    /**
     * @param req
     * @description: 批量操作
     * @return: int
     * @author: sj
     * @time: 2021/6/2 6:29 下午
     */
    public int batchAdd(BatchAddReq req) throws Exception {

        Class<?> _class = null;
        List<?> beanList = null;
        if (StrUtil.equals(req.getIndexName(), "merchant_sku")) {
            _class = MerchantSku.class;
            beanList = dbService.findAllMerchantSku();
        } else if (StrUtil.equals(req.getIndexName(), "push_msg")) {
            _class = PushMsg.class;
            beanList = dbService.findAllPushMsg();
        }

        int count = esUtils.batchAdd(_class, beanList);
        return count;
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

        Pageable pageable = PageRequest.of(req.getPageNum(), req.getPageSize(), Sort.Direction.DESC, "createTime");
        List<MerchantSku> content = merchantRepository.search(fuzzyQueryBuilder, pageable).getContent();

        return content;
    }


    public List<MerchantSku> nativeQuery(MerchantSkuReq req) {
        //创建bool多条件查询
        BoolQueryBuilder mustQuery = QueryBuilders.boolQuery().
                must(QueryBuilders.matchQuery("sku_name", req.getSkuName()));//.must(deleteflag)
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
        highlightBuilder.preTags("<span class='highlighted'>")
                .postTags("</span>")
                .field("sku_name");//哪个字段高亮
        //使用分页查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(mustQuery)
                .withHighlightFields(
                        new HighlightBuilder.Field("sku_name"),
                        new HighlightBuilder.Field("supplier_name"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"))
                .withPageable(PageRequest.of(req.getPageNum(), req.getPageSize(), Sort.Direction.DESC, "create_time"))
//                .withSort(order)
//                .withHighlightBuilder(highlightBuilder)
//                .withHighlightFields(
//                        new HighlightBuilder.Field("sku_name").preTags("<span style=\"color:red\">").postTags("</span>"),
//                        new HighlightBuilder.Field("supplier_name").preTags("<span style=\"color:red\">").postTags("</span>"))
//                .withSourceFilter(new SourceFilter() {
//                                      @Override
//                                      public String[] getIncludes() {
//                                          return new String[]{"create_time", "sku_name", "supplier_name"};
//                                      }
//
//                                      @Override
//                                      public String[] getExcludes() {
//                                          return new String[0];
//                                      }
//                                  });
                .withFields("create_time", "sku_name", "supplier_name");

        System.out.println(queryBuilder.toString());
        //
        SearchHits<MerchantSku> hits = esRestTemplate.search(queryBuilder.build(), MerchantSku.class, IndexCoordinates.of("merchant_sku"));
        List<MerchantSku> skuList = DataUtils.hitsToResList(hits);

        for (SearchHit hit : hits.getSearchHits()) {

            System.out.println(hit.getContent());
            String val = String.valueOf(hit.getHighlightField("skuName").get(0));
//            recordList.add(sourceAsMap);

            System.out.println(val);
        }

        return skuList;

    }


    /**
     * @param req
     * @description: 原生查询 SearchSourceBuilder
     * @return: com.deji.demo.bean.ResultDto
     * @author: sj
     * @time: 2021/6/2 6:28 下午
     */
    public ResultDto searchSourceQuery(MerchantSkuReq req) {

//        BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
//        queryBuilders.must(QueryBuilders.matchQuery("sku_name", req.getSkuName()));
//        System.out.println(queryBuilders.toString());

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

        return resultDto;
    }


    public ResultDto pushMsgQuery(PushMsgSkuReq req) {

//        ArrayList<String> receivedList = new ArrayList<>();
//        Collections.addAll(receivedList, "0", "1", "2");
//        ArrayList<String> msgTypeList = new ArrayList<>();
//        Collections.addAll(msgTypeList, "207");
//        reqMap.put("receivedList", receivedList);
//        reqMap.put("msgTypeList", msgTypeList);
//        reqMap.put("user_id", "bc775af283e445e3a44b25b9bb9d9817");
//        reqMap.put("app_name", "CarFleetMan");
//        reqMap.put("push_mode", "0");
//        reqMap.put("clean_flag", "1");
//        reqMap.put("pageFrom", "0");
//        reqMap.put("pageSize", "20");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("user_id", req.getUserId()))
                .must(QueryBuilders.termsQuery("msg_type", req.getMsgTypeList()))
                .must(QueryBuilders.termQuery("push_mode", req.getPushMode()))
                .mustNot(QueryBuilders.termQuery("clean_flag", req.getCleanFlag()))
        ).fetchSource("app_name,received,msg_type".split(","), null);  //无效

//                .from(pageFrom).size(pageSize)
//        searchSourceBuilder.sort("alarm_time", SortOrder.DESC).from(pageFrom).size(pageSize);
        System.out.println(searchSourceBuilder);

        Pageable pageable = PageRequest.of(req.getPageFrom(), req.getPageSize(), Sort.Direction.DESC, "push_time");
//        Page<PushMsg> search = pushMsgRepository.pushMsgQuery(searchSourceBuilder.query(), pageable);

        Page<PushMsg> search = pushMsgRepository.pushMsgQuery(pageable);

        System.out.println(search.getContent());
        ResultDto resultDto = ESUtils.transToResDto(search);

        return resultDto;
    }


}
