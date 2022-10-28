package com.example.myapp;

import com.example.myapp.controller.interceptor.AuthInterceptor;
import org.junit.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/20
 */
public class MyappWebMvcConfigurerTest {
    private final AuthInterceptor authInterceptor = mock(AuthInterceptor.class);
    private final MyappWebMvcConfigurer myappWebMvcConfigurer = new MyappWebMvcConfigurer(authInterceptor);

    @Test
    public void addInterceptorsAddInterceptorIfCommon() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        myappWebMvcConfigurer.addInterceptors(registry);

        verify(registry).addInterceptor(authInterceptor);
    }
}