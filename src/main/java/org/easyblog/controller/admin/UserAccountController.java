package org.easyblog.controller.admin;


import org.easyblog.bean.User;
import org.easyblog.bean.UserSigninLog;
import org.easyblog.service.impl.UserServiceImpl;
import org.easyblog.service.impl.UserSigninLogServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String resetPassword() {
        return PREFIX+"account-setting-pwd";
    }


    @GetMapping(value = "/reset/phone")
    public String resetPhone1() {
        return PREFIX+"/account-setting-phone";
    }

    @GetMapping(value = "/reset/phone/next")
    public String resetPhone2() {
        return PREFIX+"/account-setting-phone-next";
    }


    @GetMapping(value = "/reset/email")
    public String resetEmail() {
        return PREFIX+"/account-setting-mail";
    }


    @GetMapping(value = "/reset/email/next")
    public String resetEmail2() {
        return PREFIX+"/account-setting-mail-next";
    }


    @GetMapping(value = "/logs")
    public String loginLog(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(user!=null){
            List<UserSigninLog> infos = userSigninLogService.getUserLoginInfo(user.getUserId(), 50);
            model.addAttribute("infos",infos);
            return PREFIX+"/account-setting-signInLog";
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
