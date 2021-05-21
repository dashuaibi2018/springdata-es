package com.deji.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@Document(indexName = "account")
public class Message implements Serializable {

    private static final long serialVersionUID = 5710293639676035958L;
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String username;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String email;

    @Field(type = FieldType.Long)
    private String age;

    @Field(type = FieldType.Long) // 意思自定义属性格式 时间格式，我们在java程序中可以传入这些格式的时间
    private Long createTime;
}

