package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSource;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.FlowConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ExtraFlowTypeMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ExtraFlowTypePacketMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowExtraInfoMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlow;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ConditionWithValues;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo.NodeExtraInfoTypeEnum;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.neParam;

/**
 * <p>
 * 流程表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Transactional
@Service
public class FlowServiceImpl extends ServiceImpl<FlowMapper, Flow> implements IFlowService {

    @Resource
    FlowMapper flowMapper;

    @Resource
    IConditionService conditionService;

    @Resource
    INodeVectorService nodeVectorService;

    @Resource
    INodeService nodeService;

    @Resource
    IFlowExtraInfoService flowExtraInfoService;

    @Resource
    IConditionValueService conditionValueService;

    @Resource
    INodeVectorExtraInfoService nodeVectorExtraInfoService;

    @Resource
    INodeExtraInfoService nodeExtraInfoService;

    @Resource
    private IRemoteService remoteService;

    @Resource
    private ExtraFlowTypePacketMapper extraFlowTypePacketMapper;

    @Resource
    private ExtraFlowTypeMapper extraFlowTypeMapper;

    @Resource
    FlowExtraInfoMapper flowExtraInfoMapper;

    @Autowired(required = false)
    private RepairWorkflowConfigMqSource repairWorkflowConfigSource;

    protected static final Logger logger = LoggerFactory.getLogger(FlowServiceImpl.class);

    private List<FlowInfo> getFlowInfoList(QueryFlowModel queryFlowModel) {
        //查流程
        List<FlowWithExtraInfo> flowWithExtraInfos = this.getFlowWithExtraInfoList(queryFlowModel);
        Map<String, String> flowConditionIdMap = new HashMap<>();
        Set<String> valueList = new HashSet<>();
        //得到流程额外信息type=CONDITION的value
        for (FlowWithExtraInfo flowWithExtraInfo : flowWithExtraInfos) {
            for (FlowExtraInfo flowExtraInfo : flowWithExtraInfo.getFlowExtraInfoList()) {
                if (flowExtraInfo.getType().equals("CONDITION")) {
                    valueList.add(flowExtraInfo.getValue());
                    //流程表id 条件表id
                    flowConditionIdMap.put(flowWithExtraInfo.getId(), flowExtraInfo.getValue());
                }
            }
        }
        List<FlowInfo> flowInfoList = new ArrayList<>();
        if (flowWithExtraInfos.size() > 0) {
            Map<String, ConditionWithValues> conditionMap = new HashMap<>();
            //查条件
            if (valueList.size() > 0) {
                List<ConditionWithValues> conditionWithValues = conditionService.getConditionWithValues(new ArrayList<>(valueList));
                for (ConditionWithValues conditionWithValue : conditionWithValues) {
                    //条件表id
                    conditionMap.put(conditionWithValue.getId(), conditionWithValue);
                }
            }

            flowInfoList = parseFlowInfoList(flowWithExtraInfos, flowConditionIdMap, conditionMap);
        }
        return flowInfoList;

    }


    @Override
    public List<FlowInfo> getFlowInfoList(QueryFlowModelGeneral queryFlowModelGeneral) {
        QueryFlowModel queryFlowModel = new QueryFlowModel();
        BeanUtils.copyProperties(queryFlowModelGeneral, queryFlowModel);
        return this.getFlowInfoList(queryFlowModel);
    }


    /**
     * 解析转换条件表数据
     */
    public List<FlowInfo> parseFlowInfoList(List<FlowWithExtraInfo> flowWithExtraInfos, Map<String, String> flowConditionIdMap, Map<String, ConditionWithValues> conditionMap) {
        return flowWithExtraInfos.stream().map(flowWithExtraInfo -> {
            //解析额外流程信息
            FlowInfo flowInfo = parseFlowExtraInfoList(flowWithExtraInfo);

            //得到条件表id
            String conditionId = flowConditionIdMap.get(flowWithExtraInfo.getId());
            flowInfo.setConditionId(conditionId);

            ConditionWithValues condition = conditionMap.get(conditionId);
            List<String> trainsetIds = new ArrayList<>();
            List<String> trainTemplates = new ArrayList<>();
            List<String> trainTypes = new ArrayList<>();
            List<String> keyWords = new ArrayList<>();
            if (condition != null) {
                for (ConditionValue conditionValue : condition.getConditionValues()) {
                    String value = conditionValue.getValue();
                    switch (conditionValue.getType()) {
                        //车型
                        case "TRAIN_TYPE":
                            trainTypes.add(conditionValue.getValue());
                            break;
                        //车型排除条件值存储
                        case "TRAIN_TYPE_EXCLUDE":
                            flowInfo.setTrainTypeExclude(FlowUtil.stringToBoolean(value, false));
                            break;
                        //批次
                        case "TRAIN_TEMPLATE":
                            trainTemplates.add(value);
                            break;
                        //批次排除条件值存储
                        case "TRAIN_TEMPLATE_EXCLUDE":
                            flowInfo.setTrainTemplateExclude(FlowUtil.stringToBoolean(value, false));
                            break;
                        //车组
                        case "TRAIN_SET_ID":
                            trainsetIds.add(value);
                            break;
                        //车组排除条件值存储
                        case "TRAIN_SET_ID_EXCLUDE":
                            flowInfo.setTrainsetIdExclude(FlowUtil.stringToBoolean(value, false));
                            break;
                        //关键字条件值存储
                        case "KEY_WORD":
                            keyWords.add(value);
                            break;
                        case "REPAIR_TYPE":
                            flowInfo.setRepairType(value);
                            break;
                    }
                }
            }
            flowInfo.setKeyWords(keyWords);
            flowInfo.setTrainTypes(trainTypes);
            flowInfo.setTrainTemplates(trainTemplates);
            flowInfo.setTrainsetIds(trainsetIds);
            flowInfo = parseNodes(flowInfo);
            return flowInfo;
        }).collect(Collectors.toList());

    }


    // TODO 待优化，应该一次性把所有的子表信息查询出来，而不是一个流程查询一遍
    public FlowInfo parseNodes(FlowInfo flowInfo) {
        //节点向量信息
        List<NodeVectorWithExtraInfo> nodeVectorWithExtraInfos = nodeVectorService.getNodeVectorWithInfoList(flowInfo.getId());
        flowInfo.setNodeVectors(nodeVectorWithExtraInfos);

        //节点信息
        List<NodeWithExtraInfo> nodeWithExtraInfos = nodeService.getNodeWithExtraInfoList(flowInfo.getId());
        flowInfo.setNodes(FlowUtil.parseNodeInfo(nodeWithExtraInfos));

        //子流程
        List<SubflowInfo> subflowInfoList = new ArrayList<>();
        for (NodeWithExtraInfo nodeWithExtraInfo : nodeWithExtraInfos) {
            if (StringUtils.isNotBlank(nodeWithExtraInfo.getChildFlowId())) {
                SubflowInfo subflowInfo = new SubflowInfo();
                Flow flow = this.getFlowByFlowId(nodeWithExtraInfo.getChildFlowId());
                subflowInfo.setId(nodeWithExtraInfo.getChildFlowId());
                BeanUtils.copyProperties(subflowInfo, flow);

                //节点信息
                List<NodeWithExtraInfo> subNodeWithExtraInfos = nodeService.getNodeWithExtraInfoList(nodeWithExtraInfo.getChildFlowId());
                subflowInfo.setNodes(FlowUtil.parseNodeInfo(subNodeWithExtraInfos));
                //节点向量
                List<NodeVector> nodeVectorList = nodeVectorService.getNodeVector(nodeWithExtraInfo.getChildFlowId());
                subflowInfo.setNodeVectors(nodeVectorList);
                subflowInfoList.add(subflowInfo);
            }
        }

        flowInfo.setSubflowInfoList(subflowInfoList);
        return flowInfo;

    }

    /**
     * 解析流程额外信息
     */
    public FlowInfo parseFlowExtraInfoList(FlowWithExtraInfo flowWithExtraInfo) {
        FlowInfo flowInfo = new FlowInfo();
        BeanUtils.copyProperties(flowWithExtraInfo, flowInfo);
        flowInfo.setUsable(FlowUtil.stringToBoolean(flowWithExtraInfo.getUsable(), false));
        flowInfo.setDeleted(FlowUtil.stringToBoolean(flowWithExtraInfo.getDeleted(), false));
        Map<String, Object> pairNodeInfoMap = new HashMap();

        List<FlowParallelSection> flowParallelSections = new ArrayList<>();
        for (FlowExtraInfo flowExtraInfo : flowWithExtraInfo.getFlowExtraInfoList()) {
            List<String> nodeIds = new ArrayList<>();

            switch (flowExtraInfo.getType()) {
                //是否为默认流程
                case "DEFAULT":
                    flowInfo.setDefaultType(flowExtraInfo.getValue().equals("1") ? true : false);
                    break;
                //成对节点信息
                case "PAIR_NODE_INFO":
                    String[] nodeInfo = flowExtraInfo.getValue().split(",");
                    if (pairNodeInfoMap.get(nodeInfo[0]) == null) {
                        pairNodeInfoMap.put(nodeInfo[0], nodeInfo[1]);
                    } else {
                        pairNodeInfoMap.put(nodeInfo[0], pairNodeInfoMap.get(nodeInfo[0]) + "," + nodeInfo[1]);
                    }
                    break;
                //并行区段信息
                case "PARALLEL_SECTION_SORT":
                    //并行区段排序信息
                    FlowParallelSection flowParallelSection = new FlowParallelSection();
                    String[] sectionSort = flowExtraInfo.getValue().split(",");
                    flowParallelSection.setId(sectionSort[0]);
                    if (org.springframework.util.StringUtils.hasText(sectionSort[1]) && !sectionSort[1].equals("null")) {
                        flowParallelSection.setSort(Integer.valueOf(sectionSort[1]));
                    }
                    //并行区段节点信息
                    for (FlowExtraInfo extraInfo : flowWithExtraInfo.getFlowExtraInfoList()) {
                        if (extraInfo.getType().equals("PARALLEL_SECTION_NODE_ID")) {
                            String[] sectionNodeId = extraInfo.getValue().split(",");
                            if (sectionNodeId[0].equals(flowParallelSection.getId())) {
                                nodeIds.add(sectionNodeId[1]);
                            }

                        }
                    }
                    flowParallelSection.setNodeIds(nodeIds.toArray(new String[nodeIds.size()]));
                    flowParallelSections.add(flowParallelSection);
                    break;

            }
        }
        flowInfo.setParallelSections(flowParallelSections);
        List<PairNodeInfo> pairNodeInfos = new ArrayList<>();
        for (String id : pairNodeInfoMap.keySet()) {
            PairNodeInfo pairNodeInfo = new PairNodeInfo();
            pairNodeInfo.setNodeIds(pairNodeInfoMap.get(id).toString().split(","));
            pairNodeInfo.setId(id);
            pairNodeInfos.add(pairNodeInfo);
        }
        flowInfo.setPairNodeInfo(pairNodeInfos);
        return flowInfo;
    }

    /**
     * 根据id查单个流程
     */
    @Override
    public FlowInfo getFlowInfoByFlowId(String flowId) {
        QueryFlowModel queryFlowModel = new QueryFlowModel();
        queryFlowModel.setFlowIds(new String[]{flowId});
        queryFlowModel.setShowDeleted(true);
        List<FlowInfo> flowInfoList = this.getFlowInfoList(queryFlowModel);
        if (flowInfoList.size() > 1) {
            throw new RuntimeException("查询结果数量异常，请检查代码");
        }
        if (flowInfoList.size() == 1) {
            return flowInfoList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void setFlowUsable(String flowId) {
        this.sendSetFlowUsable(flowId);
        //发送数据同步
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<String> messageWrapper = new MessageWrapper<>(FlowConfigHeaderConstant.class, FlowConfigHeaderConstant.SET_FLOW_USABLE, flowId);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.flowConfigChannel(), messageWrapper);
        }
    }

    @Override
    public void sendSetFlowUsable(String flowId) {
        FlowInfo flowInfo = this.getFlowInfoByFlowId(flowId);
        ConditionInfo conditionInfo = verifyFlowAndGetConditionId(flowInfo, null, false);
        //true旧id false新id
        Boolean existed = conditionInfo.getExisted();

        FlowExtraInfo flowExtraInfoDefault = new FlowExtraInfo();
        flowExtraInfoDefault.setType("DEFAULT");
        flowExtraInfoDefault.setFlowId(flowId);

        if (existed) {
            // 将当前流程融合进既有流程条件组里
            // 1、修改当前conditionId为旧有conditionId
            FlowExtraInfo flowExtraInfo = new FlowExtraInfo();
            flowExtraInfo.setFlowId(flowId);
            flowExtraInfo.setValue(conditionInfo.getConditionId());
            MybatisPlusUtils.update(
                flowExtraInfoMapper,
                flowExtraInfo,
                eqParam(FlowExtraInfo::getFlowId, flowId),
                eqParam(FlowExtraInfo::getType, "CONDITION")
            );
            // 删除旧condition

            //删除流程额外信息
            String flowConditionId = flowInfo.getConditionId();
            MybatisPlusUtils.delete(
                flowExtraInfoMapper,
                eqParam(FlowExtraInfo::getValue, flowConditionId)
            );

            //删除条件信息
            MybatisPlusUtils.delete(
                conditionService,
                eqParam(Condition::getId, flowConditionId)
            );

            MybatisPlusUtils.delete(
                conditionValueService,
                eqParam(ConditionValue::getConditionId, flowConditionId)
            );

            // 2、设置当前流程为非默认流程
            flowExtraInfoDefault.setValue(FlowUtil.booleanToString(false));
        } else {
            // 当前流程为新条件组，设置当前流程为默认流程
            flowExtraInfoDefault.setValue(FlowUtil.booleanToString(true));
        }

        flowExtraInfoMapper.insert(flowExtraInfoDefault);

        // 修改流程状态为发布状态
        Flow flow = new Flow();
        flow.setId(flowId);
        flow.setUsable("1");
        flowMapper.updateById(flow);
    }

    /**
     * 新增或者修改流程配置，分保存并发布、仅保存两种情况
     *
     * @param flowInfoDispose
     * @param flowInfoDispose
     * @return
     */
    @Override
    public SetTaskFlowConfigResult setTaskFlowConfig(FlowInfoDispose flowInfoDispose, boolean newIdUseFlowInfo) {
        SetTaskFlowConfigResult result = this.sendSetTaskFlowConfig(flowInfoDispose, newIdUseFlowInfo);
        //发送数据同步
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            flowInfoDispose.setCreateWorkerName(UserUtil.getUserInfo().getName());
            flowInfoDispose.setCreateWorkerId(UserUtil.getUserInfo().getId());
            MessageWrapper<FlowInfo> messageWrapper = new MessageWrapper<>(FlowConfigHeaderConstant.class, FlowConfigHeaderConstant.SET_FLOW_CONFIG, flowInfoDispose);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.flowConfigChannel(), messageWrapper);
        }
        return result;
    }

    @Override
    public SetTaskFlowConfigResult sendSetTaskFlowConfig(FlowInfoDispose flowInfoDispose, boolean newIdUseFlowInfo) {
        // 同流程类型下不能出现相同的流程名称
        if (hasSameNameFlow(flowInfoDispose)) {
            throw RestRequestException.normalFail("同流程类型下不能出现相同的流程名称");
        }

        if (flowInfoDispose.getPairNodeInfo().stream().anyMatch(pairNodeInfo -> pairNodeInfo.getNodeIds().length < 2)) {
            logger.error("成对节点信息错误不成立:" + JSON.toJSONString(flowInfoDispose, true));
            throw RestRequestException.normalFail("配置失败，成对节点数据错误");
        }

        SetTaskFlowConfigResult setTaskFlowConfigResult = new SetTaskFlowConfigResult();
        List<String> warningMessageList = new ArrayList<>();
        // 检查条件细节
        for (FlowConfigSingleConditionEnum singleConditionEnum : FlowConfigSingleConditionEnum.values()) {
            @SuppressWarnings("rawtypes")
            FlowConfigSingleConditionBase condition = singleConditionEnum.getCondition();
            condition.checkValue(flowInfoDispose);
            String warningMessage = condition.predigestValue(flowInfoDispose);
            if (warningMessage != null) {
                warningMessageList.add(warningMessage);
            }
        }


        //判断两个成对节点角色配置信息是否一致
        flowInfoDispose.getPairNodeInfo().forEach(pairNodeInfo -> {
            String[] pairNodeIds = pairNodeInfo.getNodeIds();
            List<NodeRoleConfig> roleConfigA = flowInfoDispose.getNodes().stream().filter(v -> v.getId().equals(pairNodeIds[0])).collect(Collectors.toList()).get(0).getRoleConfigs();
            List<NodeRoleConfig> roleConfigB = flowInfoDispose.getNodes().stream().filter(v -> v.getId().equals(pairNodeIds[1])).collect(Collectors.toList()).get(0).getRoleConfigs();

            roleConfigA.sort(Comparator.comparing(NodeRoleConfig::getRoleId));
            roleConfigB.sort(Comparator.comparing(NodeRoleConfig::getRoleId));

            List<RoleBase> roleBasesA = flowInfoDispose.getNodes().stream().filter(v -> v.getId().equals(pairNodeIds[0])).collect(Collectors.toList()).get(0).getExcludeRoles();
            List<RoleBase> roleBasesB = flowInfoDispose.getNodes().stream().filter(v -> v.getId().equals(pairNodeIds[1])).collect(Collectors.toList()).get(0).getExcludeRoles();

            roleBasesA.sort(Comparator.comparing(RoleBase::getRoleId));
            roleBasesB.sort(Comparator.comparing(RoleBase::getRoleId));
            if (!roleConfigA.equals(roleConfigB) || !roleBasesA.equals(roleBasesB)) {
                throw new RuntimeException("配置失败，成对节点角色配置信息数据错误");
            }
        });
        if (warningMessageList.size() > 0) {
            setTaskFlowConfigResult.setWarningInfo(String.join("", warningMessageList));
        }

        //旧流程
        FlowInfo oldFlowInfo = this.getFlowInfoByFlowId(flowInfoDispose.getId());

        boolean isDeletedOldFLowInfo;
        boolean isRealDeletedOldFLowInfo;
        if (oldFlowInfo != null) {
            isDeletedOldFLowInfo = true;
            isRealDeletedOldFLowInfo = !flowInfoDispose.getUsable();
            //因为需要加确认提示,验证通过后,数据操作移动到最后操作
            //isRealDeletedOldFLowInfo = this.deleteFlowInfo(oldFlowInfo);
        } else {
            isDeletedOldFLowInfo = false;
            isRealDeletedOldFLowInfo = false;
        }

        if (!newIdUseFlowInfo) {
            flowInfoDispose.setId(IdWorker.get32UUID());
        }
        Boolean conditionExisted = false;
        // 增加流程配置信息
        if (flowInfoDispose.getUsable()) {
            String[] extraAllowedFlowIds;
            // 如果是逻辑删除了旧有流程，则额外允许新流程继承其条件
            if (isDeletedOldFLowInfo && !isRealDeletedOldFLowInfo) {
                extraAllowedFlowIds = new String[]{flowInfoDispose.getId()};
            } else {
                extraAllowedFlowIds = null;
            }
            // 校验条件并解析条件id
            ConditionInfo conditionInfo = verifyFlowAndGetConditionId(flowInfoDispose, extraAllowedFlowIds, newIdUseFlowInfo);
            // 如果删除的流程是这组唯一的流程，并且当前流程的条件和删除的流程完全一致，则需要更正匹配信息
            if (!conditionInfo.getExisted() && isDeletedOldFLowInfo && !isRealDeletedOldFLowInfo && oldFlowInfo.getDefaultType() != null && oldFlowInfo.getDefaultType() && FlowUtil.allConditionEquals(oldFlowInfo, flowInfoDispose)) {
                conditionInfo.setExisted(true);
                conditionInfo.setConditionId(oldFlowInfo.getConditionId());
            }
            // 设置conditionId
            flowInfoDispose.setConditionId(conditionInfo.getConditionId());
            // 是否匹配到旧有流程的条件
            conditionExisted = conditionInfo.getExisted();

            // 设置流程额外信息和条件信息
            // 是否完全替换掉了原流程
            boolean replacedOldFlow = false;

            // 设置defaultType
            // 如果是修改发布并且原本的流程没有真删除，则可能需要更改既有流程的默认状态
            if (isDeletedOldFLowInfo && !isRealDeletedOldFLowInfo) {
                if (flowInfoDispose.getConditionId().equals(oldFlowInfo.getConditionId())) {
                    // 能归到原本的条件组下，则与原本条件组一致，相当于完全替代了原本流程的位置
                    flowInfoDispose.setDefaultType(oldFlowInfo.getDefaultType());
                    replacedOldFlow = true;
                } else {
                    // 能归到旧有的其他条件组下则设置为非默认，反之为默认
                    flowInfoDispose.setDefaultType(!conditionExisted);
                }
            } else {
                // 否则不用更改旧有流程的默认情况
                // 能归到旧有的其他条件组下则设置为非默认，反之为默认
                flowInfoDispose.setDefaultType(!conditionExisted);
            }
            // 如果删除了默认流程，并且原流程没有被当前流程所替换，则需要在原流程所在条件组推举出新的默认流程
            if (isDeletedOldFLowInfo && oldFlowInfo.getDefaultType() != null && oldFlowInfo.getDefaultType() && !replacedOldFlow) {
                setTaskFlowConfigResult = setDefaultFlowSameConditionOldestAndGetMsg(oldFlowInfo.getId(),oldFlowInfo.getConditionId(), oldFlowInfo.getUnitCode(), flowInfoDispose.getConfirmCancelUsable());
            }
        } else {
            //之前保存并发布并是默认,保存后将备用流程设置为默认
            //删除了旧流程 并且旧流程是保存已发布和默认状态
            if (isDeletedOldFLowInfo && oldFlowInfo.getUsable() && oldFlowInfo.getDefaultType() != null && oldFlowInfo.getDefaultType()) {
                setTaskFlowConfigResult = setDefaultFlowSameConditionOldestAndGetMsg(oldFlowInfo.getId(),oldFlowInfo.getConditionId(), oldFlowInfo.getUnitCode(), flowInfoDispose.getConfirmCancelUsable());
            }
            if (!newIdUseFlowInfo) {
                flowInfoDispose.setConditionId(IdWorker.get32UUID());
            }
            flowInfoDispose.setDefaultType(null);
        }
        //需要用户确认(直接返回不做操作)
        if (setTaskFlowConfigResult.getNeedCancelUsable()) {
            return setTaskFlowConfigResult;
        }


        // 增加流程配置基本信息
        Flow flow = new Flow();
        BeanUtils.copyProperties(flowInfoDispose, flow);
        flow.setUsable(FlowUtil.booleanToString(flowInfoDispose.getUsable()));
        this.addFlow(flow);
        setTaskFlowConfigResult.setId(flow.getId());

        // 如果不存在旧有条件，新增条件数据
        if (!conditionExisted) {
            parseInsertConditionValue(flowInfoDispose);
        }
        if (isDeletedOldFLowInfo) {
            this.deleteFlowInfo(oldFlowInfo);
        }
        // 流程额外信息
        parseInsertFlowExtraInfo(flowInfoDispose);
        //子流程
        addChildFlow(flowInfoDispose);
        //节点和节点额外信息
        parseInsertNode(flowInfoDispose);
        //节点向量和节点向量额外信息
        addNodeVectorWithExtraInfo(flowInfoDispose.getNodeVectors(), flowInfoDispose);
        flowInfoDispose.setId(flow.getId());
        return setTaskFlowConfigResult;
    }

    public boolean hasSameNameFlow(FlowInfo flowInfo) {
        List<ColumnParam<Flow>> columnParamList = new ArrayList<>(Arrays.asList(
            eqParam(Flow::getUnitCode, flowInfo.getUnitCode()),
            eqParam(Flow::getFlowTypeCode, flowInfo.getFlowTypeCode()),
            eqParam(Flow::getName, flowInfo.getName()),
            eqParam(Flow::getDeleted, "0")
        ));
        if (StringUtils.isNotBlank(flowInfo.getId())) {
            columnParamList.add(neParam(Flow::getId, flowInfo.getId()));
        }
        return MybatisPlusUtils.selectExist(
            flowMapper,
            columnParamList
        );
    }

    @Data
    public static class ConditionInfo {
        private String conditionId;
        private Boolean existed;
    }


    /**
     * 校验流程条件是否符合流程类型的包配置
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean verifyConditionByFlowType(FlowInfo flowInfo) {
        ExtraFlowType extraFlowType = extraFlowTypeMapper.getExtraFlowTypeByCode(flowInfo.getFlowTypeCode());
        // 如果是额外流程类型，则根据其包配置进行校验
        if (extraFlowType != null) {
            // 获取全部作业包信息
            Map<String, PacketInfo> packetMap = CacheUtil.getDataUseThreadCache("remoteService.getPacketList_all_toMap", () -> {
                List<PacketInfo> packetList = RemoteServiceCachedDataUtil.getPacketList();
                return CommonUtils.collectionToMap(packetList, PacketInfo::getPacketCode);
            });

            // 查询此类型配置的作业包编码
            Set<String> packetCodes = CacheUtil.getDataUseThreadCache(
                "FlowServiceImpl.verifyConditionByFlowType.queryPacketCodes_" + flowInfo.getFlowTypeCode(),
                () -> MybatisPlusUtils.selectList(
                    extraFlowTypePacketMapper,
                    eqParam(ExtraFlowTypePacket::getExtraFlowTypeCode, flowInfo.getFlowTypeCode())
                ).stream().map(ExtraFlowTypePacket::getPacketCode).collect(Collectors.toSet())
            );

            // 转换成作业包信息
            List<PacketInfo> flowTypePacketInfoList = packetCodes.stream().map(packetMap::get).collect(Collectors.toList());

            // Map<String, List<PacketInfo>> packetGroupMap = flowTypePacketInfoList.stream().collect(Collectors.groupingBy(
            //     v -> v.getPacketType() + "_" + v.getRepairCode()
            // ));

            // 获取所有车组信息
            List<TrainsetBaseInfo> trainsetBaseInfoList = RemoteServiceCachedDataUtil.getTrainsetList();
            // 过滤出流程配置匹配到的批次
            Set<String> filterByCondition = FlowConfigSingleConditionUtil.filterByTrainConditionValue(
                trainsetBaseInfoList,
                FlowConfigSingleConditionEnum.TrainCondition.getInstance().from(flowInfo)
            ).stream().map(TrainsetBaseInfo::getTraintempid).collect(Collectors.toSet());
            // 过滤出当前流程类型配置的包匹配到的批次
            Set<String> filterByPacketList = getBatchesFilterByPacketInfoList(trainsetBaseInfoList, flowTypePacketInfoList);

            // 如果流程匹配到的批次存在作业包配置匹配到的批次以外的，则校验失败
            return filterByPacketList.containsAll(filterByCondition);
        } else {
            return true;
        }
    }

    private Set<String> getBatchesFilterByPacketInfo(List<TrainsetBaseInfo> trainsetBaseInfos, PacketInfo packetInfo) {
        boolean useNewItem = ConfigUtil.isUseNewItem();
        Stream<TrainsetBaseInfo> trainsetBaseInfoStream = trainsetBaseInfos.parallelStream();
        if (!"ALL".equalsIgnoreCase(packetInfo.getSuitModel())) {
            trainsetBaseInfoStream = trainsetBaseInfoStream.filter(v -> v.getTraintype().equals(packetInfo.getSuitModel()));
            if (useNewItem && !"ALL".equalsIgnoreCase(packetInfo.getSuitBatch())) {
                trainsetBaseInfoStream = trainsetBaseInfoStream.filter(v -> v.getTraintempid().equals(packetInfo.getSuitBatch()));
            }
        }
        return trainsetBaseInfoStream.map(TrainsetBaseInfo::getTraintempid).collect(Collectors.toSet());
    }

    private Set<String> getBatchesFilterByPacketInfoList(List<TrainsetBaseInfo> trainsetBaseInfos, List<PacketInfo> packetInfoList) {
        List<TrainsetBaseInfo> trainsetDistinctByBatch = CommonUtils.getDistinctList(trainsetBaseInfos, TrainsetBaseInfo::getTraintempid);
        Set<String> batches = new HashSet<>();
        for (PacketInfo packetInfo : packetInfoList) {
            batches.addAll(getBatchesFilterByPacketInfo(trainsetDistinctByBatch, packetInfo));
            if (batches.size() == trainsetDistinctByBatch.size()) {
                break;
            }
        }
        return batches;
    }

    private String getValue(String value, String defaultValue) {
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    public void verifyFlowType(List<FlowInfo> flowInfos, String unitCode) {
        // 过滤出校验失败的流程
        List<FlowInfo> verifyFailFlowInfoList = flowInfos.stream().filter(flowInfo -> !verifyConditionByFlowType(flowInfo)).collect(Collectors.toList());

        // 存在校验失败的流程则抛出异常
        if (verifyFailFlowInfoList.size() > 0) {
            List<BaseFlowType> flowTypeList = flowTypeService.getFlowTypeList(unitCode);
            Map<String, String> typeNameMap = CommonUtils.reduceMap(flowTypeList.stream(), (map, value) -> {
                map.put(value.getCode(), value.getName());
                return map;
            });
            throw RestRequestException.normalFail(
                verifyFailFlowInfoList.stream().map(
                    v -> v.getName() + "(" + getValue(typeNameMap.get(v.getFlowTypeCode()), v.getFlowTypeCode()) + ")"
                ).collect(Collectors.joining("，")) + "：以上流程配置的流程类型包配置没有涵盖流程配置中的批次或车型，请检查！"
            );
        }
    }

    // public String isGroupUpdDefault(String conditionId, String flowId, Boolean defaultType, Boolean isDelete) {
    //     if (defaultType == null) {
    //         defaultType = false;
    //     }
    //     String result = "";
    //     //如果删除的是默认流程 把不是自己且最早的改为默认流程
    //     if (isDelete && defaultType) {
    //         Flow flow = getFlowFirstName(conditionId);
    //         if (flow != null) {
    //             flowExtraInfoService.setFlowDefault(flow.getId(), "1");
    //             result = flow.getName() + "已更改为默认流程";
    //         }
    //
    //         //如果修改为默认流程,把不等于自己且同组下的默认流程改为非默认流程
    //     } else if (defaultType) {
    //         Flow flow = getFlowFirstDefaultName(conditionId, flowId);
    //         if (flow != null) {
    //             //如果修改的是默认流程 把最早改为非默认流程
    //             flowExtraInfoService.setFlowDefault(flow.getId(), "0");
    //             result = flow.getName() + "已更改为非默认流程";
    //         }
    //     }
    //     return result;
    // }

    /**
     * 新增子流程
     */
    public void addChildFlow(FlowInfo flowInfo) {

        for (SubflowInfo subflowInfo : flowInfo.getSubflowInfoList()) {
            Flow flow = new Flow();
            BeanUtils.copyProperties(subflowInfo, flow);
            flow.setUnitCode(flowInfo.getUnitCode());
            this.addFlow(flow);

            //节点信息
            for (NodeInfo node : subflowInfo.getNodes()) {
                //解析转换入库 先入节点在入节点额外信息
                Node nodeInfo = new Node();
                BeanUtils.copyProperties(node, nodeInfo);
                nodeInfo.setFlowId(flow.getId());
                nodeService.addNode(nodeInfo);
                parseAddNodeExtraInfo(node);
            }
            //节点向量
            for (NodeVector nodeVector : subflowInfo.getNodeVectors()) {
                nodeVector.setFlowId(subflowInfo.getId());
                nodeVectorService.addNodeVector(nodeVector);
            }
        }

    }

    public void parseAddNodeExtraInfo(NodeInfo nodeInfo) {
        List<NodeExtraInfo> nodeExtraInfos = new ArrayList<>();

        for (NodeExtraInfoTypeEnum typeEnum : NodeExtraInfoTypeEnum.values()) {
            nodeExtraInfos.addAll(typeEnum.getConvertor().getCurrentTypeExtraInfoList(nodeInfo));
        }

        for (NodeExtraInfo nodeExtraInfo : nodeExtraInfos) {
            nodeExtraInfo.setNodeId(nodeInfo.getId());
            nodeExtraInfoService.addNodeExtraInfo(nodeExtraInfo);
        }
    }


    /**
     * 入库前验证
     *
     * @param flowInfo
     * @param newIdUseFlowInfo 是否使用flowInfo中的conditionId作为新id
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ConditionInfo verifyFlowAndGetConditionId(FlowInfo flowInfo, String[] extraAllowedFlowIds, boolean newIdUseFlowInfo) {
        if (!verifyConditionByFlowType(flowInfo)) {
            throw RestRequestException.normalFail("流程类型包配置没有涵盖流程配置中的批次或车型，请检查！");
        }

        ConditionInfo conditionInfo = new ConditionInfo();

        //先查出已有流程配置 true旧id false新id
        QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
        queryFlowModelGeneral.setFlowTypeCodes(new String[]{flowInfo.getFlowTypeCode()});
        queryFlowModelGeneral.setUnitCode(flowInfo.getUnitCode());
        queryFlowModelGeneral.setUsable(FlowUtil.booleanToString(true));
        queryFlowModelGeneral.setOrFlowIds(extraAllowedFlowIds);
        List<FlowInfo> flowInfoList = getFlowInfoList(queryFlowModelGeneral);
        // flowInfoList = flowInfoList.stream().filter(v ->! v.getId().equals(flowInfo.getId())).collect(Collectors.toList());
        // 是否存在 所有条件都交集且存在不相等的条件 的流程，存在则禁止发布
        flowInfoLoop:
        for (FlowInfo queryFlowInfo : flowInfoList) {
            FlowConfigSingleConditionEnum[] flowConfigSingleConditionEnums = FlowConfigSingleConditionEnum.values();

            DescriptionLevelDifferentDirectionEnum direction = null;
            FlowConfigSingleConditionEnum notEqualAndHasIntersectionCondition = null;

            for (FlowConfigSingleConditionEnum flowConfigSingleConditionEnum : flowConfigSingleConditionEnums) {
                FlowConfigSingleConditionBase condition = flowConfigSingleConditionEnum.getCondition();
                Object v1 = condition.from(flowInfo);
                Object v2 = condition.from(queryFlowInfo);
                if (!condition.hasIntersection(v1, v2)) {
                    // 如果存在一点交集没有的条件，则认为这个流程和当前流程没有冲突
                    continue flowInfoLoop;
                }
                DescriptionLevelDifferentDirectionEnum curDirection = condition.getDescriptionLevelDifferentDirection(v1, v2);
                // 如果在有交集的情况下，发现这个条件的交集是允许的特例，则不会因为这个条件认为这个流程与当前流程存在冲突，但是特例条件的方向必须相同
                if (curDirection != null) {
                    if (direction == null) {
                        direction = curDirection;
                    } else {
                        if (!direction.equals(curDirection)) {
                            throw RestRequestException.normalFail(
                                "当同一个流程类型下的两个流程的多个条件，其中每个条件都满足一方为空，另一方不为空时(排除逻辑的条件除外)，由于其匹配逻辑不易理解，值为空的条件只能出现在其中的一个流程中，即一侧的流程的条件需要完全包含于另一个当中"
                            );
                        }
                    }
                }
                // 如果条件有交集(走到这就意味着这一点)，并且不相等，并且不是特殊允许的情况，则在不为空的情况下进行存留
                if (notEqualAndHasIntersectionCondition == null && !condition.equals(v1, v2) && curDirection == null) {
                    notEqualAndHasIntersectionCondition = flowConfigSingleConditionEnum;
                }
            }
            // 走到这说明每个条件都有交集，如果存在特殊情况以外的不相等的条件，则需要返回这个条件的交集错误信息
            if (notEqualAndHasIntersectionCondition != null) {
                FlowConfigSingleConditionBase condition = notEqualAndHasIntersectionCondition.getCondition();
                String errorMsg = condition.getErrorMessage(condition.from(flowInfo), condition.from(queryFlowInfo), queryFlowInfo);
                if (direction != null) {
                    errorMsg += "注：两个流程在同一条件上，一侧是空条件，另一侧非空，只要不容易造成匹配逻辑理解上的混乱，通常是允许的。";
                }
                throw RestRequestException.normalFail(
                    errorMsg
                );
            }
        }

        // 是否存在条件完全相等的流程，存在则保存用旧条件id
        FlowInfo allConditionEqualFlowInfo = CommonUtils.find(flowInfoList, queryFlowInfo -> FlowUtil.allConditionEquals(queryFlowInfo, flowInfo));

        if (allConditionEqualFlowInfo != null) {
            conditionInfo.setExisted(true);
            conditionInfo.setConditionId(allConditionEqualFlowInfo.getConditionId());
        } else {
            conditionInfo.setExisted(false);
            if (newIdUseFlowInfo) {
                conditionInfo.setConditionId(flowInfo.getConditionId());
            } else {
                conditionInfo.setConditionId(IdWorker.get32UUID());
            }
        }

        return conditionInfo;
    }


    /**
     * 解析入库节点和节点额外信息
     */
    public void parseInsertNode(FlowInfo flowInfo) {
        for (NodeInfo nodeInfo : flowInfo.getNodes()) {
            //存节点额外信息
            List<NodeExtraInfo> nodeExtraInfos = new ArrayList<>();
            //存节点
            Node node = new Node();
            BeanUtils.copyProperties(nodeInfo, node);
            node.setFlowId(flowInfo.getId());

            for (NodeExtraInfoTypeEnum typeEnum : NodeExtraInfoTypeEnum.values()) {
                nodeExtraInfos.addAll(typeEnum.getConvertor().getCurrentTypeExtraInfoList(nodeInfo));
            }

            //新增节点
            nodeService.addNode(node);

            for (NodeExtraInfo nodeExtraInfo : nodeExtraInfos) {
                nodeExtraInfo.setNodeId(node.getId());
                nodeExtraInfoService.addNodeExtraInfo(nodeExtraInfo);
            }
        }
    }


    /**
     * 解析入库流程额外信息
     */
    public void parseInsertFlowExtraInfo(FlowInfo flowInfo) {
        List<FlowExtraInfo> flowExtraInfoList = new ArrayList<>();


        //条件表id放流程额外信息表
        FlowExtraInfo flowExtraInfo = new FlowExtraInfo();
        flowExtraInfo.setType("CONDITION");
        flowExtraInfo.setValue(flowInfo.getConditionId());
        flowExtraInfoList.add(flowExtraInfo);

        if (flowInfo.getDefaultType() != null) {
            //是否为默认流程
            FlowExtraInfo defaultType = new FlowExtraInfo();
            defaultType.setType("DEFAULT");
            defaultType.setValue(FlowUtil.booleanToString(flowInfo.getDefaultType()));
            flowExtraInfoList.add(defaultType);
        }


        //成对节点信息
        if (flowInfo.getPairNodeInfo() != null) {
            for (PairNodeInfo pairNodeInfo : flowInfo.getPairNodeInfo()) {
                String id = UUID.randomUUID().toString();

                for (String nodeId : pairNodeInfo.getNodeIds()) {
                    FlowExtraInfo pairNode = new FlowExtraInfo();
                    pairNode.setType("PAIR_NODE_INFO");
                    pairNode.setValue(id + "," + nodeId);
                    flowExtraInfoList.add(pairNode);
                }

            }
        }
        //并行区段排序信息
        if (flowInfo.getParallelSections() != null) {
            for (FlowParallelSection parallelSection : flowInfo.getParallelSections()) {
                String id = UUID.randomUUID().toString();
                FlowExtraInfo sort = new FlowExtraInfo();
                sort.setType("PARALLEL_SECTION_SORT");
                sort.setValue(id + "," + parallelSection.getSort());
                flowExtraInfoList.add(sort);
                for (String nodeId : parallelSection.getNodeIds()) {
                    FlowExtraInfo noteId = new FlowExtraInfo();
                    noteId.setType("PARALLEL_SECTION_NODE_ID");
                    noteId.setValue(id + "," + nodeId);
                    flowExtraInfoList.add(noteId);
                }
            }
        }
        //流程额外信息
        for (FlowExtraInfo flowExtraInfo1 : flowExtraInfoList) {
            flowExtraInfo1.setFlowId(flowInfo.getId());
            flowExtraInfoService.addFlowExtraInfo(flowExtraInfo1);
        }

    }

    /**
     * 解析入库条件表
     */
    public void parseInsertConditionValue(FlowInfo flowInfo) {
        List<ConditionValue> conditionValueList = new ArrayList<>();
        //车型
        if (flowInfo.getTrainTypes() != null) {
            for (String trainType : flowInfo.getTrainTypes()) {
                ConditionValue conditionValue = new ConditionValue();
                conditionValue.setType("TRAIN_TYPE");
                conditionValue.setValue(trainType);
                conditionValueList.add(conditionValue);
            }
        }

        //车型排除条件值存储
        ConditionValue trainTypeExclude = new ConditionValue();
        trainTypeExclude.setType("TRAIN_TYPE_EXCLUDE");
        trainTypeExclude.setValue(flowInfo.getTrainTypeExclude() == null ? "0" : flowInfo.getTrainTypeExclude() == true ? "1" : "0");
        conditionValueList.add(trainTypeExclude);

        //批次
        if (flowInfo.getTrainTemplates() != null) {
            for (String trainTemplate : flowInfo.getTrainTemplates()) {
                ConditionValue conditionValue = new ConditionValue();
                conditionValue.setType("TRAIN_TEMPLATE");
                conditionValue.setValue(trainTemplate);
                conditionValueList.add(conditionValue);
            }
        }

        //批次排除条件值存储
        ConditionValue templateExclude = new ConditionValue();
        templateExclude.setType("TRAIN_TEMPLATE_EXCLUDE");
        templateExclude.setValue(flowInfo.getTrainTemplateExclude() == null ? "0" : flowInfo.getTrainTemplateExclude() == true ? "1" : "0");
        conditionValueList.add(templateExclude);
        //车组
        if (flowInfo.getTrainsetIds() != null) {
            for (String trainsetId : flowInfo.getTrainsetIds()) {
                ConditionValue conditionValue = new ConditionValue();
                conditionValue.setType("TRAIN_SET_ID");
                conditionValue.setValue(trainsetId);
                conditionValueList.add(conditionValue);
            }
        }

        //车组排除条件值存储
        ConditionValue trainsetIdExclude = new ConditionValue();
        trainsetIdExclude.setType("TRAIN_SET_ID_EXCLUDE");
        trainsetIdExclude.setValue(flowInfo.getTrainsetIdExclude() == null ? "0" : flowInfo.getTrainsetIdExclude() == true ? "1" : "0");
        conditionValueList.add(trainsetIdExclude);
        //关键字条件值存储
        if (flowInfo.getKeyWords() != null) {
            for (String keyWord : flowInfo.getKeyWords()) {
                ConditionValue conditionValue = new ConditionValue();
                conditionValue.setType("KEY_WORD");
                conditionValue.setValue(keyWord);
                conditionValueList.add(conditionValue);
            }
        }

        //REPAIR_TYPE 检修类型
        if (StringUtils.isNotBlank(flowInfo.getRepairType())) {
            ConditionValue repairType = new ConditionValue();
            repairType.setType("REPAIR_TYPE");
            repairType.setValue(flowInfo.getRepairType());
            conditionValueList.add(repairType);
        }
        Condition condition = new Condition();
        condition.setId(flowInfo.getConditionId());
        if (conditionValueList.size() > 0) {
            //新增条件表一条
            condition.setType("CONDITION");
            conditionService.insert(condition);
        }
        //条件值表放条件表id
        for (ConditionValue conditionValue : conditionValueList) {
            conditionValue.setConditionId(condition.getId());
            conditionValueService.addConditionValue(conditionValue);
        }
    }

    @Override
    public void updFlowById(Flow flow) {
        flowMapper.updateById(flow);
    }

    @Override
    public void addFlow(Flow flow) {
        flow.setCreateTime(new Date());
        if (StringUtils.isBlank(flow.getCreateWorkerId())) {
            flow.setCreateWorkerId(UserUtil.getUserInfo().getId());
        }
        if (StringUtils.isBlank(flow.getCreateWorkerName())) {
            flow.setCreateWorkerName(UserUtil.getUserInfo().getName());
        }

        flowMapper.insert(flow);
    }

    @Override
    public List<FlowWithExtraInfo> getFlowWithExtraInfoList(QueryFlowModel queryFlowModel) {
        return flowMapper.getFlowWithExtraInfoList(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(queryFlowModel, QueryFlowModel::getFlowName));
    }

    @Autowired
    IFlowTypeService flowTypeService;

    /**
     * // 如果是已发布的逻辑删除 不是已发布的真实删除
     *
     * @param flowInfo
     * @return
     */
    @Transactional
    @Override
    public String deleteFlowInfoAndRepairConditionDefaultType(FlowInfo flowInfo) {
        FlowInfo queryFlowInfo = this.getFlowInfoByFlowId(flowInfo.getId());
        if (StringUtils.isBlank(queryFlowInfo.getDeleteWorkerId())) {
            queryFlowInfo.setDeleteWorkerId(UserUtil.getUserInfo().getStaffId());
        }
        if (StringUtils.isBlank(queryFlowInfo.getDeleteWorkerName())) {
            queryFlowInfo.setDeleteWorkerName(UserUtil.getUserInfo().getName());
        }
        String result = this.sendDeleteFlowInfoAndRepairConditionDefaultType(queryFlowInfo);
        //发送数据同步
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<FlowInfo> messageWrapper = new MessageWrapper<>(FlowConfigHeaderConstant.class, FlowConfigHeaderConstant.DELETE_FLOW_CONFIG, queryFlowInfo);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.flowConfigChannel(), messageWrapper);
        }
        return result;
    }

    @Override
    public String getDelTaskFlowConfigWarningInfo(String flowId) {
        FlowInfo queryFlowInfo = this.getFlowInfoByFlowId(flowId);
        if (queryFlowInfo.getUsable() && queryFlowInfo.getDefaultType()) {
            Flow flowSameConditionOldest = getFirstFlowByCondition(queryFlowInfo.getId(),queryFlowInfo.getConditionId());
            if (flowSameConditionOldest != null) {
                return "当前流程为默认流程，删除流程将会导致默认流程变更，是否删除？";
            }
        }
        return "此操作将永久删除该流程，是否继续？";
    }

    @Override
    public String sendDeleteFlowInfoAndRepairConditionDefaultType(FlowInfo queryFlowInfo) {
        if (queryFlowInfo == null) {
            throw new RuntimeException("流程不存在");
        }
        deleteFlowInfo(queryFlowInfo);
        if (queryFlowInfo.getUsable() && queryFlowInfo.getDefaultType()) {
            SetTaskFlowConfigResult setTaskFlowConfigResult = setDefaultFlowSameConditionOldestAndGetMsg(queryFlowInfo.getId(), queryFlowInfo.getConditionId(), queryFlowInfo.getUnitCode(), true);
            return setTaskFlowConfigResult.getResultInfo();
        }
        return "";
    }

    public String getFlowTypeName(String flowTypeCode, String unitCode) {
        List<BaseFlowType> flowTypes = CacheUtil.getDataUseThreadCache(
            "flowTypeService.getFlowTypeList_" + unitCode,
            () -> flowTypeService.getFlowTypeList(unitCode)
        );
        BaseFlowType flowType = CommonUtils.find(flowTypes, v -> v.getCode().equals(flowTypeCode));
        if (flowType != null) {
            return flowType.getName();
        } else {
            return null;
        }
    }

    /**
     * 删除流程
     *
     * @param flowInfo
     * @return 是否真实删除
     */
    public boolean deleteFlowInfo(FlowInfo flowInfo) {
        if (flowInfo.getUsable().equals(true)) {
            //修改流程表
            Flow flow = new Flow();
            flow.setId(flowInfo.getId());
            flow.setDeleted(FlowUtil.booleanToString(true));
            flow.setDeleteTime(new Date());
            flow.setDeleteWorkerId(flowInfo.getDeleteWorkerId());
            flow.setDeleteWorkerName(flowInfo.getDeleteWorkerName());
            this.updFlowById(flow);
            // 将删除的流程均设置为非默认流程
            flowExtraInfoService.setFlowDefault(flowInfo.getId(), FlowUtil.booleanToString(false));
            return false;
        } else {
            this.delFlowInfoByFlowId(flowInfo, false);
            this.delFlowInfoById(flowInfo.getId());
            return true;
        }
    }

    /**
     * 同组条件最旧的流程设置为默认
     *
     * @param conditionId
     * @return
     */
    public BaseFlow setDefaultFlowSameConditionOldest(String flowId,String conditionId) {
        // 如果删除的是默认流程，则将同组最旧的流程设置为默认
        Flow flowSameConditionOldest = getFirstFlowByCondition(flowId,conditionId);
        if (flowSameConditionOldest != null) {
            flowExtraInfoMapper.setDefaultType(flowSameConditionOldest.getId(), FlowUtil.booleanToString(true));
            return flowSameConditionOldest;
        } else {
            return null;
        }
    }

    public SetTaskFlowConfigResult setDefaultFlowSameConditionOldestAndGetMsg(String flowId, String conditionId, String unitCode, Boolean confirmCancelUsable) {
        //同条件排除自己最早的流程配置
        Flow flowSameConditionOldest = getFirstFlowByCondition(flowId,conditionId);
        SetTaskFlowConfigResult setTaskFlowConfigResult = new SetTaskFlowConfigResult();
        if (confirmCancelUsable) {
            BaseFlow baseFlow = setDefaultFlowSameConditionOldest(flowId,conditionId);
            if (baseFlow != null) {
                setTaskFlowConfigResult.setResultInfo(baseFlow.getName() + "(" + getFlowTypeName(baseFlow.getFlowTypeCode(), unitCode) + ")已更改为默认流程");
            }
        } else {
            if (flowSameConditionOldest != null) {
                setTaskFlowConfigResult.setNeedCancelUsable(true);
                setTaskFlowConfigResult.setNeedCancelUsableMessage("当前流程为默认流程，取消发布将会导致默认流程变更，是否取消发布？");
            }
        }
        return setTaskFlowConfigResult;
    }

    public void delFlowInfoByFlowId(FlowInfo flowInfo, Boolean type) {
        //flowInfo.getUsable()==true逻辑删除
        //删除流程额外信息
        flowExtraInfoService.delFlowExtraInfoByFlowId(flowInfo.getId());

        if (type == false) {
            //如果有公用的不删除条件值表
            Flow flow = getFirstFlowByCondition(flowInfo.getId(),flowInfo.getConditionId());
            if (flow == null) {
                //删除条件表
                conditionService.deleteById(flowInfo.getConditionId());
                //删除条件值表
                conditionValueService.delConditionValue(flowInfo.getConditionId());
            }
        }
        //得到节点向量表数据
        List<NodeVectorWithExtraInfo> nodeVectorWithExtraInfos = nodeVectorService.getNodeVectorWithInfoList(flowInfo.getId());
        if (nodeVectorWithExtraInfos.size() > 0) {
            //删除节点向量额外信息
            nodeVectorExtraInfoService.delNodeVectorExtraInfoById(nodeVectorWithExtraInfos);
            //删除节点向量
            nodeVectorService.delNodeVector(flowInfo.getId());
        }
        //得到节点表数据
        List<NodeWithExtraInfo> nodeWithExtraInfos = nodeService.getNodeWithExtraInfoList(flowInfo.getId());
        if (nodeWithExtraInfos.size() > 0) {
            //删除节点额外数据
            nodeExtraInfoService.delNodeExtraInfoByNodeId(nodeWithExtraInfos);
            //删除节点数据
            nodeService.delNodeByFlowId(flowInfo.getId());
        }
        //删除子流程 NodeInfo NodeVector
        for (SubflowInfo subflowInfo : flowInfo.getSubflowInfoList()) {
            this.delFlowInfoById(subflowInfo.getId());

            //得到节点向量表数据
            List<NodeVectorWithExtraInfo> subNodeVectorWithExtraInfos = nodeVectorService.getNodeVectorWithInfoList(subflowInfo.getId());
            if (nodeVectorWithExtraInfos.size() > 0) {
                //删除节点向量额外信息
                nodeVectorExtraInfoService.delNodeVectorExtraInfoById(subNodeVectorWithExtraInfos);
                //删除节点向量
                nodeVectorService.delNodeVector(subflowInfo.getId());
            }
            //得到节点表数据
            List<NodeWithExtraInfo> subNodeWithExtraInfos = nodeService.getNodeWithExtraInfoList(subflowInfo.getId());
            if (nodeWithExtraInfos.size() > 0) {
                //删除节点额外数据
                nodeExtraInfoService.delNodeExtraInfoByNodeId(subNodeWithExtraInfos);
                //删除节点数据
                nodeService.delNodeByFlowId(subflowInfo.getId());
            }

        }
    }

    @Override
    public void delFlowInfoById(String flowId) {
        flowMapper.deleteById(flowId);
    }


    public void addNodeVectorWithExtraInfo(List<NodeVectorWithExtraInfo> nodeVectorWithExtraInfoList, FlowInfo
        flowInfo) {
        //节点向量和节点向量额外信息
        for (NodeVectorWithExtraInfo nodeVectorWithExtraInfo : nodeVectorWithExtraInfoList) {
            //节点向量
            nodeVectorWithExtraInfo.setFlowId(flowInfo.getId());
            nodeVectorService.addNodeVector(nodeVectorWithExtraInfo);
            //节点向量额外信息
            if (nodeVectorWithExtraInfo.getNodeVectorExtraInfoList() != null) {
                for (NodeVectorExtraInfo nodeVectorExtraInfo : nodeVectorWithExtraInfo.getNodeVectorExtraInfoList()) {
                    nodeVectorExtraInfoService.addNodeVectorExtraInfo(nodeVectorExtraInfo);
                }
            }
        }
    }

    @Override
    public Flow getFirstFlowByCondition(String flowId,String conditionId) {
        return flowMapper.getFlowFirstName(flowId,conditionId);
    }

    @Override
    public Flow getFlowByFlowId(String flowId) {
        Flow flow = new Flow();
        flow.setId(flowId);
        return flowMapper.selectOne(flow);
    }

    @Override
    public void verifyFlowType(String unitCode) {
        QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
        queryFlowModelGeneral.setUnitCode(unitCode);
        queryFlowModelGeneral.setUsable(FlowUtil.booleanToString(true));
        List<FlowInfo> flowInfoList = this.getFlowInfoList(queryFlowModelGeneral);
        this.verifyFlowType(flowInfoList, unitCode);
    }

    @Override
    public Flow getFlowFirstDefaultName(String condition, String flowId) {
        return flowMapper.getFlowFirstDefaultName(condition, flowId);
    }

    @Override
    public List<FlowWithExtraInfo> getFlowByFlowTypeCode(String unitCode, String flowTypeCode, boolean queryPastFlow, String flowPageCode) {
        QueryFlowModel queryFlowModel = new QueryFlowModel();
        queryFlowModel.setUnitCode(unitCode);
        //是否查看过期流程
        queryFlowModel.setShowDeleted(queryPastFlow);
        List<String> flowTypeCodes = new ArrayList<>();
        if (StringUtils.isNotBlank(flowTypeCode)) {
            flowTypeCodes.add(flowTypeCode);
        } else {
            if (StringUtils.isBlank(flowPageCode)) { //统计分析页面
                flowTypeCodes = flowTypeService.getFlowTypeList(unitCode).stream().map(v -> v.getCode()).collect(Collectors.toList());
            } else { //各流程页面
                List<BaseFlowType> flowTypeList = flowTypeService.getFlowTypesByFlowPageCode(flowPageCode, unitCode);
                flowTypeCodes.addAll(flowTypeList.stream().map(v -> v.getCode()).collect(Collectors.toList()));
            }
        }
        queryFlowModel.setFlowTypeCodes(flowTypeCodes.toArray(new String[flowTypeCodes.size()]));
        List<FlowWithExtraInfo> flows = this.getFlowWithExtraInfoList(queryFlowModel);
        flows = CommonUtils.getDistinctList(flows, item -> item.getName());
        return flows;
    }


    @Override
    public String setDefaultFlowConfig(String flowId) {
        String result = this.sendSetDefaultFlowConfig(flowId);
        //发送数据同步
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<String> messageWrapper = new MessageWrapper<>(FlowConfigHeaderConstant.class, FlowConfigHeaderConstant.SET_DEFAULT_FLOW_CONFIG, flowId);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.flowConfigChannel(), messageWrapper);
        }
        return result;
    }

    @Override
    public String sendSetDefaultFlowConfig(String flowId) {
        String result = "";
        FlowInfo flowInfo = this.getFlowInfoByFlowId(flowId);
        if (flowInfo == null) {
            throw new RuntimeException("代码异常");
        }
        Flow flow = getFlowFirstDefaultName(flowInfo.getConditionId(), flowId);
        if (flow != null) {
            //把最早改为非默认流程,并把本身设置为默认
            flowExtraInfoMapper.setDefaultType(flowId, "1");
            flowExtraInfoMapper.setDefaultType(flow.getId(), "0");
            result = flow.getName() + "(" + getFlowTypeName(flow.getFlowTypeCode(), flowInfo.getUnitCode()) + ")已更改为备用流程";
        } else {
            logger.warn("未查到同条件下默认的流程" + flowId);
            result = "未查到同条件下默认的流程";
        }
        return result;
    }

    @Override
    public List<Flow> getConditionFlowsByFlowId(String flowId, String unitCode) {
        return flowMapper.getConditionFlowsByFlowId(flowId, unitCode);
    }
}
