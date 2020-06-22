package com.baitian.autotable;

import com.baitian.autotable.service.git.service.GitService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.PushResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author ldb
 * @Package com.baitian.autotable
 * @date 2020/6/17 17:28
 */
public class TestTF {
	//	public static void main(String[] args) {
	//		//		CmdUtil.executeCommand(
	//		//				"ping localhost",
	//		//				"C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\Common7\\IDE");
	//		//		boolean testt = CmdUtil.executeCommand(
	//		//				"C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\Common7\\IDE\\TF.exe get E:\\vstsworkspace\\projectX美术产品\\产品\\项目管理\\每周修改表\\pet");
	//		System.out.println();
	//	}

	@Autowired
	private GitService gitService;

	@Test
	public void testGit() {
		System.out.println(hasChange());
	}

	public boolean hasChange() {
		File repoGitDir = new File("E:\\vstsworkspace\\projectXS\\.git");
		Repository repo = null;
		try {
			repo = new FileRepository(repoGitDir.getAbsolutePath());
			Git git = new Git(repo);
			//			Ref ref = git.reset().setMode(ResetCommand.ResetType.HARD).call();
			//			git.reset().setMode(ResetCommand.ResetType.HARD).setRef(ref.getName());
			git.add().addFilepattern("source/server/config/commonEvolve.xml").call();
			//			git.commit().setMessage("test").call();
			//			git.commit().setMessage("test").call();
			Iterable<PushResult> test = git.push().call();
			System.out.println();
		} catch (Exception e) {
		} finally {
			if (repo != null) {
				repo.close();
			}
		}
		return false;
	}
}
