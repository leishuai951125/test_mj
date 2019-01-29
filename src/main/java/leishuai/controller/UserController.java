package leishuai.controller;

import leishuai.bean.Account;
import leishuai.service.AccountService;
import leishuai.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/30 7:56
 * @Version 1.0
 */
@Controller
@RequestMapping("user")
public class UserController {
    Map<Long, String> accountMap = new HashMap(8);
    @Autowired
    AccountService accountService ;

    @RequestMapping("login")
    public String login(HttpServletRequest request, HttpSession session){
        String s = request.getParameter("accountId");
        Long accountId=0L;
        try{
            accountId= Long.parseLong(s);
        }catch (Exception e){
            request.setAttribute("error","账户应该为数字");
            return "../index";
        }
        String []nameAndImg=null;
        try {
             nameAndImg=accountMap.get(accountId).toString().split("#");
        }catch (Exception e){
            request.setAttribute("error","账户不存在");
            return "../index";
        }
        String username = nameAndImg[0];
        if (username != null) { //验证通过
            //创建account
            Account account=accountService.getAccountOnGame(accountId);
            boolean isOnGame=false;
            if(account!=null){
                isOnGame=true;
            }else {
                account = new Account(accountId, username);
                int imgNo=(int)(Math.random()*12);
                imgNo=Integer.parseInt(nameAndImg[1]);
                String url="img/head/"+imgNo+".jpg";
                account.setHeadImgUrl(url);
            }
            //把account存储到session中
            session.setAttribute("account", account);
            session.setMaxInactiveInterval(-1);
            accountService.addAccountIntoSessionMap(accountId, session);
            if(isOnGame){
                return "redirect:/static/room.jsp"; //不使用重定向会出错
            }else {
                return "hall";
            }
        }
        request.setAttribute("error","账户不存在");
        return "../index";
    }

    @RequestMapping("logout")
    public String logout(){
        return null;
    }

    @RequestMapping("register")
    @ResponseBody
    public Object register(long accountId, String username, HttpServletResponse response){
        System.out.println(accountId +" "+username);
//        response.setContentType("text/html;charset=utf-8");
        if(accountMap.get(accountId)!=null){
            return "该账户已存在，添加失败".toCharArray();
        }else {
            accountMap.put(accountId,username+"#"+(int)(Math.random()*12));
            return "注册成功".toCharArray();
        }
    }

    {
        accountMap.put(15671582806L, "雷帅#8");
        accountMap.put(15671582807L, "施庄明#11");
        accountMap.put(15671582808L, "赵进化#10");
        accountMap.put(15671582809L, "杨帆#9");
        accountMap.put(15671582802L, "雷帅#1");
        accountMap.put(15671582803L, "施庄明#2");
        accountMap.put(15671582804L, "赵进化#3");
        accountMap.put(15671582805L, "杨帆#4");
        accountMap.put(15671582800L, "summer#5");
        accountMap.put(15671582801L, "张三#6");
    }
}
