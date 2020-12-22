package com.baitian.autotable.service;

import com.baitian.autotable.config.CodeConfig;
import com.baitian.autotable.db.dao.RelationRepository;
import com.baitian.autotable.entity.Relation;
import com.baitian.autotable.service.git.service.GitService;
import com.baitian.autotable.service.table.service.TableService;
import com.baitian.autotable.service.tf.service.TFService;
import com.baitian.autotable.service.type.FrontType;
import com.baitian.autotable.service.type.ProjectType;
import com.baitian.autotable.service.type.TableType;
import com.baitian.autotable.util.exception.AutoTableInterruptException;
import com.baitian.autotable.util.result.CmdResult;
import com.baitian.autotable.webscoket.bean.Message;
import com.baitian.autotable.webscoket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author ldb
 * @Package com.baitian.autotable.service
 * @date 2020/11/11 10:33
 */
@Service
public class FrontEndService {
	@Autowired
	private RelationRepository relationService;
	@Autowired
	private TableService tableService;
	@Autowired
	private TFService tfService;
	@Autowired
	private BackEndService backEndService;
	@Autowired
	private GitService gitService;
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
			if (!message.assertParams("autoTableId", "name")) {
				message.putMsg("参数错误");
				message.putResult(CodeConfig.PARAM_ERROR);
				return message;
			}
			message.setTableType(TableType.FRONT_END.id);
			List<Integer> autoTableIdList = message.getMessage().getIntList("autoTableId", ",");
			String name = message.getMessage().getString("name");
			Map<Integer, Relation> back2Relation = relationService.findAll().stream()
					.collect(Collectors.toMap(Relation::getId, rl -> rl));
			int id = autoTableIdList.stream().filter(autoTableId -> !back2Relation.containsKey(autoTableId)).findAny()
					.orElse(0);
			if (id > 0) {
				message.putMsg("没有此id" + id);
				message.putResult(CodeConfig.NO_SUCH_ID_ERROR);
				return message;
			}
			Arrays.stream(ProjectType.values()).forEach(type -> type.checkout(backEndService, message));
			backEndService.getAll(message);
			String tablesName = autoTableIdList.stream().map(back2Relation::get).map(Relation::getDescription)
					.collect(Collectors.joining(","));
			autoTableIdList.stream().map(back2Relation::get).forEach(rl -> table3(rl, message));
			Arrays.stream(ProjectType.values())
					.forEach(type -> type.checkChangeAndPush(gitService, tablesName, message, name));
		} catch (Exception e) {
			message.putResult(CodeConfig.ERROR);
			message.putMsg(e.getMessage());
			return message;
		}
		message.putResult(CodeConfig.END);
		return message;
	}

	void table3(Relation relation, Message message) {
		BackEndService.setMessageAndPushAll("开始前端导表...", message);
		if (StringUtils.isEmpty(relation.getFrontEndTable())) {
			BackEndService.setMessageAndPushAll("无前端导表...", message);
			return;
		}
		String[] tables = relation.getFrontEndTable().split(BackEndService.REGEX_2);
		String[] types = relation.getFrontEndType().split(BackEndService.REGEX_2);
		for (int i = 0; i < tables.length; i++) {
			FrontType frontType = FrontType.parse(Integer.parseInt(types.length > i ? types[i] : types[0]));
			CmdResult result = tableService.table3(frontType.getCmd(tables[i]), frontType.location);
			//SUCCESS_0 不确定状态 因为cmd范围的状态码只是正常退出的
			BackEndService.setMessageAndPushAll(CodeConfig.SUCCESS_0, "结果:" + result.toMessage(), message);
			if (!result.isSuccess()) {
				throw new AutoTableInterruptException();
			}
		}
	}

}
