package com.meteor.aifadianpay.afdian.response;


import com.google.gson.annotations.SerializedName;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class SkuDetail {
    @SerializedName("sku_id")
    private String skuId;

    @SerializedName("count")
    private int count;

    @SerializedName("name")
    private String name;
    
    @SerializedName("album_id")
    private String albumId;

    @SerializedName("pic")
    private String pic;

    // 奖励
    private transient List<String> rewards;
    private transient String displayName;

    public SkuDetail(ConfigurationSection configurationSection){
        this.name = configurationSection.getName();
        this.displayName = configurationSection.getString("displayName");
        this.rewards = configurationSection.getStringList("rewards");
    }

    public List<String> getRewards() {
        return rewards;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSkuId() {
        return skuId;
    }
    
    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAlbumId() {
        return albumId;
    }
    
    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    @Override
    public String toString() {
        return "SkuDetail{" +
                "skuId='" + skuId + '\'' +
                ", count=" + count +
                ", name='" + name + '\'' +
                ", albumId='" + albumId + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }

    public String getPic() {
        return pic;
    }
    
    public void setPic(String pic) {
        this.pic = pic;
    }
}