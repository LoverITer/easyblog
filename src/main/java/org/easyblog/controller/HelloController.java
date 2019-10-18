package org.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @RequestMapping(value = "/index")
    public String hello(){
        //int i=9/0;
        return "user_home";
    }

    @GetMapping(value = "/blog/{id}")
    public String blog(Model model,@PathVariable(value = "id",required = true) Integer id){
        return "blog";
    }


    @GetMapping(value = "/center")
    public String center(){
        return "admin/personalInfo/personal-center";
    }


    @GetMapping(value = "/write")
    public String write(){
        int[] a=new int[10];

        return "admin/blogmanage/blogs-input";
    }

}
