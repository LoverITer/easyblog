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
        new ControllerUtils(categoryServiceImpl,articleService).getArticleUserInfo(model,userId, ArticleType.Original.getArticleType());
        final List<Article> articles = articleService.getUserArticlesMonthly(userId, date.substring(0,4), date.substring(5,7));
        final User user = userService.getUser(userId);
        model.addAttribute("date", date);
        if(Objects.nonNull(articles)) {
            articles.forEach(article -> {
                //把HTML文本转化为人可以直接看懂的纯文本
                article.setArticleContent(HtmlParserUtil.HTML2Text(article.getArticleContent()));
            });
            model.addAttribute("articles", articles);
        }
        if(Objects.nonNull(user)){
            model.addAttribute("user",user);
        }
        return "archives";
    }


    @GetMapping(value = "orderByClickNum/{userId}/{date}")
    public String orderByClickNum(@PathVariable("userId") int userId,
                                  @PathVariable("date") String date,
                                  Model model){
        final List<Article> articles = articleService.getUserArticlesMonthlyOrderByClickNum(userId, date.substring(0,4), date.substring(5,7));
        articles.forEach(article -> {
            article.setArticleContent(HtmlParserUtil.HTML2Text(article.getArticleContent()));
        });
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
