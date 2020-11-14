package com.baitian.autotable.service.table.service;

import com.baitian.autotable.util.CmdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author ldb
 * @date 2020/5/28 14:25
 */
@Service
public class TableService {

	@Value("${com.baitian.autotable.jar.location}")
	public String jarLocation;
	@Value("${com.baitian.autotable.dir.location}")
	public String dirLocation;
	private HashMap<Integer, Integer> name2Times = new HashMap<>();

	/**
	 * 通过locations运行通用脚本
	 *
	 * @param tables locations
	 * @param id
	 * @return boolean
	 */
	public boolean table1(List<String> tables, int id) {
		String command = getJarCommand(tables);
		name2Times.putIfAbsent(id, 0);
		name2Times.computeIfPresent(id, (k, v) -> v + 1);
		return CmdUtil.executeCommand(command, dirLocation);
	}

	public int getCount(int id) {
		if (name2Times.containsKey(id)) {
			return name2Times.get(id);
		}
		return 0;
	}

	public String getJarCommand(List<String> locations) {
		StringBuffer buffer = new StringBuffer("java -jar " + "  " + jarLocation + " -cmd ");
		locations.forEach(str -> buffer.append("./").append(str).append(".xml "));
		return buffer.toString();
	}

	public String getBatCommand(String location) {
		return location + ".bat";
	}

	/**
	 * 前端使用
	 */
	/**
	 * 通过locations运行通用脚本
	 *
	 * @param cmd cmd
	 * @return boolean
	 */
	public boolean table3(String cmd, String location) {
		return CmdUtil.executeCommand(cmd, location);
	}
}
