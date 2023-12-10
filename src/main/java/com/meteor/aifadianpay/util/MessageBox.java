package com.meteor.aifadianpay.util;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MessageBox {

    private YamlConfiguration yamlConfiguration;

    private String prefix;

    private MessageBox(JavaPlugin javaPlugin,String fileName){

        File file = new File(javaPlugin.getDataFolder()+"/"+fileName);
        if(!file.exists()){
            javaPlugin.saveResource(fileName,false);
        }

        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        this.prefix = ChatColor.translateAlternateColorCodes('&',yamlConfiguration.getString("prefix",javaPlugin.getName()));
    }


    public static MessageBox createMessageBox(JavaPlugin javaPlugin,String fileName){
        return new MessageBox(javaPlugin,fileName);
    }



    public List<String> getMessageList(Map<String,String> replace,String path){
        List<String> stringList = yamlConfiguration.getStringList(path);

        if(stringList==null){
            return Arrays.asList("empty string");
        }

        if(replace!=null) for (String rk : replace.keySet()) stringList.replaceAll(s -> s.replace(rk,replace.get(rk)));


        stringList.replaceAll(s-> ChatColor.translateAlternateColorCodes('&',s.replace("@prefix@",prefix)));
        return stringList;
    }

    public String getMessage(Map<String,String> replace,String path){
        String string = yamlConfiguration.getString(path);
        if(string==null) return "empty string";
        if(replace!=null){
            for (String s : replace.keySet()) {
                string = string.replace(s,replace.get(s));
            }
        }
        String s = ChatColor.translateAlternateColorCodes('&', string.replace("@prefix@", prefix));

        return s;
    }

}
