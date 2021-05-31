package com.deji.demo;

import com.deji.demo.dao.MerchantSkuRepository;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.service.MerchantSkuService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        merchantDao.findAll().forEach(
                a->{
//                    LocalDateTime createTime = a.getCreateTime();
//                    Long a1 = Timestamp.valueOf(a.getCreateTime()).getTime();
//                    a.setTimestamp(a1);
                    System.out.println(a);
                }
        );
//        skus.forEach(System.out::println);
    }


    @Test
    public void deleteAll(){
        merchantDao.deleteAll();
//        merchantDao.deleteById("10633");
    }

    @Test
    public void KQLqueryMerchant() {
        String endDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String startDateTime = LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<MerchantSku> merchantSkus = merchantDao.KQLQuery("info", startDateTime, endDateTime, PageRequest.of(0, 10));
        merchantSkus.forEach(System.out::println);

    }



}