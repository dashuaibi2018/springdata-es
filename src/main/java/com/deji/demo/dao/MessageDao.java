package com.deji.demo.dao;

import com.deji.demo.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageDao extends ElasticsearchRepository<Message, String> {
    Page<Message> findAll(Pageable pageable);

    Iterable<Message> findAll(Sort sort);

    List<Message> findByUsername(String username);

    List<Message> findByAgeBetween(Long mix, Long max);

    Long countByAgeBetween(Long mix, Long max);
}