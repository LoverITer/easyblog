package org.easyblog.config;

import org.easyblog.handler.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        final InterceptorRegistration registration = registry.addInterceptor(new LoginInterceptor());
        registration.addPathPatterns("/**");
        /**排除拦截classpath:/static下的所有静态资源**/
        registration.excludePathPatterns("/static/**");
        registration.excludePathPatterns("/error");
        registration.excludePathPatterns("/**.*.html");
        registration.excludePathPatterns("/");
        /*****排除拦截器对登录、注销、去登录页、查看文章请求的拦截*****/
        registration.excludePathPatterns("/login");
        registration.excludePathPatterns("/logout");
        registration.excludePathPatterns("/user/**");
        registration.excludePathPatterns("/article/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/upload/**").addResourceLocations("file:D:/upload/");;
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }
}
