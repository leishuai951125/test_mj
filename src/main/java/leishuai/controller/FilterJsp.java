package leishuai.controller;

import com.sun.deploy.net.HttpRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "Filter", urlPatterns = "/static/*")
public class FilterJsp implements javax.servlet.Filter {
    @Override
    public void destroy() {
    }

    @Override

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
//        System.out.println("===============jspFilter");
        if (((HttpServletRequest) req).getSession().getAttribute("account") != null) {
            chain.doFilter(req, resp);
        } else {
            String contextUrl = request.getContextPath();
//            request.getRequestDispatcher(contextUrl+"/index.jsp").forward(req, resp);
            HttpServletResponse response = (HttpServletResponse) resp;
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override

    public void init(FilterConfig config) throws ServletException {

    }

}
