package com.baitian.autotable.util;

import com.baitian.autotable.util.result.CmdResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author ldb
 * @Package com.baitian.autotable.util
 * @date 2020/5/29 11:30
 */
public class CmdUtil {
	//git用
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

	//导表用
	public static CmdResult executeCommand(String command, String dirLocation) {
		try {
			File dir = new File(dirLocation);
			Process p = Runtime.getRuntime().exec(command, null, dir);
			p.waitFor();
			InputStream stdin = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stdin, "GB2312");
			BufferedReader br = new BufferedReader(isr);

			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			return new CmdResult(p.exitValue() == 0 && builder.length() == 0, builder.toString());
		} catch (Exception e) {
			System.out.println("execute command error. command:" + command);
			System.out.println("exception:" + e);
			return new CmdResult(false, e.getMessage());
		}
	}

	//测试
	public static boolean executeCommandOut(String command) {
		try {
			Process p = Runtime.getRuntime().exec("ipconfig");
			InputStream stdin = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stdin, "GB2312");
			BufferedReader br = new BufferedReader(isr);

			String line;

			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			p.waitFor();
			return p.exitValue() == 0;
		} catch (Exception e) {
			System.out.println("execute command error. command:" + command);
			System.out.println("exception:" + e);
			return false;
		}
	}
}
