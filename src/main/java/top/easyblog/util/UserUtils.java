package top.easyblog.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import top.easyblog.entity.po.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * 操作用户信息的工具类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/10 20:09
 */
@Slf4j
public class UserUtils {

    /**
     * 从Cookie中获取登录用户的信息
     *
     * @param request
     * @return
     */
    public static User getUserFromCookie(HttpServletRequest request) {
        User user = null;
        try {
            Cookie[] cookies = request.getCookies();
            if (Objects.nonNull(cookies)) {
                for (Cookie cookie : cookies) {
                    if ("USER-INFO".equalsIgnoreCase(cookie.getName())) {
                        String userJsonStr = URLDecoder.decode(cookie.getValue(), String.valueOf(CharsetUtil.UTF_8));
                        if (Objects.nonNull(userJsonStr)) {
                            user = JSON.parseObject(userJsonStr, User.class);
                            if(user!=null){
                                user.setUserPassword(null);
                            }
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        return user;
    }


    /**
     * 从Redis中查找指定Id的用户的登录信息
     *
     * @param userId 用户Id
     * @return User对象
     */
    public static User getUserFromRedis(Integer userId) {
        //从Redis中查询出已经登录User的登录信息
        String userJsonStr = (String) RedisUtils.getRedisUtils().hget("user-" + userId, "user", RedisUtils.DB_1);
        if (Objects.isNull(userJsonStr) || userJsonStr.length() <= 0) {
            return null;
        }
        return JSON.parseObject(userJsonStr, User.class);
    }

    /**
     * 更新已经登录的用户存储在Redis中和本地Cookie的信息
     *
     * @param user 较新的用户信息
     * @return 更新成功返回true, 更新失败返回false
     */
    public static boolean updateLoggedUserInfo(User user, HttpServletRequest request, HttpServletResponse response) {
        if (Objects.isNull(user)) {
            throw new NullPointerException("param 'user' must not be null");
        }
        if (Objects.isNull(request)) {
            throw new NullPointerException("param 'request' must not be null");
        }
        return updateLoggedUserInfoInRedis(user) && updateLoggedUserInfoInCookie(request, response, user);
    }


    /**
     * 更新登录用户在Redis中的信息
     *
     * @param user 较新的用户信息
     * @return 更新成功返回true, 更新失败返回false
     */
    private static boolean updateLoggedUserInfoInRedis(User user) {
        try {
            long expire = RedisUtils.getRedisUtils().getExpire("user-" + user.getUserId(), RedisUtils.DB_1) < 0 ? 60 * 60 * 24 * 15 : RedisUtils.getRedisUtils().getExpire("user-" + user.getUserId(), RedisUtils.DB_1);
            RedisUtils.getRedisUtils().delete(RedisUtils.DB_1, "user-" + user.getUserId());
            RedisUtils.getRedisUtils().hset("user-" + user.getUserId(), "user", JSONObject.toJSONString(user), RedisUtils.DB_1);
            RedisUtils.getRedisUtils().expire("user-" + user.getUserId(), expire, RedisUtils.DB_1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新登录用户在Cookie中的信息
     *
     * @param request
     * @param response
     * @param user     较新的用户信息
     * @return 更新成功返回true, 更新失败返回false
     */
    private static boolean updateLoggedUserInfoInCookie(HttpServletRequest request, HttpServletResponse response, User user) {
        return CookieUtils.updateCookie(request, response, "USER-INFO", JSON.toJSONString(user), 60 * 60 * 24);
    }

}
