package org.easyblog.controller;

import org.easyblog.service.UserEmailLogServiceImpl;
import org.easyblog.service.UserPhoneLogServiceImpl;
import org.easyblog.service.UserServiceImpl;
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

    @RequestMapping(value = "/register")
    public String register(@RequestParam("user-nickname") String nickname,
                           @RequestParam("user-pwd") String password,
                           @RequestParam("account") String account,
                           @RequestParam("captcha-code") String captchaCode) {


        return "";
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
