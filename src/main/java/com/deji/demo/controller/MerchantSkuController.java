package com.deji.demo.controller;

import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.service.MerchantSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 * @author Administrator
 *
 */

@RestController
@RequestMapping("/es/erp/product/v1.0/merchantSku")
public class MerchantSkuController {
	
	@Autowired
	MerchantSkuService skuService;


	/**
	 *
	 * @param
	 * @return
	 */
	@RequestMapping("findBySkuName")
	public ResultDto findByMerchantSkuName(@RequestBody @Validated MerchantSkuReq req) {
		ResultDto all = skuService.findSkuNameOwn(req);
		return all;
	}


	/**
	 *
	 * @param
	 * @return
	 */
	@RequestMapping("/list")
	public List<MerchantSku> getMerchantSkulist(@RequestBody @Validated MerchantSkuReq req) {
		List<MerchantSku> all = skuService.getMerchantSkulist(req);
		return all;
	}



}
