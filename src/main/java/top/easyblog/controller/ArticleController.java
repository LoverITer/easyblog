package top.easyblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.easyblog.common.enums.ArticleType;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.User;
import top.easyblog.entity.po.UserAccount;
import top.easyblog.entity.po.UserComment;
import top.easyblog.markdown.TextForm;
import top.easyblog.service.impl.UserAccountImpl;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author huangxin
 */
@Slf4j
@Controller
@RequestMapping("/article")
public class ArticleController extends BaseController {

    @Autowired
    private UserAccountImpl userAccount;

    /**
     * 关于我页面的默认文章显示数
     **/
    private static final int HOME_PAGE_DEFAULT_ARTICLE_SIZE = 15;


    /**
     * 访问个人博客首页<br/>
     * 其中visitor是访问用户，author是文章作者
     *
     * @param model       Model
     * @param userId      作者Id
     * @param articleType 文章类型 0原创文章 1转载文章 2翻译文章  3全部文章
     * @param page        分页参数-页数
     */
    @RequestMapping(value = "/index/{userId}")
    public String index(Model model,
                        HttpServletRequest request,
                        @PathVariable("userId") int userId,
                        @RequestParam(value = "articleType", defaultValue = "3") int articleType,
                        @RequestParam(value = "page", defaultValue = "1") int page) {
        User visitor = UserUtils.getUserFromCookie(request);
        model.addAttribute("visitor", visitor);
        try {
            getArticleUserInfo(model, userId, articleType + "");
            User author = userService.getUser(userId);
            PageParam pageParam = new PageParam(page, PageSize.DEFAULT_PAGE_SIZE);
            PageInfo articlePages = articleService.getUserArticlesPage(userId, articleType + "", pageParam);
            model.addAttribute("articlePages", articlePages);
            author.setUserPassword(null);
            model.addAttribute("author", author);
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
     * 访问关于我页面
     *
     * @param userId     用户Id
     * @param model
     * @param visitorUId 访问者用户Id
     */
    @RequestMapping(value = "/home/{userId}")
    public String homePage(@PathVariable("userId") int userId, Model model, @RequestParam(required = false) Integer visitorUId) {
        try {
            User author = userService.getUser(userId);
            author.setUserPassword(null);
            List<Article> articles = articleService.getUserArticles(userId, ArticleType.Unlimited.getArticleType());
            if (Objects.nonNull(articles)) {
                int articleSize = articles.size();
                //默认值显示15篇文章
                if (articleSize < HOME_PAGE_DEFAULT_ARTICLE_SIZE) {
                    model.addAttribute("articles", articles);
                } else {
                    model.addAttribute("articles", articles.subList(0, HOME_PAGE_DEFAULT_ARTICLE_SIZE));
                }
                model.addAttribute("articleSize", articleSize);
                model.addAttribute("author", author);
                String hobbyStr = "";
                String techStr = "";
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
                User visitor = UserUtils.getUserFromRedis(visitorUId);
                model.addAttribute("visitor", visitor);
                return "home";
            }
            return PAGE404;
        } catch (Exception e) {
            return "redirect:/error";
        }
    }


    /**
     * 文章内容页面
     *
     * @param articleId  文章ID
     * @param model      Model
     * @param visitorUId 访问者ID
     * @return
     */
    @GetMapping(value = "/details/{articleId}")
    public String articleDetails(@PathVariable("articleId") int articleId, Model model,
                                 @RequestParam(required = false) Integer visitorUId) {
        try {
            //根据id拿到文章
            Article article = articleService.getArticleById(articleId, TextForm.HTML);
            if (Objects.nonNull(article)) {
                model.addAttribute("article", article);
                //文章评论
                List<UserComment> articleComments = commentService.getArticleComments(article.getArticleId());
                model.addAttribute("articleComments", articleComments);
                //从Redis中查访客的登录信息
                User visitor = UserUtils.getUserFromRedis(visitorUId);
                model.addAttribute("visitor", visitor);

                User author = userService.getUser(article.getArticleUser());
                if (Objects.nonNull(author)) {
                    author.setUserPassword(null);
                    model.addAttribute("author", author);
                    //查询共享的信息
                    getArticleUserInfo(model, author.getUserId(), ArticleType.Original.getArticleType());
                    executor.execute(() -> {
                        //更新用户的访问量
                        User user1 = new User();
                        user1.setUserId(author.getUserId());
                        user1.setUserVisit(author.getUserVisit() + 1);
                        userService.updateUserInfo(user1);
                        //更新文章的访问量
                        Article article1 = new Article();
                        article1.setArticleId(article.getArticleId());
                        article1.setArticleClick(article.getArticleClick() + 1);
                        articleService.updateSelective(article1);
                    });
                }
                return "blog";
            }
            return PAGE404;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/error/error";
        }
    }

    /**
     * 首页异步请求文章
     *
     * @param pageNo 请求的文章页数
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/asyncGetArticles")
    public WebAjaxResult asyncGetArticles(@RequestParam(value = "page", defaultValue = "1") int pageNo,
                                          RedirectAttributes redirect,
                                          HttpServletRequest request) {
        WebAjaxResult result = new WebAjaxResult();
        try {
            User user = UserUtils.getUserFromCookie(request);
            PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE);
            PageInfo<Article> articlePages = articleService.getUserAllPage(pageParam);
            List<Article> articles = articlePages.getList();
            result.setSuccess(true);
            for(int i=0;i<articles.size();i++){
                result.setModel(i+"",articles.get(i));
            }
            result.setMessage(user==null?null: JSONObject.toJSONString(user.toString()));
        } catch (Exception e) {
            log.error(e.getMessage());
            redirect.addAttribute("error", "抱歉，数据加载异常！");
        }
        //只刷新index页面下的dynamic_articles片段
        //return "index::dynamic_articles";
        return result;
    }


}
