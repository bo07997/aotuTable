package com.baitian.autotable;

import com.baitian.autotable.service.git.service.GitService;
import com.baitian.autotable.service.tf.service.TFService;
import com.baitian.autotable.util.CmdUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Set;

/**
 * @author ldb
 * @Package com.baitian.autotable
 * @date 2020/6/17 17:28
 */
public class TestTF {
	public static void main(String[] args) {
		CmdUtil.executeCommandOut(
				"java -jar E:\\vstsworkspace\\projectX\\source\\tools\\excelFactory\\excelFactory.jar -cmd E:\\vstsworkspace\\projectX\\source\\tools\\excelFactory\\monsterteam2.xml");
		System.out.println("ss");
	}

	@Autowired
	private GitService gitService;
	@Autowired
	private TFService tfService;
	@Test
	public void testGit() {
		CmdUtil.executeCommandOut(
				"tf get /recursive "
						+ "E:\\vstsworkspace\\projectX\\source\\tools\\excelFactory");
		System.out.println("ss");
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
			Status status = git.status().call();
			Set<String> test = status.getUncommittedChanges();
			//			Iterable<PushResult> test = git.push().call();
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
