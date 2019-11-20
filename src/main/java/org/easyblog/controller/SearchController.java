package org.easyblog.controller;

import org.easyblog.bean.Article;
import org.easyblog.service.impl.ArticleServiceImpl;
import org.easyblog.utils.HtmlParserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(value = "/search")
@Controller
public class SearchController {

    private final ArticleServiceImpl articleService;


    public SearchController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/details")
    public String showSearchResult(@RequestParam String query, Model model){
        model.addAttribute("query",query);
        List<Article> articles = articleService.getArticleByTopic(query);
        articles.forEach(ele->{
            ele.setArticleContent(HtmlParserUtil.HTML2Text(ele.getArticleContent()));
        });

        model.addAttribute("articles",articles);
        return "search";
    }

}
