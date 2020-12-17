package com.baitian.autotable.service;

import com.baitian.autotable.config.CodeConfig;
import com.baitian.autotable.db.dao.RelationRepository;
import com.baitian.autotable.entity.Relation;
import com.baitian.autotable.service.git.service.GitService;
import com.baitian.autotable.service.mail.service.MailService;
import com.baitian.autotable.service.table.service.TableService;
import com.baitian.autotable.service.tf.service.TFService;
import com.baitian.autotable.service.type.ProjectType;
import com.baitian.autotable.service.type.TableType;
import com.baitian.autotable.util.exception.AutoTableInterruptException;
import com.baitian.autotable.webscoket.bean.Message;
import com.baitian.autotable.webscoket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author ldb
 * @Package com.baitian.autotable.service
 * @date 2020/11/11 16:47
 */
@Service
public class BackEndService {
	public static final int MAP_MAX_SIZE = 10;
	public static final String REGEX = ",";
	public static final String REGEX_2 = "#";
	@Value("${com.baitian.autotable.git.Location}")
	public String gitLocation;
	@Autowired
	private TableService tableService;
	@Autowired
	private TFService tfService;
	@Autowired
	private GitService gitService;
	@Autowired
	private MailService mailService;
	@Autowired
	private RelationRepository relationService;
	private static final TreeMap<Long, Message> TIME_RECORD = new TreeMap<>();
	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final ArrayBlockingQueue<Message> FAIR_QUEUE = new ArrayBlockingQueue<>(10, true);
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

	private synchronized Message autoTableConsumption(Message message) {
		try {
			message.setTableType(TableType.BACK_END.id);
			if (!message.assertParams("branch", "autoTableId", "name", "mail", "windy")) {
				message.putMsg("参数错误");
				message.putResult(CodeConfig.PARAM_ERROR);
				return message;
			}

			String branch = message.getMessage().getString("branch");
			List<Integer> autoTableIdList = message.getMessage().getIntList("autoTableId", ",");
			String name = message.getMessage().getString("name");
			String mail = message.getMessage().getString("mail");
			boolean windy = message.getMessage().getBool("windy");
			Map<Integer, Relation> back2Relation = relationService.findAll().stream()
					.collect(Collectors.toMap(Relation::getId, rl -> rl));
			int id = autoTableIdList.stream().filter(autoTableId -> !back2Relation.containsKey(autoTableId)).findAny()
					.orElse(0);
			if (id > 0) {
				message.putMsg("没有此id" + id);
				message.putResult(CodeConfig.NO_SUCH_ID_ERROR);
				return message;
			}
			checkout(branch, message);
			getAll(message);
			autoTableIdList.stream().map(back2Relation::get).forEach(rl -> table1(rl, message));
			long ts = System.currentTimeMillis();
			checkChange(message);
			String tablesName = autoTableIdList.stream().map(back2Relation::get).map(Relation::getDescription)
					.collect(Collectors.joining(","));
			addCommitPullPush(tablesName, message, name);
			if (windy) {
				mailService
						.send("【导表人】  " + name + "<br/>" + "【表名】  " + tablesName + "<br/>【分支】 " + branch + "<br/>"
								+ "【时间】 "
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

	TreeMap<Long, Object> getRecord() {
		TreeMap<Long, Object> kvTreeMap = new TreeMap<>();
		for (Map.Entry<Long, Message> longMessageEntry : TIME_RECORD.entrySet()) {
			kvTreeMap.put(longMessageEntry.getKey(), longMessageEntry.getValue().getMessage());
		}
		return kvTreeMap;
	}

	void checkout(String branch, Message message) {
		setMessageAndPushAll("尝试切换分支...", message);
		boolean result = gitService.checkout(branch, message, gitLocation);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	public void checkout(String branch, Message message, String gitLocation) {
		setMessageAndPushAll("尝试切换分支...", message);
		boolean result = gitService.checkout(branch, message, gitLocation);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	/**
	 * 前端用
	 */
	public void checkout(String branch, Message message, String gitLocation, ProjectType type) {
		setMessageAndPushAll(type + "尝试切换分支...", message);
		boolean result = gitService.checkout(branch, message, gitLocation);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	void getAll(Message message) {
		setMessageAndPushAll("开始拉取文件...", message);
		//导表工具更新,自带增量
		tfService.getAllTable();
		//拉取产品文件
		boolean result = tfService.getAll();
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	void table1(Relation relation, Message message) {
		BackEndService.setMessageAndPushAll("开始后端导表...", message);
		if (StringUtils.isEmpty(relation.getBackEndTable())) {
			BackEndService.setMessageAndPushAll("无后端导表...", message);
			return;
		}
		List<String> tables = Arrays.stream(relation.getBackEndTable().split(BackEndService.REGEX_2))
				.collect(Collectors.toList());
		boolean result = tableService.table1(tables, relation.getId());
		BackEndService.setMessageAndPushAll("结果:" + result, message);
	}

	void checkChange(Message message) {
		if (!gitService.hasChange(gitLocation)) {
			setMessageAndPushAll("没有变化...", message);
			throw new AutoTableInterruptException();
		}
	}

	void addCommitPullPush(String tableName, Message message, String name) {
		setMessageAndPushAll("开始提交新增...", message);
		boolean result = gitService.addCommitPullPush(tableName + " by " + name, gitLocation);
		setMessageAndPushAll("结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}

	public static void setMessageAndPushAll(String str, Message message) {
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
