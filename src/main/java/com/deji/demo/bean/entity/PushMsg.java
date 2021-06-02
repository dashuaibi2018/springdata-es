package com.deji.demo.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;

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
    private String recUid;

    /**
     * 发送用户ID
     */
    @TableField("user_Id")
    private String userId;

    /**
     * 终端ID
     */
    @TableField("push_id")
    private String pushId;

    /**
     * push时间
     */
    @TableField("push_time")
    private LocalDateTime pushTime;

    /**
     * push????
     */
    @TableField("msg")
    private String msg;

    /**
     * 任务类型，0查勘，1定损
     */
    @TableField("task_type")
    private Integer taskType;

    /**
     * 消息相关的车辆
     */
    @TableField("obj_Id")
    private String objId;

    /**
     * 消息类型
     */
    @TableField("msg_type")
    private Integer msgType;

    /**
     * 车牌号
     */
    @TableField("license_plate_no")
    private String licensePlateNo;

    /**
     * 车辆别名
     */
    @TableField("id_name")
    private String idName;

    /**
     * 消息类型，0网络，1短信
     */
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
    @TableField("case_sn")
    private String caseSn;

    /**
     * 发送消息的公司ID
     */
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
    @TableField("app_name")
    private String appName;

    /**
     * 清空标识：0未清空，1清空
     */
    @TableField("clean_flag")
    private Integer cleanFlag;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
