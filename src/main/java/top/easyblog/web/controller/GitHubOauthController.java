package top.easyblog.web.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserSigninLog;
import top.easyblog.util.CookieUtils;
import top.easyblog.util.NetWorkUtils;
import top.easyblog.web.oauth2.IAuthService;
import top.easyblog.web.oauth2.bean.GitHubUser;
import top.easyblog.web.oauth2.enums.ThirdPartAppType;
import top.easyblog.web.service.IOauthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * 提供各种登录认证的控制器
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/27 23:58
 */
@Slf4j
@Controller
@RequestMapping("/oauth/github")
public class GitHubOauthController extends BaseController{

    @Autowired
    private IAuthService<GitHubUser> githubAuthService;
    @Autowired
    private IOauthService<GitHubUser> oauthService;

    /**
     * 让用户跳转到 GitHub授权页面
     *
     * @return 跳转url
     */
    @GetMapping(value = "/")
    public String authorize() {
        String url = githubAuthService.getAuthorizationUrl();
        log.info("Authorize url:{}", url);
        return "redirect:" + url;
    }


    /**
     * GitHub回调接口：当用户同一授权之后github调用此接口，用于返回到系统的指定页面
     *
     * @param code github回调的时候返回的授权码（使用过后据失效）
     * @return 指定页面
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(value = "code") String code,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) {
        String accessToken = githubAuthService.getAccessToken(code);
        //获得用户在Github上的账户信息
        GitHubUser thirdPratUserInfo = githubAuthService.getUserInfo(accessToken);
        //在本系统中尝试获取用户的信息和GitHub返回的信息对比
        GitHubUser gitHubUser = oauthService.getUserByOpenIdAndAppType(thirdPratUserInfo.getId(), ThirdPartAppType.GitHub);

        Integer registerId = -1;
        User userInfo = null;
        //和系统绑定
        if (gitHubUser == null) {
            userInfo = bindThirdPartLoggedUser(thirdPratUserInfo, request);
            if (userInfo != null) {
                registerId = userInfo.getUserId();
                log.info("用户使用第三方GitHub登录/注册成功 ,{}", LocalDateTime.now());
            } else {
                log.info("用户使用第三方GitHub登录/注册失败 ,{}", LocalDateTime.now());
                redirectAttributes.addFlashAttribute("error", "服务异常，请重试！");
                return LOGIN_PAGE;
            }
        }

        //registerId==-1说明用户已经使用GitHub授权过了，直接登录即可
        // 登录之前查询之前绑定的用户信息
        if (registerId == -1) {
            assert gitHubUser != null;
            userInfo = userService.getUser(Integer.parseInt(gitHubUser.getId()));
        }

        String sessionId = request.getSession().getId();
        Boolean exists = redisUtil.exists(sessionId, REDIS_DB);
        //第三方登录
        if (exists == null || !exists) {
            CookieUtils.updateCookie(request,response,JSESSIONID,sessionId,MAX_USER_LOGIN_STATUS_KEEP_TIME);
            //会话信息，如果没有主动退出15天有效
            redisUtil.set(sessionId, JSONObject.toJSONString(userInfo), REDIS_DB);
            redisUtil.expire(sessionId, MAX_USER_LOGIN_STATUS_KEEP_TIME, REDIS_DB);
            final int uid = registerId;
            executor.execute(() -> {
                String ip = NetWorkUtils.getInternetIPAddress(request);
                String ipInfo = NetWorkUtils.getLocation(ip);
                userSigninLogService.saveSigninLog(new UserSigninLog(uid, ip, ipInfo, "登录成功"));
            });
            return loginRedirectUrl(request);
        } else {
            redirectAttributes.addFlashAttribute("error", "您已经登录，请不要重复登录！");
            return loginRedirectUrl(request);
        }
    }


    private User bindThirdPartLoggedUser(GitHubUser thirdPratUserInfo, HttpServletRequest request) {
        //用户首次在系统使用GitHub登录
        User user = new User();
        //重github获取用户的昵称和头像
        user.setUserNickname(thirdPratUserInfo.getLogin());
        user.setUserHeaderImgUrl(thirdPratUserInfo.getAvatarUrl());
        String ip = NetWorkUtils.getInternetIPAddress(request);
        String ipInfo = NetWorkUtils.getLocation(ip);
        user.setUserRegisterIp(ip + " " + ipInfo);
        userService.registerByThirdPart(user);
        //返回用户的Id,MyBatis把自增Id返回在对象中
        int register = oauthService.register(thirdPratUserInfo, user.getUserId());
        return register > 0 ? userService.getUser(register) : null;
    }


}
