package com.deji.demo.mapper;

import com.deji.demo.bean.entity.PushMsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @param
 * @description: 通过 Spring JPA 访问 ES
 * @return:
 * @author: sujun
 * @time: 2021/5/24 14:38
 */
@Repository
public interface PushMsgRepository extends ElasticsearchRepository<PushMsg, String> {


    @Query("{\n" +
            "  \"bool\": {\n" +
            "    \"must\": [\n" +
            "      {\n" +
            "        \"term\": {\n" +
            "          \"user_id\": {\n" +
            "            \"value\": \"bc775af283e445e3a44b25b9bb9d9817\",\n" +
            "            \"boost\": 1\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"terms\": {\n" +
            "          \"msg_type\": [\n" +
            "            \"207\"\n" +
            "          ],\n" +
            "          \"boost\": 1\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"term\": {\n" +
            "          \"push_mode\": {\n" +
            "            \"value\": \"0\",\n" +
            "            \"boost\": 1\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    ],\n" +
            "    \"must_not\": [\n" +
            "      {\n" +
            "        \"term\": {\n" +
            "          \"clean_flag\": {\n" +
            "            \"value\": \"1\",\n" +
            "            \"boost\": 1\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    ],\n" +
            "    \"adjust_pure_negative\": true,\n" +
            "    \"boost\": 1\n" +
            "  }\n" +
            "} ")
    Page<PushMsg> pushMsgQuery(Pageable pageable);
}