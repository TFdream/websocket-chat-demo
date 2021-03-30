package websocket.springboot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import websocket.springboot.manager.SessionManager;
import websocket.springboot.entity.UserInfo;
import websocket.springboot.service.DispatcherService;

import javax.annotation.Resource;

/**
 * 业务逻辑处理器
 * @author Ricky Fung
 */
public class BizProcessHandler extends AbstractWebSocketHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private SessionManager sessionManager = SessionManager.getMgr();

    @Resource
    private DispatcherService dispatcherService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        // 处理业务逻辑
        UserInfo user = sessionManager.getUserInfo(session);
        Long userId = user.getId();

        String payload = message.getPayload();
        LOG.info("WebSocket示例-处理WebSocket请求开始, userId:{} 请求报文:{}", userId, payload);

        try {
            //转发请求给业务端
            dispatcherService.dispatch(session, user, payload);
        } catch (Exception e) {
            String msg = String.format("WebSocket示例-处理WebSocket请求-业务处理异常, userId:%s", userId);
            LOG.error(msg, e);
        }
    }

    //----------

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = sessionManager.getUserId(session);
        LOG.info("WebSocket示例-会话管理-连接成功, userId:{}", userId);
        //保存会话
        sessionManager.addSession(userId, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        Long userId = sessionManager.getUserId(session);
        LOG.error(String.format("WebSocket示例-会话管理-传输数据出错, userId:%s", userId), throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Long userId = sessionManager.getUserId(session);
        LOG.info("WebSocket示例-会话管理-连接关闭, userId:{}, status:{}", userId, closeStatus);
        //清除session
        sessionManager.removeSession(userId);
    }

}
