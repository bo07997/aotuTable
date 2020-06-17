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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 请求这个接口默认关注
	 *
	 * @param message
	 * @return
	 */
	public Message getXML(Message message) {
		message.putResult(CodeConfig.SUCCESS);
		message.putValue("tables", commonService.selectAll());
		TreeMap<Long, Object> kvTreeMap = new TreeMap<>();
		for (Map.Entry<Long, Message> longMessageEntry : TIME_RECORD.entrySet()) {
			kvTreeMap.put(longMessageEntry.getKey(), longMessageEntry.getValue().getMessage());
		}
		message.putValue("record", kvTreeMap);
		return message;
	}

	public synchronized Message autoTable(Message message) {
		try {
			String branch = message.getMessage().get("branch").toString();
			String tables = message.getMessage().get("tables").toString();
			String name = message.getMessage().get("name").toString();
			if (checkout(branch, message).getAll(message) == null) {
				message.putResult(CodeConfig.ERROR);
				return message;
			}

			if (Arrays.stream(tables.split(",")).map(table -> table1(table, message)).allMatch(Objects::isNull)) {
				message.putResult(CodeConfig.ERROR);
				return message;
			}
			long ts = System.currentTimeMillis();
			checkChange(message).addCommitPullPush(branch, tables, message);
			mailService.send("【导表人】  " + name + "<br/>" + "【表名】  " + tables + "<br/>【分支】 " + branch + "<br/>" + "【时间】 "
					+ FORMAT.format(new Date())
					+ "<br/>【结果】 成功");

			TIME_RECORD.put(ts, message);
			if (TIME_RECORD.size() > MAP_MAX_SIZE) {
				TIME_RECORD.pollFirstEntry();
			}
		} catch (Exception e) {
			message.putResult(CodeConfig.ERROR);
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
		setMessageAndPushAll("尝试切换分支...", message);
		boolean result = gitService.checkout(branch);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService getAll(Message message) {
		setMessageAndPushAll("开始拉取文件...", message);
		boolean result = tfService.getAll();
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService table1(String tableName, Message message) {
		setMessageAndPushAll("开始导表...", message);
		boolean result = tableService.table1(Collections.singletonList(tableName));
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService checkChange(Message message) {
		if (!gitService.hasChange()) {
			setMessageAndPushAll("没有变化...", message);
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
		setMessageAndPushAll("开始提交新增...", message);
		boolean result = gitService.addCommitPullPush(branch, tableName);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			return null;
		}
		return this;
	}

	void setMessageAndPushAll(String str, Message message) {
		synchronized (message.getSessionId()) {
			message.putResult(CodeConfig.SUCCESS);
			message.putValue("msg", str);
			WebSocketServer.sendAll(message);
			message.getMessage().remove("msg");
		}
	}

	void setMessageAndPush(String str, Message message) {
		synchronized (message.getSessionId()) {
			message.putResult(CodeConfig.SUCCESS);
			message.putValue("msg", str);
			WebSocketServer.sendTo(message);
			message.getMessage().remove("msg");
		}
	}
}
