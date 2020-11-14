package com.baitian.autotable.service.type;

import java.util.Arrays;

/**
 * @author ldb
 * @Package com.baitian.autotable.service.type
 * @date 2020/11/13 16:18
 */
public enum FrontType {
	/**
	 * f2f
	 */
	FF(
			1,
			"java -jar File2File.jar -k  %s -gn",
			"E:\\vstsworkspace\\projectX\\source\\tools\\File2File\\jarexport\\"),
	;
	public int id;
	String cmd;
	public String location;

	FrontType(int id, String cmd, String location) {
		this.id = id;
		this.cmd = cmd;
		this.location = location;
	}

	public String getCmd(String table) {
		return String.format(cmd, table);
	}

	public static FrontType parse(int id) {
		return Arrays.stream(FrontType.values()).filter(ft -> ft.id == id).findAny().orElse(null);
	}

}