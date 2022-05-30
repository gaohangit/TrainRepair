package com.ydzbinfo.emis.utils;

import lombok.Getter;

@Getter
public enum ServiceNameEnum {
    UserService("userservice", "用户信息服务"),
    SystemService("systemservice", "框架基础服务"),
    // ResumeService("resumeservice", "履历服务"),
    RepairTaskService("repairtaskservice", "检修计划服务"),
    // FaultService("faultservice", "故障服务"),
    RepairFaultService("repairfaultservice", "中台故障管理服务"),
    // WorkprocessService("workprocessservice", "中台作业管理服务"),
    // YardService("yardservice", "中台站场服务"),
    // OperateService("operateservice", "中台开行服务"),
    MessageService("messageservice", "消息通知服务"),
    RepairItemService("repairitemservice", "检修计划"),
    ResumeMidGroundService("resumemidgroundservice", "履历中台服务"),
    DeviceDataService("devicedataservice","设备接口数据服务"),
    configurationService("configurationservice", "构型服务");

    private final String id;

    private final String name;

    ServiceNameEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
