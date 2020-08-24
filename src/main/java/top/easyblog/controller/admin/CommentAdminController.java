package top.easyblog.controller.admin;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.controller.BaseController;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserComment;
import top.easyblog.service.impl.ArticleServiceImpl;
import top.easyblog.service.impl.CommentServiceImpl;
import top.easyblog.service.impl.UserServiceImpl;
import top.easyblog.util.UserUtils;

import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/manage/comment")
public class CommentAdminController extends BaseController {

    private static final String PREFIX = "/admin/comment_manage/";


    public CommentAdminController(CommentServiceImpl commentService, ArticleServiceImpl articleService, UserServiceImpl userService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "/publish")
    public String commentPublishListPage(@RequestParam Integer userId,
                                         Model model,
                                         @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE);
            PageInfo<UserComment> commentsPage = commentService.getCommentPage(user.getUserId(), "send", pageParam);
            model.addAttribute("commentsPage", commentsPage);
            return PREFIX + "comment-manage-publish";
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/receive")
    public String commentReceiveListPage(Model model,
                                         @RequestParam Integer userId,
                                         @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE);
            PageInfo<UserComment> commentsPage = commentService.getCommentPage(user.getUserId(), "receive", pageParam);
            model.addAttribute("commentsPage", commentsPage);
            return PREFIX + "comment-manage-receive";
        }
        return LOGIN_PAGE;
    }


    @ResponseBody
    @GetMapping(value = "/delete")
    public WebAjaxResult deleteComment(@RequestParam Integer userId,
                                       @RequestParam int commentId) {
        User user = UserUtils.getUserFromRedis(userId);
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("请登录后再操作！");
        if (Objects.nonNull(user)) {
            int var0 = commentService.deleteComment(commentId);
            if (var0 == 1) {
                ajaxResult.setSuccess(true);
            }
            ajaxResult.setMessage("抱歉！删除失败");
            return ajaxResult;
        }
        return ajaxResult;
    }


}
