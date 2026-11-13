package com.dandanyun.config;

import com.dandanyun.auth.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 基础配置：跨域与管理员/用户两套鉴权拦截。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AppCorsProperties corsProperties;
    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AppCorsProperties corsProperties, AuthInterceptor authInterceptor) {
        this.corsProperties = corsProperties;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(corsProperties.getAllowedOriginPatterns().toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-Request-Id")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/admin/**", "/user/**");
    }
}
