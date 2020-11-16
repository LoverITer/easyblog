package top.easyblog.util;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/11/08 21:51
 */
public class SpecialTopicUtils {

    /***专题描述信息*/
    private static final Map<String, String> SPECIAL_TOPIC_CACHE = new ConcurrentHashMap<>(16);
    /***给专题配置的图片的URL*/
    private static final Map<String, String> SPECIAL_TOPIC_IMG_CACHE = new ConcurrentHashMap<>(16);

    //加载关于专题描述信息的配置文件并放到SPECIAL_TOPIC_CACHE中
    static {
        Properties properties = new Properties();
        try {
            properties.load(SpecialTopicUtils.class.getClassLoader().getResourceAsStream("category-description.properties"));
            properties.forEach((k, v) -> {
                SPECIAL_TOPIC_CACHE.put((String) k, (String) v);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        Properties properties = new Properties();
        try {
            properties.load(SpecialTopicUtils.class.getClassLoader().getResourceAsStream("category-img.properties"));
            properties.forEach((k, v) -> {
                SPECIAL_TOPIC_IMG_CACHE.put((String) k, (String) v);
                System.out.println(k + "," + v);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对于某个专题的描述信息
     *
     * @param key 专题标识
     * @return
     */
    public static String getSpecialTopicDescriptionOf(String key) {
        return SPECIAL_TOPIC_CACHE.get(key);
    }

    /**
     * 获取对于某个专题的图片URL
     *
     * @param key 专题标识
     * @return
     */
    public static String getSpecialTopicImgOf(String key) {
        return SPECIAL_TOPIC_IMG_CACHE.get(key);
    }

}
