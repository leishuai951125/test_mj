package leishuai.controller;

import leishuai.bean.Account;
import leishuai.bean.Room;
import leishuai.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/18 10:46
 * @Version 1.0
 */
@Controller
@RequestMapping("room")
public class RoomController {
    @Autowired
    RoomService roomService;

    @RequestMapping("checkPriRoom")
    @ResponseBody
    public String checkPriRoom(Long roomId){
        if(roomService.isPriExist(roomId)==false){
            return "fail";
        }
        return "success";
    }

    @RequestMapping("createPriRoom")
    public String createPriRoom(Integer diFen, Integer sumTurn, HttpServletRequest request){
        if(false){ //如果钱不够,返回大厅  // TODO: 2019/1/18
            return "hall";
        }
        Room room=roomService.createPriRoom(diFen,sumTurn);
//        return "redirect:"+request.getContextPath()+"/static/room.jsp?roomId="+room.getRoomId();
        return "redirect:/static/room.jsp?roomId="+room.getRoomId();
    }
}
