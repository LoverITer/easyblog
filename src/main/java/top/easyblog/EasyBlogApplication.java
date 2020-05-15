package top.easyblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author Huangxin
 */
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy
@MapperScan(value = "top.easyblog.mapper")
@SpringBootApplication
public class EasyBlogApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(EasyBlogApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EasyBlogApplication.class);
    }
}
