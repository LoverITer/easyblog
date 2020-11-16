package top.easyblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import top.easyblog.util.RedisUtils;
import top.easyblog.web.service.HotWordService;

import javax.annotation.PostConstruct;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/11/10 19:09
 */
@Configuration
public class HotWordInitConfiguration {

    /**
     * 初始化的热搜词
     */
    private static final String[] INIT_HOT_WORD = {"Spring", "Spring Boot", "MySQL", "Nginx", "Redis", "Java", "Linux", "Docker", "Http"};

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private HotWordService hotWordService;


    /**
     * 初始化Redis热搜词词库,如果Redis中没有则初始化，如果有就不用再次初始化了
     */
    @PostConstruct
    public void initHotWord() {
        for (String word : INIT_HOT_WORD) {
            Boolean exists = redisUtils.exists(word, RedisUtils.RedisDataBaseSelector.DB_0);
            if (exists == null || !exists) {
                hotWordService.incrementScore(word);
            }
        }
    }

}
