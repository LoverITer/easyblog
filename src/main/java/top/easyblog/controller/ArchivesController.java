package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.common.enums.ArticleType;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.User;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 博客归档页面
 *
 * @author huangxin
 */
@Controller
@RequestMapping(value = "/archives/details")
public class ArchivesController extends BaseController{


    @RequestMapping(value = {"/{userId}/{date}", "orderByUpdateTime/{userId}/{date}"})
    public String archivesDefaultDetailsPage(@PathVariable("date") String date,
                                             @PathVariable(value = "userId") int userId,
                                             HttpServletRequest request,
                                             @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                             Model model) {
        model.addAttribute("defaultOrderFlag", true);
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE);
        PageInfo<Article> articles = articleService.getUserArticlesMonthlyPage(userId, date.substring(0, 4), date.substring(5, 7), pageParam);
        return orderArticles(model, request, userId, date, articles);
    }


    @GetMapping(value = {"orderByClickNum/{userId}/{date}"})
    public String archivesDetailsPageOrderByClickNum(@PathVariable("userId") int userId,
                                                     @PathVariable("date") String date,
                                                     @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                                     HttpServletRequest request,
                                                     Model model) {
        model.addAttribute("orderByClickNumFlag", true);
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE);
        PageInfo<Article> articles = articleService.getUserArticlesMonthlyOrderByClickNumPage(userId, date.substring(0, 4), date.substring(5, 7), pageParam);
        return orderArticles(model, request, userId, date, articles);
    }


    @GetMapping(value = "orderByUpdateTime/{userId}/{date}")
    public String archivesDetailsPageOrderByUpdateTime(@PathVariable("userId") int userId,
                                                       @PathVariable("date") String date,
                                                       @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                                       HttpServletRequest request,
                                                       Model model) {
        model.addAttribute("orderByUpdateTimeFlag", true);
        PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE);
        PageInfo<Article> articles = articleService.getUserArticlesMonthlyPage(userId, date.substring(0, 4), date.substring(5, 7), pageParam);
        return orderArticles(model, request, userId, date, articles);
    }

    /**
     * 控制文章按指定规则排序
     *
     * @param model    ModelAndView对象
     * @param userId   文章的用户的Id
     * @param date     年月
     * @param articles 文章分页的信息
     * @return 返回即将跳转的页面
     */
    private String orderArticles(Model model, HttpServletRequest request, int userId, String date, PageInfo<Article> articles) {
        try {
            getArticleUserInfo(model, userId, ArticleType.Original.getArticleType());
            model.addAttribute("date", date);
            model.addAttribute("articlePages", articles);
            //作者信息
            User author = userService.getUser(userId);
            if (Objects.nonNull(author)) {
                model.addAttribute("author", author);
            } else {
                return "redirect:/error/404";
            }
            User visitor = UserUtils.getUserFromCookie(request);
            model.addAttribute("visitor", visitor);
        } catch (Exception e) {
            return "redirect:/error/error";
        }
        return "archives";
    }

}
