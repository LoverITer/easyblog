package org.easyblog.controller.admin;

import org.easyblog.bean.User;
import org.easyblog.bean.UserComment;
import org.easyblog.config.Result;
import org.easyblog.service.impl.ArticleServiceImpl;
import org.easyblog.service.impl.CommentServiceImpl;
import org.easyblog.service.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "/manage/comment")
public class CommentAdminController {

    private static final String PREFIX="/admin/comment_manage/";
    private static final String LOGIN_PAGE="redirect:/user/loginPage";
    private final CommentServiceImpl commentService;
    private final ArticleServiceImpl articleService;
    private final UserServiceImpl userService;

    public CommentAdminController(CommentServiceImpl commentService, ArticleServiceImpl articleService, UserServiceImpl userService) {
        this.commentService = commentService;
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping(value = "/publish")
    public String commentPublishListPage(HttpSession session, Model model){
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            List<UserComment> comments = commentService.getComment(user.getUserId(), "send");

            model.addAttribute("comments",comments);
            return PREFIX+"comment-manage-publish";
        }
       return LOGIN_PAGE;
    }


    @GetMapping(value = "/receive")
    public String commentReceiveListPage(HttpSession session, Model model){
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            List<UserComment> comments = commentService.getComment(user.getUserId(), "receive");
            model.addAttribute("comments",comments);
            return PREFIX+"comment-manage-receive";
        }
        return LOGIN_PAGE;
    }


    @ResponseBody
    @GetMapping(value = "/delete")
    public Result deleteComment(HttpSession session, @RequestParam int commentId, HttpServletRequest request){
        User user = (User) session.getAttribute("user");
        Result result = new Result();
        result.setSuccess(false);
        result.setMsg("未登录");
        if(Objects.nonNull(user)){
            int var0 = commentService.deleteComment(commentId);
            if(var0==1){
                result.setSuccess(true);
                result.setMsg("OK");
            }
            result.setMsg("删除失败");
            return result;
        }
        return result;
    }


}
