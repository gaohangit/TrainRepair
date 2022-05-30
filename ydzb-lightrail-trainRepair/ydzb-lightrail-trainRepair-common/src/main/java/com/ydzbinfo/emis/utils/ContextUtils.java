package com.ydzbinfo.emis.utils;

import com.jxdinfo.hussar.common.organutil.OrganUtil;
import com.ydzbinfo.emis.configs.DeployLevelEnum;
import com.ydzbinfo.emis.configs.TrainRepairProperties;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 项目上下文工具
 *
 * @author 张天可
 */
@Component
public class ContextUtils {

    protected static final Logger logger = LoggerFactory.getLogger(ContextUtils.class);

    private static boolean center;

    private static boolean deployLevelInitOver = false;

    private static String unitName = null;

    private static TrainRepairProperties trainRepairProperties;

    /**
     * 判断是否为段级部署
     */
    public static boolean isCenter() {
        if (!deployLevelInitOver) {
            throw new RuntimeException("未在yml配置文件配置部署等级，无法通过此方法判断是否为段级服务");
        }
        return center;
    }

    /**
     * 获取所编码
     */
    public static String getUnitCode() {
        if (trainRepairProperties.getUnitCode() == null) {
            logger.warn("未在yml配置文件配置运用所编码");
        }
        return trainRepairProperties.getUnitCode();
    }

    /**
     * 获取段编码
     */
    public static String getDepotCode() {
        return CacheUtil.getDataUseThreadCache("ContextUtils.getDepotCode", () -> UserUtil.getUserInfo().getDepot().getCode());
    }

    /**
     * 获取段id
     */
    public static String getDepotId() {
        return CacheUtil.getDataUseThreadCache("ContextUtils.getDepotId", () -> UserUtil.getUserInfo().getDepot().getId());
    }

    private static boolean unitNameInitOver = false;

    /**
     * 获取所名称
     */
    public static String getUnitName() {
        String unitCode = getUnitCode();
        String depotCode = getDepotCode();
        if (!unitNameInitOver && unitCode != null) {
            List<SysOrgan> list = OrganUtil.getOranListByParent(depotCode, "08");
            SysOrgan unit = CommonUtils.find(list, v -> v.getOrganCode().equals(unitCode));
            if (unit != null) {
                unitName = unit.getOrganName();
            } else {
                logger.error("未找到配置段下的配置的运用所，当前段编码：" + depotCode + "，配置运用所编码(" + trainRepairProperties.getPropertyPath(TrainRepairProperties::getUnitCode) + ")：" + unitCode);
            }
            unitNameInitOver = true;
        }
        return unitName;
    }

    ContextUtils(TrainRepairProperties trainRepairProperties) {
        initDeployLevel(trainRepairProperties);
        ContextUtils.trainRepairProperties = trainRepairProperties;
    }


    private void initDeployLevel(TrainRepairProperties trainRepairProperties) {
        DeployLevelEnum deployLevelEnum = trainRepairProperties.getDeployLevel();
        switch (deployLevelEnum) {
            case DEPARTMENT:
                ContextUtils.center = false;
                break;
            case CENTER:
                ContextUtils.center = true;
                break;
        }
        ContextUtils.deployLevelInitOver = true;
        logger.info("当前部署等级：" + deployLevelEnum.getLabel());
    }

}
