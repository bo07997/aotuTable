package com.baitian.autotable.service;

import com.baitian.autotable.service.common.service.CommonService;
import com.baitian.autotable.service.git.GitService;
import com.baitian.autotable.service.table.service.TableService;
import com.baitian.autotable.service.tf.service.TFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author ldb
 * @Package com.baitian.autotable.service
 * @date 2020/5/29 15:25
 */
@Service
public class ClientService {

	@Autowired
	private TableService tableService;
	@Autowired
	private TFService tfService;
	@Autowired
	private GitService gitService;
	@Autowired
	private CommonService commonService;

	public List<String> getXML() {
		return commonService.selectAll();
	}

	public String autoTable1(String branch, String name) {
		StringBuilder buffer = new StringBuilder();
		try {
			checkout(branch, buffer).getAll(buffer).table1(name, buffer).addCommitPullPush(branch, name, buffer);
		} catch (Exception e) {

		}
		return buffer.toString();
	}

	public String autoTable2(String branch, String name) {
		StringBuilder buffer = new StringBuilder();
		try {
			checkout(branch, buffer).getAll(buffer).table2(name, buffer).addCommitPullPush(branch, name, buffer);
		} catch (Exception e) {

		}
		return buffer.toString();
	}

	ClientService checkout(String branch, StringBuilder buffer) {
		buffer.append("尝试切换分支...").append("<br />");
		Boolean result = gitService.checkout(branch);
		buffer.append("结果:").append(result).append("<br />");
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService getAll(StringBuilder buffer) {
		buffer.append("开始拉取文件...").append("<br />");
		Boolean result = tfService.getAll();
		buffer.append("结果:").append(result).append("<br />");
		if (!result) {
			return null;
		}
		return this;
	}

	ClientService table1(String tableName, StringBuilder buffer) {
		buffer.append("开始导表...").append("<br />");
		Boolean result = tableService.table1(Collections.singletonList(tableName));
		buffer.append("结果:").append(result).append("<br />");
		if (!result) {
			return null;
		}
		if (!gitService.hasChange()){
			buffer.append("没有变化...").append("<br />");
			return null;
		}
		return this;
	}

	ClientService table2(String tableName, StringBuilder buffer) {
		buffer.append("开始导表...").append("<br />");
		Boolean result = tableService.table2(tableName);
		buffer.append("结果:").append(result).append("<br />");
		if (!result) {
			return null;
		}
		if (!gitService.hasChange()){
			buffer.append("没有变化").append("<br />");
			return null;
		}
		return this;
	}

	ClientService addCommitPullPush(String branch, String tableName, StringBuilder buffer) {
		buffer.append("开始提交新增...").append("<br />");
		Boolean result = gitService.addCommitPullPush(branch, tableName);
		buffer.append("结果:").append(result).append("<br />");
		if (!result) {
			return null;
		}
		return this;
	}

}
