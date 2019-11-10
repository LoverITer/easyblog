package org.easyblog.controller.admin;


import org.easyblog.bean.Article;
import org.easyblog.bean.Category;
import org.easyblog.bean.User;
import org.easyblog.bean.enums.ArticleType;
import org.easyblog.config.Result;
import org.easyblog.service.impl.ArticleServiceImpl;
import org.easyblog.service.impl.CategoryServiceImpl;
import org.easyblog.service.impl.UserServiceImpl;
import org.easyblog.utils.FileUploadUtils;
import org.easyblog.utils.HtmlParserUtil;
import org.easyblog.utils.MarkdownUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;


/**
 * 后台博客管理
 */
@Controller
@RequestMapping(value = "/manage/blog")
public class ArticleAdminController {

    private final ArticleServiceImpl articleService;
    private final CategoryServiceImpl categoryService;
    private static final String PREFIX = "admin/blog_manage";

    public ArticleAdminController(ArticleServiceImpl articleService, CategoryServiceImpl categoryService, UserServiceImpl userService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
    }


    @GetMapping(value = "/")
    public String manageBlog(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            if (Objects.nonNull(user)) {
                //去管理页面默认展示所有的已发布的文章
                List<Article> articles = articleService.getUserArticles(user.getUserId(), ArticleType.Original.getArticleType());
                model.addAttribute("articles", articles);
                getShareInfo(model,user);
                putArticleNumToModel(user,model);
            } else {
                return "redirect:/user/loginPage";
            }

        } catch (Exception ex) {
            return "/error/error";
        }
        return PREFIX + "/blog-manage";
    }

    private void getShareInfo(Model model,User user){
        try {
            model.addAttribute("user", user);
            final List<Category> categories = categoryService.getUserAllCategories(user.getUserId());
            model.addAttribute("categories", categories);
            Date registerTime = user.getUserRegisterTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(registerTime);
            final int year = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date());
            List<Integer> years = new ArrayList<>();
            for (int i = year; i <= calendar.get(Calendar.YEAR); i++) {
                years.add(i);
            }
            model.addAttribute("years", years);
            model.addAttribute("months", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @GetMapping(value = "/search")
    public String searchByCondition(HttpSession session,
                                    @RequestParam(defaultValue = "不限") String year,
                                    @RequestParam(defaultValue = "不限") String month,
                                    @RequestParam(defaultValue = "不限") String articleType,
                                    @RequestParam(defaultValue = "不限") String categoryName,
                                    @RequestParam(defaultValue = "") String articleTopic,
                                    Model model) {
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            try {
                Article article = new Article();
                article.setArticleUser(user.getUserId());  //把userId传给service
                if ("不限".equals(year)) {
                    year = null;
                }
                if ("不限".equals(month)) {
                    month = null;
                }
                if (!"不限".equals(articleType)) {
                    article.setArticleType(articleType);
                }
                if (!"不限".equals(categoryName)) {
                    article.setArticleCategory(categoryName);
                }
                if (!"".equals(articleTopic)) {
                    article.setArticleTopic(articleTopic);
                }
                List<Article> articles = articleService.getArticlesSelective(article, year, month);
                model.addAttribute("articles",articles);
                getShareInfo(model,user);
                System.out.println(year+" "+month+" "+articleType+" "+categoryName);
                model.addAttribute("currentMonth",month+"");
                model.addAttribute("currentYear",year+"");
                model.addAttribute("currentType",articleType);
                model.addAttribute("currentCategory",categoryName);
                //统计各种状态文章的数量
                putArticleNumToModel(user,model);
                return PREFIX + "/blog-manage";
            } catch (Exception ex) {
                return "redirect:/error/error";
            }
        }
        return "redirect:/user/loginPage";
    }


    @RequestMapping(value = "/post")
    public String writeBlog(HttpSession session, Model model, @RequestParam(value = "userId", defaultValue = "-1", required = false) int userId) {
        //没有登录的话就去登录，登录后才可以写博客
        User user = (User) session.getAttribute("user");
        if (Objects.isNull(user)) {
            return "redirect:/user/loginPage";
        }
        if (userId == -1) {
            userId = user.getUserId();
        }
        try {
            final List<Category> categories = categoryService.getUserAllCategories(userId);
            model.addAttribute("categories", categories);
            model.addAttribute("userId", userId);
        } catch (Exception ex) {
            return "/error/error";
        }
        return PREFIX + "/blog-input";
    }


    @ResponseBody
    @PostMapping(value = "/saveArticle/{userId}")
    public Result saveArticle(@RequestBody Article article0,
                              @PathVariable(value = "userId") Integer userId) {
        Result result = new Result();
        result.setSuccess(false);
        String htmlContent = MarkdownUtil.markdownToHtmlExtensions(article0.getArticleContent());
        //不管有没有删除当前用户的这篇草稿
        try {
            articleService.deleteByUserIdAndTitle(userId, article0.getArticleTopic());
            final Article article = new Article(userId, article0.getArticleTopic(), new Date(), 0, article0.getArticleCategory(), article0.getArticleStatus(), "0", article0.getArticleType(), "", htmlContent, 0, article0.getArticleAppreciate());
            //根据分类名查用户的分类
            Category category = null;
            if (Objects.nonNull(article0.getArticleCategory())) {
                category = categoryService.getCategoryByUserIdAndName(userId, article0.getArticleCategory());
            } else {
                result.setSuccess(false);
                result.setMsg("文章分类名不可为空");
                return result;
            }
            if (Objects.isNull(category)) {
                category = new Category(userId, article0.getArticleCategory(), FileUploadUtils.defaultCategoryImage(), 1, 0, 0, "1","");
                categoryService.saveCategory(category);
            } else {
                Category category0 = new Category();
                category0.setCategoryId(category.getCategoryId());
                int num = articleService.countUserArticleInCategory(userId, article0.getArticleCategory());
                category0.setCategoryArticleNum(num + 1);
                categoryService.updateByPKSelective(category0);
            }
            articleService.saveArticle(article);
            result.setSuccess(true);
            result.setMsg(article.getArticleId() + "");  //把文章的ID返回给页面
            return result;
        } catch (Exception e) {
            result.setMsg("抱歉！服务异常，请重新提交！");
            return result;
        }
    }

    @RequestMapping(value = "/success/{articleId}")
    public String articlePublishSuccess(@PathVariable(value = "articleId") int articleId,
                                        Model model) {
        Article article = articleService.getArticleById(articleId);
        article.setArticleContent(HtmlParserUtil.HTML2Text(article.getArticleContent()));
        model.addAttribute("article", article);
        return PREFIX + "/blog-input-success";
    }


    @PostMapping(value = "/saveAsDraft/{userId}")
    public String saveAsDraft(@PathVariable(value = "userId") Integer userId,
                              @RequestParam(value = "content", defaultValue = "") String content,
                              @RequestParam(value = "title", defaultValue = "") String title,
                              Model model) {
        try {
            if (!"".equals(content) && !"".equals(title)) {
                String htmlContent = MarkdownUtil.markdownToHtmlExtensions(content);
                final Article article = new Article(userId, title, new Date(), 0, "", "2", "0", "0", "", htmlContent, 0, "0");
                articleService.saveArticle(article);
            }
            model.addAttribute("content", content);
            model.addAttribute("title", title);
            final List<Category> categories = categoryService.getUserAllCategories(userId);
            model.addAttribute("categories", categories);
            model.addAttribute("userId", userId);
        } catch (Exception e) {
            return "/error/error";
        }
        return PREFIX + "/blog-input";
    }

    @GetMapping(value = "/edit")
    public String editArticle(Model model, HttpServletRequest request, @RequestParam("articleId") int articleId) {
        if (articleId < 0) {
            String Referer = request.getHeader("Referer");
            return "redirect:" + Referer;
        }
        try {
            Article article = articleService.getArticleById(articleId);
            model.addAttribute("content", article.getArticleContent());
            model.addAttribute("title", article.getArticleTopic());
            final List<Category> categories = categoryService.getUserAllCategories(article.getArticleUser());
            model.addAttribute("categories", categories);
            model.addAttribute("userId", article.getArticleUser());
        } catch (Exception ex) {
            return "/error/error";
        }
        return PREFIX + "/blog-input";
    }


    @GetMapping(value = "/delete")
    public String deleteArticle(@RequestParam("articleId") int articleId) {
        try {
            updateArticle(articleId,"3");
        } catch (Exception e) {
            return "redirect:/error/error";
        }
        return "redirect:/manage/blog/";
    }

    @GetMapping(value = "/recycleToDraft")
    public String recycleArticleToDraft(int articleId){
        try{
            updateArticle(articleId,"2");
        }catch (Exception e){
            return "redirect:/error/error";
        }
        return "redirect:/manage/blog/";
    }

    private void updateArticle(int articleId,String destArticleStatus){
        final Article article = new Article();
        article.setArticleId((long) articleId);
        article.setArticleStatus(destArticleStatus);
        articleService.updateSelective(article);
    }

    @GetMapping(value = "/deleteComplete")
    public String deleteComplete(@RequestParam("articleId") int articleId){
        try{
            articleService.deleteByPK(articleId);
        }catch (Exception e){
            return "redirect:/error/error";
        }
        return "redirect:/manage/blog/";
    }



    @GetMapping(value = "/public")
    public String publicBlog(Model model,HttpSession session) {
       return getArticles(model,session,"0","/blog-public");
    }

    @GetMapping(value = "/private")
    public String privateBlog(Model model,HttpSession session) {
        return getArticles(model,session,"1","/blog-private");
    }

    @GetMapping(value = "/draft")
    public String draftBlog(Model model,HttpSession session) {
        return getArticles(model,session,"2","/blog-draft-box");
    }

    @GetMapping(value = "/dash")
    public String dashBlog(Model model,HttpSession session) {
        return getArticles(model,session,"3","/blog-dash");
    }


    private String getArticles(Model model,HttpSession session,String articleStatus,String dest){
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            try {
                final Article article = new Article();
                article.setArticleUser(user.getUserId());
                article.setArticleStatus(articleStatus);
                final List<Article> articles = articleService.getArticlesSelective(article, null, null);
                model.addAttribute("articles",articles);
                putArticleNumToModel(user,model);
                return PREFIX +dest;
            }catch (Exception e){
                return "redirect:/error/error";
            }
        }
        return "redirect:/user/loginPage";
    }

    /**
     * 将各种状态的文章统计的数据放到model中
     * @param user
     * @param model
     */
    private void putArticleNumToModel(User user,Model model){
        model.addAttribute("allArticles",getArticlesNum(user,null));
        model.addAttribute("publicArticles",getArticlesNum(user,"0"));
        model.addAttribute("privateArticles",getArticlesNum(user,"1"));
        model.addAttribute("draftArticles",getArticlesNum(user,"2"));
        model.addAttribute("dashArticles",getArticlesNum(user,"3"));
    }

    /**
     * 得到不同状态的文章数量
     * @param user
     * @param articleStatus
     * @return
     */
    private int  getArticlesNum(User user,String articleStatus){
        final Article article = new Article();
        article.setArticleUser(user.getUserId());
        article.setArticleStatus(articleStatus);
        return articleService.countSelective(article);
    }
}