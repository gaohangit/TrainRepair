package com.ydzbinfo.emis.utils;

import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.constant.PacketTypeEnum;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 包信息判断工具类
 *
 * @author 张天可
 * @since 2022/3/31
 */
public class PacketInfoUtils {

    @AllArgsConstructor
    public static class PacketInfoGetter {
        private final Supplier<String> packetCodeGetter;
        private final Supplier<String> packetTypeGetter;
        private final Supplier<String> repairCodeGetter;


        public String getPacketCode() {
            return packetCodeGetter.get();
        }

        public String getPacketType() {
            return packetTypeGetter.get();
        }

        public String getRepairCode() {
            return repairCodeGetter.get();
        }

        public static PacketInfoGetter fromPacket(PacketInfo packetInfo) {
            return new PacketInfoGetter(packetInfo::getPacketCode, packetInfo::getPacketType, packetInfo::getRepairCode);
        }

        public static PacketInfoGetter fromTaskPacket(ZtTaskPacketEntity taskPacketEntity) {
            return new PacketInfoGetter(taskPacketEntity::getPacketCode, taskPacketEntity::getPacketTypeCode, taskPacketEntity::getTaskRepairCode);
        }
    }

    /**
     * 是否是一级修包
     */
    public static boolean isOne(PacketInfoGetter packetInfo) {
        String repairCode = packetInfo.getRepairCode();
        return Objects.equals("1", repairCode);
    }

    /**
     * 是否是人检一级修包
     */
    public static boolean isNormalOne(PacketInfoGetter packetInfo) {
        return isNormal(packetInfo) && isOne(packetInfo);
    }

    /**
     * 是否是普通包
     */
    public static boolean isNormal(PacketInfoGetter packetInfo) {
        return PacketTypeEnum.NORMAL.is(packetInfo.getPacketType());
    }

    /**
     * 是否是二级修包
     */
    public static boolean isTwo(PacketInfoGetter packetInfo) {
        String repairCode = packetInfo.getRepairCode();
        return Objects.equals("2", repairCode);
    }

    /**
     * 是否是机检包
     */
    public static boolean isMachine(PacketInfoGetter packetInfo) {
        return PacketTypeEnum.MACHINE_CHECK.is(packetInfo.getPacketType());
    }

    /**
     * 是否是临时任务包
     */
    public static boolean isTemporary(PacketInfoGetter packetInfo) {
        return PacketTypeEnum.TEMPORARY.is(packetInfo.getPacketType());
    }

    public static boolean isPacketSuitTrainset(PacketInfo packetInfo, TrainsetBaseInfo trainsetInfo) {
        return isPacketSuitTrainset(packetInfo, trainsetInfo.getTraintype(), trainsetInfo.getTraintempid());
    }

    public static boolean isPacketSuitTrainset(PacketInfo packetInfo, String trainsetType, String batch) {
        String suitModel = packetInfo.getSuitModel();
        if (suitModel.equals("ALL")) {
            return true;
        } else {
            // 如果使用新项目，意味着使用"批次"而不是"阶段"，也就意味着可以进行批次的匹配
            if (ConfigUtil.isUseNewItem()) {
                String suitBatch = packetInfo.getSuitBatch();
                if (StringUtils.isBlank(suitBatch)) {
                    return suitModel.equals(trainsetType);
                } else {
                    return suitBatch.equals(batch);
                }
            } else {
                return suitModel.equals(trainsetType);
            }
        }
    }

}
