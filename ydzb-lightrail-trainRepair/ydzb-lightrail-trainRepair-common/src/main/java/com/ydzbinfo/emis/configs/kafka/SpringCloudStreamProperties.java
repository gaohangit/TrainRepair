package com.ydzbinfo.emis.configs.kafka;

import com.ydzbinfo.emis.configs.DeployLevelEnum;
import com.ydzbinfo.emis.utils.TrainRepairPropertiesParent;
import com.ydzbinfo.hussar.core.util.SpringCtxHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binding.BindingBeanDefinitionRegistryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * 项目额外数据流配置
 *
 * @author 张天可
 * @since 2021/11/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties("ydzb.stream")
public class SpringCloudStreamProperties extends TrainRepairPropertiesParent<SpringCloudStreamProperties> {

    /**
     * 是否启用数据推送
     */
    Boolean enableSendCloudData;

    /**
     * 是否启用数据接收
     */
    Boolean enableReceiveCloudData;

    /**
     * 启用数据接收的模块列表
     */
    List<SpringCloudStreamModuleEnum> enableSendModules;

    /**
     * 启用数据推送的模块列表
     */
    List<SpringCloudStreamModuleEnum> enableReceiveModules;

    @PostConstruct
    void checkInit() {
        if (enableSendModules == null) {
            enableSendModules = new ArrayList<>();
        }
        if (enableReceiveModules == null) {
            enableReceiveModules = new ArrayList<>();
        }
        checkProperty(SpringCloudStreamProperties::getEnableSendCloudData, "是否启用推送数据");
        checkProperty(SpringCloudStreamProperties::getEnableReceiveCloudData, "是否启用接收数据");
    }

}
