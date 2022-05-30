package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.common.model.PostAndRole;
import com.ydzbinfo.emis.trainRepair.constant.RoleTypeEnum;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.param.WorkerInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRun;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunSwitch;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecordExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseNode;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeRecordWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo.INodeRecordExtraInfoConvertor;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo.NodeExtraInfoTypeEnum;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo.NodeRecordExtraInfoTypeEnum;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 流程相关工具类
 *
 * @author 张天可
 **/
public class FlowUtil {

    public static FlowInfoAnalysis getFlowInfoAnalysis(FlowInfo flowInfo) {
        return CacheUtil.getDataUseThreadCache("FlowUtil.getFlowInfoAnalysis_" + flowInfo.getId(), () -> new FlowInfoAnalysis(flowInfo));
    }

    public static boolean stringToBoolean(String str, boolean defaultValue) {
        if (Objects.equals(str, "1")) {
            return true;
        } else if (Objects.equals(str, "0")) {
            return false;
        } else {
            return defaultValue;
        }
    }

    public static String booleanToString(Boolean value) {
        if (value == null) {
            return null;
        } else if (value) {
            return "1";
        } else {
            return "0";
        }
    }

    public static int stringToInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(str);
        }
    }

    public static String intToString(Integer value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    /**
     * 在实际判断角色中，需要对用户的角色进行额外过滤
     *
     * @param flowTypeCode 流程类型
     */
    public static Predicate<RuntimeRole> getExtraRuntimeRoleFilterByFlowType(String flowTypeCode) {
        // 关键作业要过滤掉派工岗位角色
        if (flowTypeCode.equals(BasicFlowTypeEnum.PLANLESS_KEY.getValue())) {
            return v -> !v.getType().equals(RoleTypeEnum.POST_ROLE.getValue());
        } else {
            return v -> true;
        }
    }

    /**
     * 获取当前节点的真实配置节点，如果是普通节点或成对前一个节点，返回自身节点，否则返回成对前一个节点的信息
     * 达成成对节点只返回成对前一个节点配置的效果（只用于角色配置方面）
     */
    public static NodeInfo getPrePairNodeInfoIfAfterPairNode(String nodeId, FlowInfo flowInfo) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeId);
        if (pairNodeIndex == null || pairNodeIndex == 0) {
            return flowInfoAnalysis.getNodeMap().get(nodeId);
        } else {
            String prePairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId), 0);
            return flowInfoAnalysis.getNodeMap().get(prePairNodeId);
        }
    }

    /**
     * 判断某人是否满足某节点的角色配置
     *
     * @param nodeInfo        节点配置信息
     * @param runtimeRoleList 某人的角色列表
     * @param flowTypeCode    流程类型编码
     */
    public static boolean hasPowerToDispose(NodeInfo nodeInfo, List<RuntimeRole> runtimeRoleList, String flowTypeCode) {
        List<String> roles = runtimeRoleList.stream()
            .filter(getExtraRuntimeRoleFilterByFlowType(flowTypeCode))
            .map(RoleBase::getRoleId)
            .collect(Collectors.toList());
        // 配置的角色要满足
        // 得到满足角色和排除角色
        Set<String> roleSet = nodeInfo.getRoleConfigs().stream().map(RoleBase::getRoleId).collect(Collectors.toSet());
        Set<String> excludeRoleSet = nodeInfo.getExcludeRoles().stream().map(RoleBase::getRoleId).collect(Collectors.toSet());
        // 存在满足角色里并且不存在排除角色里
        return roles.stream().anyMatch(roleSet::contains) && roles.stream().noneMatch(excludeRoleSet::contains);
    }

    /**
     * 校验流程运行时针对成对节点某个角色是否可处理的结果的枚举类型（非成对节点直接成功，排除自定义校验的影响）
     *
     * @author 张天可
     */
    public enum CheckPairNodeDisposeRuntimeRoleResultEnum {
        /**
         * 校验成功
         */
        SUCCESS,
        /**
         * 未知失败(自定义额外校验时使用)
         */
        UNKNOWN_FAILURE,
        /**
         * 前面的节点没有指定角色的处理记录(指定人，指定角色，前一个节点没有处理过，后一个节点也不可处理)
         */
        PREVIOUS_PAIR_NODE_NO_ROLE_RECORD,
        /**
         * 当前角色对于当前节点而言已过期，无法变更(指定人，指定角色，后一个节点处理过了，前一个节点禁止处理)
         */
        PAIR_NODE_ROLE_PASTED
    }

    /**
     * 校验流程运行时成对节点某个角色是否可处理的结果，用于把自定义校验的内容封装进来
     *
     * @param <T>
     * @author 张天可
     */
    @Data
    public static class CheckPairNodeDisposeRuntimeRoleResult<T> {
        private CheckPairNodeDisposeRuntimeRoleResultEnum resultEnum;
        private T payload;
    }

    /**
     * 流程运行时，针对成对节点的特殊状况，获取用于得到指定角色是否可处理的校验结果的检查器
     *
     * @param staffId                         人员id
     * @param nodeId                          节点id
     * @param flowInfo                        流程配置信息
     * @param curFlowRunNodeRecords           节点记录信息
     * @param extraPredicate                  自定义额外断言，参数为角色id
     * @param testExtraPredicateResultSuccess 判断自定义断言的成功性（extraPredicate存在时必传）
     * @param <T>                             自定义断言的结果类型
     */
    public static <T> Function<String, CheckPairNodeDisposeRuntimeRoleResult<T>> getRuntimeRoleCheckerForPairNode(
        String staffId,
        String nodeId,
        FlowInfo flowInfo,
        List<? extends NodeRecordInfo> curFlowRunNodeRecords,
        Function<String, T> extraPredicate,
        Predicate<T> testExtraPredicateResultSuccess
    ) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);

        Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeId);

        // 代理检查器，加入自定义额外断言的判断
        Function<Function<String, CheckPairNodeDisposeRuntimeRoleResult<T>>, Function<String, CheckPairNodeDisposeRuntimeRoleResult<T>>> getAgentChecker = (checker) -> {
            if (extraPredicate == null) {
                return checker;
            } else {
                if (testExtraPredicateResultSuccess == null) {
                    throw new RuntimeException("参数错误，extraPredicate存在时，testExtraPredicateResultSuccess为必传");
                }
                return (runtimeRoleId) -> {
                    T payload = extraPredicate.apply(runtimeRoleId);
                    if (testExtraPredicateResultSuccess.test(payload)) {
                        return checker.apply(runtimeRoleId);
                    } else {
                        CheckPairNodeDisposeRuntimeRoleResult<T> result = new CheckPairNodeDisposeRuntimeRoleResult<>();
                        result.setResultEnum(CheckPairNodeDisposeRuntimeRoleResultEnum.UNKNOWN_FAILURE);
                        result.setPayload(payload);
                        return result;
                    }
                };
            }
        };

        if (pairNodeIndex != null) {// 如果是成对节点
            Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.groupingBy(NodeRecord::getNodeId));
            Function<String, List<? extends NodeRecordInfo>> getNodeRecords = (targetNodeId) -> {
                List<? extends NodeRecordInfo> nodeRecords = groupedNodeRecords.get(targetNodeId);
                if (nodeRecords == null) {
                    nodeRecords = new ArrayList<>();
                }
                return nodeRecords;
            };
            if (pairNodeIndex == 1) {// 如果是成对后一个节点
                // 找到前一个节点，看看前面打了什么角色
                String prePairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId), 0);
                List<? extends NodeRecordInfo> prePairNodeRecordInfos = getNodeRecords.apply(prePairNodeId);
                Set<String> curPersonPrePairNodeDisposedRoleIds = prePairNodeRecordInfos.stream()
                    .filter(v -> v.getWorkerId().equals(staffId))
                    .map(NodeRecord::getRoleId)
                    .collect(Collectors.toSet());
                // 此种情况需要忽视自定义断言，所以不使用代理(因为要前一个节点处理过的角色后一个节点都要进行处理，额外断言没有意义)
                return runtimeRoleId -> {
                    CheckPairNodeDisposeRuntimeRoleResult<T> result = new CheckPairNodeDisposeRuntimeRoleResult<>();
                    // 如果当前角色没有成对前一个节点的处理记录，返回对应结果（角色id为空说明此前判断没有可处理的角色）
                    if (runtimeRoleId == null || !curPersonPrePairNodeDisposedRoleIds.contains(runtimeRoleId)) {
                        result.setResultEnum(CheckPairNodeDisposeRuntimeRoleResultEnum.PREVIOUS_PAIR_NODE_NO_ROLE_RECORD);
                    } else {
                        // 前一个节点处理过的角色可以进行处理
                        result.setResultEnum(CheckPairNodeDisposeRuntimeRoleResultEnum.SUCCESS);
                    }
                    return result;
                };
            } else {
                String afterPairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId), 1);
                List<? extends NodeRecordInfo> afterPairNodeRecordInfos = getNodeRecords.apply(afterPairNodeId);
                // 找出已经处理过后一个节点的角色
                Set<String> currentPersonDisposedAfterPairNodeRoles = afterPairNodeRecordInfos.stream()
                    .filter(v -> v.getWorkerId().equals(staffId))
                    .map(NodeRecord::getRoleId)
                    .collect(Collectors.toSet());
                return getAgentChecker.apply(runtimeRoleId -> {
                    CheckPairNodeDisposeRuntimeRoleResult<T> result = new CheckPairNodeDisposeRuntimeRoleResult<>();
                    // 如果当前角色已经被后一个节点处理，则此时节点在当前角色上过期(角色id为空说明此前的判断是全部节点均已过期，即没有可处理的角色)
                    if (runtimeRoleId == null || currentPersonDisposedAfterPairNodeRoles.contains(runtimeRoleId)) {
                        result.setResultEnum(CheckPairNodeDisposeRuntimeRoleResultEnum.PAIR_NODE_ROLE_PASTED);
                    } else {
                        result.setResultEnum(CheckPairNodeDisposeRuntimeRoleResultEnum.SUCCESS);
                    }

                    return result;
                });
            }
        } else {
            return getAgentChecker.apply(v -> {
                CheckPairNodeDisposeRuntimeRoleResult<T> result = new CheckPairNodeDisposeRuntimeRoleResult<>();
                result.setResultEnum(CheckPairNodeDisposeRuntimeRoleResultEnum.SUCCESS);
                return result;
            });
        }
    }

    /**
     * 解析转换节点信息
     */
    public static List<NodeInfo> parseNodeInfo(List<NodeWithExtraInfo> nodeWithExtraInfos) {
        List<NodeInfo> nodeInfos = new ArrayList<>();
        //转换为NodeInfo
        for (NodeWithExtraInfo nodeWithExtraInfo : nodeWithExtraInfos) {
            NodeInfo nodeInfo = new NodeInfo();
            BeanUtils.copyProperties(nodeWithExtraInfo, nodeInfo);

            // 将额外信息设置到nodeInfo里
            for (NodeExtraInfoTypeEnum nodeExtraInfoTypeEnum : NodeExtraInfoTypeEnum.values()) {
                nodeExtraInfoTypeEnum.getConvertor().setValueFromExtraInfoList(nodeInfo, nodeWithExtraInfo.getNodeExtraInfoList());
            }

            nodeInfos.add(nodeInfo);
        }
        return nodeInfos;
    }

    /**
     * 转换节点记录信息
     */
    public static List<NodeRecordInfo> parseNodeRecordInfo(List<NodeRecordWithExtraInfo> nodeRecordWithExtraInfoList) {
        return nodeRecordWithExtraInfoList.stream().map(FlowUtil::convertTo).collect(Collectors.toList());
    }

    public static NodeRecordInfo convertTo(NodeRecordWithExtraInfo nodeRecordWithExtraInfo) {
        NodeRecordInfo nodeRecordInfo = new NodeRecordInfo();
        BeanUtils.copyProperties(nodeRecordWithExtraInfo, nodeRecordInfo);
        for (NodeRecordExtraInfoTypeEnum nodeRecordExtraInfoTypeEnum : NodeRecordExtraInfoTypeEnum.values()) {
            nodeRecordExtraInfoTypeEnum.getConvertor().setValueFromExtraInfoList(nodeRecordInfo, nodeRecordWithExtraInfo.getNodeRecordExtraInfoList());
        }
        return nodeRecordInfo;
    }

    public static NodeRecordWithExtraInfo convertTo(NodeRecordInfo nodeRecordInfo) {
        NodeRecordWithExtraInfo nodeRecordWithExtraInfo = new NodeRecordWithExtraInfo();
        BeanUtils.copyProperties(nodeRecordInfo, nodeRecordWithExtraInfo);
        List<NodeRecordExtraInfo> nodeRecordExtraInfoList = new ArrayList<>();
        nodeRecordWithExtraInfo.setNodeRecordExtraInfoList(nodeRecordExtraInfoList);
        for (NodeRecordExtraInfoTypeEnum nodeRecordExtraInfoTypeEnum : NodeRecordExtraInfoTypeEnum.values()) {
            INodeRecordExtraInfoConvertor<?> convertor = nodeRecordExtraInfoTypeEnum.getConvertor();
            nodeRecordExtraInfoList.addAll(convertor.getCurrentTypeExtraInfoList(nodeRecordInfo));
        }
        return nodeRecordWithExtraInfo;
    }

    @Data
    public static class FlowRunSwitchPath {
        private List<FlowRun> flowRuns;
        private String startFlowId;
        private String endFlowId;
    }

    public static List<FlowRunSwitchPath> analyseFlowRunSwitchPaths(List<FlowRun> flowRunList, List<FlowRunSwitch> flowRunSwitchList) {
        List<FlowRunSwitchPath> flowRunSwitchPaths = new ArrayList<>();
        Map<String, FlowRun> flowRunMap = CommonUtils.collectionToMap(flowRunList, FlowRun::getId);
        Map<String, FlowRunSwitch> sourceFlowRunSwitchMap = CommonUtils.collectionToMap(flowRunSwitchList, FlowRunSwitch::getSourceFlowRunId);
        Map<String, FlowRunSwitch> targetFlowRunSwitchMap = CommonUtils.collectionToMap(flowRunSwitchList, FlowRunSwitch::getTargetFlowId);

        Set<String> checkedFlowRunIds = new HashSet<>();


        for (FlowRun flowRun : flowRunList) {
            String flowRunId = flowRun.getId();

            if (checkedFlowRunIds.contains(flowRunId)) {
                continue;
            }

            FlowRunSwitchPath flowRunSwitchPath = new FlowRunSwitchPath();
            LinkedList<FlowRun> flowRuns = new LinkedList<>();
            flowRuns.add(flowRun);
            checkedFlowRunIds.add(flowRunId);

            String curFlowRunId = flowRunId;
            while (sourceFlowRunSwitchMap.containsKey(curFlowRunId)) {
                String targetFlowRunId = sourceFlowRunSwitchMap.get(curFlowRunId).getTargetFlowRunId();
                flowRuns.addLast(flowRunMap.get(targetFlowRunId));
                checkedFlowRunIds.add(targetFlowRunId);
                curFlowRunId = targetFlowRunId;
            }

            curFlowRunId = flowRunId;
            while (targetFlowRunSwitchMap.containsKey(curFlowRunId)) {
                String sourceFlowRunId = targetFlowRunSwitchMap.get(curFlowRunId).getSourceFlowRunId();
                // 切换源可能为空
                if (StringUtils.isNotBlank(sourceFlowRunId)) {
                    flowRuns.addFirst(flowRunMap.get(sourceFlowRunId));
                    checkedFlowRunIds.add(sourceFlowRunId);
                }
                curFlowRunId = sourceFlowRunId;
            }

            flowRunSwitchPath.setFlowRuns(flowRuns);

            FlowRun firstFlowRun = flowRuns.get(0);
            String firstFlowRunId = firstFlowRun.getId();
            if (targetFlowRunSwitchMap.containsKey(firstFlowRunId)) {
                flowRunSwitchPath.setStartFlowId(targetFlowRunSwitchMap.get(curFlowRunId).getSourceFlowId());
            } else {
                flowRunSwitchPath.setStartFlowId(firstFlowRun.getFlowId());
            }

            flowRunSwitchPath.setEndFlowId(flowRuns.get(flowRuns.size() - 1).getFlowId());

            flowRunSwitchPaths.add(flowRunSwitchPath);

        }
        return flowRunSwitchPaths;
    }

    /**
     * 是否处理过
     *
     * @param curNodeRecords
     * @param staffId
     * @return
     */
    public static boolean disposed(List<? extends NodeRecordInfo> curNodeRecords, String staffId) {
        if (curNodeRecords == null || curNodeRecords.size() == 0) {
            return false;
        } else {
            return curNodeRecords.stream().anyMatch(v -> nodeRecordPersonDisposeFilter(v, staffId, null));
        }
    }

    public static boolean nodeRecordPersonDisposeFilter(NodeRecordInfo nodeRecordInfo, String staffId, String
        roleId) {
        return nodeRecordInfo.getType().equals(NodeRecordTypeEnum.PERSON.getValue()) &&
            nodeRecordInfo.getWorkerId().equals(staffId) &&
            !Objects.equals(nodeRecordInfo.getSkip(), true) &&// 排除跳过记录
            (roleId == null || roleId.equals(nodeRecordInfo.getRoleId()));
    }

    /**
     * 节点是否已经完成
     *
     * @param curNodeRecords 当前节点记录信息
     */
    public static boolean nodeFinished(List<? extends NodeRecord> curNodeRecords) {
        if (curNodeRecords == null || curNodeRecords.size() == 0) {
            return false;
        } else {
            return curNodeRecords.stream().anyMatch(v -> v.getType().equals(NodeRecordTypeEnum.NODE_FINISH.getValue()));
        }
    }

    /**
     * 节点是否已跳过
     *
     * @param curNodeRecords 当前节点记录信息
     */
    public static boolean nodeSkipped(List<? extends NodeRecordInfo> curNodeRecords) {
        if (curNodeRecords == null) {
            return false;
        } else {
            return curNodeRecords.stream().anyMatch(
                v -> v.getSkip() != null && v.getSkip()
            );
        }
    }

    /**
     * 获取可处理的角色列表
     *
     * @param nodeId                节点id
     * @param flowInfo              流程配置信息
     * @param curFlowRunNodeRecords 当前流程运行所有的节点记录
     * @param staffId               打卡人id(为空时表示第三视角看节点信息)
     * @param runtimeRoleList       打卡人角色信息（包含车组下派工岗位信息；为空则意味着拥有通用权限，相当于拥有所有角色）
     */
    public static List<String> getNodeCouldDisposeRuntimeRoleIds(String nodeId, FlowInfo flowInfo, List<NodeRecordInfo> curFlowRunNodeRecords, String staffId, List<RuntimeRole> runtimeRoleList) {
        // 根据流程本身判断是否满足打卡条件，不满足则不允许任何角色进行处理
        boolean couldDisposeByFlowInfoAndDisposeInfo = FlowUtil.nodeCouldDisposeIgnoreNodeRoleConfigs(nodeId, flowInfo, curFlowRunNodeRecords, staffId).getResult();// 根据流程的结构和打卡情况判断当前节点是否可处理
        if (!couldDisposeByFlowInfoAndDisposeInfo) {
            return new ArrayList<>();
        }

        // 如果人员id存在，则认为是第一人称视角看节点信息
        boolean firstPersonPerspective = StringUtils.isNotBlank(staffId);
        // 如果没有角色，则认为拥有所有角色
        boolean hasAllRole = runtimeRoleList == null || runtimeRoleList.size() == 0;

        NodeInfo targetNodeConfigForRoleConfig = FlowUtil.getPrePairNodeInfoIfAfterPairNode(nodeId, flowInfo);

        if (firstPersonPerspective) {// 如果第一人称视角看节点信息，则判断当前人是否可处理此节点
            // 忽略刷卡记录，根据节点配置判断是否有权限进行节点处理
            if (!hasAllRole && !FlowUtil.hasPowerToDispose(targetNodeConfigForRoleConfig, runtimeRoleList, flowInfo.getFlowTypeCode())) {
                // 没有权限返回空数组
                return new ArrayList<>();
            } else {// 有权限则根据刷卡记录动态返回当前人可刷卡的角色
                Predicate<String> filterByRuntimeRole;
                // 如果是第一人称视角看节点信息，则需要根据当前人角色信息进行过滤
                if (hasAllRole) {// 有全部角色直接返回true
                    filterByRuntimeRole = v -> true;
                } else {// 否则需要在当前人的角色里
                    Set<String> runtimeRoleIds = runtimeRoleList.stream()
                        .filter(FlowUtil.getExtraRuntimeRoleFilterByFlowType(flowInfo.getFlowTypeCode()))
                        .map(RoleBase::getRoleId)
                        .collect(Collectors.toSet());
                    filterByRuntimeRole = runtimeRoleIds::contains;
                }

                Function<String, FlowUtil.CheckPairNodeDisposeRuntimeRoleResult<Boolean>> runtimeRoleChecker = FlowUtil.getRuntimeRoleCheckerForPairNode(
                    staffId,
                    nodeId,
                    flowInfo,
                    curFlowRunNodeRecords,
                    filterByRuntimeRole::test,
                    v -> v
                );
                return targetNodeConfigForRoleConfig.getRoleConfigs().stream().map(RoleBase::getRoleId)
                    .filter(filterByRuntimeRole)
                    .filter(runtimeRoleId -> runtimeRoleChecker.apply(runtimeRoleId).getResultEnum().equals(FlowUtil.CheckPairNodeDisposeRuntimeRoleResultEnum.SUCCESS))
                    .collect(Collectors.toList());
            }
        } else {
            // 如果不是第一人称视角看节点信息，则从流程结构上能处理节点，就看作能使用这个节点所有的角色进行处理
            return targetNodeConfigForRoleConfig.getRoleConfigs().stream().map(RoleBase::getRoleId)
                .collect(Collectors.toList());
        }
    }


    /**
     * 判断节点是否可处理（忽略节点的角色配置）
     *
     * @param nodeId                节点id
     * @param flowInfo              流程信息
     * @param curFlowRunNodeRecords 当前流程运行节点记录
     * @param staffId               打卡者id
     */
    public static CheckNodeDisposeResult nodeCouldDisposeIgnoreNodeRoleConfigs(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords, String staffId) {

        Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));

        List<? extends NodeRecordInfo> curNodeRecords = groupedNodeRecords.get(nodeId);

        CheckNodeStartedResult checkNodeStartedResult = nodeStartedUseSkippedNodes(nodeId, flowInfo, curFlowRunNodeRecords, staffId);

        // 节点是否启动
        boolean isNodeStarted = checkNodeStartedResult.getResult();

        CheckNodeDisposeResult checkNodeDisposeResult = new CheckNodeDisposeResult();
        checkNodeDisposeResult.setNodeStarted(isNodeStarted);
        checkNodeDisposeResult.setSkippedNodes(checkNodeStartedResult.getSkippedNodes());

        // 如果节点没有启动，则节点不能进行处理
        if (!isNodeStarted) {
            checkNodeDisposeResult.setNodePasted(false);
            checkNodeDisposeResult.setDisposeAfterSkip(false);
            checkNodeDisposeResult.setResult(false);
            return checkNodeDisposeResult;
        }

        // 判断节点是否从结构上过期
        boolean isNodePasted = nodePasted(nodeId, flowInfo, curFlowRunNodeRecords);
        checkNodeDisposeResult.setNodePasted(isNodePasted);
        // 如果没有过期，则一定可处理
        if (!isNodePasted) {
            checkNodeDisposeResult.setDisposeAfterSkip(false);
            checkNodeDisposeResult.setResult(true);
            return checkNodeDisposeResult;
        }

        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        NodeInfo curNodeInfo = flowInfoAnalysis.getNodeMap().get(nodeId);

        // 虽然过期了，但是如果可以补卡，也可以处理
        boolean disposeAfterSkip = nodeSkipped(curNodeRecords) && curNodeInfo.getDisposeAfterSkip();
        checkNodeDisposeResult.setResult(disposeAfterSkip);
        checkNodeDisposeResult.setDisposeAfterSkip(disposeAfterSkip);
        return checkNodeDisposeResult;
    }

    /**
     * 节点是否能够处理检查结果类
     */
    @Data
    public static class CheckNodeDisposeResult {
        private Boolean result;
        private Boolean nodeStarted;
        private Boolean nodePasted;
        private List<BaseNode> skippedNodes;
        private Boolean disposeAfterSkip;
    }

    /**
     * 节点启动检查结果类
     */
    @Data
    public static class CheckNodeStartedResult {
        private Boolean result;
        private List<BaseNode> skippedNodes;
    }

    public static boolean nodeStarted(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords, String staffId) {
        return nodeStartedUseSkippedNodes(nodeId, flowInfo, curFlowRunNodeRecords, staffId).getResult();
    }

    public static boolean normalTypeNodeRecordFilter(NodeRecordInfo nodeRecord) {
        return !nodeRecord.getType().equals(NodeRecordTypeEnum.NODE_START.getValue()) &&
            !nodeRecord.getType().equals(NodeRecordTypeEnum.NODE_FINISH.getValue()) &&
            !(nodeRecord.getSkip() != null && nodeRecord.getSkip());
    }

    /**
     * 节点是否已经启动，带临时缓存，需要处理后
     *
     * @param nodeId
     * @param flowInfo
     * @param curFlowRunNodeRecords
     * @param staffId
     * @return
     */
    public static CheckNodeStartedResult nodeStartedUseSkippedNodes(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords, String staffId) {
        String flowRunId = curFlowRunNodeRecords.size() > 0 ? curFlowRunNodeRecords.get(0).getFlowRunId() : "";
        return CacheUtil.getDataUseThreadCache(
            "FlowUtil.nodeStartedUseSkippedNodesNoCache_" + flowInfo.getId() + "" + nodeId + "_" + flowRunId + "_" + staffId,
            () -> nodeStartedUseSkippedNodesNoCache(nodeId, flowInfo, curFlowRunNodeRecords, staffId)
        );
    }

    /**
     * 本节点前未完成的节点
     *
     * @param nodeId
     * @param flowInfo
     * @param curFlowRunNodeRecords
     * @return
     */
    public static List<NodeInfo> findCurrentNodePreviousNotFinishedNodeList(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords, String staffId) {
        Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));

        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);

        List<NodeInfo> previousNodes = flowInfoAnalysis.getPreviousNodes(nodeId);
        Function<NodeInfo, Boolean> checkNodeSkip = getInnerCheckNodeSkip(flowInfo, curFlowRunNodeRecords, staffId);

        List<NodeInfo> nodeFinished = previousNodes.stream().filter(nodeInfo -> {
            List<? extends NodeRecordInfo> curNodeRecords = groupedNodeRecords.get(nodeInfo.getId());
            if (nodeSkipped(curNodeRecords) || checkNodeSkip.apply(nodeInfo)) {
                return false;
            } else if (!nodeFinished(curNodeRecords)) {
                return true;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
        return nodeFinished;
    }

    public static String getNodeNotFinishedCause(List<NodeInfo> nodeInfoList, List<? extends NodeRecordInfo> curFlowRunNodeRecords, List<PostAndRole> postAndRoleList) {
        curFlowRunNodeRecords = curFlowRunNodeRecords.stream().filter(v -> v.getType().equals(NodeRecordTypeEnum.PERSON.getValue())).collect(Collectors.toList());
        //按节点id分组节点刷卡记录信息
        Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));
        //按code分组角色岗位信息
        Map<String, PostAndRole> groupPostAndRolesByCode = CommonUtils.collectionToMap(postAndRoleList, PostAndRole::getCode);
        StringBuilder stringBuilder = new StringBuilder();
        for (NodeInfo nodeInfo : nodeInfoList) {
            //获取节点刷卡记录信息
            List<? extends NodeRecordInfo> curNodeRecords = groupedNodeRecords.get(nodeInfo.getId());
            if (curNodeRecords != null) {
                //按workerId分组刷卡记录
                Map<String, ? extends List<? extends NodeRecordInfo>> groupNodeRecordByWorkId = curNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getWorkerId));
                //按角色岗位id分组刷卡记录
                Map<String, ? extends List<? extends NodeRecordInfo>> groupNodeRecordByRoleId = curNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getRoleId));
                //查看角色配置人数是否满足
                List<NodeRoleConfig> nodeRoleConfigs = nodeInfo.getRoleConfigs().stream().filter(v -> v.getMinNum() > 0).collect(Collectors.toList());
                stringBuilder.append("[" + nodeInfo.getName() + "节点]");
                for (NodeRoleConfig nodeRoleConfig : nodeRoleConfigs) {
                    List<? extends NodeRecordInfo> nodeRecordByRoleId = groupNodeRecordByRoleId.get(nodeRoleConfig.getRoleId());
                    PostAndRole postAndRole = groupPostAndRolesByCode.get(nodeRoleConfig.getRoleId());
                    if (nodeRecordByRoleId != null) {
                        if (nodeRecordByRoleId.size() < nodeRoleConfig.getMinNum()) {
                            stringBuilder.append(postAndRole.getName() + "角色配置了__" + nodeRoleConfig.getMinNum() + "__人刷卡,当前有__" + nodeRecordByRoleId.size() + "__人刷卡!");
                        }
                    } else {
                        stringBuilder.append(postAndRole.getName() + "角色配置了__" + nodeRoleConfig.getMinNum() + "__人刷卡,当前有0人刷卡!");
                    }
                }
                //查看最小处理人数和打卡人数
                Integer minDisposeNum = nodeInfo.getMinDisposeNum();
                if (minDisposeNum != null && minDisposeNum > 0) {
                    if (groupNodeRecordByWorkId.size() < minDisposeNum) {
                        stringBuilder.append(" 配置最小处理人数__" + minDisposeNum + "__个,当前有__" + groupNodeRecordByWorkId.size() + "__个人刷卡!");
                    }
                }
            } else {
                return "[" + nodeInfo.getName() + "]或其前置节点未打卡!";
            }
        }
        return stringBuilder.toString();
    }

    public static Function<NodeInfo, Boolean> getInnerCheckNodeSkip(FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords, String staffId) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        // 检查单个节点是否能跳过
        Function<NodeInfo, Boolean> innerCheckNodeSkip = nodeInfo -> {
            Boolean skip = nodeInfo.getSkip();
            if (skip == null) {
                skip = false;
            }
            if (skip) {
                BaseNode baseNode = new BaseNode();
                BeanUtils.copyProperties(nodeInfo, baseNode);
            }
            return skip;
        };


        // 检查前面的节点是否可跳过，如果可跳过，则需要记录这个节点，外层方法最终需要返回原节点启动需要跳过的节点
        Function<NodeInfo, Boolean> checkNodeSkip = nodeInfoNeedCheckSkip -> {
            // 成对节点下标
            Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeInfoNeedCheckSkip.getId());

            if (pairNodeIndex == null) {
                // 判断普通节点可跳过之前要先起判断是否已经启动，未启动的节点禁止跳过
                CheckNodeStartedResult curNodeCheckDisposeResult = nodeStartedUseSkippedNodes(nodeInfoNeedCheckSkip.getId(), flowInfo, curFlowRunNodeRecords, staffId);
                if (!curNodeCheckDisposeResult.result) {
                    return false;
                } else {
                    return innerCheckNodeSkip.apply(nodeInfoNeedCheckSkip);
                }
            } else {
                PairNodeInfo pairNodeInfo = flowInfoAnalysis.getPairNodeInfoByNodeId(nodeInfoNeedCheckSkip.getId());
                NodeInfo prePairNodeInfo = flowInfoAnalysis.getNodeMap().get(getPairNodeIdByIndex(
                    flowInfo,
                    pairNodeInfo,
                    0
                ));
                // 判断成对前一个节点是否启动
                CheckNodeStartedResult prePairNodeCheckDisposeResult = nodeStartedUseSkippedNodes(prePairNodeInfo.getId(), flowInfo, curFlowRunNodeRecords, staffId);
                if (!prePairNodeCheckDisposeResult.result) {
                    return false;
                } else {
                    // 检查成对节点能否整体跳过
                    boolean canSkip = innerCheckNodeSkip.apply(prePairNodeInfo);
                    if (canSkip) {
                        NodeInfo afterPairNodeInfo = flowInfoAnalysis.getNodeMap().get(getPairNodeIdByIndex(
                            flowInfo,
                            pairNodeInfo,
                            1
                        ));
                        BaseNode baseNode = new BaseNode();
                        BeanUtils.copyProperties(afterPairNodeInfo, baseNode);
                        // 如果成对节点可跳过，并且当前节点是成对后一个节点，则需要判断上游节点是否不存在没有启动的节点（排除成对前一个节点），不存在则可跳过，存在则不可跳过
                        if (pairNodeIndex == 1) {
                            return flowInfoAnalysis.getPreviousNodes(nodeInfoNeedCheckSkip.getId()).stream().filter(
                                nodeInfo -> !nodeInfo.getId().equals(prePairNodeInfo.getId())
                            ).noneMatch(nodeInfo -> {
                                CheckNodeStartedResult nodeStartedResult = nodeStartedUseSkippedNodes(nodeInfo.getId(), flowInfo, curFlowRunNodeRecords, staffId);
                                return !nodeStartedResult.result;
                            });
                        }
                    }
                    return canSkip;
                }
            }
        };
        return checkNodeSkip;
    }

    /**
     * 节点是否已经启动
     */
    public static CheckNodeStartedResult nodeStartedUseSkippedNodesNoCache(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords, String staffId) {
        CheckNodeStartedResult checkNodeDisposeResult = new CheckNodeStartedResult();
        List<BaseNode> skippedNodes = new ArrayList<>();
        checkNodeDisposeResult.setSkippedNodes(skippedNodes);

        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));

        // 检查单个节点是否能跳过
        Function<NodeInfo, Boolean> innerCheckNodeSkip = nodeInfo -> {
            Boolean skip = nodeInfo.getSkip();
            if (skip == null) {
                skip = false;
            }
            if (skip) {
                BaseNode baseNode = new BaseNode();
                BeanUtils.copyProperties(nodeInfo, baseNode);
                skippedNodes.add(baseNode);
            }
            return skip;
        };


        // 检查前面的节点是否可跳过，如果可跳过，则需要记录这个节点，外层方法最终需要返回原节点启动需要跳过的节点
        Function<NodeInfo, Boolean> checkNodeSkip = nodeInfoNeedCheckSkip -> {
            // 成对节点下标
            Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeInfoNeedCheckSkip.getId());

            if (pairNodeIndex == null) {
                // 判断普通节点可跳过之前要先起判断是否已经启动，未启动的节点禁止跳过
                CheckNodeStartedResult curNodeCheckDisposeResult = nodeStartedUseSkippedNodes(nodeInfoNeedCheckSkip.getId(), flowInfo, curFlowRunNodeRecords, staffId);
                if (!curNodeCheckDisposeResult.result) {
                    return false;
                } else {
                    skippedNodes.addAll(curNodeCheckDisposeResult.getSkippedNodes());
                    return innerCheckNodeSkip.apply(nodeInfoNeedCheckSkip);
                }
            } else {
                PairNodeInfo pairNodeInfo = flowInfoAnalysis.getPairNodeInfoByNodeId(nodeInfoNeedCheckSkip.getId());
                NodeInfo prePairNodeInfo = flowInfoAnalysis.getNodeMap().get(getPairNodeIdByIndex(
                    flowInfo,
                    pairNodeInfo,
                    0
                ));
                // 判断成对前一个节点是否启动
                CheckNodeStartedResult prePairNodeCheckDisposeResult = nodeStartedUseSkippedNodes(prePairNodeInfo.getId(), flowInfo, curFlowRunNodeRecords, staffId);
                if (!prePairNodeCheckDisposeResult.result) {
                    return false;
                } else {
                    skippedNodes.addAll(prePairNodeCheckDisposeResult.getSkippedNodes());
                    // 检查成对节点能否整体跳过
                    boolean canSkip = innerCheckNodeSkip.apply(prePairNodeInfo);
                    if (canSkip) {
                        NodeInfo afterPairNodeInfo = flowInfoAnalysis.getNodeMap().get(getPairNodeIdByIndex(
                            flowInfo,
                            pairNodeInfo,
                            1
                        ));
                        BaseNode baseNode = new BaseNode();
                        BeanUtils.copyProperties(afterPairNodeInfo, baseNode);
                        skippedNodes.add(baseNode);
                        // 如果成对节点可跳过，并且当前节点是成对后一个节点，则需要判断上游节点是否不存在没有启动的节点（排除成对前一个节点），不存在则可跳过，存在则不可跳过
                        if (pairNodeIndex == 1) {
                            return flowInfoAnalysis.getPreviousNodes(nodeInfoNeedCheckSkip.getId()).stream().filter(
                                nodeInfo -> !nodeInfo.getId().equals(prePairNodeInfo.getId())
                            ).noneMatch(nodeInfo -> {
                                CheckNodeStartedResult nodeStartedResult = nodeStartedUseSkippedNodes(nodeInfo.getId(), flowInfo, curFlowRunNodeRecords, staffId);
                                return !nodeStartedResult.result;
                            });
                        }
                    }
                    return canSkip;
                }
            }
        };

        // 成对节点下标
        Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeId);
        // 普通节点或者成对节点中的前一个
        if (pairNodeIndex == null || pairNodeIndex == 0) {
            // 上游节点均满足 (完成) 或 (可跳过) 或 (已跳过)
            List<NodeInfo> previousNodes = flowInfoAnalysis.getPreviousNodes(nodeId);
            boolean result = CommonUtils.every(previousNodes, nodeInfo -> {
                String currentNodeId = nodeInfo.getId();
                List<? extends NodeRecordInfo> curNodeRecords = groupedNodeRecords.get(currentNodeId);
                Integer currentPairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(currentNodeId);
                if (currentPairNodeIndex != null && currentPairNodeIndex == 0) {
                    // 如果是成对前一个节点，则只要处理过就可向后进行
                    return curNodeRecords != null && curNodeRecords.size() > 0;
                } else {
                    // 否则要求节点完成或者节点可跳过或已跳过
                    return nodeFinished(curNodeRecords) || nodeSkipped(curNodeRecords) || checkNodeSkip.apply(nodeInfo);
                }
            });
            checkNodeDisposeResult.setResult(result);
        } else {
            PairNodeInfo pairNodeInfo = flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId);
            Set<String> pairNodeIds = new HashSet<>(Arrays.asList(pairNodeInfo.getNodeIds()));
            // 排除了当前成对节点内节点的上游节点均满足 (完成) 或 (已跳过) 或 (可跳过) 并且 ((成对前一个节点满足单人已完成[目标打卡人存在]) 或者 (成对前一个节点存在打卡记录[目标打卡人不存在]))
            List<NodeInfo> previousNodes = flowInfoAnalysis.getPreviousNodes(nodeId).stream().filter(
                v -> !pairNodeIds.contains(v.getId())
            ).collect(Collectors.toList());

            List<? extends NodeRecordInfo> prePairNodeRecords = groupedNodeRecords.get(getPairNodeIdByIndex(
                flowInfo,
                flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId),
                pairNodeIndex - 1
            ));
            boolean result = CommonUtils.every(previousNodes, nodeInfo -> {
                List<? extends NodeRecordInfo> curNodeRecords = groupedNodeRecords.get(nodeInfo.getId());
                // 均满足 (完成) 或 (可跳过) 或 (已跳过)
                return nodeFinished(curNodeRecords) || nodeSkipped(curNodeRecords) || checkNodeSkip.apply(nodeInfo);
            }) &&
                (   // 如果人员id存在，要求前一个节点打过卡，否则只要成对前一个节点存在节点处理记录就算节点启动了
                    staffId != null ? disposed(
                        prePairNodeRecords,
                        staffId
                    ) : (
                        prePairNodeRecords != null && prePairNodeRecords.stream().anyMatch(FlowUtil::normalTypeNodeRecordFilter)
                    )
                );
            checkNodeDisposeResult.setResult(result);
        }
        return checkNodeDisposeResult;
    }


    /**
     * 实时获取节点是否完成
     *
     * @param nodeId                节点id
     * @param flowInfo              流程信息
     * @param curFlowRunNodeRecords 当前流程运行所有的节点记录信息
     * @param staffId               刷卡人id（如果不想查看当前人刷卡完成情况，而是想整体看刷卡完成情况，请传null）
     */
    public static boolean runtimeNodeFinished(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords, String staffId, List<RuntimeRole> runtimeRoleList) {
        // 如果人员id存在，则认为是第一人称视角看节点信息
        boolean firstPersonPerspective = StringUtils.isNotBlank(staffId);

        Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));
        Function<String, List<? extends NodeRecordInfo>> getNodeRecordsByNodeId = (nodeId_) -> {
            // 返回不为null的节点处理记录列表
            return groupedNodeRecords.get(nodeId_) != null ? groupedNodeRecords.get(nodeId_) : new ArrayList<>();
        };
        List<? extends NodeRecordInfo> curNodeRecords = getNodeRecordsByNodeId.apply(nodeId);
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeId);

        // 判断当前人是否满足节点配置角色
        boolean hasPowerToDispose = runtimeRoleList == null || runtimeRoleList.size() == 0 || FlowUtil.hasPowerToDispose(
            FlowUtil.getPrePairNodeInfoIfAfterPairNode(nodeId, flowInfo),
            runtimeRoleList,
            flowInfo.getFlowTypeCode()
        );

        // 如果节点是第一人称视角，并且存在刷卡的可能性（节点没有过期或者可补卡，并且有刷卡权限），则需要根据刷卡记录动态判断节点的当前人的完成状态
        if (firstPersonPerspective && hasPowerToDispose &&
            (!nodePasted(nodeId, flowInfo, curFlowRunNodeRecords) ||// 节点没有过期
                FlowUtil.getPrePairNodeInfoIfAfterPairNode(nodeId, flowInfo).getDisposeAfterSkip()// 节点可补卡
            )
        ) {
            List<? extends NodeRecordInfo> curNormalNodeRecords = curNodeRecords.stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList());
            // 如果没有任何正常打卡记录，则一定没有完成
            if (curNormalNodeRecords.size() == 0) {
                return false;
            }
            if (pairNodeIndex == null) {
                // 如果是普通节点，那么节点存在完成记录或者这个人打过卡，则完成
                return nodeFinished(curNodeRecords) || FlowUtil.disposed(curNormalNodeRecords, staffId);
            } else if (pairNodeIndex == 0) {// 如果是成对前一个节点
                // 对于成对前一个节点，如果有权限，是否完成只看当前人是否处理过当前节点，不管整体看是否完成，因为此时节点尚未过期，仍然有使节点暂时无法完成的可能性
                return curNormalNodeRecords.stream().anyMatch(v -> v.getWorkerId().equals(staffId));
            } else if (pairNodeIndex == 1) {// 如果是成对后一个节点
                // 如果有权限，则忽略此前的完成记录，通过成对前后节点的处理记录信息判断当前人是否完成当前节点（即成对后一个节点）
                // 获取成对前一个节点的id
                PairNodeInfo pairNodeInfo = flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId);
                String startPairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, pairNodeInfo, 0);
                // 获取成对前一个节点的处理记录
                List<? extends NodeRecordInfo> startPairNodeNormalNodeRecords = getNodeRecordsByNodeId.apply(startPairNodeId).stream().filter(
                    FlowUtil::normalTypeNodeRecordFilter
                ).collect(Collectors.toList());
                // 如果不存在当前人的前一个节点的处理记录则判定当前人未完成当前节点
                if (startPairNodeNormalNodeRecords.stream().noneMatch(v -> v.getWorkerId().equals(staffId))) {
                    return false;
                } else {
                    // 当前人成对前一个节点的刷卡记录信息
                    Set<List<String>> startPairNodeRecordWorkerIdSet = startPairNodeNormalNodeRecords.stream().filter(v -> v.getWorkerId().equals(staffId)).map(
                        v -> Arrays.asList(v.getWorkerId(), v.getRoleId())
                    ).collect(Collectors.toSet());
                    // 当前人当前节点（即成对后一个节点）的刷卡记录信息
                    Set<List<String>> afterPairNodeRecordWorkerIdSet = curNormalNodeRecords.stream().filter(v -> v.getWorkerId().equals(staffId)).map(
                        v -> Arrays.asList(v.getWorkerId(), v.getRoleId())
                    ).collect(Collectors.toSet());
                    // 两个节点的记录完全对应时，看做当前人完成此节点（成对后一个节点）
                    return startPairNodeRecordWorkerIdSet.equals(afterPairNodeRecordWorkerIdSet);
                }
            } else {
                throw new RuntimeException("意外的成对节点索引坐标");
            }
        } else {// 如果是第三人称视角看节点信息
            boolean hasNodeFinishedRecord = nodeFinished(curNodeRecords);
            // 对于普通节点和成对后一个节点，看是否存在完成记录即可
            if (pairNodeIndex == null || pairNodeIndex == 1) {
                return hasNodeFinishedRecord;
            } else if (pairNodeIndex == 0) {
                if (hasNodeFinishedRecord) {
                    return true;
                } else {
                    // 如果是前一个节点，且不存在完成记录，则看刷卡记录数量是否满足配置要求即可
                    List<? extends NodeRecordInfo> curNormalNodeRecords = curNodeRecords.stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList());
                    return checkNodeFinishedByNodeConfig(flowInfoAnalysis.getNodeMap().get(nodeId), curNormalNodeRecords);
                }
            } else {
                throw new RuntimeException("意外的成对节点索引坐标");
            }
        }

    }

    /**
     * 从结构上看，节点是否过期
     *
     * @param nodeId                节点id
     * @param flowInfo              流程信息
     * @param curFlowRunNodeRecords 当前流程运行所有节点记录
     * @return
     */
    public static boolean nodePasted(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);

        // 成对节点下标
        Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeId);

        if (pairNodeIndex == null || pairNodeIndex == 1) {
            // 普通节点或者成对节点中的后一个,当前节点的下游节点存在已刷卡节点或已跳过节点时,节点过期
            Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));
            return flowInfoAnalysis.getNextNodes(nodeId).stream().anyMatch(node -> {
                List<? extends NodeRecordInfo> nodeRecords = groupedNodeRecords.get(node.getId());
                return (nodeRecords != null && nodeRecords.stream().anyMatch(FlowUtil::normalTypeNodeRecordFilter)) || nodeSkipped(nodeRecords);
            });
        } else if (pairNodeIndex == 0) {
            // 成对节点中的前一个, 则跟随后一个节点过期, 得到成对节点的后一个节点的下游节点
            String pairNextNodeId = getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId), 1);
            return nodePasted(pairNextNodeId, flowInfo, curFlowRunNodeRecords);
        } else {
            throw new RuntimeException("意外的成对节点下标");
        }
    }

    public static String getPairNodeIdByIndex(FlowInfo flowInfo, String pairNodeInfoId, int index) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        return getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByInfoId(pairNodeInfoId), index);
    }

    // 获取指定index的成对节点
    public static String getPairNodeIdByIndex(FlowInfo flowInfo, PairNodeInfo pairNodeInfo, int index) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        return Arrays.stream(pairNodeInfo.getNodeIds()).filter(v -> flowInfoAnalysis.getPairNodeIndexForSort(v) == index).findAny().orElseGet(() -> {
            throw new RuntimeException("意外的成对节点下标");
        });
    }

    // /**
    //  * 节点是否存在已完成
    //  *
    //  * @param nextNodeIdList        下游节点
    //  * @param curFlowRunNodeRecords 本流程下节点记录信息
    //  */
    // public static boolean nodeRecordHasNodeFinish(List<String> nextNodeIdList, List<? extends NodeRecord> curFlowRunNodeRecords) {
    //     return curFlowRunNodeRecords.stream().anyMatch(n -> nextNodeIdList.contains(n.getNodeId()) && n.getType().equals(NodeRecordEnum.NODE_FINISH.getValue()));
    // }

    /**
     * 根据节点记录状态判断当前节点是否完成
     *
     * @param nodeId                节点id
     * @param flowInfo              流程配置信息
     * @param curFlowRunNodeRecords 当前流程运行的节点记录信息
     */
    public static boolean checkNodeFinished(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords) {
        // 解析流程信息
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);

        // 过滤掉系统自动记录的节点记录，然后按节点id分组
        Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().filter(
            FlowUtil::normalTypeNodeRecordFilter
        ).collect(Collectors.groupingBy(NodeRecord::getNodeId));

        // 如果是普通节点
        if (!flowInfoAnalysis.isPairNode(nodeId)) {
            // 获取当前节点信息
            NodeInfo nodeInfo = flowInfoAnalysis.getNodeMap().get(nodeId);
            // 当前节点记录
            List<? extends NodeRecordInfo> curNodeRecords = groupedNodeRecords.get(nodeId);
            return checkNodeFinishedByNodeConfig(nodeInfo, curNodeRecords);
        } else {
            PairNodeInfo pairNodeInfo = flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId);
            String startPairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, pairNodeInfo, 0);
            String endPairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, pairNodeInfo, 1);
            List<? extends NodeRecordInfo> startPairNodeRecords = groupedNodeRecords.get(startPairNodeId);
            List<? extends NodeRecordInfo> endPairNodeRecords = groupedNodeRecords.get(endPairNodeId);
            if (startPairNodeRecords == null || endPairNodeRecords == null) {
                return false;
            }

            Set<List<String>> startPairNodeRecordWorkerIdSet = startPairNodeRecords.stream().map(
                v -> Arrays.asList(v.getWorkerId(), v.getRoleId())
            ).collect(Collectors.toSet());
            Set<List<String>> afterPairNodeRecordWorkerIdSet = endPairNodeRecords.stream().map(
                v -> Arrays.asList(v.getWorkerId(), v.getRoleId())
            ).collect(Collectors.toSet());
            // 如果成对节点记录不完全对应，则判断为不完成
            if (!startPairNodeRecordWorkerIdSet.equals(afterPairNodeRecordWorkerIdSet)) {
                return false;
            }
            return checkNodeFinishedByNodeConfig(flowInfoAnalysis.getNodeMap().get(startPairNodeId), startPairNodeRecords);
        }

    }

    /**
     * 根据节点配置和当前节点记录判断当前节点是否完成
     *
     * @param nodeInfo             节点信息
     * @param curNormalNodeRecords 当前节点记录信息（要求被FlowUtil.normalTypeNodeRecordFilter过滤）
     */
    public static boolean checkNodeFinishedByNodeConfig(NodeInfo nodeInfo, List<? extends NodeRecordInfo> curNormalNodeRecords) {
        // 如果没有打卡记录，返回false
        if (curNormalNodeRecords == null || curNormalNodeRecords.size() == 0) {
            return false;
        }
        // 如果打卡总数小于设定数量，返回false
        if (nodeInfo.getMinDisposeNum() != null && curNormalNodeRecords.size() < nodeInfo.getMinDisposeNum()) {
            return false;
        }
        // 如果角色实际打卡数小于分配的最小打卡数量，返回false
        if (nodeInfo.getRoleConfigs() != null && nodeInfo.getRoleConfigs().size() > 0) {
            Map<String, Long> roleCountMap = curNormalNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getRoleId, Collectors.counting()));
            return nodeInfo.getRoleConfigs().stream().noneMatch(
                nodeRoleConfig -> {
                    Integer minNum = nodeRoleConfig.getMinNum();
                    Long count = roleCountMap.get(nodeRoleConfig.getRoleId());
                    if (count == null) {
                        count = 0L;
                    }
                    return minNum != null && minNum > count;
                }
            );
        }
        return true;
    }

    public static List<NodeInfo> getSortedNodes(FlowInfo flowInfo) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        List<NodeInfo> sortedNodes = new ArrayList<>();
        // 获取主干信息
        FlowInfoAnalysis.FlowSectionInfo trunkSectionInfo = flowInfoAnalysis.getTrunkSectionInfo();
        // 遍历主干节点
        for (String sortedTrunkNodeId : trunkSectionInfo.getSortedNodeIds()) {
            sortedNodes.add(flowInfoAnalysis.getNodeMap().get(sortedTrunkNodeId));
            sortedNodes.addAll(getBranchNodesByTrunkNodeId(flowInfoAnalysis, trunkSectionInfo, sortedTrunkNodeId));
            // List<FlowInfoAnalysis.FlowSectionInfo> flowSectionInfoList = flowInfoAnalysis.getSectionInfoListByStartNodeId(trunkSectionInfo.getPreviousNodeId(sortedTrunkNodeId));
            // flowSectionInfoList.sort((a, b) -> {
            //     int returnNum;
            //
            //     NodeInfo endNodeA = a.getToEndNode();
            //     NodeInfo endNodeB = b.getToEndNode();
            //     String endNodeIdA = endNodeA == null ? null : endNodeA.getId();
            //     String endNodeIdB = endNodeB == null ? null : endNodeB.getId();
            //     if (endNodeIdA != null && endNodeIdB != null) {
            //         returnNum = trunkSectionInfo.getNodeIndex(endNodeIdA) - trunkSectionInfo.getNodeIndex(endNodeIdB);
            //     } else if (endNodeIdB != null) {
            //         returnNum = 1;
            //     } else if (endNodeIdA != null) {
            //         returnNum = -1;
            //     } else {
            //         returnNum = 0;
            //     }
            //
            //     if (returnNum == 0) {
            //         Map<String, FlowParallelSection> flowParallelSectionMap = flowInfoAnalysis.getFlowParallelSectionMap();
            //         returnNum = flowParallelSectionMap.get(a.getFlowSection().getId()).getSort() - flowParallelSectionMap.get(b.getFlowSection().getId()).getSort();
            //     }
            //
            //     return returnNum;
            // });
            // for (FlowInfoAnalysis.FlowSectionInfo flowSectionInfo : flowSectionInfoList) {
            //     sortedNodes.addAll(flowSectionInfo.getSortedNodeIds().stream().map(v -> flowInfoAnalysis.getNodeMap().get(v)).collect(Collectors.toList()));
            // }
        }
        return sortedNodes.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 获取主干节点下的分支节点
     */
    public static List<NodeInfo> getBranchNodesByTrunkNodeId(FlowInfoAnalysis flowInfoAnalysis, FlowInfoAnalysis.FlowSectionInfo trunkSectionInfo, String sortedTrunkNodeId) {
        List<FlowInfoAnalysis.FlowSectionInfo> flowSectionInfoList = flowInfoAnalysis.getSectionInfoListByStartNodeId(trunkSectionInfo.getPreviousNodeId(sortedTrunkNodeId));
        List<NodeInfo> sortedNodes = new ArrayList<>();
        flowSectionInfoList.sort((a, b) -> {
            int returnNum;

            NodeInfo endNodeA = a.getToEndNode();
            NodeInfo endNodeB = b.getToEndNode();
            String endNodeIdA = endNodeA == null ? null : endNodeA.getId();
            String endNodeIdB = endNodeB == null ? null : endNodeB.getId();
            if (endNodeIdA != null && endNodeIdB != null) {
                returnNum = trunkSectionInfo.getNodeIndex(endNodeIdA) - trunkSectionInfo.getNodeIndex(endNodeIdB);
            } else if (endNodeIdB != null) {
                returnNum = 1;
            } else if (endNodeIdA != null) {
                returnNum = -1;
            } else {
                returnNum = 0;
            }

            if (returnNum == 0) {
                Map<String, FlowParallelSection> flowParallelSectionMap = flowInfoAnalysis.getFlowParallelSectionMap();
                returnNum = flowParallelSectionMap.get(a.getFlowSection().getId()).getSort() - flowParallelSectionMap.get(b.getFlowSection().getId()).getSort();
            }

            return returnNum;
        });
        for (FlowInfoAnalysis.FlowSectionInfo flowSectionInfo : flowSectionInfoList) {
            sortedNodes.addAll(flowSectionInfo.getSortedNodeIds().stream().map(v -> flowInfoAnalysis.getNodeMap().get(v)).collect(Collectors.toList()));
        }
        return sortedNodes;
    }

    @Data
    @Builder
    public static class MoveNodeInfo {
        private String sourceNodeId;
        private String targetNodeId;
    }

    public static List<MoveNodeInfo> getMoveNodeInfos(FlowInfo sourceFlowInfo, FlowInfo targetFlowInfo) {
        //原流程配置信息
        FlowInfoAnalysis sourceFlowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(sourceFlowInfo);
        //新流程配置信息
        FlowInfoAnalysis targetFlowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(targetFlowInfo);


        // 获取原节点主干信息
        FlowInfoAnalysis.FlowSectionInfo sourceTrunkSectionInfo = sourceFlowInfoAnalysis.getTrunkSectionInfo();
        // 获取新节点主干信息
        FlowInfoAnalysis.FlowSectionInfo targetTrunkSectionInfo = targetFlowInfoAnalysis.getTrunkSectionInfo();


        //原流程节点信息
        Map<String, NodeInfo> sourceNodeInfoById = CommonUtils.collectionToMap(sourceFlowInfo.getNodes(), NodeInfo::getId);
        //新流程节点信息
        Map<String, NodeInfo> targetNodeInfoById = CommonUtils.collectionToMap(targetFlowInfo.getNodes(), NodeInfo::getId);

        List<MoveNodeInfo> moveNodeInfoList = new ArrayList<>();

        Boolean needBreak = false;
        //遍历原流程主干节点
        for (int nodeIndex = 0; nodeIndex < sourceTrunkSectionInfo.getSortedNodeIds().size(); nodeIndex++) {
            if (needBreak) {
                break;
            }
            if (targetTrunkSectionInfo.getSortedNodeIds().size() >= nodeIndex) {
                //得到主干节点信息
                String sourceNodeId = sourceTrunkSectionInfo.getSortedNodeIds().get(nodeIndex);
                String targetNodeId = targetTrunkSectionInfo.getSortedNodeIds().get(nodeIndex);
                NodeInfo sourceNodeInfo = sourceNodeInfoById.get(sourceNodeId);
                NodeInfo targetNodeInfo = targetNodeInfoById.get(targetNodeId);
                //主干节点是否满足
                if (sourceNodeInfo != null && targetNodeInfo != null && sourceNodeInfo.getName().equals(targetNodeInfo.getName())) {
                    moveNodeInfoList.add(MoveNodeInfo.builder().sourceNodeId(sourceNodeId).targetNodeId(targetNodeId).build());

                    //原流程本节点下分支节点根据名称分组
                    List<NodeInfo> sourceBranchNodesByNodeId = getBranchNodesByTrunkNodeId(sourceFlowInfoAnalysis, sourceTrunkSectionInfo, sourceNodeId);
                    Map<String, List<NodeInfo>> groupedSourceBranchNodesByName = sourceBranchNodesByNodeId.stream().collect(
                        Collectors.groupingBy(NodeInfo::getName)
                    );
                    //新流程本节点下分支节点根据名称分组
                    List<NodeInfo> targetBranchNodesByNodeId = getBranchNodesByTrunkNodeId(targetFlowInfoAnalysis, targetTrunkSectionInfo, targetNodeId);
                    Map<String, List<NodeInfo>> groupedTargetBranchNodesByName = targetBranchNodesByNodeId.stream().collect(
                        Collectors.groupingBy(NodeInfo::getName)
                    );

                    for (String sourceNodeName : groupedTargetBranchNodesByName.keySet()) {
                        List<NodeInfo> sourceNodeInfoListByNodeName = groupedSourceBranchNodesByName.get(sourceNodeName);
                        List<NodeInfo> targetNodeInfoListByNodeName = groupedTargetBranchNodesByName.get(sourceNodeName);
                        if (sourceNodeInfoListByNodeName == null) { //新流程有分支节点而原流程没有,找一下个分支节点信息
                            needBreak = true;
                            continue;
                        }
                        //已经被新流程匹配到的原节点id
                        List<String> matchedSourceNodeId = new ArrayList<>();
                        //已经匹配到原节点的新节点id
                        List<String> matchedTargetNodeId = new ArrayList<>();
                        //同名称的分支节点存在,开始比较
                        for (NodeInfo targetBranchNodeInfo : targetNodeInfoListByNodeName) {//新流程本节点下的分支节点
                            //名字相同的原节点分支
                            List<NodeInfo> sourceBranchNodesByName = groupedSourceBranchNodesByName.get(targetBranchNodeInfo.getName());
                            for (NodeInfo sourceBranchNode : sourceBranchNodesByName) {
                                if (sourceBranchNode.getName().equals(targetBranchNodeInfo.getName()) && !matchedSourceNodeId.contains(sourceBranchNode.getId())) {
                                    matchedSourceNodeId.add(sourceBranchNode.getId());
                                    matchedTargetNodeId.add(targetBranchNodeInfo.getId());
                                    moveNodeInfoList.add(MoveNodeInfo.builder().sourceNodeId(sourceBranchNode.getId()).targetNodeId(targetBranchNodeInfo.getId()).build());
                                    break;
                                }
                            }
                        }
                        if (matchedTargetNodeId.size() != targetNodeInfoListByNodeName.size()) {
                            needBreak = true;
                        }
                    }
                } else {
                    break;
                }
            }
        }
        return moveNodeInfoList;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean allConditionEquals(FlowInfo flowInfoA, FlowInfo flowInfoB) {
        FlowConfigSingleConditionEnum[] flowConfigSingleConditionEnums = FlowConfigSingleConditionEnum.class.getEnumConstants();
        return CommonUtils.every(flowConfigSingleConditionEnums, flowConfigSingleConditionEnum -> {
            FlowConfigSingleConditionBase condition = flowConfigSingleConditionEnum.getCondition();
            Object v1 = condition.from(flowInfoA);
            Object v2 = condition.from(flowInfoB);
            return condition.equals(v1, v2);
        });
    }

    private static Predicate<FlowInfo> getFlowInfoPredicateByTrainInfo(String trainType, String trainTemplate, String trainsetId) {
        Predicate<TrainsetBaseInfo> trainsetPredicate;
        TrainInfoConditionLevelEnum curTrainInfoMinLevel;
        String curTrainInfoValue;
        if (StringUtils.hasText(trainsetId)) {
            curTrainInfoMinLevel = TrainInfoConditionLevelEnum.TRAINSET_LEVEL;
            curTrainInfoValue = trainsetId;
            trainsetPredicate = (trainsetBaseInfo) -> trainsetBaseInfo.getTrainsetid().equals(trainsetId);
        } else if (StringUtils.hasText(trainTemplate)) {
            curTrainInfoMinLevel = TrainInfoConditionLevelEnum.TRAIN_TEMPLATE_LEVEL;
            curTrainInfoValue = trainTemplate;
            trainsetPredicate = (trainsetBaseInfo) -> trainsetBaseInfo.getTraintempid().equals(trainTemplate);
        } else if (StringUtils.hasText(trainType)) {
            curTrainInfoMinLevel = TrainInfoConditionLevelEnum.TRAIN_TYPE_LEVEL;
            curTrainInfoValue = trainType;
            trainsetPredicate = (trainsetBaseInfo) -> trainsetBaseInfo.getTraintype().equals(trainType);
        } else {
            return v -> true;
        }
        return flowInfo -> {
            //得到车组信息条件
            TrainConditionValue trainConditionValue = FlowConfigSingleConditionEnum.TRAIN.getCondition(TrainConditionValue.class).from(flowInfo);
            //是否是排除条件
            boolean isExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(trainConditionValue);
            //得到车组信息最小层级
            TrainInfoConditionLevelEnum flowConditionMinLevel = FlowConfigSingleConditionUtil.getMinLevel(trainConditionValue);
            if (isExcludeCondition || curTrainInfoMinLevel != flowConditionMinLevel) {// (排除条件) 或者 (给定条件与流程的条件的级别不一致) 则只能查询所有车组进行判断
                List<TrainsetBaseInfo> allTrainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
                List<TrainsetBaseInfo> filteredTrainsetBaseInfos = FlowConfigSingleConditionUtil.filterByTrainConditionValue(allTrainsetBaseInfos, trainConditionValue);
                return filteredTrainsetBaseInfos.parallelStream().anyMatch(trainsetPredicate);
            } else {// 如果级别一致且流程车组条件为包含条件，则可直接根据流程条件判断当前传入车组信息是否满足流程
                Set<String> flowConditionValues = flowConditionMinLevel.getValue(trainConditionValue);
                return flowConditionValues.contains(curTrainInfoValue);
            }
        };
    }

    /**
     * 根据车组信息过滤流程配置列表
     *
     * @param flowInfoList
     * @param trainType
     * @param trainTemplate
     * @param trainsetId
     * @return
     */
    public static List<FlowInfo> filterFlowInfoListByTrainInfo(List<FlowInfo> flowInfoList, String trainType, String trainTemplate, String trainsetId) {
        if (!StringUtils.hasText(trainType) && !StringUtils.hasText(trainTemplate) && !StringUtils.hasText(trainsetId)) {
            return new ArrayList<>(flowInfoList);
        }
        return flowInfoList.stream().filter(getFlowInfoPredicateByTrainInfo(trainType, trainTemplate, trainsetId)).collect(Collectors.toList());
    }

    /**
     * 判断流程配置是否满足车组信息
     *
     * @param flowInfo
     * @param trainType
     * @param trainTemplate
     * @param trainsetId
     * @return
     */
    public static boolean matchFlowInfoListByTrainInfo(FlowInfo flowInfo, String trainType, String trainTemplate, String trainsetId) {
        if (!StringUtils.hasText(trainType) && !StringUtils.hasText(trainTemplate) && !StringUtils.hasText(trainsetId)) {
            return true;
        }
        return getFlowInfoPredicateByTrainInfo(trainType, trainTemplate, trainsetId).test(flowInfo);
    }

    /**
     * 排除状态反转
     *
     * @param flowInfo
     * @author 高晗
     * @modified 张天可 (移动到了工具类中)
     */
    public static void convertToNotExclude(FlowInfo flowInfo) {
        Boolean type = flowInfo.getTrainsetIds().size() > 0 ? flowInfo.getTrainsetIdExclude() : flowInfo.getTrainTemplates().size() > 0 ? flowInfo.getTrainTemplateExclude() : flowInfo.getTrainTypeExclude();
        if (type != null && type) {
            try {
                List<TrainsetBaseInfo> trainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
                Set<String> trainTypes = new HashSet<>();
                Set<String> trainTemplates = new HashSet<>();
                Set<String> trainsetIds = new HashSet<>();
                //判断已选择最后一项
                for (TrainsetBaseInfo trainsetBaseInfo : trainsetBaseInfos) {
                    //A,B,C 配置排除A,B  C进入
                    if (!flowInfo.getTrainTypes().contains(trainsetBaseInfo.getTraintype())) {
                        //没有配置排除c完全加入
                        trainTypes.add(trainsetBaseInfo.getTraintype());
                        trainTemplates.add(trainsetBaseInfo.getTraintempid());
                        trainsetIds.add(trainsetBaseInfo.getTrainsetid());
                    } else {
                        //A1 A2 A3 配置排除了A1, A2 A3要加入
                        if (!flowInfo.getTrainTemplates().contains(trainsetBaseInfo.getTraintempid())) {
                            trainTypes.add(trainsetBaseInfo.getTraintype());
                            trainTemplates.add(trainsetBaseInfo.getTraintempid());
                            trainsetIds.add(trainsetBaseInfo.getTrainsetid());
                        } else {
                            //车组 A11 A12 A13 配置排除了A11, A12 A13加入
                            if (!flowInfo.getTrainsetIds().contains(trainsetBaseInfo.getTrainsetid())) {
                                trainTypes.add(trainsetBaseInfo.getTraintype());
                                trainTemplates.add(trainsetBaseInfo.getTraintempid());
                                trainsetIds.add(trainsetBaseInfo.getTrainsetid());
                            }
                        }
                    }
                }

                flowInfo.setTrainsetIds(new ArrayList<>(trainsetIds));
                flowInfo.setTrainTemplates(new ArrayList<>(trainTemplates));
                flowInfo.setTrainTypes(new ArrayList<>(trainTypes));
            } catch (Exception e) {
                throw new RuntimeException("排除转换失败", e);
            }
        }
    }

    /**
     * 节点是否满足打卡最小间隔
     *
     * @param nodeId                节点id
     * @param flowInfo              流程配置信息
     * @param curFlowRunNodeRecords 节点记录信息
     * @author 高晗
     */
    public static MeetMinIntervalRestrict getMeetMinIntervalRestrict(String nodeId, FlowInfo flowInfo, List<? extends NodeRecordInfo> curFlowRunNodeRecords) {
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
        // 获取主干信息
        FlowInfoAnalysis.FlowSectionInfo trunkSectionInfo = flowInfoAnalysis.getTrunkSectionInfo();
        //获取到上一个节点
        String lastNodeId = null;
        //上一个节点和是上一个节点的并行区段
        List<String> lastNodeIds = new ArrayList<>();
        for (String sortedNodeId : trunkSectionInfo.getSortedNodeIds()) {
            if (lastNodeId != null && sortedNodeId.equals(nodeId)) {
                lastNodeIds.add(lastNodeId);
                break;
            } else {
                lastNodeId = sortedNodeId;
            }
        }

        //上一个节点的并行区段节点
        List<FlowInfoAnalysis.FlowSectionInfo> flowSectionInfoList = flowInfoAnalysis.getSectionInfoListByStartNodeId(trunkSectionInfo.getPreviousNodeId(lastNodeId));
        flowSectionInfoList.forEach(v -> {
            lastNodeIds.addAll(v.getSortedNodeIds());
        });

        MeetMinIntervalRestrict meetMinIntervalRestrict = new MeetMinIntervalRestrict();
        meetMinIntervalRestrict.setResult(false);
        meetMinIntervalRestrict.setMinIntervalRestrictMessage("");
        Integer minIntervalRestrict = flowInfo.getNodes().stream().filter(v -> v.getId().equals(nodeId)).collect(Collectors.toList()).get(0).getMinIntervalRestrict();

        if (minIntervalRestrict != null) {
            lastNodeIds.forEach(v -> {
                List<NodeRecordInfo> nodeRecordInfoByNodeId = curFlowRunNodeRecords.stream().filter(nodeRecordInfo -> nodeRecordInfo.getNodeId().equals(v)).collect(Collectors.toList());
                if (nodeRecordInfoByNodeId.size() > 0) {
                    nodeRecordInfoByNodeId.sort(Comparator.comparing(NodeRecordInfo::getRecordTime));
                    Date newDate = new Date();
                    Date recordTime = nodeRecordInfoByNodeId.get(nodeRecordInfoByNodeId.size() - 1).getRecordTime();
                    int differDate = (int) ((newDate.getTime() - recordTime.getTime())) / (1000) / 60;
                    if (differDate < minIntervalRestrict) {
                        meetMinIntervalRestrict.setResult(true);
                        meetMinIntervalRestrict.setMinIntervalRestrictMessage("当前节点配置距上一节点最小间隔:" + minIntervalRestrict + "分钟,现已间隔:" + differDate + "分钟是否继续");
                    }
                }
            });
        }
        return meetMinIntervalRestrict;
    }

    /**
     * 节点是否满足打卡最小间隔
     */
    @Data
    public static class MeetMinIntervalRestrict {
        private Boolean result;
        private String minIntervalRestrictMessage;
    }

    /**
     * 获取上个班次的日计划id
     *
     * @param dayPlanId
     * @return
     */
    public static String getLastDayPlanId(String dayPlanId) {
        String lastDayPlanId;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        if (dayPlanId.substring(dayPlanId.length() - 2, dayPlanId.length()).equals("00")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            lastDayPlanId = simpleDateFormat.format(calendar.getTime()) + "-01";
        } else {
            lastDayPlanId = simpleDateFormat.format(date) + "-00";
        }
        return lastDayPlanId;
    }

    /**
     * 查询当前班次的前x个班次
     *
     * @param dayPlanId
     * @param number
     * @return
     */
    public static List<String> getQueryDayPlanIds(String dayPlanId, int number) {
        List<String> dayPlanIds = new ArrayList<>();
        Date date = new Date();
        LocalDate localDate = DateTimeUtil.asLocalDate(date);

        int singular = number % 2;
        int dataNumber = number / 2;

        boolean isNight = dayPlanId.startsWith("01", dayPlanId.length() - 2);
        if (isNight) {
            dayPlanIds.add(localDate + "-00");
            if (singular == 0) {
                dataNumber = dataNumber - 1;
            }
        }

        for (int i = 1; i <= dataNumber; i++) {
            localDate = localDate.minusDays(1);
            dayPlanIds.add(localDate + "-01");
            dayPlanIds.add(localDate + "-00");
        }

        localDate = localDate.minusDays(1);
        if (!isNight) {
            if (singular != 0) {
                dayPlanIds.add(localDate + "-01");
            }
        } else {
            if (singular == 0) {
                dayPlanIds.add(localDate + "-00");
            }
        }
        return dayPlanIds;
    }
}
