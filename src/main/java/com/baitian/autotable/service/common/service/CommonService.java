package com.baitian.autotable.service.common.service;

import com.baitian.autotable.service.table.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ldb
 * @date 2020/5/28 14:25
 */
@Service
public class CommonService {

	@Value("${com.baitian.autotable.jar.location}")
	public String jarLocation;
	@Value("${com.baitian.autotable.dir.location}")
	public String dirLocation;
	@Autowired
	private TableService tableService;

	/**
	 * 查询脚本
	 *
	 * @return
	 */
	public List<String> selectAll() {
		String xmlTail = ".xml";
		ArrayList<String> names = getFiles(dirLocation);
		return names.stream()
				.filter(str -> xmlTail.equals(str.substring(str.length() - xmlTail.length())))
				.map(str -> str.substring(0, str.length() - xmlTail.length()))
				.sorted(Comparator.comparing(str -> -tableService.getCount(str))).collect(Collectors.toList());
	}

	public static ArrayList<String> getFiles(String path) {
		ArrayList<String> files = new ArrayList<String>();
		File file = new File(path);
		File[] tempList = file.listFiles();

		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				files.add(tempList[i].getName());
			}
		}
		return files;
	}

	//	/**
	//	 * 查询脚本
	//	 * @param locations
	//	 * @return
	//	 */
	//	public boolean table1(List<String> locations) {
	//		return CmdUtil.executeCommand(command, dirLocation);
	//	}

}
