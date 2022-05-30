package com.ydzbinfo.emis.handlers.kafka;

import com.alibaba.fastjson.JSON;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.hussar.core.util.SpringCtxHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author 张天可
 * @since 2021/11/26
 */
@Aspect
@Configuration
@Slf4j
public class StreamCloudTransDataAop {

    private static final ThreadLocal<Boolean> isExecuting = ThreadLocal.withInitial(() -> false);

    @Around("@annotation(com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        boolean innerSetIsExecuting = false;
        try {
            if (isExecuting.get() || StreamCloudTransDataReceiverConfigurator.getIsReceivingInCurrentThread()) {
                return pjp.proceed();
            } else {
                isExecuting.set(true);
                innerSetIsExecuting = true;
                Object result = pjp.proceed();
                isExecuting.set(false);
                Method method = ((MethodSignature) pjp.getSignature()).getMethod();
                StreamCloudTransData streamCloudTransData = method.getAnnotation(StreamCloudTransData.class);
                if (SpringCloudStreamUtil.enableSendCloudData(streamCloudTransData.module())) {
                    Map<String, MessageChannel> beansOfType = SpringCtxHolder.getApplicationContext().getBeansOfType(MessageChannel.class);
                    if (beansOfType.containsKey(streamCloudTransData.outputChannel())) {
                        MessageChannel messageChannel = beansOfType.get(streamCloudTransData.outputChannel());
                        String operateType = streamCloudTransData.operateType();
                        if (StringUtils.isBlank(operateType)) {
                            operateType = method.getName();
                        }
                        // 将方法的参数作为要同步的数据进行传递
                        Object[] args = pjp.getArgs();
                        String argsJsonStr = JSON.toJSONString(args);
                        System.out.println(argsJsonStr);
                        MessageWrapper<byte[]> messageWrapper = new MessageWrapper<>(operateType, argsJsonStr.getBytes(StandardCharsets.UTF_8));
                        boolean sendFlag = MessageUtil.sendMessage(messageChannel, messageWrapper);
                        if (!sendFlag) {
                            log.error("数据推送失败\n" + JSON.toJSONString(messageWrapper, true));
                        }
                    }
                }
                return result;
            }
        } finally {
            if (isExecuting.get() && innerSetIsExecuting) {
                isExecuting.set(false);
            }
        }
    }

}
