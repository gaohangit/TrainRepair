package com.ydzbinfo.emis.guns.config;

import com.ydzbinfo.emis.utils.TrainRepairPropertiesParent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 复核任务服务属性配置
 *
 * @author gaohan
 * @description
 * @createDate 2021/3/23 17:42
 * @modifier 张天可 2021年12月23日10:11:25
 **/
@EqualsAndHashCode(callSuper = false)
@Component
@Data
@ConfigurationProperties("recheck-task-properties")
public class RecheckTaskProperties extends TrainRepairPropertiesParent<RecheckTaskProperties> {
    /**
     * 获取token认证地址
     */
    private String getTokenUrl;
    /**
     * 刷新token认证地址
     */
    private String refreshTokenUrl;
    /**
     * 公钥
     */
    private String xmlPublicKey;
    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;

    /**
     * 查询LY超限对象列表地址
     */
    private String lyGetOverRunRecordUrl;

    /**
     * 查询SJ超限对象列表地址
     */
    private String sjGetOverRunRecordUrl;

    /**
     * 获取镟轮设备数据地址
     */
    private String wheelGetDeviceRecordUrl;

    /**
     * 获取空心轴探伤设备数据地址
     */
    private String axleGetDeviceRecordUrl;

    /**
     * 获取LU探伤设备数据地址
     */
    private String luGetDeviceRecordUrl;

    @PostConstruct
    void checkInit() {
        String moduleName = "复核任务服务:";
        checkProperty(RecheckTaskProperties::getGetTokenUrl, moduleName + "获取token认证地址");
        checkProperty(RecheckTaskProperties::getRefreshTokenUrl, moduleName + "刷新token认证地址");
        checkProperty(RecheckTaskProperties::getXmlPublicKey, moduleName + "公钥");
        checkProperty(RecheckTaskProperties::getAccount, moduleName + "账号");
        checkProperty(RecheckTaskProperties::getPassword, moduleName + "密码");
        checkProperty(RecheckTaskProperties::getLyGetOverRunRecordUrl, moduleName + "查询LY超限对象列表地址");
        checkProperty(RecheckTaskProperties::getSjGetOverRunRecordUrl, moduleName + "查询SJ超限对象列表地址");
        checkProperty(RecheckTaskProperties::getWheelGetDeviceRecordUrl, moduleName + "获取镟轮设备数据地址");
        checkProperty(RecheckTaskProperties::getAxleGetDeviceRecordUrl, moduleName + "获取空心轴探伤设备数据地址");
        checkProperty(RecheckTaskProperties::getLuGetDeviceRecordUrl, moduleName + "获取LU探伤设备数据地址");
    }
}
