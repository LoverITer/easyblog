package top.easyblog.service.impl;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.easyblog.entity.po.Article;
import top.easyblog.service.IArticleService;
import top.easyblog.service.ISeoService;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 自动生成站点地图
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/06/06 08:27
 */
@Slf4j
@Service
public class SeoServiceImpl implements ISeoService {

    private String domain="www.easyblog.top";
    @Autowired
    private IArticleService articleService;

    @Override
    public String createSiteMapXmlContent() {
        String baseUrl = String.format("https://%s", domain);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        WebSitemapGenerator wsg = null;
        try {
            wsg = new WebSitemapGenerator(baseUrl);
            // 首页 url
            WebSitemapUrl url = new WebSitemapUrl.Options(baseUrl)
                    .lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(1.0).changeFreq(ChangeFreq.ALWAYS).build();
            wsg.addUrl(url);

            // 查询所有的问题方案数据
            List<Article> articles = articleService.getAllArticles();
            // 动态添加 url
            if (Objects.nonNull(articles)) {
                final WebSitemapGenerator finalWsg = wsg;
                articles.forEach(article -> {
                    WebSitemapUrl tmpUrl = null;
                    try {
                        tmpUrl = new WebSitemapUrl.Options(baseUrl + "/article/details/" + article.getArticleId())
                                .lastMod(new SimpleDateFormat("yyyy-MM-dd").format(article.getArticlePublishTime())).priority(0.9).changeFreq(ChangeFreq.DAILY).build();
                    } catch (ParseException | MalformedURLException e) {
                        log.error(e.getLocalizedMessage());
                    }
                    assert tmpUrl != null;
                    finalWsg.addUrl(tmpUrl);
                });
            }
        } catch (Exception e) {
            log.error("create sitemap xml error:", e);
        }
        assert wsg != null;
        return String.join("", wsg.writeAsStrings());
    }
}
