package org.easyblog.controller;

import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;
import org.easyblog.bean.Category;
import org.easyblog.enumHelper.ArticleType;
import org.easyblog.service.impl.ArticleServiceImpl;
import org.easyblog.service.impl.CategoryServiceImpl;
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
     * @param articleType
     */
    void getArticleUserInfo(Model model, int userId, String articleType) {
        //查作者的所有允许显示的分类
        List<Category> lists = categoryService.getUserAllViableCategory(userId);
        //查作者的所有归档
        List<ArticleCount> archives = articleService.getUserAllArticleArchives(userId);
        //查作者的最新文章
        List<Article> newestArticles = articleService.getUserNewestArticles(userId, 5);
        //查作者的原创文章数
        Article article = new Article();
        article.setArticleUser(userId);
        article.setArticleType(ArticleType.Original.getArticleType());
        int originalArticle = articleService.countSelective(article);
        //查作者的指定类型的所有文章
        List<Article> articles = articleService.getUserArticles(userId, articleType);
        model.addAttribute("categories", lists);
        model.addAttribute("archives", archives);
        model.addAttribute("newestArticles", newestArticles);
        model.addAttribute("articles", articles);
        model.addAttribute("articleNum", articles.size());
        model.addAttribute("originalArticleNum", originalArticle);
    }


}
