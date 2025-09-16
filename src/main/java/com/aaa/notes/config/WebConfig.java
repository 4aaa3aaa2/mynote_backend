package com.aaa.notes.config;


import com.aaa.notes.filter.TraceIdFilter;
import com.aaa.notes.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //指定文件上传路径
    @Value("${upload.path:C:/Users/mingzhi.huang/Desktop/projects/java_pratice1/note_prj/upload}")
    private String uploadPath;

    @Autowired
    private TokenInterceptor tokenInterceptor;

    /**配置静态资源访问
     * 访问图片等文件时候导航到本地指定目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }

    /**
     * 添加拦截器，用于验证 token，初始化请求周期中的用户相关信息
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/error");
    }

    /**
     * 跨域配置，解决前后端分离的问题
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")  // 允许的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")// 允许的 HTTP 方法
                .allowedHeaders("*") //允许所有请求头
                .allowCredentials(true)  //允许携带验证信息
                .maxAge(3600);  //缓存1h
    }

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }



}
