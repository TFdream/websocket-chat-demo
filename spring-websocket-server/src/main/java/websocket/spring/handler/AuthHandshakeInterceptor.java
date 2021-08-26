package websocket.spring.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import websocket.commons.Constants;
import websocket.commons.entity.UserInfoDTO;
import websocket.commons.manager.UserAuthManager;
import websocket.commons.util.StringUtils;

import java.util.Map;

/**
 * 用户身份认证
 * @author Ricky Fung
 */
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        LOG.info("在线聊天系统-握手前-用户身份认证开始");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest req = (ServletServerHttpRequest) request;
            //获取token认证
            String token = req.getServletRequest().getHeader(Constants.AUTHORIZATION_HEADER);
            if (StringUtils.isEmpty(token)) {
                //http://localhost:8080/chat?token=access_token
                token = req.getServletRequest().getParameter(Constants.TOKEN_PARAMETER);
            }
            LOG.info("在线聊天系统-握手前-用户身份认证, token:{}", token);
            if (StringUtils.isEmpty(token)) {
                LOG.info("在线聊天系统-握手前-用户身份认证, 用户未登录token为空", token);
                return false;
            }
            try {
                UserInfoDTO userInfoDTO = UserAuthManager.getInstance().validateToken(token);
                //保存认证用户
                attributes.put(Constants.ATTRIBUTES_USER, userInfoDTO);
                attributes.put(Constants.ATTRIBUTES_TOKEN, token);

                LOG.info("在线聊天系统-握手前-用户身份认证成功, 用户登录成功:{}, token:{}", userInfoDTO.getId(), token);

                return true;
            } catch (Exception e) {
                LOG.error(String.format("在线聊天系统-握手前-用户身份认证异常, token:%s", token), e);
            }
        }
        LOG.info("在线聊天系统-握手前结束");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        LOG.info("在线聊天系统-握手后结束, wsHandler:{}", wsHandler);
    }
}
