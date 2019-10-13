package org.easyblog.controller;

import org.easyblog.handler.exception.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @RequestMapping(value = "/index")
    public String hello(){
        int i=3/0;
        return "index";
    }

    @GetMapping(value = "/blog")
    public String blog(Model model){

        String blog=null;
        if(blog==null){
            model.addAttribute("msg","你访问的博客找不到啦~！");
            throw new NotFoundException("你访问的博客找不到啦~！");
        }

        return "blog";
    }

}
