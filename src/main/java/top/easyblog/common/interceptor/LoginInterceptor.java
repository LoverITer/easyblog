package top.easyblog.common.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.easyblog.entity.po.User;
import top.easyblog.util.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 非法访问拦截器
 *
 * @author huangxin
 */

@Slf4j
@Configuration
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private final RedisUtils redisUtil;

    public LoginInterceptor(RedisUtils redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Request from：{}", request.getRequestURL());
        String url = request.getRequestURL().toString();
        //用户进行后台（改个人信息、发布/修改/删除博客、分类、编辑评论....）操作的时候必须是处于登录状态
        if (url.contains("/manage")) {
            String userId = request.getParameter("userId");
            if (Objects.isNull(userId)) {
                userId = url.substring(url.lastIndexOf('/')+1);
            }
            if (Objects.isNull(userId)) {
                response.sendRedirect(request.getContextPath() + "/user/loginPage");
                return false;
            }
            String userJsonStr = (String) redisUtil.hget("user-" + userId, "user", 1);
            if (Objects.isNull(userJsonStr) || userJsonStr.length() <= 0) {
                //找不到直接重定位到登录页面
                response.sendRedirect(request.getContextPath() + "/user/loginPage");
                return false;
            }
            User user = JSON.parseObject(userJsonStr, User.class);
            if (Objects.isNull(user)) {
                response.sendRedirect(request.getContextPath() + "/user/loginPage");
                return false;
            }
        }
        //如果仅仅是浏览一下的博客，分类...都一律放行
        return true;
    }

}
