package com.deji.demo.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.deji.demo.bean.entity.MerchantSku;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataUtils {

    /**
     * 转换为驼峰格式/转换为下划线方式
     *
     * @param json  等待转换的方法
     * @param upper 首字母大写或者小写
     * @return 转换后的
     */
    public static JSONObject formatKey(JSONObject json, boolean upper) {
        JSONObject real = new JSONObject();
        for (String it : json.keySet()) {
            Object objR = json.get(it);
            // 转换为驼峰格式/转换为下划线方式
            String key = it.contains("_") ? StrUtil.toCamelCase(it) : StrUtil.toUnderlineCase(it);
            // 首字母大写或者小写
            key = upper ? StrUtil.upperFirst(key) : StrUtil.lowerFirst(key);
            if (objR instanceof String) {
                real.set(key, objR);
            } else if (objR instanceof JSONObject) {
                real.set(key, formatKey((JSONObject) objR, upper));
            } else if (objR instanceof JSONArray) {
                JSONArray jsonA = new JSONArray();
                for (Object objA : (JSONArray) objR) {
                    jsonA.add(formatKey((JSONObject) objA, upper));
                }
                real.set(key, jsonA);
            } /*else if (objR instanceof Long) {
                LocalDateTime dateTime = null;
                if (StrUtil.contains(it, "time")) {
                    dateTime = LocalDateTimeUtil.of((Long) objR, ZoneId.systemDefault());
                    real.set(key, dateTime);
                }else {
                    real.set(key, objR);
                }
            }*/ else {
                real.set(key, objR);
            }
        }
        return real;
    }

    /**
     * @param json
     * @description: 批量操作时jsonObj转Document
     * @return: org.springframework.data.elasticsearch.core.document.Document
     * @author: sj
     * @time: 2021/6/3 1:24 下午
     */
    public static Document jsonObjToDoc(JSONObject json) {
        Map<String, Object> map = new HashMap<>();
        for (String s : json.keySet()) {
            map.put(s, json.get(s));
        }
        Document document = Document.from(map);
        return document;
    }


    /**
     * @param searchHits
     * @description: searchHits转list
     * @return: java.util.List<com.deji.demo.bean.entity.MerchantSku>
     * @author: sj
     * @time: 2021/6/3 1:25 下午
     */
    public static List<MerchantSku> hitsToResList(SearchHits<MerchantSku> searchHits) {

        List<MerchantSku> list = searchHits.getSearchHits().stream().map(a -> a.getContent()).collect(Collectors.toList());
        return list;
    }


}