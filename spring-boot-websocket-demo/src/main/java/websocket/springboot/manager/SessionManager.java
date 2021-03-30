package websocket.springboot.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocket.springboot.constants.SBConstants;
import websocket.springboot.entity.UserInfo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ricky Fung
 */
public class SessionManager {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentHashMap<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>(1024 * 4);

    //统计在线人数
    private final AtomicInteger counter = new AtomicInteger();

    private SessionManager() {}

    //======== 会话管理
    public WebSocketSession getSession(Long userId) {
        return sessionMap.get(userId);
    }

    public int addSession(Long userId, WebSocketSession session) {
        sessionMap.put(userId, session);
        return counter.incrementAndGet();
    }

    public int removeSession(Long userId) {
        sessionMap.remove(userId);
        return counter.decrementAndGet();
    }

    public void closeSession(WebSocketSession session) {
        try {
            session.close();
        } catch (IOException e) {
            LOG.error(String.format("客服呼叫中心-关闭连接异常, userId:%s",
                    getUserId(session)), e);
        }
    }

    public int getOnlineCount() {
        return counter.get();
    }

    //=========
    public boolean sendMessage(Long userId, String msg) {
        if (userId == null || userId < 1L) { //广播消息
            for (Map.Entry<Long, WebSocketSession> me : sessionMap.entrySet()) {
                sendMessage(me.getValue(), msg);
            }
            return true;
        } else {
            WebSocketSession session = getSession(userId);
            return sendMessage(session, msg);
        }
    }

    public boolean sendMessage(WebSocketSession session, String msg) {
        if (session==null) {
            LOG.warn("WebSocket示例-响应数据发送, 会话为NULL");
            return false;
        }
        Long userId = getUserId(session);
        if (!session.isOpen()) {
            LOG.warn("WebSocket示例-响应数据发送, userId:{} 会话已关闭", userId);
            return false;
        }

        try {
            session.sendMessage(new TextMessage(msg));
            return true;
        } catch (IOException e) {
            LOG.error(String.format("WebSocket示例-响应数据发送异常, userId:%s",
                    getUserId(session)), e);
        }
        return false;
    }

    //==========
    public Long getUserId(WebSocketSession session) {
        UserInfo principal = getUserInfo(session);
        return principal!=null ? principal.getId() : null;
    }

    public UserInfo getUserInfo(WebSocketSession session) {
        Object obj = session.getAttributes().get(SBConstants.ATTRIBUTES_USER);
        if (obj==null) {
            return null;
        }
        return (UserInfo) obj;
    }

    //========= 单例模式
    public static SessionManager getMgr() {
        return SingletonHolder.INSTANCE;
    }

    static class SingletonHolder {
        static final SessionManager INSTANCE = new SessionManager();
    }
}
