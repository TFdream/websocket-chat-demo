package websocket.springboot.handler.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import websocket.springboot.constants.SBConstants;
import websocket.springboot.entity.UserInfo;
import websocket.springboot.manager.TokenManager;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 用户身份认证
 * @author Ricky Fung
 */
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Resource
    private TokenManager tokenManager;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        LOG.info("WebSocket示例-握手前-用户身份认证开始");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest req = (ServletServerHttpRequest) request;
            //1.优先从Header中获取token
            String token = req.getServletRequest().getHeader(SBConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isEmpty(token)) {
                //http://localhost:8080/endpoint?token=access_token
                token = req.getServletRequest().getParameter(SBConstants.TOKEN_PARAMETER);
            }
            LOG.info("WebSocket示例-握手前-用户身份认证, token:{}", token);
            if (StringUtils.isEmpty(token)) {
                LOG.info("WebSocket示例-握手前-用户身份认证, 用户未登录token为空", token);
                return false;
            }

            try {
                UserInfo userInfo = tokenManager.validateToken(token);
                //保存认证用户
                attributes.put(SBConstants.ATTRIBUTES_USER, userInfo);
                attributes.put(SBConstants.ATTRIBUTES_TOKEN, token);

                LOG.info("WebSocket示例-握手前-用户身份认证成功, 用户登录成功:{}, token:{}", userInfo.getId(), token);

                return true;
            } catch (Exception e) {
                LOG.error(String.format("WebSocket示例-握手前-用户身份认证异常, token:%s", token), e);
            }
        }
        LOG.info("WebSocket示例-握手前结束, 未知类型");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        LOG.info("WebSocket示例-握手后结束, wsHandler:{}", wsHandler);
    }
}
