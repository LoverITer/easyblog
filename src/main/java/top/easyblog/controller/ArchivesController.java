package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.pagehelper.PageSize;
import top.easyblog.service.impl.*;

import java.util.Objects;

@Controller
@RequestMapping(value = "/archives/details")
public class ArchivesController {

    private final ArticleServiceImpl articleService;
    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryServiceImpl;
    private final CommentServiceImpl commentService;
    private final UserAttentionImpl userAttention;

    public ArchivesController(ArticleServiceImpl articleService, UserServiceImpl userService, CategoryServiceImpl categoryServiceImpl, CommentServiceImpl commentService, UserAttentionImpl userAttention) {
        this.articleService = articleService;
        this.userService = userService;
        this.categoryServiceImpl = categoryServiceImpl;
        this.commentService = commentService;
        this.userAttention = userAttention;
    }


    @RequestMapping(value = {"/{userId}/{date}","orderByUpdateTime/{userId}/{date}"})
    public String archivesDefaultDetailsPage(@PathVariable("date") String date,
                                      @PathVariable(value = "userId") int userId,
                                      @RequestParam(value = "page",defaultValue = "1") int pageNo,
                                      Model model) {
        model.addAttribute("defaultOrderFlag", true);
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE.getPageSize());
        PageInfo<Article> articles = articleService.getUserArticlesMonthlyPage(userId, date.substring(0, 4), date.substring(5, 7), pageParam);
        String page = orderArticles(model, userId, date, articles);
        return page == null ? "archives" : page;
    }


    @GetMapping(value = {"orderByClickNum/{userId}/{date}"})
    public String archivesDetailsPageOrderByClickNum(@PathVariable("userId") int userId,
                                                     @PathVariable("date") String date,
                                                     @RequestParam(value = "page",defaultValue = "1") int pageNo,
                                                     Model model) {
        model.addAttribute("orderByClickNumFlag", true);
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE.getPageSize());
        PageInfo<Article> articles = articleService.getUserArticlesMonthlyOrderByClickNumPage(userId, date.substring(0, 4), date.substring(5, 7), pageParam);
        String page=orderArticles(model,userId,date,articles);
        return page==null?"archives":page;
    }



    @GetMapping(value = "orderByUpdateTime/{userId}/{date}")
    public String archivesDetailsPageOrderByUpdateTime(@PathVariable("userId") int userId,
                                                       @PathVariable("date") String date,
                                                       @RequestParam(value = "page",defaultValue = "1") int pageNo,
                                                       Model model) {
        model.addAttribute("orderByUpdateTimeFlag", true);
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE.getPageSize());
        PageInfo<Article> articles = articleService.getUserArticlesMonthlyPage(userId, date.substring(0, 4), date.substring(5, 7),pageParam);
        String page=orderArticles(model,userId,date,articles);
        return page==null?"archives":page;
    }

    /**
     * 控制文章按指定规则排序
     * @param model   ModelAndView对象
     * @param userId  文章的用户的Id
     * @param date    年月
     * @param articles   文章分页的信息
     * @return  正常情况下返回null（主调会根据null返回归档页面）
     */
    private String orderArticles(Model model,int userId,String date,PageInfo<Article> articles){
        try{
            ControllerUtils.getInstance(categoryServiceImpl, articleService, commentService, userAttention).getArticleUserInfo(model, userId, ArticleType.Original.getArticleType());
            model.addAttribute("date", date);
            model.addAttribute("articlePages", articles);
            User user = userService.getUser(userId);
            if (Objects.nonNull(user)) {
                model.addAttribute("user", user);
            }else{
                return "redirect:/error/404";
            }
        }catch (Exception e){
            return "redirect:/error/error";
        }
        return null;
    }

}
