package com.baitian.autotable.webscoket.bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

	public List<Integer> getIntList(String key, String regex) {
		return Arrays.stream(get(key).toString().split(regex)).map(Integer::parseInt).collect(Collectors.toList());
	}

	public boolean getBool(String key) {
		return Boolean.parseBoolean(getString(key));
	}
}
