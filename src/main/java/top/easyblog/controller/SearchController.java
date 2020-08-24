package top.easyblog.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.entity.po.Article;
import top.easyblog.util.UserUtils;

/**
 * @author huangxin
 */
@RequestMapping(value = "/search")
@Controller
public class SearchController extends BaseController{

    /**
     * 搜索结果显示
     *
     * @param query   查询的关键字
     * @param pageNo   分页号
     * @param model   Model
     */
    @GetMapping("/details")
    public String showSearchResult(@RequestParam String query,
                                   @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                   @RequestParam(required = false) Integer visitorUId,
                                   Model model) {
        model.addAttribute("visitor",UserUtils.getUserFromRedis(visitorUId));
        model.addAttribute("query", query);
        PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE);
        PageInfo<Article> articlePages = articleService.getArticleByTopicPage(query, pageParam);
        model.addAttribute("articlePages", articlePages);
        return "search";
    }

}
