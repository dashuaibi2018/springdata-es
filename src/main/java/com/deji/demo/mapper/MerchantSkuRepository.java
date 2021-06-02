package com.deji.demo.mapper;

import com.deji.demo.bean.entity.MerchantSku;
import org.springframework.data.domain.PageRequest;
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
@Repository
public interface MerchantSkuRepository extends ElasticsearchRepository<MerchantSku, String> {


//    List<MerchantSku> findByAgeBetween(int mix, int max);

    List<MerchantSku> findBySkuName(String skuName, Pageable pageable);

    @Query("{\n" +

            "    \"match\": {\n" +
            "      \"sku_name\": \"美少女\"\n" +
            "    }\n" +
            "  }")
    List<MerchantSku> KQLQuery(String info, String startDateTime, String endDateTime, PageRequest of);

//    List<MerchantSku> findByTitle(String title, Pageable pageable);
//
//    Long countByAgeBetween(int i, int j);


//    @Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
//    List<LogEntity> findByName(String name, Pageable pageable);


}