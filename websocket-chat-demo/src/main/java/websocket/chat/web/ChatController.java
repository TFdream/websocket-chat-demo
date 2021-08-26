package websocket.chat.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Ricky Fung
 */
@Controller
@RequestMapping("/ws")
public class ChatController {

    @GetMapping("/chat")
    public ModelAndView hello(@RequestParam("token") String token) {
        ModelAndView mav = new ModelAndView("chat");
        mav.addObject("token", token);
        return mav;
    }
}
