package top.easyblog.web.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.entity.po.Article;
import top.easyblog.global.pagehelper.PageParam;
import top.easyblog.global.pagehelper.PageSize;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huangxin
 */
@RequestMapping(value = "/search")
@Controller
public class SearchController extends BaseController{


    private static final String PLACEHOLDER="请输入关键字词";

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
                                   HttpServletRequest request,
                                   Model model) {
        if(query==null||query.replaceAll(" ","").length()==0||PLACEHOLDER.equals(query)){
            String backward = request.getHeader(HttpHeaders.REFERER);
            return "redirect:" + backward;
        }
        model.addAttribute("visitor", UserUtils.getUserFromRedis(visitorUId));
        model.addAttribute("query", query);
        PageParam pageParam = new PageParam(pageNo, PageSize.MAX_PAGE_SIZE);
        PageInfo<Article> articlePages = articleService.getArticleByTopicPage(query, pageParam);
        model.addAttribute("articlePages", articlePages);
        return "search";
    }

}
