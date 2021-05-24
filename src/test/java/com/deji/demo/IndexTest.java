package com.deji.demo;

import cn.hutool.json.JSONUtil;
import com.deji.demo.entity.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
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
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class IndexTest {

    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;

    /**
     * @param
     * @description: 索引相关
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 13:54
     */
    @Test
    public void testMapping() {
        IndexOperations ops = esRestTemplate.indexOps(LogEntity.class);
//        final boolean exists = esRestTemplate.exists("c2f4efb04f7b4e3d9f57dc63fa2de713", IndexCoordinates.of("demo-log"));
        if (ops.exists()) {
            ops.delete();
        }
        // 创建索引，会根据Item类的@Document注解信息来创建
        ops.create();
        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        ops.putMapping(LogEntity.class);
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
     * @description: Template相关
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 13:53
     */
    @Test
    public void createTemplate() {
        IndexOperations ops = esRestTemplate.indexOps(LogEntity.class);

        Map<String, Object> mappings = null;
        Map<String, Object> settings = null;
        if (ops.exists()) {
            mappings = ops.getMapping();
            settings = ops.getSettings();

            // {"properties":{"level":{"type":"keyword"},"title":{"type":"text"},"content":{"type":"text"},"recordTime":{"format":"date_time","type":"date"},"objContent":{"type":"object"}}}
            System.out.println(JSONUtil.toJsonStr(mappings));
            //{"index.creation_date":"1621827252763","index.uuid":"FEVk7f-bSICPgbgaWhhDzw","index.version.created":"7090299","index.provided_name":"demo-log","index.number_of_replicas":"1","index.store.type":"fs","index.refresh_interval":"1s","index.number_of_shards":"1"}
            System.out.println(JSONUtil.toJsonStr(settings));
        }

        Document mapping = Document.parse("{\"properties\":{\"level\":{\"type\":\"keyword\"},\"title\":{\"type\":\"keyword\"},\"content\":{\"type\":\"text\"},\"recordTime\":{\"format\":\"date_time\",\"type\":\"date\"},\"objContent\":{\"type\":\"object\"}}}");
        Document setting = Document.parse("{\"index.refresh_interval\":\"2s\",\"index.number_of_shards\":\"1\"},\"index.number_of_replicas\":\"3\"}");
        PutTemplateRequest template = PutTemplateRequest.builder("template", "demo-log*")
                .withMappings(Document.from(mapping)).withSettings(setting)
                .withVersion(1).build();

        ops.putTemplate(template);
    }

    @Test
    public void deleteTemplate() {
        IndexOperations ops = esRestTemplate.indexOps(LogEntity.class);

        final boolean b = ops.deleteTemplate("template");
        System.out.println(b);
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
        IndexOperations ops = esRestTemplate.indexOps(LogEntity.class);
//        if (!ops.exists()) {
//            testMapping();
//        }

        String id = UUID.randomUUID().toString().replaceAll("-", "");
        HashMap obj = new HashMap();
        obj.put("name", "aoteman");
        LogEntity logEntity = new LogEntity(id, "info", "新增3", "插入一条数据3", LocalDateTime.now(), obj);

        final LogEntity save = esRestTemplate.save(logEntity);
        System.out.println(save);
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

    @Test
    public void termQuery() {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 查询日志级别为 error 的
        boolQueryBuilder.must().add(new TermQueryBuilder("level", "info"));

        // 查询3天内数的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("recordTime");
        rangeQueryBuilder.gte(LocalDateTime.now().minusDays(3));
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

}