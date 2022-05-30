package com.ydzbinfo.emis.utils;

import com.ydzbinfo.emis.configs.kafka.BaseMqSink;
import com.ydzbinfo.emis.configs.kafka.BaseMqSource;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

/**
 * @author 张天可
 * @date 2021/10/13
 */
@Component
public class SpringCloudStreamUtil {

    public static final String ENABLE_SEND_CLOUD_DATA_PROPERTY_NAME;
    public static final String ENABLE_RECEIVE_CLOUD_DATA_PROPERTY_NAME;

    static {
        SpringCloudStreamProperties springCloudStreamProperties_ = new SpringCloudStreamProperties();
        ENABLE_SEND_CLOUD_DATA_PROPERTY_NAME = springCloudStreamProperties_.getPropertyPath(SpringCloudStreamProperties::getEnableSendCloudData);
        ENABLE_RECEIVE_CLOUD_DATA_PROPERTY_NAME = springCloudStreamProperties_.getPropertyPath(SpringCloudStreamProperties::getEnableReceiveCloudData);
    }

    private static boolean matches(ConditionContext conditionContext, String propertyName) {
        Boolean property = conditionContext.getEnvironment().getProperty(propertyName, Boolean.class);
        return property != null && property;
    }

    public abstract static class EnableReceiveCloudDataConditionBase implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            if (SpringCloudStreamUtil.matches(conditionContext, ENABLE_RECEIVE_CLOUD_DATA_PROPERTY_NAME)) {
                SpringCloudStreamModuleEnum moduleEnum = this.getModule();
                SpringCloudStreamProperties springCloudStreamProperties = Binder
                    .get(conditionContext.getEnvironment())
                    .bind(
                        TrainRepairPropertiesParent.getPrefix(SpringCloudStreamProperties.class),
                        SpringCloudStreamProperties.class
                    ).get();
                return springCloudStreamProperties.getEnableReceiveModules().stream().anyMatch(v -> v == moduleEnum);
            } else {
                return false;
            }
        }

        protected abstract SpringCloudStreamModuleEnum getModule();
    }

    public abstract static class EnableSendCloudDataConditionBase implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            if (SpringCloudStreamUtil.matches(conditionContext, ENABLE_SEND_CLOUD_DATA_PROPERTY_NAME)) {
                SpringCloudStreamModuleEnum moduleEnum = this.getModule();
                SpringCloudStreamProperties springCloudStreamProperties = Binder
                    .get(conditionContext.getEnvironment())
                    .bind(
                        TrainRepairPropertiesParent.getPrefix(SpringCloudStreamProperties.class),
                        SpringCloudStreamProperties.class
                    ).get();
                return springCloudStreamProperties.getEnableSendModules().stream().anyMatch(v -> v == moduleEnum);
            } else {
                return false;
            }
        }

        protected abstract SpringCloudStreamModuleEnum getModule();
    }

    private static SpringCloudStreamProperties springCloudStreamProperties;

    public SpringCloudStreamUtil(SpringCloudStreamProperties springCloudStreamProperties) {
        SpringCloudStreamUtil.springCloudStreamProperties = springCloudStreamProperties;
    }

    // /**
    //  * 是否配置启用发送同步数据
    //  */
    // public static boolean enableSendCloudData() {
    //     return springCloudStreamProperties.getEnableSendCloudData();
    // }

    /**
     * 指定模块是否启用发送同步数据
     */
    public static boolean enableSendCloudData(SpringCloudStreamModuleEnum moduleEnum) {
        return springCloudStreamProperties.getEnableSendCloudData() && springCloudStreamProperties.getEnableSendModules().stream().anyMatch(v -> v == moduleEnum);
    }

    public static <T extends BaseMqSource> boolean enableSendCloudData(Class<T> sourceClass) {
        SpringCloudStreamModuleEnum moduleEnum = EnumUtils.findEnum(SpringCloudStreamModuleEnum.class, SpringCloudStreamModuleEnum::getSourceClass, sourceClass);
        if (moduleEnum == null) {
            throw new IllegalArgumentException("未注册的source类：" + sourceClass.getName());
        }
        return enableSendCloudData(moduleEnum);
    }

    // /**
    //  * 是否配置启用接收同步数据
    //  */
    // public static boolean enableReceiveCloudData() {
    //     return springCloudStreamProperties.getEnableReceiveCloudData();
    // }

    /**
     * 指定模块是否启用接收同步数据
     */
    public static boolean enableReceiveCloudData(SpringCloudStreamModuleEnum moduleEnum) {
        return springCloudStreamProperties.getEnableReceiveCloudData() && springCloudStreamProperties.getEnableReceiveModules().stream().anyMatch(v -> v == moduleEnum);
    }

    /**
     * 指定模块是否启用接收同步数据
     */
    public static <T extends BaseMqSink> boolean enableReceiveCloudData(Class<T> sinkClass) {
        SpringCloudStreamModuleEnum moduleEnum = EnumUtils.findEnum(SpringCloudStreamModuleEnum.class, SpringCloudStreamModuleEnum::getSinkClass, sinkClass);
        if (moduleEnum == null) {
            throw new IllegalArgumentException("未注册的sink类：" + sinkClass.getName());
        }
        return enableReceiveCloudData(moduleEnum);
    }

}
