package com.ydzbinfo.emis.guns.config;

import com.ydzbinfo.emis.utils.upload.UploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gaohan
 * @description
 * @createDate 2021/5/11 14:56
 **/
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + uploadProperties.getUploadFileHandlerPath() + "/**")
            .addResourceLocations("file:" + uploadProperties.getUploadFilePath());
    }
}
