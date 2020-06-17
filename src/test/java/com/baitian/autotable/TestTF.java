package com.baitian.autotable;

import com.baitian.autotable.util.CmdUtil;

/**
 * @author ldb
 * @Package com.baitian.autotable
 * @date 2020/6/17 17:28
 */
public class TestTF {
	public static void main(String[] args) {
		//		CmdUtil.executeCommand(
		//				"ping localhost",
		//				"C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\Common7\\IDE");
		boolean testt = CmdUtil.executeCommand(
				"C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\Common7\\IDE\\TF.exe get E:\\vstsworkspace\\projectX美术产品\\产品\\项目管理\\每周修改表\\pet");
		System.out.println();
	}
}
