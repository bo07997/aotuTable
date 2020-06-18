package com.baitian.autotable.webscoket.bean;

import java.util.HashMap;

/**
 * @author ldb
 * @Package com.baitian.autotable.webscoket.bean
 * @date 2020/6/18 13:55
 */
public class MessageMap<K, V> extends HashMap<K, V> {

	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}

	public String getString(String key) {
		return get(key).toString();
	}

	public boolean getBool(String key) {
		return Boolean.parseBoolean(getString(key));
	}
}
