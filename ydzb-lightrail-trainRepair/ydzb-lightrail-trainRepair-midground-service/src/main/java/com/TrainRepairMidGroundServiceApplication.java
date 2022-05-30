package com;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = { "com" },
        exclude = {SecurityAutoConfiguration.class})
@MapperScan(basePackages = {"com.**.dao"})
@EnableSwagger2
public class TrainRepairMidGroundServiceApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

    protected static final Logger logger = LoggerFactory.getLogger(TrainRepairMidGroundServiceApplication.class);

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

    // @Bean
    // public FilterRegistrationBean<MobileEncryptFilter> mobileEncryptFilter(@Value("${ydzb.mobile.deskey}") String desKey) {
    //     FilterRegistrationBean<MobileEncryptFilter> bean = new FilterRegistrationBean<>(new MobileEncryptFilter(desKey));
    //     bean.addUrlPatterns("/" + Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/*");
    //     return bean;
    // }

    // @Bean
    // public HttpMessageConverters configHttpMessageConverters() {
    //     // 1.定义一个converters转换消息的对象
    //     FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
    //     // 2.添加fastjson的配置信息，比如: 是否需要格式化返回的json数据
    //     FastJsonConfig fastJsonConfig = new FastJsonConfig();
    //     fastJsonConfig.setSerializerFeatures(
    //         SerializerFeature.WriteNullListAsEmpty,
    //         SerializerFeature.WriteMapNullValue,
    //         SerializerFeature.WriteNullStringAsEmpty
    //     );
    //     // 3.在converter中添加配置信息
    //     fastConverter.setFastJsonConfig(fastJsonConfig);
    //     // 4.将converter赋值给HttpMessageConverter
    //     // 5.返回HttpMessageConverters对象
    //     fastConverter.setDefaultCharset(StandardCharsets.UTF_8);
    //     fastConverter.setSupportedMediaTypes(
    //         Arrays.asList(
    //             MediaType.APPLICATION_JSON_UTF8,
    //             MediaType.APPLICATION_JSON
    //         )
    //     );
    //     return new HttpMessageConverters(fastConverter);
    // }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TrainRepairMidGroundServiceApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TrainRepairMidGroundServiceApplication.class, args);
        logger.info("TrainRepairMidGroundServiceApplication is success!");
    }
}
