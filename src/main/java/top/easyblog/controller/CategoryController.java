package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.easyblog.common.enums.ArticleType;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.Category;
import top.easyblog.entity.po.CategoryCare;
import top.easyblog.entity.po.User;
import top.easyblog.service.ICategoryCareService;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/category/details")
public class CategoryController extends BaseController{

    @Autowired
    private ICategoryCareService categoryCareService;

    /**
     * 访问分类详细信息页面
     *
     * @param categoryId
     * @param userId
     * @param model
     * @param pageNo
     */
    @GetMapping(value = "/{categoryId}/{userId}")
    public String categoryDetailsPage(Model model,
                                      HttpServletRequest request,
                                      @PathVariable(value = "categoryId") int categoryId,
                                      @PathVariable("userId") int userId,
                                      @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        getArticleUserInfo(model, userId, ArticleType.Original.getArticleType());
        //分类的信息
        Category category = categoryService.getCategory(categoryId);
        if (Objects.nonNull(category)) {
            model.addAttribute("category", category);
        }
        //文章细节
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE);
        PageInfo<Article> categoryArticlesPage = articleService.getByCategoryAndUserIdPage(userId, categoryId, pageParam);
        model.addAttribute("categoryArticles", categoryArticlesPage);
        //分类的关注按钮状态控制
        model.addAttribute("care", "false");
        List<CategoryCare> categoryCare = categoryCareService.getCategoryCare(categoryId);
        if (Objects.nonNull(categoryCare)) {
            categoryCare.forEach(ele -> {
                if (userId == ele.getCategoryCareUserId()) {
                    model.addAttribute("care", "true");
                }
            });
        }
        //文章作者的信息
        User author = userService.getUser(userId);
        author.setUserPassword(null);
        model.addAttribute("author", author);
        //访问者的信息
        User visitor = UserUtils.getUserFromCookie(request);
        model.addAttribute("visitor", visitor);
        executor.execute(()->{
            //更新专栏的访问量
            Category var0 = new Category();
            var0.setCategoryId(categoryId);
            var0.setCategoryClickNum(category.getCategoryClickNum()+1);
            categoryService.updateByPKSelective(var0);
        });
        return "category-details";
    }


    /**
     * 关注分类
     *
     * @param categoryId
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/care/{categoryId}")
    public WebAjaxResult careCategory(@PathVariable("categoryId") int categoryId,
                                      @RequestParam("userId") int userId) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setSuccess(false);
        ajaxResult.setMessage("服务异常，请重试！");
        HashMap<String, Object> map = new HashMap<>(8);
        //更新关注数
        map.put("categoryCareNum", 1);
        try {
            categoryService.updateCategoryInfo(categoryId, map);
            categoryCareService.saveCareInfo(userId, categoryId);
            ajaxResult.setSuccess(true);
            ajaxResult.setMessage("OK");
        } catch (Exception e) {
            return ajaxResult;
        }
        return ajaxResult;
    }


    /**
     * 取消关注
     *
     * @param categoryId
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cancelCare/{categoryId}")
    public WebAjaxResult cancelCare(@PathVariable("categoryId") int categoryId,
                                    @RequestParam("userId") int userId) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setSuccess(false);
        ajaxResult.setMessage("服务异常，请重试！");
        HashMap<String, Object> map = new HashMap<>(8);
        //更新关注数
        map.put("categoryCareNum", -1);
        try {
            categoryService.updateCategoryInfo(categoryId, map);
            categoryCareService.deleteCareInfo(userId, categoryId);
            ajaxResult.setSuccess(true);
            ajaxResult.setMessage("OK");
        } catch (Exception e) {
            return ajaxResult;
        }
        return ajaxResult;
    }


}
