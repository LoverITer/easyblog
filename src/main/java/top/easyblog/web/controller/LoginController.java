package top.easyblog.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.easyblog.config.executor.WorkerThreadPoolExecutor;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserAccount;
import top.easyblog.entity.po.UserSigninLog;
import top.easyblog.global.email.Email;
import top.easyblog.global.email.EmailSender;
import top.easyblog.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;

import static top.easyblog.global.enums.UserLoginStatus.UNLOGIN;

/**
 * @author huangxin
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class LoginController extends BaseController {

    @Autowired
    private EmailSender emailUtil;

    private static final String REFERER_PREFIX="Referer-";

    /***AES ECB加密的key*/
    private static final String PASSWORD_KEY="1a2b3c4d5e6f7g8h";
    /**
     * SHA1 秘钥
     **/
    private static final String SHA1_SECRET_KEY = "user";
    /**
     * 验证码key的前缀，验证码的key由 CAPTCHA_CODE_PREFIX+用户账号构成
     * 发送验证码之后会将其缓存到Redis中，
     */
    private static final String CAPTCHA_CODE_KEY_PREFIX = "captcha-code-";
    /**
     * 验证码有效的期限，60*5(秒，即5分钟)
     */
    private static final Integer CAPTCHA_CODE_KEEP_ALIVE = 60 * 5;


    @GetMapping("/login.html")
    public String login(HttpServletRequest request) {
        //把用户登录前的地址存下来,方便用户登录成功之后直接跳转到登录前的页面
        String refererKey = REFERER_PREFIX + NetWorkUtils.getInternetIPAddress(request);
        String refererUrl = (String) redisUtil.get(refererKey, REDIS_DB);
        if (Objects.isNull(refererUrl)) {
            String referUrl = request.getHeader(HttpHeaders.REFERER);
            //服务器的根路径
            StringBuilder baseUrl = new StringBuilder();
            baseUrl.append(request.getScheme()).append("://")
                    .append(request.getServerName()).append(":")
                    .append(request.getServerPort())
                    .append(request.getContextPath()).append("/");
            if (StringUtil.isNotEmpty(referUrl) &&
                    !referUrl.contains("/login.html") &&
                    !referUrl.contains("/registration.html") &&
                    !referUrl.contains("/reset.html") &&
                    !referUrl.contains("/register-success") &&
                    !referUrl.equalsIgnoreCase(baseUrl.toString())) {
                //存到Redis中
                WorkerThreadPoolExecutor.setThreadName("Save Referer URL To Redis");
                executor.execute(() -> redisUtil.set(refererKey, referUrl, 30 * 30, REDIS_DB));
            }
        }
        return "login";
    }


    /**
     * 发送登注册、找回密码的验证码到用户手机
     *
     * @param phone   手机号
     * @param option  操作：register表示注册 modify-pwd表示修改密码
     */
    @ResponseBody
    @GetMapping(value = "/phoneCaptchaCode")
    public WebAjaxResult sendCaptchaCode2Phone(@RequestParam("phone") String phone,
                                               @RequestParam(value = "option", defaultValue = "register") String option) {
        WebAjaxResult webAjaxResult = new WebAjaxResult();
        webAjaxResult.setMessage("不支持的操作");
        String code = SendMessageUtils.getRandomCode(6);
        if ("register".equals(option)) {
            String content = String.format("【EasyBlog】您正在通过手机注册，验证码为：%s，%d分钟内有效，如非本人操作请忽略。", code, (CAPTCHA_CODE_KEEP_ALIVE / 60));
            webAjaxResult.setSuccess(true);
            webAjaxResult.setMessage(sendSms(phone, code, content));
        } else if ("modify-pwd".equals(option)) {
            String content = String.format("【EasyBlog】您正在通过手机找回密码，验证码为：%s，%d分钟内有效，如非本人操作请忽略。", code, (CAPTCHA_CODE_KEEP_ALIVE / 60));
            webAjaxResult.setSuccess(true);
            webAjaxResult.setMessage(sendSms(phone, code, content));
        }
        return webAjaxResult;
    }

    /**
     * 发送验证码消息到手机
     *
     * @param phone   手机号
     * @param code    验证码
     * @param content 验证消息文本
     */
    private String sendSms(String phone, String code, String content) {
        Integer send = 0;
        try {
            //短信验证码5分钟有效
            redisUtil.set(CAPTCHA_CODE_KEY_PREFIX + phone, code, CAPTCHA_CODE_KEEP_ALIVE, REDIS_DB);
            log.info("向{}发送验证码:{}", phone, code);
            send = SendMessageUtils.send("loveIT", "d41d8cd98f00b204e980", phone, content);
            if (send == 1) {
                log.info("向{}发送验证码成功", phone);
                userPhoneLogService.saveSendCaptchaCode2User(phone, content);
                return "验证码已发送到您的手机，请注意查收！";
            } else {
                throw new RuntimeException(SendMessageUtils.getMessage(send));
            }
        } catch (Exception e) {
            userPhoneLogService.saveSendCaptchaCode2User(phone, "短信发送异常！");
            redisUtil.delete(REDIS_DB, CAPTCHA_CODE_KEY_PREFIX + phone);
            log.error("向{}发送验证码异常:{}", phone, e.getMessage());
            return "服务异常，请重试！";
        }
    }

    /**
     * 发送登注册、找回密码的验证码到用户邮箱
     *
     * @param email  邮箱号
     * @param option 操作：register表示注册 modify-pwd表示修改密码
     */
    @ResponseBody
    @GetMapping(value = "/mailCaptchaCode")
    public WebAjaxResult sendCaptchaCode2Email(@RequestParam("email") String email,
                                               @RequestParam(value = "option", defaultValue = "register") String option) {
        WebAjaxResult webAjaxResult = new WebAjaxResult();
        webAjaxResult.setMessage("不支持的操作");
        String code = SendMessageUtils.getRandomCode(6);
        if ("register".equals(option)) {
            String content = String.format("【EasyBlog】您正在通过邮箱注册，验证码为：%s，%d分钟内有效，如非本人操作请忽略。", code, (CAPTCHA_CODE_KEEP_ALIVE / 60));
            webAjaxResult.setSuccess(true);
            webAjaxResult.setMessage(sendEmail(email, content, code));
        } else if ("modify-pwd".equals(option)) {
            String content = String.format("【EasyBlog】您正在通过邮箱找回密码，验证码为：%s，%d分钟内有效，如非本人操作请忽略。", code, (CAPTCHA_CODE_KEEP_ALIVE / 60));
            webAjaxResult.setSuccess(true);
            webAjaxResult.setMessage(sendEmail(email, content, code));
        }
        return webAjaxResult;
    }

    /**
     * 发送邮件
     *
     * @param email   邮箱号
     * @param content 邮件内容
     * @param code    验证码
     * @return
     */
    private String sendEmail(String email, String content, String code) {
        boolean send = false;
        try {
            log.info("向" + email + "发送验证码：" + code);
            redisUtil.set(CAPTCHA_CODE_KEY_PREFIX + email, code, CAPTCHA_CODE_KEEP_ALIVE, REDIS_DB);
            Email e = new Email("验证码", email, content, null);
            send = emailUtil.send(e);
            userEmailLogService.saveSendCaptchaCode2User(email, content);
        } catch (Exception e) {
            redisUtil.delete(REDIS_DB, CAPTCHA_CODE_KEY_PREFIX + email);
            userEmailLogService.saveSendCaptchaCode2User(email, "邮件发送异常！");
            log.error("邮件发送异常" + e.getMessage());
            return "服务异常，请重试！";
        }
        log.info("向" + email + "发送验证码成功");
        return send ? "验证码已发送到您的邮箱，请注意查收！" : "请检查邮箱是否正确，并重试！";
    }


    /**
     * 用户登录逻辑
     *
     * @param username           用户的账号
     * @param password           密码
     * @param remember           "记住我"功能状态
     * @return
     */
    @RequestMapping(value = "/login")
    public String login(@RequestParam(value = "username", defaultValue = "") String username,
                        @RequestParam(value = "password", defaultValue = "") String password,
                        @RequestParam(value = "remember-me", defaultValue = "") String remember,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        User user = userService.checkUser(username, EncryptUtils.getInstance().SHA1(password, SHA1_SECRET_KEY));
        String ip = NetWorkUtils.getInternetIPAddress(request);
        String location = NetWorkUtils.getLocation(ip);
        String sessionId=null;
        try {
            if (user != null) {
                user.setUserPassword(null);
                //防止重复登录
                Boolean exists = redisUtil.exists(String.valueOf(user.getUserId()), REDIS_DB);
                if (exists == null || !exists) {
                    sessionId = doLogin(request, response, user);
                    // 记住我功能：保存用户名密码一个月
                    if ("on".equals(remember)) {
                        Object value = CookieUtils.getCookieValue(request, REMEMBER_ME_COOKIE);
                        if (Objects.isNull(value)) {
                            CookieUtils.setCookie(request, response, REMEMBER_ME_COOKIE, username + "-" + AESCryptUtils.encryptECB(password, PASSWORD_KEY), REMEMBER_ME_TIME);
                        }
                    }
                    WorkerThreadPoolExecutor.setThreadName("Save Sign In Log");
                    executor.execute(() -> userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录成功")));
                    return loginRedirectUrl(request);
                } else {
                    String localSessionId = CookieUtils.getCookieValue(request, JSESSIONID);
                    if (localSessionId == null) {
                        redirectAttributes.addFlashAttribute("error", "您的账号已经在别处登录！");
                    } else {
                        redirectAttributes.addFlashAttribute("error", "您已经登录，请不要重复登录！");
                    }

                    return LOGIN_PAGE;
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "用户名和密码不匹配！");
                return LOGIN_PAGE;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            WorkerThreadPoolExecutor.setThreadName("Save Sign In Exception Log");
            String finalSessionId = sessionId;
            executor.execute(() -> {
                if (Objects.nonNull(user)) {
                    redisUtil.delete(REDIS_DB, finalSessionId);
                    redisUtil.delete(REDIS_DB, String.valueOf(user.getUserId()));
                    CookieUtils.deleteCookie(request, response, JSESSIONID);
                    userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录失败"));
                }
            });
            redirectAttributes.addFlashAttribute("error", "服务异常，请重试！");
            return LOGIN_PAGE;
        }
    }

    /**
     * 用户登录，生成一个会话Id，以会话id为key，用户信息为value，存放到Redis，同时将以用户id为key，用户登录时间戳为value
     * 这个键用户检查用户是否登录，如果Redis中存在对应用户的id，那就不能让用户重复登录
     *
     * @param request
     * @param response
     * @param user
     * @return 会话id
     */
    private String doLogin(HttpServletRequest request, HttpServletResponse response, User user) {
        //登录的逻辑：服务器端会产生一个sessionId，客户端保存这个sessionId到本地，并将session为key，User的信息为val，存入Redis中
        //之后访问页面的时候首先客户端带着sessionId去Redis中检查，如果有对应的sessionId，那么继续。否则重定向到登录页面
        //request.getSession()执行之后会给客户端设置一个名为JSESSIONID的Cookie，这里面保存了第一次和服务器会话的sessionId
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();
        String userLoggedKey = String.format("%d-%s", user.getUserId(), sessionId);
        //更新用户的
        CookieUtils.updateCookie(request, response, JSESSIONID, userLoggedKey, MAX_USER_LOGIN_STATUS_KEEP_TIME);
        //会话信息，如果没有主动退出60天有效
        redisUtil.set(userLoggedKey, JSONObject.toJSONString(user), MAX_USER_LOGIN_STATUS_KEEP_TIME, REDIS_DB);
        //用户登录的时候同时将用户的id放入Redis，防止用户重复登录
        redisUtil.set(String.valueOf(user.getUserId()), System.currentTimeMillis(), MAX_USER_LOGIN_STATUS_KEEP_TIME, REDIS_DB);
        return sessionId;
    }

    /**
     * 退出操作
     *
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/logout")
    public WebAjaxResult logout(HttpServletRequest request, HttpServletResponse response) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user = UserUtils.getUserFromRedis(sessionId);
        if (user != null) {
            //删除Redis中保存的登录信息以及用户登录标志
            boolean ret = redisUtil.delete(REDIS_DB, sessionId) &&
                    redisUtil.delete(REDIS_DB, String.valueOf(user.getUserId()));
            //退出前先删除本次登录的Cookie
            boolean deleteCookieRes = CookieUtils.deleteCookie(request, response, JSESSIONID);
            ajaxResult.setSuccess(ret && deleteCookieRes);
        }
        return ajaxResult;
    }


    /**
     * 用户注册逻辑
     *
     * @param password    密码
     * @param account     用户账号
     * @param captchaCode 输入的验证码
     * @param request
     */
    @ResponseBody
    @RequestMapping(value = "/register")
    public WebAjaxResult register(@RequestParam(value = "username", defaultValue = "") String account,
                                  @RequestParam(value = "password", defaultValue = "") String password,
                                  @RequestParam(value = "captchaCode", defaultValue = "") String captchaCode,
                                  HttpServletRequest request) {
        String nickname = "用户_" + System.currentTimeMillis();
        String captcha = (String) redisUtil.get(CAPTCHA_CODE_KEY_PREFIX + account, REDIS_DB);
        String ip = NetWorkUtils.getInternetIPAddress(request);
        String ipInfo = NetWorkUtils.getLocation(ip);
        WebAjaxResult ajaxResult = new WebAjaxResult();
        if (userService.getUser(account) != null) {
            ajaxResult.setMessage("此邮箱/手机号已经注册了！");
        } else if (StringUtils.isEmpty(captcha)) {
            ajaxResult.setMessage("验证码已过期，请重新获取！");
        } else if (StringUtils.isEmpty(captchaCode)) {
            ajaxResult.setMessage("请输入验证码！");
        } else if (!captcha.equals(captchaCode)) {
            ajaxResult.setMessage("输入的验证码有误！");
        } else {
            try {
                User user = new User();
                user.setUserNickname(nickname);
                user.setUserPassword(EncryptUtils.getInstance().SHA1(password, SHA1_SECRET_KEY));
                if (RegexUtils.isEmail(account)) {
                    user.setUserMail(account);
                } else if (RegexUtils.isPhone(account)) {
                    user.setUserPhone(account);
                }
                user.setUserRegisterIp(String.format("%s %s", ip, ipInfo));
                int var0 = userService.register(user);
                if (var0 > 0) {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setAccountUser(user.getUserId());
                    int var1 = userAccountService.createAccount(userAccount);
                    if (var1 > 0) {
                        ajaxResult.setSuccess(true);
                        ajaxResult.setMessage("注册成功!");
                        log.info("用户：{} 注册成功,{}", account, new Date());
                    } else {
                        log.info("用户：{} 注册失败,{}", account, new Date());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                ajaxResult.setMessage("服务异常，请重试！");
            }
        }
        return ajaxResult;
    }


    /**
     * 找回密码
     */
    @ResponseBody
    @RequestMapping("/resetPassword")
    public WebAjaxResult resetPassword(@RequestParam("username") String account,
                                       @RequestParam("password") String newPassword,
                                       @RequestParam(value = "captchaCode", defaultValue = "") String code,
                                       HttpServletRequest request) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setSuccess(false);
        String captchaCode = (String) redisUtil.get(CAPTCHA_CODE_KEY_PREFIX + account, REDIS_DB);
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user = UserUtils.getUserFromRedis(sessionId);
        if (StringUtils.isEmpty(account)) {
            ajaxResult.setMessage("请填写您的账号！");
        } else if (StringUtils.isEmpty(captchaCode)) {
            ajaxResult.setMessage("验证码已过期，请重新获取！");
        } else if (!code.equals(captchaCode)) {
            ajaxResult.setMessage("验证码错误！");
        } else if (StringUtils.isEmpty(newPassword)) {
            ajaxResult.setMessage("请填写新密码！");
        } else if (user != null && newPassword.equals(user.getUserPassword())) {
            ajaxResult.setMessage("新密码不能和旧密码一致！");
        } else {
            try {
                int ret = userService.updateUserInfo(account, EncryptUtils.getInstance().SHA1(newPassword, SHA1_SECRET_KEY));
                if (ret > 0) {
                    ajaxResult.setSuccess(true);
                    ajaxResult.setMessage("密码修改成功，即将跳转到登录页面...");
                }
            } catch (Exception e) {
                ajaxResult.setMessage("服务异常，请重试！");
            } finally {
                redisUtil.delete(REDIS_DB, CAPTCHA_CODE_KEY_PREFIX + account);
            }
        }
        return ajaxResult;
    }


    /**
     * 检查用户的登录状态
     */
    @ResponseBody
    @RequestMapping(value = "/checkUserStatus")
    public WebAjaxResult checkUserLoginStatus(HttpServletRequest request) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage(UNLOGIN.getStatus() + "");
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user = UserUtils.getUserFromRedis(sessionId);
        if (user != null) {
            ajaxResult.setMessage(JSON.toJSONString(user));
            ajaxResult.setSuccess(true);
        }
        return ajaxResult;
    }


    /**
     * 修改关于我的描述的内容
     *
     * @param aboutMeInfo 描述信息
     * @param userId      用户Id
     * @param response
     * @param request
     */
    @ResponseBody
    @GetMapping(value = "/aboutMe")
    public WebAjaxResult settingAboutMe(@RequestParam(value = "aboutMeInfo") String aboutMeInfo,
                                        @RequestParam(defaultValue = "-1") Integer userId,
                                        HttpServletResponse response,
                                        HttpServletRequest request) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user= UserUtils.getUserFromRedis(sessionId);
        if (Objects.nonNull(user)) {
            user.setUserDescription(aboutMeInfo);
            try {
                int res = userService.updateUserInfo(user);
                WorkerThreadPoolExecutor.setThreadName("Update User Sign Info:About Me");
                executor.execute(() -> {
                    UserUtils.updateLoggedUserInfo(user, request, response);
                });
                if (res < 0) {
                    ajaxResult.setMessage("更新异常，请重试！");
                } else {
                    ajaxResult.setMessage("修改成功！");
                    ajaxResult.setSuccess(true);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                ajaxResult.setMessage("服务异常，请重试！");
            }

        }
        return ajaxResult;
    }

    /**
     * 设置我的联系方式
     *
     * @param github  Github
     * @param wechat  微信
     * @param qq      QQ
     * @param weibo   微博
     * @param twitter 推特
     * @param steam   Steam
     * @param userId  用户Id
     */
    @ResponseBody
    @GetMapping(value = "/settingContact")
    public WebAjaxResult settingContact(@RequestParam String github,
                                        @RequestParam String wechat,
                                        @RequestParam String qq,
                                        @RequestParam String weibo,
                                        @RequestParam String twitter,
                                        @RequestParam String steam,
                                        @RequestParam(defaultValue = "-1") Integer userId,
                                        HttpServletRequest request) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user= UserUtils.getUserFromRedis(sessionId);
        if (Objects.nonNull(user)) {
            UserAccount account = new UserAccount(github, wechat, qq, steam, twitter, weibo, userId);
            //尝试更新用户的account
            int res = userAccountService.updateAccountByUserId(account);
            if (res <= 0) {
                //如果没有就创建一个
                res = userAccountService.createAccount(account);
            }
            if (res > 0) {
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("修改成功！");
            } else {
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("修改失败！");
            }
        }
        return ajaxResult;
    }

    /**
     * 修改用户爱好信息
     *
     * @param hobby    爱好信息
     * @param userId   用户ID
     * @param response
     * @param request
     */
    @ResponseBody
    @GetMapping(value = "/settingHobby")
    public WebAjaxResult setUserHobby(@RequestParam String hobby,
                                      @RequestParam(defaultValue = "-1") Integer userId,
                                      HttpServletResponse response,
                                      HttpServletRequest request) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user= UserUtils.getUserFromRedis(sessionId);
        if (Objects.nonNull(user)) {
            User userHobby = new User();
            userHobby.setUserId(user.getUserId());
            userHobby.setUserHobby(hobby);
            int res = userService.updateUserInfo(userHobby);
            User user1 = CombineBeans.combine(userHobby, user);
            WorkerThreadPoolExecutor.setThreadName("Update User Sign Info:Hobby");
            executor.execute(() -> {
                UserUtils.updateLoggedUserInfo(user1, request, response);
            });
            if (res < 0) {
                ajaxResult.setMessage("更新失败，请稍后重试！");
                return ajaxResult;
            }
            ajaxResult.setSuccess(true);
            ajaxResult.setMessage("更新成功！");
        }
        return ajaxResult;
    }

    /**
     * 修改我的技术栈信息
     *
     * @param techStr  技术栈信息
     * @param userId   用户ID
     * @param response
     * @param request
     */
    @ResponseBody
    @GetMapping(value = "/settingTech")
    public WebAjaxResult settingTech(@RequestParam String techStr,
                                     @RequestParam(defaultValue = "-1") Integer userId,
                                     HttpServletResponse response,
                                     HttpServletRequest request) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user= UserUtils.getUserFromRedis(sessionId);
        if (Objects.nonNull(user)) {
            User userTech = new User();
            userTech.setUserId(user.getUserId());
            userTech.setUserTech(techStr);
            int res = userService.updateUserInfo(userTech);
            User user1 = CombineBeans.combine(userTech, user);
            WorkerThreadPoolExecutor.setThreadName("Update User Sign Info:Tech");
            executor.execute(() -> {
                UserUtils.updateLoggedUserInfo(user1, request, response);
            });
            if (res < 0) {
                ajaxResult.setMessage("修改失败！请稍后重试！");
                return ajaxResult;
            }
            ajaxResult.setSuccess(true);
            ajaxResult.setMessage("修改成功！");
        }
        return ajaxResult;
    }


}
