package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.bean.Article;
import top.easyblog.commons.pagehelper.PageParam;
import top.easyblog.commons.pagehelper.PageSize;
import top.easyblog.service.impl.ArticleServiceImpl;

@RequestMapping(value = "/search")
@Controller
public class SearchController {

    private final ArticleServiceImpl articleService;


    public SearchController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/details")
    public String showSearchResult(@RequestParam String query,
                                   @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                   Model model) {
        model.addAttribute("query", query);
        PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE.getPageSize());
        PageInfo<Article> articlePages = articleService.getArticleByTopicPage(query, pageParam);
        model.addAttribute("articlePages", articlePages);
        return "search";
    }

}
