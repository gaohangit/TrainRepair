/*
 * 金现代轻骑兵V8开发平台
 * HussarApplication.java
 * 版权所有：金现代信息产业股份有限公司  Copyright (c) 2018-2023 .
 * 金现代信息产业股份有限公司保留所有权利,未经允许不得以任何形式使用.
 */
package com;




import com.jxdinfo.hussar.config.properties.HussarProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@ServletComponentScan
@SpringBootApplication(scanBasePackages = { "com" },
        exclude = {SecurityAutoConfiguration.class,DataSourceAutoConfiguration.class/*,org.activiti.spring.boot.SecurityAutoConfiguration.class*/})
public class TrainRepairUIApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

    protected static final Logger logger = LoggerFactory.getLogger(TrainRepairUIApplication.class);

    @Autowired
    HussarProperties hussarProperties;

    /**
     * 增加swagger的支持
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (hussarProperties.getSwaggerOpen()) {
            registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
            registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/static/swagger/");
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TrainRepairUIApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TrainRepairUIApplication.class, args);
        logger.info("TrainRepairUIApplication is success!");
    }


}
