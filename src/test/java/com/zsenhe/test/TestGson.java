package com.zsenhe.test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TestGson {
    public static Gson gson = new Gson();


    private static JsonArray toJsonArray(ResultSet rs) throws SQLException {
        JsonArray jsonArray = new JsonArray();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            JsonObject obj = new JsonObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                obj.addProperty(columnName, value.toString());
            }
            jsonArray.add(obj);
        }

        return jsonArray;
    }
}
