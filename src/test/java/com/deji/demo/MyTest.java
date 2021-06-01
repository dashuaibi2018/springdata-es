package com.deji.demo;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
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

}