package com.deji.demo.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.DateFormat;
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
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(schema = "product", value = "merchant_sku")

@Document(indexName = "merchant_sku", shards = 1, replicas = 1, useServerConfiguration = false)
@TypeAlias("MerchantSku")
@AllArgsConstructor
@NoArgsConstructor
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantSku implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SKU编码
     */
    @Id()
    @Field(name = "merchant_sku_id", type = FieldType.Keyword)
    @TableId(value = "merchant_sku_id", type = IdType.ASSIGN_UUID)
    private Long merchantSkuId;

    /**
     * 商品名
     */
    @Field(name = "sku_name", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    //ik_max_word
    @TableField("sku_name")
    private String skuName;

    @Field(name = "create_time", type = FieldType.Date, format = DateFormat.date_optional_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Field(name = "create_user", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @TableField("create_user")
    private String createUser;

    //    @Field(name = "update_time",type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(name = "update_time", type = FieldType.Date, format = DateFormat.date_optional_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
//yyyy-MM-dd'T'HH:mm:ss.SSSZ
//    @Field(name = "update_time", type = FieldType.Date)
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 商品条码
     */
    @Field(name = "sku_sn", type = FieldType.Keyword)
    @TableField("sku_sn")
    private String skuSn;

    /**
     * 管理编码(助记码)
     */
    @Field(name = "sku_mng_code", type = FieldType.Keyword)
    @TableField("sku_mng_code")
    private String skuMngCode;

    /**
     * 外部系统商品编码
     */
    @Field(name = "outer_sn", type = FieldType.Keyword)
    @TableField("outer_sn")
    private String outerSn;

    /**
     * 产品分类
     */
    @Field(name = "category_id", type = FieldType.Keyword)
    @TableField("category_id")
    private Long categoryId;

    @Field(name = "merchant_id", type = FieldType.Keyword)
    @TableField("merchant_id")
    private Integer merchantId;

    @Field(name = "market_price", type = FieldType.Float)
    @TableField("market_price")
    private BigDecimal marketPrice;

    /**
     * 品牌
     */
    @Field(name = "brand_id", type = FieldType.Keyword)
    @TableField("brand_id")
    private Long brandId;

    /**
     * 规格
     */
    @Field(name = "spec_txt", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @TableField("spec_txt")
    private String specTxt;

    /**
     * 库存单位ID
     */
    @Field(name = "unit_id", type = FieldType.Integer)
    @TableField("unit_id")
    private Integer unitId;

    /**
     * 库存单位名称
     */
    @Field(name = "unit_name", type = FieldType.Keyword)
    @TableField("unit_name")
    private String unitName;

    /**
     * 供应商ID
     */
    @Field(name = "supplier_id", type = FieldType.Keyword)
    @TableField("supplier_id")
    private Long supplierId;

    /**
     * 供应商名称
     */
    @Field(name = "supplier_name", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @TableField("supplier_name")
    private String supplierName;

    /**
     * 进项税率编码
     */
    @Field(name = "input_tax_code", type = FieldType.Keyword)
    @TableField("input_tax_code")
    private String inputTaxCode;

    /**
     * 进项税率
     */
    @Field(name = "input_tax_rate", type = FieldType.Float)
    @TableField("input_tax_rate")
    private BigDecimal inputTaxRate;

    /**
     * NC财务系统关联商品编号
     */
    @Field(name = "nc_sku_code", type = FieldType.Keyword)
    @TableField("nc_sku_code")
    private String ncSkuCode;

    /**
     * NC财务系统关联商品doc的id（NC商品修改必传字段)
     */
    @Field(name = "nc_sku_doc_id", type = FieldType.Keyword)
    @TableField("nc_sku_doc_id")
    private String ncSkuDocId;

    /**
     * 同邦系统关联商品编号
     */
    @Field(name = "topon_sku_code", type = FieldType.Keyword)
    @TableField("topon_sku_code")
    private String toponSkuCode;

    /**
     * 测试商品标识(0:非测试商品；1:测试商品)
     */
    @Field(name = "test_flag", type = FieldType.Keyword)
    @TableField("test_flag")
    private Integer testFlag;

    /**
     * 有效标识(0:无效;1:有效)
     */
    @Field(name = "active_status", type = FieldType.Keyword)
    @TableField("active_status")
    private Integer activeStatus;


    @Field(name = "update_user", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @TableField("update_user")
    private String updateUser;

    /**
     * 销项税率编码
     */
    @Field(name = "output_tax_item_code", type = FieldType.Keyword)
    @TableField("output_tax_item_code")
    private String outputTaxItemCode;

    /**
     * 销项税率
     */
    @Field(name = "output_tax_rate", type = FieldType.Float)
    @TableField("output_tax_rate")
    private BigDecimal outputTaxRate;

    /**
     * 审核状态(1:待审核；2:审核通过；3:审核未通过)
     */
    @Field(name = "approve_status", type = FieldType.Keyword)
    @TableField("approve_status")
    private Integer approveStatus;

    /**
     * 审核人
     */
    @Field(name = "approve_user", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @TableField("approve_user")
    private String approveUser;

    /**
     * NC系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    @Field(name = "nc_sync_status", type = FieldType.Keyword)
    @TableField("nc_sync_status")
    private Integer ncSyncStatus;

    /**
     * 同邦系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    @Field(name = "topon_sync_status", type = FieldType.Keyword)
    @TableField("topon_sync_status")
    private Integer toponSyncStatus;

    /**
     * 版本(乐观锁)
     */
    @Field(type = FieldType.Keyword)
    @TableField("version")
    private Integer version;

    /**
     * 毛重
     */
    @Field(name = "gross_weight", type = FieldType.Integer)
    @TableField("gross_weight")
    private Integer grossWeight;

    /**
     * 保质期天数
     */
    @Field(name = "shelf_life", type = FieldType.Integer)
    @TableField("shelf_life")
    private Integer shelfLife;

    /**
     * 保质期禁收天数
     */
    @Field(name = "reject_lifecycle", type = FieldType.Integer)
    @TableField("reject_lifecycle")
    private Integer rejectLifecycle;

    /**
     * 保质期禁售天数
     */
    @Field(name = "lockup_lifecycle", type = FieldType.Integer)
    @TableField("lockup_lifecycle")
    private Integer lockupLifecycle;

    /**
     * 保质期临期预警天数
     */
    @Field(name = "advent_lifecycle", type = FieldType.Integer)
    @TableField("advent_lifecycle")
    private Integer adventLifecycle;

}
