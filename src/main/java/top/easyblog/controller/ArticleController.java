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
import top.easyblog.bean.UserComment;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.pagehelper.PageSize;
import top.easyblog.service.impl.*;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/article")
public class ArticleController {


    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryServiceImpl;
    private final ArticleServiceImpl articleServiceImpl;
    private final CommentServiceImpl commentService;
    private final UserAttentionImpl userAttention;
    private final String PAGE404 = "redirect:/error/404";

    public ArticleController(CategoryServiceImpl categoryServiceImpl, UserServiceImpl userService, ArticleServiceImpl articleServiceImpl, CommentServiceImpl commentService, UserAttentionImpl userAttention) {
        this.categoryServiceImpl = categoryServiceImpl;
        this.userService = userService;
        this.articleServiceImpl = articleServiceImpl;
        this.commentService = commentService;
        this.userAttention = userAttention;
    }

    @RequestMapping(value = "/index/{userId}")
    public String index(@PathVariable("userId") int userId,
                        @RequestParam(value = "articleType", defaultValue = "3") int articleType,
                        Model model,
                        @RequestParam(value = "page",defaultValue = "1") int page) {
        try {
            ControllerUtils.getInstance(categoryServiceImpl, articleServiceImpl, commentService, userAttention).getArticleUserInfo(model, userId, articleType + "");
            final User user = userService.getUser(userId);
            PageParam pageParam = new PageParam(page, PageSize.DEFAULT_PAGE_SIZE.getPageSize());
            PageInfo articlePages = articleServiceImpl.getUserArticlesPage(userId, articleType + "", pageParam);
            model.addAttribute("articlePages", articlePages);
            user.setUserPassword(null);
            model.addAttribute("user", user);
            if (ArticleType.Original.getArticleType().equals(articleType + "")) {
                model.addAttribute("displayOnlyOriginal", "1");
            } else if (ArticleType.Unlimited.getArticleType().equals(articleType + "")) {
                model.addAttribute("displayOnlyOriginal", "0");
            }
            return "user_home";
        }catch (Exception ex){
            return "/error/error";
        }
    }


    @RequestMapping(value = "/home/{userId}")
    public String homePage(@PathVariable("userId") int userId, Model model) {
        try {
            User user = userService.getUser(userId);
            user.setUserPassword(null);
            List<Article> articles = articleServiceImpl.getUserArticles(userId, ArticleType.Unlimited.getArticleType());
            if (Objects.nonNull(articles)) {
                int articleSize = articles.size();
                //默认值显示15条数据
                if (articleSize < 15) {
                    model.addAttribute("articles", articles);
                } else {
                    model.addAttribute("articles", articles.subList(0, 15));
                }
                model.addAttribute("articleSize", articleSize);
                model.addAttribute("user", user);
                return "home";
            }
            return PAGE404;
        }catch (Exception e){
            return "redirect:/error";
        }
    }


    @GetMapping(value = "/details/{articleId}")
    public String articleDetails(@PathVariable("articleId") int articleId, Model model) {
        try {
            Article article = articleServiceImpl.getArticleById(articleId,"html");
            if (Objects.nonNull(article)) {
                model.addAttribute("article", article);
                List<UserComment> articleComments = commentService.getArticleComments(article.getArticleId());
                model.addAttribute("articleComments", articleComments);
                User user = userService.getUser(article.getArticleUser());
                if (Objects.nonNull(user)) {
                    user.setUserPassword(null);
                    model.addAttribute("user", user);
                    //更新用户的访问量
                    User user1 = new User();
                    user1.setUserId(user.getUserId());
                    user1.setUserVisit(user.getUserVisit() + 1);
                    userService.updateUserInfo(user1);
                    user1=null;
                    //更新文章的访问量
                    Article article1 = new Article();
                    article1.setArticleId(article.getArticleId());
                    article1.setArticleClick(article.getArticleClick() + 1);
                    articleServiceImpl.updateSelective(article1);
                    article=null;
                    ControllerUtils.getInstance(categoryServiceImpl, articleServiceImpl, commentService, userAttention).getArticleUserInfo(model, user.getUserId(), ArticleType.Original.getArticleType());
                    return "blog";
                }
            }
            return PAGE404;
        }catch (Exception e){
            return "redirect:/error/error";
        }
    }


}
