package org.easyblog.controller;

import org.easyblog.config.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 后台用户分类管理
 */
@Controller
@RequestMapping(value = "/manage/category")
public class UserCategoryManageController {


    private static final String PREFIX="/admin/type_manage/";

    /***封装AJAX请求的返回结果***/
    private final Result result = new Result();
    /***ajax异步请求成功标志***/
    private static final String AJAX_SUCCESS = "OK";
    /***ajax异步请求失败标志***/
    private static final String AJAX_ERROR = "FATAL";

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

    @GetMapping(value = "/add")
    public String categoryAddPage(){
        return PREFIX+"category-add";
    }


    @GetMapping(value = "/edit")
    public String categoryEdit(){
        return PREFIX+"category-edit";
    }


    @GetMapping(value = "/delete")
    public String categoryDelete(){
        return "/manage/category/list";
    }

    @ResponseBody
    @GetMapping(value = "/upload")
    public String uploadImage(){
        return "";
    }

}
