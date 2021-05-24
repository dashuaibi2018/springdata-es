package com.deji.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@EnableElasticsearchRepositories
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class SpringdataEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringdataEsApplication.class, args);
    }

}
