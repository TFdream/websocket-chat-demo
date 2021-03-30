package websocket.springboot.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import websocket.springboot.entity.ResultDTO;
import websocket.springboot.manager.SessionManager;
import websocket.springboot.manager.TokenManager;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ricky Fung
 */
@Controller
@RequestMapping("/api/web-socket")
public class UserController {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final AtomicLong counter = new AtomicLong();
    private final Map<String, Long> userMap = new HashMap<>();

    @Resource
    private TokenManager tokenManager;

    //页面请求
    @GetMapping("/login/{username}")
    public ModelAndView socket(@PathVariable("username") String username) {
        if (StringUtils.isEmpty(username)) {
             throw new IllegalArgumentException("username不能为空");
        }
        Long userId = userMap.computeIfAbsent(username, (key)-> counter.incrementAndGet());
        LOG.info("用户登录开始, username={}, userId={}", username, userId);

        //生成token
        String token = tokenManager.genToken(userId, username);

        ModelAndView mav = new ModelAndView("/chat");
        mav.addObject("userId", userId);
        mav.addObject("username", username);
        mav.addObject("token", token);
        return mav;
    }

    //推送数据接口
    @ResponseBody
    @RequestMapping("/push/{username}")
    public ResultDTO pushToWeb(@PathVariable("username") String username, String message) {
        Long userId = null;
        if (StringUtils.isNotEmpty(username)) {
            userId = userMap.get(username);
        }
        //给对应用户发送消息
        LOG.info("推送消息开始, username={}, userId={}", username, userId);
        SessionManager.getMgr().sendMessage(userId, message);
        return ResultDTO.ok();
    }

    //=======
    @GetMapping("/hello/{username}")
    public ModelAndView hello(@PathVariable("username") String username) {
        ModelAndView mav = new ModelAndView("/hello");
        mav.addObject("username", username);
        return mav;
    }
}
