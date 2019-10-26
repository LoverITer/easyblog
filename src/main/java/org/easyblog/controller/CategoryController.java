package org.easyblog.controller;

import org.easyblog.bean.Category;
import org.easyblog.bean.CategoryCare;
import org.easyblog.config.Result;
import org.easyblog.service.CategoryCareServiceImpl;
import org.easyblog.service.CategoryServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping(value = "/category/details")
public class CategoryController {

    private final CategoryServiceImpl categoryServiceImpl;
    private final CategoryCareServiceImpl categoryCareService;

    public CategoryController(CategoryServiceImpl categoryServiceImpl, CategoryCareServiceImpl categoryCareService) {
        this.categoryServiceImpl = categoryServiceImpl;
        this.categoryCareService = categoryCareService;
    }

    @GetMapping(value = "/{id}/{userId}")
    public String categoryDetailsPage(@PathVariable(value = "id") int categoryId, @PathVariable("userId") int userId, Model model){
        Category category = categoryServiceImpl.getCategory(categoryId);
        List<CategoryCare> categoryCare = categoryCareService.getCategoryCare(categoryId);
        model.addAttribute("care","false");
        System.out.println(categoryCare);
        if(Objects.nonNull(categoryCare)) {
            categoryCare.forEach(ele -> {
                //待优化
                if (userId == ele.getCategoryCareUserId()) {
                    model.addAttribute("care", "true");
                }
            });
        }
        if(Objects.nonNull(category)) {
            model.addAttribute("category", category);
        }
        //文章细节，待写
        return "category-details";
    }

    @ResponseBody
    @RequestMapping(value = "/care/{categoryId}")
    public Result careCategory(@PathVariable("categoryId") int categoryId,
                               @RequestParam("userId")int userId){
        Result result = new Result();
        result.setSuccess(false);
        result.setMsg("服务异常，请重试！");
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("categoryCareNum",1);   //更新关注数
        try {
            categoryServiceImpl.updateCategoryInfo(categoryId,map);
            categoryCareService.saveCareInfo(userId,categoryId);
            result.setSuccess(true);
            result.setMsg("OK");
        }catch (Exception e){
            return result;
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/cancelCare/{categoryId}")
    public Result cancelCare(@PathVariable("categoryId") int categoryId,
                               @RequestParam("userId")int userId){
        Result result = new Result();
        result.setSuccess(false);
        result.setMsg("服务异常，请重试！");
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("categoryCareNum",-1);   //更新关注数
        try {
            categoryServiceImpl.updateCategoryInfo(categoryId,map);
            categoryCareService.deleteCareInfo(userId,categoryId);
            result.setSuccess(true);
            result.setMsg("OK");
        }catch (Exception e){
            return result;
        }
        return result;
    }







}
