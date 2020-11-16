package top.easyblog.web.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.easyblog.entity.po.Article;
import top.easyblog.entity.po.User;
import top.easyblog.global.pagehelper.PageParam;
import top.easyblog.global.pagehelper.PageSize;
import top.easyblog.util.CookieUtils;
import top.easyblog.util.SensitiveWordUtils;
import top.easyblog.util.UserUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
                                   HttpServletRequest request,
                                   Model model) {
        if(query==null||query.replaceAll(" ","").length()==0||PLACEHOLDER.equals(query)){
            String backward = request.getHeader(HttpHeaders.REFERER);
            return "redirect:" + backward;
        }
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        model.addAttribute("baseUrl", baseUrl);
        String sessionId = CookieUtils.getCookieValue(request, JSESSIONID);
        User visitor= UserUtils.getUserFromRedis(sessionId);
        model.addAttribute("user", visitor);
        model.addAttribute("query", query);
        SensitiveWordUtils wordUtils = SensitiveWordUtils.getInstance();
        //检测是否有敏感字
        int index = wordUtils.CheckSensitiveWord(query, 0, SensitiveWordUtils.MatchType.MIN_MATCH_TYPE);
        if (index == 0 && !StringUtils.isEmpty(query)) {
            //如果有敏感字就不往热搜词排行榜总存放
            hotWordService.incrementScore(query);
        }
        //重新获取一下热搜词排行前15的关键字
        List<String> hotList = hotWordService.getHotList(null);
        model.addAttribute("hotList", hotList);
        PageParam pageParam = new PageParam(pageNo, PageSize.DEFAULT_PAGE_SIZE);
        PageInfo<Article> articlePages = articleService.getArticleByTopicPage(query, pageParam);
        model.addAttribute("articlePages", articlePages);

        //热文推荐
        List<Article> allHistoryFamousArticles = articleService.getAllHistoryFamousArticles(10);
        model.addAttribute("hotArticles", allHistoryFamousArticles);
        return "search";
    }

}
