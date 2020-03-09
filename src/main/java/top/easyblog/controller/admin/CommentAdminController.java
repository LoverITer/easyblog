package top.easyblog.controller.admin;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.easyblog.bean.User;
import top.easyblog.bean.UserComment;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.pagehelper.PageSize;
import top.easyblog.config.web.Result;
import top.easyblog.service.impl.ArticleServiceImpl;
import top.easyblog.service.impl.CommentServiceImpl;
import top.easyblog.service.impl.UserServiceImpl;

import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/manage/comment")
public class CommentAdminController {

    private static final String PREFIX = "/admin/comment_manage/";
    private static final String LOGIN_PAGE = "redirect:/user/loginPage";
    private final CommentServiceImpl commentService;


    public CommentAdminController(CommentServiceImpl commentService, ArticleServiceImpl articleService, UserServiceImpl userService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "/publish")
    public String commentPublishListPage(@RequestParam Integer userId,
                                         Model model,
                                         @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        User user = User.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE.getPageSize());
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
        User user = User.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE.getPageSize());
            PageInfo<UserComment> commentsPage = commentService.getCommentPage(user.getUserId(), "receive", pageParam);
            model.addAttribute("commentsPage", commentsPage);
            return PREFIX + "comment-manage-receive";
        }
        return LOGIN_PAGE;
    }


    @ResponseBody
    @GetMapping(value = "/delete")
    public Result deleteComment(@RequestParam Integer userId,
                                @RequestParam int commentId) {
        User user = User.getUserFromRedis(userId);
        Result result = new Result();
        result.setMessage("请登录后再操作！");
        if (Objects.nonNull(user)) {
            int var0 = commentService.deleteComment(commentId);
            if (var0 == 1) {
                result.setSuccess(true);
            }
            result.setMessage("抱歉！删除失败");
            return result;
        }
        return result;
    }


}
