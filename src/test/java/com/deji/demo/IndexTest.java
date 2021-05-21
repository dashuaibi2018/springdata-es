package com.deji.demo;

import com.deji.demo.entity.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class IndexTest {


    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;

    @Test
    public void testMapping() {
        // 创建索引，会根据Item类的@Document注解信息来创建
        esRestTemplate.createIndex(LogEntity.class);
        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        esRestTemplate.putMapping(LogEntity.class);
    }

    @Test
    public void deleteIndex() {
        esRestTemplate.deleteIndex("demo-log");
    }

    @Test
    public void insert() {

        // 创建索引，会根据Item类的@Document注解信息来创建
        esRestTemplate.createIndex(LogEntity.class);
        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        esRestTemplate.putMapping(LogEntity.class);

        String id = UUID.randomUUID().toString().replaceAll("-", "");

        HashMap obj = new HashMap();
        obj.put("name", "aoteman");
        LogEntity logEntity = new LogEntity(id, "info", "新增", "插入一条数据", LocalDateTime.now(), obj);

        esRestTemplate.save(logEntity);
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


}