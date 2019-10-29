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
        String url=request.getRequestURL().toString();
        //用户进行后台（改个人信息、发布/修改/删除博客、分类、编辑评论....）操作的时候必须是处于登录状态
        if(url.contains("/manage")) {
            User user = (User) request.getSession().getAttribute("LOGIN-USER");
            if (Objects.isNull(user)) {
                response.sendRedirect(request.getContextPath() + "/user/loginPage");
                return false;
            }
        }
        //如果仅仅是查看一下别人的博客，分类...都一律放行
        return true;
    }

}
