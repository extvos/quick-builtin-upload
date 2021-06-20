package org.extvos.builtin.upload.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Mingcai SHEN
 */
@Configuration
public class TestPageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/_builtin/upload-test/**").addResourceLocations("classpath:/webapp/");
    }
}
