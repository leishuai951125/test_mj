package leishuai.controller;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by leishuai on 2018/7/23.
 */
public class FilterController implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest httpRequest,
                                HttpServletResponse httpResponse, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                           Object arg2, ModelAndView arg3) throws Exception {


    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object object) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("account") != null) { //已登陆
            return true;
        } else { //未登陆
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return false;
        }
    }
}
