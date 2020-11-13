package top.easyblog.web.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.User;
import top.easyblog.global.pagehelper.PageParam;
import top.easyblog.global.pagehelper.PageSize;
import top.easyblog.util.CollectionUtils;
import top.easyblog.util.CookieUtils;
import top.easyblog.util.SpecialTopicUtils;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author huangxin
 */
@Slf4j
@Controller
@RequestMapping(value = "/")
public class WelcomeController extends BaseController {


    /***点击量排序前8的文章数*/
    private static final int TOP_EIGHT_ARTICLE = 10;
    /***默认推荐的文章列表长度 */
    private static final int DEFAULT_RECOMMEND_ARTICLE_SIZE = 10;
    /**
     * 默认置顶显示的文章数
     */
    private static final int DEFAULT_DISPLAY_HOT_ARTICLE_SIZE = 18;


    @GetMapping(value = "/")
    public String index(Model model, HttpServletRequest request) {
        try {
            String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
            User user = UserUtils.getUserFromRedis(sessionId);
            model.addAttribute("user", user);
            //检查用户的访问设备
            if (isMobileDevice(request)) {
                model.addAttribute("mobileDevice", true);
            } else {
                model.addAttribute("mobileDevice", false);
            }
            //查询最近1个月内的文章，不足10篇，查询历史的20篇
            PageInfo<Article> newestArticlesPages = articleService.getAllUserNewestArticlesPage(new PageParam(1,
                    PageSize.DEFAULT_PAGE_SIZE));
            model.addAttribute("newestArticlesPages", newestArticlesPages);
            PageParam pageParam = new PageParam(1, PageSize.SINGLE_ARTICLE);
            PageInfo<Article> articlePages = articleService.getUserAllPage(pageParam);
            int displayedSize = newestArticlesPages.getList().size();
            //总的文章数量
            model.addAttribute("articlePagesSize", (int) Math.ceil((double) articlePages.getTotal() / PageSize.MIN_PAGE_SIZE.getPageSize()));
            //已经显示的文章数量
            model.addAttribute("displayedSize", displayedSize);
            //每次点击“阅读更多”后加载5篇文章
            model.addAttribute("pageSize", PageSize.MIN_PAGE_SIZE.getPageSize());
            //查询访问量最高的18篇最近的文章用于首页大图、访问排行、特别推荐的显示
            List<List<?>> topVisitArticles = getTopArticle(18, new int[]{5, 10, 3});
            if (topVisitArticles != null) {
                model.addAttribute("articles", topVisitArticles.get(0));
                //访问排行文章
                model.addAttribute("famousSideBarTopArticle", topVisitArticles.get(1));
                //推荐的文章
                model.addAttribute("specialRecommendArticles", topVisitArticles.get(2));
            }

            //文章推荐
            List<Article> allHistoryFamousArticles = articleService.getAllHistoryFamousArticles(7);
            model.addAttribute("recommendArticles", allHistoryFamousArticles);
            //热门搜索
            List<String> hotList = hotWordService.getHotList(null);
            model.addAttribute("hotList", hotList);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "index";
    }


    /**
     * 解析专题和文章的URL
     *
     * @return
     */
    private Map<String, String> matchFeatureArticleAndUrl(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        String contextPath = request.getServletPath();
        String[] urls = contextPath.split("/");
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < urls.length; i++) {
            if (!StringUtils.isEmpty(urls[i])) {
                path.append("/").append(urls[i]);
                map.put(path.toString(), getFeatureName(urls[i].replaceAll("/", "")));
            }
        }
        return map;
    }

    /**
     * 通过请求路径上的关键字判断当前用户访问的专题名称
     *
     * @param url
     * @return
     */
    private String getFeatureName(String url) {
        StringBuilder name = new StringBuilder();
        switch (url) {
            case "cb":
                name.append("计算机基础");
                break;
            case "algorithm":
                name.append("数据结构与算法");
                break;
            case "network":
                name.append("计算机网络");
                break;
            case "os":
                name.append("操作系统");
                break;
            case "dp":
                name.append("设计模式");
                break;
            case "se":
                name.append("软件工程");
                break;
            case "java":
                name.append("Java");
                break;
            case "framework":
                name.append("架构/框架");
                break;
            case "spring":
                name.append("Spring");
                break;
            case "springmvc":
                name.append("Spring MVC");
                break;
            case "springboot":
                name.append("Spring Boot");
                break;
            case "mybatis":
                name.append("MyBatis");
                break;
            case "redis":
                name.append("Redis");
                break;
            case "nginx":
                name.append("Nginx");
                break;
            case "netty":
                name.append("Netty");
                break;
            case "docker":
                name.append("Docket");
                break;
            case "db":
                name.append("数据库");
                break;
            case "bigdata":
                name.append("云计算/大数据");
                break;
            case "others":
                name.append("其它技术");
                break;
            default:
                break;
        }
        return name.toString();
    }

    /**
     * 获取某个专题下的文章
     *
     * @param model      向页面传值的Model
     * @param request    HTTP请求对象
     * @param categories 分类模糊查询关键字
     */
    private void dispatcher(Model model, HttpServletRequest request, String[] categories) {
        //从Redis中尝试获取用户的登录信息
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User user = UserUtils.getUserFromRedis(sessionId);
        model.addAttribute("user", user);
        //检查用户的访问设备
        if (isMobileDevice(request)) {
            model.addAttribute("mobileDevice", true);
        } else {
            model.addAttribute("mobileDevice", false);
        }
        Map<String, String> map = matchFeatureArticleAndUrl(request);
        model.addAttribute("featureAndUrlMap", map);
        //以最后一个作为当前访问的专题的key
        Iterator<String> it = map.keySet().iterator();
        String type = "";
        while (it.hasNext()) {
            String[] split = it.next().split("/");
            type = split[split.length - 1];
        }
        String description = SpecialTopicUtils.getSpecialTopicDescriptionOf(type);
        String imgUrl = SpecialTopicUtils.getSpecialTopicImgOf(type);
        model.addAttribute("imgUrl", imgUrl);
        model.addAttribute("description", description);
        //查询某个专题下全部文章
        List<Article> allArticles = articleService.getArticleByCategoryFuzzy(categories, false, -1);
        model.addAttribute("allArticles", allArticles);
        List<Article> sortedArticles = new ArrayList<>(allArticles);
        if (allArticles != null) {
            //对文章进行排序
            sortedArticles.sort((o1, o2) -> {
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
            if (allArticles.size() > DEFAULT_RECOMMEND_ARTICLE_SIZE) {
                model.addAttribute("hotArticles", sortedArticles.subList(0, DEFAULT_RECOMMEND_ARTICLE_SIZE));
            } else {
                model.addAttribute("hotArticles", sortedArticles);
            }
            //文章推荐
            List<Article> allHistoryFamousArticles = articleService.getAllHistoryFamousArticles(7);
            model.addAttribute("recommendArticles", allHistoryFamousArticles);
            //热门搜索
            List<String> hotList = hotWordService.getHotList(null);
            model.addAttribute("hotList", hotList);
        }
    }


    /**
     * 获取点击量Top N篇文章
     *
     * @param n         需要的文章篇数
     * @param splitArgs 分隔参数
     * @return java.util.List
     */
    private List<List<?>> getTopArticle(int n, int[] splitArgs) {
        List<List<?>> articles = new ArrayList<>();
        //点击排行前n的文章
        List<Article> topArticles = articleService.getMostFamousArticles(n);
        if (topArticles == null || topArticles.size() < n) {
            //获取系统历史以来的n篇访问最高的文章
            topArticles = articleService.getAllHistoryFamousArticles(n);
        }
        if (topArticles != null && topArticles.size() == n) {
            //分页参数为null，直接返回查询到的数据
            if (splitArgs == null) {
                articles.add(topArticles);
                return articles;
            }
            articles = CollectionUtils.splitList(topArticles, splitArgs);
        }
        return articles;
    }

    @GetMapping(value = {"/cb", "/cb/{category}"})
    public String indexCategoryDetailsOfComputerBase(Model model,
                                                     HttpServletRequest request,
                                                     @PathVariable(value = "category", required = false) String category) {
        if (category == null || "".equals(category)) {
            dispatcher(model, request, new String[]{"计算机", "设计模式", "软件工程", "网络", "计算机网络", "数据结构", "算法", "操作系统",
                    "Linux", "CentOS", "Windows", "Mac OS", "ios", "Android"});
        } else {
            switch (category) {
                case "algorithm":
                    dispatcher(model, request, new String[]{"数据结构", "算法"});
                    break;
                case "network":
                    dispatcher(model, request, new String[]{"网络", "计网", "计算机网络"});
                    break;
                case "os":
                    dispatcher(model, request, new String[]{"操作系统", "Linux", "CentOS", "Windows", "Mac OS", "ios", "Android"});
                    break;
                case "dp":
                    dispatcher(model, request, new String[]{"设计模式"});
                    break;
                case "se":
                    dispatcher(model, request, new String[]{"软件工程", "软工"});
                    break;
                default:
                    log.error("error request path");
            }
        }

        return "special-topic";
    }

    @GetMapping(value = "/java")
    public String indexCategoryDetailsOfJava(Model model, HttpServletRequest request) {
        dispatcher(model, request, new String[]{"java", "jvm", "servlet"});
        return "special-topic";
    }


    @GetMapping(value = {"/framework/{category}", "/framework"})
    public String indexCategoryDetailsOfFramework(Model model,
                                                  HttpServletRequest request,
                                                  @PathVariable(value = "category", required = false) String category) {
        if (category == null || "".equals(category)) {
            dispatcher(model, request, new String[]{"架构", "框架", "spring", "spring mvc", "spring boot", "redis", "nginx",
                    "docker", "mybatis", "spring cloud", "netty"});
        } else {
            switch (category) {
                case "spring":
                    dispatcher(model, request, new String[]{"spring"});
                    break;
                case "springmvc":
                    dispatcher(model, request, new String[]{"spring mvc"});
                    break;
                case "springboot":
                    dispatcher(model, request, new String[]{"spring boot", "springboot", "Spring Boot", "SpringBoot"});
                    break;
                case "mybatis":
                    dispatcher(model, request, new String[]{"mybatis", "MyBatis", "iBatis"});
                    break;
                case "redis":
                    dispatcher(model, request, new String[]{"redis", "Redis"});
                    break;
                case "nginx":
                    dispatcher(model, request, new String[]{"nginx", "Nginx"});
                    break;
                case "docker":
                    dispatcher(model, request, new String[]{"docker", "Docker"});
                    break;
                case "netty":
                    dispatcher(model, request, new String[]{"netty", "Netty"});
                    break;
                default:
                    log.error("error request path");
            }
        }
        return "special-topic";
    }


    @GetMapping(value = "/db")
    public String indexCategoryDetailsOfDataBase(Model model, HttpServletRequest request) {
        dispatcher(model, request, new String[]{"数据库", "MySQL", "SQL", "oracle", "SQL Server"});
        return "special-topic";
    }

    @GetMapping(value = "/bigdata")
    public String indexCategoryDetailsOfCloudAndBigData(Model model, HttpServletRequest request) {
        dispatcher(model, request, new String[]{"云计算", "大数据", "hadoop", "hbase", "spark", "hive", "Tachyon", "Pig"});
        return "special-topic";
    }

    @GetMapping(value = "/others")
    public String indexCategoryDetailsOfOthers(Model model, HttpServletRequest request) {
        dispatcher(model, request, new String[]{"git", "github", "maven", "druid", "news", "行业新闻", "行业发展"});
        return "special-topic";
    }


    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }


}
