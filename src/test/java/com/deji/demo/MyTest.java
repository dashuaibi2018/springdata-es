package com.deji.demo;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MyTest {

    @Test
    public void update() {
        System.out.println(SecureUtil.md5("123456"));
    }


}