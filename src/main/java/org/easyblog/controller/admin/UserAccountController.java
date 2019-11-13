package org.easyblog.controller.admin;


import org.easyblog.bean.User;
import org.easyblog.bean.UserSigninLog;
import org.easyblog.config.Result;
import org.easyblog.service.impl.UserServiceImpl;
import org.easyblog.service.impl.UserSigninLogServiceImpl;
import org.easyblog.utils.EncryptUtil;
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

    public UserAccountController(UserSigninLogServiceImpl userSigninLogService, UserServiceImpl userService) {
        this.userSigninLogService = userSigninLogService;
        this.userService = userService;
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


    @GetMapping(value = "/reset/email")
    public String resetEmail(HttpSession session,Model model) { User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            user.setUserPassword(null);
            model.addAttribute("user",user);
            return PREFIX+"account-setting-mail";
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/reset/email/next")
    public String resetEmailNext() {
        return PREFIX+"/account-setting-mail-next";
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
