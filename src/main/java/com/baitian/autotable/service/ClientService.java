package com.baitian.autotable.service;

import com.baitian.autotable.config.CodeConfig;
import com.baitian.autotable.db.dao.RelationRepository;
import com.baitian.autotable.service.common.service.CommonService;
import com.baitian.autotable.service.table.service.TableService;
import com.baitian.autotable.webscoket.bean.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ldb
 * @Package com.baitian.autotable.service
 * @date 2020/5/29 15:25
 * clientService bean里面的key是首字母小写
 */
@Service
public class ClientService {

	@Autowired
	private BackEndService backEndService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private FrontEndService frontEndService;
	@Autowired
	private RelationRepository relationService;
	@Autowired
	private TableService tableService;
	/**
	 * @param message message
	 * @return Message
	 */
	public Message getXML(Message message) {
		message.putResult(CodeConfig.SUCCESS);
		message.putValue("record", backEndService.getRecord());
		message.putValue("relation", relationService.findAll().stream()
				.sorted(Comparator.comparing(rl -> -tableService.getCount(rl.getId()))).collect(
						Collectors.toList()));
		return message;
	}

	public Message autoTable(Message message) {
		try {
			BackEndService.FAIR_QUEUE.offer(message, 10, TimeUnit.SECONDS);
			FrontEndService.FAIR_QUEUE.offer(message.copyMessage(), 10, TimeUnit.SECONDS);
			message.putResult(CodeConfig.SUCCESS);
			message.putMsg(String.format("放入任务队列成功,当前待完成任务数目:%d", BackEndService.FAIR_QUEUE.size()));
		} catch (InterruptedException e) {
			e.printStackTrace();
			message.putResult(CodeConfig.ERROR);
			message.putMsg("放入任务队列超时(队列已满)");
		}
		return message;
	}

}
