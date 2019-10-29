package org.easyblog.controller;

import org.easyblog.bean.User;
import org.easyblog.bean.UserSigninLog;
import org.easyblog.config.Result;
import org.easyblog.service.UserEmailLogServiceImpl;
import org.easyblog.service.UserPhoneLogServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.easyblog.service.UserSigninLogServiceImpl;
import org.easyblog.utils.EncryptUtil;
import org.easyblog.utils.NetWorkUtil;
import org.easyblog.utils.SendEmailUtil;
import org.easyblog.utils.SendMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;


@Controller
@RequestMapping("/user")
public class UserController {


    private final UserServiceImpl userService;
    private final UserEmailLogServiceImpl userEmailLogService;
    private final UserPhoneLogServiceImpl userPhoneLogService;
    private final SendEmailUtil emailUtil;
    private final UserSigninLogServiceImpl userSigninLogService;

    /***ajax异步请求成功标志***/
    private static final String AJAX_SUCCESS = "OK";
    /***ajax异步请求失败标志***/
    private static final String AJAX_ERROR = "FATAL";

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public UserController(UserServiceImpl userService, UserEmailLogServiceImpl userEmailLogService, UserPhoneLogServiceImpl userPhoneLogService, SendEmailUtil emailUtil, UserSigninLogServiceImpl userSigninLogService) {
        this.userService = userService;
        this.userEmailLogService = userEmailLogService;
        this.userPhoneLogService = userPhoneLogService;
        this.emailUtil = emailUtil;
        this.userSigninLogService = userSigninLogService;
    }

    @GetMapping("/loginPage")
    public String toLoginPage(HttpServletRequest request,HttpSession session) {
        //把用户登录前的地址存下来
        if(null==session.getAttribute("Referer")) {
            String referUrl = request.getHeader("Referer");
            session.setAttribute("Referer", referUrl);
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
            String content = "您正在申请邮箱注册，验证码为：" + code + "，5分钟内有效！";
            return sendEmail(email, content, code, session);
        } else if ("modify-pwd".equals(option)) {
            String content = "您正在通过邮箱找回密码，验证码为：" + code + "，5分钟内有效！";
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
            session.setMaxInactiveInterval(60 * 5);
            SendEmailUtil.Email e = new SendEmailUtil.Email("验证码", email, content, null);
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
    public Result register(@RequestParam("nickname") String nickname,
                           @RequestParam("pwd") String password,
                           @RequestParam("account") String account,
                           @RequestParam("code") String captchaCode,
                           HttpServletRequest request,
                           HttpSession session) {
        String captcha = (String) session.getAttribute("captcha-code");
        String ip = NetWorkUtil.getUserIp(request);
        String ipInfo = NetWorkUtil.getLocation(request, ip);
        Result result = new Result();
        result.setSuccess(false);

        User user = userService.getUser(nickname);
        if (user != null) {
            result.setMsg("昵称已存在");
        } else if (userService.getUser(account) != null) {
            result.setMsg("此邮箱已经注册了");
        } else if (userService.getUser(account) != null) {
            result.setMsg("此手机号已经注册过了");
        } else if (!captcha.equals(captchaCode)) {
            result.setMsg("验证码不正确");
        } else {
            userService.register(nickname, EncryptUtil.getInstance().DESEncode(password, "user"), account, ip + " " + ipInfo);
            result.setSuccess(true);
            result.setMsg("注册成功");
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
    @GetMapping(value = "/checkEmail")
    public Result checkUserEmail(@RequestParam(value = "email", defaultValue = "") String email) {
        Result result = new Result();
        result.setSuccess(false);
        if (!"".equals(email)) {
            User user = userService.getUser(email);
            if (user == null) {
                result.setSuccess(true);
            }
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/checkPhone")
    public Result checkUserPhone(@RequestParam(value = "phone", defaultValue = "") String phone) {
        Result result = new Result();
        result.setSuccess(true);
        if (!"".equals(phone)) {
            User user = userService.getUser(phone);
            if (user != null) {
                result.setSuccess(false);
            }
        }
        return result;
    }


    @ResponseBody
    @GetMapping(value = "/checkPassword")
    public Result checkPassword(@RequestParam("password") String password) {
        Result result = new Result();
        result.setSuccess(true);
        if (password.length() < 6 || password.length() > 16) {
            result.setSuccess(false);
            result.setMsg("密码长度必须介于6-16个字符");
        }
        return result;
    }


    @RequestMapping(value = "/login")
    public String login(@RequestParam(value = "username", defaultValue = "") String username,
                        @RequestParam(value = "password", defaultValue = "") String password,
                        @RequestParam(value = "remember", defaultValue = "") String remember,
                        HttpSession session,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        User user = userService.checkUser(username, EncryptUtil.getInstance().DESEncode(password, "user"));
        String ip = NetWorkUtil.getUserIp(request);
        String location = NetWorkUtil.getLocation(request, ip);
        try {
            if (user != null) {
                user.setUserPassword(null);   //不要把用户密码带到前端页面
                user.setUserPower(null);
                session.setAttribute("LOGIN-USER", user);
                session.setAttribute("user",user);
                session.setMaxInactiveInterval(60 * 60 * 24 * 10);   //登录信息10天有效
                // 保存登录状态
                Cookie ck = new Cookie("JSESSIONID", request.getSession().getId());
                ck.setPath("/");
                ck.setMaxAge(30);
                response.setHeader("JSESSIONID", ck.getValue());
                new Thread(() -> {
                    userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录成功"));
                }).start();
                //得到用户登录前的页面
                String refererUrl= (String) session.getAttribute("Referer");
                System.out.println(refererUrl);
                if(Objects.nonNull(refererUrl)&&!"".equals(refererUrl)){
                    session.removeAttribute("Referer");
                    return "redirect:"+refererUrl;
                }
                return "redirect:/article/index/"+user.getUserId();
            } else {
                redirectAttributes.addFlashAttribute("msg", "用户名和密码不正确！");
                return "redirect:/user/loginPage";
            }
        }catch (Exception e){
            session.removeAttribute("LOGIN-USER");
            new Thread(() -> {
                if(Objects.nonNull(user)) {
                    userSigninLogService.saveSigninLog(new UserSigninLog(user.getUserId(), ip, location, "登录失败"));
                }
            }).start();
            redirectAttributes.addFlashAttribute("msg", "服务异常，请重试！");
            return "redirect:/user/loginPage";
        }
    }


    @ResponseBody
    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("LOGIN-USER");
        return AJAX_SUCCESS;
    }


    @ResponseBody
    @RequestMapping(value = "/checkUserStatus")
    public int checkUserLoginStatus(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("LOGIN-USER");
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
        if (code.equals(session.getAttribute("captcha-code"))&&
                Objects.nonNull(newPassword)&&
                Objects.nonNull(account)) {
            try {
                new Thread(()->{
                    userService.updateUserInfo(account, EncryptUtil.getInstance().DESEncode(newPassword, "user"));
                }).start();
                result.setSuccess(true);
                result.setMsg("密码修改成功，正在跳转到登录页面...");
                return result;
            } catch (Exception e) {
                result.setMsg("抱歉，服务异常，请重试！");
                result.setSuccess(false);
                return result;
            } finally {
                session.removeAttribute("captcha-code");
            }
        }
        result.setSuccess(false);
        result.setMsg("验证码不正确");
        return result;
    }

}
