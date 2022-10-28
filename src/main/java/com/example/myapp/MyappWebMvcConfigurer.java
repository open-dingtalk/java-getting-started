package com.example.myapp;

import com.example.myapp.controller.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * register auth interceptor
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/20
 */
@Component
public class MyappWebMvcConfigurer implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    @Autowired
    public MyappWebMvcConfigurer(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }
}
