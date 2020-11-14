package com.baitian.autotable.controller;

import com.baitian.autotable.db.dao.RelationRepository;
import com.baitian.autotable.entity.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 维护前后端导表的关系
 */
@RestController
@RequestMapping(value = "/relation")
public class RelationController {
	@Autowired
	private RelationRepository relationService;


	/**
	 * 客户端发消息，服务端接收
	 *
	 */
	@GetMapping("/getRelation")
	public List<Relation> getRelation() {
		return relationService.findAll();
	}

	@PostMapping(value = "/saveRelation")
	public List<Relation> insertRelation(@RequestBody List<Relation> relation) {
		return relationService.saveAll(relation);
	}
}
