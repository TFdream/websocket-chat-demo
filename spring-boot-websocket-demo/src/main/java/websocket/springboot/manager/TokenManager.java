package websocket.springboot.manager;

import org.springframework.stereotype.Component;
import websocket.springboot.entity.UserInfo;
import websocket.springboot.util.Base64Utils;
import websocket.springboot.util.JsonUtils;

/**
 * @author Ricky Fung
 */
@Component
public class TokenManager {

    /**
     * 用户身份解密
     * @param token
     * @return
     */
    public UserInfo validateToken(String token) {
        String json = Base64Utils.decode(token);
        return JsonUtils.parseObject(json, UserInfo.class);
    }

    public String genToken(Long userId, String username) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(username);

        String json = JsonUtils.toJson(userInfo);
        return Base64Utils.encode(json);
    }
}
