package com.ydzbinfo.emis.utils.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.*;
import com.ydzbinfo.emis.utils.TriFunction;
import lombok.Data;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张天可
 * @since 2022/4/12
 */
public class JacksonJsonUtil {

    private static class TypeResolverBuilderWrapper implements TypeResolverBuilder<TypeResolverBuilderWrapper> {

        private final TypeResolverBuilder<?> oriTypeResolverBuilder;
        private final TriFunction<DeserializationConfig, JavaType, Collection<NamedType>, TypeDeserializer> buildTypeDeserializer;

        public TypeResolverBuilderWrapper(TypeResolverBuilder<?> oriTypeResolverBuilder, TriFunction<DeserializationConfig, JavaType, Collection<NamedType>, TypeDeserializer> buildTypeDeserializer) {
            this.oriTypeResolverBuilder = oriTypeResolverBuilder;
            this.buildTypeDeserializer = buildTypeDeserializer;
        }

        @Override
        public Class<?> getDefaultImpl() {
            return oriTypeResolverBuilder == null ? null : oriTypeResolverBuilder.getDefaultImpl();
        }

        @Override
        public TypeSerializer buildTypeSerializer(SerializationConfig serializationConfig, JavaType javaType, Collection<NamedType> collection) {
            return oriTypeResolverBuilder == null ? null : oriTypeResolverBuilder.buildTypeSerializer(serializationConfig, javaType, collection);
        }

        @Override
        public TypeDeserializer buildTypeDeserializer(DeserializationConfig deserializationConfig, JavaType javaType, Collection<NamedType> collection) {
            TypeDeserializer deserializer = buildTypeDeserializer.apply(deserializationConfig, javaType, collection);
            if (deserializer != null) {
                return deserializer;
            } else {
                if (oriTypeResolverBuilder != null) {
                    return oriTypeResolverBuilder.buildTypeDeserializer(deserializationConfig, javaType, collection);
                } else {
                    return null;
                }
            }
        }

        @Override
        public TypeResolverBuilderWrapper init(JsonTypeInfo.Id id, TypeIdResolver typeIdResolver) {
            if (oriTypeResolverBuilder != null)
                oriTypeResolverBuilder.init(id, typeIdResolver);
            return this;
        }

        @Override
        public TypeResolverBuilderWrapper inclusion(JsonTypeInfo.As as) {
            if (oriTypeResolverBuilder != null)
                oriTypeResolverBuilder.inclusion(as);
            return this;
        }

        @Override
        public TypeResolverBuilderWrapper typeProperty(String s) {
            if (oriTypeResolverBuilder != null)
                oriTypeResolverBuilder.typeProperty(s);
            return this;
        }

        @Override
        public TypeResolverBuilderWrapper defaultImpl(Class<?> aClass) {
            if (oriTypeResolverBuilder != null)
                oriTypeResolverBuilder.defaultImpl(aClass);
            return this;
        }

        @Override
        public TypeResolverBuilderWrapper typeIdVisibility(boolean b) {
            if (oriTypeResolverBuilder != null)
                oriTypeResolverBuilder.typeIdVisibility(b);
            return this;
        }
    }

    public static TypeResolverBuilder<?> getWrappedTypeResolverBuilder(
        TypeResolverBuilder<?> oriTypeResolverBuilder,
        TriFunction<DeserializationConfig, JavaType, Collection<NamedType>, TypeDeserializer> buildTypeDeserializer
    ) {
        return new TypeResolverBuilderWrapper(oriTypeResolverBuilder, buildTypeDeserializer);
    }

    @FunctionalInterface
    public interface BiFunctionWithIOException<T, U, R> {
        R apply(T t, U u) throws IOException;
    }

    private static class SimpleTypeDeserializer extends TypeDeserializer {
        private final BiFunctionWithIOException<JsonParser, DeserializationContext, Object> deserializer;

        private SimpleTypeDeserializer(BiFunctionWithIOException<JsonParser, DeserializationContext, Object> deserializer) {
            this.deserializer = deserializer;
        }

        @Override
        public TypeDeserializer forProperty(BeanProperty beanProperty) {
            return this;
        }

        @Override
        public JsonTypeInfo.As getTypeInclusion() {
            return null;
        }

        @Override
        public String getPropertyName() {
            return null;
        }

        @Override
        public TypeIdResolver getTypeIdResolver() {
            return null;
        }

        @Override
        public Class<?> getDefaultImpl() {
            return null;
        }

        @Override
        public Object deserializeTypedFromObject(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return deserializer.apply(jsonParser, deserializationContext);
        }

        @Override
        public Object deserializeTypedFromArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return deserializer.apply(jsonParser, deserializationContext);
        }

        @Override
        public Object deserializeTypedFromScalar(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return deserializer.apply(jsonParser, deserializationContext);
        }

        @Override
        public Object deserializeTypedFromAny(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return deserializer.apply(jsonParser, deserializationContext);
        }
    }

    public static TypeDeserializer buildSimpleTypeDeserializer(BiFunctionWithIOException<JsonParser, DeserializationContext, Object> deserializer) {
        return new SimpleTypeDeserializer(deserializer);
    }

    @Data
    public static class JsonObjectPair {
        private String name;
        private Supplier namea;
    }

    public static void nextPair(JsonParser jsonParser) {
    }
}
