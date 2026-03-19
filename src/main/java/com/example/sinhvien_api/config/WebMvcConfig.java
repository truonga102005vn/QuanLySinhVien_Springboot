package com.example.sinhvien_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Redirect shortcut URL → file HTML
     * Không override addResourceHandlers để tránh conflict với REST API
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/",         "/sinhvien.html");
        registry.addRedirectViewController("/admin",    "/admin.html");
        registry.addRedirectViewController("/login",    "/login.html");
        registry.addRedirectViewController("/sinhvien", "/sinhvien.html");
    }
}