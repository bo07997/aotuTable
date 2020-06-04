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
	private HashMap<String, Integer> name2Times = new HashMap<>();

	/**
	 * 通过locations运行通用脚本
	 *
	 * @param tables locations
	 * @return boolean
	 */
	public boolean table1(List<String> tables) {
		String command = getJarCommand(tables);
		tables.forEach(name -> {
			if (!name2Times.containsKey(name)) {
				name2Times.put(name, 0);
			}
			int times = name2Times.get(name);
			name2Times.put(name, times + 1);
		});
		return CmdUtil.executeCommand(command, dirLocation);
	}

	/**
	 * 运行本地已打包脚本形式
	 *
	 * @param location location
	 * @return boolean
	 */
	public boolean table2(String location) {
		String command = getBatCommand(location);
		return CmdUtil.executeCommand(command, dirLocation);
	}

	public int getCount(String name) {
		if (name2Times.containsKey(name)) {
			return name2Times.get(name);
		}
		return 0;
	}

	public String getJarCommand(List<String> locations) {
		StringBuffer buffer = new StringBuffer("java -jar " + "  " + jarLocation + " -cmd ");
		locations.forEach(str -> {
			buffer.append("./").append(str).append(".xml ");
		});
		return buffer.toString();
	}

	public String getBatCommand(String location) {
		return location + ".bat";
	}

}
