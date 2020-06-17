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
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			return p.exitValue() == 0;
		} catch (Exception e) {
			System.out.println("execute command error. command:" + command);
			System.out.println("exception:" + e);
			return false;
		}
	}

	public static boolean executeCommand(String command, String dirLocation) {
		try {
			File test = new File(dirLocation);
			Process p = Runtime.getRuntime().exec(command, null, test);
			p.waitFor();
			return p.exitValue() == 0;
		} catch (Exception e) {
			System.out.println("execute command error. command:" + command);
			System.out.println("exception:" + e);
			return false;
		}
	}
}
