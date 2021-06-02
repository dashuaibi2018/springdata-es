package com.deji.demo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.deji.demo.bean.entity.LogEntity;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.service.MerchantSkuService;
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
    MerchantSkuService skuService;


    @Test
    public void testBatch() {
        String indexName = "merchant_sku";
        List<MerchantSku> beanList = skuService.findAll();
//        batchSave(indexName, beanList);
        batchUpdate(indexName, beanList);

    }

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
        //创建mapping
//        testMapping(MerchantSkuDB.class);

        List<MerchantSku> skus = skuService.findAll();
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
     * 修改数据
     *
     * @param //indexName 索引名称
     * @param //bean      修改数据对象，ID不能为空
     */
    @Test
    public void update() {

        String indexName = "demo-log";
        Document deafaultDoc = Document.create();
        deafaultDoc.put("rec_id", "默认值");
        deafaultDoc.put("level", "默认值");

        Document doc = Document.create();
        doc.setId("210");
        doc.put("rec_id", "110");
        doc.put("level", "warn1");
        doc.put("time", "20210601");

        UpdateQuery.Builder builder = UpdateQuery.builder(doc.getId())
                .withDocument(doc)
//                .withDocAsUpsert(false) //当设置为false时，更新请求检查发现文档不存在时(对应的ID文档)，会抛出ElasticsearchException
//                .withDocAsUpsert(true) //默认false,true表示更新时不存在就插入
                .withUpsert(deafaultDoc) //当设置 docAsUpsert(false)时，设置了upsert数据源时，当文档不存在时，会新增request.upsert内容设置的文档
                .withRetryOnConflict(1) //冲突重试
                .withRouting(StrUtil.toString(doc.get("rec_id"))); //默认是_id来路由的，用来路由到不同的shard，会对这个值做hash，然后映射到shard。所以分片
        UpdateQuery updateQuery = builder.build();

        UpdateResponse updateResponse = esRestTemplate.update(updateQuery, IndexCoordinates.of(indexName));
        System.out.println(updateResponse);

    }

    /**
     * 批量新增（toJsonStr会导致localDateTime转为Long, 用批量更新替代）
     *
     * @param indexName 索引名称
     * @param beanList  新增对象集合
     */
    public void batchSave(String indexName, List<MerchantSku> beanList) {
        List<IndexQuery> queries = new ArrayList<>();
        IndexQuery indexQuery;
        int counter = 0;
        for (MerchantSku item : beanList) {
            //分批提交索引
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
//            indexQuery.setSource(document.toJson());  //有瑕疵
            indexQuery.setSource(JSONUtil.toJsonStr(formatObj)); //localDateTime会自动转为Long
            queries.add(indexQuery);

            counter++;
        }
        //不足批的索引最后不要忘记提交
        if (queries.size() > 0) {
            esRestTemplate.bulkIndex(queries, IndexCoordinates.of(indexName));
        }
//        esRestTemplate.refresh(MerchantSku.class);
        esRestTemplate.indexOps(IndexCoordinates.of(indexName)).refresh();
    }

    /**
     * 批量新增或修改
     *
     * @param indexName 索引名称
     * @param beanList  修改对象集合
     */
    public void batchUpdate(String indexName, List<MerchantSku> beanList) {
        List<UpdateQuery> queries = new ArrayList<>();
        UpdateQuery updateQuery;
        int counter = 0;
        for (MerchantSku item : beanList) {

            //分批提交索引
            if (counter != 0 && counter % 1000 == 0) {
                esRestTemplate.bulkUpdate(queries, IndexCoordinates.of(indexName));
                queries.clear();
                System.out.println("bulkIndex counter : " + counter);
            }

            JSONObject formatObj = DataUtils.formatKey(JSONUtil.parseObj(item), false);
            updateQuery = UpdateQuery.builder(item.getMerchantSkuId().toString())
                    .withDocument(DataUtils.jsonObjToDoc(formatObj))
                    .withDocAsUpsert(true) // 默认false,true表示更新时不存在就插入
                    .withRetryOnConflict(1) //冲突重试
                    .withRouting(item.getMerchantSkuId().toString())
                    .build();

            queries.add(updateQuery);
            counter++;
        }

        //不足批的索引最后不要忘记提交
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
     * 根据条件，自定义删除（在setQuery中的条件，可以根据需求自由拼接各种参数，与查询方法一样）
     *
     * @param //indexName 索引
     */
    @Test
    public void deleteByCondition() {

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("sku_name", "海贼王"))
//                .filter(QueryBuilders.termsQuery("sku_name", "海贼王"))
                .filter(QueryBuilders.termQuery("supplier_name", "公司"));

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