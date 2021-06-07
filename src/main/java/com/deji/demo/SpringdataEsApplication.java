package com.deji.demo;

import cn.hutool.json.JSONException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deji.demo.bean.exception.UserBusinessException;
import com.deji.demo.bean.rsp.BasePageQueryResp;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableElasticsearchRepositories(basePackages = "com.deji.demo.mapper")
@SpringBootApplication()  //exclude= {DataSourceAutoConfiguration.class}
@MapperScan(basePackages = {"com.deji.demo.mapper"})
@Slf4j
public class SpringdataEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringdataEsApplication.class, args);
    }


    @Configuration
    public class CubecarMerchantWebConfig extends WebMvcConfigurationSupport {

        /**
         * 默认resultful通知
         *
         * @author guandh
         */
        @RestControllerAdvice
        public class DefaultRestControllerAdvice {

            private Map<String, Object> createResMap(int result, String resultNote) {
                if (result == 0) {
                    result = -1;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("result", result);
                map.put("resultNote", resultNote);

                return map;
            }

            @ExceptionHandler(UserBusinessException.class)
            public Map<String, Object> onUserBusinessException(UserBusinessException ex) { // 业务类中抛出的异常
                int code = ex.getResultCode() != 0 ? ex.getResultCode() : -1;
                Map<String, Object> map = createResMap(code, ex.getMessage());
                //防止测试姑娘过敏
                //log.debug(ex.getMessage(), ex);
                if (log.isDebugEnabled()) {
                    log.debug(ex.getMessage());
                }
                return map;
            }


            @ExceptionHandler(HttpMessageNotReadableException.class)
            public Map<String, Object> onHttpMessageNotReadableException(HttpMessageNotReadableException ex) { // HttpMessageNotReadableException - JSONException
                Throwable cause = ex.getCause();
                if (cause instanceof JSONException) {
                    return onThrowable(cause);
                }
                return onThrowable(ex);
            }

            @ExceptionHandler(RuntimeException.class)
            public Map<String, Object> onRuntimeException(RuntimeException ex) { // 业务类中抛出的异常
                Map<String, Object> map = createResMap(-1, ex.getMessage());
                log.error(ex.getMessage(), ex);
                return map;
            }

            @ExceptionHandler(MethodArgumentNotValidException.class)
            public Map<String, Object> onValidException(MethodArgumentNotValidException ex) { // validation不通过
                Map<String, Object> map = createResMap(-1,
                        ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
//                log.error(ex.getMessage(), ex);
                return map;
            }

            @ExceptionHandler({HttpClientErrorException.class, SocketTimeoutException.class})
            public Map<String, Object> onHttpClientErrorException(HttpClientErrorException ex) { // 网络异常
                Map<String, Object> map = createResMap(-1, "网络异常，请稍后重试");
                return map;
            }

            @ExceptionHandler(Throwable.class)
            public Map<String, Object> onThrowable(Throwable ex) { // 全局
                Map<String, Object> map = createResMap(-1, ex.getMessage());
                log.error(ex.getMessage(), ex);
                return map;

            }

            @ExceptionHandler(Exception.class)
            public Map<String, Object> onThrowable(Exception ex) { // 全局
                Map<String, Object> map = createResMap(-1, ex.getMessage());
                log.error(ex.getMessage(), ex);
                return map;

            }
        }


        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

            // add by gdh 长整型统一转string
//            SerializeConfig.globalInstance.put(Long.class, ToStringSerializer.instance);
//            SerializeConfig.globalInstance.put(Long.TYPE, ToStringSerializer.instance);

            //1、定义一个convert转换消息的对象
            FastJsonHttpMessageConvertersExtend fastjsonConverter = new FastJsonHttpMessageConvertersExtend();
            fastjsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));

            //2、添加fastjson的配置信息
            FastJsonConfig fastJsonConfig = new FastJsonConfig();
            fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
            fastJsonConfig.setParserConfig(ParserConfig.global);
            fastJsonConfig.setSerializeConfig(SerializeConfig.globalInstance);

            //3、在convert中添加配置信息
            fastjsonConverter.setFastJsonConfig(fastJsonConfig);

            //4、将convert添加到converters中
            converters.add(fastjsonConverter); // fastjson

//			converters.add(new ByteArrayHttpMessageConverter());
        }

        public class FastJsonHttpMessageConvertersExtend extends FastJsonHttpMessageConverter {

            public FastJsonHttpMessageConvertersExtend() {
                super();
            }

            @Override
            protected void writeInternal(Object object, HttpOutputMessage outputMessage)
                    throws IOException, HttpMessageNotWritableException {

                Object param = null;
                if (object instanceof JSONObject) {
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("result", 0);
                    dataMap.put("resultNote", "success");
                    dataMap.put("detail", object);
                    param = dataMap;
                } else if (object instanceof Map) {
                    param = object;
                } else if (object instanceof IPage) {

                    IPage<?> page = (IPage<?>) object;
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("result", 0);
                    dataMap.put("resultNote", "success");
                    dataMap.put("totalRecordNum", page.getTotal());
                    dataMap.put("pages", page.getPages());
                    dataMap.put("data", page.getRecords());
                    dataMap.put("hasNextPage", page.getCurrent() < page.getPages());
                    param = dataMap;

                } else if (object instanceof BasePageQueryResp) {

                    BasePageQueryResp<?> page = (BasePageQueryResp<?>) object;
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("result", 0);
                    dataMap.put("resultNote", "success");
                    dataMap.put("totalRecordNum", page.getTotal());
                    dataMap.put("pages", page.getPages());
                    dataMap.put("data", object);
                    dataMap.put("hasNextPage", page.getCurrent() < page.getPages());
                    param = dataMap;

                } else {
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("result", 0);
                    dataMap.put("resultNote", "success");
                    dataMap.put("data", object);
                    param = dataMap;
                }
                String json = JSON.toJSONString(param);
                log.info("response : {}", json);
                super.writeInternal(param, outputMessage);
            }

        }


    }


}
