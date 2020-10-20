package top.easyblog.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.easyblog.util.CookieUtils;
import top.easyblog.util.RedisUtils;
import top.easyblog.web.controller.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法访问拦截器
 *
 * @author huangxin
 */

@Slf4j
@Configuration
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public LoginInterceptor() {

    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Request from：{}", request.getRequestURL());
        //从Cookie中拿出sessionId去Redis中检查，如果有相关信息，那就说明用户登录过了，否者没有登录过，不允许访问后条管理界面，重定向到登录页面
        String sessionId = CookieUtils.getCookieValue(request, BaseController.JSESSIONID);
        Boolean exists = RedisUtils.getRedisUtils().exists(sessionId, RedisUtils.DB_1);
        //用户进行后台（改个人信息、发布/修改/删除博客、分类、编辑评论....）操作的时候必须是处于登录状态
        String url = request.getRequestURL().toString();
        if (url.contains("/manage") && !exists) {
            response.sendRedirect(request.getContextPath() + "/user/loginPage");
            return false;
        }
        //如果仅仅是浏览一下首页、博客文章都一律放行
        return true;
    }

}
