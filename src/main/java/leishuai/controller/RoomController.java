package leishuai.controller;

import com.alibaba.fastjson.JSON;
import leishuai.bean.Account;
import leishuai.bean.Room;
import leishuai.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

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
    public String createPriRoom(Integer diFen, Integer sumTurn, Integer sumPlayer,HttpServletRequest request){
        if(false){ //如果钱不够,返回大厅  // TODO: 2019/1/18
            return "hall";
        }
        Room room=roomService.createPriRoom(diFen,sumTurn,sumPlayer);
//        return "redirect:"+request.getContextPath()+"/static/room.jsp?roomId="+room.getRoomId();
        return "redirect:/static/room.jsp?roomId="+room.getRoomId();
    }

    static int i=0;
    static long[]time=new long[2];
    static Object lock=new Object();
    @RequestMapping("test")
    public void test(HttpServletResponse response) throws IOException {
        synchronized (lock){
            if(i%100==0){
                time[1]=System.nanoTime();
                System.out.println((time[1]-time[0])/1000000);
                time[0]=time[1];
//            System.out.println("accept");
            }
            i++;
            PrintWriter writer=response.getWriter();
            writer.write("success");
//            writer.flush();
            writer.close();
        }
    }
}

