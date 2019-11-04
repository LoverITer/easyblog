package org.easyblog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/manage/uc")
public class UserCenterController {

    private static final String PREFIX="/admin/personalInfo";

    @GetMapping(value = "/profile")
    public String center() {
        return PREFIX + "/personal-center";
    }


    @GetMapping(value = "/follow-list")
    public String care(){
      return PREFIX+"/personal-care";
    }


    @GetMapping(value = "/fans-list")
    public String fans(){
        return PREFIX+"/personal-follower";
    }



}
