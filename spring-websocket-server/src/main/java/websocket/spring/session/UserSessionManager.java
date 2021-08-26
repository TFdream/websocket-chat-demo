package websocket.spring.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocket.commons.Constants;
import websocket.commons.entity.UserInfoDTO;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ricky Fung
 */
public class UserSessionManager {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentHashMap<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>(1024 * 2);

    private UserSessionManager() {}

    public WebSocketSession getSession(Long userId) {
        return sessionMap.get(userId);
    }

    public void addSession(Long userId, WebSocketSession session) {
        sessionMap.put(userId, session);
    }

    public void removeSession(Long userId) {
        sessionMap.remove(userId);
    }

    public void closeSession(WebSocketSession session) {
        try {
            session.close();
        } catch (IOException e) {
            LOG.error(String.format("客服呼叫中心-关闭连接异常, userId:%s",
                    getUserId(session)), e);
        }
    }
    //--------

    public boolean sendMessage(Long userId, String response) {
        WebSocketSession session = getSession(userId);
        if (session != null) {
            return sendMessage(session, response);
        }
        return false;
    }

    /**
     * 发送数据
     * @param session
     * @param response
     * @return
     */
    public boolean sendMessage(WebSocketSession session, String response) {
        if (session==null) {
            LOG.warn("客服呼叫中心-响应数据发送, 会话为NULL");
            return false;
        }
        Long userId = getUserId(session);
        if (!session.isOpen()) {
            LOG.warn("客服呼叫中心-响应数据发送, userId:{} 会话已关闭", userId);
            return false;
        }

        try {
            session.sendMessage(new TextMessage(response));
            return true;
        } catch (IOException e) {
            LOG.error(String.format("客服呼叫中心-响应数据发送异常, userId:%s",
                    getUserId(session)), e);
        }
        return false;
    }

    //--------

    public Long getUserId(WebSocketSession session) {
        UserInfoDTO principal = getUserInfo(session);
        return principal!=null ? principal.getId() : null;
    }

    public UserInfoDTO getUserInfo(WebSocketSession session) {
        Object obj = session.getAttributes().get(Constants.ATTRIBUTES_USER);
        if (obj==null) {
            return null;
        }
        return (UserInfoDTO) obj;
    }

    //--------
    public static UserSessionManager getMgr() {
        return SingletonHolder.INSTANCE;
    }

    static class SingletonHolder {
        static final UserSessionManager INSTANCE = new UserSessionManager();
    }
}

