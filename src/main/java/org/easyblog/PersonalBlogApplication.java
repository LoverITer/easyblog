package org.easyblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@MapperScan(value = "org.easyblog.mapper")
@SpringBootApplication
public class PersonalBlogApplication {


    public static void main(String[] args) {
        SpringApplication.run(PersonalBlogApplication.class, args);
    }

}
