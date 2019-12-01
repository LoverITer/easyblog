package top.easyblog.config.cache;

import org.apache.ibatis.cache.Cache;
import top.easyblog.config.ApplicationContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 使用Redis作为MyBatis的二级缓存
 */
public class MyBatisRedisCache implements Cache {

    private final String id;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private RedisTemplate<String, Object> redisTemplate =null;
    private static final long EXPIRE_TIME_IN_MINUTES = 30; // redis过期时间


    public MyBatisRedisCache(final String id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }


    @Override
    public String getId() {
        return this.id;
    }

    /**
     * 向MyBatis二级缓存中放值
     *
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key, Object value) {
        if (Objects.nonNull(value)) {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key.toString(), value, EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
        }
    }

    /**
     * 从缓存中取值
     *
     * @param key
     * @return
     */
    @Override
    public Object getObject(Object key) {
        if(Objects.nonNull(key)) {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
            ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
            return opsForValue.get(key.toString());
        }
        return null;
    }

    @Override
    public Object removeObject(Object o) {
        if(Objects.nonNull(o)) {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
            return redisTemplate.delete(o.toString());
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        redisTemplate.execute((RedisCallback) connection -> {
            connection.flushDb();
            return null;
        });
    }

    @Override
    public int getSize() {
        Long size = (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize();
            }
        });
        return size!=null?size.intValue():0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    private RedisTemplate<String, Object> getRedisTemplate() {
        if (Objects.isNull(redisTemplate)) {
            synchronized (MyBatisRedisCache.class) {
                redisTemplate = ApplicationContextHolder.getBean("redisTemplate");
            }
        }
        return redisTemplate;
    }
}
