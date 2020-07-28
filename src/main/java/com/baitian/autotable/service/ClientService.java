package com.baitian.autotable.service;

import com.baitian.autotable.config.CodeConfig;
import com.baitian.autotable.service.common.service.CommonService;
import com.baitian.autotable.service.git.service.GitService;
import com.baitian.autotable.service.mail.service.MailService;
import com.baitian.autotable.service.table.service.TableService;
import com.baitian.autotable.service.tf.service.TFService;
import com.baitian.autotable.util.exception.AutoTableInterruptException;
import com.baitian.autotable.webscoket.bean.Message;
import com.baitian.autotable.webscoket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ldb
 * @Package com.baitian.autotable.service
 * @date 2020/5/29 15:25
 * clientService bean里面的key是首字母小写
 */
@Service
public class ClientService {

	public static final int MAP_MAX_SIZE = 10;
	public static final String REGEX = ",";
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
	private static final ArrayBlockingQueue<Message> FAIR_QUEUE = new ArrayBlockingQueue<>(10, true);
	private static final ExecutorService SINGLE_POOL = Executors.newSingleThreadExecutor();

	{
		SINGLE_POOL.execute(() -> {
			while (true) {
				try {
					WebSocketServer.sendAll(autoTableConsumption(FAIR_QUEUE.take()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param message message
	 * @return Message
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

	public Message autoTable(Message message) {
		try {
			FAIR_QUEUE.offer(message, 10, TimeUnit.SECONDS);
			message.putResult(CodeConfig.SUCCESS);
			message.putMsg(String.format("放入任务队列成功,当前待完成任务数目:%d", FAIR_QUEUE.size()));
		} catch (InterruptedException e) {
			e.printStackTrace();
			message.putResult(CodeConfig.ERROR);
			message.putMsg("放入任务队列超时(队列已满)");
		}
		return message;
	}

	private synchronized Message autoTableConsumption(Message message) {
		try {
			if (!message.assertParams("branch", "tables", "name", "mail", "windy")) {
				message.putMsg("参数错误");
				message.putResult(CodeConfig.PARAM_ERROR);
				return message;
			}
			String branch = message.getMessage().getString("branch");
			String tables = message.getMessage().getString("tables");
			String name = message.getMessage().getString("name");
			String mail = message.getMessage().getString("mail");
			boolean windy = message.getMessage().getBool("windy");
			checkout(branch, message);
			getAll(message);
			Arrays.stream(tables.split(REGEX)).forEach(table -> table1(table, message));
			long ts = System.currentTimeMillis();
			checkChange(message);
			addCommitPullPush(branch, tables, message, name);
			if (windy) {
				mailService
						.send("【导表人】  " + name + "<br/>" + "【表名】  " + tables + "<br/>【分支】 " + branch + "<br/>" + "【时间】 "
								+ FORMAT.format(new Date())
								+ "<br/>【结果】 成功", mail);
			}
			TIME_RECORD.put(ts, message);
			if (TIME_RECORD.size() > MAP_MAX_SIZE) {
				TIME_RECORD.pollFirstEntry();
			}
		} catch (Exception e) {
			message.putResult(CodeConfig.ERROR);
			message.putMsg(e.getMessage());
			return message;
		}
		message.putResult(CodeConfig.END);
		return message;
	}

	void checkout(String branch, Message message) {
		setMessageAndPushAll("尝试切换分支...", message);
		boolean result = gitService.checkout(branch, message);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	void getAll(Message message) {
		setMessageAndPushAll("开始拉取文件...", message);
		tfService.getAllTable();
		boolean result = tfService.getAll();
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	void table1(String tableName, Message message) {
		setMessageAndPushAll("开始导表...", message);
		boolean result = tableService.table1(Collections.singletonList(tableName));
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	void checkChange(Message message) {
		if (!gitService.hasChange()) {
			setMessageAndPushAll("没有变化...", message);
			throw new AutoTableInterruptException();
		}
	}

	void addCommitPullPush(String branch, String tableName, Message message, String name) {
		setMessageAndPushAll("开始提交新增...", message);
		boolean result = gitService.addCommitPullPush(branch, tableName + "by " + name);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	void setMessageAndPushAll(String str, Message message) {
		synchronized (message.getSessionId()) {
			message.putResult(CodeConfig.SUCCESS);
			message.putMsg(str);
			WebSocketServer.sendAll(message);
			message.clearMsg();
		}
	}

	void setMessageAndPush(String str, Message message) {
		synchronized (message.getSessionId()) {
			message.putResult(CodeConfig.SUCCESS);
			message.putMsg(str);
			WebSocketServer.sendTo(message);
			message.clearMsg();
		}
	}
}
