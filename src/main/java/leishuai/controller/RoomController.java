package leishuai.controller;

import leishuai.lsmj.ws.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/18 10:46
 * @Version 1.0
 */
@Controller
@RequestMapping("room")
public class RoomController {
    @RequestMapping("checkPriRoom")
    @ResponseBody
    public String checkPriRoom(){
        if(true){
            return "fail";
        }
        return "success";
    }
}
