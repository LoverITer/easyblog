package org.easyblog.controller;


import org.easyblog.service.ArticleServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 后台博客管理
 */
@Controller
@RequestMapping(value = "/manage/blog")
public class ArticleAdminController {

    private final CategoryServiceImpl categoryService;
    private final ArticleServiceImpl articleService;
    private static final String PREFIX="admin/blog_manage";

    public ArticleAdminController(CategoryServiceImpl categoryService, ArticleServiceImpl articleService) {
        this.categoryService = categoryService;
        this.articleService = articleService;
    }


    @GetMapping(value = "/")
    public String manageBlog(){
        return PREFIX+"/blog-manage";
    }

    @GetMapping(value = "/post")
    public String writeBlog(Model model){

        return PREFIX+"/blog-input";
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
