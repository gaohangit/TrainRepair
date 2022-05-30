package com.ydzbinfo.emis.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.configs.FastJsonHttpMessageConverterRepairConfig;
import com.ydzbinfo.emis.configs.FilterNullValueFastJsonHttpMessageConverter;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.DESUtil;
import lombok.Data;
import org.apache.catalina.util.ParameterMap;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * filter 用于统一加解密
 * 转换规则：
 * 1、加密字符串统一按照json格式解析
 * 2、get请求：从url里解密名称为RWMT的参数，按照form形式传递给controller
 * 3、post请求(application/x-www-form-urlencoded)：从请求体里和url解密名称为RWMT的参数，按照json的形式传递给controller
 * 4、post请求(multipart/form-data)：从请求体里和url解密名称为RWMT的参数，按照form的形式传递给controller，文件不进行解密
 * <p>
 * 传递参数说明：
 * 1、get请求：
 * 客户端：把参数转换为json串后用统一工具类加密后，以RWMT名称进行参数传递
 * 服务端：正常接收参数即可
 * 2、post请求(需求json形式)：
 * 客户端：设置content-type为application/x-www-form-urlencoded，把参数转换为json串后用统一工具类加密后，以RWMT名称进行参数传递
 * 服务端：正常接收参数即可
 * 3、post请求(需求form形式)
 * 客户端：设置content-type为multipart/form-data，把非文件参数转换为json串后用统一工具类加密，以RWMT名称进行参数传递，文件参数名称和内容均不用加密
 * 服务端：正常接收参数即可
 *
 * @author 张天可
 * @since 2021/6/30
 */
public class MobileEncryptFilter extends OncePerRequestFilter {

    protected static final Logger logger = LoggerFactory.getLogger(MobileEncryptFilter.class);

    private final String desKey;

    public MobileEncryptFilter(String desKey) {
        this.desKey = desKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        MobileEncryptServletRequestWrapper requestWrapper = new MobileEncryptServletRequestWrapper(httpServletRequest);
        MobileEncryptServletResponseWrapper responseWrapper = new MobileEncryptServletResponseWrapper(httpServletResponse);
        filterChain.doFilter(requestWrapper, responseWrapper);
        String data = new String(responseWrapper.getResponseData(), responseWrapper.getCharacterEncoding());
        logger.debug("原始返回数据：" + data);
        String encodedData = DESUtil.encode(desKey, data);
        logger.debug("加密返回数据：" + encodedData);
        assert encodedData != null;
        writeResponse(httpServletResponse, encodedData);
    }

    private void writeResponse(HttpServletResponse httpServletResponse, String data) throws IOException {
        PrintWriter writer = httpServletResponse.getWriter();
        httpServletResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
        httpServletResponse.setContentLength(data.length());
        writer.print(data);
        writer.flush();
        writer.close();
    }

    class MobileEncryptServletRequestWrapper extends HttpServletRequestWrapper {

        public static final String CONTENT_TYPE_NAME = "content-type";
        private final HttpServletRequest request;

        private final List<HeaderField> headers = new ArrayList<>();

        private final ParameterMap<String, String[]> parameterMap;

        private Supplier<String> getContent = null;
        private final String queryString;

        public MobileEncryptServletRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.request = request;
            this.parameterMap = new ParameterMap<>();

            String targetContentType = null;

            // 处理所有参数
            if (request.getContentLengthLong() > 0L && request.getContentType() != null) {
                String contentType = request.getContentType();
                if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    JSONObject jsonBody = new JSONObject();

                    JSONArray jsonArray = null;
                    boolean isArray = false;

                    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                        String name = entry.getKey();
                        String[] values = entry.getValue();
                        if (name.equals(Constants.HTTP_MOBILE_ENCRYPT_FORM_KEY)) {
                            // 加密字段
                            for (String value : values) {
                                Object jsonObj = JSON.parse(DESUtil.decode(desKey, value));
                                if (jsonObj instanceof JSONObject) {
                                    JSONObject jsonObject = (JSONObject) jsonObj;
                                    jsonBody.putAll(jsonObject);
                                    addJsonPropertyToMap(jsonObject, parameterMap);
                                } else if (jsonObj instanceof JSONArray) {
                                    jsonArray = (JSONArray) jsonObj;
                                    isArray = true;
                                }
                            }
                        } else {
                            parameterMap.put(name, values);
                        }
                    }
                    parameterMapToJSONObject(parameterMap).forEach(jsonBody::putIfAbsent);

                    String requestBody = isArray ? jsonArray.toJSONString() : jsonBody.toJSONString();
                    this.getContent = () -> requestBody;
                    targetContentType = getJsonContentType();
                } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                    MultipartHttpServletRequest multipartHttpServletRequest = new StandardMultipartHttpServletRequest(request);
                    multipartHttpServletRequest.getParameterMap().forEach((key, values) -> {
                        if (key.equals(Constants.HTTP_MOBILE_ENCRYPT_FORM_KEY)) {
                            // 加密字段
                            for (String value : values) {
                                JSONObject jsonObject = JSON.parseObject(DESUtil.decode(desKey, value));
                                addJsonPropertyToMap(jsonObject, parameterMap);
                            }
                        } else {
                            parameterMap.put(key, values);
                        }
                    });

                    this.getContent = () -> {
                        throw new RuntimeException("加密文件上传禁止直接读取请求体内容");
                    };
                } else if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                    byte[] encJsonStringData = IOUtils.readFully(request.getInputStream(), request.getContentLength());
                    Object obj = JSON.parse(DESUtil.decode(desKey, new String(encJsonStringData)));
                    if (obj instanceof JSONObject) {
                        JSONObject jsonBody = (JSONObject) obj;
                        addJsonPropertyToMap(jsonBody, parameterMap);
                        parameterMapToJSONObject(parameterMap).forEach(jsonBody::putIfAbsent);
                        String requestBody = jsonBody.toJSONString();
                        this.getContent = () -> requestBody;
                    } else if (obj instanceof JSONArray) {
                        JSONArray jsonBody = (JSONArray) obj;
                        String requestBody = jsonBody.toJSONString();
                        this.getContent = () -> requestBody;
                    }

                    targetContentType = getJsonContentType();
                }

            } else {
                // 仅处理queryString的参数
                String qs = request.getQueryString();
                if (qs != null) {
                    Parameters oriQueryStringParameters = new Parameters();
                    oriQueryStringParameters.setCharset(Charset.forName(getCharacterEncoding()));
                    MessageBytes queryMB = MessageBytes.newInstance();

                    byte[] qsBytes = qs.getBytes();
                    queryMB.setBytes(qsBytes, 0, qsBytes.length);
                    oriQueryStringParameters.setQuery(queryMB);
                    oriQueryStringParameters.handleQueryParameters();

                    everyParameters(oriQueryStringParameters, (name, values) -> {
                        if (name.equals(Constants.HTTP_MOBILE_ENCRYPT_FORM_KEY)) {
                            // 加密字段
                            for (String value : values) {
                                JSONObject jsonObject = JSON.parseObject(DESUtil.decode(desKey, value));
                                addJsonPropertyToMap(jsonObject, parameterMap);
                            }
                        } else {
                            parameterMap.put(name, values);
                        }
                    });
                }

            }
            this.queryString = serializeForm(parameterMap, Charset.forName(request.getCharacterEncoding()));

            parameterMap.setLocked(true);

            for (String headerName : EnumerationUtils.toList(request.getHeaderNames())) {
                HeaderField headerField = new HeaderField();
                headerField.setName(headerName);
                if (targetContentType != null && headerName.equalsIgnoreCase(CONTENT_TYPE_NAME)) {
                    headerField.setValue(new String[]{targetContentType});
                } else {
                    headerField.setValue(EnumerationUtils.toList(request.getHeaders(headerName)).toArray(new String[0]));
                }
                headers.add(headerField);
            }
        }

        @Data
        class HeaderField {
            private String name;
            private String[] value;
        }

        private String getJsonContentType() {
            return request.getCharacterEncoding().equals(StandardCharsets.UTF_8.name()) ? MediaType.APPLICATION_JSON_UTF8_VALUE : MediaType.APPLICATION_JSON_VALUE;
        }

        private String serializeForm(Map<String, String[]> formData, Charset charset) {
            StringBuilder builder = new StringBuilder();
            formData.forEach((name, values) -> {
                for (String value : values) {
                    try {
                        if (builder.length() != 0) {
                            builder.append('&');
                        }

                        builder.append(URLEncoder.encode(name, charset.name()));
                        if (value != null) {
                            builder.append('=');
                            builder.append(URLEncoder.encode(value, charset.name()));
                        }

                    } catch (UnsupportedEncodingException var5) {
                        throw new IllegalStateException(var5);
                    }
                }
            });
            return builder.toString();
        }

        private JSONObject parameterMapToJSONObject(Map<String, String[]> parameterMap) {
            JSONObject jsonObject = new JSONObject();
            parameterMap.forEach((key, value) -> {
                Object targetValue = null;
                if (value.length == 1) {
                    targetValue = value[0];
                } else if (value.length > 1) {
                    targetValue = value;
                }
                jsonObject.put(key, targetValue);
            });
            return jsonObject;
        }

        private Map<String, String[]> getParameterMap(Parameters parameters) {
            ParameterMap<String, String[]> parameterMap = new ParameterMap<>();
            Enumeration<String> enumeration = parameters.getParameterNames();

            while (enumeration.hasMoreElements()) {
                String name = enumeration.nextElement();
                String[] values = parameters.getParameterValues(name);
                parameterMap.put(name, values);
            }

            parameterMap.setLocked(true);
            return parameterMap;
        }

        private void everyParameters(Parameters parameters, BiConsumer<String, String[]> biConsumer) {
            Map<String, String[]> oriParametersMap = getParameterMap(parameters);
            for (Map.Entry<String, String[]> entry : oriParametersMap.entrySet()) {
                String name = entry.getKey();
                String[] value = entry.getValue();
                biConsumer.accept(name, value);
            }
        }

        private void addJsonPropertyToMap(JSONObject jsonObject, Map<String, String[]> map) {
            for (String encName : jsonObject.keySet()) {
                Object encValue = jsonObject.get(encName);
                String[] oriValue = map.get(encName);
                if (oriValue == null) {
                    oriValue = new String[0];
                }
                if (encValue instanceof List) {
                    List<?> arrayEncValue = (List<?>) encValue;
                    map.put(
                        encName,
                        ArrayUtils.addAll(oriValue, arrayEncValue.stream().filter(
                            Objects::nonNull
                        ).map(
                            Object::toString
                        ).toArray(String[]::new))

                    );
                } else {
                    map.put(encName, ArrayUtils.addAll(oriValue, encValue.toString()));
                }
            }
        }


        @Override
        public String getQueryString() {
            return this.queryString;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.stream().map(HeaderField::getName).collect(Collectors.toList()));
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            for (HeaderField header : this.headers) {
                if (header.getName().equalsIgnoreCase(name)) {
                    return Collections.enumeration(Arrays.asList(header.getValue()));
                }
            }
            return null;
        }

        @Override
        public String getHeader(String name) {
            Enumeration<String> headers = this.getHeaders(name);
            if (headers != null) {
                List<String> headerList = EnumerationUtils.toList(headers);
                if (headerList.size() > 0) {
                    return headerList.get(0);
                } else {
                    return "";
                }
            } else {
                return null;
            }
        }

        @Override
        public int getIntHeader(String name) {
            String value = this.getHeader(name);
            return value == null ? -1 : Integer.parseInt(value);
        }

        @Override
        public long getDateHeader(String name) {
            String value = this.getHeader(name);
            if (value == null) {
                return -1L;
            } else {
                long result = FastHttpDateFormat.parseDate(value);
                if (result != -1L) {
                    return result;
                } else {
                    throw new IllegalArgumentException(value);
                }
            }
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return this.parameterMap;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(this.parameterMap.keySet());
        }

        private String getValue(Map<String, String[]> map, String key) {
            if (map.containsKey(key)) {
                String[] value = map.get(key);
                return value.length == 0 ? "" : value[0];
            } else {
                return null;
            }
        }

        @Override
        public String getParameter(String name) {
            return getValue(this.parameterMap, name);
        }

        @Override
        public String[] getParameterValues(String name) {
            return this.parameterMap.get(name);
        }

        @Override
        public int getContentLength() {
            if (this.getContent == null) {
                return 0;
            } else {
                return this.getContent.get().getBytes().length;
            }
        }

        @Override
        public String getContentType() {
            return getHeader(CONTENT_TYPE_NAME);
        }

        @Override
        public long getContentLengthLong() {
            return new Integer(getContentLength()).longValue();
        }


        ServletInputStream inputStream = null;

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (getContent == null) {
                return null;
            } else {
                if (inputStream == null) {
                    String charsetName = request.getCharacterEncoding();
                    inputStream = new ServletInputStream() {
                        final ByteArrayInputStream inputStream = new ByteArrayInputStream(getContent.get().getBytes(charsetName));

                        @Override
                        public boolean isFinished() {
                            return inputStream.available() <= 0;
                        }

                        @Override
                        public boolean isReady() {
                            return true;
                        }

                        @Override
                        public void setReadListener(ReadListener readListener) {

                        }

                        @Override
                        public int read() throws IOException {
                            return inputStream.read();
                        }
                    };
                }
                return inputStream;
            }

        }

        BufferedReader reader = null;

        @Override
        public BufferedReader getReader() throws IOException {
            if (reader == null) {
                reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
            }
            return reader;
        }
    }

    static class MobileEncryptServletResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream buffer;
        private final ServletOutputStream outputStream;
        private final PrintWriter writer;

        public MobileEncryptServletResponseWrapper(HttpServletResponse response) {
            super(response);
            this.buffer = new ByteArrayOutputStream();
            this.outputStream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                }

                @Override
                public void write(int b) throws IOException {
                    buffer.write(b);
                }
            };
            try {
                this.writer = new PrintWriter(new OutputStreamWriter(buffer, this.getCharacterEncoding()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void reset() {
            buffer.reset();
        }

        @Override
        public void flushBuffer() throws IOException {
            outputStream.flush();
            writer.flush();
        }

        public byte[] getResponseData() throws IOException {
            flushBuffer();
            return buffer.toByteArray();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }
    }

}
