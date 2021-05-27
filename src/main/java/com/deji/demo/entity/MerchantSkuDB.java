package com.deji.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("merchant_sku")
public class MerchantSkuDB implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SKU编码
     */
    @TableId(value = "merchant_sku_id", type = IdType.ASSIGN_UUID)
    private Long merchant_sku_id;

    /**
     * 商品名
     */
    @TableField("sku_name")
    private String sku_name;

    /**
     * 商品条码
     */
    @TableField("sku_sn")
    private String sku_sn;

    /**
     * 管理编码(助记码)
     */
    @TableField("sku_mng_code")
    private String sku_mng_code;

    /**
     * 外部系统商品编码
     */
    @TableField("outer_sn")
    private String outer_sn;

    /**
     * 产品分类
     */
    @TableField("category_id")
    private Long category_id;

    @TableField("merchant_id")
    private Integer merchant_id;

    @TableField("market_price")
    private BigDecimal market_price;

    /**
     * 品牌
     */
    @TableField("brand_id")
    private Long brand_id;

    /**
     * 规格
     */
    @TableField("spec_txt")
    private String spec_txt;

    /**
     * 库存单位ID
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 库存单位名称
     */
    @TableField("unit_name")
    private String unit_name;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Long supplier_id;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplier_name;

    /**
     * 进项税率编码
     */
    @TableField("input_tax_code")
    private String input_tax_code;

    /**
     * 进项税率
     */
    @TableField("input_tax_rate")
    private BigDecimal input_tax_rate;

    /**
     * NC财务系统关联商品编号
     */
    @TableField("nc_sku_code")
    private String nc_sku_code;

    /**
     * NC财务系统关联商品doc的id（NC商品修改必传字段)
     */
    @TableField("nc_sku_doc_id")
    private String nc_sku_doc_id;

    /**
     * 同邦系统关联商品编号
     */
    @TableField("topon_sku_code")
    private String topon_sku_code;

    /**
     * 测试商品标识(0:非测试商品；1:测试商品)
     */
    @TableField("test_flag")
    private Boolean test_flag;

    /**
     * 有效标识(0:无效;1:有效)
     */
    @TableField("active_status")
    private Boolean active_status;

    @TableField("create_time")
    private LocalDateTime create_time;

    @TableField("create_user")
    private String create_user;

    @TableField("update_time")
    private LocalDateTime update_time;

    @TableField("update_user")
    private String update_user;

    /**
     * 销项税率编码
     */
    @TableField("output_tax_item_code")
    private String output_tax_item_code;

    /**
     * 销项税率
     */
    @TableField("output_tax_rate")
    private BigDecimal output_tax_rate;

    /**
     * 审核状态(1:待审核；2:审核通过；3:审核未通过)
     */
    @TableField("approve_status")
    private Integer approve_status;

    /**
     * 审核人
     */
    @TableField("approve_user")
    private String approve_user;

    /**
     * NC系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    @TableField("nc_sync_status")
    private Integer nc_sync_status;

    /**
     * 同邦系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    @TableField("topon_sync_status")
    private Integer topon_sync_status;

    /**
     * 版本(乐观锁)
     */
    @TableField("version")
    private Integer version;


}
