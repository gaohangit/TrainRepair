package com.ydzbinfo.emis.configs;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.boot.starter.ConfigurationCustomizer;
import com.baomidou.mybatisplus.spring.boot.starter.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.spring.boot.starter.MybatisPlusProperties;
import com.ydzbinfo.emis.handlers.AppPaginationInterceptor;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 自定义sqlSessionFactory，替换掉框架分页插件
 *
 * @author 张天可
 * @since 2022/3/24
 */
@Configuration()
public class AppMybatisPlusConfig {

    protected static final Logger logger = LoggerFactory.getLogger(AppMybatisPlusConfig.class);

    /**
     * 创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(MybatisPlusProperties properties,
                                               ObjectProvider<Interceptor[]> interceptorsProvider,
                                               ResourceLoader resourceLoader,
                                               ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                               ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                               DataSource dataSource) throws Exception {
        Interceptor[] interceptors = interceptorsProvider.getIfAvailable();
        // 替换掉框架提供的分页插件
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (int i = 0; i < interceptors.length; i++) {
                Interceptor interceptor = interceptors[i];
                if (interceptor instanceof PaginationInterceptor) {
                    PaginationInterceptor paginationInterceptor = (PaginationInterceptor) interceptor;
                    AppPaginationInterceptor appPaginationInterceptor = new AppPaginationInterceptor();
                    Field dialectClazzField = ReflectUtil.getEntityField(paginationInterceptor.getClass(), "dialectClazz");
                    dialectClazzField.setAccessible(true);
                    try {
                        appPaginationInterceptor.setDialectClazz((String) dialectClazzField.get(paginationInterceptor));
                    } catch (IllegalAccessException e) {
                        logger.error("设置方言实现类失败", e);
                    }
                    interceptors[i] = appPaginationInterceptor;
                }
            }
        }
        return new MybatisPlusAutoConfiguration(
            properties,
            new ObjectProvider<Interceptor[]>() {
                @Override
                public Interceptor[] getObject(Object... objects) throws BeansException {
                    return interceptors;
                }

                @Override
                public Interceptor[] getIfAvailable() throws BeansException {
                    return interceptors;
                }

                @Override
                public Interceptor[] getIfUnique() throws BeansException {
                    return interceptors;
                }

                @Override
                public Interceptor[] getObject() throws BeansException {
                    return interceptors;
                }
            },
            resourceLoader,
            databaseIdProvider,
            configurationCustomizersProvider
        ).sqlSessionFactory(dataSource);
    }
}
