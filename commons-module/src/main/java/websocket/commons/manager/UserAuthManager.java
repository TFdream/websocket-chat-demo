package websocket.commons.manager;

import websocket.commons.entity.UserInfoDTO;
import websocket.commons.util.Base64Codec;
import websocket.commons.util.JsonUtils;

/**
 * @author Ricky Fung
 */
public class UserAuthManager {

    public static UserAuthManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public UserInfoDTO validateToken(String token) {
        String json = Base64Codec.decode(token);
        return JsonUtils.parseObject(json, UserInfoDTO.class);
    }

    public String genToken(Long id, String nickname) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(id);
        userInfoDTO.setNickname(nickname);
        return Base64Codec.encode(JsonUtils.toJson(userInfoDTO));
    }

    private static class SingletonHolder {
        private static final UserAuthManager INSTANCE = new UserAuthManager();

    }
}
