package com.example.pr5simple.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${app.path.upload.file}")
    private String pathUploadFile;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/upload-files/**")
                .addResourceLocations("file://" + pathUploadFile + "/");

        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
