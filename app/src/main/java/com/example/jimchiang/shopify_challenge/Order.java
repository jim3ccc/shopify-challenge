package com.example.jimchiang.shopify_challenge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Order {
    public JsonObject customer;
    public String total_price;
    public String currency;
    public JsonArray line_items;
}
