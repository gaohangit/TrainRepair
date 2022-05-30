package com.ydzbinfo.emis.utils;

import com.alibaba.fastjson.JSON;
import com.ydzbinfo.hussar.core.util.RestKit;
import com.ydzbinfo.hussar.core.util.RestRequestKit;
import com.ydzbinfo.hussar.core.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.LinkedHashMap;
import java.util.Objects;

public class RestRequestKitUseLogger<T> extends RestRequestKit<T> {
    private Logger logger;

    private String requestServiceId;

    private RestRequestKitUseLogger() {
        super();
    }

    public RestRequestKitUseLogger(Logger logger) {
        this();
        Objects.requireNonNull(logger);
        this.logger = logger;
    }

    public RestRequestKitUseLogger(String serviceId, Logger logger) {
        super(serviceId);
        requestServiceId = serviceId;
        Objects.requireNonNull(logger);
        this.logger = logger;
    }

    @Override
    public T exchangeObject(String url, HttpMethod methodType, HttpEntity requestEntity, Object... uriVariables) {
        T result;
        try {
            result = super.exchangeObject(url, methodType, requestEntity, uriVariables);
            return result;
        } catch (Exception e) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            if (ToolUtil.isNotEmpty(this.requestServiceId)) {
                url = RestKit.getServicePath(this.requestServiceId) + url;
            }
            map.put("url", url);
            map.put("methodType", methodType);
            if (requestEntity.getBody() != null) {
                map.put("requestBody", requestEntity.getBody());
            }
            if (uriVariables != null && uriVariables.length > 0) {
                map.put("uriVariables", uriVariables);
            }
            if (e instanceof HttpStatusCodeException) {
                HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) e;
                map.put("responseStatusCode", EnumUtils.toMap(httpStatusCodeException.getStatusCode()));
                map.put("responseBody", httpStatusCodeException.getResponseBodyAsString());
            }
            this.logger.error(JSON.toJSONString(map, true));
            throw e;
        }
    }
}
