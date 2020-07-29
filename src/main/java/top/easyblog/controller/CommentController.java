package top.easyblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.UserComment;

import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/comment")
public class CommentController extends BaseController{

    /**
     * 发表评论
     *
     * @param comment 评论信息
     */
    @ResponseBody
    @PostMapping(value = "/publish", produces = "application/json;charset=UTF-8")
    public WebAjaxResult publishComment(@RequestBody UserComment comment) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setMessage("您还未登陆，请登录后重试！");
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
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("评论保存成功！");
            }
        }
        return ajaxResult;
    }


}
