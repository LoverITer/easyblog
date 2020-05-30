package top.easyblog.controller.admin;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.easyblog.autoconfig.qiniu.QiNiuCloudService;
import top.easyblog.bean.Article;
import top.easyblog.bean.Category;
import top.easyblog.bean.User;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.common.util.DefaultImageDispatcherUtils;
import top.easyblog.common.util.RedisUtils;
import top.easyblog.common.util.UserUtils;
import top.easyblog.config.web.Result;
import top.easyblog.markdown.TextForm;
import top.easyblog.service.impl.ArticleServiceImpl;
import top.easyblog.service.impl.CategoryServiceImpl;
import top.easyblog.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;


/**
 * 博客后台文章管理
 *
 * @author HuangXin
 */
@Slf4j
@Controller
@RequestMapping(value = "/manage/blog")
public class ArticleAdminController {

    private final ArticleServiceImpl articleService;
    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;
    private final QiNiuCloudService qiNiuCloudService;
    private static final String BLOG_MANAGE_PAGE_PREFIX = "admin/blog_manage";
    private static final String LOGIN_PAGE = "redirect:/user/loginPage";
    private static final String ERROR_PAGE = "redirect:/error/error";
    @Autowired
    private RedisUtils redisUtil;
    @Autowired
    private Executor executor;

    public ArticleAdminController(ArticleServiceImpl articleService, CategoryServiceImpl categoryService, UserServiceImpl userService1, QiNiuCloudService qiNiuCloudService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.userService = userService1;
        this.qiNiuCloudService = qiNiuCloudService;
    }

    @GetMapping(value = "/")
    public String manageBlog(Model model,
                             @RequestParam(value = "userId") Integer userId,
                             @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        //从Redis中查询出已经登录User的登录信息
        String userJsonStr = (String) redisUtil.hget("user-" + userId, "user", 1);
        if (Objects.isNull(userJsonStr) || userJsonStr.length() <= 0) {
            //找不到直接重定位到登录页面
            return LOGIN_PAGE;
        }
        User user = JSON.parseObject(userJsonStr, User.class);
        if (Objects.nonNull(user)) {
            try {
                //去管理页面默认展示所有的已发布的文章
                PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE.getPageSize());
                Article article = new Article();
                article.setArticleUser(user.getUserId());
                PageInfo articlePages = articleService.getArticlesSelectivePage(article, pageParam);
                model.addAttribute("articlePages", articlePages);
                getShareInfo(model, user, "年", "月", "文章类型", "分类专栏");
                putArticleNumToModel(user, model);
                model.addAttribute("user", user);
                model.addAttribute("visitor", user);
                return BLOG_MANAGE_PAGE_PREFIX + "/blog-manage";
            } catch (Exception ex) {
                return ERROR_PAGE;
            }
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/search")
    public String searchByCondition(@RequestParam(defaultValue = "-1") Integer userId,
                                    @RequestParam(defaultValue = "不限") String year,
                                    @RequestParam(defaultValue = "不限") String month,
                                    @RequestParam(defaultValue = "不限") String articleType,
                                    @RequestParam(defaultValue = "不限") String categoryName,
                                    @RequestParam(defaultValue = "") String articleTopic,
                                    @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                    Model model) {
        //从Redis中查询出已经登录User的登录信息
        String userJsonStr = (String) redisUtil.hget("user-" + userId, "user", 1);
        if (Objects.isNull(userJsonStr) || userJsonStr.length() <= 0) {
            //找不到直接重定位到登录页面
            return LOGIN_PAGE;
        }
        User user = JSON.parseObject(userJsonStr, User.class);
        if (Objects.nonNull(user)) {
            try {
                Article article = new Article();
                //把userId传给service
                article.setArticleUser(user.getUserId());
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
                PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE.getPageSize());
                PageInfo<Article> articlePages = articleService.getArticlesSelectivePage(article, year, month, pageParam);
                model.addAttribute("articlePages", articlePages);
                model.addAttribute("articleTopic", articleTopic);
                model.addAttribute("showSearchCondition", true);
                getShareInfo(model, user, year, month, articleType, categoryName);
                //统计各种状态文章的数量
                putArticleNumToModel(user, model);
                return BLOG_MANAGE_PAGE_PREFIX + "/blog-manage";
            } catch (Exception ex) {
                return ERROR_PAGE;
            }
        }
        return LOGIN_PAGE;
    }

    /**
     * 写文章页面
     *
     * @param model  Model
     * @param userId 用户ID
     * @return java.lang.String
     */
    @RequestMapping(value = "/post")
    public String writeBlog(Model model,
                            @RequestParam(value = "userId", defaultValue = "-1") int userId) {
        //没有登录的话就去登录，登录后才可以写博客
        //从Redis中查询出已经登录User的登录信息
        String userJsonStr = (String) redisUtil.hget("user-" + userId, "user", 1);
        if (Objects.isNull(userJsonStr) || userJsonStr.length() <= 0) {
            //找不到直接重定位到登录页面
            return LOGIN_PAGE;
        }
        User user = JSON.parseObject(userJsonStr, User.class);
        if (Objects.isNull(user)) {
            return LOGIN_PAGE;
        }
        model.addAttribute("user", user);
        model.addAttribute("visitor", user);
        userId = userId == -1 ? user.getUserId() : userId;
        try {
            List<Category> categories = categoryService.getUserAllCategories(userId);
            model.addAttribute("categories", categories);
            model.addAttribute("userId", userId);
        } catch (Exception ex) {
            return ERROR_PAGE;
        }
        return BLOG_MANAGE_PAGE_PREFIX + "/blog-input";
    }

    /**
     * 发布文章
     *
     * @param article 文章类容
     * @param userId  用户ID
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveArticle/{userId}")
    public Result publishArticle(@RequestBody Article article,
                                 @PathVariable(value = "userId") Integer userId) {
        Result result = new Result();
        result.setMessage("请登录后重试！");
        //从Redis中查询出已经登录User的登录信息
        String userJsonStr = (String) redisUtil.hget("user-" + userId, "user", 1);
        if (Objects.isNull(userJsonStr) || userJsonStr.length() <= 0) {
            //找不到直接重定位到登录页面
            return result;
        }
        User user = JSON.parseObject(userJsonStr, User.class);
        if (Objects.nonNull(user)) {
            try {
                //根据分类名查用户的分类
                Category category = null;
                if (Objects.nonNull(article.getArticleCategory())) {
                    category = categoryService.getCategoryByUserIdAndName(userId, article.getArticleCategory());
                    //处理用户文章分类
                    if (Objects.isNull(category)) {
                        //数据库中用户没有这个分类就新建分类
                        category = new Category(userId, article.getArticleCategory(), DefaultImageDispatcherUtils.defaultCategoryImage(), 1, 0, 0, "1", "");
                        categoryService.saveCategory(category);
                    } else {
                        //数据库中用户有这个分类就更新该分类下的文章的数量
                        //id=-1标志这是一篇新文章
                        if (article.getArticleId() == -1) {
                            Category c0 = new Category();
                            c0.setCategoryId(category.getCategoryId());
                            int num = articleService.countUserArticleInCategory(userId, article.getArticleCategory());
                            c0.setCategoryArticleNum(num + 1);
                            categoryService.updateByPKSelective(c0);
                        }
                    }
                } else {
                    result.setMessage("请填写文章专栏名");
                    return result;
                }
                int updateRes = 0;
                //id=-1标志这是一篇新文章
                if (article.getArticleId() != -1) {
                    //更新已有的数据
                    updateRes = articleService.updateSelective(article);
                } else {
                    //让数据库自增ID
                    article.setArticleId(null);
                }

                //用户之前的可能把文章已经保存为草稿了，再次提交发布就更新
                if (updateRes == 0) {
                    //数据库新增一条记录
                    article.setArticleUser(userId);
                    article.setArticleTags("");
                    article.setArticleCommentNum(0);
                    article.setArticlePublishTime(new Date());
                    //如果更新影响的行数是0，那就直接保存文章
                    int createRes = articleService.saveArticle(article);
                    if (createRes > 0) {
                        executor.execute(() -> {
                            //根据文章不同的分类给用户加对应的积分
                            user.setUserScore(article.getArticleType());
                            user.setUserRank();
                            userService.updateUserInfo(user);
                        });
                        result.setSuccess(true);
                        //把文章的ID返回给页面
                        result.setMessage(article.getArticleId().toString());
                    }
                } else {
                    //编辑文章重新发布
                    result.setSuccess(true);
                    //把文章的ID返回给页面
                    result.setMessage(article.getArticleId().toString());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                result.setMessage("服务异常，请重试！");
            }
        }
        return result;
    }


    @ResponseBody
    @PostMapping(value = "/saveAsDraft/{userId}")
    public Result saveArticleAsDraft(Model model,
                                     @PathVariable(value = "userId") Integer userId,
                                     @RequestBody Article article) {
        Result result = new Result();
        result.setMessage("请登录后再操作！");
        //从Redis中查询出已经登录User的登录信息
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user) || userId != null) {
            try {
                int updateRes = 0;
                //尝试更新文章
                if (article.getArticleId() != -1) {
                    String categoryName = article.getArticleCategory();
                    if (Objects.nonNull(categoryName)) {
                        Category category = categoryService.getCategoryByUserIdAndName(userId, categoryName);
                        if (Objects.isNull(category)) {
                            categoryService.saveCategory(new Category(userId, article.getArticleCategory(), DefaultImageDispatcherUtils.defaultCategoryImage(), 1, 0, 0, "1", ""));
                        }
                    }
                    //让数据库自动分配ID
                    updateRes = articleService.updateSelective(article);
                }
                //更新发现没有再插入一条新纪录
                if (updateRes == 0) {
                    article.setArticleUser(userId);
                    article.setArticleTags("");
                    article.setArticleCommentNum(0);
                    article.setArticleTop("0");
                    article.setArticleClick(0);
                    article.setArticlePublishTime(new Date());
                    int createRes = articleService.saveArticle(article);
                    if (createRes > 0) {
                        result.setSuccess(true);
                        result.setMessage(article.getArticleId().toString());
                    } else {
                        result.setMessage("服务异常，请尝试重新提交！");
                    }
                } else {
                    result.setSuccess(true);
                    result.setMessage(article.getArticleId().toString());
                }
                List<Category> categories = categoryService.getUserAllCategories(userId);
                model.addAttribute("categories", categories);
                model.addAttribute("userId", userId);
            } catch (Exception e) {
                log.error(e.getMessage());
                result.setMessage("服务异常，请尝试重新提交！");
            }
        }
        return result;
    }


    @RequestMapping(value = "/success/{articleId}/{userId}")
    public String articlePublishSuccess(@PathVariable(value = "articleId") int articleId,
                                        @PathVariable(value = "userId") int userId,
                                        Model model) {
        //从Redis中查询出已经登录User的登录信息
        String userJsonStr = (String) redisUtil.hget("user-" + userId, "user", 1);
        if (Objects.isNull(userJsonStr) || userJsonStr.length() <= 0) {
            //找不到直接重定位到登录页面
            return LOGIN_PAGE;
        }
        User user = JSON.parseObject(userJsonStr, User.class);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            Article article = articleService.getArticleById(articleId, TextForm.TXT);
            model.addAttribute("article", article);
            return BLOG_MANAGE_PAGE_PREFIX + "/blog-input-success";
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/edit")
    public String editArticle(Model model,
                              @RequestParam(value = "userId") Integer userId,
                              @RequestParam("articleId") int articleId,
                              HttpServletRequest request) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            if (articleId < 0) {
                return "redirect:" + request.getHeader("Referer");
            }
            try {
                //得到文章
                Article article = articleService.getArticleById(articleId, TextForm.MARKDOWN);
                model.addAttribute("article", article);
                List<Category> categories = categoryService.getUserAllCategories(article.getArticleUser());
                model.addAttribute("categories", categories);
                model.addAttribute("userId", article.getArticleUser());
            } catch (Exception ex) {
                return ERROR_PAGE;
            }
            return BLOG_MANAGE_PAGE_PREFIX + "/blog-input";
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/delete")
    public String deleteArticle(@RequestParam("articleId") int articleId,
                                @RequestParam int userId,
                                HttpServletRequest request) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            try {
                //删除的文章暂时移动到垃圾桶
                updateArticle(articleId, "3");
                log.debug(request.getLocalAddr()+"@"+LocalDateTime.now()+" move article: ["+articleId+"] status to 3");
            } catch (Exception e) {
                return ERROR_PAGE;
            }
            return "redirect:/manage/blog/?userId=" + userId;
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/deleteDraft")
    public String deleteDraftArticle(@RequestParam("articleId") int articleId,
                                     @RequestParam int userId) {
        String updateStatus = deleteArticle2Dash(articleId);
        return Objects.isNull(updateStatus) ? "redirect:/manage/blog/draft?userId=" + userId : updateStatus;
    }

    @GetMapping(value = "/deletePrivateArticle")
    public String deletePrivateArticle(@RequestParam("articleId") int articleId,
                                       @RequestParam int userId) {
        String updateStatus = deleteArticle2Dash(articleId);
        return Objects.isNull(updateStatus) ? "redirect:/manage/blog/private?userId=" + userId : updateStatus;
    }

    @GetMapping(value = "/deletePublicArticle")
    public String deletePublicArticle(@RequestParam("articleId") int articleId,
                                      @RequestParam int userId) {
        String updateStatus = deleteArticle2Dash(articleId);
        return Objects.isNull(updateStatus) ? "redirect:/manage/blog/public?userId=" + userId : updateStatus;
    }


    @GetMapping(value = "/recycleToDraft")
    public String recycleArticleFromDash2Draft(@RequestParam int articleId,
                                               @RequestParam int userId) {
        try {
            updateArticle(articleId, "2");
        } catch (Exception e) {
            return ERROR_PAGE;
        }
        return "redirect:/manage/blog/dash?userId=" + userId;
    }

    private String deleteArticle2Dash(int articleId) {
        try {
            //删除的文章暂时放进垃圾桶
            updateArticle(articleId, "3");
        } catch (Exception e) {
            return ERROR_PAGE;
        }
        return null;
    }

    @GetMapping(value = "/deleteComplete")
    public String deleteComplete(@RequestParam("articleId") int articleId, @RequestParam int userId) {
        try {
            articleService.deleteByPK(articleId);
        } catch (Exception e) {
            return ERROR_PAGE;
        }
        return "redirect:/manage/blog/dash?userId=" + userId;
    }

    private void updateArticle(int articleId, String destArticleStatus) {
        Article article = new Article();
        article.setArticleId((long) articleId);
        article.setArticleStatus(destArticleStatus);
        articleService.updateSelective(article);
    }

    private void getShareInfo(Model model, User user, String currentYear, String month, String type, String category) {
        try {
            //文章作者信息
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            //文章作者的所有分类
            List<Category> categories = categoryService.getUserAllCategories(user.getUserId());
            model.addAttribute("categories", categories);
            //计算文章作者注册距今的时间
            Date registerTime = user.getUserRegisterTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(registerTime);
            int year = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date());
            List<Integer> years = new ArrayList<>();
            for (int i = year; i <= calendar.get(Calendar.YEAR); i++) {
                years.add(i);
            }
            model.addAttribute("years", years);
            model.addAttribute("months", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
            //下面的信息用户控制查询
            model.addAttribute("currentMonth", month);
            model.addAttribute("currentYear", currentYear);
            model.addAttribute("currentType", type);
            model.addAttribute("currentCategory", category);
        } catch (Exception e) {
            //
        }
    }


    @GetMapping(value = "/public")
    public String publicBlogPage(Model model,
                                 @RequestParam(value = "userId") Integer userId,
                                 @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        PageParam pageParam = new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE.getPageSize());
        return getArticles(model, "0", userId, "/blog-public", pageParam);
    }

    @GetMapping(value = "/private")
    public String privateBlogPage(Model model,
                                  @RequestParam(value = "userId") Integer userId,
                                  @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        PageParam pageParam = new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE.getPageSize());
        return getArticles(model, "1", userId, "/blog-private", pageParam);
    }

    @GetMapping(value = "/draft")
    public String draftBlog(Model model, @RequestParam(value = "userId") Integer userId, @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        PageParam pageParam = new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE.getPageSize());
        return getArticles(model, "2", userId, "/blog-draft-box", pageParam);
    }

    @GetMapping(value = "/dash")
    public String dashBlogPage(Model model, @RequestParam(value = "userId") Integer userId,
                               @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        PageParam pageParam = new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE.getPageSize());
        return getArticles(model, "3", userId, "/blog-dash", pageParam);
    }

    @ResponseBody
    @PostMapping(value = "/upload_article_img/{userId}")
    public Result editArticleFirstImg(@PathVariable(value = "userId") Integer userId,
                                      @RequestParam long articleId,
                                      @RequestParam String imgByte64Str) {
        Result result = new Result();
        result.setMessage("请登录后在操作！");
        if (Objects.isNull(userId)) {
            return result;
        }
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            try {
                if (Objects.nonNull(imgByte64Str) && articleId > 0) {
                    //把base64字符串转换为字节数组
                    byte[] imageBytes = Base64.decodeBase64(imgByte64Str.replace("data:image/jpeg;base64,", ""));
                    String imageName = System.currentTimeMillis() + UUID.randomUUID().toString() + ".jpg";
                    String imageUrl = qiNiuCloudService.putBase64Image(imageBytes, imageName);
                    if (Objects.nonNull(imageUrl)) {
                        Article article = new Article();
                        article.setArticleId(articleId);
                        article.setArticleFirstPicture(imageUrl);
                        int res = articleService.updateSelective(article);
                        if (res <= 0) {
                            result.setMessage("上传失败，请重试！");
                            return result;
                        }
                        result.setMessage("上传成功！");
                        result.setSuccess(true);
                    }
                } else {
                    result.setMessage("参数非法");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                result.setMessage("服务异常，请稍后重试！");
            }
        }
        return result;
    }


    /**
     * 返回不同状态的文章并且分页
     *
     * @param model         Model
     * @param articleStatus 文章状态
     * @param dest          目标页面
     * @param pageParam     分页参数
     * @return java.lang.String
     */
    private String getArticles(Model model,
                               String articleStatus,
                               Integer userId,
                               String dest,
                               PageParam pageParam) {
        //从Redis中查询出已经登录User的登录信息
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            try {
                Article article = new Article();
                article.setArticleUser(user.getUserId());
                article.setArticleStatus(articleStatus);
                PageInfo<Article> articlePages = articleService.getArticlesSelectivePage(article, pageParam);
                model.addAttribute("articlePages", articlePages);
                putArticleNumToModel(user, model);
                model.addAttribute("user", user);
                model.addAttribute("visitor", user);
                return BLOG_MANAGE_PAGE_PREFIX + dest;
            } catch (Exception e) {
                return ERROR_PAGE;
            }
        }
        return LOGIN_PAGE;
    }

    /**
     * 将各种状态的文章统计的数据放到Model中
     *
     * @param user  User对象
     * @param model Model
     */
    private void putArticleNumToModel(User user, Model model) {
        model.addAttribute("allArticles", getArticlesNum(user, null));
        model.addAttribute("publicArticles", getArticlesNum(user, "0"));
        model.addAttribute("privateArticles", getArticlesNum(user, "1"));
        model.addAttribute("draftArticles", getArticlesNum(user, "2"));
        model.addAttribute("dashArticles", getArticlesNum(user, "3"));
    }

    /**
     * 得到不同状态的文章数量
     *
     * @param user          User对象
     * @param articleStatus 文章的数量
     * @return 文章的数量
     */
    private int getArticlesNum(User user, String articleStatus) {
        Article article = new Article();
        article.setArticleUser(user.getUserId());
        article.setArticleStatus(articleStatus);
        return articleService.countSelective(article);
    }
}
