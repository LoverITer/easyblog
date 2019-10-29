package org.easyblog.controller;

import org.easyblog.bean.Article;
import org.easyblog.bean.User;
import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "/archives/details")
public class ArchivesController {

    private final ArticleServiceImpl articleService;
    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryServiceImpl;

    public ArchivesController(ArticleServiceImpl articleService, UserServiceImpl userService, CategoryServiceImpl categoryServiceImpl) {
        this.articleService = articleService;
        this.userService = userService;
        this.categoryServiceImpl = categoryServiceImpl;
    }

    @RequestMapping(value = "/{userId}/{date}")
    public String archivesPage(@PathVariable("date") String date,
                               @PathVariable(value = "userId") int userId,
                               Model model){
        new ControllerUtils(categoryServiceImpl,articleService).getArticleUserInfo(model,userId,0);
        final List<Article> articles = articleService.getUserArticlesMonthly(userId, date.substring(0,4), date.substring(5,7));
        final User user = userService.getUser(userId);
        model.addAttribute("date", date);
        model.addAttribute("userId", userId);
        if(Objects.nonNull(articles)) {
            model.addAttribute("articles", articles);
        }
        if(Objects.nonNull(user)) {
            model.addAttribute("userHeaderImage",user.getUserHeaderImgUrl());
            model.addAttribute("userName", user.getUserNickname());
        }
        return "archives";
    }

    /*private void getArticleUserInfo(Model model, int userId, int option){
        final List<Category> lists = categoryServiceImpl.getUserAllViableCategory(userId);
        final List<ArticleCount> archives = articleService.getUserAllArticleArchives(userId);
        final List<Article> newestArticles = articleService.getUserNewestArticles(userId, 5);
        List<Article> articles = articleService.getUserArticles(userId, option);
        model.addAttribute("categories", lists);
        model.addAttribute("archives", archives);
        model.addAttribute("newestArticles", newestArticles);
        model.addAttribute("articles", articles);
        model.addAttribute("articleNum", articles.size());
    }*/

    @GetMapping(value = "orderByClickNum/{userId}/{date}")
    public String orderByClickNum(@PathVariable("userId") int userId,
                                  @PathVariable("date") String date,
                                  Model model){
        final List<Article> articles = articleService.getUserArticlesMonthlyOrderByClickNum(userId, date.substring(0,4), date.substring(5,7));
        model.addAttribute("articles",articles);
        model.addAttribute("date",date);
        model.addAttribute("userId",userId);
        return "archives";
    }

    @GetMapping(value = "orderByUpdateTime/{userId}/{date}")
    public String orderByUpdateTime(@PathVariable("userId") int userId,
                                    @PathVariable("date") String date){
       return "forward:/archives/details/"+userId+"/"+date;
    }



}
