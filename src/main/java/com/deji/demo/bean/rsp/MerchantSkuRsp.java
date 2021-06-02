package com.deji.demo.bean.rsp;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品sku信息表
 * </p>
 *
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MerchantSkuRsp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SKU编码
     */
    private Long merchantSkuId;

    /**
     * 商品名
     */
    private String skuName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

//    private String createTimeStr;
//    private String updateTimeStr;

    private Long createTimeLong;
    private Long updateTimeLong;

    private String createUser;

    /**
     * 商品条码
     */
    private String skuSn;

    /**
     * 管理编码(助记码)
     */
    private String skuMngCode;

    /**
     * 外部系统商品编码
     */
    private String outerSn;

    /**
     * 产品分类
     */
    private Long categoryId;

    private Integer merchantId;

    private BigDecimal marketPrice;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 规格
     */
    private String specTxt;

    /**
     * 库存单位ID
     */
    private Integer unitId;

    /**
     * 库存单位名称
     */
    private String unitName;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 进项税率编码
     */
    private String inputTaxCode;

    /**
     * 进项税率
     */
    private BigDecimal inputTaxRate;

    /**
     * NC财务系统关联商品编号
     */
    private String ncSkuCode;

    /**
     * NC财务系统关联商品doc的id（NC商品修改必传字段)
     */
    private String ncSkuDocId;

    /**
     * 同邦系统关联商品编号
     */
    private String toponSkuCode;

    /**
     * 测试商品标识(0:非测试商品；1:测试商品)
     */
    private Boolean testFlag;

    /**
     * 有效标识(0:无效;1:有效)
     */
    private Boolean activeStatus;


    private String updateUser;

    /**
     * 销项税率编码
     */
    private String outputTaxItemCode;

    /**
     * 销项税率
     */
    private BigDecimal outputTaxRate;

    /**
     * 审核状态(1:待审核；2:审核通过；3:审核未通过)
     */
    private Integer approveStatus;

    /**
     * 审核人
     */
    private String approveUser;

    /**
     * NC系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    private Integer ncSyncStatus;

    /**
     * 同邦系统同步状态(1:待同步; 2:同步成功；3:同步失败)
     */
    private Integer toponSyncStatus;

    /**
     * 版本(乐观锁)
     */
    private Integer version;

    private Long timestamp;

}
