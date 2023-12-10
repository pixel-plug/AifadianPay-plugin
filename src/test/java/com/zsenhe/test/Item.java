package com.zsenhe.test;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("id")
    private String id;

    public Item(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
