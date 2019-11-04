package org.easyblog.controller.admin;

import org.easyblog.bean.Category;
import org.easyblog.bean.User;
import org.easyblog.config.Result;
import org.easyblog.service.CategoryServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;


/**
 * 用户后台文章分类管理
 */
@Controller
@RequestMapping(value = "/manage/category")
public class CategoryAdminController {


    private static final String PREFIX = "/admin/type_manage/";
    private static final String LOGIN_PAGE="redirect:/user/loginPage";

    private final CategoryServiceImpl categoryService;

    public CategoryAdminController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping(value = "/list")
    public String categoryPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            final List<Category> categories = categoryService.getUserAllCategories(user.getUserId());

            model.addAttribute("categories", categories);
            model.addAttribute("userId", user.getUserId());
            putCategoryNumInModel(user,model);
            return PREFIX + "category-manage";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @RequestMapping(value = "/changeDisplay")
    public Result changeCategoryDisplayOrNot(@RequestParam(value = "categoryId") int categoryId,
                                             @RequestParam(value = "displayStatus") String displayStatus,
                                             HttpSession session) {
        final Result result = new Result();
        result.setSuccess(false);
        User user = (User) session.getAttribute("user");
        if (Objects.isNull(user)) {
            result.setMsg("请先登录后再操作");
            return result;
        }
        try {
            Category category = new Category();
            category.setCategoryId(categoryId);
            category.setDisplay(displayStatus);
            categoryService.updateByPKSelective(category);
            result.setSuccess(true);
            result.setMsg("Ok");
            return result;
        } catch (Exception e) {
            result.setMsg("服务异常，请重试！");
            return result;
        }
    }

    @GetMapping(value = "/delete")
    public String deleteCategory(@RequestParam(value = "categoryId") int categoryId) {
        if (categoryId > 0) {
            final Category category = new Category();
            category.setCategoryId(categoryId);
            category.setDisplay("3");
            categoryService.updateByPKSelective(category);
            return "redirect:/manage/category/list";
        }
        return "/error/404";
    }


    @GetMapping(value = "/restore")
    public String restoreCategory(@RequestParam int categoryId){
        if (categoryId > 0) {
            final Category category = new Category();
            category.setCategoryId(categoryId);
            category.setDisplay("1");
            categoryService.updateByPKSelective(category);
            return "redirect:/manage/category/dash";
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/deleteComplete")
    public String deleteComplete(@RequestParam int categoryId) {
        if (categoryId > 0) {
            final Category category = new Category();
            category.setCategoryId(categoryId);
            categoryService.deleteCategoryByCondition(category);
            return "redirect:/manage/category/dash";
        }
        return LOGIN_PAGE;
    }


    /**
     * 从分类管理首页删除的分类进入到垃圾箱中，垃圾箱页面
     * @param session
     * @param model
     * @return
     */
    @GetMapping(value = "/dash")
    public String categoryDashBoxPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            List<Category> categories = categoryService.getUserAllDeletedCategory(user.getUserId());
            model.addAttribute("categories", categories);
            putCategoryNumInModel(user,model);
            return PREFIX + "category-dash";
        }
        return LOGIN_PAGE;
    }

    /**
     * 统计分类的数量
     *
     * @param displayStatus 分类的状态 0 不在前台显示  1 在前台显示  2 放在垃圾桶中
     * @return
     */
    private int getCategoryNum(User user, String displayStatus) {
        if (Objects.nonNull(user)) {
            Category category = new Category();
            category.setCategoryUser(user.getUserId());
            category.setDisplay(displayStatus);
            return categoryService.countSelective(category);
        }
        return 0;
    }

    private void putCategoryNumInModel(User user, Model model) {
        int var0 = getCategoryNum(user, "0");
        int var1 = getCategoryNum(user, "1");
        int var2 = getCategoryNum(user, "2");
        model.addAttribute("visibleCategoryNum",var0+var1);
        model.addAttribute("deletedCategoryNum",var2);
    }

    @GetMapping(value = "/add")
    public String categoryAddPage() {
        return PREFIX + "category-add";
    }


    @GetMapping(value = "/edit")
    public String categoryEdit(HttpSession session,@RequestParam int categoryId,Model model) {
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            final Category category = categoryService.getCategory(categoryId);
            model.addAttribute("category",category);
            return PREFIX + "category-edit";
        }
       return LOGIN_PAGE;
    }

    @ResponseBody
    @RequestMapping(value = "/saveEdit/{categoryId}")
    public Result saveEdit(HttpSession session, @PathVariable("categoryId") int categoryId,
                           @RequestParam String categoryName,
                           @RequestParam String categoryDesc,
                           @RequestParam String categoryImgUrl){
        Result result = new Result();
        result.setSuccess(false);
        User user = (User) session.getAttribute("user");
        if(Objects.nonNull(user)){
            try{
                final Category category = new Category();
                category.setCategoryId(categoryId);
                category.setCategoryName(categoryName);
                category.setCategoryDescription(categoryDesc);
                category.setCategoryImageUrl(categoryImgUrl);
                int re = categoryService.updateByPKSelective(category);
                if(re>0) {
                    result.setSuccess(true);
                    result.setMsg("OK");
                }
            }catch (Exception ex){
                result.setMsg("更新失败，请重试！");
                return result;
            }
        }
        return result;
    }


    @ResponseBody
    @GetMapping(value = "/upload")
    public String uploadImage() {
        return "";
    }

}
