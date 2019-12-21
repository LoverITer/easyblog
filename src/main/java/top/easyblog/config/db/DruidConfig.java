package top.easyblog.config.db;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DruidConfig {

    //配置Druid的其他属性
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")   //让Spring boot去加载这些属性，完成对连接池的初始化
    public DataSource druid(){
        return new DruidDataSource();
    }
}