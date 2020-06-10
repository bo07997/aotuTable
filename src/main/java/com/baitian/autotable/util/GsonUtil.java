package com.baitian.autotable.util;

import com.baitian.autotable.webscoket.sendone.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ldb
 * @Package com.baitian.autotable.util
 * @date 2020/6/9 17:58
 */
public class GsonUtil {
	static final Gson GSON = new GsonBuilder().create();

	public static Map<String, Object> toMap(String json) {
		return GSON.fromJson(json, new TypeToken<HashMap<String, Object>>() {
		}.getType());
	}

	public static String toJson(Object obj) {
		return GSON.toJson(obj);
	}

	public static Message getMessage(Object obj) {
		Message message = new Message();
		//		message.setMessage(toJson(obj));
		return message;
	}

}
