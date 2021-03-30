package websocket.springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import websocket.springboot.entity.UserInfo;
import websocket.springboot.manager.SessionManager;

/**
 * @author Ricky Fung
 */
@Service
public class DispatcherService {

    /**
     * 处理业务消息
     * @param session
     * @param user
     * @param payload
     */
    public void dispatch(WebSocketSession session, UserInfo user, String payload) {
        SessionManager.getMgr().sendMessage(session, "");
    }
}
