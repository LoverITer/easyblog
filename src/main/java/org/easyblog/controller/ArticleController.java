package org.easyblog.controller;

import org.easyblog.bean.Article;
import org.easyblog.bean.ArticleCount;
import org.easyblog.bean.Category;
import org.easyblog.bean.User;
import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String index(@PathVariable("id") int userId,
                        @RequestParam(defaultValue = "0") int option,
                        HttpSession session,
                        Model model){
        final List<Category> lists = categoryServiceImpl.getUserAllViableCategory(userId);
        final List<ArticleCount> archives = articleServiceImpl.getUserAllArticleArchives(userId);
        final List<Article> newestArticles = articleServiceImpl.getUserNewestArticles(userId, 5);
        List<Article> articles = articleServiceImpl.getUserArticles(userId,option);
        session.setAttribute("categories",lists);
        session.setAttribute("archives",archives);
        session.setAttribute("newestArticles",newestArticles);
        model.addAttribute("articles",articles);
        model.addAttribute("articleNum",articles.size());
        session.setMaxInactiveInterval(60*60*12);   //保存一天

        final User user = userService.getUser(userId);
        user.setUserPassword(null);
        model.addAttribute("user",user);
        if(option==0){
            model.addAttribute("display","0");
        } else if (option == 1) {
            model.addAttribute("display","1");
        }
        return "user_home";
    }

/*    @GetMapping(value = "display")
    public String  display(@PathVariable("id") int userId,
                           @RequestParam(defaultValue = "0") int option){
        if(option==0){
            return "forward:/article/index/"+userId+"?"+option;
        }else if(option==1){
            return "forward:/article/index/"+userId+"?"+option;
        }
    }*/

    @RequestMapping(value = "/home/{id}")
    public String homePage(@PathVariable("id") String id){
        return "home";
    }

}
