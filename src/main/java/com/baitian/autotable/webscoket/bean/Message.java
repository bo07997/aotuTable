package com.baitian.autotable.webscoket.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * @author liudianbo
 */
public class Message {
	private String sessionId;
	private MessageMap<String, Object> message = new MessageMap<>();
	private String service;
	private String method;
	private int TableType;
	private String log;

	public Message copyMessage() {
		Message newMessage = new Message();
		newMessage.setSessionId(sessionId);
		newMessage.setService(service);
		newMessage.setMethod(method);
		newMessage.setLog(log);
		message.forEach(newMessage::addMessage);
		return newMessage;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public int getTableType() {
		return TableType;
	}

	public void setTableType(int tableType) {
		TableType = tableType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public MessageMap<String, Object> getMessage() {
		return message;
	}

	public void setMessage(MessageMap<String, Object> message) {
		this.message = message;
	}

	public void addMessage(String key, Object value) {
		this.message.put(key, value);
	}

	public void putResult(String result) {
		this.message.put("r", result);
	}

	public void putMsg(String msg) {
		this.message.put("msg", msg);
	}

	public void clearMsg() {
		this.message.remove("msg");
	}

	public void putValue(String key, Object result) {
		this.message.put(key, result);
	}

	public boolean assertParams(String... keys) {
		return Arrays.stream(keys).allMatch(key -> this.message.containsKey(key));
	}

}
