package websocket.springboot.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * 全局跨域
 * 解决：https://crm.xxx.com 和 https://crm-ws.xxx.com
 */
@Configuration
public class HttpCorsConfig {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private static final String ALL = "*";
    private static final String ALL_PATH = "/**";

    @Value("${cors.path:}")
    private String path;

    //多个值 逗号分隔
    @Value("${cors.allowedOrigins:}")
    private List<String> allowedOrigins;

    @Value("${cors.allowedMethods:}")
    private List<String> allowedMethods;

    @Value("${cors.exposedHeaders:}")
    private List<String> exposedHeaders;

    @Bean
    public CorsFilter corsFilter() {
        LOG.info("WebSocket示例-HTTP跨域, path:{}, allowedOrigins:{}", allowedOrigins);

        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //放行哪些原始域
        if (CollectionUtils.isEmpty(allowedOrigins)) {
            config.addAllowedOrigin(ALL);
            LOG.info("WebSocket示例-HTTP跨域, 允许全部原始域");
        } else {
            config.setAllowedOrigins(allowedOrigins);
            LOG.info("WebSocket示例-HTTP跨域, 增加允许原始域:{}", allowedOrigins);
        }
        //是否发送Cookie信息
        config.setAllowCredentials(true);
        //放行哪些原始域(请求方式)
        if (CollectionUtils.isEmpty(allowedMethods)) {
            config.addAllowedMethod(ALL);
        } else {
            config.setAllowedMethods(allowedMethods);
            LOG.info("WebSocket示例-HTTP跨域, 增加允许方法:{}", allowedMethods);
        }

        //放行哪些原始域(头部信息)
        config.addAllowedHeader(ALL);
        //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        if (!CollectionUtils.isEmpty(exposedHeaders)) {
            config.setExposedHeaders(exposedHeaders);
            LOG.info("WebSocket示例-HTTP跨域, 增加允许Header:{}", exposedHeaders);
        }

        //2.添加映射路径
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        if (StringUtils.isNotEmpty(path)) {
            configSource.registerCorsConfiguration(path, config);
        } else {
            configSource.registerCorsConfiguration(ALL_PATH, config);
        }
        return new CorsFilter(configSource);
    }
}