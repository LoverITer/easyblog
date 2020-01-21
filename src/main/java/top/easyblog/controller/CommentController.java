package top.easyblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.bean.UserComment;
import top.easyblog.config.web.Result;
import top.easyblog.service.impl.ArticleServiceImpl;
import top.easyblog.service.impl.CommentServiceImpl;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Controller
@RequestMapping(value = "/comment")
public class CommentController {

    private final CommentServiceImpl commentService;
    private final ArticleServiceImpl articleService;

    public CommentController(CommentServiceImpl commentService, ArticleServiceImpl articleService) {
        this.commentService = commentService;
        this.articleService = articleService;
    }

    @ResponseBody
    @PostMapping(value = "/publish", produces = "application/json;charset=UTF-8")
    public Result publishComment(@RequestBody UserComment comment, HttpSession session) {
        Result result = new Result();
        result.setSuccess(false);
        //登录用户就是发评论者，如果没有登录不可以发评论
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user) && Objects.nonNull(comment)) {
            comment.setCommentSend(user.getUserId());
            if (comment.getPid() == 0) {
                comment.setPid(null);
            }
            //更新文章的评论数
            new Thread(()->{
                Article article = new Article();
                article.setArticleId(comment.getArticleId());
                article.setArticleCommentNum(1);
                articleService.updateSelective(article);
            }).start();
            int re = commentService.save(comment);
            if (re > 0) {
                result.setSuccess(true);
                result.setMessage("OK");
            }
        }
        return result;
    }


}
