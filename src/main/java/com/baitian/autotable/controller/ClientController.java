package com.baitian.autotable.controller;

import com.baitian.autotable.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientController {
	@Autowired
	private ClientService clientService;

	@GetMapping("/autoTable/getXML")
	public List<String> getXML() {
		return clientService.getXML();
	}

	/**
	 * 通过config运行通用脚本
	 *
	 * @param name 表名
	 * @return 结果
	 */
	@GetMapping("/autoTable/1/{branch}/{name}")
	public String autoTable1(@PathVariable String branch, @PathVariable String name) {
		synchronized (ClientController.class) {
			return clientService.autoTable1(branch, name);
		}
	}

	/**
	 * 运行本地已打包脚本形式
	 *
	 * @param name 脚本名
	 * @return 结果
	 */
	@GetMapping("/autoTable/2/{branch}/{name}")
	public String autoTable2(@PathVariable String branch, @PathVariable String name) {
		synchronized (ClientController.class) {
			return clientService.autoTable2(branch, name);
		}
	}

}
