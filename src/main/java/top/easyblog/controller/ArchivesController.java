package top.easyblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.commons.ArticleType;
import top.easyblog.service.impl.*;
import top.easyblog.commons.utils.HtmlParserUtil;
import top.easyblog.commons.utils.MarkdownUtil;

import java.util.List;
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

    @RequestMapping(value = "/{userId}/{date}")
    public String archivesPage(@PathVariable("date") String date,
                               @PathVariable(value = "userId") int userId,
                               Model model) {
        model.addAttribute("defaultOrderFlag", true);
        new ControllerUtils(categoryServiceImpl, articleService, commentService, userAttention).getArticleUserInfo(model, userId, ArticleType.Original.getArticleType());
        List<Article> articles = articleService.getUserArticlesMonthly(userId, date.substring(0, 4), date.substring(5, 7));
        String page=orderArticles(model,userId,date,articles);
        return page==null?"archives":page;
    }


    @GetMapping(value = "orderByClickNum/{userId}/{date}")
    public String orderByClickNum(@PathVariable("userId") int userId,
                                  @PathVariable("date") String date,
                                  Model model) {
        model.addAttribute("orderByClickNumFlag", true);
        List<Article> articles = articleService.getUserArticlesMonthlyOrderByClickNum(userId, date.substring(0, 4), date.substring(5, 7));
        String page=orderArticles(model,userId,date,articles);
        return page==null?"archives":page;
    }

    @GetMapping(value = "orderByUpdateTime/{userId}/{date}")
    public String orderByUpdateTime(@PathVariable("userId") int userId,
                                    @PathVariable("date") String date,
                                    Model model) {
        model.addAttribute("orderByUpdateTimeFlag", true);
        final List<Article> articles = articleService.getUserArticlesMonthly(userId, date.substring(0, 4), date.substring(5, 7));
        String page=orderArticles(model,userId,date,articles);
        return page==null?"archives":page;
    }

    /**
     * 控制文章按指定规则排序
     * @param model   ModelAndView对象
     * @param userId  文章的用户的Id
     * @param date    年月
     * @param articles   查询出来的文章
     * @return
     */
    private String orderArticles(Model model,int userId,String date,List<Article> articles){
        try{
            new ControllerUtils(categoryServiceImpl, articleService, commentService, userAttention).getArticleUserInfo(model, userId, ArticleType.Original.getArticleType());
            model.addAttribute("date", date);
            if (Objects.nonNull(articles)) {
                articles.forEach(article -> {
                    String htmlContent = MarkdownUtil.markdownToHtmlExtensions(article.getArticleContent());
                    String textContent = HtmlParserUtil.HTML2Text(htmlContent);
                    article.setArticleContent(textContent);
                });
                model.addAttribute("articles", articles);
            }
            User user = userService.getUser(userId);
            if (Objects.nonNull(user)) {
                model.addAttribute("user", user);
            }else{
                return "redirect:/error/error";
            }
        }catch (Exception e){
            return "redirect:/error/error";
        }
        return null;
    }

}
