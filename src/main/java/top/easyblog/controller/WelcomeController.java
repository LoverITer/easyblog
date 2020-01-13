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
import top.easyblog.service.IArticleService;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        PageInfo<Article> newestArticlesPages = articleService.getAllUserNewestArticlesPage(new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE.getPageSize()));
        //查询访问量最高的13篇最近的文章
        List<Article> mostFamousArticles = articleService.getMostFamousArticles(13);
        List<Article> articles = new ArrayList<>();
        List<Article> otherArticles = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        Iterator<Article> iterator = mostFamousArticles.iterator();
        while (iterator.hasNext()) {
            if (count.get() <= 4) {
                articles.add(iterator.next());
                iterator.remove();
            } else {
                if(Objects.nonNull(mostFamousArticles)&&mostFamousArticles.size()>0) {
                    model.addAttribute("famousSideBarTopArticle",mostFamousArticles.get(0));
                    mostFamousArticles.remove(0);
                    otherArticles=mostFamousArticles;
                }
                break;
            }
            count.getAndIncrement();
        }
        model.addAttribute("otherFamousArticles", otherArticles);
        model.addAttribute("articles", articles);
        model.addAttribute("newestArticlesPages", newestArticlesPages);
        //当前年份
        model.addAttribute("CURRENT_YEAR", new Date());
        return "index";
    }

    @GetMapping(value = "/help")
    public String help() {
        return "help";
    }

}
