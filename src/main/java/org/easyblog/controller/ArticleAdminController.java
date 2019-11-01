package org.easyblog.controller;


import org.easyblog.bean.Article;
import org.easyblog.bean.Category;
import org.easyblog.config.Result;
import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.easyblog.utils.FileUploadUtils;
import org.easyblog.utils.HtmlParserUtil;
import org.easyblog.utils.MarkdownUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;


/**
 * 后台博客管理
 */
@Controller
@RequestMapping(value = "/manage/blog")
public class ArticleAdminController {

    private final ArticleServiceImpl articleService;
    private final CategoryServiceImpl categoryService;
    private static final String PREFIX = "admin/blog_manage";

    public ArticleAdminController(ArticleServiceImpl articleService, CategoryServiceImpl categoryService, UserServiceImpl userService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
    }


    @GetMapping(value = "/")
    public String manageBlog() {
        return PREFIX + "/blog-manage";
    }

    @RequestMapping(value = "/post")
    public String writeBlog(HttpSession session, Model model, @RequestParam(value = "userId", defaultValue = "-1", required = false) int userId) {
        //没有登录的话就去登录，登录后才可以写博客
        if (null == session.getAttribute("user")) {
            return "redirect:/user/loginPage";
        }
        if (userId != -1) {
            final List<Category> categories = categoryService.getUserAllCategories(userId);
            model.addAttribute("categories", categories);
            model.addAttribute("userId", userId);
        }
        return PREFIX + "/blog-input";
    }


    @ResponseBody
    @PostMapping(value = "/saveArticle/{userId}")
    public Result saveArticle(@RequestBody Article article0,
                              @PathVariable(value = "userId") Integer userId) {
        Result result = new Result();
        result.setSuccess(false);
        String htmlContent = MarkdownUtil.markdownToHtmlExtensions(article0.getArticleContent());
        //不管有没有删除当前用户的这篇草稿
        try {
            articleService.deleteByUserIdAndTitle(userId, article0.getArticleTopic());
            final Article article = new Article(userId, article0.getArticleTopic(), new Date(), 0, article0.getArticleCategory(), article0.getArticleStatus(), "0", article0.getArticleType(), "", htmlContent, 0, article0.getArticleAppreciate());
            //根据分类名查用户的分类
            Category category = null;
            if (Objects.nonNull(article0.getArticleCategory())) {
                category = categoryService.getCategoryByUserIdAndName(userId, article0.getArticleCategory());
            } else {
                result.setSuccess(false);
                result.setMsg("文章分类名不可为空");
                return result;
            }
            if (Objects.isNull(category)) {
                category = new Category(userId, article0.getArticleCategory(), FileUploadUtils.defaultCategoryImage(), 1, 0, 0, 1);
                categoryService.saveCategory(category);
            } else {
                Category category0 = new Category();
                category0.setCategoryId(category.getCategoryId());
                int num = articleService.countUserArticleInCategory(userId, article0.getArticleCategory());
                category0.setCategoryArticleNum(num + 1);
                categoryService.updateByPKSelective(category0);
            }
            articleService.saveArticle(article);
            result.setSuccess(true);
            result.setMsg(article.getArticleId() + "");  //把文章的ID返回给页面
            return result;
        } catch (Exception e) {
            result.setMsg("抱歉！服务异常，请重新提交！");
            return result;
        }
    }


    @PostMapping(value = "/saveAsDraft/{userId}")
    public String saveAsDraft(@PathVariable(value = "userId") Integer userId,
                              @RequestParam(value = "content", defaultValue = "") String content,
                              @RequestParam(value = "title", defaultValue = "") String title,
                              Model model) {
        try {
            if (!"".equals(content) && !"".equals(title)) {
                String htmlContent = MarkdownUtil.markdownToHtmlExtensions(content);
                final Article article = new Article(userId, title, new Date(), 0, "", "2", "0", "0", "", htmlContent, 0, "0");
                articleService.saveArticle(article);
            }
            model.addAttribute("content", content);
            model.addAttribute("title", title);
            final List<Category> categories = categoryService.getUserAllCategories(userId);
            model.addAttribute("categories", categories);
            model.addAttribute("userId", userId);
        }catch (Exception e){
            return "/error/error";
        }
        return PREFIX + "/blog-input";
    }


    @GetMapping(value = "/public")
    public String publicBlog() {
        return PREFIX + "/blog-public";
    }

    @GetMapping(value = "/private")
    public String privateBlog() {
        return PREFIX + "/blog-private";
    }

    @GetMapping(value = "/draft")
    public String draftBlog() {
        return PREFIX + "/blog-draft-box";
    }

    @GetMapping(value = "/dash")
    public String dashBlog() {
        return PREFIX + "/blog-dash";
    }


    @RequestMapping(value = "/success/{articleId}")
    public String articlePublishSuccess(@PathVariable(value = "articleId") int articleId,
                                        Model model) {
        Article article = articleService.getArticleById(articleId);
        article.setArticleContent(HtmlParserUtil.HTML2Text(article.getArticleContent()));
        model.addAttribute("article", article);
        return PREFIX + "/blog-input-success";
    }

}
