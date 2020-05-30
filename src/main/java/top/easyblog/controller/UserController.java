package top.easyblog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.easyblog.bean.User;
import top.easyblog.bean.UserAccount;
import top.easyblog.bean.UserSigninLog;
import top.easyblog.common.email.Email;
import top.easyblog.common.email.SendEmailUtil;
import top.easyblog.common.util.*;
import top.easyblog.config.web.Result;
import top.easyblog.service.IUserAccountService;
import top.easyblog.service.impl.UserEmailLogServiceImpl;
import top.easyblog.service.impl.UserPhoneLogServiceImpl;
import top.easyblog.service.impl.UserServiceImpl;
import top.easyblog.service.impl.UserSigninLogServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;

import static top.easyblog.bean.UserLoginStatus.UNLOGIN;

/**
 * @author huangxin
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserEmailLogServiceImpl userEmailLogService;
    @Autowired
    private UserPhoneLogServiceImpl userPhoneLogService;
    @Autowired
    private SendEmailUtil emailUtil;
    @Autowired
    private UserSigninLogServiceImpl userSigninLogService;
    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    RedisUtils redisUtil;
    @Autowired
    private Executor executor;

    /***ajax异步请求成功标志*/
    private static final String AJAX_SUCCESS = "OK";
    /***ajax异步请求失败标志*/
    private static final String AJAX_ERROR = "FATAL";

    private static final String LOGIN_PAGE = "redirect:/user/loginPage";


    @GetMapping("/loginPage")
    public String toLoginPage(HttpServletRequest request) {
        //把用户登录前的地址存下来
        String refererUrl = (String) redisUtil.get("Referer-" + NetWorkUtils.getUserIp(request), 1);
        if (Objects.isNull(refererUrl)) {
            String referUrl = request.getHeader("Referer");
            //服务器的根路径
            String baseUrl = request.getScheme()
                    + "://" + request.getServerName()
                    + ":" + request.getServerPort()
                    + request.getContextPath() + "/";
            if (StringUtil.isNotEmpty(referUrl) &&
                    !referUrl.contains("/login") &&
                    !referUrl.contains("register") &&
                    !referUrl.contains("loginPage") &&
                    !referUrl.contains("change_password") &&
                    !referUrl.contains("/article/index") &&
                    !referUrl.equalsIgnoreCase(baseUrl)) {
                //存到Redis中
                redisUtil.set("Referer-" + NetWorkUtils.getUserIp(request), referUrl, 1);
            }
        }
        return "login";
    }

    /**
     * 访问注册成功页面
     *
     * @return
     */
    @GetMapping("/register-success")
    public String toRegisterSuccessPage() {
        return "register_success";
    }

    /**
     * 访问修改密码页面
     *
     * @return
     */
    @GetMapping("/change_password")
    public String toChangePassword() {
        return "change_password";
    }

    /**
     * 发送登注册、找回密码的验证码到用户手机
     *
     * @param phone   手机号
     * @param option  操作：register表示注册 modify-pwd表示修改密码
     * @param request
     */
    @ResponseBody
    @GetMapping(value = "/captcha-code2phone")
    public String sendCaptchaCode2Phone(@RequestParam("phone") String phone,
                                        @RequestParam("option") String option,
                                        HttpServletRequest request) {

        String code = SendMessageUtils.getRandomCode(6);
        if ("register".equals(option)) {
            String content = "您正在申请手机注册，验证码为：" + code + "，5分钟内有效！";
            return sendMessage(phone, code, content, request);
        } else if ("modify-pwd".equals(option)) {
            String content = "您正在通过手机找回密码，验证码为：" + code + "，5分钟内有效！";
            return sendMessage(phone, code, content, request);
        }
        return AJAX_ERROR;

    }

    /**
     * 发送消息到手机
     *
     * @param phone   手机号
     * @param code     验证码
     * @param content  验证消息文本
     * @param request    request
     */
    private String sendMessage(String phone, String code, String content, HttpServletRequest request) {

        try {
            String captchaCode = (String) redisUtil.get("captcha-code-" + phone, 1);
            if (Objects.nonNull(captchaCode)) {
                redisUtil.delete(1, "captcha-code-" + phone);
            }
            //短信验证码5分钟有效
            redisUtil.set("captcha-code-" + phone, code, 5, 1);
            log.info("向{}发送验证码:{}", phone, code);
            SendMessageUtils.send("loveIT", "d41d8cd98f00b204e980", phone, content);
            userPhoneLogService.saveSendCaptchaCode2User(phone, content);
        } catch (Exception e) {
            userPhoneLogService.saveSendCaptchaCode2User(phone, "短信发送异常！");
            redisUtil.delete(1, "captcha-code-" + phone);
            log.error("短信发送异常" + e.getMessage());
            return AJAX_ERROR;
        }
        return AJAX_SUCCESS;
    }

    /**
     * 发送登注册、找回密码的验证码到用户邮箱
     *
     * @param email  邮箱号
     * @param option 操作：register表示注册 modify-pwd表示修改密码
     */
    @ResponseBody
    @GetMapping(value = "/captcha-code2mail")
    public String sendCaptchaCode2Email(@RequestParam("email") String email,
                                        @RequestParam("option") String option) {
        String code = SendMessageUtils.getRandomCode(6);
        if ("register".equals(option)) {
            String content = "您正在申请邮箱注册，验证码为：" + code + "，60秒内有效！";
            return sendEmail(email, content, code);
        } else if ("modify-pwd".equals(option)) {
            String content = "您正在通过邮箱找回密码，验证码为：" + code + "，60秒内有效！";
            return sendEmail(email, content, code);
        }
        return AJAX_SUCCESS;
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
        try {
            String captchaCode = (String) redisUtil.get("captcha-code-" + email, RedisUtils.DB_1);
            if (Objects.nonNull(captchaCode)) {
                redisUtil.delete(1, "captcha-code-" + email);
            }
            log.info("向" + email + "发送验证码：" + code);
            redisUtil.set("captcha-code-" + email, code, 60, RedisUtils.DB_1);
            Email e = new Email("验证码", email, content, null);
            emailUtil.send(e);
            userEmailLogService.saveSendCaptchaCode2User(email, content);
        } catch (Exception e) {
            redisUtil.delete(RedisUtils.DB_1, "captcha-code-" + email);
            userEmailLogService.saveSendCaptchaCode2User(email, "邮件发送异常！");
            log.error("邮件发送异常" + e.getMessage());
            return AJAX_ERROR;
        }
        return AJAX_SUCCESS;
    }

    /**
     * 用户注册逻辑
     *
     * @param nickname    昵称
     * @param password    密码
     * @param account     用户账号
     * @param captchaCode 输入的验证码
     * @param request
     */
    @ResponseBody
    @RequestMapping(value = "/register")
    public Result register(@RequestParam(value = "nickname", defaultValue = "") String nickname,
                           @RequestParam(value = "pwd", defaultValue = "") String password,
                           @RequestParam(value = "account", defaultValue = "") String account,
                           @RequestParam(value = "code", defaultValue = "") String captchaCode,
                           HttpServletRequest request) {
        String captcha = (String) redisUtil.get("captcha-code-" + account, 1);
        String ip = NetWorkUtils.getUserIp(request);
        String ipInfo = NetWorkUtils.getLocation(request, ip);
        Result result = new Result();
        if (userService.getUser(nickname) != null) {
            result.setMessage("昵称已存在!");
        } else if (userService.getUser(account) != null) {
            result.setMessage("此邮箱/手机号已经注册了!");
        } else if (Objects.isNull(captcha) || !captcha.equals(captchaCode)) {
            result.setMessage("验证码不正确或验证码已过期!");
        } else {
            try {
                User user = new User();
                user.setUserNickname(nickname);
                user.setUserPassword(EncryptUtils.getInstance().SHA1(password, "user"));
                if (RegexUtils.isEmail(account)) {
                    user.setUserMail(account);
                } else if (RegexUtils.isPhone(account)) {
                    user.setUserPhone(account);
                }
                user.setUserRegisterIp(ip + " " + ipInfo);
                int var0 = userService.register(user);
                if (var0 > 0) {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setAccountUser(user.getUserId());
                    int var1 = userAccountService.createAccount(userAccount);
                    if (var1 > 0) {
                        result.setSuccess(true);
                        result.setMessage("注册成功!");
                        log.info("用户：{}注册成功,{}", account, new Date());
                    } else {
                        log.info("用户：{}注册失败,{}", account, new Date());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                result.setMessage("服务异常，请重试！");
            }
        }
        return result;
    }

    /**
     * 检查用户的昵称是否存在,以及是否合法
     *
     * @param nickname 昵称
     */
    @ResponseBody
    @GetMapping(value = "/checkNickname")
    public Result checkUserNickname(@RequestParam(value = "nickname", defaultValue = "") String nickname) {
        Result result = new Result();
        result.setSuccess(true);
        if (!"".equals(nickname)) {
            User user = userService.getUser(nickname);
            if (user != null) {
                result.setSuccess(false);
            }
        }
        return result;
    }

    /**
     * 检查输入的邮箱不存在
     *
     * @param email
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/checkEmailNotExist")
    public Result checkUserEmailNotExist(@RequestParam(value = "email", defaultValue = "") String email) {
        Result result = new Result();
        if (!"".equals(email)) {
            if (Objects.isNull(userService.getUser(email))) {
                result.setSuccess(true);
            }
        }
        return result;
    }


    /**
     * 检查输入的邮箱存在
     *
     * @param email
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/checkEmailExist")
    public Result checkUserEmailExist(@RequestParam(value = "email", defaultValue = "") String email) {
        Result result = new Result();
        if (!"".equals(email)) {
            if (!Objects.isNull(userService.getUser(email))) {
                result.setSuccess(true);
            }
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/checkPhone")
    public Result checkUserPhone(@RequestParam(value = "phone", defaultValue = "") String phone) {
        Result result = new Result();
        if (!"".equals(phone)) {
            User user = userService.getUser(phone);
            if (user == null) {
                result.setSuccess(true);
            }
        }
        return result;
    }

    /**
     * 检查用户的密码是否合法
     *
     * @param password   密码
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/checkPassword")
    public Result checkPassword(@RequestParam("password") String password) {
        return userService.isPasswordLegal(password);
    }

    /**
     * 用户登录逻辑
     *
     * @param username           用户的账号
     * @param password           密码
     * @param remember           "记住我"功能状态
     * @param redirectAttributes
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/login")
    public String login(@RequestParam(value = "username", defaultValue = "") String username,
                        @RequestParam(value = "password", defaultValue = "") String password,
                        @RequestParam(value = "remember-me", defaultValue = "") String remember,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        User user = userService.checkUser(username, EncryptUtils.getInstance().SHA1(password, "user"));
        String ip = NetWorkUtils.getUserIp(request);
        String location = NetWorkUtils.getLocation(request, ip);
        try {
            if (user != null) {
                user.setUserPassword(null);
                //防止重复登录
                if (Objects.isNull(UserUtils.getUserFromRedis(user.getUserId()))) {
                    redisUtil.hset("user-" + user.getUserId(), "user", JSONObject.toJSONString(user), RedisUtils.DB_1);
                    //会话信息，如果没有主动退出15天有效
                    redisUtil.expire("user-" + user.getUserId(), 60 * 60 * 24 * 15, RedisUtils.DB_1);
                }
                if(Objects.isNull(CookieUtils.getCookieValue(request,"USER-INFO"))) {
                    //添加用户的登录信息到Cookie中
                    CookieUtils.addCookie(request, response, "USER-INFO", JSONObject.toJSONString(user), 60 * 60 * 24 * 15);
                }
                // 保存用户名密码一个月
                if ("on".equals(remember)) {
                    Object value = CookieUtils.getCookieValue(request, "USER-COOKIE");
                    if (Objects.isNull(value)) {
                        CookieUtils.setCookie(request, response, "USER-COOKIE", username + "-" + AESCryptUtils.encryptECB(password, "1a2b3c4d5e6f7g8h"), 60 * 60 * 24 * 30);
                    }
                }
                executor.execute(() -> userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录成功")));
                // 跳转到用户登录前的页面
                String refererUrl = (String) redisUtil.get("Referer-" + NetWorkUtils.getUserIp(request), RedisUtils.DB_1);
                if (Objects.nonNull(refererUrl) && !"".equals(refererUrl)) {
                    //登录成功后删除对应的key
                    redisUtil.delete(RedisUtils.DB_1, "Referer-" + NetWorkUtils.getUserIp(request));
                    return "redirect:" + refererUrl;
                }
                return "redirect:/article/index/" + user.getUserId();
            } else {
                redirectAttributes.addFlashAttribute("error", "登录失败！用户名和密码不匹配！");
                return LOGIN_PAGE;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            executor.execute(() -> {
                if (Objects.nonNull(user)) {
                    redisUtil.delete(RedisUtils.DB_1, "user-" + user.getUserId());
                    userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录失败"));
                }
            });
            redirectAttributes.addFlashAttribute("error", "服务异常，请重试！");
            log.error(e.getMessage());
            return LOGIN_PAGE;
        }
    }

    /**
     * 退出操作
     *
     * @param userId   用户ID
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/logout")
    public Result logout(@RequestParam int userId, HttpServletRequest request, HttpServletResponse response) {
        Result result = new Result();
        result.setMessage(AJAX_ERROR);
        if (userId <= 0) {
            return result;
        }
        //退出前先删除本次登录的Cookie
        CookieUtils.deleteCookie(request, response, "USER-INFO");
        //删除Redis中保存的登录信息
        Boolean res = redisUtil.delete(RedisUtils.DB_1, "user-" + userId);
        if (res != null && res) {
            result.setSuccess(true);
            result.setMessage(AJAX_SUCCESS);
        }
        return result;
    }

    /**
     * 检查用户的登录状态
     *
     * @param userId 用户Id
     */
    @ResponseBody
    @RequestMapping(value = "/checkUserStatus")
    public Result checkUserLoginStatus(@RequestParam int userId) {
        Result result = new Result();
        result.setMessage(UNLOGIN.getStatus() + "");
        if (redisUtil.hHasKey("user-" + userId, "user", RedisUtils.DB_1)) {
            result.setMessage(JSON.toJSONString(UserUtils.getUserFromRedis(userId)));
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 找回密码
     */
    @ResponseBody
    @RequestMapping("/modifyPwd")
    public Result modifyPassword(@RequestParam("account") String account,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam(value = "code", defaultValue = "") String code) {
        Result result = new Result();
        result.setSuccess(false);
        String captchaCode = (String) redisUtil.get("captcha-code-" + account, RedisUtils.DB_1);
        if (!code.equalsIgnoreCase(captchaCode)) {
            result.setMessage("验证码错误！");
        } else if (Objects.isNull(newPassword)) {
            result.setMessage("请填写新密码！");
        } else if (Objects.isNull(account)) {
            result.setMessage("请填写您的账号！");
        } else {
            try {
                executor.execute(() -> {
                    userService.updateUserInfo(account, EncryptUtils.getInstance().SHA1(newPassword, "user"));
                });
                result.setSuccess(true);
                result.setMessage("密码修改成功，正在跳转到登录页面...");
            } catch (Exception e) {
                result.setMessage("抱歉，服务异常，请重试！");
                return result;
            } finally {
                redisUtil.delete(RedisUtils.DB_1, "captcha-code-" + account);
            }
        }
        return result;
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
    public Result settingAboutMe(@RequestParam(value = "aboutMeInfo") String aboutMeInfo,
                                 @RequestParam(defaultValue = "-1") Integer userId,
                                 HttpServletResponse response,
                                 HttpServletRequest request) {
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            user.setUserDescription(aboutMeInfo);
            try {
                int res = userService.updateUserInfo(user);
                executor.execute(() -> {
                    UserUtils.updateLoggedUserInfo(user, request, response);
                });
                if (res < 0) {
                    result.setMessage("更新异常，请重试！");
                } else {
                    result.setMessage("修改成功！");
                    result.setSuccess(true);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                result.setMessage("服务异常，请重试！");
            }

        }
        return result;
    }

    /**
     * 设置我的联系方式
     *
     * @param github  Github
     * @param wechat  微信
     * @param qq       QQ
     * @param weibo    微博
     * @param twitter  推特
     * @param steam    Steam
     * @param userId    用户Id
     */
    @ResponseBody
    @GetMapping(value = "/settingContact")
    public Result settingContact(@RequestParam String github,
                                 @RequestParam String wechat,
                                 @RequestParam String qq,
                                 @RequestParam String weibo,
                                 @RequestParam String twitter,
                                 @RequestParam String steam,
                                 @RequestParam(defaultValue = "-1") Integer userId) {
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            UserAccount account = new UserAccount(github, wechat, qq, steam, twitter, weibo, userId);
            //尝试更新用户的account
            int res = userAccountService.updateAccountByUserId(account);
            if (res <= 0) {
                //如果没有就创建一个
                res = userAccountService.createAccount(account);
            }
            if (res > 0) {
                result.setSuccess(true);
                result.setMessage("修改成功！");
            } else {
                result.setSuccess(false);
                result.setMessage("修改失败！");
            }
        }
        return result;
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
    public Result setUserHobby(@RequestParam String hobby,
                               @RequestParam(defaultValue = "-1") Integer userId,
                               HttpServletResponse response,
                               HttpServletRequest request) {
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            User userHobby = new User();
            userHobby.setUserId(user.getUserId());
            userHobby.setUserHobby(hobby);
            int res = userService.updateUserInfo(userHobby);
            User user1 = CombineBeans.combine(userHobby, user);
            executor.execute(() -> {
                UserUtils.updateLoggedUserInfo(user1, request, response);
            });
            if (res < 0) {
                result.setMessage("更新失败，请稍后重试！");
                return result;
            }
            result.setSuccess(true);
            result.setMessage("更新成功！");
        }
        return result;
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
    public Result settingTech(@RequestParam String techStr,
                              @RequestParam(defaultValue = "-1") Integer userId,
                              HttpServletResponse response,
                              HttpServletRequest request) {
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            User userTech = new User();
            userTech.setUserId(user.getUserId());
            userTech.setUserTech(techStr);
            int res = userService.updateUserInfo(userTech);
            User user1 = CombineBeans.combine(userTech, user);
            executor.execute(() -> {
                UserUtils.updateLoggedUserInfo(user1, request, response);
            });
            if (res < 0) {
                result.setMessage("修改失败！请稍后重试！");
                return result;
            }
            result.setSuccess(true);
            result.setMessage("修改成功！");
        }
        return result;
    }


}
