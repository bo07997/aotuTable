package com.baitian.autotable.service;

import com.baitian.autotable.config.CodeConfig;
import com.baitian.autotable.service.common.service.CommonService;
import com.baitian.autotable.service.git.service.GitService;
import com.baitian.autotable.service.mail.service.MailService;
import com.baitian.autotable.service.table.service.TableService;
import com.baitian.autotable.service.tf.service.TFService;
import com.baitian.autotable.webscoket.sendone.Message;
import com.baitian.autotable.webscoket.sendone.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ldb
 * @Package com.baitian.autotable.service
 * @date 2020/5/29 15:25
 * clientService bean里面的key是首字母小写
 */
@Service
public class ClientService {

	public static final int MAP_MAX_SIZE = 10;
	@Autowired
	private TableService tableService;
	@Autowired
	private TFService tfService;
	@Autowired
	private GitService gitService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private MailService mailService;
	private static final TreeMap<Long, Message> TIME_RECORD = new TreeMap<>();

	/**
	 * 请求这个接口默认关注
	 *
	 * @param message
	 * @return
	 */
	public Message getXML(Message message) {
		message.putResult(CodeConfig.SUCCESS);
		message.putValue("tables", commonService.selectAll());
		return message;
	}

	public Message getRecord(Message message) {
		message.putValue("record", TIME_RECORD.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, en -> en.getValue().getMessage())));
		return message;
	}

	public synchronized Message autoTable(Message message) {
		try {
			String branch = message.getMessage().get("branch").toString();
			String tables = message.getMessage().get("tables").toString();
			String name = message.getMessage().get("name").toString();
			checkout(branch, message).getAll(message);
			if (Arrays.stream(tables.split(",")).map(table -> table1(table, message)).allMatch(Objects::isNull)) {
				message.putResult(CodeConfig.SUCCESS);
				return message;
			}
			long ts = System.currentTimeMillis();
			checkChange(message).addCommitPullPush(branch, tables, message);
			mailService.send("【导表人】  " + name + "<br/>" + "【表名】  " + tables + "<br/>【分支】 " + branch + "<br/>" + "【时间戳】 "
					+ ts
					+ "<br/>【结果】 成功");
			TIME_RECORD.put(ts, message);
			if (TIME_RECORD.size() > MAP_MAX_SIZE) {
				TIME_RECORD.pollFirstEntry();
			}
		} catch (Exception e) {
			message.putResult(e.getMessage());
			return message;
		}
		message.putResult(CodeConfig.END);
		return message;
	}

	//	public String autoTable2(String branch, String name) {
	//		StringBuilder buffer = new StringBuilder();
	//		try {
	//			checkout(branch, buffer, message).getAll(buffer).table2(name, buffer).addCommitPullPush(branch, name, buffer);
	//		} catch (Exception e) {
	//
	//		}
	//		return buffer.toString();
	//	}

	ClientService checkout(String branch, Message message) {
		setMessageAndPush("尝试切换分支..." ,message);
		Boolean result = gitService.checkout(branch);
		setMessageAndPush("结果:" + result ,message);
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService getAll(Message message) {
		setMessageAndPush("开始拉取文件..." ,message);
		Boolean result = tfService.getAll();
		setMessageAndPush("结果:" + result ,message);
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService table1(String tableName, Message message) {
		setMessageAndPush("开始导表..." ,message);
		Boolean result = tableService.table1(Collections.singletonList(tableName));
		setMessageAndPush("结果:" + result ,message);
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService checkChange(Message message) {
		if (!gitService.hasChange()) {
			setMessageAndPush("没有变化...",message);
			return null;
		}
		return this;
	}
	//	ClientService table2(String tableName, Message message) {
	//		buffer.append("开始导表...").append("<br />");
	//		Boolean result = tableService.table2(tableName);
	//		buffer.append("结果:").append(result).append("<br />");
	//		if (!result) {
	//			return null;
	//		}
	//		if (!gitService.hasChange()){
	//			buffer.append("没有变化").append("<br />");
	//			return null;
	//		}
	//		return this;
	//	}

	ClientService addCommitPullPush(String branch, String tableName, Message message) {
		setMessageAndPush("开始提交新增...",message);
		Boolean result = gitService.addCommitPullPush(branch, tableName);
		setMessageAndPush("结果:" + result,message);
		if (!result) {
			return null;
		}
		return this;
	}

	void setMessageAndPush(String str, Message message) {
		synchronized (message.getSessionId()) {
			message.putResult(CodeConfig.SUCCESS);
			message.putValue("msg", str);
			WebSocketServer.sendAll(message);
			message.getMessage().remove("msg");
		}
	}
}
