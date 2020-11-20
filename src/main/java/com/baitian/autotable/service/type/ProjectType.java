package com.baitian.autotable.service.type;

import com.baitian.autotable.service.BackEndService;
import com.baitian.autotable.service.git.service.GitService;
import com.baitian.autotable.util.exception.AutoTableInterruptException;
import com.baitian.autotable.webscoket.bean.Message;

/**
 * @author ldb
 * @Package com.baitian.autotable.service.type
 * @date 2020/11/20 16:04
 */
public enum ProjectType {
	/**
	 * flash
	 */
	FLASH("E:\\vstsworkspace\\projectX\\source\\as\\.git", "develop"),
	H5("D:\\projectX-H5\\.git", "master");
	String gitLocation;
	String branchName;

	ProjectType(String gitLocation, String branchName) {
		this.gitLocation = gitLocation;
		this.branchName = branchName;
	}

	public void checkout(BackEndService backEndService, Message message) {
		backEndService.checkout(branchName, message, gitLocation, this);
	}

	public boolean checkChangeAndPush(GitService gitService, String tables, Message message, String name) {
		boolean change = false;
		if (!gitService.hasChange(gitLocation)) {
			BackEndService.setMessageAndPushAll(this + "没有变化...", message);
		} else {
			change = true;
			addCommitPullPush(gitService, tables, message, name);
		}
		return change;
	}

	public void addCommitPullPush(GitService gitService, String tables, Message message, String name) {
		BackEndService.setMessageAndPushAll(this + "开始提交新增...", message);
		boolean result = gitService.addCommitPullPush(tables + " by " + name, gitLocation);
		BackEndService.setMessageAndPushAll(this + "结果:" + result, message);
		if (!result) {
			throw new AutoTableInterruptException();
		}
	}
}

