package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.constant.PacketTypeEnum;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.PacketInfoUtils;
import com.ydzbinfo.emis.utils.PacketInfoUtils.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.PacketInfoUtils.*;

/**
 * 基础流程类型枚举类
 * 包含基本信息和基本匹配逻辑
 *
 * @author 张天可
 **/
@Getter
@AllArgsConstructor
public enum BasicFlowTypeEnum {
    REPAIR_ONE(
        "REPAIR_ONE",
        "一级修",
        PacketInfoUtils::isOne,
        (packetListInfo, judgePacketFn) -> {
            List<PacketInfoGetter> packets = packetListInfo.getPackets();
            // 如果没有二级修包并且存在普通一级修包，则启动一级修流程
            if (packets.stream().anyMatch(PacketInfoUtils::isNormalOne) && packets.stream().noneMatch(PacketInfoUtils::isTwo)) {
                return true;
            } else {
                // 或者存在机检一级修包，也启动一级修流程
                return packets.stream().anyMatch(packet -> isOne(packet) && isMachine(packet));
            }
        }
    ),
    REPAIR_TWO(
        "REPAIR_TWO",
        "二级修",
        PacketInfoUtils::isTwo,
        (packetListInfo, judgePacketFn) -> {
            List<PacketInfoGetter> packets = packetListInfo.getPackets();
            return packets.stream().anyMatch(PacketInfoUtils::isTwo) && packets.stream().noneMatch(PacketInfoUtils::isNormalOne);
        }
    ),
    REPAIR_ONE_AND_TWO(
        "REPAIR_ONE_AND_TWO",
        "一二级修",
        packetInfo -> isOne(packetInfo) || isTwo(packetInfo),
        (packetListInfo, judgePacketFn) -> {
            List<PacketInfoGetter> packets = packetListInfo.getPackets();
            return packets.stream().anyMatch(PacketInfoUtils::isNormalOne) && packets.stream().anyMatch(PacketInfoUtils::isTwo);
        }
    ),
    HOSTLING(
        "HOSTLING",
        "整备作业",
        (packetInfo) -> {
            // 只有在配置了不使用默认的整备作业匹配逻辑时，才会匹配具体的包
            if (!CacheUtil.getDataUseThreadCache("FlowDatabaseConfigUtil.isUseDefaultHostlingFlowType", FlowDatabaseConfigUtil::isUseDefaultHostlingFlowType)) {
                return BasicFlowTypeEnum.isOthers(packetInfo);
            } else {
                // 此处相当于禁用子流程类型
                return false;
            }
        },
        (packetListInfo, judgePacketFn) -> {
            List<PacketInfoGetter> packets = packetListInfo.getPackets();
            // 整备作业通常会合并到人检一级修中，所以其存在时，不启动整备作业；而二级修往往之后会进行人检一级修作业，不必要在二级修时启动整备作业
            return packets.stream().noneMatch(PacketInfoUtils::isNormal);
        }
    ),
    TEMPORARY(
        "TEMPORARY",
        "临时作业",
        PacketInfoUtils::isTemporary,
        (packetListInfo, judgePacketFn) -> packetListInfo.getPackets().stream().anyMatch(judgePacketFn)
    ),
    THE_OTHERS(
        "THE_OTHERS",
        "其它",
        BasicFlowTypeEnum::isOthers,
        (packetListInfo, judgePacketFn) -> packetListInfo.getPackets().stream().anyMatch(judgePacketFn)
    ),
    PLANLESS_KEY(
        "PLANLESS_KEY",
        "关键作业",
        v -> {
            throw new RuntimeException("关键作业不支持匹配包");
        },
        (v, j) -> {
            throw new RuntimeException("关键作业不支持匹配包");
        }
    );

    private final String value;
    private final String label;
    private final Predicate<PacketInfoUtils.PacketInfoGetter> judgePacketInfo;
    private final BiPredicate<PacketListInfoGetter, Predicate<PacketInfoGetter>> judgePacketListInfo;

    /**
     * 是否是管理范围之外的包
     */
    public static boolean isOthers(PacketInfoGetter packetInfo) {
        String packetType = packetInfo.getPacketType();
        return !PacketTypeEnum.NORMAL.is(packetType)
            && !PacketTypeEnum.TEMPORARY.is(packetType)
            && !PacketTypeEnum.MACHINE_CHECK.is(packetType);
    }

    /**
     * 判断是否为当前基本流程类型所管理的包
     */
    public boolean judgePacket(PacketInfo v) {
        return judgePacketInfo.test(PacketInfoGetter.fromPacket(v));
    }

    /**
     * 判断是否为当前基本流程类型所管理的任务
     */
    public boolean judgeTask(ZtTaskPacketEntity v) {
        return judgePacketInfo.test(PacketInfoGetter.fromTaskPacket(v));
    }

    public boolean match(List<ZtTaskPacketEntity> taskList) {
        return judgePacketListInfo.test(
            new PacketListInfoGetter(
                CacheUtil.getCachedDataGetter(() -> {
                    // 转换为通用类
                    return taskList.stream().map(PacketInfoGetter::fromTaskPacket).collect(Collectors.toList());
                })
            ),
            judgePacketInfo
        );
    }

    public boolean is(String flowTypeCode) {
        return this.getValue().equals(flowTypeCode);
    }

    public static BasicFlowTypeEnum from(String flowTypeCode) {
        return EnumUtils.findEnum(BasicFlowTypeEnum.class, BasicFlowTypeEnum::getValue, flowTypeCode);
    }
}
