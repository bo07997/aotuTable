package com.baitian.autotable.controller;

import com.baitian.autotable.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {
	@Autowired
	private ClientService clientService;

	//	@GetMapping("/autoTable/getXML")
	//	public List<String> getXML() {
	//		return clientService.getXML();
	//	}

	/**
	 * 通过config运行通用脚本
	 *
	 * @param name 表名
	 * @return 结果
	 */
	//	@GetMapping("/autoTable/1/{branch}/{name}")
	//	public String autoTable1(@PathVariable String branch, @PathVariable String name) {
	//		synchronized (ClientController.class) {
	//			return clientService.autoTable1(branch, name);
	//		}
	//	}

	/**
	 * 运行本地已打包脚本形式
	 *
	 * @param name 脚本名
	 * @return 结果
	 */
	//	@GetMapping("/autoTable/2/{branch}/{name}")
	//	public String autoTable2(@PathVariable String branch, @PathVariable String name) {
	//		synchronized (ClientController.class) {
	//			return clientService.autoTable2(branch, name);
	//		}
	//	}

	/**
	 * 客户端发消息，服务端接收
	 *
	 * @param message
	 */
	// 相当于RequestMapping
	@MessageMapping("/sendServer")
	public void sendServer(String message) {
		System.out.println();
	}

}
