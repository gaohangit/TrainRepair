package com;

import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.handlers.MobileEncryptFilter;
import com.ydzbinfo.emis.handlers.MobileEncryptFilterUtil;
import com.ydzbinfo.emis.utils.Constants;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com"},
    exclude = {SecurityAutoConfiguration.class})
@MapperScan(basePackages = {"com.**.dao", "com.ydzbinfo.emis.base.dao"})
@EnableSwagger2
public class TrainRepairServiceApplication extends SpringBootServletInitializer implements WebMvcConfigurer {


    protected static final Logger logger = LoggerFactory.getLogger(TrainRepairServiceApplication.class);

    /*@Autowired
    HussarProperties hussarProperties;*/

    /**
     * 增加swagger的支持
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*if (hussarProperties.getSwaggerOpen()) {
            registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
            registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/static/swagger/");
        }*/
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/static/swagger/");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TrainRepairServiceApplication.class);
    }

    /**
     * 注册手持机统一接口加密解密Filter
     */
    @Bean
    public FilterRegistrationBean<MobileEncryptFilter> mobileEncryptFilter(TrainRepairMobileProperties trainRepairMobileProperties) {
        FilterRegistrationBean<MobileEncryptFilter> bean = new FilterRegistrationBean<>(new MobileEncryptFilter(trainRepairMobileProperties.getDeskey()));
        bean.addUrlPatterns(MobileEncryptFilterUtil.getUrlPattern());
        // 禁用统一转换手持机接口的null值空字符串转换
        MobileEncryptFilterUtil.disableMobileHttpJsonNullValueFilter();
        return bean;
    }

    public static void main(String[] args) {
        SpringApplication.run(TrainRepairServiceApplication.class, args);
        logger.info("TrainRepairServiceApplication is success!");
    }
}
