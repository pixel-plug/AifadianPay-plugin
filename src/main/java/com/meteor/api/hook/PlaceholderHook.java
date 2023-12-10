package com.meteor.api.hook;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.AfadianApi;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "apl";
    }

    @Override
    public @NotNull String getAuthor() {
        return "meteor";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("donate")){
            return String.valueOf(AifadianPay.INSTANCE.getiStorage().queryPlayerDonate(player.getName()));
        }
        return "错误的变量";
    }
}
