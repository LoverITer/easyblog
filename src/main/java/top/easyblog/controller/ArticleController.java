package top.easyblog.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.bean.UserAccount;
import top.easyblog.bean.UserComment;
import top.easyblog.commons.enums.ArticleType;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.pagehelper.PageSize;
import top.easyblog.commons.utils.RedisUtils;
import top.easyblog.service.impl.*;

import java.util.List;
import java.util.Objects;

/**
 * @author huangxin
 */
@Controller
@RequestMapping("/article")
public class ArticleController {


    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryServiceImpl;
    private final ArticleServiceImpl articleServiceImpl;
    private final CommentServiceImpl commentService;
    private final UserAttentionImpl userAttention;
    private final UserAccountImpl userAccount;
    @Autowired
    RedisUtils redisUtil;
    private final String PAGE404 = "redirect:/error/404";

    public ArticleController(CategoryServiceImpl categoryServiceImpl, UserServiceImpl userService, ArticleServiceImpl articleServiceImpl, CommentServiceImpl commentService, UserAttentionImpl userAttention, UserAccountImpl userAccount) {
        this.categoryServiceImpl = categoryServiceImpl;
        this.userService = userService;
        this.articleServiceImpl = articleServiceImpl;
        this.commentService = commentService;
        this.userAttention = userAttention;
        this.userAccount = userAccount;
    }

    @RequestMapping(value = "/index/{userId}")
    public String index(@PathVariable("userId") int userId,
                        @RequestParam(value = "articleType", defaultValue = "3") int articleType,
                        Model model,
                        @RequestParam(value = "page", defaultValue = "1") int page) {
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
        } catch (Exception ex) {
            return "/error/error";
        }
    }


    /**
     * 关于我页面
     *
     * @param userId     用户Id
     * @param model
     * @param visitorUId 访问者用户Id
     * @return
     */
    @RequestMapping(value = "/home/{userId}")
    public String homePage(@PathVariable("userId") int userId, Model model, @RequestParam(required = false) Integer visitorUId) {
        try {
            User author = userService.getUser(userId);
            author.setUserPassword(null);
            List<Article> articles = articleServiceImpl.getUserArticles(userId, ArticleType.Unlimited.getArticleType());
            if (Objects.nonNull(articles)) {
                int articleSize = articles.size();
                //默认值显示15篇文章
                if (articleSize < 15) {
                    model.addAttribute("articles", articles);
                } else {
                    model.addAttribute("articles", articles.subList(0, 15));
                }
                model.addAttribute("articleSize", articleSize);
                model.addAttribute("user", author);
                String hobbyStr;
                String techStr;
                if (Objects.nonNull(hobbyStr = author.getUserHobby())) {
                    //先用英文的逗号切分
                    String[] hobbies = hobbyStr.replaceAll("，", ",").split(",");
                    model.addAttribute("userHobby", hobbies);
                }
                if (Objects.nonNull(techStr = author.getUserTech())) {
                    String[] techs = techStr.replaceAll("，", ",").split(",");
                    model.addAttribute("userTech", techs);
                }
                //帮助页面正常显示
                if ("".equals(author.getUserTech())) {
                    model.addAttribute("userTech", null);
                }
                //作者的各种联系方式
                UserAccount authorAccounts = userAccount.getAccountByUserId(author.getUserId());
                model.addAttribute("authorAccounts", authorAccounts);
                //从Redis中查用户的登录信息
                User visitor = null;
                if (Objects.nonNull(visitorUId) && visitorUId > 0) {
                    String userJsonStr = (String) redisUtil.hget("user-" + visitorUId, "user", 1);
                    if (Objects.nonNull(userJsonStr)) {
                        visitor = JSON.parseObject(userJsonStr, User.class);
                    }
                }
                if (Objects.nonNull(visitor)) {
                    model.addAttribute("visitorUId", visitorUId);
                }
                return "home";
            }
            return PAGE404;
        } catch (Exception e) {
            return "redirect:/error";
        }
    }


    @GetMapping(value = "/details/{articleId}")
    public String articleDetails(@PathVariable("articleId") int articleId, Model model, @RequestParam(required = false) Integer visitorUId) {
        try {
            //根据id拿到文章
            Article article = articleServiceImpl.getArticleById(articleId, "html");
            if (Objects.nonNull(article)) {
                model.addAttribute("article", article);
                List<UserComment> articleComments = commentService.getArticleComments(article.getArticleId());
                model.addAttribute("articleComments", articleComments);
                //从Redis中查用户的登录信息
                User visitor = null;
                if (Objects.nonNull(visitorUId) && visitorUId > 0) {
                    String userJsonStr = (String) redisUtil.hget("user-" + visitorUId, "user", 1);
                    if (Objects.nonNull(userJsonStr)) {
                        visitor = JSON.parseObject(userJsonStr, User.class);
                    }
                }
                if (Objects.nonNull(visitor)) {
                    model.addAttribute("userId", visitor.getUserId());
                }
                User author = userService.getUser(article.getArticleUser());
                //更新文章作者的信息
                if (Objects.nonNull(author)) {
                    author.setUserPassword(null);
                    model.addAttribute("user", author);
                    //更新用户的访问量
                    User user1 = new User();
                    user1.setUserId(author.getUserId());
                    user1.setUserVisit(author.getUserVisit() + 1);
                    userService.updateUserInfo(user1);
                    //更新文章的访问量
                    Article article1 = new Article();
                    article1.setArticleId(article.getArticleId());
                    article1.setArticleClick(article.getArticleClick() + 1);
                    articleServiceImpl.updateSelective(article1);
                    ControllerUtils.getInstance(categoryServiceImpl, articleServiceImpl, commentService, userAttention).getArticleUserInfo(model, author.getUserId(), ArticleType.Original.getArticleType());
                    return "blog";
                }
            }
            return PAGE404;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error/error";
        }
    }


}
