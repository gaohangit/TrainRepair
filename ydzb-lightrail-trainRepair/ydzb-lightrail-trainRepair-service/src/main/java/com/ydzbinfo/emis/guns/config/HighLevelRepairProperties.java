package com.ydzbinfo.emis.guns.config;

import com.ydzbinfo.emis.utils.TrainRepairPropertiesParent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 高级修服务属性配置
 *
 * @author 高晗
 * @description
 * @createDate 2021/12/20 15:50
 * @modifier 张天可 2021/12/23 09:10
 **/
@EqualsAndHashCode(callSuper = false)
@Component
@Data
@ConfigurationProperties("high-level-repair-properties")
public class HighLevelRepairProperties extends TrainRepairPropertiesParent<HighLevelRepairProperties> {

    private String wsdlUrl;

    private String xmlnsUrl;

    private String methodName;

    @PostConstruct
    void checkInit() {
        String moduleName = "高级修服务：";
        checkProperty(HighLevelRepairProperties::getWsdlUrl, moduleName + "wsdl地址");
        checkProperty(HighLevelRepairProperties::getXmlnsUrl, moduleName + "xmlns地址");
        checkProperty(HighLevelRepairProperties::getMethodName, moduleName + "方法名");
    }
}
