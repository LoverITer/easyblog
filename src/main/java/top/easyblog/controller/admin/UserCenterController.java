package top.easyblog.controller.admin;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.controller.BaseController;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserAttention;
import top.easyblog.util.CalendarUtils;
import top.easyblog.util.CombineBeans;
import top.easyblog.util.UserProfessionUtils;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/manage/uc")
public class UserCenterController extends BaseController {

    private final String PREFIX = "admin/personalInfo";


    @GetMapping(value = "/profile")
    public String center(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
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

    @PostMapping(value = "/updateInfo/{userId}")
    public String updateUserInfo(User user,
                                 @PathVariable Integer userId,
                                 @RequestParam(value = "birthday", required = false) String birthday,
                                 @RequestParam(required = false) String country,
                                 @RequestParam(required = false) String city,
                                 @RequestParam(required = false) String county,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        User user1 = null;
        if (Objects.nonNull(user1 = UserUtils.getUserFromRedis(userId))) {
            if (Objects.nonNull(user)) {
                if (Objects.nonNull(birthday)) {
                    user.setUserBirthday(CalendarUtils.getInstance().getDate(birthday));
                }
                String address = country + "," + city + "," + county;
                user.setUserAddress(address);
                user.setUserId(user1.getUserId());
                //页面提交过来的是用户的职业的代号，具体职业需要转码
                user.setUserPrefession(UserProfessionUtils.getUserProfession(user.getUserPrefession()));
                userService.updateUserInfo(user);
                User user2 = CombineBeans.combine(user, user1);
                executor.execute(() -> UserUtils.updateLoggedUserInfo(user2, request, response));
            }
            return "redirect:/manage/uc/profile?userId=" + userId;
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/follow-list")
    public String care(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            UserAttention var0 = new UserAttention();
            var0.setAttentionId(user.getUserId());
            List<UserAttention> attentionInfo = userAttention.getAllUserAttentionInfo(var0);
            model.addAttribute("attentionInfo", attentionInfo);
            model.addAttribute("follow-list", 1);
            return PREFIX + "/personal-care";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @RequestMapping(value = "/cancelAttention")
    public WebAjaxResult cancelAttention(@RequestParam Integer userId, @RequestParam(value = "attentionId") int attentionId) {
        User user = UserUtils.getUserFromRedis(userId);
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        if (Objects.nonNull(user)) {
            int res = userAttention.deleteByPK(attentionId);
            if (res > 0) {
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("OK");
            } else {
                ajaxResult.setMessage("服务异常，请重试！");
            }
        }
        return ajaxResult;
    }


    @GetMapping(value = "/fans-list")
    public String fans(@RequestParam Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            UserAttention var1 = new UserAttention();
            var1.setUserId(user.getUserId());
            List<UserAttention> attentionInfo = userAttention.getAllUserAttentionInfo(var1);
            model.addAttribute("attentionInfo", attentionInfo);
            model.addAttribute("fans-list", 1);
            return PREFIX + "/personal-follower";
        }
        return LOGIN_PAGE;
    }

    /**
     * 更换用户头像
     *
     * @param map
     * @param userId   用户Id
     * @param request
     * @param response
     */
    @ResponseBody
    @PostMapping(value = "/uploadImg", produces = "application/json;charset=UTF-8")
    public WebAjaxResult changeHeaderImage(@RequestBody Map<String, String> map,
                                           @RequestParam Integer userId,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        User user = UserUtils.getUserFromRedis(userId);
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("请登录后重试！");
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
                if (!user.getUserHeaderImgUrl().contains("static")) {
                    qiNiuCloudService.delete(user.getUserHeaderImgUrl());
                }
                User user1 = CombineBeans.combine(var0, user);
                executor.execute(() -> UserUtils.updateLoggedUserInfo(user1, request, response));
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("头像上传成功");
            } catch (Exception e) {
                e.printStackTrace();
                ajaxResult.setMessage("服务异常，请重试！");
            }
        }
        return ajaxResult;
    }

}
