package com.company;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;

public class DataViewer {

    public static void showAllCategories(String response) {
        JsonObject root = JsonParser.parseString(response).getAsJsonObject();
        JsonObject categories = root.getAsJsonObject("categories");
//        System.out.println("Total: " + categories.getAsJsonPrimitive("total")/*.getAsString()*/);

        for (JsonElement item : categories.getAsJsonArray("items")) {
            System.out.println(item.getAsJsonObject().get("name").getAsString());
        }
    }
}
