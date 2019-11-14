package org.easyblog.controller.admin;


import org.easyblog.bean.User;
import org.easyblog.bean.UserSigninLog;
import org.easyblog.config.Result;
import org.easyblog.service.impl.UserServiceImpl;
import org.easyblog.service.impl.UserSigninLogServiceImpl;
import org.easyblog.utils.email.Email;
import org.easyblog.utils.EncryptUtil;
import org.easyblog.utils.email.SendEmailUtil;
import org.easyblog.utils.SendMessageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;


/***
 * 用户账户设置后台管理——修改以及查看个人信息
 */
@Controller
@RequestMapping(value = "/manage/account")
public class UserAccountController {

    private static final String PREFIX="/admin/setting/";
    private static final String LOGIN_PAGE = "redirect:/user/loginPage";
    private final UserSigninLogServiceImpl userSigninLogService;
    private final UserServiceImpl userService;
    private final SendEmailUtil emailUtil;

    public UserAccountController(UserSigninLogServiceImpl userSigninLogService, UserServiceImpl userService, SendEmailUtil emailUtil) {
        this.userSigninLogService = userSigninLogService;
        this.userService = userService;
        this.emailUtil = emailUtil;
    }


    @GetMapping(value = "/reset/password")
    public String resetPassword(HttpSession session) {
        if(session.getAttribute("user")!=null) {
            return PREFIX + "account-setting-pwd";
        }
        return LOGIN_PAGE;
    }


    @ResponseBody
    @GetMapping(value = "/reset/password/save")
    public Result saveResetPassword(HttpSession session,
                                    @RequestParam String oldPwd,
                                    @RequestParam String newPwd,
                                    @RequestParam String newPwdConfirm){
        Result result = new Result();
        result.setSuccess(false);
        User user = (User) session.getAttribute("user");
        if(user!=null){
            Result authorized = userService.isAuthorized(user, oldPwd);
            Result isSame = userService.isNewPasswordSameOldPassword(oldPwd, newPwd);
            Result passwordLegal = userService.isPasswordLegal(newPwd);
            if(authorized.isSuccess()) {
                if (!isSame.isSuccess()) {
                    if (passwordLegal.isSuccess()) {
                        if (newPwd.equals(newPwdConfirm)) {
                            User var0 = new User();
                            var0.setUserPassword(EncryptUtil.getInstance().DESEncode(newPwdConfirm, "user"));
                            var0.setUserId(user.getUserId());
                            userService.updateUserInfo(var0);
                            result.setMsg("密码修改成功！");
                            result.setSuccess(true);
                        } else {
                            result.setMsg("两次输入的新密码不一致");
                        }
                    } else {
                        result.setMsg(passwordLegal.getMsg());
                    }
                } else {
                    result.setMsg(isSame.getMsg());
                }

            }else{
                result.setMsg(authorized.getMsg());
            }
        }else{
            result.setMsg("请登录后再修改密码，如果忘记密码可以到登录页找回密码");
        }
        return result;
    }


    @GetMapping(value = "/reset/phone")
    public String resetPhone(HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            user.setUserPassword(null);
            model.addAttribute("user",user);
            return PREFIX+"account-setting-phone";
        }
        return LOGIN_PAGE;
    }

    @GetMapping(value = "/reset/phone/next")
    public String resetPhoneNext(HttpSession session,Model model) {
        return PREFIX+"account-setting-phone-next";
    }


    @ResponseBody
    @GetMapping(value = "/sendByPhone")
    public Result sendCaptchaCodeByPhone(HttpSession session,@RequestParam String phone){
        Result result = new Result();
        result.setSuccess(false);
        String code = SendMessageUtil.getRandomCode(6);
        session.setAttribute("code",code);
        session.setMaxInactiveInterval(60);     //60s有效
        String content = "您正在修改绑定的手机，验证码为：" + code + "，60s内有效！";
        SendMessageUtil.send("loveIT", "d41d8cd98f00b204e980", phone, content);
        result.setSuccess(true);
        return result;
    }


    @GetMapping(value = "/reset/email")
    public String resetEmail(HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            user.setUserPassword(null);
            model.addAttribute("user",user);
            return PREFIX+"account-setting-mail";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @GetMapping(value = "/reset/email/next")
    public Result resetEmailNext(HttpSession session,@RequestParam String code) {
        Result result = new Result();
        result.setSuccess(false);
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)) {
            String var0 = (String) session.getAttribute("code");
            if (Objects.nonNull(var0)) {
                if(code.equals(var0)) {
                    session.removeAttribute("code");
                    result.setSuccess(true);
                    result.setMsg("OK");
                }else {
                    result.setMsg("验证码输入错误，请重新输入");
                }
            }else {
                result.setMsg("验证码已超时，请重新获取");
            }
        }else {
            session.removeAttribute("code");
        }
        return result;
    }

    @ResponseBody
    @GetMapping(value = "/reset/email/save")
    public Result saveRestEmail(HttpSession session,String email){
        Result result = new Result();
        result.setSuccess(false);
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            User var0 = new User();
            var0.setUserId(user.getUserId());
            var0.setUserMail(email);
            int res = userService.updateUserInfo(var0);
            if(res>0){
                result.setSuccess(true);
            }
        }
        return result;
    }

    @GetMapping(value = "/reset/email/nextPage")
    public String toResetEmailNextPage(){
        return PREFIX+"account-setting-mail-next";
    }

    @ResponseBody
    @GetMapping(value = "/sendByEmail")
    public Result sendCaptchaCodeByEmail(HttpSession session,@RequestParam String email){
        Result result = new Result();
        result.setSuccess(false);
        String code = SendMessageUtil.getRandomCode(6);
        session.setAttribute("code",code);
        session.setMaxInactiveInterval(60);     //60s有效
        String content = "您正在修改已经绑定的邮箱，验证码为：" + code + "，60秒内有效！";
        Email e=new Email("验证码",email,content,null);
        emailUtil.send(e);
        result.setSuccess(true);
        return result;
    }


    @GetMapping(value = "/logs")
    public String loginLog(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(user!=null){
            List<UserSigninLog> infos = userSigninLogService.getUserLoginInfo(user.getUserId(), 50);
            model.addAttribute("infos",infos);
            return PREFIX+"account-setting-signInLog";
        }
       return LOGIN_PAGE;
    }

    @GetMapping(value="/accountDestruction")
    public String accountDestruction(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){

        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }
}
