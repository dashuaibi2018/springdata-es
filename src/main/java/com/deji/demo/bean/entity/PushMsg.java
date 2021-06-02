package com.deji.demo.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author mybatis-plus codegen
 * @since 2021-06-02
 */

@Document(indexName = "push_msg", shards = 1, replicas = 1, useServerConfiguration = false)
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(schema = "iov", value = "push_msg")
public class PushMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,消息唯一标识
     */
    @TableId(value = "rec_uId", type = IdType.ASSIGN_UUID)
    @Id
    @Field(name = "rec_uId")
    private String recUid;

    /**
     * 发送用户ID
     */
    @Field(name = "user_id")
    @TableField("user_Id")
    private String userId;

    /**
     * 终端ID
     */
    @Field(name = "push_id")
    @TableField("push_id")
    private String pushId;

    /**
     * push时间
     */
    @Field(name = "push_time")
    @TableField("push_time")
    private LocalDateTime pushTime;

    /**
     * push
     */
    @TableField("msg")
    private String msg;

    /**
     * 任务类型，0查勘，1定损
     */
    @Field(name = "task_type")
    @TableField("task_type")
    private Integer taskType;

    /**
     * 消息相关的车辆
     */
    @Field(name = "obj_Id")
    @TableField("obj_Id")
    private String objId;

    /**
     * 消息类型
     */
    @Field(name = "msg_type")
    @TableField("msg_type")
    private Integer msgType;

    /**
     * 车牌号
     */
    @Field(name = "license_plate_no")
    @TableField("license_plate_no")
    private String licensePlateNo;

    /**
     * 车辆别名
     */
    @Field(name = "id_name")
    @TableField("id_name")
    private String idName;

    /**
     * 消息类型，0网络，1短信
     */
    @Field(name = "push_mode")
    @TableField("push_mode")
    private String pushMode;

    /**
     * 接收短信的号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 案件号
     */
    @Field(name = "case_sn")
    @TableField("case_sn")
    private String caseSn;

    /**
     * 发送消息的公司ID
     */
    @Field(name = "corp_id")
    @TableField("corp_id")
    private String corpId;

    /**
     * 短信是否同步发1同步0异步
     */
    @TableField("online")
    private Integer online;

    /**
     * 0未处理，1已发PUSH，2已读
     */
    @TableField("received")
    private Integer received;

    /**
     * 产品名称：xfinder4personal车行者,xfinder4company 车掌柜
     */
    @Field(name = "app_name")
    @TableField("app_name")
    private String appName;

    /**
     * 清空标识：0未清空，1清空
     */
    @Field(name = "clean_flag")
    @TableField("clean_flag")
    private Integer cleanFlag;

    /**
     * 修改时间
     */
    @Field(name = "update_time")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
