package org.easyblog.controller;

import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;
import org.easyblog.bean.Category;
import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

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

    @RequestMapping(value = "/index/{id}")
    public String index(@PathVariable("id") int userId, Model model, HttpSession session){
        final List<Category> lists = categoryServiceImpl.getUserAllViableCategory(userId);
        final List<ArticleCount> archives = articleServiceImpl.getUserAllArticleArchives(userId);
        final List<Article> newestArticles = articleServiceImpl.getUserNewestArticles(userId, 5);
        session.setAttribute("categories",lists);
        session.setAttribute("archives",archives);
        session.setAttribute("newestArticles",newestArticles);
        session.setMaxInactiveInterval(60*60*12);   //保存一天
        return "user_home";
    }


    @RequestMapping(value = "/home/{id}")
    public String homePage(@PathVariable("id") String id){
        return "home";
    }

}
