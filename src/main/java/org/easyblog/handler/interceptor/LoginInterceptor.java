package org.easyblog.handler.interceptor;

import org.easyblog.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 非法访问拦截器
 */

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

   private final Logger log= LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Request from：{}",request.getRequestURL());
        User user = (User) request.getSession().getAttribute("LOGIN-USER");
        if(Objects.isNull(user)){
            response.sendRedirect(request.getContextPath()+"/toLoginPage");
            return false;
        }
        return true;
    }

}
