package com.deji.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author Engr-Z
 * @since 2021/2/5
 */
@Document(indexName = "demo-log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEntity {

    @Id
    private String id;

    /**
     * 日志等级
     */
    @Field(type = FieldType.Keyword)
    private String level;

    /**
     * 日志标题
     */
    @Field(type = FieldType.Text)
    private String title;

    /**
     * 日志内容
     */
    @Field(type = FieldType.Text)
    private String content;

    /**
     * 记录时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime recordTime;


    @Field(type = FieldType.Object)
    private HashMap objContent;

}