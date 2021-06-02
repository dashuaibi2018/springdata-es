package com.deji.demo.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.deji.demo.bean.ResultDto;
import com.deji.demo.bean.entity.MerchantSku;
import com.deji.demo.bean.entity.PushMsg;
import com.deji.demo.bean.rsp.MerchantSkuRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ESUtils {

    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;

    /**
     * @param _class
     * @param beanList
     * @description: 批量新增或修改
     * @return: int
     * @author: sj
     * @time: 2021/6/2 1:32 下午
     */
    public int batchAdd(Class<?> _class, List<?> beanList) throws Exception {
        List<UpdateQuery> queries = new ArrayList<>();
        UpdateQuery updateQuery;
        int counter = 0;

        for (Object item : beanList) {
            //分批提交索引
            if (counter != 0 && counter % 1000 == 0) {
                esRestTemplate.bulkUpdate(queries, _class);  //IndexCoordinates.of(indexName)
                queries.clear();
                log.info("bulkIndex counter :  {}", counter);
            }

            String methodName = "";
            if (_class.equals(MerchantSku.class)) {
                methodName = "getMerchantSkuId";
            } else if (_class.equals(PushMsg.class)) {
                methodName = "getRecUid";
            }

            String recId = StrUtil.toString(ReflectUtil.invoke(item, methodName)); //主键
            JSONObject formatObj = DataUtils.formatKey(JSONUtil.parseObj(item), false);
            updateQuery = UpdateQuery.builder(recId)
                    .withDocument(DataUtils.jsonObjToDoc(formatObj))
                    .withDocAsUpsert(true) // 默认false,true表示更新时不存在就插入
                    .withRetryOnConflict(1) //冲突重试
                    .withRouting(recId)
                    .build();

            queries.add(updateQuery);
            counter++;
        }

        //不足批的索引最后不要忘记提交
        if (queries.size() > 0) {
            esRestTemplate.bulkUpdate(queries, _class);
            log.info("bulkIndex counter :  {}", counter);
        }
        esRestTemplate.refresh(_class);

        return counter;
    }


    public static ResultDto transToResDto(Page<?> search) {
        ResultDto resultDto = new ResultDto();

        resultDto.setTotal(search.getTotalElements())
                .setPages(search.getTotalPages())
                .setCurrentPage(search.getNumber() + 1)
                .setPageSize(search.getSize())
                .setRecords(search.getContent());

        return resultDto;
    }


    public static MerchantSkuRsp merchantSkuToRsp(MerchantSku merchantSku) {
        MerchantSkuRsp skuRsp = new MerchantSkuRsp();
        BeanUtil.copyProperties(merchantSku, skuRsp);
//        skuRsp.setCreateTimeStr(LocalDateTimeUtil.format(skuRsp.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
//        skuRsp.setUpdateTimeStr(LocalDateTimeUtil.format(skuRsp.getUpdateTime(),"yyyy-MM-dd HH:mm:ss"));
        skuRsp.setCreateTimeLong(Timestamp.valueOf(skuRsp.getCreateTime()).getTime());
        skuRsp.setUpdateTimeLong(Timestamp.valueOf(skuRsp.getUpdateTime()).getTime());

        return skuRsp;
    }

}