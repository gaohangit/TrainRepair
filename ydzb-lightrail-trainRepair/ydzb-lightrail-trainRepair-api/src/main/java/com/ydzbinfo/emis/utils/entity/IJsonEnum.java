package com.ydzbinfo.emis.utils.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ydzbinfo.emis.utils.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * 用于自定义枚举的序列化和反序列化，需要在类加载时静态调用register方法
 * 暂不支持String和Integer以外的值类型
 *
 * @author 张天可
 * @since 2021/8/19
 */
public interface IJsonEnum<T> {

    T getValue();

    static <T> IJsonEnum<T> getEnum(Class<? extends IJsonEnum<T>> clazz, T value) {
        if (clazz.isEnum()) {
            for (IJsonEnum<T> enumConstant : clazz.getEnumConstants()) {
                if (value == null) {
                    if (enumConstant.getValue() == null) {
                        return enumConstant;
                    }
                } else {
                    if (enumConstant.getValue() != null && value.toString().equals(enumConstant.getValue().toString())) {
                        return enumConstant;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    class CodecUtil {
        private static void serialize(Object enumValue, Consumer<Integer> writeInt, Consumer<String> writeString, Runnable writeNull) {
            if (!(enumValue instanceof IJsonEnum)) {
                writeNull.run();
            } else {
                Object value = ((IJsonEnum) enumValue).getValue();
                if (value == null) {
                    writeNull.run();
                } else {
                    if (value instanceof Integer) {
                        writeInt.accept((Integer) value);
                    } else if (value instanceof String) {
                        writeString.accept((String) value);
                    } else {
                        throw new RuntimeException("IFastjsonEnum不支持Integer、String以外的值类型");
                    }
                }
            }
        }

        private static IJsonEnum deserialize(Class<?> enumClass, Object value) {
            if (IJsonEnum.class.isAssignableFrom(enumClass)) {
                Class<IJsonEnum<Object>> clazz = (Class<IJsonEnum<Object>>) enumClass;
                return IJsonEnum.getEnum(clazz, value);
            } else {
                return null;
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Configuration
    class JacksonConfig {
        private static final Queue<Class<? extends IJsonEnum>> UNREGISTERED_ENUM_CLASSES = new ConcurrentLinkedQueue<>();
        private static final Queue<Class<? extends IJsonEnum>> REGISTERED_ENUM_CLASSES = new ConcurrentLinkedQueue<>();

        private static void register(Class<? extends IJsonEnum> enumClass) {
            UNREGISTERED_ENUM_CLASSES.add(enumClass);
            checkRegister();
        }

        private static List<ObjectMapper> objectMappers;

        @Autowired(required = false)
        public void setObjectMappers(List<ObjectMapper> objectMappers) {
            JacksonConfig.objectMappers = objectMappers;
            checkRegister();
        }

        private static void throwException(IOException e, IJsonEnum enumValue) {
            throw new RuntimeException("枚举值序列化失败:" + (enumValue == null ? "" : enumValue.getClass().getName()), e);
        }

        private static void checkRegister() {
            if (objectMappers != null && objectMappers.size() > 0) {
                List<Class<? extends IJsonEnum>> needRegisteredJsonEnumList = new ArrayList<>();
                while (UNREGISTERED_ENUM_CLASSES.size() > 0) {
                    Class<? extends IJsonEnum> enumClass = UNREGISTERED_ENUM_CLASSES.remove();
                    if (!REGISTERED_ENUM_CLASSES.contains(enumClass)) {
                        needRegisteredJsonEnumList.add(enumClass);
                        REGISTERED_ENUM_CLASSES.add(enumClass);
                    }
                }
                if (needRegisteredJsonEnumList.size() > 0) {
                    SimpleModule module = new SimpleModule();
                    for (Class<? extends IJsonEnum> enumClass : needRegisteredJsonEnumList) {
                        module.addSerializer(enumClass, new JsonSerializer<IJsonEnum>() {
                            @Override
                            public void serialize(IJsonEnum enumValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                                CodecUtil.serialize(enumValue, (v) -> {
                                    try {
                                        jsonGenerator.writeNumber(v);
                                    } catch (IOException e) {
                                        throwException(e, enumValue);
                                    }
                                }, (v) -> {
                                    try {
                                        jsonGenerator.writeString(v);
                                    } catch (IOException e) {
                                        throwException(e, enumValue);
                                    }
                                }, () -> {
                                    try {
                                        jsonGenerator.writeNull();
                                    } catch (IOException e) {
                                        throwException(e, enumValue);
                                    }
                                });
                            }
                        });
                    }
                    Set<Class<? extends IJsonEnum>> currentEnumClasses = Collections.unmodifiableSet(new HashSet<>(needRegisteredJsonEnumList));
                    for (ObjectMapper objectMapper : objectMappers) {
                        objectMapper.registerModule(module);
                        TypeResolverBuilder<?> defaultTyper = objectMapper.getDeserializationConfig().getDefaultTyper(null);
                        objectMapper.setDefaultTyping(
                            JacksonJsonUtil.getWrappedTypeResolverBuilder(defaultTyper, (deserializationConfig, javaType, namedTypes) -> {
                                Class<?> rawClass = javaType.getRawClass();
                                if (currentEnumClasses.contains(rawClass)) {
                                    return JacksonJsonUtil.buildSimpleTypeDeserializer(
                                        (jsonParser, deserializationContext) -> CodecUtil.deserialize(rawClass, jsonParser.getValueAsString())
                                    );
                                } else {
                                    return null;
                                }
                            })
                        );
                    }
                }
            }
        }

    }


    /**
     * 注册enum类用于序列化、反序列化
     *
     * @param clazz 要注册的类对象
     */
    static <T> void register(Class<? extends IJsonEnum<T>> clazz) {
        JSON.addMixInAnnotations(clazz, FastjsonEnumMixIn.class);
        JacksonConfig.register(clazz);
    }

    @JSONType(
        deserializer = IJsonEnum.FastjsonEnumDeserializer.class,
        serializer = IJsonEnum.FastjsonEnumSerializer.class,
        serializeEnumAsJavaBean = true// 强制取消默认的序列化实现，否则不会使用自定义的实现
    )
    class FastjsonEnumMixIn {

    }

    class FastjsonEnumDeserializer implements ObjectDeserializer {
        @SuppressWarnings("unchecked")
        @Override
        public Object deserialze(DefaultJSONParser defaultJSONParser, Type fieldType, Object fieldName) {
            if (fieldType instanceof Class) {
                Object value = defaultJSONParser.parse(fieldName);
                return CodecUtil.deserialize((Class<?>) fieldType, value);
            } else {
                return null;
            }
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }

    class FastjsonEnumSerializer implements ObjectSerializer {
        @Override
        public void write(JSONSerializer serializer, Object value, Object fieldName, Type fieldType, int features) {
            SerializeWriter serializeWriter = serializer.out;
            CodecUtil.serialize(value, serializeWriter::writeInt, serializeWriter::writeString, serializeWriter::writeNull);
        }
    }

    // static void main(String[] args) throws IOException {
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     JacksonConfig jacksonConfig = new JacksonConfig();
    //     jacksonConfig.setObjectMappers(new ArrayList<>(Collections.singletonList(objectMapper)));
    //     register(BillEntityChangeTypeEnum.class);
    //     ChecklistAreaInfoForSave aa = new ChecklistAreaInfoForSave();
    //     aa.setChangeType(BillEntityChangeTypeEnum.INSERT);
    //     List<BillEntityChangeTypeEnum> a = new ArrayList<>();
    //     a.add(BillEntityChangeTypeEnum.INSERT);
    //     String json = objectMapper.writeValueAsString(a);
    //     System.out.println(json);
    //     String jsonaa = objectMapper.writeValueAsString(aa);
    //     System.out.println(jsonaa);
    //     // ChecklistAreaInfoForSave b = objectMapper.readValue(json, ChecklistAreaInfoForSave.class);
    //     List<BillEntityChangeTypeEnum> b = objectMapper.readValue(json, new TypeReference<ArrayList<BillEntityChangeTypeEnum>>() {
    //     });
    //     System.out.println(b);
    //     // ChecklistAreaInfoForSave c = objectMapper.readValue(json, ChecklistAreaInfoForSave.class);
    //     ChecklistAreaInfoForSave caa = objectMapper.readValue(jsonaa, new TypeReference<ChecklistAreaInfoForSave>() {
    //     });
    //     System.out.println(caa);
    // }

    static <ENUM extends IJsonEnum<T>, T> ENUM from(T value, Class<ENUM> enumClass) {
        return EnumUtils.findEnum(enumClass, IJsonEnum::getValue, value);
    }

    default boolean is(T value) {
        return Objects.equals(value, this.getValue());
    }
}
