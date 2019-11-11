package org.easyblog.controller.admin;

import org.easyblog.bean.User;
import org.easyblog.bean.UserAttention;
import org.easyblog.config.Result;
import org.easyblog.service.impl.UserAttentionImpl;
import org.easyblog.service.impl.UserServiceImpl;
import org.easyblog.utils.CalendarUtil;
import org.easyblog.utils.UserProfessionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "/manage/uc")
public class UserCenterController {

    private static final String PREFIX = "admin/personalInfo";
    private static final String LOGIN_PAGE = "redirect:/user/loginPage";
    private final UserServiceImpl userService;
    private final UserAttentionImpl userAttentionService;

    public UserCenterController(UserServiceImpl userService, UserAttentionImpl userAttentionService) {
        this.userService = userService;
        this.userAttentionService = userAttentionService;
    }

    @GetMapping(value = "/profile")
    public String center(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            model.addAttribute("user", userService.getUser(user.getUserId()));
            String[] address = user.getUserAddress().split(",");
            model.addAttribute("country", address[0]);
            model.addAttribute("city", address[1]);
            model.addAttribute("county", address[2]);
            model.addAttribute("profile",1);
            return PREFIX + "/personal-center";
        }
        return LOGIN_PAGE;
    }

    @PostMapping(value = "/updateInfo")
    public String updateUserInfo(HttpSession session, User user,
                                 @RequestParam(value = "birthday", required = false) String birthday,
                                 @RequestParam(required = false) String country,
                                 @RequestParam(required = false) String city,
                                 @RequestParam(required = false) String county) {
        User user1 = (User) session.getAttribute("user");
        if (Objects.nonNull(user1)) {
            if (Objects.nonNull(user)) {
                if (Objects.nonNull(birthday)) {
                    user.setUserBirthday(CalendarUtil.getInstance().getDate(birthday));
                }
                String address = country + "," + city + "," + county;
                user.setUserAddress(address);
                user.setUserId(user1.getUserId());
                //页面提交过来的是用户的职业代码，具体实啥职业需要转码
                user.setUserPrefession(UserProfessionUtil.getUserProfession(user.getUserPrefession()));
                userService.updateUserInfo(user);
            }
            return "redirect:/manage/uc/profile";
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/follow-list")
    public String care(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            UserAttention var0 = new UserAttention();
            var0.setAttentionId(user.getUserId());
            List<UserAttention> attentionInfo = userAttentionService.getAllUserAttentionInfo(var0);
            model.addAttribute("attentionInfo",attentionInfo);
            model.addAttribute("follow-list",1);
            return PREFIX + "/personal-care";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @RequestMapping(value = "/cancelAttention")
    public Result cancelAttention(HttpSession session,@RequestParam(value = "attentionId") int attentionId){
        User user = (User) session.getAttribute("user");
        Result result = new Result();
        result.setSuccess(false);
        if(Objects.nonNull(user)){
            int res = userAttentionService.deleteByPK(attentionId);
            if(res>0){
                result.setSuccess(true);
                result.setMsg("OK");
            }else{
                result.setMsg("服务异常，请重试！");
            }
        }
        return result;
    }


    @GetMapping(value = "/fans-list")
    public String fans(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            UserAttention var1 = new UserAttention();
            var1.setUserId(user.getUserId());
            final List<UserAttention> attentionInfo = userAttentionService.getAllUserAttentionInfo(var1);
            model.addAttribute("attentionInfo",attentionInfo);
            model.addAttribute("fans-list",1);
            return PREFIX + "/personal-follower";
        }
        return LOGIN_PAGE;
    }


}
