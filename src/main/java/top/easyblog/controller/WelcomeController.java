package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.bean.Article;
import top.easyblog.bean.User;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.pagehelper.PageSize;
import top.easyblog.commons.utils.CollectionUtils;
import top.easyblog.service.IArticleService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "/")
public class WelcomeController {

    private final IArticleService articleService;

    public WelcomeController(IArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping(value = "/")
    public String index(Model model, HttpSession session, @RequestParam(defaultValue = "1") int pageNo) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        //查询最近1个月内的文章
        PageInfo<Article> newestArticlesPages = articleService.getAllUserNewestArticlesPage(new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE.getPageSize()));
        //查询访问量最高的19篇最近的文章用于首页大图、访问排行、特别推荐的显示
        List<Article> mostFamousArticles = articleService.getMostFamousArticles(22);
        model.addAttribute("newestArticlesPages", newestArticlesPages);
        List<List<?>> splitList = CollectionUtils.splitList(mostFamousArticles, new int[]{5,1,7,6});
        if(Objects.nonNull(splitList)&&splitList.size()==4) {
            model.addAttribute("articles", splitList.get(0));
            //访问排行侧边栏带首图显示的文章
            model.addAttribute("famousSideBarTopArticle", splitList.get(1));
            //访问排行侧边栏其他文章
            model.addAttribute("visitRankingArticles", splitList.get(2));
            model.addAttribute("specialRecommendArticles", splitList.get(3));
        }
        List<Article> allHistoryFamousArticles = articleService.getAllHistoryFamousArticles(10);
        List<List<?>> lists = CollectionUtils.splitList(allHistoryFamousArticles, new int[]{1, 9});
        if(Objects.nonNull(lists)&&lists.size()==2) {
            model.addAttribute("recommendTopic", lists.get(0));
            model.addAttribute("recommend", lists.get(1));
        }
        List<Article> youMayAlsoLikeArticles = articleService.getYouMayAlsoLikeArticles();
        model.addAttribute("youMayAlsoLikeArticles",youMayAlsoLikeArticles);
        return "index";
    }

    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }

}
