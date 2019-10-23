package org.easyblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 后台博客管理
 */
@Controller
@RequestMapping(value = "/manage/blog")
public class BlogAdminController {


    private static final String PREFIX="admin/blogmanage";


    @GetMapping(value = "/")
    public String manageBlog(){
        return PREFIX+"/blog-manage";
    }

    @GetMapping(value = "/write")
    public String writeBlog(){
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
