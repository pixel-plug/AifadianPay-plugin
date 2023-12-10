package com.zsenhe.test;

import com.google.gson.Gson;

public class TestGson {
    public static Gson gson = new Gson();

    public static void main(String[] args) {
        Item item = new Item("exm");
        String s = gson.toJson(item);
        System.out.println(s);
    }
}
