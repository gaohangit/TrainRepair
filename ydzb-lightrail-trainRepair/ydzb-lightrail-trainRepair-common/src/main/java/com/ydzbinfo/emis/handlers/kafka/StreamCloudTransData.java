package com.ydzbinfo.emis.handlers.kafka;

import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据同步标记注解
 * 支持方法同步方式嵌套
 * 注：要求被注解的方法中只有数据库插入、删除或更新操作，没有其他导致数据同步不一致的情况
 *
 * @author 张天可
 * @since 2021/11/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StreamCloudTransData {
    /**
     * 模块
     */
    SpringCloudStreamModuleEnum module();

    /**
     * 输出通道名称
     */
    String outputChannel();

    /**
     * 输入通道名称
     */
    String inputChannel();

    /**
     * 通道操作类型名称，默认为注解的方法名
     */
    String operateType() default "";
}
