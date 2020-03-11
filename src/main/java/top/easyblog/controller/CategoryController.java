package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.easyblog.bean.Article;
import top.easyblog.bean.Category;
import top.easyblog.bean.CategoryCare;
import top.easyblog.bean.User;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.pagehelper.PageSize;
import top.easyblog.commons.utils.UserUtil;
import top.easyblog.config.web.Result;
import top.easyblog.service.impl.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/category/details")
public class CategoryController {

    private final CategoryServiceImpl categoryServiceImpl;
    private final CategoryCareServiceImpl categoryCareService;
    private final ArticleServiceImpl articleService;
    private final UserServiceImpl userService;
    private final CommentServiceImpl commentService;
    private final UserAttentionImpl userAttention;

    public CategoryController(CategoryServiceImpl categoryServiceImpl, CategoryCareServiceImpl categoryCareService, ArticleServiceImpl articleService, UserServiceImpl userService, CommentServiceImpl commentService, UserAttentionImpl userAttention) {
        this.categoryServiceImpl = categoryServiceImpl;
        this.categoryCareService = categoryCareService;
        this.articleService = articleService;
        this.userService = userService;
        this.commentService = commentService;
        this.userAttention = userAttention;
    }


    /**
     * 访问分类详细信息页面
     *
     * @param categoryId
     * @param userId
     * @param model
     * @param pageNo
     * @return
     */
    @GetMapping(value = "/{categoryId}/{userId}")
    public String categoryDetailsPage(Model model,
                                      HttpServletRequest request,
                                      @PathVariable(value = "categoryId") int categoryId,
                                      @PathVariable("userId") int userId,
                                      @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        ControllerUtils.getInstance(categoryServiceImpl, articleService, commentService, userAttention).getArticleUserInfo(model, userId, ArticleType.Original.getArticleType());
        //分类的信息
        Category category = categoryServiceImpl.getCategory(categoryId);
        if (Objects.nonNull(category)) {
            model.addAttribute("category", category);
        }
        //文章细节
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE.getPageSize());
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
        User visitor = UserUtil.getUserFromCookie(request);
        model.addAttribute("visitor", visitor);
        return "category-details";
    }

    @ResponseBody
    @RequestMapping(value = "/care/{categoryId}")
    public Result careCategory(@PathVariable("categoryId") int categoryId,
                               @RequestParam("userId") int userId) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage("服务异常，请重试！");
        HashMap<String, Object> map = new HashMap<>(8);
        //更新关注数
        map.put("categoryCareNum", 1);
        try {
            categoryServiceImpl.updateCategoryInfo(categoryId, map);
            categoryCareService.saveCareInfo(userId, categoryId);
            result.setSuccess(true);
            result.setMessage("OK");
        } catch (Exception e) {
            return result;
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/cancelCare/{categoryId}")
    public Result cancelCare(@PathVariable("categoryId") int categoryId,
                             @RequestParam("userId") int userId) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage("服务异常，请重试！");
        HashMap<String, Object> map = new HashMap<>(8);
        map.put("categoryCareNum", -1);   //更新关注数
        try {
            categoryServiceImpl.updateCategoryInfo(categoryId, map);
            categoryCareService.deleteCareInfo(userId, categoryId);
            result.setSuccess(true);
            result.setMessage("OK");
        } catch (Exception e) {
            return result;
        }
        return result;
    }


}
