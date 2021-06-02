package com.deji.demo.mapper;

import com.deji.demo.bean.entity.PushMsg;
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

}