package com.deji.demo;

import com.deji.demo.mapper.LogEntityRepository;
import com.deji.demo.bean.entity.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class LogEntityDaoTests {

    @Autowired
    private LogEntityRepository logEntityDao;

    @Test
    public void add() {

        HashMap obj = new HashMap<String, Object>();
        obj.put("name", "aoteman");

        LogEntity logEntity;
        for (int i = 1; i < 6; i++) {
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            logEntity = new LogEntity(id, "info", "1JPA新增" + i, "JPA插入一条数据" + i, LocalDateTime.now(), obj, 19);
            LogEntity save = logEntityDao.save(logEntity);
            System.out.println(save);
        }
    }

    @Test
    public void findAll() {
        Iterable<LogEntity> all = logEntityDao.findAll();
        long count = logEntityDao.count();
        log.info("count -> {}", count);
        all.forEach(System.out::println);
    }


    @Test
    public void findAllPageRequest() {
        Iterable<LogEntity> all = logEntityDao.findAll(PageRequest.of(0, 1));
        all.forEach(System.out::println);
    }

    @Test
    public void findAllSort() {
        Iterable<LogEntity> all = logEntityDao.findAll(Sort.by("recordTime").descending());
        all.forEach(System.out::println);
    }

    @Test
    public void findById() {
        Optional<LogEntity> entity = logEntityDao.findById("2a1680fe084c4b98b5ebb8291f0bd518");
        log.info("size -> {}", entity.orElse(null));
    }

    @Test
    public void deleteById() {
        logEntityDao.deleteById("8b36eb08174a4f088be72986053dce98");
    }

    @Test
    public void findByLevel() {
        List<LogEntity> level = logEntityDao.findByLevel("info", PageRequest.of(0, 10));
        level.stream().forEach(System.out::println);

        System.out.println("--------------------------------");
        List<LogEntity> title = logEntityDao.findByTitle("新增", PageRequest.of(0, 10));
        title.stream().forEach(System.out::println);

    }

    @Test
    public void findByAgeBetween() {
        List<LogEntity> LogEntitys = logEntityDao.findByAgeBetween(10, 15);
        LogEntitys.forEach(System.out::println);

        Long count = logEntityDao.countByAgeBetween(10, 20);
        System.out.println(count);
    }


    /**
     * @param
     * @description: KQL查询
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 15:40
     */
    @Test
    public void KQLquery() {
        String endDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String startDateTime = LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<LogEntity> LogEntitys = logEntityDao.exampleQuery("info", startDateTime, endDateTime, PageRequest.of(0, 10));
        LogEntitys.forEach(System.out::println);

    }


}

