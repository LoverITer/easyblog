package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.common.util.CollectionUtils;
import top.easyblog.common.util.UserUtils;
import top.easyblog.service.IArticleService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author huangxin
 */
@Slf4j
@Controller
@RequestMapping(value = "/")
public class WelcomeController {

    private final IArticleService articleService;

    public WelcomeController(IArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 访问网站首页
     *
     * @param model
     * @param pageNo
     * @param request
     * @return
     */
    @GetMapping(value = "/")
    public String index(Model model, @RequestParam(defaultValue = "1") int pageNo, HttpServletRequest request) {
        try {
            User user = UserUtils.getUserFromCookie(request);
            model.addAttribute("user", user);
            //查询最近1个月内的文章
            PageInfo<Article> newestArticlesPages = articleService.getAllUserNewestArticlesPage(new PageParam(pageNo,
                    PageSize.DEFAULT_PAGE_SIZE.getPageSize()));
            model.addAttribute("newestArticlesPages", newestArticlesPages);
            //查询访问量最高的22篇最近的文章用于首页大图、访问排行、特别推荐的显示
            List<List<?>> top22Articles = getTopNArticle(22,new int[]{5, 1, 7, 6});
            if(top22Articles!=null){
                model.addAttribute("articles", top22Articles.get(0));
                //访问排行侧边栏带首图显示的文章
                model.addAttribute("famousSideBarTopArticle", top22Articles.get(1));
                //访问排行侧边栏其他文章
                model.addAttribute("visitRankingArticles", top22Articles.get(2));
                model.addAttribute("specialRecommendArticles", top22Articles.get(3));
            }

            //文章推荐
            List<Article> allHistoryFamousArticles = articleService.getAllHistoryFamousArticles(10);
            if (allHistoryFamousArticles != null && allHistoryFamousArticles.size() == 10) {
                List<List<?>> recommends = CollectionUtils.splitList(allHistoryFamousArticles, new int[]{1, 9});
                model.addAttribute("recommendTopic", recommends.get(0));
                model.addAttribute("recommend", recommends.get(1));
            }
            List<Article> likes = articleService.getYouMayAlsoLikeArticles();
            model.addAttribute("likes", likes);
        } catch (Exception e) {
            log.error(e.getMessage() + "@ index() line 78");
        }
        return "index";
    }

    /**
     * 查询几个模块共享的文章和信息
     */
    private void getSharedArticle(Model model, HttpServletRequest request) {
        //从Redis中尝试获取用户的登录信息
        User user = UserUtils.getUserFromCookie(request);
        model.addAttribute("user", user);
        //猜你喜欢的文章
        List<Article> likes = articleService.getYouMayAlsoLikeArticles();
        model.addAttribute("likes", likes);
        //点击排行前8的文章
        List<List<?>> top10Articles = getTopNArticle(8,new int[]{1, 7});
        if(top10Articles!=null){
            model.addAttribute("famousSideBarTopArticle", top10Articles.get(0));
            model.addAttribute("visitRankingArticles", top10Articles.get(1));
        }
        model.addAttribute(top10Articles);
    }

    /**
     * 获取点击量Top N篇文章
     *
     * @param n         需要的文章篇数
     * @param splitArgs 分隔参数,不允许为null
     */
    private List<List<?>> getTopNArticle(int n,@NotBlank int[] splitArgs) {
        List<List<?>> articles = null;
        //点击排行前n的文章
        List<Article> topArticles = articleService.getMostFamousArticles(n);
        if (topArticles == null || topArticles.size() < n) {
            //获取系统历史以来的n篇访问最高的文章
            topArticles = articleService.getAllHistoryFamousArticles(n);
        }
        if (topArticles != null && topArticles.size() == n) {
            articles = CollectionUtils.splitList(topArticles, splitArgs);
        }
        return articles;
    }

    @GetMapping(value = "/cb")
    public String indexCategoryDetailsOfComputerBase(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "cb");
        return "index-category";
    }

    @GetMapping(value = "/cb/algorithm")
    public String indexCategoryDetailsOfAlgorithm(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "algorithm");
        return "index-category";
    }

    @GetMapping(value = "/cb/network")
    public String indexCategoryDetailsOfNetWork(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "network");
        return "index-category";
    }


    @GetMapping(value = "/cb/os")
    public String indexCategoryDetailsOfOS(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "os");
        return "index-category";
    }

    @GetMapping(value = "/java")
    public String indexCategoryDetailsOfJava(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "java");
        return "index-category";
    }

    @GetMapping(value = "/framework")
    public String indexCategoryDetailsOfFramework(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "framework");
        return "index-category";
    }

    @GetMapping(value = "/framework/spring")
    public String indexCategoryDetailsOfSpring(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "spring");
        return "index-category";
    }

    @GetMapping(value = "/framework/springmvc")
    public String indexCategoryDetailsOfSpringMVC(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "springmvc");
        return "index-category";
    }


    @GetMapping(value = "/framework/springboot")
    public String indexCategoryDetailsOfSpringBoot(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "springboot");
        return "index-category";
    }

    @GetMapping(value = "/framework/mybatis")
    public String indexCategoryDetailsOfMyBatis(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "mybatis");
        return "index-category";
    }

    @GetMapping(value = "/framework/redis")
    public String indexCategoryDetailsOfRedis(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "redis");
        return "index-category";
    }

    @GetMapping(value = "/framework/nginx")
    public String indexCategoryDetailsOfNginx(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "nginx");
        return "index-category";
    }

    @GetMapping(value = "/framework/docker")
    public String indexCategoryDetailsOfDocker(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "docker");
        return "index-category";
    }

    @GetMapping(value = "/framework/netty")
    public String indexCategoryDetailsOfNetty(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "netty");
        return "index-category";
    }

    @GetMapping(value = "/db")
    public String indexCategoryDetailsOfDataBase(Model model, HttpServletRequest request) {
        User user = UserUtils.getUserFromCookie(request);
        model.addAttribute("user", user);
        model.addAttribute("type", "db");
        return "index-category";
    }

    @GetMapping(value = "/bigdata")
    public String indexCategoryDetailsOfCloudAndBigData(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "bigdata");
        return "index-category";
    }

    @GetMapping(value = "/others")
    public String indexCategoryDetailsOfOthers(Model model, HttpServletRequest request) {
        getSharedArticle(model, request);
        model.addAttribute("type", "others");
        return "index-category";
    }



    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }


}
