package com.baitian.autotable.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author ldb
 * @Package com.baitian.autotable.entity
 * @date 2020/11/11 18:27
 */
@Entity
@Table(name = "AutoTableRelation")
public class Relation implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(nullable = false)
	private int id;
	@Column(nullable = false)
	private String backEndTable;
	@Column(nullable = false)
	private String frontEndTable;
	@Column(nullable = false)
	private String frontEndType;
	private String frontEndParam;
	private String desc;

	public Relation() {
	}

	public Relation(int id, String backEndTable, String frontEndTable, String frontEndType, String frontEndParam,
			String desc) {
		this.id = id;
		this.backEndTable = backEndTable;
		this.frontEndTable = frontEndTable;
		this.frontEndType = frontEndType;
		this.frontEndParam = frontEndParam;
		this.desc = desc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBackEndTable() {
		return backEndTable;
	}

	public void setBackEndTable(String backEndTable) {
		this.backEndTable = backEndTable;
	}

	public String getFrontEndTable() {
		return frontEndTable;
	}

	public void setFrontEndTable(String frontEndTable) {
		this.frontEndTable = frontEndTable;
	}

	public String getFrontEndType() {
		return frontEndType;
	}

	public void setFrontEndType(String frontEndType) {
		this.frontEndType = frontEndType;
	}

	public String getFrontEndParam() {
		return frontEndParam;
	}

	public void setFrontEndParam(String frontEndParam) {
		this.frontEndParam = frontEndParam;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
