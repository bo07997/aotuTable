package com.baitian.autotable.service.type;

/**
 * @author ldb
 * @Package com.baitian.autotable.service
 * @date 2020/11/11 17:17
 */
public enum TableType {
	/**
	 * 前端
	 */
	FRONT_END(1),
	/**
	 * 后端
	 */
	BACK_END(2);
	public int id;

	TableType(int id) {
		this.id = id;
	}
}


