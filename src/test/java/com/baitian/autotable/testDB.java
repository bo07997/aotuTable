package com.baitian.autotable;

import com.baitian.autotable.db.dao.RelationRepository;
import com.baitian.autotable.service.git.service.GitService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author ldb
 * @Package com.baitian.autotable
 * @date 2020/11/11 19:43
 */
public class testDB {
	@Autowired
	private RelationRepository relationRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private GitService gitService;

	@Test
	public void testGit() {
		//		Relation tt = relationRepository.findByBackEndTable("test");
		String[] test = "".split("#");
	}
}
