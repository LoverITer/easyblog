package top.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.bean.UserComment;
import top.easyblog.enumHelper.ArticleType;
import top.easyblog.service.impl.*;
import top.easyblog.utils.HtmlParserUtil;
import top.easyblog.utils.MarkdownUtil;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/article")
public class ArticleController {


    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryServiceImpl;
    private final ArticleServiceImpl articleServiceImpl;
    private final CommentServiceImpl commentService;
    private final UserAttentionImpl userAttention;
    private final String PAGE404 = "redirect:/error/404";

    public ArticleController(CategoryServiceImpl categoryServiceImpl, UserServiceImpl userService, ArticleServiceImpl articleServiceImpl, CommentServiceImpl commentService, UserAttentionImpl userAttention) {
        this.categoryServiceImpl = categoryServiceImpl;
        this.userService = userService;
        this.articleServiceImpl = articleServiceImpl;
        this.commentService = commentService;
        this.userAttention = userAttention;
    }

    @RequestMapping(value = "/index/{userId}")
    public String index(@PathVariable("userId") int userId,
                        @RequestParam(value = "articleType", defaultValue = "3") int articleType,
                        Model model) {
        new ControllerUtils(categoryServiceImpl, articleServiceImpl, commentService, userAttention).getArticleUserInfo(model, userId, articleType + "");
        final User user = userService.getUser(userId);
        List<Article> articles = articleServiceImpl.getUserArticles(userId, articleType + "");
        if (Objects.nonNull(articles)) {
            articles.forEach(article -> {
                String htmlContent = MarkdownUtil.markdownToHtmlExtensions(article.getArticleContent());
                String textContent = HtmlParserUtil.HTML2Text(htmlContent);
                article.setArticleContent(textContent);
            });
            model.addAttribute("articles", articles);
            user.setUserPassword(null);
            model.addAttribute("user", user);
            if (ArticleType.Original.getArticleType().equals(articleType + "")) {
                model.addAttribute("displayOnlyOriginal", "1");
            } else if (ArticleType.Unlimited.getArticleType().equals(articleType + "")) {
                model.addAttribute("displayOnlyOriginal", "0");
            }
            return "user_home";
        }
        return PAGE404;
    }


    @RequestMapping(value = "/home/{userId}")
    public String homePage(@PathVariable("userId") int userId, Model model) {
        final User user = userService.getUser(userId);
        user.setUserPassword(null);
        List<Article> articles = articleServiceImpl.getUserArticles(userId, ArticleType.Unlimited.getArticleType());
        if (Objects.nonNull(articles)) {
            articles.forEach(article -> {
                String htmlContent = MarkdownUtil.markdownToHtmlExtensions(article.getArticleContent());
                String textContent = HtmlParserUtil.HTML2Text(htmlContent);
                article.setArticleContent(textContent);
            });
            int articleSize = articles.size();
            if (articleSize < 15) {
                model.addAttribute("articles", articles);
            } else {
                model.addAttribute("articles", articles.subList(0, 15));
            }
            model.addAttribute("articleSize", articleSize);
            model.addAttribute("user", user);
            return "home";
        }
        return PAGE404;
    }


    @GetMapping(value = "/details/{articleId}")
    public String articleDetails(@PathVariable("articleId") int articleId, Model model) {
        Article article = articleServiceImpl.getArticleById(articleId);
        if (Objects.nonNull(article)) {
            String htmlContent = MarkdownUtil.markdownToHtmlExtensions(article.getArticleContent());
            article.setArticleContent(htmlContent);
            model.addAttribute("article", article);
            User user = userService.getUser(article.getArticleUser());
            List<UserComment> articleComments = commentService.getArticleComments(article.getArticleId());
            model.addAttribute("articleComments", articleComments);
            if (Objects.nonNull(user)) {
                user.setUserPassword(null);
                model.addAttribute("user", user);
                //更新用户的访问量
                User user1 = new User();
                user1.setUserId(user.getUserId());
                user1.setUserVisit(user.getUserVisit() + 1);
                userService.updateUserInfo(user1);
                //更新文章的访问量
                Article article1 = new Article();
                article1.setArticleId(article.getArticleId());
                article1.setArticleClick(article.getArticleClick()+1);
                articleServiceImpl.updateSelective(article1);
                new ControllerUtils(categoryServiceImpl, articleServiceImpl, commentService, userAttention).getArticleUserInfo(model, user.getUserId(), ArticleType.Original.getArticleType());
                return "blog";
            }
        }
        return PAGE404;
    }


}
