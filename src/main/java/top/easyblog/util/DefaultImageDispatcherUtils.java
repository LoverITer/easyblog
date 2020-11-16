package top.easyblog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 默认的图片分配工具类
 *
 * @author huangxin
 */
public class DefaultImageDispatcherUtils {


    public static final String DEFAULT_CATEGORY_IMG = "/static/images/category-default-pic/";

    public static final String DEFAULT_ARTICLE_IMG = "/static/images/article-default-pic/article-pic";

    private static final Logger log = LoggerFactory.getLogger(DefaultImageDispatcherUtils.class);


    /***
     * 获得默认的分类头像
     * @return DEFAULT_CATEGORY_IMG+nextInt+".jpg"
     */
    public static String defaultCategoryImage() {
        Random random = new Random();
        /**随机产生一个[0,21)的数**/
        int nextInt = random.nextInt(21) + 1;
        return DEFAULT_CATEGORY_IMG + nextInt + ".jpg";
    }

    /**
     * 获得默认的文章首图
     *
     * @return
     */
    public static String defaultArticleFirstImage() {
        Random random = new Random();
        /**随机产生一个[0,36)的数**/
        int nextInt = random.nextInt(36) + 1;
        return DEFAULT_ARTICLE_IMG + nextInt + ".jpg";
    }
}
