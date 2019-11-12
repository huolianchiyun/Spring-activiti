package boot.spring.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String requestUrl = request.getServletPath();
        if(!(requestUrl.endsWith("login") || requestUrl.endsWith("loginvalidate") || requestUrl.endsWith(".js") || requestUrl.endsWith(".css"))
                && StringUtils.isEmpty(username)){
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
            response.sendRedirect(basePath + "login");
        }else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
