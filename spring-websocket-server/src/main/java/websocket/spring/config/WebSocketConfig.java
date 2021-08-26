package websocket.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import websocket.spring.handler.AuthHandshakeInterceptor;
import websocket.spring.handler.BusinessWebSocketHandler;

/**
 * 参考资料：https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/websocket.html
 * @author Ricky Fung
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final int MAX_MESSAGE_BUFFER_SIZE = 1024 * 32;

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(MAX_MESSAGE_BUFFER_SIZE);
        container.setMaxBinaryMessageBufferSize(MAX_MESSAGE_BUFFER_SIZE);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(dispatcherHandler(), "/chat")
                .addInterceptors(authHandshakeInterceptor())
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler dispatcherHandler() {
        return new BusinessWebSocketHandler();
    }

    @Bean
    public HandshakeInterceptor authHandshakeInterceptor() {
        return new AuthHandshakeInterceptor();
    }
}
