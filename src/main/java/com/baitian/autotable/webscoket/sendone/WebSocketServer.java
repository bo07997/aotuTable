package com.baitian.autotable.webscoket.sendone;

import com.baitian.autotable.util.BeanUtils;
import com.baitian.autotable.util.GsonUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ServerEndpoint("/ws")
@Component
@Slf4j
public class WebSocketServer {

	/**
	 * 用于存放所有在线客户端
	 */
	private static Map<String, Session> clients = new ConcurrentHashMap<>();
	private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	private Gson gson = new Gson();

	@OnOpen
	public void onOpen(Session session) {
		//        log.info("有新的客户端上线: {}", session.getId());
		clients.put(session.getId(), session);
	}

	@OnClose
	public void onClose(Session session) {
		String sessionId = session.getId();
		//        log.info("有客户端离线: {}", sessionId);
		clients.remove(sessionId);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		String sessionId = session.getId();
		if (clients.get(sessionId) != null) {
			clients.remove(sessionId);
		}
		throwable.printStackTrace();
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		Message messageObject = gson.fromJson(message, Message.class);
		messageObject.setSessionId(session.getId());
		Message result = null;
		try {
			Object service = BeanUtils.getBean(messageObject.getService());
			Method method = null;
			try {
				method = service.getClass().getMethod(messageObject.getMethod(), Message.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				messageObject.putResult("no such method!");
				sendTo(messageObject);
			}
			try {
				result = (Message) method.invoke(service, messageObject);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			assert result != null;
			sendTo(result);
		} catch (Exception e) {
			messageObject.putResult(e.getMessage());
			sendTo(messageObject);
		}
	}

	/**
	 * 发送消息
	 *
	 * @param message 消息对象
	 */
	public static void sendTo(Message message) {
		Session s = clients.get(message.getSessionId());
		String result = GsonUtil.toJson(message);
		if (s != null) {
			try {
				s.getBasicRemote().sendText(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 群发消息
	 *
	 * @param message 消息内容
	 */
	//	public synchronized static void sendAll(Message message) throws IOException {
	//		String result = message.toString();
	//		for (Map.Entry<String, Session> sessionEntry : clients.entrySet()) {
	//			sessionEntry.getValue().getBasicRemote().sendText(result);
	//		}
	//	}
	public static void sendAll(Message message) {
		String result = GsonUtil.toJson(message);
		for (Map.Entry<String, Session> sessionEntry : clients.entrySet()) {
			cachedThreadPool.execute(() -> {
				synchronized (sessionEntry.getValue()) {
					try {
						sessionEntry.getValue().getBasicRemote().sendText(result);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
