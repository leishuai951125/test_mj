package leishuai.controller;

import leishuai.lsmj.ws.bean.Account;
import leishuai.lsmj.ws.service.AccountService;
import leishuai.lsmj.ws.service.impl.AccountServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
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
    AccountService accountService = new AccountServiceImpl();
    @RequestMapping("login")
    public String login(HttpServletRequest request, HttpSession session){
//        String path = request.getContextPath();  //要么为空，要么为"/xiangmuming" 总之后面要加/
//        System.out.println("========="+path);
//        String indexPath="redirect:/index.jsp";
        String s = request.getParameter("accountId");
        Long accountId=0L;
        try{
            accountId= Long.parseLong(s);
        }catch (Exception e){
            request.setAttribute("error","账户应该为数字");
            return "../index";
        }
        String username = accountMap.get(accountId);
        if (username != null) { //验证通过
            Account account = new Account(accountId, username);
            account.setHeadImgUrl("img/头像.jpg");
            session.setAttribute("account", account);
            session.setMaxInactiveInterval(-1);
            accountService.addAccountIntoSessionMap(accountId, session);
            return "redirect:/static/index.jsp";
        }
        request.setAttribute("error","账户不存在");
        return "../index";
    }

    @RequestMapping("logout")
    public String logout(){
        return null;
    }
    {
        accountMap.put(15671582806L, "雷帅");
        accountMap.put(15671582807L, "施庄明");
        accountMap.put(15671582808L, "赵进化");
        accountMap.put(15671582809L, "杨帆");
        accountMap.put(15671582802L, "雷帅");
        accountMap.put(15671582803L, "施庄明");
        accountMap.put(15671582804L, "赵进化");
        accountMap.put(15671582805L, "杨帆");
        accountMap.put(15671582800L, "summer");
        accountMap.put(15671582801L, "张三");
    }
}
