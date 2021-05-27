package com.deji.demo.controller;

import com.deji.demo.entity.MerchantSkuDB;
import com.deji.demo.service.MerchantSkuService;
import org.springframework.beans.factory.annotation.Autowired;
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
	public List<MerchantSkuDB> find() {
		System.out.println(111);
		List<MerchantSkuDB> all = skuService.findAll();
		return all;
	}


}
