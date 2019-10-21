package org.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/article")
public class BlogController {


    @RequestMapping(value = "/index")
    public String toUserHome(){
        return "user_home";
    }




}
