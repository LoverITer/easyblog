package top.easyblog.web.controller.admin;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserComment;
import top.easyblog.global.pagehelper.PageParam;
import top.easyblog.global.pagehelper.PageSize;
import top.easyblog.util.CookieUtils;
import top.easyblog.util.UserUtils;
import top.easyblog.web.controller.BaseController;
import top.easyblog.web.service.impl.ArticleServiceImpl;
import top.easyblog.web.service.impl.CommentServiceImpl;
import top.easyblog.web.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
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
    public String commentPublishListPage(HttpServletRequest request,
                                         Model model,
                                         @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user= UserUtils.getUserFromRedis(sessionId);
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
                                         HttpServletRequest request,
                                         @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user= UserUtils.getUserFromRedis(sessionId);
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
    public WebAjaxResult deleteComment(HttpServletRequest request,
                                       @RequestParam int commentId) {
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user= UserUtils.getUserFromRedis(sessionId);
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
