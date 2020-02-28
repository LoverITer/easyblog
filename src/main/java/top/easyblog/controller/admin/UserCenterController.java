package top.easyblog.controller.admin;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.easyblog.autoconfig.qiniu.QiNiuCloudService;
import top.easyblog.bean.User;
import top.easyblog.bean.UserAttention;
import top.easyblog.commons.utils.CalendarUtil;
import top.easyblog.commons.utils.UserProfessionUtil;
import top.easyblog.config.web.Result;
import top.easyblog.service.impl.UserAttentionImpl;
import top.easyblog.service.impl.UserServiceImpl;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/manage/uc")
public class UserCenterController {

    private final String PREFIX = "admin/personalInfo";
    private final String LOGIN_PAGE = "redirect:/user/loginPage";
    private final UserServiceImpl userService;
    private final UserAttentionImpl userAttentionService;
    private final QiNiuCloudService qiNiuCloudService;

    public UserCenterController(UserServiceImpl userService, UserAttentionImpl userAttentionService, QiNiuCloudService qiNiuCloudService) {
        this.userService = userService;
        this.userAttentionService = userAttentionService;
        this.qiNiuCloudService = qiNiuCloudService;
    }

    @GetMapping(value = "/profile")
    public String center(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            model.addAttribute("user", userService.getUser(user.getUserId()));
            String str = user.getUserAddress();
            if (Objects.nonNull(str) && !"".equals(str)) {
                String[] address = str.split(",");
                model.addAttribute("country", address[0]);
                model.addAttribute("city", address[1]);
                model.addAttribute("county", address[2]);
            }
            model.addAttribute("profile", 1);
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
                //页面提交过来的是用户的职业的代号，具体实啥职业需要转码
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
        if (Objects.nonNull(user)) {
            UserAttention var0 = new UserAttention();
            var0.setAttentionId(user.getUserId());
            List<UserAttention> attentionInfo = userAttentionService.getAllUserAttentionInfo(var0);
            model.addAttribute("attentionInfo", attentionInfo);
            model.addAttribute("follow-list", 1);
            return PREFIX + "/personal-care";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @RequestMapping(value = "/cancelAttention")
    public Result cancelAttention(HttpSession session, @RequestParam(value = "attentionId") int attentionId) {
        User user = (User) session.getAttribute("user");
        Result result = new Result();
        result.setSuccess(false);
        if (Objects.nonNull(user)) {
            int res = userAttentionService.deleteByPK(attentionId);
            if (res > 0) {
                result.setSuccess(true);
                result.setMessage("OK");
            } else {
                result.setMessage("服务异常，请重试！");
            }
        }
        return result;
    }


    @GetMapping(value = "/fans-list")
    public String fans(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            UserAttention var1 = new UserAttention();
            var1.setUserId(user.getUserId());
            List<UserAttention> attentionInfo = userAttentionService.getAllUserAttentionInfo(var1);
            model.addAttribute("attentionInfo", attentionInfo);
            model.addAttribute("fans-list", 1);
            return PREFIX + "/personal-follower";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @PostMapping(value = "/uploadImg", produces = "application/json;charset=UTF-8")
    public Result changeHeaderImage(@RequestBody Map<String, String> map, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Result result = new Result();
        result.setSuccess(false);
        if (Objects.nonNull(user)) {
            try {
                String image = map.get("image");
                String imageBytes = image.substring(image.indexOf(",") + 1);
                Base64 base64Decoder = new Base64();
                //转码得到base64的字节码
                byte[] bytes = base64Decoder.decode(imageBytes);
                //把图片保存到七牛云上，并返回图片的URL
                String imageUrl = qiNiuCloudService.putBase64Image(bytes, null);
                //把图片的URL保存到数据库中
                User var0 = new User();
                var0.setUserId(user.getUserId());
                var0.setUserHeaderImgUrl(imageUrl);
                userService.updateUserInfo(var0);
                System.out.println(user.getUserHeaderImgUrl());
                if (!user.getUserHeaderImgUrl().contains("static")) {
                    qiNiuCloudService.delete(user.getUserHeaderImgUrl());
                    user.setUserHeaderImgUrl(imageUrl);
                }
                result.setSuccess(true);
                result.setMessage("头像上传成功");
            } catch (Exception e) {
                e.printStackTrace();
                result.setMessage("服务异常，请重试！");
            }
        }
        return result;
    }

}
