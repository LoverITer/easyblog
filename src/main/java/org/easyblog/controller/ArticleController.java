package org.easyblog.controller;

import org.easyblog.bean.Article;
import org.easyblog.bean.User;
import org.easyblog.bean.enums.ArticleType;
import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.easyblog.utils.HtmlParserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/article")
public class ArticleController {

    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryServiceImpl;
    private final ArticleServiceImpl articleServiceImpl;

    public ArticleController(CategoryServiceImpl categoryServiceImpl, UserServiceImpl userService, ArticleServiceImpl articleServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
        this.userService = userService;
        this.articleServiceImpl = articleServiceImpl;
    }

    @RequestMapping(value = "/index/{userId}")
    public String index(@PathVariable("userId") int userId,
                        @RequestParam(value = "articleType",defaultValue = "3") int articleType,
                        Model model) {
        new ControllerUtils(categoryServiceImpl,articleServiceImpl).getArticleUserInfo(model,userId,articleType+"");
        final User user = userService.getUser(userId);
        List<Article> articles = articleServiceImpl.getUserArticles(userId, articleType+"");
        if(articles!=null){
            articles.forEach(article -> article.setArticleContent(HtmlParserUtil.HTML2Text(article.getArticleContent())));
        }
        model.addAttribute("articles",articles);
        user.setUserPassword(null);
        model.addAttribute("user", user);
        if (ArticleType.Original.getArticleType().equals(articleType+"")) {
            model.addAttribute("displayOnlyOriginal", "1");
        } else if (ArticleType.Unlimited.getArticleType().equals(articleType+"")) {
            model.addAttribute("displayOnlyOriginal", "0");
        }
        return "user_home";
    }


    @RequestMapping(value = "/home/{userId}")
    public String homePage(@PathVariable("userId") int userId, Model model) {
        final User user = userService.getUser(userId);
        user.setUserPassword(null);
        List<Article> articles = articleServiceImpl.getUserArticles(userId, ArticleType.Original.getArticleType());
        articles.forEach(article -> {
            article.setArticleContent(HtmlParserUtil.HTML2Text(article.getArticleContent()));
        });
        if (articles.size() < 15) {
            model.addAttribute("articles", articles);
        } else {
            model.addAttribute("articles", articles.subList(0, 15));
        }
        model.addAttribute("user", user);
        return "home";
    }


    @GetMapping(value = "/details/{articleId}")
    public String articleDetails(@PathVariable("articleId") int articleId, Model model) {
        final Article article = articleServiceImpl.getArticleById(articleId);
        model.addAttribute("article",article);
        User user = userService.getUser(article.getArticleUser());
        model.addAttribute("user",user);
        if(Objects.nonNull(user)) {
            new ControllerUtils(categoryServiceImpl, articleServiceImpl).getArticleUserInfo(model, user.getUserId(), ArticleType.Original.getArticleType());
        }
        return "blog";
    }


}
