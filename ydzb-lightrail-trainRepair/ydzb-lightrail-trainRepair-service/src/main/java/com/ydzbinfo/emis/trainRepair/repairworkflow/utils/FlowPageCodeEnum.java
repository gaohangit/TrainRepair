package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairworkflow.config.RepairWorkflowProperties;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowTypeService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.PacketInfoUtils.PacketInfoGetter;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description 流程处理页面code
 * @createDate 2021/6/4 9:02
 **/
public enum FlowPageCodeEnum {
    ONE("ONE", "一级修", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.REPAIR_ONE,
        BasicFlowTypeEnum.REPAIR_ONE_AND_TWO
    }, null, null),
    TWO("TWO", "二级修", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.REPAIR_TWO,
        BasicFlowTypeEnum.REPAIR_ONE_AND_TWO
    }, null, null),
    TRAIN_MONITOR(
        "TRAIN_MONITOR",
        "车组监控",
        null,
        CacheUtil.getCachedDataGetter(FlowPageCodeEnum::getTrainMonitorMixins),
        null),
    TEMPORARY("TEMPORARY", "临时作业", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.TEMPORARY
    }, null, null),
    HOSTLING("HOSTLING", "整备作业", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.HOSTLING
    }, null, FlowPageCodeEnum::hostLingPageMatcher),
    KEY_WORK_HANDLE("KEY_WORK_HANDLE", "关键作业处理(移动端)", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.PLANLESS_KEY
    }, null, FlowPageCodeEnum::errorMatcher),
    KEY_WORK_INPUT("KEY_WORK_INPUT", "关键作业录入(移动端)", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.PLANLESS_KEY
    }, null, FlowPageCodeEnum::errorMatcher),
    FAULT_TO_KEY_WORK("FAULT_TO_KEY_WORK", "故障转关键作业(移动端)", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.PLANLESS_KEY
    }, null, FlowPageCodeEnum::errorMatcher),
    PLANLESS_KEY("PLANLESS_KEY", "关键作业", new BasicFlowTypeEnum[]{
        BasicFlowTypeEnum.PLANLESS_KEY
    }, null, FlowPageCodeEnum::errorMatcher),
    ;

    @Getter
    private final String value;
    @Getter
    private final String label;
    private final BasicFlowTypeEnum[] basicFlowTypeEnums;
    private final Supplier<FlowPageCodeEnum[]> mixinsGetter;
    private final Supplier<BasicFlowTypeEnum[]> allBasicFlowTypeEnumsGetter;
    private final Predicate<List<ZtTaskPacketEntity>> typeMatcher;


    FlowPageCodeEnum(String value, String label, BasicFlowTypeEnum[] basicFlowTypeEnums, Supplier<FlowPageCodeEnum[]> mixinsGetter, Predicate<List<ZtTaskPacketEntity>> typeMatcher) {
        this.value = value;
        this.label = label;
        this.basicFlowTypeEnums = basicFlowTypeEnums;
        this.mixinsGetter = mixinsGetter;
        this.typeMatcher = typeMatcher;
        this.allBasicFlowTypeEnumsGetter = CacheUtil.getCachedDataGetter(() -> {
            BasicFlowTypeEnum[] allFlowTypes = getBasicFlowTypeEnums();
            for (FlowPageCodeEnum mixin : getMixins()) {
                allFlowTypes = ArrayUtils.addAll(allFlowTypes, mixin.getBasicFlowTypeEnums());
            }
            return allFlowTypes;
        });
    }

    private static FlowPageCodeEnum[] getTrainMonitorMixins() {
        return new FlowPageCodeEnum[]{
            FlowPageCodeEnum.ONE,
            FlowPageCodeEnum.TWO,
            FlowPageCodeEnum.HOSTLING
        };
    }

    private BasicFlowTypeEnum[] getBasicFlowTypeEnums() {
        if (basicFlowTypeEnums == null) {
            return new BasicFlowTypeEnum[0];
        } else {
            return basicFlowTypeEnums;
        }
    }

    private FlowPageCodeEnum[] getMixins() {
        if (mixinsGetter == null) {
            return new FlowPageCodeEnum[0];
        } else {
            return mixinsGetter.get();
        }
    }

    @Component
    public static class InjectComponents {
        static IFlowTypeService flowTypeService;
        static RepairWorkflowProperties repairWorkflowProperties;

        @Autowired
        public void setFlowTypeService(IFlowTypeService flowTypeService) {
            InjectComponents.flowTypeService = flowTypeService;
        }

        @Autowired
        public void setRepairWorkflowProperties(RepairWorkflowProperties repairWorkflowProperties) {
            InjectComponents.repairWorkflowProperties = repairWorkflowProperties;
        }
    }

    private static boolean errorMatcher(List<ZtTaskPacketEntity> taskPackets) {
        throw RestRequestException.serviceInnerFatalFail("此页面类型无法匹配包");
    }

    private static boolean hostLingPageMatcher(List<ZtTaskPacketEntity> taskPackets) {
        String unitCode = ContextUtils.getUnitCode();
        boolean hostLing = FlowDatabaseConfigUtil.isUseDefaultHostlingFlowType();
        if (hostLing) {
            return BasicFlowTypeEnum.HOSTLING.match(taskPackets);
        } else {
            // 过滤出可能的整备作业包
            List<ZtTaskPacketEntity> hostlingFlowTypeTaskPackets = taskPackets.stream().filter(BasicFlowTypeEnum.HOSTLING::judgeTask).collect(Collectors.toList());
            //获取到流程配置按code分组
            List<FlowTypeInfoWithPackets> flowTypeInfoWithPacketList = CacheUtil.getDataUseThreadCache(
                "flowTypeService.getFlowTypeAndPacket_" + unitCode,
                () -> InjectComponents.flowTypeService.getFlowTypeAndPacket(unitCode)
            );

            FlowTypeInfoWithPackets hostlingFlowType = CommonUtils.find(flowTypeInfoWithPacketList, v -> v.getCode().equals(InjectComponents.repairWorkflowProperties.getHostlingFlowTypeCode()));
            if (hostlingFlowType != null) {
                FlowTypeConfigTypeEnum configTypeEnum = EnumUtils.findEnum(FlowTypeConfigTypeEnum.class, FlowTypeConfigTypeEnum::getValue, hostlingFlowType.getConfigType());
                return configTypeEnum.isFlowTypeMatchedByTaskPackets(hostlingFlowType, hostlingFlowTypeTaskPackets);
            } else {
                return false;
            }

        }
    }

    /**
     * 判断是否包含基本类型
     */
    public boolean containsFlowType(FlowType flowType) {
        BasicFlowTypeEnum[] allFlowTypes = allBasicFlowTypeEnumsGetter.get();
        return Arrays.stream(allFlowTypes).anyMatch(v -> v.getValue().equals(flowType.getCode()));
    }

    /**
     * 判断是否包含额外类型
     */
    public boolean containsExtraFlowType(ExtraFlowType extraFlowType) {
        String pageType = extraFlowType.getPageType();
        if (StringUtils.isNotBlank(pageType)) {
            if (!this.getValue().equals(pageType) && Arrays.stream(getMixins()).noneMatch(v -> v.getValue().equals(pageType))) {
                return false;
            }
        }
        BasicFlowTypeEnum[] allFlowTypes = allBasicFlowTypeEnumsGetter.get();
        return Arrays.stream(allFlowTypes).anyMatch(v -> v.getValue().equals(extraFlowType.getParentFlowTypeCode()));
    }

    public boolean match(List<ZtTaskPacketEntity> taskList) {
        if (typeMatcher != null) {
            return typeMatcher.test(taskList);
        } else {
            BasicFlowTypeEnum[] allFlowTypes = allBasicFlowTypeEnumsGetter.get();
            return Arrays.stream(allFlowTypes).anyMatch(v -> v.match(taskList));
        }
    }
}
