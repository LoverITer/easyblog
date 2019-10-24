package org.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 后台用户分类管理
 */
@Controller
@RequestMapping(value = "/manage/category")
public class UserCategoryManageController {


    private static final String PREFIX="/admin/typemanage/";

    /**
     * 去分类管理页面
     * @return
     */
    @GetMapping(value = "/list")
    public String categoryPage(){
        return PREFIX+"category-manage";
    }


    @GetMapping(value = "/dash")
    public String categoryDashPage(){
        return PREFIX+"category-dash";
    }


}
