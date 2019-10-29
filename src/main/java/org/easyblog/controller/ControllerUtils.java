package org.easyblog.controller;

import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;
import org.easyblog.bean.Category;
import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.springframework.ui.Model;

import java.util.List;

class ControllerUtils {

    private CategoryServiceImpl categoryService;
    private ArticleServiceImpl articleService;

    ControllerUtils(CategoryServiceImpl categoryService, ArticleServiceImpl articleService) {
        this.categoryService = categoryService;
        this.articleService = articleService;
    }


    /***
     * 这是几个页面公共需要查询数据
     * @param model
     * @param userId
     * @param option
     */
    void getArticleUserInfo(Model model, int userId, int option){
        final List<Category> lists = categoryService.getUserAllViableCategory(userId);
        final List<ArticleCount> archives = articleService.getUserAllArticleArchives(userId);
        final List<Article> newestArticles = articleService.getUserNewestArticles(userId, 5);
        List<Article> articles = articleService.getUserArticles(userId, option);
        model.addAttribute("categories", lists);
        model.addAttribute("archives", archives);
        model.addAttribute("newestArticles", newestArticles);
        model.addAttribute("articles", articles);
        model.addAttribute("articleNum", articles.size());
    }



}
