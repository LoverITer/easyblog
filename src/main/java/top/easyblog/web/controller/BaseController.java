package top.easyblog.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import top.easyblog.config.autoconfig.qiniu.QiNiuCloudService;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.ArticleCounter;
import top.easyblog.entity.po.Category;
import top.easyblog.global.enums.ArticleType;
import top.easyblog.util.NetWorkUtils;
import top.easyblog.util.RedisUtils;
import top.easyblog.web.service.*;
import top.easyblog.web.service.impl.UserPhoneLogServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Controller层抽取出来公共代用的方法
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/06/15 19:54
 */
@Slf4j
@Component
public abstract class BaseController {

    /**
     * 登录页面
     */
    protected static final String LOGIN_PAGE = "redirect:/user/login.html";
    /**
     * 404页面路径
     */
    protected static final String PAGE404 = "redirect:/error/404";
    /**
     * 错误页面
     */
    protected static final String ERROR_PAGE = "redirect:/error/error";
    /**
     * 用户登录标记
     */
    public static final String JSESSIONID = "JSESSIONID";
    /**
     * 用户信息标记
     */
    protected static final String REMEMBER_ME_COOKIE = "REMEMBER-ME-COOKIE";

    /**
     * 用户登录信息最大保存时间（免登陆最大时间）:60天
     */
    protected static final int MAX_USER_LOGIN_STATUS_KEEP_TIME = 60 * 60 * 24 * 60;

    /**
     * 浏览器保存用户名，密码90天
     */
    protected static final int REMEMBER_ME_TIME =60*60*24*90;
    /**
     * Redis数据库号
     */
    public static final RedisUtils.RedisDataBaseSelector REDIS_DB = RedisUtils.RedisDataBaseSelector.DB_1;
    @Autowired
    protected ICategoryService categoryService;
    @Autowired
    protected IArticleService articleService;
    @Autowired
    protected ICommentService commentService;
    @Autowired
    protected IUserAttentionService userAttention;
    @Autowired
    protected IUserService userService;
    @Autowired
    protected IUserSigninLogService userSigninLogService;
    @Autowired
    protected IUserAccountService userAccountService;
    @Autowired
    protected IUserEmailLogService userEmailLogService;
    @Autowired
    protected  UserPhoneLogServiceImpl userPhoneLogService;
    @Autowired
    protected QiNiuCloudService qiNiuCloudService;
    @Autowired
    protected RedisUtils redisUtil;
    @Autowired
    protected Executor executor;
    @Autowired
    protected HotWordService hotWordService;


    protected boolean isMobileDevice(HttpServletRequest request) {
        //检查用户的访问设备
        String userAgent = request.getHeader("User-Agent");
        return userAgent.contains("Android") || userAgent.contains("iPhone");
    }

    /**
     * 跳转到用户登录前的页面
     *
     * @param request HttpServletRequest
     * @return java.lang.String
     */
    protected String loginRedirectUrl(HttpServletRequest request) {
        String ip = NetWorkUtils.getInternetIPAddress(request);
        String refererUrl = (String) redisUtil.get("Referer-" + ip, REDIS_DB);
        if (Objects.nonNull(refererUrl) && !"".equals(refererUrl)) {
            //在每次取登录界面的时候都会在Redis中记录登录之前访问的页面Referer，登录成功后删除对应的Referer
            redisUtil.delete(REDIS_DB, "Referer-" + ip);
            log.info("redirect to : {}", refererUrl);
            return "redirect:" + refererUrl;
        }
        log.info("redirect to : index");
        return "redirect:/";
    }


    /***
     * 页面共享的数据
     * @param model    Model
     * @param userId   用户Id
     * @param articleType   文章类型
     */
    void getArticleUserInfo(Model model, int userId, String articleType) {
        try {
            //作者的所有允许显示的分类
            List<Category> lists = categoryService.getUserAllViableCategory(userId);
            //作者的所有归档
            List<ArticleCounter> archives = articleService.getUserAllArticleArchives(userId);
            //作者的最新文章5篇文章
            List<Article> newestArticles = articleService.getUserNewestArticles(userId, 5);
            //作者的原创文章数
            Article article = new Article();
            article.setArticleUser(userId);
            article.setArticleType(ArticleType.Original.getArticleType());
            int originalArticle = articleService.countSelective(article);
            //作者指定类型的所有文章
            List<Article> articles = articleService.getUserArticles(userId, articleType);
            //关于我的文章的评论数
            int receiveCommentNum = commentService.getReceiveCommentNum(userId);
            //我的关注数
            int attentionNumOfMe = userAttention.countAttentionNumOfMe(userId);
            model.addAttribute("attentionNumOfMe", attentionNumOfMe);
            model.addAttribute("receiveCommentNum", receiveCommentNum);
            model.addAttribute("categories", lists);
            model.addAttribute("archives", archives);
            model.addAttribute("newestArticles", newestArticles);
            model.addAttribute("articles", articles);
            model.addAttribute("articleNum", articles.size());
            model.addAttribute("originalArticleNum", originalArticle);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
