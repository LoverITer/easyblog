package top.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class WelcomeController {

    @GetMapping(value="/")
    public String index(){
      return "index";
    }

    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }

}
