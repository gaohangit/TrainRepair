package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.PacketInfoUtils.PacketInfoGetter;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.PacketInfoUtils.*;

/**
 * 额外流程类型的配置类型枚举
 *
 * @author 张天可
 */
@Getter
@AllArgsConstructor
public enum FlowTypeConfigTypeEnum {
    PACKET_NARROW(
        "PACKET_NARROW",
        "收缩类型",
        (PacketInfoGetter packetInfo, FlowTypeInfoWithPackets flowTypeInfo, TrainsetBaseInfo trainsetInfo) -> {
            boolean extendsRepairOneAndTwo = BasicFlowTypeEnum.REPAIR_ONE_AND_TWO.is(flowTypeInfo.getParentFlowTypeCode());
            // 如果基本类型是一二级修，并且配置的包中不存在一级修，则意味着匹配所有的一级修包
            if (extendsRepairOneAndTwo) {
                Set<String> packetCodesSuitCurrentTrainset = getPacketCodesInCurrentTrainset(flowTypeInfo, trainsetInfo);
                if (packetCodesSuitCurrentTrainset.stream().noneMatch(v -> isOne(PacketInfoGetter.fromPacket(getPacketInfo(v))))
                    && isOne(packetInfo)) {
                    return true;
                }
            }
            return FlowTypeConfigTypeEnum.isPacketMatchedByFlowType(packetInfo, flowTypeInfo, trainsetInfo);
        },
        (params) -> {
            FlowTypeInfoWithPackets flowTypeInfo = params.getFlowTypeInfo();
            PacketListInfoGetter packetListInfo = params.getPacketListInfo();
            TrainsetBaseInfo trainsetInfo = params.getTrainsetInfo();
            boolean extendsRepairOneAndTwo = BasicFlowTypeEnum.REPAIR_ONE_AND_TWO.is(flowTypeInfo.getParentFlowTypeCode());
            Set<String> packetCodesSuitCurrentTrainset = getPacketCodesInCurrentTrainset(flowTypeInfo, trainsetInfo);

            if (extendsRepairOneAndTwo) {
                // 如果配置的包中不存在一级修，则过滤掉下发计划中的一级修包后进行匹配
                if (packetCodesSuitCurrentTrainset.stream().noneMatch(v -> isOne(PacketInfoGetter.fromPacket(getPacketInfo(v))))) {
                    return packetListInfo.getPackets().stream()
                        .filter(packetInfo -> !isOne(packetInfo))
                        .allMatch(v -> packetCodesSuitCurrentTrainset.contains(v.getPacketCode()));
                }
            }
            return packetListInfo.getPackets().stream()
                .allMatch(v -> packetCodesSuitCurrentTrainset.contains(v.getPacketCode()));
        }
    ),
    PACKET_INDEPENDENT(
        "PACKET_INDEPENDENT",
        "独立类型",
        FlowTypeConfigTypeEnum::isPacketMatchedByFlowType,
        (params) -> {
            FlowTypeInfoWithPackets flowTypeInfo = params.getFlowTypeInfo();
            PacketListInfoGetter packetListInfo = params.getPacketListInfo();
            TrainsetBaseInfo trainsetInfo = params.getTrainsetInfo();
            Set<String> packetCodesSuitCurrentTrainset = getPacketCodesInCurrentTrainset(flowTypeInfo, trainsetInfo);
            return packetListInfo.getPackets().stream().anyMatch(packetInfo -> packetCodesSuitCurrentTrainset.contains(packetInfo.getPacketCode()));
        }
    ),
    PACKET_EXIST(
        "PACKET_EXIST",
        "存在类型",
        FlowTypeConfigTypeEnum::isPacketMatchedByFlowType,
        (params) -> {
            FlowTypeInfoWithPackets flowTypeInfo = params.getFlowTypeInfo();
            PacketListInfoGetter packetListInfo = params.getPacketListInfo();
            TrainsetBaseInfo trainsetInfo = params.getTrainsetInfo();
            Set<String> packetCodesSuitCurrentTrainset = getPacketCodesInCurrentTrainset(flowTypeInfo, trainsetInfo);
            return packetListInfo.getPackets().stream().anyMatch(packetInfo -> packetCodesSuitCurrentTrainset.contains(packetInfo.getPacketCode()));
        }
    );
    private final String value;
    private final String description;
    private final TriPredicate<PacketInfoGetter, FlowTypeInfoWithPackets, TrainsetBaseInfo> judgePacketInfo;
    private final Predicate<JudgePacketListInfoParamInfo> judgePacketListInfo;

    @Data
    @AllArgsConstructor
    static class JudgePacketListInfoParamInfo {
        private FlowTypeInfoWithPackets flowTypeInfo;
        private PacketListInfoGetter packetListInfo;
        private TrainsetBaseInfo trainsetInfo;
    }

    private static boolean isPacketMatchedByFlowType(PacketInfoGetter packetInfo, FlowTypeInfoWithPackets extraFlowType, TrainsetBaseInfo trainsetInfo) {
        return getPacketCodesInCurrentTrainset(extraFlowType, trainsetInfo).contains(packetInfo.getPacketCode());

    }

    private static Set<String> getPacketCodesInCurrentTrainset(FlowTypeInfoWithPackets flowTypeInfo, TrainsetBaseInfo trainsetInfo) {
        return CacheUtil.getDataUseThreadCache(
            "FlowTypeConfigTypeEnum.getPacketCodesInCurrentTrainset_" + flowTypeInfo.getCode() + "_" + trainsetInfo.getTrainsetid(),
            () -> flowTypeInfo.getPacketCodes().stream().filter(
                packetCode -> PacketInfoUtils.isPacketSuitTrainset(getPacketInfo(packetCode), trainsetInfo)
            ).collect(Collectors.toSet())
        );
    }

    /**
     * 判断是否匹配到额外流程类型，根据类型配置和计划信息
     *
     * @param extraFlowType                          额外类型配置
     * @param taskPacketListFilteredByParentFlowType 根据父流程类型(基本流程类型)条件过滤过的计划信息
     */
    public boolean isFlowTypeMatchedByTaskPackets(FlowTypeInfoWithPackets extraFlowType, List<ZtTaskPacketEntity> taskPacketListFilteredByParentFlowType) {
        if (taskPacketListFilteredByParentFlowType.size() == 0) {
            return false;
        } else {
            extraFlowType.setPacketCodes(extraFlowType.getPacketCodes().stream().filter(packetCode -> getPacketListMap().get(packetCode) != null).collect(Collectors.toSet()));
            return judgePacketListInfo.test(
                new JudgePacketListInfoParamInfo(
                    getFilteredExtraFlowType(extraFlowType),
                    new PacketListInfoGetter(
                        CacheUtil.getCachedDataGetter(() -> {
                            // 转换为通用类
                            return taskPacketListFilteredByParentFlowType.stream().map(PacketInfoGetter::fromTaskPacket).collect(Collectors.toList());
                        })
                    ),
                    RemoteServiceCachedDataUtil.getTrainsetBaseinfoByID(taskPacketListFilteredByParentFlowType.get(0).getTrainsetId())
                )
            );
        }
    }

    private FlowTypeInfoWithPackets getFilteredExtraFlowType(FlowTypeInfoWithPackets extraFlowType){
        FlowTypeInfoWithPackets filteredExtraFlowType = new FlowTypeInfoWithPackets();
        BeanUtils.copyProperties(extraFlowType, filteredExtraFlowType);
        filteredExtraFlowType.setPacketCodes(extraFlowType.getPacketCodes().stream().filter(
            packetCode -> getPacketListMap().get(packetCode) != null
        ).collect(Collectors.toSet()));
        return filteredExtraFlowType;
    }

    public boolean isTaskPacketMatchedByFlowType(ZtTaskPacketEntity taskPacket, FlowTypeInfoWithPackets extraFlowType) {
        return judgePacketInfo.test(
            PacketInfoGetter.fromTaskPacket(taskPacket),
            getFilteredExtraFlowType(extraFlowType),
            RemoteServiceCachedDataUtil.getTrainsetBaseinfoByID(taskPacket.getTrainsetId())
        );
    }

    private static Map<String, PacketInfo> getPacketListMap() {
        return CacheUtil.getDataUseThreadCache("FlowTypeConfigTypeEnum.getPacketListMap", () -> {
            List<PacketInfo> packetList = RemoteServiceCachedDataUtil.getPacketList();
            return CommonUtils.collectionToMap(packetList, PacketInfo::getPacketCode);
        });
    }

    private static PacketInfo getPacketInfo(String packetCode) {
        PacketInfo packetInfo = getPacketListMap().get(packetCode);
        if (packetInfo == null) {
            throw RestRequestException.fatalFail("作业包不存在，请检查流程类型配置！");
        }
        return packetInfo;
    }

}
