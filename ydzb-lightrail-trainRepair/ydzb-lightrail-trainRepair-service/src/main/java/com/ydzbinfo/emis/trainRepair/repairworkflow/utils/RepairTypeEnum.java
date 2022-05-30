package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.constant.PacketTypeEnum;
import lombok.Getter;

/**
 * 检修类型
 */
@Getter
public enum RepairTypeEnum {
    PersonRepair("1", "人检", PacketTypeEnum.NORMAL),
    MachineRepair("2", "机检", PacketTypeEnum.MACHINE_CHECK),
    MachineWithPersonRepair("3", "人工辅助机检", PacketTypeEnum.MACHINE_CHECK_WITH_ASSISTANT);

    String value;
    String label;
    PacketTypeEnum packetType;

    RepairTypeEnum(String value, String label, PacketTypeEnum packetType) {
        this.value = value;
        this.label = label;
        this.packetType = packetType;
    }
}
