package com.deji.demo.bean.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品sku信息表
 * </p>
 *
 * @author mybatis-plus codegen
 * @since 2021-05-25
 */
@Document(indexName = "merchant_sku", shards = 2, replicas = 2, useServerConfiguration = false)
@TypeAlias("MerchantSku")
@Data
@AllArgsConstructor
@NoArgsConstructor

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantSkuES1 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SKU编码
     */
    @Id
    private Long merchant_sku_id;

    /**
     * 商品名
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart") //ik_max_word
    private String sku_name;

    /**
     * 商品条码
     */
    @Field(type = FieldType.Keyword)
    private String sku_sn;

    /**
     * 管理编码(助记码)
     */
    @Field(type = FieldType.Keyword)
    private String sku_mng_code;

    /**
     * 外部系统商品编码
     */
    @Field(type = FieldType.Keyword)
    private String outer_sn;

    /**
     * 产品分类
     */
    @Field(type = FieldType.Keyword)
    private Long category_id;

    @Field(type = FieldType.Keyword)
    private Integer merchant_id;

    @Field(type = FieldType.Float)
    private BigDecimal market_price;

    /**
     * 品牌
     */
    @Field(type = FieldType.Keyword)
    private Long brand_id;

    /**
     * 规格
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String spec_txt;

    /**
     * 库存单位ID
     */
    @Field(type = FieldType.Integer)
    private Integer unit_id;

    /**
     * 库存单位名称
     */
    @Field(type = FieldType.Keyword)
    private String unit_name;

    /**
     * 供应商ID
     */
    @Field(type = FieldType.Keyword)
    private Long supplier_id;

    /**
     * 供应商名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String supplier_name;

    /**
     * 进项税率编码
     */
    @Field(type = FieldType.Keyword)
    private String input_tax_code;

    /**
     * 进项税率
     */
    @Field(type = FieldType.Float)
    private BigDecimal input_tax_rate;

    /**
     * NC财务系统关联商品编号
     */
    @Field(type = FieldType.Keyword)
    private String nc_sku_code;

    /**
     * NC财务系统关联商品doc的id（NC商品修改必传字段)
     */
    @Field(type = FieldType.Keyword)
    private String nc_sku_doc_id;

    /**
     * 同邦系统关联商品编号
     */
    @Field(type = FieldType.Keyword)
    private String topon_sku_code;

    /**
     * 测试商品标识(0:非测试商品；1:测试商品)
     */
    @Field(type = FieldType.Keyword)
    private Boolean test_flag;

    /**
     * 有效标识(0:无效;1:有效)
     */
    @Field(type = FieldType.Keyword)
    private Boolean active_status;

    //    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") , format = DateFormat.basic_date_time
    @Field(type = FieldType.Date)
    private LocalDateTime create_time;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String create_user;

    //    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(type = FieldType.Date)
    private LocalDateTime update_time;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String update_user;

    /**
     * 销项税率编码
     */
    @Field(type = FieldType.Keyword)
    private String output_tax_item_code;

    /**
     * 销项税率
     */
    @Field(type = FieldType.Float)
    private BigDecimal output_tax_rate;

    /**
     * 审核状态(1:待审核；2:审核通过；3:审核未通过)
     */
    @Field(type = FieldType.Keyword)
    private Integer approve_status;

    /**
     * 审核人
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String approve_user;

    /**
     * NC系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    @Field(type = FieldType.Keyword)
    private Integer nc_sync_status;

    /**
     * 同邦系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    @Field(type = FieldType.Keyword)
    private Integer topon_sync_status;

    /**
     * 版本(乐观锁)
     */
    @Field(type = FieldType.Keyword)
    private Integer version;


}
