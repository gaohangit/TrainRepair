package com.ydzbinfo.emis.handlers.kafka;

import com.alibaba.fastjson.JSON;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamProperties;
import com.ydzbinfo.emis.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author 张天可
 * @since 2021/11/26
 */
@Component
@Slf4j
public class StreamCloudTransDataReceiverConfigurator implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private final SpringCloudStreamProperties springCloudStreamProperties;

    private static final ThreadLocal<Boolean> isReceiving = ThreadLocal.withInitial(() -> false);

    public StreamCloudTransDataReceiverConfigurator(SpringCloudStreamProperties springCloudStreamProperties) {
        this.springCloudStreamProperties = springCloudStreamProperties;
    }

    private static final Map<SubscribableChannel, Map<String, MessageHandler>> subscribableChannelListeners = new HashMap<>();

    @PostConstruct
    private void subscribeCloudData() {
        if (springCloudStreamProperties.getEnableReceiveCloudData()) {
            String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                Method[] methods = applicationContext.getBean(beanDefinitionName).getClass().getMethods();
                for (Method method : methods) {
                    StreamCloudTransData streamCloudTransData = AnnotationUtils.findAnnotation(method, StreamCloudTransData.class);
                    if (streamCloudTransData != null && springCloudStreamProperties.getEnableReceiveModules().stream().anyMatch(v -> v == streamCloudTransData.module())) {
                        SubscribableChannel subscribableChannel = applicationContext.getBean(streamCloudTransData.inputChannel(), SubscribableChannel.class);
                        if (!subscribableChannelListeners.containsKey(subscribableChannel)) {
                            subscribableChannelListeners.put(subscribableChannel, new HashMap<>());
                            subscribableChannel.subscribe((Message<?> message) -> {
                                String currentOperateType = (String) message.getHeaders().get("operateType");
                                MessageHandler messageHandler = subscribableChannelListeners.get(subscribableChannel).get(currentOperateType);
                                if (messageHandler != null) {
                                    messageHandler.handleMessage(message);
                                }
                            });
                        }
                        String operateType;
                        if (StringUtils.isNotBlank(streamCloudTransData.operateType())) {
                            operateType = streamCloudTransData.operateType();
                        } else {
                            operateType = method.getName();
                        }
                        Method oriMethod = findOriMethod(method);
                        Type[] types = Arrays.stream(oriMethod.getParameters()).map(Parameter::getParameterizedType).toArray(Type[]::new);
                        Map<String, MessageHandler> map = subscribableChannelListeners.get(subscribableChannel);
                        if (map.containsKey(operateType)) {
                            throw new RuntimeException("同一通道下的operateType重复，请检查程序。operateType值：" + operateType + "，通道名称：" + streamCloudTransData.inputChannel());
                        }
                        map.put(operateType, (Message<?> message) -> {
                            try {
                                String payload = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                                List<Object> parameters = JSON.parseArray(payload, types);
                                isReceiving.set(true);
                                method.invoke(applicationContext.getBean(beanDefinitionName), parameters.toArray());
                                isReceiving.set(false);
                            } catch (Exception e) {
                                log.error("数据接收失败\n" + JSON.toJSONString(message, true), e);
                            } finally {
                                if (isReceiving.get()) {
                                    isReceiving.set(false);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private static Method findOriMethod(Method method) {
        Method curMethod = method;
        while (curMethod != null && curMethod.getAnnotation(StreamCloudTransData.class) == null) {
            try {
                curMethod = curMethod.getDeclaringClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                curMethod = null;
            }
        }
        return curMethod;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        StreamCloudTransDataReceiverConfigurator.applicationContext = applicationContext;
    }

    public static Boolean getIsReceivingInCurrentThread() {
        return isReceiving.get();
    }

}
