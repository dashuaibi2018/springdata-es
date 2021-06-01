package com.deji.demo.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.data.elasticsearch.core.document.Document;

import java.util.HashMap;
import java.util.Map;

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

    public static Document jsonObjToDoc(JSONObject json) {
        Map<String, Object> map = new HashMap<>();
        for (String s : json.keySet()) {
            map.put(s, json.get(s));
        }
        Document document = Document.from(map);
        return document;
    }
}