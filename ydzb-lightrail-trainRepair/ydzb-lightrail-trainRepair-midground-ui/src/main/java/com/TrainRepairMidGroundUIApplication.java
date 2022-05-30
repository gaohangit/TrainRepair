/*
 * 金现代轻骑兵V8开发平台
 * HussarApplication.java
 * 版权所有：金现代信息产业股份有限公司  Copyright (c) 2018-2023 .
 * 金现代信息产业股份有限公司保留所有权利,未经允许不得以任何形式使用.
 */
package com;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = { "com" },
        exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class/*,org.activiti.spring.boot.SecurityAutoConfiguration.class*/})
public class TrainRepairMidGroundUIApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

    protected static final Logger logger = LoggerFactory.getLogger(TrainRepairMidGroundUIApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TrainRepairMidGroundUIApplication.class, args);
        logger.info("TrainRepairMidGroundUIApplication is success!");
    }


}
