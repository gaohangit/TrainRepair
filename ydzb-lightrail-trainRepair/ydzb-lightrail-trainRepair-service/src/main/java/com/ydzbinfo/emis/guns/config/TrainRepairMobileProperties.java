package com.ydzbinfo.emis.guns.config;

import com.ydzbinfo.emis.utils.TrainRepairPropertiesParent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 项目移动端配置
 *
 * @author 张天可
 * @since 2021/11/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties("ydzb.mobile")
public class TrainRepairMobileProperties extends TrainRepairPropertiesParent<TrainRepairMobileProperties> {

    /**
     * 手持机加密key
     */
    String deskey;

    @PostConstruct
    void checkInit() {
        checkProperty(TrainRepairMobileProperties::getDeskey, "手持机加密key");
    }

}
