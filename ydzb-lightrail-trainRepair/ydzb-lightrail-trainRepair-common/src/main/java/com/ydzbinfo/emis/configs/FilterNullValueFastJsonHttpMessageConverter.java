package com.ydzbinfo.emis.configs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONPObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.spring.FastJsonContainer;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.ydzbinfo.emis.utils.SimpleTypeUtil;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author 张天可
 * @since 2022/4/19
 */
public class FilterNullValueFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    private static final List<Predicate<HttpServletRequest>> disableFilterNullValueMatchers = new ArrayList<>();

    public static synchronized void addDisableFilterNullValueMatcher(Predicate<HttpServletRequest> requestMatcher) {
        disableFilterNullValueMatchers.add(requestMatcher);
    }

    public static List<Predicate<HttpServletRequest>> getDisableFilterNullValueMatchers() {
        return Collections.unmodifiableList(disableFilterNullValueMatchers);
    }

    public static synchronized void removeDisableFilterNullValueMatchersIf(Predicate<Predicate<HttpServletRequest>> filter) {
        disableFilterNullValueMatchers.removeIf(filter);
    }

    private static final String EMPTY_STRING = "";
    /**
     * 部分null值转换为空字符串
     */
    private final ValueFilter nullValueFilter = (object, fieldName, value) -> {
        if (value == null && object != null && fieldName != null) {
            if (object instanceof Map) {
                // 存在不得不使用map的情况，如返回枚举的属性信息，所以这里放开，不处理map的情况
                return null;
            }
            Field field = ReflectUtil.getEntityField(object.getClass(), fieldName);
            if (field != null && SimpleTypeUtil.isSimpleType(field.getType())) {
                return EMPTY_STRING;
            }
        }
        return value;
    };

    private Object strangeCodeForJackson(Object obj) {
        if (obj != null) {
            String className = obj.getClass().getName();
            if ("com.fasterxml.jackson.databind.node.ObjectNode".equals(className)) {
                return obj.toString();
            }
        }

        return obj;
    }

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try (ByteArrayOutputStream outNew = new ByteArrayOutputStream()) {
            HttpHeaders headers = outputMessage.getHeaders();
            SerializeFilter[] globalFilters = this.getFastJsonConfig().getSerializeFilters();
            List<SerializeFilter> allFilters = new ArrayList<>(Arrays.asList(globalFilters));
            boolean isJsonp = false;
            Object value = this.strangeCodeForJackson(object);
            if (value instanceof FastJsonContainer) {
                FastJsonContainer fastJsonContainer = (FastJsonContainer) value;
                PropertyPreFilters filters = fastJsonContainer.getFilters();
                allFilters.addAll(filters.getFilters());
                value = fastJsonContainer.getValue();
            }
            HttpServletRequest currentRequest = getCurrentRequest();
            // 如果存在希望当前请求null值转换被禁用的matcher则不进行null值转换
            if (disableFilterNullValueMatchers.stream().noneMatch(v -> v.test(currentRequest))) {
                allFilters.add(nullValueFilter);
            }

            if (value instanceof JSONPObject) {
                isJsonp = true;
            }

            // 修复JSONField不生效的问题
            int len = JSON.writeJSONStringWithFastJsonConfig(
                outNew,
                this.getFastJsonConfig().getCharset(),
                value,
                this.getFastJsonConfig().getSerializeConfig(),
                allFilters.toArray(new SerializeFilter[allFilters.size()]),
                this.getFastJsonConfig().getDateFormat(),
                JSON.DEFAULT_GENERATE_FEATURE,
                this.getFastJsonConfig().getSerializerFeatures()
            );
            if (isJsonp) {
                headers.setContentType(APPLICATION_JAVASCRIPT);
            }

            if (this.getFastJsonConfig().isWriteContentLength()) {
                headers.setContentLength(len);
            }

            outNew.writeTo(outputMessage.getBody());
        } catch (JSONException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }
}
