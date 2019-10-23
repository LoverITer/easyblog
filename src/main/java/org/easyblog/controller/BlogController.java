package org.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/article")
public class BlogController {


    @RequestMapping(value = "/index")
    public String index(){
        return "user_home";
    }


    @RequestMapping(value = "/home/{id}")
    public String homePage(@PathVariable String id){
        System.out.println(id);
        return "home";
    }


    @RequestMapping(value = "/archives/{year}/{month}")
    public String archivesPage(@PathVariable String year, @PathVariable String month){
        System.out.println(year +" "+month);
       return "archives";
    }


    @RequestMapping(value = "/types")
    public String typesPage(){
        return "types";
    }






}
