package com.baitian.autotable.entity;

import javax.persistence.*;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private int id;

	private String backEndTable;

	private String frontEndTable;

	private String frontEndType;
	private String frontEndParam;
	private String description;

	public Relation() {
	}

	public Relation(int id, String backEndTable, String frontEndTable, String frontEndType, String frontEndParam,
			String description) {
		this.id = id;
		this.backEndTable = backEndTable;
		this.frontEndTable = frontEndTable;
		this.frontEndType = frontEndType;
		this.frontEndParam = frontEndParam;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
