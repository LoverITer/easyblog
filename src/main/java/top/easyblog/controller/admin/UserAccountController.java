package top.easyblog.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.easyblog.bean.User;
import top.easyblog.bean.UserSigninLog;
import top.easyblog.common.email.Email;
import top.easyblog.common.email.EmailSender;
import top.easyblog.common.util.*;
import top.easyblog.config.web.AjaxResult;
import top.easyblog.service.impl.UserServiceImpl;
import top.easyblog.service.impl.UserSigninLogServiceImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;


/***
 * 用户账户设置后台管理——修改以及查看个人信息
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/manage/account")
public class UserAccountController {

    private static final String PREFIX = "/admin/setting/";
    private static final String LOGIN_PAGE = "redirect:/user/loginPage";
    private final UserSigninLogServiceImpl userSigninLogService;
    private final UserServiceImpl userService;
    private final EmailSender emailUtil;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private Executor executor;

    public UserAccountController(UserSigninLogServiceImpl userSigninLogService, UserServiceImpl userService, EmailSender emailUtil) {
        this.userSigninLogService = userSigninLogService;
        this.userService = userService;
        this.emailUtil = emailUtil;
    }


    @GetMapping(value = "/reset/password")
    public String resetPassword(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "account-setting-pwd";
        }
        return LOGIN_PAGE;
    }


    @ResponseBody
    @GetMapping(value = "/reset/password/save")
    public AjaxResult saveResetPassword(@RequestParam Integer userId,
                                        @RequestParam String oldPwd,
                                        @RequestParam String newPwd,
                                        @RequestParam String newPwdConfirm) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage("请登录后重试");
        User user = UserUtils.getUserFromRedis(userId);
        if (user != null) {
            AjaxResult authorized = userService.isAuthorized(user, oldPwd);
            AjaxResult isSame = userService.isNewPasswordSameOldPassword(oldPwd, newPwd);
            AjaxResult passwordLegal = userService.isPasswordLegal(newPwd);
            if (authorized.isSuccess()) {
                if (!isSame.isSuccess()) {
                    if (passwordLegal.isSuccess()) {
                        if (newPwd.equals(newPwdConfirm)) {
                            User var0 = new User();
                            var0.setUserPassword(EncryptUtils.getInstance().SHA1(newPwdConfirm, "user"));
                            var0.setUserId(user.getUserId());
                            userService.updateUserInfo(var0);
                            ajaxResult.setMessage("密码修改成功！");
                            ajaxResult.setSuccess(true);
                        } else {
                            ajaxResult.setMessage("两次输入的新密码不一致");
                        }
                    } else {
                        ajaxResult.setMessage(passwordLegal.getMessage());
                    }
                } else {
                    ajaxResult.setMessage(isSame.getMessage());
                }

            } else {
                ajaxResult.setMessage(authorized.getMessage());
            }
        } else {
            ajaxResult.setMessage("请登录后再修改密码，如果忘记密码可以到登录页找回密码");
        }
        return ajaxResult;
    }


    @GetMapping(value = "/reset/phone")
    public String resetPhone(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            user.setUserPassword(null);
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "account-setting-phone";
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/reset/phone/nextPage")
    public String resetPhoneNextPage(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "account-setting-phone-next";
        }
        return LOGIN_PAGE;
    }


    @ResponseBody
    @GetMapping(value = "/sendByPhone")
    public AjaxResult sendCaptchaCodeByPhone(@RequestParam Integer userId, @RequestParam String phone) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setSuccess(false);
        String code = SendMessageUtils.getRandomCode(6);
        redisUtils.set("code-"+userId, code, RedisUtils.DB_1);
        //60s有效
        redisUtils.expire("code-"+userId, 60, RedisUtils.DB_1);
        String content = "您正在修改绑定的手机，验证码为：" + code + "，60s内有效！";
        SendMessageUtils.send("loveIT", "d41d8cd98f00b204e980", phone, content);
        ajaxResult.setSuccess(true);
        return ajaxResult;
    }

    @ResponseBody
    @GetMapping(value = "/reset/phone/next")
    public AjaxResult resetPhoneNext(@RequestParam Integer userId, @RequestParam String code) {
        User user = UserUtils.getUserFromRedis(userId);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage("请登录后再操作！");
        if (Objects.nonNull(user)) {
            if (code.equals(redisUtils.get("code-"+userId, RedisUtils.DB_1))) {
                ajaxResult.setSuccess(true);
                redisUtils.delete(1, "code-"+userId);
            } else {
                ajaxResult.setMessage("验证码输入错误！");
            }
        }
        return ajaxResult;
    }

    @GetMapping(value = "/bindPhonePage")
    public String bindPhonePage(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "account-setting-phone-add";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @GetMapping(value = "/bindPhone")
    public AjaxResult saveBindPhone(@RequestParam Integer userId,
                                    @RequestParam String phone,
                                    @RequestParam String code,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        String realCode = (String) redisUtils.get("code-"+userId, RedisUtils.DB_1);
        if (Objects.nonNull(user)) {
            if (code.equals(realCode)) {
                try {
                    user.setUserPhone(phone);
                    int res = userService.updateUserInfo(user);
                    if (res <= 0) {
                        ajaxResult.setMessage("服务异常，请重试！");
                        return ajaxResult;
                    }
                    executor.execute(() -> UserUtils.updateLoggedUserInfo(user,request,response));
                    ajaxResult.setMessage("手机号绑定成功!");
                    ajaxResult.setSuccess(true);
                    return ajaxResult;
                } catch (Exception e) {
                    ajaxResult.setMessage("服务异常，请重试！");
                }
            } else {
                ajaxResult.setMessage("验证码输入不正确");
            }
        } else {
            ajaxResult.setMessage("请登录后再操作");
        }
        return ajaxResult;
    }


    @GetMapping(value = "/reset/email")
    public String resetEmail(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            user.setUserPassword(null);
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "account-setting-mail";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @GetMapping(value = "/reset/email/next")
    public AjaxResult resetEmailNext(@RequestParam Integer userId, @RequestParam String code) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            String var0 = (String) redisUtils.get("code-" + userId, RedisUtils.DB_1);
            if (Objects.nonNull(var0)) {
                if (code.equals(var0)) {
                    redisUtils.delete(RedisUtils.DB_1, "code-" + userId);
                    ajaxResult.setSuccess(true);
                    ajaxResult.setMessage("OK");
                } else {
                    ajaxResult.setMessage("验证码输入错误，请重新输入");
                }
            } else {
                ajaxResult.setMessage("验证码已超时，请重新获取");
            }
        } else {
            redisUtils.delete(RedisUtils.DB_1, "code-" + userId);
        }
        return ajaxResult;
    }

    @ResponseBody
    @GetMapping(value = "/reset/email/save")
    public AjaxResult saveRestEmail(@RequestParam Integer userId,
                                    @RequestParam String email,
                                    HttpServletResponse response,
                                    HttpServletRequest request) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            User var0 = new User();
            var0.setUserId(user.getUserId());
            var0.setUserMail(email);
            int res = userService.updateUserInfo(var0);
            if (res > 0) {
                UserUtils.updateLoggedUserInfo(CombineBeans.combine(var0,user),request,response);
                ajaxResult.setSuccess(true);
            }
        }
        return ajaxResult;
    }

    @GetMapping(value = "/reset/email/nextPage")
    public String toResetEmailNextPage(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "account-setting-mail-next";
        }
        return LOGIN_PAGE;
    }


    @ResponseBody
    @GetMapping(value = "/sendByEmail")
    public AjaxResult sendCaptchaCodeByEmail(@RequestParam Integer userId, @RequestParam String email) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setSuccess(false);
        String code = SendMessageUtils.getRandomCode(6);
        redisUtils.set("code-"+userId,code,RedisUtils.DB_1);
        //60s有效
        redisUtils.expire("code-"+userId,60,RedisUtils.DB_1);
        String content = "您正在修改已经绑定的邮箱，验证码为：" + code + "，60秒内有效！";
        Email e = new Email("验证码", email, content, null);
        emailUtil.send(e);
        ajaxResult.setSuccess(true);
        return ajaxResult;
    }


    @GetMapping(value = "/logs")
    public String loginLog(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            List<UserSigninLog> infos = userSigninLogService.getUserLoginInfo(user.getUserId(), 50);
            model.addAttribute("infos", infos);
            return PREFIX + "account-setting-signInLog";
        }
        return LOGIN_PAGE;
    }

    @GetMapping(value = "/accountDestroy")
    public String accountDestroyPage(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "account-setting-destroy";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @GetMapping(value = "/destroy")
    public AjaxResult deleteAccount(@RequestParam Integer userId, @RequestParam String password, HttpServletRequest request) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            Cookie[] cookies = request.getCookies();
            for(Cookie cookie:cookies){
                if("USER-INFO".equalsIgnoreCase(cookie.getName())){
                    Cookie ck = new Cookie("USER-INFO", null);
                    ck.setMaxAge(0);
                    break;
                }
            }
            AjaxResult authorized = userService.isAuthorized(user, password);
            if (authorized.isSuccess()) {
                ajaxResult.setSuccess(true);
                userService.deleteUserByPK(user.getUserId());
            } else {
                ajaxResult.setMessage("密码输入错误，请重试！");
            }
        }
        return ajaxResult;
    }

}
