package com.ydzbinfo.emis.utils.result;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.ydzbinfo.emis.utils.entity.IJsonEnum;
import com.ydzbinfo.emis.utils.entity.JacksonJsonUtil;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.base.RestResponseUtil;
import com.ydzbinfo.emis.utils.result.base.RestResultGenericBase;
import lombok.Data;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * 带泛型的通用返回结果实体
 *
 * @author 张天可
 */
@JSONType(
    deserializer = RestResultGeneric.FastjsonRestResultGenericDeserializer.class
)
public class RestResultGeneric<T> extends RestResultGenericBase<T, RestResultGeneric<T>> {

    private static final RestResponseUtil<RestResultGeneric<?>> restResponseUtilInstance;

    static {
        restResponseUtilInstance = new RestResponseUtil<RestResultGeneric<?>>() {
            @Override
            public RestResultGeneric<?> getNewResponseObject(Integer code) {
                return new RestResultGeneric<>(RestResponseCodeEnum.from(code));
            }

            @Override
            public void setMessage(RestResultGeneric<?> responseObject, String message) {
                responseObject.setMsg(message);
            }
        };
    }

    RestResultGeneric(RestResponseCodeEnum code) {
        super(code);
    }

    // /**
    //  * 获取成功结果对象
    //  */
    // public static <T> RestResultGeneric<T> success() {
    //     return new RestResultGeneric<>(RestResponseCodeEnum.SUCCESS);
    // }

    /**
     * 获取成功结果对象
     *
     * @param data 返回数据对象
     */
    public static <T> RestResultGeneric<T> success(T data) {
        return new RestResultGeneric<T>(RestResponseCodeEnum.SUCCESS).setData(data);
    }

    @SuppressWarnings("unchecked")
    public static <T> RestResultGeneric<T> fromException(Exception e, Logger logger, String defaultErrorMessage) {
        return (RestResultGeneric<T>) restResponseUtilInstance.getResultFromException(e, RESULT_CODE_STYLE, logger, defaultErrorMessage);
    }

    /**
     * 由于RestResultGeneric类私有化了构造方法，所以需要自定义json反序列化，jackson
     */
    @Configuration
    public static class JacksonRestResultGenericDeserializeConfig {

        private void checkJsonToken(JsonToken currentJsonToken, JsonToken... needJsonTokens) {
            if (Arrays.stream(needJsonTokens).noneMatch(needJsonToken -> currentJsonToken == needJsonToken)) {
                throw new RuntimeException("反序列化RestResultGeneric类型失败");
            }
        }

        @Autowired(required = false)
        public void setObjectMappers(List<ObjectMapper> objectMappers) {
            for (ObjectMapper objectMapper : objectMappers) {
                TypeResolverBuilder<?> defaultTyper = objectMapper.getDeserializationConfig().getDefaultTyper(null);
                objectMapper.setDefaultTyping(JacksonJsonUtil.getWrappedTypeResolverBuilder(
                    defaultTyper,
                    (deserializationConfig, javaType, namedTypes) -> {
                        if (javaType.getRawClass().equals(RestResultGeneric.class)) {
                            JavaType subDataType = javaType.containedType(0);

                            return JacksonJsonUtil.buildSimpleTypeDeserializer((jsonParser, deserializationContext) -> {
                                checkJsonToken(jsonParser.currentToken(), JsonToken.START_OBJECT);
                                RestResponseCodeEnum codeEnum = null;
                                String msg = null;
                                Object data = null;
                                for (
                                    JsonToken pairStartToken = jsonParser.nextToken();
                                    pairStartToken != JsonToken.END_OBJECT && pairStartToken != null;
                                    pairStartToken = jsonParser.nextToken()
                                ) {
                                    checkJsonToken(pairStartToken, JsonToken.FIELD_NAME);
                                    String fieldName = jsonParser.getCurrentName();
                                    JsonToken valueToken = jsonParser.nextToken();
                                    switch (fieldName) {
                                        case "code":
                                            checkJsonToken(valueToken, JsonToken.VALUE_NUMBER_INT);
                                            Integer code = jsonParser.getIntValue();
                                            codeEnum = IJsonEnum.from(code, RestResponseCodeEnum.class);
                                            break;
                                        case "msg":
                                            checkJsonToken(valueToken, JsonToken.VALUE_STRING, JsonToken.VALUE_NULL);
                                            msg = jsonParser.getValueAsString();
                                            break;
                                        case "data":
                                            if (valueToken != JsonToken.VALUE_NULL) {
                                                ObjectCodec rootCodec = jsonParser.getCodec();
                                                JsonParser subDataJsonParser = rootCodec.treeAsTokens(rootCodec.readTree(jsonParser));
                                                ObjectCodec subDataCodec = subDataJsonParser.getCodec();
                                                data = subDataCodec.readValue(subDataJsonParser, subDataType);
                                            }
                                            break;
                                    }
                                }
                                if (jsonParser.currentToken() == JsonToken.END_OBJECT) {
                                    jsonParser.nextToken();
                                }
                                return new RestResultGeneric<>(codeEnum).setData(data).setMsg(msg);
                            });
                        } else {
                            return null;
                        }
                    }
                ));
            }
        }
    }

    /**
     * 由于RestResultGeneric类私有化了构造方法，所以需要自定义json反序列化，fastjson
     */
    public static class FastjsonRestResultGenericDeserializer implements ObjectDeserializer {

        @Data
        static class FakeResult<T> {
            private Integer code;
            private String msg;
            private T data;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object deserialze(DefaultJSONParser defaultJSONParser, Type fieldType, Object fieldName) {
            Integer code;
            String msg;
            Object data;
            if (fieldType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) fieldType;
                FakeResult<?> fakeResult = defaultJSONParser.parseObject(new ParameterizedTypeImpl(parameterizedType.getActualTypeArguments(), null, FakeResult.class), fieldName);
                code = fakeResult.getCode();
                msg = fakeResult.getMsg();
                data = fakeResult.getData();
            } else {
                JSONObject value = defaultJSONParser.parseObject();
                code = value.getInteger("code");
                msg = value.getString("msg");
                data = value.get("data");
            }
            RestResponseCodeEnum restResponseCodeEnum = IJsonEnum.from(code, RestResponseCodeEnum.class);
            return new RestResultGeneric<>(restResponseCodeEnum).setData(data).setMsg(msg);
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }


    // public static void main(String[] args) throws IOException {
    //     IJsonEnum.register(RestResponseCodeEnum.class);
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     new IJsonEnum.JacksonConfig().setObjectMappers(Collections.singletonList(objectMapper));
    //     new JacksonJsonDeserializerConfiguration().setObjectMappers(Collections.singletonList(objectMapper));
    //     // RestResponseCodeEnum[] aa = new RestResponseCodeEnum[]{RestResponseCodeEnum.FATAL_FAIL, RestResponseCodeEnum.NORMAL_FAIL, RestResponseCodeEnum.SUCCESS};
    //     // String aax = JSON.toJSONString(aa);
    //     // System.out.println(aax);
    //     // System.out.println(JSON.parseObject(aax, new TypeReference<ArrayList<RestResponseCodeEnum>>(){}));
    //     RestResultGeneric<ArrayList<TemplateAll>> success = RestResultGeneric.success(new ArrayList<>());
    //     success.setMsg("asd");
    //     TemplateAll templateAll = new TemplateAll();
    //     templateAll.setTemplateId("asd");
    //     success.getData().add(templateAll);
    //     String x = JSON.toJSONString(success);
    //     System.out.println(x);
    //     System.out.println(objectMapper.writeValueAsString(success));
    //     TypeReference<RestResultGeneric<ArrayList<TemplateAll>>> restResultGenericTypeReference = new TypeReference<RestResultGeneric<ArrayList<TemplateAll>>>() {
    //     };
    //     RestResultGeneric<ArrayList<TemplateAll>> arrayListRestResultGeneric = JSON.parseObject(x, restResultGenericTypeReference);
    //     System.out.println(arrayListRestResultGeneric);
    //     Object value = objectMapper.readValue(x, new com.fasterxml.jackson.core.type.TypeReference<RestResultGeneric<ArrayList<TemplateAll>>>() {
    //     });
    //     System.out.println(value);
    //
    //     RestResultGeneric<Integer> success2 = RestResultGeneric.success(1);
    //     Object value2 = objectMapper.readValue(objectMapper.writeValueAsString(success2), new com.fasterxml.jackson.core.type.TypeReference<RestResultGeneric<Integer>>() {
    //     });
    //     System.out.println(value2);
    // }
}
