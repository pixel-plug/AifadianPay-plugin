package com.meteor.aifadianpay.data;

import com.meteor.aifadianpay.afdian.response.SkuDetail;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品类
 */
public class ShopItem {

    private String planTitle;

    // 型号
    private Map<String,SkuDetail> skuDetailMap;

    public ShopItem(YamlConfiguration yamlConfiguration){
        this.planTitle = yamlConfiguration.getString("planTitle");
        this.skuDetailMap = new HashMap<>();
        ConfigurationSection skuDetail = yamlConfiguration.getConfigurationSection("skuDetail");
        skuDetail.getKeys(false).stream().forEach(id-> skuDetailMap.put(id,new SkuDetail(skuDetail.getConfigurationSection(id))));
    }

    public String getPlanTitle() {
        return planTitle;
    }

    public Map<String, SkuDetail> getSkuDetailMap() {
        return skuDetailMap;
    }
}
