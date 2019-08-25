package com.solinia.solinia.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
	public static <T> String getObjectAsJson(T model) {
		Gson gson = new GsonBuilder().create();
        return gson.toJson(model);
        
	}
}
