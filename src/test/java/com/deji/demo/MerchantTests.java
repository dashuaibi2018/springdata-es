package com.deji.demo;

import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.dao.MerchantSkuRepository;
import com.deji.demo.service.MerchantSkuService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class MerchantTests {

    @Autowired
    private MerchantSkuRepository merchantDao;

    @Autowired
    MerchantSkuService skuService;

    @Test
    public void addMerchant() {

        List<MerchantSku> skus = skuService.findAll();
        MerchantSku save = merchantDao.save(skus.get(1));
        System.out.println(save);

        findAll();
    }

    @Test
    public void queryMerchant() {
        List<MerchantSku> skus = merchantDao.findBySkuName("勿删", PageRequest.of(0, 10));
        skus.forEach(System.out::println);
    }

    @Test
    public void findAll() {

        List<MerchantSku> skus = new ArrayList<>();
        merchantDao.findAll().forEach(
                a -> {
                    System.out.println(a);
                    skus.add(a);
                }
        );
    }


    /**
     * 删除所有
     */
    @Test
    public void deleteAll() {
        merchantDao.deleteAll();
//        merchantDao.deleteById("10633");
    }

    /**
     * 根据对象集合，批量删除
     *
     * @param //beanList 对象集合
     */
    @Test
    public void deleteByBeans() {

        List<MerchantSku> skus = new ArrayList<>();
        merchantDao.findAll().forEach(
                a -> {
                    System.out.println(a);
                    skus.add(a);
                }
        );
        merchantDao.deleteAll(skus);
    }

    /**
     * 根据对象删除数据，主键ID不能为空
     *
     * @param bean 对象
     */
    public void deleteByBean(MerchantSku bean) {
        merchantDao.delete(bean);
    }

    /**
     * 根据ID，删除数据
     *
     * @param id 数据ID
     */
    public void deleteById(String id) {
        merchantDao.deleteById(id);
    }


    @Test
    public void KQLqueryMerchant() {
        String endDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String startDateTime = LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<MerchantSku> merchantSkus = merchantDao.KQLQuery("info", startDateTime, endDateTime, PageRequest.of(0, 10));
        merchantSkus.forEach(System.out::println);

    }

    @Test
    public void queryAnd() {

        BoolQueryBuilder queryBuilders = QueryBuilders.boolQuery();
        queryBuilders.must(QueryBuilders.matchPhraseQuery("title", "springboot"));
        queryBuilders.must(QueryBuilders.matchPhraseQuery("content", "springboot"));

        System.out.println(queryBuilders.toString());
        Iterable<MerchantSku> search = merchantDao.search(queryBuilders);
        search.forEach(x -> {
            System.out.println(x);
        });

    }


}