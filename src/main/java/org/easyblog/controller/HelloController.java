package org.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @RequestMapping(value = "/index")
    public String hello(){
        return "index";
    }

    @GetMapping(value = "/blog")
    public String blog(){
        return "blog";
    }

}
