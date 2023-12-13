package com.meteor.aifadianpay.util;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.data.ShopItem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理配置文件
 */
public class BaseConfig {

    private Map<String, ShopItem> shopItemMap;

    private AifadianPay plugin;

    public static BaseConfig STORE;

    private MessageBox messageBox;


    // 初始化
    public static void init(AifadianPay plugin){
        STORE = new BaseConfig(plugin);
    }

    private BaseConfig(AifadianPay plugin){
        this.plugin = plugin;
        this.reload();
    }

    /**
     * 加载示例商品
     */
    private void loadDefaultExampleShopItem(){
        InputStream inputStream = plugin.getResource("点券.yml");
        try {
            byte[] bytes = new byte[inputStream.available()];
            while (inputStream.read(bytes)!=-1);
            StringReader stringReader = new StringReader(new java.lang.String(bytes,"utf-8"));
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(stringReader);
                yamlConfiguration.save(plugin.getDataFolder()+"/shopitems/点券.yml");
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查配置文件完整
     */
    private void checkConfig(){

        File root = plugin.getDataFolder();

        File shopitemsDir = new File(root,"shopitems");
        if(!shopitemsDir.exists()){
            shopitemsDir.mkdirs();
            this.loadDefaultExampleShopItem();
        }

        Arrays.asList("config.yml","message.yml").forEach(fileName->{
            File file = new File(root,fileName);
            if(!file.exists()) plugin.saveResource(fileName,false);
        });

        // 载入商品
        this.shopItemMap = new HashMap<>();
        for (File file : shopitemsDir.listFiles()) {
            ShopItem shopItem = new ShopItem(YamlConfiguration.loadConfiguration(file));
            shopItemMap.put(shopItem.getPlanTitle(),shopItem);
        }
        plugin.getLogger().info("载入了 "+shopItemMap.size()+" 个商品");
    }

    /**
     * 重载配置文件
     * @return
     */
    public void reload(){
        this.checkConfig();
        this.plugin.reloadConfig();
        this.messageBox = MessageBox.createMessageBox(plugin,"message.yml");
        AifadianPay.debug = plugin.getConfig().getBoolean("debug",false);

        if(AifadianPay.debug) plugin.getLogger().info("已开启DEBUG模式，将在控制台输出调试信息");

    }

    public int getHttpTimeout(){
        return plugin.getConfig().getInt("timeout",30)*1000;
    }


    public MessageBox getMessageBox() {
        return messageBox;
    }

    public Map<String, ShopItem> getShopItemMap() {
        return shopItemMap;
    }
}
