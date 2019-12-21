package top.easyblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
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