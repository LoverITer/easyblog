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
import java.util.List;
import java.util.Objects;

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
            PageInfo<Article> newestArticlesPages = articleService.getAllUserNewestArticlesPage(new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE.getPageSize()));
            //查询访问量最高的19篇最近的文章用于首页大图、访问排行、特别推荐的显示
            List<Article> mostFamousArticles = articleService.getMostFamousArticles(22);
            model.addAttribute("newestArticlesPages", newestArticlesPages);
            List<List<?>> splitList = CollectionUtils.splitList(mostFamousArticles, new int[]{5, 1, 7, 6});
            if (Objects.nonNull(splitList)) {
                model.addAttribute("articles", splitList.get(0));
                //访问排行侧边栏带首图显示的文章
                model.addAttribute("famousSideBarTopArticle", splitList.get(1));
                //访问排行侧边栏其他文章
                model.addAttribute("visitRankingArticles", splitList.get(2));
                model.addAttribute("specialRecommendArticles", splitList.get(3));
            }
            List<Article> allHistoryFamousArticles = articleService.getAllHistoryFamousArticles(10);
            List<List<?>> lists = CollectionUtils.splitList(allHistoryFamousArticles, new int[]{1, 9});
            if (Objects.nonNull(lists)) {
                model.addAttribute("recommendTopic", lists.get(0));
                model.addAttribute("recommend", lists.get(1));
            }
            List<Article> youMayAlsoLikeArticles = articleService.getYouMayAlsoLikeArticles();
            model.addAttribute("youMayAlsoLikeArticles", youMayAlsoLikeArticles);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "index";
    }

    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }

}
