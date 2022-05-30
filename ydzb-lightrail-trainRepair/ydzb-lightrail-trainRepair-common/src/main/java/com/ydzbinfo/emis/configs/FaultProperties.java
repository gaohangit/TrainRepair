package com.ydzbinfo.emis.configs;

import com.ydzbinfo.emis.utils.TrainRepairPropertiesParent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 故障
 * @author 高晗
 * @since 2022/04/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties("ydzb.fault")
public class FaultProperties extends TrainRepairPropertiesParent<FaultProperties> {

    /**
     * 故障文件存储路径
     */
    String fileAddress;

}
