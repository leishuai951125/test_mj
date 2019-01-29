package leishuai.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "FilterImage",urlPatterns = {"/static/img/*"})
public class FilterImage implements Filter {
    interface T{
        long one_hour=60*60;
        long one_day=one_hour*24;
        long one_month=one_day*30;
    }
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse response=(HttpServletResponse)resp;
        HttpServletRequest request=(HttpServletRequest)req;
        System.out.println(request.getRequestURI());
//        System.out.println(request.getRequestURL());
        response.setHeader("Cache-Control","max-age="+ T.one_month*3);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
