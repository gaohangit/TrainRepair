package com.ydzbinfo.emis.utils;


import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张天可
 * @description
 * @createDate 2021/5/31 17:19
 **/
public class CacheUtil {

    private static final ThreadLocal<Map<Object, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    @SuppressWarnings("unchecked")
    public static <T> T getDataUseThreadCache(Object key, Supplier<T> supplier) {
        Object cacheValue = threadLocal.get().get(key);
        if (cacheValue != null) {
            return (T) cacheValue;
        } else {
            T value = supplier.get();
            threadLocal.get().put(key, value);
            return value;
        }
    }

    /**
     * 获取数据提供方法的缓存版本，即获取第一次之后都从缓存中获取
     */
    public static <T> Supplier<T> getCachedDataGetter(Supplier<T> supplier) {
        ValueWrapper<T> data = new ValueWrapper<>();
        ValueWrapper<Boolean> isGotten = new ValueWrapper<>(false);
        return () -> {
            if (!isGotten.getValue()) {
                data.setValue(supplier.get());
                isGotten.setValue(true);
            }
            return data.getValue();
        };
    }

    @Configuration
    public static class LocalInterceptorConfigurer implements WebMvcConfigurer {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new LocalInterceptor());
        }
    }

    /**
     * 由于线程池会复用线程，每次请求后需要刷新缓存数据
     */
    static class LocalInterceptor implements HandlerInterceptor {
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            // 必须 remove
            // 避免因容器线程池复用导致 ThreadLocal 错乱问题
            threadLocal.remove();
        }
    }
}
