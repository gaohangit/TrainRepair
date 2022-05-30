package com.ydzbinfo.emis.trainRepair.common.querymodel;

/**
 * 计划包基本信息实体接口
 *
 * @author 张天可
 * @since 2021/11/2
 */
public interface ITaskPacketEntityBase {
    String getTaskId();

    void setTaskId(String taskId);

    String getPacketCode();

    void setPacketCode(String packetCode);

    String getPacketName();

    void setPacketName(String packetName);

    String getDayPlanId();

    void setDayPlanId(String dayPlanId);

    String getTrainsetId();

    void setTrainsetId(String trainsetId);

    String getTrainsetName();

    void setTrainsetName(String trainsetName);

    String getPacketTypeCode();

    void setPacketTypeCode(String packetTypeCode);

    String getRepairDeptCode();

    void setRepairDeptCode(String repairDeptCode);

    String getRepairDeptName();

    void setRepairDeptName(String repairDeptName);

    String getDepotCode();

    void setDepotCode(String depotCode);

}
