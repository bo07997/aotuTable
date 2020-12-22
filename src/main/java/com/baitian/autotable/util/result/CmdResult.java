package com.baitian.autotable.util.result;

/**
 * @author ldb
 * @Package com.baitian.autotable.util.result
 * @date 2020/12/22 14:23
 */
public class CmdResult {
	private boolean success;
	private String errorMessage;

	public CmdResult(boolean success, String errorMessage) {
		this.success = success;
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String toMessage() {
		return success + (success ? "" : errorMessage);
	}
}
