package com.deji.demo;

import cn.hutool.core.bean.BeanUtil;
import com.deji.demo.entity.LogEntity;
import com.deji.demo.entity.MerchantSkuES;
import com.deji.demo.entity.MerchantSkuDB;
import com.deji.demo.service.MerchantSkuService;
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
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class EsRestTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;


    @Autowired
    MerchantSkuService skuService;

    /**
     * @param
     * @description: 索引相关
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
        // 创建索引，会根据Item类的@Document注解信息来创建
        ops.create();
        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
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
        IndexOperations ops = esRestTemplate.indexOps(MerchantSkuES.class);
//        if (!ops.exists()) {
//            testMapping(MerchantSkuES.class);
//        }

//        String id = UUID.randomUUID().toString().replaceAll("-", "");
//        HashMap obj = new HashMap();
//        obj.put("name", "aoteman");
//        LogEntity logEntity = new LogEntity(id, "info", "新增4", "插入一条数据4", LocalDateTime.now(), obj, 10);

        List<MerchantSkuDB> skus = skuService.findAll();
        List<MerchantSkuES> skuESList = skus.stream().map(a -> change(a)).collect(Collectors.toList());

        skuESList.stream().forEach(a -> {
            MerchantSkuES save = esRestTemplate.save(a);
            System.out.println(save);
        });

//        for (MerchantSkuES merchantSkuES : skuESList) {
//            final MerchantSkuES save = esRestTemplate.save(merchantSkuES);
//            System.out.println(save);
//            break;
//        }
        System.out.println(111);

//        final MerchantSkuES save = esRestTemplate.save(merchantSkuES);
//        System.out.println(save);
    }

    private MerchantSkuES change(MerchantSkuDB a) {
        MerchantSkuES skuES = new MerchantSkuES();
        BeanUtil.copyProperties(a, skuES);
        return skuES;
    }

    @Test
    public void update() {
        Document doc = Document.create();
        doc.put("level", "warn1");
        doc.put("recordTime", LocalDateTime.now());
        UpdateQuery.Builder builder = UpdateQuery.builder("c2f4efb04f7b4e3d9f57dc63fa2de713")
                .withDocument(doc);
        UpdateQuery updateQuery = builder.build();
        esRestTemplate.update(updateQuery, IndexCoordinates.of("demo-log"));

    }

    @Test
    public void delete() {
        esRestTemplate.delete("6cba54cbc56a49cc8deee516e18b0121", IndexCoordinates.of("demo-log"));
    }

    @Test
    public void queryCount() {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        long count = esRestTemplate.count(searchQueryBuilder.build(), IndexCoordinates.of("demo-log"));
        log.info("count -> {}", count);

    }

    /**
     * @param
     * @description: 需求描述：查找3天以内，级别为 error 的日志，按记录时间倒序，分页，取前20条
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

        // 查询日志级别为 error 的
        boolQueryBuilder.must().add(new TermQueryBuilder("level", "info"));

        // 查询3天内数的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("recordTime");
        rangeQueryBuilder.gte(LocalDateTime.now().minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        boolQueryBuilder.must().add(rangeQueryBuilder);

        // 分页查询20条
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("recordTime").descending());

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(boolQueryBuilder)
                .withPageable(pageRequest);

        Query searchQuery = searchQueryBuilder.build();

        SearchHits<LogEntity> hits = esRestTemplate.search(searchQuery, LogEntity.class);
        List<SearchHit<LogEntity>> hitList = hits.getSearchHits();
        log.info("hit size -> {}", hitList.size());
        hitList.forEach(hit -> {
            log.info("返回数据：  {}", hit.getContent().toString());
        });
    }

    /**
     * @param
     * @description: 按日志级别分组，打印出每个级别的日志数
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