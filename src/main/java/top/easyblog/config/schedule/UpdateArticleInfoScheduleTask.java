package top.easyblog.config.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import top.easyblog.bean.Article;
import top.easyblog.commons.utils.DefaultImageDispatcherUtils;
import top.easyblog.service.IArticleService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**定时更新系统数据文章的信息
 * @author HuangXin
 * @since 2020/2/8 21:50
 *
 */
@Configuration
public class UpdateArticleInfoScheduleTask {

    private final IArticleService articleService;
    private static Logger log= LoggerFactory.getLogger(UpdateArticleInfoScheduleTask.class);

    public UpdateArticleInfoScheduleTask(IArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 定时任务：每天凌晨2:00给系统数据中没有首图的文章设置默认的图片
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void settingNoneFirstPictureArticle(){
        log.info(new Date()+"定时任务开始");
        try {
            List<Article> articles = articleService.getAllNoneFirstPicArticles();
            if (Objects.nonNull(articles)) {
                articles.forEach(article -> {
                    //分配系统默认的图片
                    String imageUrl = DefaultImageDispatcherUtils.defaultArticleFirstImage();
                    Article var0 = new Article();
                    var0.setArticleId(article.getArticleId());
                    var0.setArticleFirstPicture(imageUrl);
                    int res = articleService.updateSelective(var0);
                    if(res<=0){
                        log.error("添加首图失败！");
                    }
                });
            }
        }catch (Exception e){
            log.error(new Date().toString() +":"+e.getMessage());
        }
        log.info(new Date()+"定时任务结束");
    }

}
