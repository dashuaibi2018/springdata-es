package com.deji.demo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.deji.demo.bean.entity.LogEntity;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.service.DBService;
import com.deji.demo.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class EsRestTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;


    @Autowired
    DBService dbService;

    @Test
    public void testBatch() {
        String indexName = "merchant_sku";
        List<MerchantSku> beanList = dbService.findAllMerchantSku();
//        batchSave(indexName, beanList);
        batchUpdate(indexName, beanList);
    }

    /**
     * @param
     * @description: ????????????
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 13:54
     */
    @Test
    public void testMapping(Class<?> obj) {
        IndexOperations ops = esRestTemplate.indexOps(obj); //MerchantSkuES.class
//        final boolean exists = esRestTemplate.exists("c2f4efb04f7b4e3d9f57dc63fa2de713", IndexCoordinates.of("demo-log"));
        if (ops.exists()) {
            ops.delete();
        }
        // ????????????????????????Item??????@Document?????????????????????
        ops.create();
        // ????????????????????????Item?????????id???Field??????????????????????????????
        ops.putMapping(obj);
//        final Document mapping = ops.createMapping(LogEntity.class);
//        ops.putMapping();
        System.out.println(111);
    }

    @Test
    public void deleteIndex() {
        IndexOperations ops = esRestTemplate.indexOps(LogEntity.class);
        ops.delete();
    }

    /**
     * @param
     * @description: CRUD
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 14:06
     */
    @Test
    public void insert() {
        //??????mapping
//        testMapping(MerchantSkuDB.class);

        List<MerchantSku> skus = dbService.findAllMerchantSku();
//        List<MerchantSkuES> skuESList = skus.stream().map(a -> change(a)).collect(Collectors.toList());

//        skuESList.stream().forEach(a -> {
//            MerchantSkuES save = esRestTemplate.save(a);
//            System.out.println(save);
//        });

        for (MerchantSku merchantSku : skus) {
            final MerchantSku save = esRestTemplate.save(merchantSku);
            System.out.println(save);
            break;
        }
        System.out.println(111);

//        final MerchantSkuES save = esRestTemplate.save(merchantSkuES);
//        System.out.println(save);
    }

    private MerchantSku change(MerchantSku a) {
        MerchantSku skuES = new MerchantSku();
        BeanUtil.copyProperties(a, skuES);
        return skuES;
    }

    /**
     * ????????????
     *
     * @param //indexName ????????????
     * @param //bean      ?????????????????????ID????????????
     */
    @Test
    public void update() {

        String indexName = "demo-log";
        Document deafaultDoc = Document.create();
        deafaultDoc.put("rec_id", "?????????");
        deafaultDoc.put("level", "?????????");

        Document doc = Document.create();
        doc.setId("210");
        doc.put("rec_id", "110");
        doc.put("level", "warn1");
        doc.put("time", "20210601");

        UpdateQuery.Builder builder = UpdateQuery.builder(doc.getId())
                .withDocument(doc)
//                .withDocAsUpsert(false) //????????????false????????????????????????????????????????????????(?????????ID??????)????????????ElasticsearchException
//                .withDocAsUpsert(true) //??????false,true?????????????????????????????????
                .withUpsert(deafaultDoc) //????????? docAsUpsert(false)???????????????upsert????????????????????????????????????????????????request.upsert?????????????????????
                .withRetryOnConflict(1) //????????????
                .withRouting(StrUtil.toString(doc.get("rec_id"))); //?????????_id???????????????????????????????????????shard?????????????????????hash??????????????????shard???????????????
        UpdateQuery updateQuery = builder.build();

        UpdateResponse updateResponse = esRestTemplate.update(updateQuery, IndexCoordinates.of(indexName));
        System.out.println(updateResponse);

    }

    /**
     * ???????????????toJsonStr?????????localDateTime??????Long, ????????????????????????
     *
     * @param indexName ????????????
     * @param beanList  ??????????????????
     */
    public void batchSave(String indexName, List<MerchantSku> beanList) {
        List<IndexQuery> queries = new ArrayList<>();
        IndexQuery indexQuery;
        int counter = 0;
        for (MerchantSku item : beanList) {
            //??????????????????
            if (counter != 0 && counter % 50 == 0) {
                esRestTemplate.bulkIndex(queries, IndexCoordinates.of(indexName));
                queries.clear();
                System.out.println("bulkIndex counter : " + counter);
            }

            JSONObject formatObj = DataUtils.formatKey(JSONUtil.parseObj(item), false);

            Document document = DataUtils.jsonObjToDoc(formatObj);
//            System.out.println(JSONUtil.toJsonStr(formatObj));
            System.out.println(document.toJson());
            System.out.println(document.toString());

            indexQuery = new IndexQuery();
            indexQuery.setId(StrUtil.toString(item.getMerchantSkuId()));
//            indexQuery.setSource(document.toJson());  //?????????
            indexQuery.setSource(JSONUtil.toJsonStr(formatObj)); //localDateTime???????????????Long
            queries.add(indexQuery);

            counter++;
        }
        //??????????????????????????????????????????
        if (queries.size() > 0) {
            esRestTemplate.bulkIndex(queries, IndexCoordinates.of(indexName));
        }
//        esRestTemplate.refresh(MerchantSku.class);
        esRestTemplate.indexOps(IndexCoordinates.of(indexName)).refresh();
    }

    /**
     * ?????????????????????
     *
     * @param indexName ????????????
     * @param beanList  ??????????????????
     */
    public void batchUpdate(String indexName, List<MerchantSku> beanList) {
        List<UpdateQuery> queries = new ArrayList<>();
        UpdateQuery updateQuery;
        int counter = 0;
        for (MerchantSku item : beanList) {

            //??????????????????
            if (counter != 0 && counter % 1000 == 0) {
                esRestTemplate.bulkUpdate(queries, IndexCoordinates.of(indexName));
                queries.clear();
                System.out.println("bulkIndex counter : " + counter);
            }

            JSONObject formatObj = DataUtils.formatKey(JSONUtil.parseObj(item), false);
            updateQuery = UpdateQuery.builder(item.getMerchantSkuId().toString())
                    .withDocument(DataUtils.jsonObjToDoc(formatObj))
                    .withDocAsUpsert(true) // ??????false,true?????????????????????????????????
                    .withRetryOnConflict(1) //????????????
                    .withRouting(item.getMerchantSkuId().toString())
                    .build();

            queries.add(updateQuery);
            counter++;
        }

        //??????????????????????????????????????????
        if (queries.size() > 0) {
            esRestTemplate.bulkUpdate(queries, IndexCoordinates.of(indexName));
            System.out.println("bulkIndex counter : " + counter);
        }
        esRestTemplate.refresh(IndexCoordinates.of(indexName));
    }

    @Test
    public void delete() {
        esRestTemplate.delete("6cba54cbc56a49cc8deee516e18b0121", IndexCoordinates.of("demo-log"));
    }



    /**
     * ????????????????????????????????????setQuery????????????????????????????????????????????????????????????????????????????????????
     *
     * @param //indexName ??????
     */
    @Test
    public void deleteByCondition() {

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("sku_name", "?????????"))
//                .filter(QueryBuilders.termsQuery("sku_name", "?????????"))
                .filter(QueryBuilders.termQuery("supplier_name", "??????"));

        searchQueryBuilder.withQuery(builder);
        Query searchQuery = searchQueryBuilder.build();

        System.out.println(builder.toString());
        esRestTemplate.delete(searchQuery, MerchantSku.class);
    }


    @Test
    public void queryCount() {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        long count = esRestTemplate.count(searchQueryBuilder.build(), IndexCoordinates.of("demo-log"));
        log.info("count -> {}", count);

    }

    /**
     * @param
     * @description: ?????????????????????3????????????????????? error ???????????????????????????????????????????????????20???
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 14:31
     * GET demo-log/_search
     * {
     * "query": {
     * "bool": {
     * "must": [
     * {
     * "term": {
     * "level": "error"
     * }
     * },
     * {
     * "range": {
     * "recordTime.keyword": {
     * "gte": "2021-02-05T10:00:00",
     * "format": "yyyy-MM-dd HH:mm:ss"
     * }
     * }
     * }
     * ]
     * }
     * },
     * "sort": [
     * {
     * "recordTime.keyword": {
     * "order": "desc"
     * }
     * }
     * ],
     * "size": 20
     * }
     */

    @Test
    public void termQuery() {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // ????????????????????? error ???
        boolQueryBuilder.must().add(new TermQueryBuilder("level", "info"));

        // ??????3??????????????????
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("recordTime");
        rangeQueryBuilder.gte(LocalDateTime.now().minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        boolQueryBuilder.must().add(rangeQueryBuilder);

        // ????????????20???
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("recordTime").descending());

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(boolQueryBuilder)
                .withPageable(pageRequest);

        Query searchQuery = searchQueryBuilder.build();

        SearchHits<LogEntity> hits = esRestTemplate.search(searchQuery, LogEntity.class);
        List<SearchHit<LogEntity>> hitList = hits.getSearchHits();
        log.info("hit size -> {}", hitList.size());
        hitList.forEach(hit -> {
            log.info("???????????????  {}", hit.getContent().toString());
        });
    }

    /**
     * @param
     * @description: ?????????????????????????????????????????????????????????
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 14:34
     * <p>
     * GET demo-log/_search
     * {
     * "aggs": {
     * "termLevel": {
     * "terms": {
     * "field": "level"
     * }
     * }
     * }
     * }
     */
    @Test
    public void testAggs() {
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("termLevel").field("level");

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.addAggregation(termsAggregationBuilder);

        Query searchQuery = searchQueryBuilder.build();
        SearchHits<LogEntity> hits = esRestTemplate.search(searchQuery, LogEntity.class);
        Terms aggTerms = hits.getAggregations().get("termLevel");
        for (Terms.Bucket bucket : aggTerms.getBuckets()) {
            log.info("level={}, count={}", bucket.getKey(), bucket.getDocCount());
        }

    }

}