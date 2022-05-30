package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

import java.util.Set;

/**
 * @author gaohan
 * @description 流程类型和额外流程类型作业包
 * @createDate 2021/5/8 16:46
 **/
@Data
public class FlowTypeInfoWithPackets {
    /**
     * 类型标识
     */
    private String code;

    /**
     * 类型名称
     */
    private String name;
    /**
     * 父流程类型标识
     */
    private String parentFlowTypeCode;
    /**
     * 流程配置类型
     */
    private String configType;
    /**
     * 描述
     */
    private String comment;
    /**
     * 所编码
     */
    private String unitCode;
    /**
     * 作业包编码
     */
    private Set<String> packetCodes;
}
