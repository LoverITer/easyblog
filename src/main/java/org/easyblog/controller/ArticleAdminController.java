package org.easyblog.controller;


import org.easyblog.bean.Article;
import org.easyblog.bean.Category;
import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.easyblog.service.UserServiceImpl;
import org.easyblog.utils.MarkdownUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;


/**
 * 后台博客管理
 */
@Controller
@RequestMapping(value = "/manage/blog")
public class ArticleAdminController {

    private final ArticleServiceImpl articleService;
    private final CategoryServiceImpl categoryService;
    private static final String PREFIX="admin/blog_manage";

    public ArticleAdminController(ArticleServiceImpl articleService, CategoryServiceImpl categoryService, UserServiceImpl userService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
    }


    @GetMapping(value = "/")
    public String manageBlog(){
        return PREFIX+"/blog-manage";
    }

    @GetMapping(value = "/post")
    public String writeBlog(HttpSession session, Model model,@RequestParam(value = "userId",defaultValue = "-1",required = false) int userId){
        //没有登录的话就去登录，登录后才可以写博客
        if(null==session.getAttribute("user")){
            return "redirect:/user/loginPage";
        }
        if(userId!=-1) {
            final List<Category> categories = categoryService.getUserAllCategories(userId);
            model.addAttribute("categories", categories);
            model.addAttribute("userId",userId);
        }
        return PREFIX+"/blog-input";
    }


    @PostMapping(value = "/saveArticle/{userId}")
    public String saveArticle(@PathVariable(value = "userId") Integer userId){
        System.out.println(userId);
        return PREFIX+"/blog-input";
    }


    @PostMapping(value = "/saveAsDraft/{userId}")
    public String saveAsDraft(@PathVariable(value = "userId") Integer userId,
                              @RequestParam(value = "content",defaultValue = "") String content,
                              @RequestParam(value = "title",defaultValue = "")String title){
        if(!"".equals(content)&&!"".equals(title)) {
            String htmlContent = MarkdownUtil.markdownToHtmlExtensions(content);
            final Article article = new Article(userId, title, new Date(), 0, "", "2", false, "0", "", htmlContent, 0, "0");
            articleService.saveArticle(article);
        }
        return PREFIX+"/blog-input";
    }

    @GetMapping(value = "/public")
    public String publicBlog(){
        return PREFIX+"/blog-public";
    }

    @GetMapping(value="/private")
    public String privateBlog(){
        return PREFIX+"/blog-private";
    }

    @GetMapping(value = "/draft")
    public String draftBlog(){
        return PREFIX+"/blog-draft-box";
    }

    @GetMapping(value = "/dash")
    public String dashBlog(){
        return PREFIX+"/blog-dash";
    }


}
