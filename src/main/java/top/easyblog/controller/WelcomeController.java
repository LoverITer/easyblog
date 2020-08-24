package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.User;
import top.easyblog.util.CollectionUtils;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author huangxin
 */
@Slf4j
@Controller
@RequestMapping(value = "/")
public class WelcomeController extends BaseController{


    /**
     * 点击量排序前8的文章数
     */
    private static final int TOP_EIGHT_ARTICLE = 8;

    /**
     * 默认推荐的文章列表长度
     */
    private static final int DEFAULT_RECOMMEND_ARTICLE_SIZE = 10;

    /**默认置顶显示的文章数*/
    private static final int DEFAULT_DISPLAY_HOT_ARTICLE_SIZE = 22;


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
            //查询最近1个月内的文章，不足10篇，查询历史的20篇
            PageInfo<Article> newestArticlesPages = articleService.getAllUserNewestArticlesPage(new PageParam(pageNo,
                    PageSize.DEFAULT_PAGE_SIZE));
            model.addAttribute("newestArticlesPages", newestArticlesPages);
            PageParam pageParam = new PageParam(pageNo, PageSize.SINGLE_ARTICLE);
            PageInfo<Article> articlePages = articleService.getUserAllPage(pageParam);
            int displayedSize = newestArticlesPages.getList().size();
            //总的文章大小
            model.addAttribute("articlePagesSize",Math.floorMod(articlePages.getTotal(),PageSize.MIN_PAGE_SIZE.getPageSize()));
            //已经显示的文章大小
            model.addAttribute("displayedSize",displayedSize);
            model.addAttribute("pageSize",PageSize.MIN_PAGE_SIZE);
            //查询访问量最高的22篇最近的文章用于首页大图、访问排行、特别推荐的显示
            List<List<?>> top22Articles = getTopNArticle(DEFAULT_DISPLAY_HOT_ARTICLE_SIZE,new int[]{5, 1, 7, 6});
            if(top22Articles!=null){
                model.addAttribute("articles", top22Articles.get(0));
                //访问排行侧边栏带首图显示的文章
                model.addAttribute("famousSideBarTopArticle", top22Articles.get(1));
                //访问排行侧边栏其他文章
                model.addAttribute("visitRankingArticles", top22Articles.get(2));
                model.addAttribute("specialRecommendArticles", top22Articles.get(3));
            }

            //文章推荐
            List<Article> allHistoryFamousArticles = articleService.getAllHistoryFamousArticles(DEFAULT_RECOMMEND_ARTICLE_SIZE);
            if (allHistoryFamousArticles != null && allHistoryFamousArticles.size() == DEFAULT_RECOMMEND_ARTICLE_SIZE) {
                List<List<?>> recommends = CollectionUtils.splitList(allHistoryFamousArticles, new int[]{1, 9});
                model.addAttribute("recommendTopic", recommends.get(0));
                model.addAttribute("recommend", recommends.get(1));
            }
            //给用用户推荐猜你喜欢的文章
            List<Article> likes = articleService.getYouMayAlsoLikeArticles();
            model.addAttribute("likes", likes);
        } catch (Exception e) {
            log.error( e.getMessage() + " @ index() line 78");
        }
        return "index";
    }

    /**
     * 查询几个模块共享的文章和信息
     * @param model  Model
     * @param request  HTTP请求对象
     * @param categories  分类模糊查询关键字
     */
    private void getSharedArticle(Model model, HttpServletRequest request, String[] categories) {
        //从Redis中尝试获取用户的登录信息
        User user = UserUtils.getUserFromCookie(request);
        model.addAttribute("user", user);
        //给用用户推荐猜你喜欢的文章
        List<Article> likes = articleService.getYouMayAlsoLikeArticles();
        model.addAttribute("likes", likes);
        //点击排行前8的文章
        List<List<?>> top10Articles = getTopNArticle(TOP_EIGHT_ARTICLE,new int[]{1, 7});
        if(top10Articles!=null){
            model.addAttribute("famousSideBarTopArticle", top10Articles.get(0));
            model.addAttribute("visitRankingArticles", top10Articles.get(1));
        }
        model.addAttribute(top10Articles);
        //推荐的文章，默认不需要排序
        List<Article> allArticles = articleService.getArticleByCategoryFuzzy(categories, false, -1);
        model.addAttribute("allArticles", allArticles);

        List<Article> sortedArticles=new ArrayList<>(allArticles);
        //排序后截取前DEFAULT_RECOMMEND_ARTICLE_SIZE篇文章
        Objects.requireNonNull(sortedArticles).sort((o1, o2) -> {
            if (o1 == null || o2 == null) {
                throw new IllegalArgumentException("Argument can not be null");
            }
            //按照点击量递减排序
            if (o1.getArticleClick().equals(o2.getArticleClick())) {
                return 0;
            } else if (o1.getArticleClick() > o2.getArticleClick()) {
                return -1;
            } else {
                return 1;
            }
        });
        //获取排序后的前10篇文章
        if (sortedArticles != null && allArticles.size() > DEFAULT_RECOMMEND_ARTICLE_SIZE) {
            model.addAttribute("recommendArticles", sortedArticles.subList(0, DEFAULT_RECOMMEND_ARTICLE_SIZE));
        } else {
            model.addAttribute("recommendArticles", sortedArticles);
        }
    }

    /**
     * 获取点击量Top N篇文章
     *
     * @param n         需要的文章篇数
     * @param splitArgs 分隔参数,不允许为null
     * @return java.util.List
     */
    private List<List<?>> getTopNArticle(int n, @NotBlank int[] splitArgs) {
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
        getSharedArticle(model, request, new String[]{"计算机", "设计模式", "软件工程", "网络","计算机网络", "数据结构", "算法", "操作系统",
                "Linux", "CentOS", "Windows","Mac OS","ios","Android"});
        model.addAttribute("type", "cb");
        return "index-category";
    }

    @GetMapping(value = "/cb/algorithm")
    public String indexCategoryDetailsOfAlgorithm(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"数据结构","算法"});
        model.addAttribute("type", "algorithm");
        return "index-category";
    }

    @GetMapping(value = "/cb/network")
    public String indexCategoryDetailsOfNetWork(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"网络","计网","计算机网络"});
        model.addAttribute("type", "network");
        return "index-category";
    }


    @GetMapping(value = "/cb/os")
    public String indexCategoryDetailsOfOS(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"操作系统", "Linux", "CentOS", "Windows","Mac OS","ios","Android"});
        model.addAttribute("type", "os");
        return "index-category";
    }

    @GetMapping(value = "/cb/poc")
    public String indexCategoryDetailsOfPCO(Model model, HttpServletRequest request) {
        /*getSharedArticle(model, request);*/
        model.addAttribute("type", "pco");
        return "index-category";
    }

    @GetMapping(value = "/cb/dp")
    public String indexCategoryDetailsOfDesignPattern(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"设计模式"});
        model.addAttribute("type", "dp");
        return "index-category";
    }

    @GetMapping(value = "/cb/se")
    public String indexCategoryDetailsOfSoftEngineering(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"软件工程", "软工"});
        model.addAttribute("type", "se");
        return "index-category";
    }

    @GetMapping(value = "/java")
    public String indexCategoryDetailsOfJava(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"java", "jvm", "servlet"});
        model.addAttribute("type", "java");
        return "index-category";
    }

    @GetMapping(value = "/framework")
    public String indexCategoryDetailsOfFramework(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"架构", "框架", "spring", "spring mvc", "spring boot", "redis", "nginx",
                "docker", "mybatis","spring cloud","netty"});
        model.addAttribute("type", "framework");
        return "index-category";
    }

    @GetMapping(value = "/framework/spring")
    public String indexCategoryDetailsOfSpring(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"spring"});
        model.addAttribute("type", "spring");
        return "index-category";
    }

    @GetMapping(value = "/framework/springmvc")
    public String indexCategoryDetailsOfSpringMVC(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"spring mvc"});
        model.addAttribute("type", "springmvc");
        return "index-category";
    }


    @GetMapping(value = "/framework/springboot")
    public String indexCategoryDetailsOfSpringBoot(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"spring boot"});
        model.addAttribute("type", "springboot");
        return "index-category";
    }

    @GetMapping(value = "/framework/mybatis")
    public String indexCategoryDetailsOfMyBatis(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"mybatis"});
        model.addAttribute("type", "mybatis");
        return "index-category";
    }

    @GetMapping(value = "/framework/redis")
    public String indexCategoryDetailsOfRedis(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"redis"});
        model.addAttribute("type", "redis");
        return "index-category";
    }

    @GetMapping(value = "/framework/nginx")
    public String indexCategoryDetailsOfNginx(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"nginx"});
        model.addAttribute("type", "nginx");
        return "index-category";
    }

    @GetMapping(value = "/framework/docker")
    public String indexCategoryDetailsOfDocker(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"docker"});
        model.addAttribute("type", "docker");
        return "index-category";
    }

    @GetMapping(value = "/framework/netty")
    public String indexCategoryDetailsOfNetty(Model model, HttpServletRequest request) {
        getSharedArticle(model, request,new String[]{"netty"});
        model.addAttribute("type", "netty");
        return "index-category";
    }

    @GetMapping(value = "/db")
    public String indexCategoryDetailsOfDataBase(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"数据库", "MySQL", "SQL","oracle","SQL Server"});
        model.addAttribute("type", "db");
        return "index-category";
    }

    @GetMapping(value = "/bigdata")
    public String indexCategoryDetailsOfCloudAndBigData(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"云计算", "大数据", "hadoop", "hbase", "spark","hive","Tachyon","Pig"});
        model.addAttribute("type", "bigdata");
        return "index-category";
    }

    @GetMapping(value = "/others")
    public String indexCategoryDetailsOfOthers(Model model, HttpServletRequest request) {
        getSharedArticle(model, request, new String[]{"git", "github", "maven", "druid","news","行业新闻","行业发展"});
        model.addAttribute("type", "others");
        return "index-category";
    }



    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }


}
