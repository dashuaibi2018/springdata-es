package com.deji.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableElasticsearchRepositories
@SpringBootApplication()  //exclude= {DataSourceAutoConfiguration.class}
public class SpringdataEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringdataEsApplication.class, args);
    }

}
