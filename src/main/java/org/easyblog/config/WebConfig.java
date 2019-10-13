package org.easyblog.config;

import org.easyblog.handler.interceptor.DefaultInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        DefaultInterceptor defaultInterceptor=new DefaultInterceptor();
        final InterceptorRegistration registration = registry.addInterceptor(new DefaultInterceptor());
        registration.addPathPatterns("/**");
        //不拦截静态资源：自定义拦截器，排除拦截classpath:/static下的所有静态资源
        registration.excludePathPatterns("/static/**");
        registration.excludePathPatterns("/error");
        registration.excludePathPatterns("/");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }
}
