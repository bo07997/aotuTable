package com.baitian.autotable.db.dao;

import com.baitian.autotable.entity.Relation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ldb
 * @Package com.baitian.autotable.db.service
 * @date 2020/11/11 19:38
 */
public interface RelationRepository extends JpaRepository<Relation, Long> {
	Relation findByBackEndTable(String backEndTable);
}
