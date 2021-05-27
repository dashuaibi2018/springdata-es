package com.deji.demo;

import cn.hutool.json.JSONUtil;
import com.deji.demo.entity.LogEntity;
import com.deji.demo.entity.MerchantSkuES;
import com.deji.demo.service.MerchantSkuService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;

import java.util.Map;

@Slf4j
@SpringBootTest
public class ESTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;

    @Autowired
    MerchantSkuService skuService;


    @Test
    public void deleteTemplate(String templateName) {
        IndexOperations ops = esRestTemplate.indexOps(LogEntity.class);

        final boolean b = ops.deleteTemplate(templateName);
        System.out.println(b);
    }


    /**
     * @param
     * @description: Template相关
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 13:53
     */
    @Test
    public void createLogEntity() {
        IndexOperations ops = esRestTemplate.indexOps(LogEntity.class);

        String templateName = "demolog";

        Map<String, Object> mappings = null;
        Map<String, Object> settings = null;
        if (ops.exists()) {
            mappings = ops.getMapping();
            settings = ops.getSettings();

            // {"properties":{"level":{"type":"keyword"},"title":{"type":"text"},"content":{"type":"text"},"recordTime":{"format":"date_time","type":"date"},"objContent":{"type":"object"}}}
            System.out.println(JSONUtil.toJsonStr(mappings));
            //{"index.creation_date":"1621827252763","index.uuid":"FEVk7f-bSICPgbgaWhhDzw","index.version.created":"7090299","index.provided_name":"demo-log","index.number_of_replicas":"1","index.store.type":"fs","index.refresh_interval":"1s","index.number_of_shards":"1"}
            System.out.println(JSONUtil.toJsonStr(settings));
        }

        if (ops.existsTemplate(templateName)) {
            deleteTemplate(templateName);
        }
        Document mapping = Document.parse("{\"properties\":{\"level\":{\"type\":\"keyword\"},\"title\":{\"type\":\"keyword\"},\"content\":{\"type\":\"text\"},\"recordTime\":{\"format\":\"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_second\",\"type\":\"date\"},\"objContent\":{\"type\":\"object\"},\"age\":{\"type\":\"keyword\"}}}");
        Document setting = Document.parse("{\"index.number_of_shards\":\"2\",\"index.number_of_replicas\":\"2\"}");
        PutTemplateRequest template = PutTemplateRequest.builder(templateName, "demo*")
                .withMappings(Document.from(mapping)).withSettings(setting)
                .withVersion(1).build();

        ops.putTemplate(template);
    }

    /**
     * @param
     * @description: Template相关
     * @return: void
     * @author: sujun
     * @time: 2021/5/24 13:53
     */
    @Test
    public void createMerchantSkuES() {
        IndexOperations ops = esRestTemplate.indexOps(MerchantSkuES.class);
        String templateName = "merchantsku";

        Map<String, Object> mappings;
        Map<String, Object> settings;
        if (ops.exists()) {
            mappings = ops.getMapping();
            settings = ops.getSettings();
            //{"properties":{"approve_status":{"type":"keyword"},"merchant_id":{"type":"keyword"},"outer_sn":{"type":"keyword"},"topon_sync_status":{"type":"keyword"},"nc_sku_doc_id":{"type":"keyword"},"output_tax_item_code":{"type":"keyword"},"update_time":{"type":"date"},"update_user":{"type":"text","analyzer":"ik_max_word"},"category_id":{"type":"keyword"},"nc_sku_code":{"type":"keyword"},"sku_name":{"type":"text","search_analyzer":"ik_smart","analyzer":"ik_max_word"},"test_flag":{"type":"keyword"},"sku_mng_code":{"type":"keyword"},"supplier_name":{"type":"text","analyzer":"ik_max_word"},"nc_sync_status":{"type":"keyword"},"unit_id":{"type":"integer"},"input_tax_rate":{"type":"float"},"create_time":{"type":"date"},"sku_sn":{"type":"keyword"},"topon_sku_code":{"type":"keyword"},"version":{"type":"keyword"},"brand_id":{"type":"keyword"},"unit_name":{"type":"keyword"},"active_status":{"type":"keyword"},"input_tax_code":{"type":"keyword"},"output_tax_rate":{"type":"float"},"spec_txt":{"type":"text","analyzer":"ik_max_word"},"approve_user":{"type":"text","analyzer":"ik_max_word"},"merchant_sku_id":{"type":"long"},"market_price":{"type":"float"},"_class":{"type":"text","fields":{"keyword":{"ignore_above":256,"type":"keyword"}}},"create_user":{"type":"text","analyzer":"ik_max_word"},"supplier_id":{"type":"keyword"}}}
            System.out.println(JSONUtil.toJsonStr(mappings));
            //{"index.creation_date":"1621827252763","index.uuid":"FEVk7f-bSICPgbgaWhhDzw","index.version.created":"7090299","index.provided_name":"demo-log","index.number_of_replicas":"1","index.store.type":"fs","index.refresh_interval":"1s","index.number_of_shards":"1"}
            System.out.println(JSONUtil.toJsonStr(settings));
        }

        if (ops.existsTemplate(templateName)) {
            deleteTemplate(templateName);
        }

        //创建模板中的索引别名
        AliasActionParameters merchantParams = AliasActionParameters.builderForTemplate().withAliases("baba").build();
        AliasActions aliasActions = new AliasActions(new AliasAction.Add(merchantParams));

        Document mapping = Document.parse("{\"properties\":{\"approve_status\":{\"type\":\"keyword\"},\"merchant_id\":{\"type\":\"keyword\"},\"outer_sn\":{\"type\":\"keyword\"},\"topon_sync_status\":{\"type\":\"keyword\"},\"nc_sku_doc_id\":{\"type\":\"keyword\"},\"output_tax_item_code\":{\"type\":\"keyword\"},\"update_time\":{\"type\":\"date\"},\"update_user\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"},\"category_id\":{\"type\":\"keyword\"},\"nc_sku_code\":{\"type\":\"keyword\"},\"sku_name\":{\"type\":\"text\",\"search_analyzer\":\"ik_smart\",\"analyzer\":\"ik_max_word\"},\"test_flag\":{\"type\":\"keyword\"},\"sku_mng_code\":{\"type\":\"keyword\"},\"supplier_name\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"},\"nc_sync_status\":{\"type\":\"keyword\"},\"unit_id\":{\"type\":\"integer\"},\"input_tax_rate\":{\"type\":\"float\"},\"create_time\":{\"type\":\"date\"},\"sku_sn\":{\"type\":\"keyword\"},\"topon_sku_code\":{\"type\":\"keyword\"},\"version\":{\"type\":\"keyword\"},\"brand_id\":{\"type\":\"keyword\"},\"unit_name\":{\"type\":\"keyword\"},\"active_status\":{\"type\":\"keyword\"},\"input_tax_code\":{\"type\":\"keyword\"},\"output_tax_rate\":{\"type\":\"float\"},\"spec_txt\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"},\"approve_user\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"},\"merchant_sku_id\":{\"type\":\"long\"},\"market_price\":{\"type\":\"float\"},\"_class\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"ignore_above\":256,\"type\":\"keyword\"}}},\"create_user\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\"},\"supplier_id\":{\"type\":\"keyword\"}}}\n");
//        Document mapping = Document.parse("{\"properties\":{\"approve_status\":{\"type\":\"keyword\"},\"merchant_id\":{\"type\":\"keyword\"},\"outer_sn\":{\"type\":\"keyword\"},\"topon_sync_status\":{\"type\":\"keyword\"},\"nc_sku_doc_id\":{\"type\":\"keyword\"},\"output_tax_item_code\":{\"type\":\"keyword\"},\"update_time\":{\"type\":\"date\"},\"update_user\":{\"type\":\"text\"},\"category_id\":{\"type\":\"keyword\"},\"nc_sku_code\":{\"type\":\"keyword\"},\"sku_name\":{\"type\":\"text\"},\"test_flag\":{\"type\":\"keyword\"},\"sku_mng_code\":{\"type\":\"keyword\"},\"supplier_name\":{\"type\":\"text\"},\"nc_sync_status\":{\"type\":\"keyword\"},\"unit_id\":{\"type\":\"integer\"},\"input_tax_rate\":{\"type\":\"float\"},\"create_time\":{\"type\":\"date\"},\"sku_sn\":{\"type\":\"keyword\"},\"topon_sku_code\":{\"type\":\"keyword\"},\"version\":{\"type\":\"keyword\"},\"brand_id\":{\"type\":\"keyword\"},\"unit_name\":{\"type\":\"keyword\"},\"active_status\":{\"type\":\"keyword\"},\"input_tax_code\":{\"type\":\"keyword\"},\"output_tax_rate\":{\"type\":\"float\"},\"spec_txt\":{\"type\":\"text\"},\"approve_user\":{\"type\":\"text\"},\"merchant_sku_id\":{\"type\":\"long\"},\"market_price\":{\"type\":\"float\"},\"create_user\":{\"type\":\"text\"},\"supplier_id\":{\"type\":\"keyword\"}}}\n");
        Document setting = Document.parse("{\"index.number_of_shards\":\"1\",\"index.number_of_replicas\":\"1\"}");
        PutTemplateRequest template = PutTemplateRequest.builder(templateName, "merchant_sku*")
                .withMappings(Document.from(mapping)).withSettings(setting)
                .withVersion(1).withAliasActions(aliasActions).build();

        ops.putTemplate(template);
    }





}