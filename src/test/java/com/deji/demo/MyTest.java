package com.deji.demo;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.deji.demo.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@SpringBootTest
public class MyTest {

    @Test
    public void update() {
        System.out.println(SecureUtil.md5("123456"));
    }


    @Test
    public void testDate() {

//        LocalDateTime longToLocalDateTime =
//                LocalDateTime.ofInstant(Instant.ofEpochMilli(16L), ZoneId.systemDefault());
//        System.out.println("Long -> LocalDateTime:  " + longToLocalDateTime);

        String it = "update_time";
        Long objR = 1621834340556L;
        LocalDateTime dateTime = null;
        if (StrUtil.contains(it, "time")) {
            dateTime = LocalDateTimeUtil.of((Long) objR, ZoneId.systemDefault());
        }

    }


    @Test
    public void testDate1() {

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        String s1 = LocalDateTime.now().minusHours(10).format(formatter);
//        LocalDateTime localDateTime = LocalDateTime.parse(s1,formatter);
//        System.out.println(localDateTime.toInstant(ZoneOffset.UTC));

        System.out.println(DataUtils.camelOrUnderline("ncSyncStatus"));
        System.out.println(DataUtils.camelOrUnderline("ali_pig"));

//        List<People> people = CollectionUtil.newArrayList(new People("1", "2", "3", "4")
//                , new People("2", "2", "3", "4"), new People("3", "2", "3", "4"));
//
//        System.out.println(people);
//        List<People> list = people.stream().map(a -> (a.setRecUid("666"))).collect(Collectors.toList());
//
//        System.out.println(list);
//        for (People person : people) {
//            person.setPushId("change");
//        }


    }


}