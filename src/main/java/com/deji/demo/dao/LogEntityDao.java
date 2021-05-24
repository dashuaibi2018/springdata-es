package com.deji.demo.dao;

import com.deji.demo.entity.LogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @param
 * @description: 通过 Spring JPA 访问 ES
 * @return:
 * @author: sujun
 * @time: 2021/5/24 14:38
 */
@Mapper
@Repository
public interface LogEntityDao extends ElasticsearchRepository<LogEntity, String> {


    List<LogEntity> findByAgeBetween(int mix, int max);

    List<LogEntity> findByLevel(String level, Pageable pageable);

    List<LogEntity> findByTitle(String title, Pageable pageable);

    Long countByAgeBetween(int i, int j);

//    @Query("{\n" +
//            "    \"bool\": {\n" +
//            "      \"must\": [\n" +
//            "        {\n" +
//            "          \"term\": {\n" +
//            "            \"level\": ?0\n" +
//            "          }\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"range\": {\n" +
//            "            \"recordTime.keyword\": {\n" +
//            "              \"gte\": ?1,\n" +
//            "              \"lte\": ?2\n" +
//            "            }\n" +
//            "          }\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    }\n" +
//            "  }")
    @Query("{\n" +
            "    \"bool\": {\n" +
            "      \"must\": [\n" +
            "        {\n" +
            "          \"term\": {\n" +
            "            \"level\":  \"?0\"\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"filter\": [\n" +
            "        {\n" +
            "          \"range\": {\n" +
            "            \"recordTime\": {\n" +
            "              \"gte\": \"?1\" , \n" +
            "              \"lte\": \"?2\" \n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }")

    List<LogEntity> exampleQuery(String level, String startDateTime, String endDateTime, Pageable pageable);


}