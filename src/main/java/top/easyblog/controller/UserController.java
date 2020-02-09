package top.easyblog.controller;

import com.github.pagehelper.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.easyblog.bean.User;
import top.easyblog.bean.UserAccount;
import top.easyblog.bean.UserLoginStatus;
import top.easyblog.bean.UserSigninLog;
import top.easyblog.commons.email.Email;
import top.easyblog.commons.email.SendEmailUtil;
import top.easyblog.commons.utils.AESCrypt;
import top.easyblog.commons.utils.EncryptUtil;
import top.easyblog.commons.utils.NetWorkUtil;
import top.easyblog.commons.utils.SendMessageUtil;
import top.easyblog.config.web.Result;
import top.easyblog.service.IUserAccountService;
import top.easyblog.service.impl.UserEmailLogServiceImpl;
import top.easyblog.service.impl.UserPhoneLogServiceImpl;
import top.easyblog.service.impl.UserServiceImpl;
import top.easyblog.service.impl.UserSigninLogServiceImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;


@Controller
@RequestMapping("/user")
public class UserController {


    private final UserServiceImpl userService;
    private final UserEmailLogServiceImpl userEmailLogService;
    private final UserPhoneLogServiceImpl userPhoneLogService;
    private final SendEmailUtil emailUtil;
    private final UserSigninLogServiceImpl userSigninLogService;
    private final IUserAccountService userAccountService;

    /***ajax异步请求成功标志***/
    private static final String AJAX_SUCCESS = "OK";
    /***ajax异步请求失败标志***/
    private static final String AJAX_ERROR = "FATAL";

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public UserController(UserServiceImpl userService, UserEmailLogServiceImpl userEmailLogService, UserPhoneLogServiceImpl userPhoneLogService, SendEmailUtil emailUtil, UserSigninLogServiceImpl userSigninLogService, IUserAccountService userAccountService) {
        this.userService = userService;
        this.userEmailLogService = userEmailLogService;
        this.userPhoneLogService = userPhoneLogService;
        this.emailUtil = emailUtil;
        this.userSigninLogService = userSigninLogService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/loginPage")
    public String toLoginPage(HttpServletRequest request, HttpSession session) {
        //把用户登录前的地址存下来
        if (null == session.getAttribute("Referer")) {
            String referUrl = request.getHeader("Referer");
            String baseUrl = request.getScheme()    //当前链接使用的协议
                    + "://" + request.getServerName()   //服务器地址
                    + ":" + request.getServerPort()   //端口号
                    + request.getContextPath() + "/";
            if (StringUtil.isNotEmpty(referUrl) &&
                    !referUrl.contains("/login") &&
                    !referUrl.contains("register") &&
                    !referUrl.contains("loginPage") &&
                    !referUrl.contains("change_password") &&
                    !referUrl.contains("/article/index") &&
                    !referUrl.equalsIgnoreCase(baseUrl)) {
                session.setAttribute("Referer", referUrl);
            }
        }
        return "login";
    }

    @GetMapping("/register-success")
    public String toRegisterSuccessPage() {
        return "register_success";
    }

    @GetMapping("/change_password")
    public String toChangePassword() {
        return "change_password";
    }

    @ResponseBody
    @GetMapping(value = "/captcha-code2phone")
    public String sendCaptchaCode2Phone(@RequestParam("phone") String phone,
                                        @RequestParam("option") String option,
                                        HttpSession session) {

        String code = SendMessageUtil.getRandomCode(6);
        if ("register".equals(option)) {
            String content = "您正在申请手机注册，验证码为：" + code + "，5分钟内有效！";
            return sendMessage(phone, code, content, session);
        } else if ("modify-pwd".equals(option)) {
            String content = "您正在通过手机找回密码，验证码为：" + code + "，5分钟内有效！";
            return sendMessage(phone, code, content, session);
        }
        return AJAX_ERROR;

    }

    private String sendMessage(String phone, String code, String content, HttpSession session) {

        try {
            session.setAttribute("captcha-code", code);
            log.info("向{}发送验证码:{}", phone, code);
            session.setMaxInactiveInterval(60 * 5);    //验证码5分钟有效
            SendMessageUtil.send("loveIT", "d41d8cd98f00b204e980", phone, content);
            userPhoneLogService.saveSendCaptchaCode2User(phone, content);
        } catch (Exception e) {
            userPhoneLogService.saveSendCaptchaCode2User(phone, "短信发送异常！");
            log.error("短信发送异常" + e.getMessage());
            return AJAX_ERROR;
        }
        return AJAX_SUCCESS;
    }

    @ResponseBody
    @GetMapping(value = "/captcha-code2mail")
    public String sendCaptchaCode2Email(@RequestParam("email") String email,
                                        @RequestParam("option") String option,
                                        HttpSession session) {
        String code = SendMessageUtil.getRandomCode(6);
        System.out.println(option);
        if ("register".equals(option)) {
            String content = "您正在申请邮箱注册，验证码为：" + code + "，60秒内有效！";
            return sendEmail(email, content, code, session);
        } else if ("modify-pwd".equals(option)) {
            String content = "您正在通过邮箱找回密码，验证码为：" + code + "，60秒内有效！";
            return sendEmail(email, content, code, session);
        }
        return AJAX_SUCCESS;
    }

    private String sendEmail(String email, String content, String code, HttpSession session) {
        try {
            if (session.getAttribute("captcha-code") != null) {
                session.removeAttribute("captcha-code");
            }
            log.info("向" + email + "发送验证码：" + code);
            session.setAttribute("captcha-code", code);
            session.setMaxInactiveInterval(60);
            Email e = new Email("验证码", email, content, null);
            emailUtil.send(e);
            userEmailLogService.saveSendCaptchaCode2User(email, content);
        } catch (Exception e) {
            userEmailLogService.saveSendCaptchaCode2User(email, "邮件发送异常！");
            log.error("邮件发送异常" + e.getMessage());
            return AJAX_ERROR;
        }
        return AJAX_SUCCESS;
    }


    @ResponseBody
    @RequestMapping(value = "/register")
    public Result register(@RequestParam(value = "nickname", defaultValue = "") String nickname,
                           @RequestParam(value = "pwd", defaultValue = "") String password,
                           @RequestParam(value = "account", defaultValue = "") String account,
                           @RequestParam(value = "code", defaultValue = "") String captchaCode,
                           HttpServletRequest request,
                           HttpSession session) {
        String captcha = (String) session.getAttribute("captcha-code");
        String ip = NetWorkUtil.getUserIp(request);
        String ipInfo = NetWorkUtil.getLocation(request, ip);
        Result result = new Result();
        result.setSuccess(false);
        if (userService.getUser(nickname) != null) {
            result.setMessage("昵称已存在!");
        } else if (userService.getUser(account) != null) {
            result.setMessage("此邮箱/手机号已经注册了!");
        } else if (Objects.isNull(captcha) || !captcha.equals(captchaCode)) {
            result.setMessage("验证码填写不正确或验证码已过期!");
        } else {
            try {
                int userId = userService.register(nickname, EncryptUtil.getInstance().SHA1(password, "user"), account, ip + " " + ipInfo);
                if(userId>0) {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setAccountUser(userId);
                    int res = userAccountService.createAccount(userAccount);
                    if(res>0) {
                        result.setSuccess(true);
                        result.setMessage("注册成功!");
                        log.info("用户：{}注册成功,{}", account, new Date());
                    }else{
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

    @ResponseBody
    @GetMapping(value = "/checkEmailNotExist")
    public Result checkUserEmailNotExist(@RequestParam(value = "email", defaultValue = "") String email) {
        Result result = new Result();
        result.setSuccess(false);
        if (!"".equals(email)) {
            if (Objects.isNull(userService.getUser(email))) {
                result.setSuccess(true);
            }
        }
        return result;
    }


    @ResponseBody
    @GetMapping(value = "/checkEmailExist")
    public Result checkUserEmailExist(@RequestParam(value = "email", defaultValue = "") String email) {
        Result result = new Result();
        result.setSuccess(false);
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
        result.setSuccess(false);
        if (!"".equals(phone)) {
            User user = userService.getUser(phone);
            if (user == null) {
                result.setSuccess(true);
            }
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/checkPassword")
    public Result checkPassword(@RequestParam("password") String password) {
        Result result = new Result();
        result.setSuccess(true);
        result = userService.isPasswordLegal(password);
        return result;
    }


    @RequestMapping(value = "/login")
    public String login(@RequestParam(value = "username", defaultValue = "") String username,
                        @RequestParam(value = "password", defaultValue = "") String password,
                        @RequestParam(value = "remember-me", defaultValue = "") String remember,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        User user = userService.checkUser(username, EncryptUtil.getInstance().SHA1(password, "user"));
        String ip = NetWorkUtil.getUserIp(request);
        String location = NetWorkUtil.getLocation(request, ip);
        HttpSession session = request.getSession();
        try {
            if (user != null) {
                user.setUserPassword(null);
                //会话信息，如果没有退出15天有效
                if (Objects.isNull(session.getAttribute("user"))) {
                    session.setAttribute("user", user);
                    session.setMaxInactiveInterval(60 * 60 * 24 * 15);
                }
                // 保存用户名密码一个月
                if ("on".equals(remember)) {
                    boolean newCookie = true;   //是否创建一个新的Cookie
                    Cookie[] cookies = request.getCookies();
                    for (Cookie ck : cookies) {
                        if ("USER-COOKIE".equals(ck.getName())) {
                            newCookie = false;
                        }
                    }
                    if (newCookie) {
                        //使用AES对密码进行加密保存 :用户名-密码密文
                        Cookie ck = new Cookie("USER-COOKIE", username + "-" + AESCrypt.encryptECB(password, "1a2b3c4d5e6f7g8h"));
                        ck.setMaxAge(60 * 60 * 24 * 30);
                        ck.setPath("/");
                        // addCookie后，如果已经存在相同名字的cookie，则最新的覆盖旧的cookie
                        response.addCookie(ck);
                    }
                }
                new Thread(() -> userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录成功"))).start();
                // 跳转到用户登录前的页面
                String refererUrl = (String) session.getAttribute("Referer");
                if (Objects.nonNull(refererUrl) && !"".equals(refererUrl)) {
                    session.removeAttribute("Referer");
                    return "redirect:" + refererUrl;
                }
                return "redirect:/article/index/" + user.getUserId();
            } else {
                redirectAttributes.addFlashAttribute("error", "抱歉！用户名和密码不匹配！");
                return "redirect:/user/loginPage";
            }
        } catch (Exception e) {
            session.removeAttribute("user");
            new Thread(() -> {
                if (Objects.nonNull(user)) {
                    userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录失败"));
                }
            }).start();
            redirectAttributes.addFlashAttribute("error", "服务异常，请重试！");
            return "redirect:/user/loginPage";
        }
    }


    @ResponseBody
    @RequestMapping(value = "/logout")
    public Result logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.removeAttribute("user");
        }
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage(AJAX_SUCCESS);
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/checkUserStatus")
    public int checkUserLoginStatus(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.nonNull(user)) {
            return UserLoginStatus.LOGIN.getStatus();
        }
        return UserLoginStatus.UNLOGIN.getStatus();
    }

    /**
     * 找回密码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/modifyPwd")
    public Result modifyPassword(@RequestParam("account") String account,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam(value = "code", defaultValue = "") String code,
                                 HttpSession session) {
        Result result = new Result();
        result.setSuccess(false);
        if (!code.equals(session.getAttribute("captcha-code"))) {
            result.setMessage("验证码错误！");
        } else if (Objects.isNull(newPassword)) {
            result.setMessage("请填写新密码！");
        } else if (Objects.isNull(account)) {
            result.setMessage("请填写您的账号！");
        } else {
            try {
                new Thread(() -> userService.updateUserInfo(account, EncryptUtil.getInstance().SHA1(newPassword, "user"))).start();
                result.setSuccess(true);
                result.setMessage("密码修改成功，正在跳转到登录页面...");
            } catch (Exception e) {
                result.setMessage("抱歉，服务异常，请重试！");
                return result;
            } finally {
                session.removeAttribute("captcha-code");
            }
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/aboutMe")
    public Result settingAboutMe(@RequestParam(value = "aboutMeInfo") String aboutMeInfo,
                                HttpSession session) {
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            user.setUserDescription(aboutMeInfo);
            try {
                int res = userService.updateUserInfo(user);
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

    @ResponseBody
    @GetMapping(value = "/settingContact")
    public Result settingContact(@RequestParam String github,
                                 @RequestParam String wechat,
                                 @RequestParam String qq,
                                 @RequestParam String weibo,
                                 @RequestParam String twitter,
                                 @RequestParam String steam,
                                 HttpSession session) {
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
             UserAccount account = new UserAccount(github, wechat, qq, steam, twitter, weibo, user.getUserId());
             //检查用户的account是否存在
            int res=userAccountService.updateAccountByUserId(account);
            if (res<=0) {
                //如果没有就创建一个
               res=userAccountService.createAccount(account);
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

    @ResponseBody
    @GetMapping(value = "/settingHobby")
    public Result setUserHobby(@RequestParam String hobby, HttpSession session){
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            User userHobby = new User();
            userHobby.setUserId(user.getUserId());
            userHobby.setUserHobby(hobby);
            int res = userService.updateUserInfo(userHobby);
            if(res<0){
                result.setMessage("更新失败，请稍后重试！");
                return result;
            }
            result.setSuccess(true);
            result.setMessage("更新成功！");
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/settingTech")
    public Result settingTech(HttpSession session,@RequestParam String techStr){
        Result result = new Result();
        result.setMessage("请登录后重试！");
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            User userTech = new User();
            userTech.setUserId(user.getUserId());
            userTech.setUserTech(techStr);
            int res = userService.updateUserInfo(userTech);
            if(res<0){
                result.setMessage("修改失败！请稍后重试！");
                return result;
            }
            result.setSuccess(true);
            result.setMessage("修改成功！");
        }
        return result;
    }


}
