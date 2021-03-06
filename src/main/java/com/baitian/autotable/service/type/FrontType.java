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
			"E:\\vstsworkspace\\projectX\\source\\tools\\File2File\\jarexport\\",
			"E:\\vstsworkspace\\projectX\\source\\tools\\"),
	;
	public int id;
	String cmd;
	public String location;
	public String getLocation;

	FrontType(int id, String cmd, String location, String getLocation) {
		this.id = id;
		this.cmd = cmd;
		this.location = location;
		this.getLocation = getLocation;
	}

	public String getCmd(String table) {
		return String.format(cmd, table);
	}

	public static FrontType parse(int id) {
		return Arrays.stream(FrontType.values()).filter(ft -> ft.id == id).findAny().orElse(null);
	}

}