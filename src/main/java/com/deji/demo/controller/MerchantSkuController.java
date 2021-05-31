package com.deji.demo.controller;

import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.req.MerchantSkuReq;
import com.deji.demo.bean.rsp.MerchantSkuRsp;
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
@RequestMapping("/test")
public class MerchantSkuController {
	
	@Autowired
	MerchantSkuService skuService;
	
	/**
	 * 更新
	 * @param
	 * @return
	 */
	@RequestMapping("find")
	public List<MerchantSku> find() {
		System.out.println(111);
		List<MerchantSku> all = skuService.findAll();
		return all;
	}

	/**
	 * 更新
	 * @param
	 * @return
	 */
	@RequestMapping("findBySkuName")
	public List<MerchantSkuRsp> findByMerchantSkuName(@RequestBody @Validated MerchantSkuReq req) {
		List<MerchantSkuRsp> all = skuService.findByMerchantName(req);
		return all;
	}


	/**
	 * 更新
	 * @param
	 * @return
	 */
	@RequestMapping("test")
	public String test1() {
		return "111111";
	}


}
