package org.easyblog.controller;

import org.easyblog.bean.Category;
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

    public ArticleController(CategoryServiceImpl categoryServiceImpl, UserServiceImpl userService) {
        this.categoryServiceImpl = categoryServiceImpl;
        this.userService = userService;
    }

    @RequestMapping(value = "/index/{id}")
    public String index(@PathVariable("id") int userId, Model model, HttpSession session){
        List<Category> lists = categoryServiceImpl.getUserAllViableCategory(userId);
        session.setAttribute("categories",lists);
        session.setMaxInactiveInterval(60*60*1);   //保存一天
        return "user_home";
    }


    @RequestMapping(value = "/home/{id}")
    public String homePage(@PathVariable("id") String id){
        return "home";
    }


    @RequestMapping(value = "/archives/{year}/{month}")
    public String archivesPage(@PathVariable String year, @PathVariable String month){
        System.out.println(year +" "+month);
       return "archives";
    }


    @RequestMapping(value = "/types")
    public String typesPage(){
        return "category-details";
    }






}
