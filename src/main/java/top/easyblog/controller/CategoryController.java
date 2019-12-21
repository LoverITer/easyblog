package top.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.easyblog.bean.Article;
import top.easyblog.bean.Category;
import top.easyblog.bean.CategoryCare;
import top.easyblog.bean.User;
import top.easyblog.config.web.Result;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.service.impl.*;
import top.easyblog.commons.utils.HtmlParserUtil;
import top.easyblog.commons.utils.MarkdownUtil;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

    @GetMapping(value = "/{categoryId}/{userId}")
    public String categoryDetailsPage(HttpSession session,@PathVariable(value = "categoryId") int categoryId, @PathVariable("userId") int userId, Model model){
        ControllerUtils.getInstance(categoryServiceImpl,articleService,commentService,userAttention).getArticleUserInfo(model,userId, ArticleType.Original.getArticleType());
        final Category category = categoryServiceImpl.getCategory(categoryId);
        final List<CategoryCare> categoryCare = categoryCareService.getCategoryCare(categoryId);
        final List<Article> categoryArticles = articleService.getByCategoryAndUserId(userId, categoryId);
        final User user = userService.getUser(userId);
        model.addAttribute("care","false");

        categoryArticles.forEach(article -> {
            String htmlContent= MarkdownUtil.markdownToHtmlExtensions(article.getArticleContent());
            String textContent= HtmlParserUtil.HTML2Text(htmlContent);
            article.setArticleContent(textContent);
        });
        if(Objects.nonNull(categoryCare)) {
            categoryCare.forEach(ele -> {
                if (userId == ele.getCategoryCareUserId()) {
                    model.addAttribute("care", "true");
                }
            });
        }
        if(Objects.nonNull(category)) {
            model.addAttribute("category", category);
        }
        //文章细节
        model.addAttribute("categoryArticles",categoryArticles);
        user.setUserPassword(null);
        model.addAttribute("user",user);
        User user1 = (User) session.getAttribute("user");
        if(null!=user1){
            model.addAttribute("userId",user1.getUserId());
        }
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
