package top.easyblog.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.easyblog.bean.Article;
import top.easyblog.bean.UserComment;
import top.easyblog.common.util.RedisUtils;
import top.easyblog.config.web.Result;
import top.easyblog.service.impl.ArticleServiceImpl;
import top.easyblog.service.impl.CommentServiceImpl;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/comment")
public class CommentController {

    private final CommentServiceImpl commentService;
    private final ArticleServiceImpl articleService;
    @Autowired
    private RedisUtils redisUtil;
    @Autowired
    private Executor executor;

    public CommentController(CommentServiceImpl commentService, ArticleServiceImpl articleService) {
        this.commentService = commentService;
        this.articleService = articleService;
    }

    /**
     * 发表评论
     *
     * @param comment 评论信息
     */
    @ResponseBody
    @PostMapping(value = "/publish", produces = "application/json;charset=UTF-8")
    public Result publishComment(@RequestBody UserComment comment) {
        Result result = new Result();
        result.setMessage("您还未登陆，请登录后重试！");
        //登录用户就是发评论者，如果没有登录不可以发评论
        if (Objects.nonNull(comment) && Objects.nonNull(comment.getCommentSend()) && Objects.nonNull(comment.getCommentReceived())) {
            //更新文章的评论数
            executor.execute(() -> {
                Article article = new Article();
                article.setArticleId(comment.getArticleId());
                article.setArticleCommentNum(1);
                articleService.updateSelective(article);
            });
            int re = commentService.save(comment);
            if (re > 0) {
                result.setSuccess(true);
                result.setMessage("评论保存成功！");
            }
        }
        return result;
    }


}
