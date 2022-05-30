package com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.NodeInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.NodeRoleConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowUtil;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节点额外信息类型枚举定义
 *
 * @author 张天可
 * @since 2021/6/28
 */
@Getter
public enum NodeExtraInfoTypeEnum {
    ROLE("ROLE", Role.class),
    EXCLUDE_ROLE("EXCLUDE_ROLE", ExcludeRole.class),
    // ONLY_DISPATCHING("ONLY_DISPATCHING", OnlyDispatching.class),
    ALLOW_SKIP("ALLOW_SKIP", AllowSkip.class),
    ALLOW_DISPOSE_AFTER_SKIP("ALLOW_DISPOSE_AFTER_SKIP", AllowDisposeAfterSkip.class),
    MIN_DISPOSE_NUM("MIN_DISPOSE_NUM", MinDisposeNum.class),
    RECOMMENDED_PIC_NUM("RECOMMENDED_PIC_NUM", RecommendedPicNum.class),
    DISPOSE_SUBFLOW("DISPOSE_SUBFLOW", DisposeSubflow.class),
    FUNCTION_TYPE("FUNCTION_TYPE", FunctionType.class),
    OVERTIME_WARNING("OVERTIME_WARNING", OvertimeWarning.class),
    MIN_INTERVAL_RESTRICT("MIN_INTERVAL_RESTRICT", MinIntervalRestrict.class),
    ;

    private final String name;
    private final INodeExtraInfoConvertor<?> convertor;

    NodeExtraInfoTypeEnum(String name, Class<? extends INodeExtraInfoConvertor<?>> convertorClass) {
        this.name = name;
        try {
            this.convertor = convertorClass.getDeclaredConstructor(NodeExtraInfoTypeEnum.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw RestRequestException.serviceInnerFatalFail("NodeExtraInfoTypeEnum初始化失败", e);
        }
    }

    // public List<NodeExtraInfo> filterByName(List<NodeExtraInfo> nodeExtraInfos) {
    //     return nodeExtraInfos.stream().filter(v -> v.getType().equals(name)).collect(Collectors.toList());
    // }

    public abstract static class NodeExtraInfoConvertorBase<T> extends ExtraInfoConvertorBase<T, NodeInfo, NodeExtraInfo> implements INodeExtraInfoConvertor<T> {
        // 加载类时需要初始化依赖的枚举类，防止出现空指针异常
        static {
            EnumUtils.staticInitializeEnum(NodeExtraInfoTypeEnum.class);
        }

        @Override
        protected NodeExtraInfo newExtraInfoModel() {
            return new NodeExtraInfo();
        }
    }

    public static class Role extends NodeExtraInfoConvertorBase<List<NodeRoleConfig>> {

        private static NodeExtraInfoTypeEnum enumValue;

        Role(NodeExtraInfoTypeEnum enumValue) {
            Role.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static Role getInstance() {
            return (Role) getEnum().getConvertor();
        }

        @Override
        public List<NodeRoleConfig> transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            List<NodeRoleConfig> nodeRoleConfigs = transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                values -> Arrays.stream(values).map(
                    value -> {
                        NodeRoleConfig nodeRoleConfig = new NodeRoleConfig();
                        String[] role = value.split("[,]");
                        nodeRoleConfig.setRoleId(role[0]);
                        nodeRoleConfig.setType(role[1]);
                        nodeRoleConfig.setMinNum(Integer.parseInt(role[2]));
                        if (role.length > 3) {
                            nodeRoleConfig.setSort(Integer.parseInt(role[3]));
                        }
                        return nodeRoleConfig;
                    }
                ).collect(Collectors.toList())
            );
            nodeRoleConfigs.sort(Comparator.comparing(NodeRoleConfig::getSort));
            return nodeRoleConfigs;
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(List<NodeRoleConfig> value) {
            int sort = 0;
            for (NodeRoleConfig nodeRoleConfig : value) {
                nodeRoleConfig.setSort(sort);
                sort++;
            }
            return transToExtraInfoListByTypeName(
                getEnum().getName(),
                value.stream().map(
                    nodeRoleConfig -> nodeRoleConfig.getRoleId() + "," + nodeRoleConfig.getType() + "," + nodeRoleConfig.getMinNum() + "," + nodeRoleConfig.getSort()
                ).toArray(String[]::new)
            );
        }

        @Override
        public List<NodeRoleConfig> getValue(NodeInfo nodeInfo) {
            return nodeInfo.getRoleConfigs();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, List<NodeRoleConfig> value) {
            nodeInfo.setRoleConfigs(value);
        }
    }

    public static class ExcludeRole extends NodeExtraInfoConvertorBase<List<RoleBase>> {

        private static NodeExtraInfoTypeEnum enumValue;

        ExcludeRole(NodeExtraInfoTypeEnum enumValue) {
            ExcludeRole.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static ExcludeRole getInstance() {
            return (ExcludeRole) getEnum().getConvertor();
        }

        @Override
        public List<RoleBase> transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                values -> Arrays.stream(values).map(
                    value -> {
                        RoleBase roleBase = new NodeRoleConfig();
                        String[] role = value.split("[,]");
                        roleBase.setRoleId(role[0]);
                        roleBase.setType(role[1]);
                        return roleBase;
                    }
                ).collect(Collectors.toList())
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(List<RoleBase> value) {
            return transToExtraInfoListByTypeName(
                getEnum().getName(),
                value.stream().map(
                    nodeRoleConfig -> nodeRoleConfig.getRoleId() + "," + nodeRoleConfig.getType()
                ).toArray(String[]::new)
            );
        }

        @Override
        public List<RoleBase> getValue(NodeInfo nodeInfo) {
            return nodeInfo.getExcludeRoles();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, List<RoleBase> value) {
            nodeInfo.setExcludeRoles(value);
        }
    }

    // public static class OnlyDispatching extends NodeExtraInfoConvertorBase<Boolean> {
    //
    //     private static Supplier<NodeExtraInfoTypeEnum> enumGetter;
    //
    //     OnlyDispatching(Supplier<NodeExtraInfoTypeEnum> enumGetter) {
    //         OnlyDispatching.enumGetter = enumGetter;
    //     }
    //
    //     private static NodeExtraInfoTypeEnum getEnum() {
    //         return enumGetter.get();
    //     }
    //
    //     public static OnlyDispatching getInstance() {
    //         return (OnlyDispatching) getEnum().getConvertor();
    //     }
    //
    //     @Override
    //     public Boolean transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
    //         return transToValueByTypeName(
    //             nodeExtraInfos,
    //             getEnum().getName(),
    //             v -> FlowUtil.stringToBoolean(v[0], false)
    //         );
    //     }
    //
    //     @Override
    //     public List<NodeExtraInfo> transformToNodeExtraInfoList(Boolean value) {
    //         return transToNodeExtraInfoListByTypeName(getEnum().getName(), FlowUtil.booleanToString(value));
    //     }
    //
    //     @Override
    //     public Boolean getValue(NodeInfo nodeInfo) {
    //         return nodeInfo.getOnlyDispatching();
    //     }
    //
    //     @Override
    //     public void setValue(NodeInfo nodeInfo, Boolean value) {
    //         nodeInfo.setOnlyDispatching(value);
    //     }
    // }

    public static class AllowSkip extends NodeExtraInfoConvertorBase<Boolean> {

        private static NodeExtraInfoTypeEnum enumValue;

        AllowSkip(NodeExtraInfoTypeEnum enumValue) {
            AllowSkip.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static AllowSkip getInstance() {
            return (AllowSkip) getEnum().getConvertor();
        }

        @Override
        public Boolean transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToBoolean(v[0], false)
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(Boolean value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.booleanToString(value));
        }

        @Override
        public Boolean getValue(NodeInfo nodeInfo) {
            return nodeInfo.getSkip();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, Boolean value) {
            nodeInfo.setSkip(value);
        }
    }

    public static class AllowDisposeAfterSkip extends NodeExtraInfoConvertorBase<Boolean> {

        private static NodeExtraInfoTypeEnum enumValue;

        AllowDisposeAfterSkip(NodeExtraInfoTypeEnum enumValue) {
            AllowDisposeAfterSkip.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static AllowDisposeAfterSkip getInstance() {
            return (AllowDisposeAfterSkip) getEnum().getConvertor();
        }

        @Override
        public Boolean transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToBoolean(v[0], false)
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(Boolean value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.booleanToString(value));
        }

        @Override
        public Boolean getValue(NodeInfo nodeInfo) {
            return nodeInfo.getDisposeAfterSkip();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, Boolean value) {
            nodeInfo.setDisposeAfterSkip(value);
        }
    }

    public static class MinDisposeNum extends NodeExtraInfoConvertorBase<Integer> {

        private static NodeExtraInfoTypeEnum enumValue;

        MinDisposeNum(NodeExtraInfoTypeEnum enumValue) {
            MinDisposeNum.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static MinDisposeNum getInstance() {
            return (MinDisposeNum) getEnum().getConvertor();
        }

        @Override
        public Integer transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToInt(v[0], 0)
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(Integer value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.intToString(value));
        }

        @Override
        public Integer getValue(NodeInfo nodeInfo) {
            return nodeInfo.getMinDisposeNum();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, Integer value) {
            nodeInfo.setMinDisposeNum(value);
        }
    }

    public static class RecommendedPicNum extends NodeExtraInfoConvertorBase<Integer> {

        private static NodeExtraInfoTypeEnum enumValue;

        RecommendedPicNum(NodeExtraInfoTypeEnum enumValue) {
            RecommendedPicNum.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static RecommendedPicNum getInstance() {
            return (RecommendedPicNum) getEnum().getConvertor();
        }

        @Override
        public Integer transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToInt(v[0], 0)
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(Integer value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.intToString(value));
        }

        @Override
        public Integer getValue(NodeInfo nodeInfo) {
            return nodeInfo.getRecommendedPicNum();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, Integer value) {
            nodeInfo.setRecommendedPicNum(value);
        }
    }

    public static class DisposeSubflow extends NodeExtraInfoConvertorBase<Boolean> {

        private static NodeExtraInfoTypeEnum enumValue;

        DisposeSubflow(NodeExtraInfoTypeEnum enumValue) {
            DisposeSubflow.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static DisposeSubflow getInstance() {
            return (DisposeSubflow) getEnum().getConvertor();
        }

        @Override
        public Boolean transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToBoolean(v[0], true)
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(Boolean value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.booleanToString(value));
        }

        @Override
        public Boolean getValue(NodeInfo nodeInfo) {
            return nodeInfo.getDisposeSubflow();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, Boolean value) {
            nodeInfo.setDisposeSubflow(value);
        }
    }

    public static class FunctionType extends NodeExtraInfoConvertorBase<String> {

        private static NodeExtraInfoTypeEnum enumValue;

        FunctionType(NodeExtraInfoTypeEnum enumValue) {
            FunctionType.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static FunctionType getInstance() {
            return (FunctionType) getEnum().getConvertor();
        }

        @Override
        public String transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> v[0]
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(String value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), value);
        }

        @Override
        public String getValue(NodeInfo nodeInfo) {
            return nodeInfo.getFunctionType();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, String value) {
            nodeInfo.setFunctionType(value);
        }
    }

    public static class OvertimeWarning extends NodeExtraInfoConvertorBase<Integer> {

        private static NodeExtraInfoTypeEnum enumValue;

        OvertimeWarning(NodeExtraInfoTypeEnum enumValue) {
            OvertimeWarning.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static OvertimeWarning getInstance() {
            return (OvertimeWarning) getEnum().getConvertor();
        }

        @Override
        public Integer transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToInt(v[0], 0)
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(Integer value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.intToString(value));
        }

        @Override
        public Integer getValue(NodeInfo nodeInfo) {
            return nodeInfo.getOvertimeWaring();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, Integer value) {
            nodeInfo.setOvertimeWaring(value);
        }
    }

    public static class MinIntervalRestrict extends NodeExtraInfoConvertorBase<Integer> {

        private static NodeExtraInfoTypeEnum enumValue;

        MinIntervalRestrict(NodeExtraInfoTypeEnum enumValue) {
            MinIntervalRestrict.enumValue = enumValue;
        }

        private static NodeExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static MinIntervalRestrict getInstance() {
            return (MinIntervalRestrict) getEnum().getConvertor();
        }

        @Override
        public Integer transformToValue(List<NodeExtraInfo> nodeExtraInfos) {
            return transToValueByTypeName(
                nodeExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToInt(v[0], 0)
            );
        }

        @Override
        public List<NodeExtraInfo> transformToExtraInfoList(Integer value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.intToString(value));
        }

        @Override
        public Integer getValue(NodeInfo nodeInfo) {
            return nodeInfo.getMinIntervalRestrict();
        }

        @Override
        public void setValue(NodeInfo nodeInfo, Integer value) {
            nodeInfo.setMinIntervalRestrict(value);
        }
    }
}
