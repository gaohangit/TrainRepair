package com.ydzbinfo.emis.configs;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.ydzbinfo.emis.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 修复框架使用fastjson序列化转换值为null的List时，会将其处理为空字符串的bug
 * 重置filter为只转化简单类型的null值为空字符串，添加了空数组和空字符串的转换配置
 *
 * @author 张天可
 * @time 2021/6/15 18:56
 */
@Configuration
public class FastJsonHttpMessageConverterRepairConfig implements WebMvcConfigurer {

    protected static final Logger logger = LoggerFactory.getLogger(FastJsonHttpMessageConverterRepairConfig.class);

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        int index = CommonUtils.findIndex(converters, v -> v.getClass().equals(FastJsonHttpMessageConverter.class));
        if (index != -1) {
            FastJsonHttpMessageConverter converter = (FastJsonHttpMessageConverter) converters.remove(index);
            converters.add(index, getFilterNullValueFastJsonHttpMessageConverter(converter));
            logger.info("FastJsonHttpMessageConverter配置已转换为FilterNullValueFastJsonHttpMessageConverter");
        } else {
            logger.error("FastJsonHttpMessageConverter配置失败");
        }
    }

    private static FilterNullValueFastJsonHttpMessageConverter getFilterNullValueFastJsonHttpMessageConverter(FastJsonHttpMessageConverter fastJsonHttpMessageConverter) {
        FilterNullValueFastJsonHttpMessageConverter filterNullValueFastJsonHttpMessageConverter = new FilterNullValueFastJsonHttpMessageConverter();
        filterNullValueFastJsonHttpMessageConverter.setFastJsonConfig(fastJsonHttpMessageConverter.getFastJsonConfig());
        repairFastJsonHttpMessageConverter(filterNullValueFastJsonHttpMessageConverter);
        return filterNullValueFastJsonHttpMessageConverter;
    }

    private static void repairFastJsonHttpMessageConverter(FastJsonHttpMessageConverter fastJsonHttpMessageConverter) {
        fastJsonHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(
            Arrays.asList(
                MediaType.APPLICATION_JSON_UTF8,
                MediaType.APPLICATION_JSON
            )
        );
        FastJsonConfig fastJsonConfig = fastJsonHttpMessageConverter.getFastJsonConfig();
        fastJsonHttpMessageConverter.setFastJsonConfig(getRepairedFastjsonConfig(fastJsonConfig));
    }

    @Autowired
    public void repairRestTemplate(RestTemplate restTemplate) {
        Optional<HttpMessageConverter<?>> optional = restTemplate.getMessageConverters().stream().filter(v -> v instanceof FastJsonHttpMessageConverter).findAny();
        if (optional.isPresent()) {
            FastJsonHttpMessageConverter fastJsonHttpMessageConverter = (FastJsonHttpMessageConverter) optional.get();
            // 传递给其他服务的数据不需要加null值过滤
            repairFastJsonHttpMessageConverter(fastJsonHttpMessageConverter);
        }
    }


    private static FastJsonConfig getRepairedFastjsonConfig(FastJsonConfig fastJsonConfig) {
        FastJsonConfig newFastJsonConfig = new FastJsonConfig();

        Set<SerializerFeature> serializerFeatures = new HashSet<>(Arrays.asList(fastJsonConfig.getSerializerFeatures()));
        serializerFeatures.addAll(Arrays.asList(
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteMapNullValue
        ));
        SerializerFeature[] newSerializerFeatures = serializerFeatures.toArray(new SerializerFeature[]{});
        newFastJsonConfig.setSerializerFeatures(newSerializerFeatures);

        newFastJsonConfig.setDateFormat(fastJsonConfig.getDateFormat());

        newFastJsonConfig.setCharset(fastJsonConfig.getCharset());

        newFastJsonConfig.setSerializeConfig(fastJsonConfig.getSerializeConfig());

        newFastJsonConfig.setClassSerializeFilters(fastJsonConfig.getClassSerializeFilters());

        newFastJsonConfig.setWriteContentLength(fastJsonConfig.isWriteContentLength());

        newFastJsonConfig.setFeatures(fastJsonConfig.getFeatures());

        newFastJsonConfig.setParserConfig(fastJsonConfig.getParserConfig());

        newFastJsonConfig.setParseProcess(fastJsonConfig.getParseProcess());

        return newFastJsonConfig;
    }

}
