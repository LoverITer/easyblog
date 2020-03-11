package top.easyblog.commons.utils;

import io.netty.util.CharsetUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 对Cookie进行增删改的工具类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/11 12:01
 */
public class CookieUtil {

    private CookieUtil() {
    }

    /**
     * 读取cookie
     *
     * @param req
     */
    public static Map<String, Object> getAllCookiesAsMap(HttpServletRequest req) {
        //获得所有cookie数据
        Cookie[] cookies = req.getCookies();
        Map<String, Object> map = null;
        if (Objects.nonNull(cookies)) {
            map = new HashMap<>(cookies.length);
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie.getValue());
            }
        }
        return map;
    }

    /**
     * 根据指定获得cookie值
     *
     * @param req
     */
    @SuppressWarnings("unused")
    public static Object getCookieValue(HttpServletRequest req, String name) {
        if (Objects.isNull(name)) {
            throw new NullPointerException("param 'name' must not be null");
        }
        String value = null;
        //首先从cookie中取存储的值
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                try {
                    value = URLDecoder.decode(cookie.getValue(), String.valueOf(CharsetUtil.UTF_8));
                    if (name.equals(value)) {
                        value = cookie.getValue();
                        break;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }
        return value;
    }


    /**
     * 添加cookie
     * 注意若添加的cookie包含中文则必须设置编码，读取也必须设置编码
     *
     * @param req
     * @param resp
     */
    public static void addCookie(HttpServletRequest req, HttpServletResponse resp, String name, String value, int expire) {
        //创建cookie值
        Cookie cookie = null;
        try {
            cookie = new Cookie(name, URLEncoder.encode(value, String.valueOf(CharsetUtil.UTF_8)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //设置cookie路径
        cookie.setPath("/");
        //不设置的话，则cookies不写入硬盘,而是写在内存,只在当前页面有用,以秒为单位
        cookie.setMaxAge(expire);
        resp.addCookie(cookie);
    }


    /**
     * 根据指定名称修改cookie<br/>
     * 注意一、修改、删除Cookie时，新建的Cookie除value、maxAge之外的所有属性，例如name、path、domain等，都要与原Cookie完全一样。
     * 否则，浏览器将视为两个不同的Cookie不予覆盖，导致修改、删除失败。
     *
     * @param request
     * @param response
     * @param name     Cookie name
     * @param value    Cookie value
     * @param expire   Cookie 过期时间
     */
    public static boolean updateCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int expire) {
        Cookie[] cookies = request.getCookies();
        try {
            if (Objects.nonNull(cookies)) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name)) {
                        cookie.setValue(URLEncoder.encode(value, String.valueOf(CharsetUtil.UTF_8)));
                        cookie.setPath("/");
                        cookie.setMaxAge(expire);
                        response.addCookie(cookie);
                        return true;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据指定名称删除cookie
     *
     * @param request
     * @param response
     * @param name
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    //设置值为null
                    cookie.setValue(null);
                    //立即销毁cookie
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }
}
