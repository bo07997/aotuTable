package com.baitian.autotable.util;

import java.io.File;

/**
 * @author ldb
 * @Package com.baitian.autotable.util
 * @date 2020/5/29 11:30
 */
public class CmdUtil {
	public static boolean executeCommand(String command) {
		try {
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			System.out.println("execute command error. command:" + command);
			System.out.println("exception:" + e);
			return false;
		}
		return true;
	}

	public static boolean executeCommand(String command, String dirLocation) {
		try {
			Process p = Runtime.getRuntime().exec(command, null, new File(dirLocation));
			System.out.println();
		} catch (Exception e) {
			System.out.println("execute command error. command:" + command);
			System.out.println("exception:" + e);
			return false;
		}
		return true;
	}
}
