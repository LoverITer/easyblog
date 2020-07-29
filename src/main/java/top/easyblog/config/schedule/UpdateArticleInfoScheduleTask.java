package top.easyblog.config.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import top.easyblog.entity.po.Article;
import top.easyblog.service.IArticleService;
import top.easyblog.util.DefaultImageDispatcherUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**定时更新系统数据文章的信息
 * @author HuangXin
 * @since 2020/2/8 21:50
 *
 */
@Slf4j
@Configuration
public class UpdateArticleInfoScheduleTask {

    private final IArticleService articleService;

    public UpdateArticleInfoScheduleTask(IArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 定时任务：每天凌晨2:00给系统数据中没有首图的文章设置默认的图片
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void settingNoneFirstPictureArticle(){
        log.info(new Date()+" schedule task start");
        try {
            //后驱所有没有首图的文章
            List<Article> articles = articleService.getAllNoneFirstPicArticles();
            if (Objects.nonNull(articles)) {
                articles.forEach(article -> {
                    //分配系统默认的图片
                    String imageUrl = DefaultImageDispatcherUtils.defaultArticleFirstImage();
                    Article var0 = new Article();
                    var0.setArticleId(article.getArticleId());
                    var0.setArticleFirstPicture(imageUrl);
                    int res = articleService.updateSelective(var0);
                    if (res <= 0) {
                        log.error("schedule task execute failed!");
                    }
                });
            }
        }catch (Exception e){
            log.error(new Date().toString() +":"+e.getMessage());
        }
        log.info(new Date()+" schedule task complete successful!");
    }

}
