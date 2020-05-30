package com.solinia.solinia.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solinia.solinia.Models.EQItem;
import com.solinia.solinia.Models.EQMob;

public class JsonUtils {
	public static <T> String getObjectAsJson(T model) {
		Gson gson = new GsonBuilder().create();
        return gson.toJson(model);
	}
	
	public static EQMob getEQMobFromJson(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, EQMob.class);
	}
	public static EQItem getEQItemFromJson(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, EQItem.class);
	}
}
