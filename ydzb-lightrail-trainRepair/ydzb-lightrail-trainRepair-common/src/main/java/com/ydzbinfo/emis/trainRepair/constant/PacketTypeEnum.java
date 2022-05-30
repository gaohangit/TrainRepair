package com.ydzbinfo.emis.trainRepair.constant;

import com.ydzbinfo.emis.utils.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 作业包类型枚举类
 *
 * @author 张天可
 * @since 2022/1/11
 */
@Getter
@AllArgsConstructor
public enum PacketTypeEnum {
    NORMAL("1", "普通任务"),
    SIEVE("2", "滤网任务"),
    TEMPORARY("3", "临时任务"),
    // 临修任务("4","临修任务"), // (已废弃)
    FAULT("5", "故障任务"),
    TEAMWORK("6", "协同任务"),
    OTHERS("7", "其他任务"),
    POINT_CHECK("8", "重点检查"),
    PARTS_CHANGE("9", "部件更换任务"),
    TEDS_RECHECK("10", "TEDS复核任务"),
    RECHECK("12", "轮对受电弓复核任务"),
    PHM_FAULT("15", "PHM故障任务"),
    MACHINE_CHECK("16", "机检任务"),
    MACHINE_CHECK_WITH_ASSISTANT("17", "人工辅助机检任务"),
    PHM_PREVENTION("18", "PHM预测修任务"),// 根据PHM预测下发的任务
    ;
    private final String value;
    private final String description;

    public static PacketTypeEnum from(String packetType) {
        return EnumUtils.findEnum(PacketTypeEnum.class, PacketTypeEnum::getValue, packetType);
    }

    public boolean is(String packetTypeCode) {
        return this.getValue().equals(packetTypeCode);
    }
}
