package top.easyblog.config.cache;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

/**
 * @author  huangxin
 */
@EnableCaching
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    /**
     * 自动义key的生成策略
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (obj, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(obj.getClass().getName()).append("@"); // 类
            if(Objects.nonNull(params)&&params.length>0) {
                for (Object param : params) {
                    sb.append(param.hashCode()); // 参数名的hashcode
                }
            }else{
                sb.append(".").append("0");
            }
            return sb.toString();
        };
    }


    /**
     * 配置缓存管理器
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 5分钟缓存失效
                .entryTtl(Duration.ofSeconds(60*5))
                //不缓存null值
                .disableCachingNullValues()
                // 设置key的序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                // 设置value的序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

    /**
     * key键序列化方式
     *
     * @return
     */
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * value值序列化方式
     *
     * @return
     */
    private GenericJackson2JsonRedisSerializer valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
