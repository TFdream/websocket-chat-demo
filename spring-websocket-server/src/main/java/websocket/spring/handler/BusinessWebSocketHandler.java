package websocket.spring.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import websocket.commons.entity.UserInfoDTO;
import websocket.spring.session.UserSessionManager;

/**
 * @author Ricky Fung
 */
public class BusinessWebSocketHandler extends AbstractWebSocketHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private UserSessionManager sessionManager = UserSessionManager.getMgr();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        //获取用户身份信息
        UserInfoDTO user = sessionManager.getUserInfo(session);
        Long userId = user.getId();

        String payload = message.getPayload();
        LOG.info("在线聊天系统-处理WebSocket请求开始, userId:{} 请求报文:{}", userId, payload);

        try {
            //处理请求
            processRequest(user, payload, session);

        } catch (Exception e) {
            String msg = String.format("在线聊天系统-处理WebSocket请求-业务处理异常, userId:%s", userId);
            LOG.error(msg, e);
        }
    }

    private void processRequest(UserInfoDTO user, String payload, WebSocketSession session) {

    }

    //----------

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = sessionManager.getUserId(session);
        LOG.info("在线聊天系统-会话管理-连接成功, userId:{}", userId);

        //保存会话
        sessionManager.addSession(userId, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        Long userId = sessionManager.getUserId(session);
        LOG.info("在线聊天系统-会话管理-传输数据出错, userId:{}", userId);
        if (session.isOpen()) {
            session.close();
        }
        //清除session
        sessionManager.addSession(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Long userId = sessionManager.getUserId(session);
        LOG.info("在线聊天系统-会话管理-连接关闭, userId:{}, status:{}", userId, closeStatus);
        //清除session
        sessionManager.removeSession(userId);
    }

}
