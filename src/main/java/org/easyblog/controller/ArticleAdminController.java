package org.easyblog.controller;


import org.easyblog.service.ArticleServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


/**
 * 后台博客管理
 */
@Controller
@RequestMapping(value = "/manage/blog")
public class ArticleAdminController {

    private final ArticleServiceImpl articleService;
    private static final String PREFIX="admin/blog_manage";

    public ArticleAdminController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }


    @GetMapping(value = "/")
    public String manageBlog(){
        return PREFIX+"/blog-manage";
    }

    @GetMapping(value = "/post")
    public String writeBlog(HttpSession session){
        //没有登录的话就去登录，登录后才可以写博客
        if(null==session.getAttribute("LOGIN-USER")){
            return "redirect:/user/loginPage";
        }
        return PREFIX+"/blog-input";
    }

    @RequestMapping(value = "/post")
    public String postArticle(){
       return "";
    }

    @RequestMapping(value = "")
    public String saveArticle(){
      return "";
    }

    @GetMapping(value = "/public")
    public String pubBlog(){
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
