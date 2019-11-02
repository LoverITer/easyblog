package org.easyblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/***
 * 用户账户设置后台管理——修改以及查看个人信息
 */
@Controller
@RequestMapping(value = "/manage/account")
public class UserAccountController {

    private static final String PREFIX="/admin/setting/";

    @GetMapping(value = "/help")
    public String help() {
        return "help";
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


    @GetMapping(value = "/loginLog")
    public String loginLog() {
        return PREFIX+"/account-setting-signInLog";
    }


}
