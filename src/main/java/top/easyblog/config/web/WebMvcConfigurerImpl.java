package top.easyblog.config.web;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.*;
import top.easyblog.web.LoginInterceptor;

import javax.servlet.MultipartConfigElement;

/**
 * 自定义Web MVC配置
 * @author huangxin
 */
@EnableWebMvc
@Configuration
public class WebMvcConfigurerImpl implements WebMvcConfigurer {



    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大5MB
        factory.setMaxFileSize(DataSize.ofMegabytes(5));
        //一次请求最大10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        return factory.createMultipartConfig();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new LoginInterceptor());
        registration.addPathPatterns("/**");
        /**
         * 排除拦截classpath:/static下的所有静态资源
         */
        registration.excludePathPatterns("/static/**");
        registration.excludePathPatterns("/error");
        registration.excludePathPatterns("/**.*.html");
        registration.excludePathPatterns("/");
        /**
         * 排除拦截器对登录、注销、去登录页、查看文章请求的拦截
         */
        registration.excludePathPatterns("/login");
        registration.excludePathPatterns("/logout");
        registration.excludePathPatterns("/user/**");
        registration.excludePathPatterns("/article/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/upload/**").addResourceLocations("file:D:/upload/");
        registry.addResourceHandler("/robots.txt").addResourceLocations("classpath:/robots.txt");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //注册页面
        registry.addViewController("/user/registration.html").setViewName("registration");
        //找回密码
        registry.addViewController("/user/reset.html").setViewName("reset-password");
        //注册成功
        registry.addViewController("/user/register-success").setViewName("registration-success");
        //帮助
        registry.addViewController("/help").setViewName("help");
    }


}
