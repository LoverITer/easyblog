package org.easyblog.controller;

import org.easyblog.bean.User;
import org.easyblog.service.Result;
import org.easyblog.service.UserEmailLogServiceImpl;
import org.easyblog.service.UserPhoneLogServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.easyblog.utils.EncryptUtil;
import org.easyblog.utils.NetWorkUtil;
import org.easyblog.utils.SendEmailUtil;
import org.easyblog.utils.SendMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserEmailLogServiceImpl userEmailLogService;

    @Autowired
    private UserPhoneLogServiceImpl userPhoneLogService;

    @Autowired
    private SendEmailUtil emailUtil;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/toLoginPage")
    public String toLoginPage() {
        return "login";
    }


    @GetMapping("/register-success")
    public String toRegisterSuccessPage(){
        return "register_success";
    }


    @ResponseBody
    @GetMapping(value = "/captcha-code2phone")
    public String sendCaptchaCode2Phone(@RequestParam("phone") String phone, HttpSession session) {
        try {
            String code = SendMessageUtil.getRandomCode(6);
            session.setAttribute("captcha-code", code);
            log.info("向{}发送验证码:{}", phone, code);
            session.setMaxInactiveInterval(60 * 5);    //验证码5分钟有效
            String content = "您正在申请手机注册，验证码为：" + code + "，5分钟内有效！";
            SendMessageUtil.send("loveIT", "d41d8cd98f00b204e980", phone, content);
            userPhoneLogService.saveSendCaptchaCode2User(phone, content);
        } catch (Exception e) {
            userPhoneLogService.saveSendCaptchaCode2User(phone, "短信发送异常！");
            log.error("短信发送异常" + e.getMessage());
            return "FATAL";
        }
        return "OK";
    }

    @ResponseBody
    @GetMapping(value = "/captcha-code2mail")
    public String sendCaptchaCode2Email(@RequestParam("email") String Email, HttpSession session) {
        try {
            String code = SendMessageUtil.getRandomCode(6);
            if (session.getAttribute("captcha-code") != null) {
                session.removeAttribute("captcha-code");
            }
            log.info("向" + Email + "发送验证码：" + code);
            session.setAttribute("captcha-code", code);
            session.setMaxInactiveInterval(60 * 5);
            String content = "您正在申请邮箱注册，验证码为：" + code + "，5分钟内有效！";
            SendEmailUtil.Email email = new SendEmailUtil.Email("验证码", Email, content, null);
            emailUtil.send(email);
            userEmailLogService.saveSendCaptchaCode2User(Email, content);
        } catch (Exception e) {
            userEmailLogService.saveSendCaptchaCode2User(Email, "邮件发送异常！");
            log.error("邮件发送异常" + e.getMessage());
            return "FATAL";
        }
        return "OK";
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
            userService.register(nickname, EncryptUtil.getInstance().DESEncode(password,"user"), account, ip + " " + ipInfo);
            result.setSuccess(true);
            result.setMsg("注册成功");
        }
       return result;
    }

    @ResponseBody
    @GetMapping(value = "/checkNickname")
    public Result  checkUserNickname(@RequestParam("nickname") String nickname){
        Result result = new Result();
        result.setSuccess(true);
        User user = userService.getUser(nickname);
        if(user!=null){
            result.setSuccess(false);
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/checkEmail")
    public Result checkUserEmail(@RequestParam("email") String email){
        Result result = new Result();
        result.setSuccess(true);
        User user = userService.getUser(email);
        if(user!=null){
            result.setSuccess(false);
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/checkPhone")
    public Result checkUserPhone(@RequestParam("phone") String phone){
        Result result = new Result();
        result.setSuccess(true);
        User user = userService.getUser(phone);
        if(user!=null){
            result.setSuccess(false);
        }
        return result;
    }


    @ResponseBody
    @GetMapping(value = "/checkPassword")
    public Result checkPassword(@RequestParam("password") String password){
        Result result = new Result();
        result.setSuccess(true);
        if(password.length()<6||password.length()>16){
            result.setSuccess(false);
            result.setMsg("密码长度必须介于6-16个字符");
        }
        return result;
    }


    @GetMapping(value = "/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {
        return "";
    }

    public String logout(@RequestParam("username") String username,
                         @RequestParam("password") String password) {
        return "";
    }


}
