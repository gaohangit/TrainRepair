package com.ydzbinfo.emis.configs;

import com.ydzbinfo.emis.utils.TrainRepairPropertiesParent;
import com.ydzbinfo.hussar.config.properties.ServiceInfo;
import com.ydzbinfo.hussar.config.properties.ServicePathProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 项目基本配置
 *
 * @author 张天可
 * @since 2021/11/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties("ydzb")
public class TrainRepairProperties extends TrainRepairPropertiesParent<TrainRepairProperties> {

    /**
     * 检修班组类型编码
     */
    String workTeamTypeCode = "BD_06";

    /**
     * 段编码
     */
    String unitCode;

    /**
     * 部署等级
     */
    DeployLevelEnum deployLevel;

    /**
     * 基本服务路径列表，用于简化配置
     */
    List<ServiceInfo> baseServiceList;

    /**
     * 服务路径列表
     */
    @Autowired
    ServicePathProperties servicePathProperties;

    /**
     * 记录单文件路径，开发用
     */
    String devBillFilePath;

    @PostConstruct
    void checkInit() {
        if (baseServiceList != null && baseServiceList.size() > 0) {
            if (servicePathProperties.getList() == null) {
                servicePathProperties.setList(new ArrayList<>());
            }
            for (ServiceInfo serviceInfo : baseServiceList) {
                if (servicePathProperties.getList().stream().noneMatch(v -> Objects.equals(v.getId(), serviceInfo.getId()))) {
                    servicePathProperties.getList().add(0, serviceInfo);
                }
            }
        }
        checkProperty(TrainRepairProperties::getDeployLevel, "部署等级");
        // checkProperty(TrainRepairProperties::getWorkTeamTypeCode, "检修班组类型编码");
    }

}
