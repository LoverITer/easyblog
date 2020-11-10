package top.easyblog.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.easyblog.util.RedisUtils;
import top.easyblog.web.service.HotWordService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/11/09 18:13
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class HotWordServiceImpl implements HotWordService {

    @Autowired
    private RedisUtils redisUtils;
    /**
     * 数据存放的数据库
     */
    private static final RedisUtils.RedisDataBaseSelector REDIS_DB = RedisUtils.RedisDataBaseSelector.DB_0;
    /**
     * 将用户搜索数据放到Redis的zset，这个是Zset的key
     */
    private static final String HOT_WORD_KEY = "hot_word";

    /**
     * 新增一条该userid用户在搜索栏的历史记录
     *
     * @param userId    用户ID
     * @param searchKey 代表输入的关键词
     * @return
     */
    @Override
    public int addSearchHistoryByUserId(String userId, String searchKey) {
        String shistory = (String) redisUtils.get(userId, REDIS_DB);
        boolean b = redisUtils.exists(shistory, REDIS_DB);
        if (b) {
            Object hk = redisUtils.hget(shistory, searchKey, REDIS_DB);
            if (hk != null) {
                return 1;
            } else {
                redisUtils.hset(shistory, searchKey, "1", REDIS_DB);
            }
        } else {
            redisUtils.hset(shistory, searchKey, "1", REDIS_DB);
        }
        return 1;
    }

    /**
     * 删除个人历史数据
     */
    @Override
    public Long delSearchHistoryByUserId(String userId, String searchKey) {
        String shistory = (String) redisUtils.get(userId, REDIS_DB);
        return redisUtils.hdel(REDIS_DB, shistory, searchKey);
    }

    /**
     * 获取个人历史数据列表
     */
    @Override
    public List<String> getSearchHistoryByUserId(String userId) {
        return null;
    }

    /**
     * 新增一条热词搜索记录，将用户输入的热词存储下来
     */
    @Override
    public int incrementScoreByUserId(String searchKey) {
        Long now = System.currentTimeMillis();
        List<String> title = new ArrayList<>();
        title.add(searchKey);
        for (int i = 0, len = title.size(); i < len; i++) {
            String tle = title.get(i);
            try {
                if (redisUtils.zscore(HOT_WORD_KEY, tle, REDIS_DB) <= 0) {
                    redisUtils.zadd(HOT_WORD_KEY, tle, 0, REDIS_DB);
                    redisUtils.set(tle, String.valueOf(now), REDIS_DB);
                }
            } catch (Exception e) {
                redisUtils.zadd(HOT_WORD_KEY, tle, 0, REDIS_DB);
                redisUtils.set(tle, String.valueOf(now), REDIS_DB);
            }
        }
        return 1;
    }

    /**
     * 根据searchkey搜索其相关最热的前十名 (如果searchkey为null空，则返回redis存储的前十最热词条)
     */
    @Override
    public List<String> getHotList(String searchKey) {
        Long now = System.currentTimeMillis();
        List<String> result = new ArrayList<>();
        Set<Object> values = redisUtils.zRevRangeByScore(HOT_WORD_KEY, 0, Double.MAX_VALUE, REDIS_DB);
        if (values == null) {
            return null;
        }
        //key不为空的时候 推荐相关的最热前十名
        if (!StringUtils.isEmpty(searchKey)) {
            for (Object val : values) {
                String value = String.valueOf(val);
                if (value.toLowerCase().contains(searchKey.toLowerCase())) {
                    if (result.size() > 9) {
                        //只返回最热的前十名
                        break;
                    }
                    Long time = Long.valueOf((String) redisUtils.get(value, REDIS_DB));
                    if ((now - time) < 2592000000L) {
                        //返回最近一个月的数据
                        result.add(value);
                    } else {
                        //时间超过一个月没搜索就把这个词热度归0
                        redisUtils.zadd(HOT_WORD_KEY, val, 0, REDIS_DB);
                    }
                }
            }
        } else {
            for (Object val : values) {
                String value = String.valueOf(val);
                if (result.size() > 9) {
                    //只返回最热的前十名
                    break;
                }
                Long time = Long.valueOf((String) redisUtils.get(value, REDIS_DB));
                if ((now - time) < 2592000000L) {
                    //返回最近一个月的数据
                    result.add(value);
                } else {
                    //时间超过一个月没搜索就把这个词热度归0
                    redisUtils.zadd(HOT_WORD_KEY, value, 0, REDIS_DB);
                }
            }
        }
        return result;
    }

    /**
     * 每次点击给相关词searchkey热度 +1
     */
    @Override
    public int incrementScore(String searchKey) {
        Long now = System.currentTimeMillis();
        redisUtils.zincrby(HOT_WORD_KEY, searchKey, 1, REDIS_DB);
        redisUtils.getAndSet(searchKey, String.valueOf(now), REDIS_DB);
        return 1;
    }


}
