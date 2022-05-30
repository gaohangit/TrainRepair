package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import lombok.Data;

/**
 * Description: 作业包集合
 * Author: wuyuechang
 * Create Date Time: 2021/4/25 9:53
 * Update Date Time: 2021/4/25 9:53
 *
 * @see
 */
@Data
public class PacketData {

    /**
     * 作业类型CODE
     */
    private String workTypeCode;

    /**
     * 作业类型名称
     */
    private String workTypeName;

    /**
     * 作业任务CODE
     */
    private String workTaskCode;

    /**
     * 作业任务名称
     */
    private String workTaskName;

    /**
     * 作业任务显示名称
     */
    private String workTaskDisplayName;

    /**
     * 派工状态Code
     */
    private String allotStateCode;

    /**
     * 派工状态名称
     */
    private String allotStateName;

    /**
     * 派工模式类型
     */
    private String workModeTypeCode;

    /**
     * 派工模式类型名称
     */
    private String workModeTypeName;

    /**
     * 派工包
     */
    private XzyMTaskallotpacket packet;

    /**
     * 是否新作业包 1—是，0—否
     */
    private String newPacket = "0";

    /**
     * 显示模式
     */
    private String showMode;
}
