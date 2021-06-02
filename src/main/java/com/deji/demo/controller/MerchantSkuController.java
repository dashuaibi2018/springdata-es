package com.deji.demo.controller;

import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.service.MerchantSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 * @author Administrator
 *
 */

@RestController
@RequestMapping("/test")
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



}
