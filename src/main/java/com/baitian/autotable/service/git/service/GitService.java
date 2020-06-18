package com.baitian.autotable.service.git.service;

import com.baitian.autotable.service.ClientService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author ldb
 * @date 2020/5/28 19:52
 */
@Service
public class GitService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

	@Value("${com.baitian.autotable.git.Location}")
	public String gitLocation;

	@Value("${com.baitian.autotable.git.workLocation}")
	public String workLocation;

	public boolean addCommitPullPush(String branchName, String info) {
		File repoGitDir = new File(gitLocation);
		Repository repo = null;
		try {
			repo = new FileRepository(repoGitDir.getAbsolutePath());
			Git git = new Git(repo);
			add(git, info).commit(git, info).pull(git).push(git);
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			return false;
		} finally {
			if (repo != null) {
				repo.close();
			}
		}
		return true;
	}

	/**
	 * 状态
	 */
	public void status() {
		File repoGitDir = new File(gitLocation);
		Repository repo = null;
		try {
			repo = new FileRepository(repoGitDir.getAbsolutePath());
			Git git = new Git(repo);
			Status status = git.status().call();
			LOGGER.info("Git Change: " + status.getChanged());
			LOGGER.info("Git Modified: " + status.getModified());
			LOGGER.info("Git UncommittedChanges: " + status.getUncommittedChanges());
			LOGGER.info("Git Untracked: " + status.getUntracked());
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		} finally {
			if (repo != null) {
				repo.close();
			}
		}
	}

	public boolean hasChange(){
		File repoGitDir = new File(gitLocation);
		Repository repo = null;
		try {
			repo = new FileRepository(repoGitDir.getAbsolutePath());
			Git git = new Git(repo);
			Status status = git.status().call();
			return status.hasUncommittedChanges();
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		} finally {
			if (repo != null) {
				repo.close();
			}
		}
		return false;
	}

	/**
	 * 外部切换分支,并且尝试重置
	 */
	public boolean checkout(String branchName) {
		File repoGitDir = new File(gitLocation);
		Repository repo = null;
		try {
			repo = new FileRepository(repoGitDir.getAbsolutePath());
			Git git = new Git(repo);
			if (hasChange(git)) {
				git.reset().setMode(ResetCommand.ResetType.HARD)
						.setRef(git.fetch().getRemote() + "/" + repo.getBranch())
						.call();
			}
			git.checkout().setCreateBranch(false).setName(branchName).call();
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			return false;
		} finally {
			if (repo != null) {
				repo.close();
			}
		}
		return true;
	}

	/**
	 * 变化
	 */
	public boolean hasChange(Git git) throws GitAPIException {
		Status status = git.status().call();
		return status.hasUncommittedChanges();
	}

	/**
	 * 切换分支
	 */
	public GitService checkout(Git git, String branchName) throws GitAPIException {
		git.checkout().setCreateBranch(false).setName(branchName).call();
		return this;
	}

	/**
	 * 添加到工作区,这里有bug用jGit,老是提交一些奇怪的修改,所以限制严格点
	 */
	public GitService add(Git git, String info) throws GitAPIException {
		for (String table : info.split(ClientService.REGEX)) {
			git.add().addFilepattern(workLocation + table + "*").call();
		}
		return this;
	}

	/**
	 * 提交
	 */
	public GitService commit(Git git, String info) throws GitAPIException {
		git.commit().setMessage("MOD <自动导表> " + info).call();
		return this;
	}

	/**
	 * 拉取最新
	 */
	public GitService pull(Git git) throws GitAPIException {
		git.pull().call();
		return this;
	}

	/**
	 * 推到远程
	 */
	public GitService push(Git git) throws GitAPIException {
		git.push().call();
		return this;
	}

}
