package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.ydzbinfo.emis.configs.FaultProperties;
import com.ydzbinfo.emis.trainRepair.common.model.PostAndRole;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.common.model.WorkTeam;
import com.ydzbinfo.emis.trainRepair.common.querymodel.Log;
import com.ydzbinfo.emis.trainRepair.common.service.ILogService;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultFile;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultSearchWithKeyWork;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultWithKeyWork;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.NodeRecordTypeEnum;
import com.ydzbinfo.emis.trainRepair.constant.PacketTypeEnum;
import com.ydzbinfo.emis.trainRepair.constant.RoleTypeEnum;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowRunMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowRunSwitchMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.NodeRecordMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.param.NodeDispose;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.param.WorkerInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlow;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseNode;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowRunWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeRecordWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo.NodeRecordExtraInfoTypeEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotperson;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPostService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotPersonService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateCurService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetPostIonCurService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.upload.UpLoadFileUtils;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfo;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfoWithPayload;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import lombok.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
@Service
public class FlowRunServiceImpl extends ServiceImpl<FlowRunMapper, FlowRun> implements IFlowRunService {

    @Autowired
    InnerFlowRunService innerFlowRunService;

    @Autowired
    INodeService nodeService;

    @Autowired
    IFlowService flowService;

    @Autowired
    FlowRunMapper flowRunMapper;

    @Autowired
    IFlowRunRecordService flowRunRecordService;

    @Autowired
    INodeRecordService nodeRecordService;

    @Autowired
    IRemoteService remoteService;

    @Autowired
    IRepairMidGroundService repairMidGroundService;

    @Autowired
    IExtraFlowTypeService extraFlowTypeService;

    @Autowired
    IFlowTypeService flowTypeService;

    @Autowired
    IFlowRunSwitchService flowRunSwitchService;

    @Autowired
    IFlowRunExtraInfoService flowRunExtraInfoService;

    @Autowired
    INodeVectorService nodeVectorService;

    @Autowired
    TrainsetPostIonCurService trainsetPostIonCurService;

    @Autowired
    XzyMTaskAllotPersonService taskAllotPersonService;

    @Autowired
    TrackPowerStateCurService trackPowerStateCurService;

    @Autowired
    ILogService logService;

    @Autowired
    IPostService postService;

    @Autowired
    FlowRunSwitchMapper flowRunSwitchMapper;

    @Autowired
    FlowMapper flowMapper;

    @Autowired
    FaultProperties faultProperties;

    protected static final Logger logger = LoggerFactory.getLogger(FlowRunServiceImpl.class);

    /**
     * @param flowPageCode      ??????????????????code
     * @param unitCode          ????????????
     * @param dayPlanId         ?????????
     * @param trainsetIdListStr ??????id
     * @return
     */
    @Override
    public FlowRunInfo getFlowRunInfoByTrainset(String flowPageCode, String unitCode, String dayPlanId, String trainsetIdListStr, String staffId, List<RuntimeRole> runtimeRoles) {
        List<String> trainsetIdList;
        if (StringUtils.isNotBlank(trainsetIdListStr)) {
            trainsetIdList = Arrays.asList(trainsetIdListStr.split(","));
        } else {
            trainsetIdList = new ArrayList<>();
        }
        //???????????????????????????????????????
        Set<String> flowTypeCodeByFlowPageCode = getFlowTypeCodesByFlowPageCode(flowPageCode, unitCode);

        //???????????????ID???????????????????????????????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList;
        List<TrainsetPostionCur> trainsetPostionCurList;
        if (trainsetIdList.size() == 1) {
            taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetIdListStr, "", "", unitCode);
            trainsetPostionCurList = trainsetPostIonCurService.getByTrainsetIds(trainsetIdListStr);
            if (trainsetPostionCurList.size() == 0) {
                throw new RuntimeException("?????????????????????????????????");
            }
        } else {
            taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, "", "", "", unitCode);
            trainsetPostionCurList = trainsetPostIonCurService.getByTrainsetIds(taskPacketEntityList.stream().map(ZtTaskPacketEntity::getTrainsetId).distinct().toArray(String[]::new));
            Set<String> existTrainsetIds = trainsetPostionCurList.stream().map(TrainsetPostionCur::getTrainsetId).collect(Collectors.toSet());
            taskPacketEntityList = taskPacketEntityList.stream().filter(v -> existTrainsetIds.contains(v.getTrainsetId())).collect(Collectors.toList());
        }

        Map<String, TrainsetPostionCur> trainsetPostionCurMap = CommonUtils.collectionToMap(trainsetPostionCurList, TrainsetPostionCur::getTrainsetId);

        //??????????????????????????????????????????????????????
        List<FlowRun> flowRunList = this.getFlowRunInfoListByDayPlanId(dayPlanId, trainsetIdList.size() > 1 ? null : trainsetIdListStr).stream()
            .filter(v -> !v.getFlowTypeCode().equals(BasicFlowTypeEnum.PLANLESS_KEY.getValue()))// ?????????????????????
            .filter(v -> flowTypeCodeByFlowPageCode.contains(v.getFlowTypeCode()))//????????????????????????????????????????????????
            .collect(Collectors.toList());
        Map<String, List<FlowRun>> groupedFlowRunListByTrainsetId = flowRunList.stream().collect(Collectors.groupingBy(FlowRun::getTrainsetId));

        //???????????????????????????????????????(???????????????(???????????????????????????????????????????????????))
        QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
        queryFlowModelGeneral.setUsable(FlowUtil.booleanToString(true));
        queryFlowModelGeneral.setUnitCode(unitCode);
        // ????????????????????????id??????????????????????????????????????????
        queryFlowModelGeneral.setOrFlowIds(flowRunList.stream().map(FlowRun::getFlowId).distinct().toArray(String[]::new));
        List<FlowInfo> flowInfoList = flowService.getFlowInfoList(queryFlowModelGeneral);
        Map<String, FlowInfo> flowInfoMap = CommonUtils.collectionToMap(flowInfoList, BaseFlow::getId);

        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<MatchedFlowTypeInTrain> triggerFlowTypeCodeList = getMatchedFlowTypeCode(taskPacketEntityList, unitCode).stream().filter((matchedFlowTypeInTrain) -> {
            String currentTrainsetId = matchedFlowTypeInTrain.getTrainsetId();
            List<FlowRun> runningFlowsInCurrentTrainset = groupedFlowRunListByTrainsetId.get(currentTrainsetId);
            String flowTypeCode = matchedFlowTypeInTrain.getFlowTypeCode();
            return runningFlowsInCurrentTrainset == null || runningFlowsInCurrentTrainset.stream().noneMatch((flowRun) -> {
                String flowId = flowRun.getFlowId();
                return flowRun.getFlowTypeCode().equals(flowTypeCode) && flowInfoMap.get(flowId).getDeleted();
            });
        }).filter(matchedFlowTypeInTrain -> {
            return flowTypeCodeByFlowPageCode.contains(matchedFlowTypeInTrain.getFlowTypeCode());
        }).collect(Collectors.toList());


        String nullTrainset = "null";
        //???????????????????????????
        Map<String, List<RuntimeRole>> groupedRuntimeRoleByTrainsetId = runtimeRoles.stream().collect(Collectors.groupingBy(v -> {
            if (StringUtils.isBlank(v.getTrainsetId())) {
                return nullTrainset;
            } else {
                return v.getTrainsetId();
            }
        }));
        //??????????????????
        Map<String, List<MatchedFlowTypeInTrain>> groupedMatchedFlowTypeInTrain = triggerFlowTypeCodeList.stream().collect(
            Collectors.groupingBy(MatchedFlowTypeInTrain::getTrainsetId)
        );


        //????????????????????????????????????????????????????????????id??????
        List<FlowRunSwitch> flowRunSwitchList = flowRunSwitchService.getFlowRunSwitchListByDayPlanId(dayPlanId);
        Map<String, List<FlowRunSwitch>> groupedFlowRunSwitchListByTrainsetId = flowRunSwitchList.stream().collect(Collectors.groupingBy(
            FlowRunSwitch::getTrainsetId
        ));
        Set<String> switchTargetFlowRunIdList = flowRunSwitchList.stream().map(FlowRunSwitch::getTargetFlowRunId).collect(Collectors.toSet());


        Set<String> flowTypeCodeList = triggerFlowTypeCodeList.stream().map(v -> v.getFlowTypeCode()).collect(Collectors.toSet());

        //????????????????????????????????????????????????
        List<FlowInfo> flowInfoListFilteredByFlowTypeCode = flowInfoList.stream().filter(flow -> flowTypeCodeList.contains(flow.getFlowTypeCode())).collect(Collectors.toList());

        // ?????????????????????--??????id
        Map<String, Map<String, List<FlowInfo>>> groupedByFlowTypeAndConditionFlowInfoList = flowInfoListFilteredByFlowTypeCode.stream().collect(
            Collectors.groupingBy(FlowInfo::getFlowTypeCode,
                Collectors.groupingBy(FlowInfo::getConditionId)));

        // ???????????????????????????????????????????????????????????????????????????????????????
        groupedByFlowTypeAndConditionFlowInfoList.forEach((flowTypeCode, flowTypeGroup) -> {
            Set<String> conditionIds = new HashSet<>(flowTypeGroup.keySet());
            for (String conditionId : conditionIds) {
                List<FlowInfo> list = flowTypeGroup.get(conditionId);
                if (list.stream().noneMatch(v -> v.getDefaultType() && !v.getDeleted())) {
                    flowTypeGroup.remove(conditionId);
                }
            }
        });

        // conditionId -> condition???????????????????????????
        Map<String, FlowCondition> conditionMap = new HashMap<>();
        Map<String, List<FlowInfo>> groupedFlowInfoByCondition = flowInfoListFilteredByFlowTypeCode.stream().collect(Collectors.groupingBy(FlowInfo::getConditionId));
        for (List<FlowInfo> value : groupedFlowInfoByCondition.values()) {
            FlowInfo flowInfo = value.get(0);
            FlowCondition flowCondition = new FlowCondition();
            flowCondition.setConditionId(flowInfo.getConditionId());
            flowCondition.setTrainConditionValue(FlowConfigSingleConditionEnum.TRAIN.getCondition(TrainConditionValue.class).from(flowInfo));
            flowCondition.setKeyWords(flowInfo.getKeyWords());
            flowCondition.setRepairType(flowInfo.getRepairType());
            FlowInfo defaultFlowInfo = CommonUtils.find(value, v -> v.getDefaultType() && !v.getDeleted());
            if (defaultFlowInfo != null) {
                flowCondition.setDefaultFlowCreateTime(defaultFlowInfo.getCreateTime());
                flowCondition.setDefaultFlowId(defaultFlowInfo.getId());
            }

            conditionMap.put(flowCondition.getConditionId(), flowCondition);
        }

        // ??????????????????????????????key?????????id
        List<ErrorFlowFunInfo> errorFlowFunInfos = new ArrayList<>();

        List<TempFlowRunInfoGroup> matchingInfoGroupList = new ArrayList<>();

        for (List<MatchedFlowTypeInTrain> value : groupedMatchedFlowTypeInTrain.values()) {
            String matchTrainsetId = value.get(0).getTrainsetId();
            TrainsetBaseInfo trainsetBaseInfo = RemoteServiceCachedDataUtil.getTrainsetBaseinfoByID(matchTrainsetId);
            //??????????????????
            List<FlowCondition> matchedConditions = new ArrayList<>();
            for (MatchedFlowTypeInTrain matchedFlowTypeInTrain : value) {
                //????????????code
                String curFlowTypeCode = matchedFlowTypeInTrain.getFlowTypeCode();
                //??????????????????code??????????????????
                Map<String, List<FlowInfo>> conditionIdFlowInfoListMap = groupedByFlowTypeAndConditionFlowInfoList.get(curFlowTypeCode);
                if (conditionIdFlowInfoListMap == null) {
                    continue;
                }
                if (matchedFlowTypeInTrain.getPacketEntityList().size() == 0) {
                    // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    List<MatchedResultByCondition> matchedResultByConditions = new ArrayList<>();
                    for (String conditionId : conditionIdFlowInfoListMap.keySet()) {
                        // ????????????
                        FlowCondition flowCondition = conditionMap.get(conditionId);
                        if (flowCondition.getDefaultFlowId() == null) {
                            continue;
                        }
                        MatchedResultByCondition matchedResultByCondition = new MatchedResultByCondition();
                        matchedResultByCondition.setConditionId(conditionId);
                        matchedResultByCondition.setFlowCondition(flowCondition);
                        matchedResultByCondition.setMatchedResultByKeyWordList(new ArrayList<>());
                        matchedResultByCondition.setMatchedResultByRepairTypeList(new ArrayList<>());
                        matchedResultByConditions.add(matchedResultByCondition);
                    }
                    MatchedResultByCondition bestMatchedResultByCondition = getBestOne("", matchedResultByConditions);
                    if (bestMatchedResultByCondition != null) {
                        matchedConditions.add(bestMatchedResultByCondition.getFlowCondition());
                    }
                } else {
                    // ???????????????????????????
                    Map<String, List<MatchedResultByCondition>> groupedMatchedResultByCondition = new HashMap<>();

                    //??????????????????
                    for (String conditionId : conditionIdFlowInfoListMap.keySet()) {
                        // ????????????
                        FlowCondition flowCondition = conditionMap.get(conditionId);
                        if (flowCondition.getDefaultFlowId() == null) {
                            continue;
                        }
                        TrainConditionValue trainConditionValue = flowCondition.getTrainConditionValue();

                        Set<String> trainsetIds = FlowConfigSingleConditionUtil.getFilteredTrainsetIds(Collections.singletonList(trainsetBaseInfo), trainConditionValue);
                        if (trainsetIds.size() == 0) {
                            continue;
                        }

                        List<MatchedResultByRepairType> matchedResultByRepairTypeList = new ArrayList<>();
                        List<MatchedResultByKeyWord> matchedResultByKeyWordList = new ArrayList<>();


                        String repairType = flowCondition.getRepairType();
                        PacketTypeEnum packetType = EnumUtils.findValue(RepairTypeEnum.class, RepairTypeEnum::getValue, RepairTypeEnum::getPacketType, repairType);
                        boolean repairTypeExist = packetType != null;


                        //?????????
                        List<String> keyWords = flowCondition.getKeyWords();
                        boolean keyWordsExist = keyWords.size() > 0;

                        //????????????????????????
                        for (ZtTaskPacketEntity ztTaskPacketEntity : matchedFlowTypeInTrain.getPacketEntityList()) {
                            if (repairTypeExist) {
                                if (PacketTypeEnum.from(ztTaskPacketEntity.getPacketTypeCode()) == packetType) {
                                    MatchedResultByRepairType matchedResultByRepairType = new MatchedResultByRepairType();
                                    matchedResultByRepairType.setPacketEntity(ztTaskPacketEntity);
                                    matchedResultByRepairType.setRepairType(repairType);
                                    matchedResultByRepairTypeList.add(matchedResultByRepairType);
                                }
                            }

                            if (keyWordsExist) {
                                List<MatchedResultByKeyWord> curPacketMatchedResultByKeyWordList = new ArrayList<>();
                                for (String keyWord : keyWords) {
                                    if (ztTaskPacketEntity.getPacketName().contains(keyWord)) {
                                        MatchedResultByKeyWord matchedResultByKeyWord = new MatchedResultByKeyWord();
                                        matchedResultByKeyWord.setKeyWord(keyWord);
                                        matchedResultByKeyWord.setPacketEntity(ztTaskPacketEntity);
                                        curPacketMatchedResultByKeyWordList.add(matchedResultByKeyWord);
                                    }
                                }
                                if (curPacketMatchedResultByKeyWordList.size() > 0) {
                                    matchedResultByKeyWordList.addAll(curPacketMatchedResultByKeyWordList);
                                }
                            }
                        }

                        Set<String> keyWordMatchedPacketCodes = matchedResultByKeyWordList.stream().map(v -> v.getPacketEntity().getPacketCode()).collect(Collectors.toSet());
                        Set<String> repairTypeMatchedPacketCodes = matchedResultByRepairTypeList.stream().map(v -> v.getPacketEntity().getPacketCode()).collect(Collectors.toSet());

                        // ????????????????????????????????????????????????????????????????????????????????????????????????
                        for (ZtTaskPacketEntity packetEntity : matchedFlowTypeInTrain.getPacketEntityList()) {
                            String packetCode = packetEntity.getPacketCode();
                            if (keyWordsExist && !keyWordMatchedPacketCodes.contains(packetCode)) {
                                continue;
                            }
                            if (repairTypeExist && !repairTypeMatchedPacketCodes.contains(packetCode)) {
                                continue;
                            }

                            if (!groupedMatchedResultByCondition.containsKey(packetCode)) {
                                groupedMatchedResultByCondition.put(packetEntity.getPacketCode(), new ArrayList<>());
                            }
                            List<MatchedResultByCondition> packetMatchedConditions = groupedMatchedResultByCondition.get(packetCode);

                            MatchedResultByCondition matchedResultByCondition = new MatchedResultByCondition();
                            matchedResultByCondition.setConditionId(conditionId);
                            matchedResultByCondition.setFlowCondition(flowCondition);
                            matchedResultByCondition.setMatchedResultByKeyWordList(matchedResultByKeyWordList);
                            matchedResultByCondition.setMatchedResultByRepairTypeList(matchedResultByRepairTypeList);
                            packetMatchedConditions.add(matchedResultByCondition);
                        }
                    }

                    // ??????Set????????????????????????
                    Set<MatchedResultByCondition> finalMatchedResultByConditions = new HashSet<>();
                    for (String code : groupedMatchedResultByCondition.keySet()) {
                        MatchedResultByCondition bestMatchedResultByCondition = getBestOne(code, groupedMatchedResultByCondition.get(code));
                        if (bestMatchedResultByCondition != null) {
                            finalMatchedResultByConditions.add(bestMatchedResultByCondition);
                        }
                    }
                    for (MatchedResultByCondition finalMatchedResultByCondition : finalMatchedResultByConditions) {
                        matchedConditions.add(finalMatchedResultByCondition.getFlowCondition());
                    }
                }

            }


            //??????condition???????????????(????????????)
            //????????????????????????(????????????????????????)
            List<FlowInfo> allMatchedFlowList = matchedConditions.stream().map(v -> flowInfoMap.get(v.getDefaultFlowId())).collect(Collectors.toList());

            Set<String> matchedFlowIdList = matchedConditions.stream().map(FlowCondition::getDefaultFlowId).collect(Collectors.toSet());

            //??????????????????,????????????,??????????????????????????????????????????????????????
            //????????????????????????????????????
            List<FlowRun> flowRunsByTrainsetId = groupedFlowRunListByTrainsetId.get(matchTrainsetId);

            if (flowRunsByTrainsetId != null) {
                List<FlowRun> errorFlowRuns = flowRunsByTrainsetId.stream().filter(flowRun -> {
                    //??????????????????????????????????????????????????????????????????????????????
                    boolean inMatchedFlowIdList = matchedFlowIdList.contains(flowRun.getFlowId());
                    return !inMatchedFlowIdList;
                }).filter(flowRun -> {
                    //?????????????????????????????????(?????????????????????????????????????????????)
                    boolean inExistedSwitchList = switchTargetFlowRunIdList.contains(flowRun.getId());
                    return !inExistedSwitchList;
                }).filter(flowRun -> {//??????????????????????????????????????????????????????????????????
                    //????????????????????????????????????
                    List<String> matchedFlowTypeInTrain = groupedMatchedFlowTypeInTrain.get(flowRun.getTrainsetId()).stream().map(v -> v.getFlowTypeCode()).collect(Collectors.toList());
                    //?????????????????????????????????????????????????????????/????????????????????????
                    return !matchedFlowTypeInTrain.contains(flowRun.getFlowTypeCode());
                }).collect(Collectors.toList());

                if (errorFlowRuns.size() > 0) {
                    ErrorFlowFunInfo errorFlowFunInfo = new ErrorFlowFunInfo();
                    errorFlowFunInfo.setTrainsetId(matchTrainsetId);
                    errorFlowFunInfo.setFlowRuns(errorFlowRuns);
                    errorFlowFunInfo.setMatchedFlowTypeCodes(allMatchedFlowList.stream().map(BaseFlow::getFlowTypeCode).collect(Collectors.toSet()));
                    errorFlowFunInfos.add(errorFlowFunInfo);
                }

            }

            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????(????????????????????????)??????????????????

            List<FlowRunSwitch> flowRunSwitchByTrainsetId = groupedFlowRunSwitchListByTrainsetId.get(matchTrainsetId);

            Map<String, FlowUtil.FlowRunSwitchPath> startFlowRunSwitchPathMap;
            Map<String, FlowUtil.FlowRunSwitchPath> endFlowRunSwitchPathMap;

            if (flowRunsByTrainsetId != null) {
                List<FlowUtil.FlowRunSwitchPath> flowRunSwitchPaths = FlowUtil.analyseFlowRunSwitchPaths(
                    flowRunsByTrainsetId,
                    flowRunSwitchByTrainsetId == null ? new ArrayList<>() : flowRunSwitchByTrainsetId
                );
                startFlowRunSwitchPathMap = CommonUtils.collectionToMap(flowRunSwitchPaths, v -> v.getStartFlowId());
                endFlowRunSwitchPathMap = CommonUtils.collectionToMap(flowRunSwitchPaths, v -> v.getEndFlowId());
            } else {
                startFlowRunSwitchPathMap = new HashMap<>();
                endFlowRunSwitchPathMap = new HashMap<>();
            }


            List<FlowCondition> curPageFlowConditions = matchedConditions.stream().filter(flowCondition -> {
                String conditionId = flowCondition.getConditionId();
                //?????????????????????????????????
                return flowTypeCodeByFlowPageCode.contains(groupedFlowInfoByCondition.get(conditionId).get(0).getFlowTypeCode());
            }).collect(Collectors.toList());

            //?????????????????????????????????????????????????????????????????????
            // 1????????????????????????
            // 2??????????????????????????????????????????
            Map<String, FlowRun> flowRunMap = new HashMap<>();
            for (FlowCondition curPageFlowCondition : curPageFlowConditions) {
                String conditionId = curPageFlowCondition.getConditionId();
                List<FlowInfo> currentConditionFlows = groupedFlowInfoByCondition.get(conditionId);

                boolean needNew = true;
                for (FlowInfo currentConditionFlow : currentConditionFlows) {
                    String id = currentConditionFlow.getId();
                    if (startFlowRunSwitchPathMap.containsKey(id) || endFlowRunSwitchPathMap.containsKey(id)) {
                        FlowUtil.FlowRunSwitchPath flowRunSwitchPath = startFlowRunSwitchPathMap.containsKey(id) ? startFlowRunSwitchPathMap.get(id) : endFlowRunSwitchPathMap.get(id);
                        List<FlowRun> flowRuns = flowRunSwitchPath.getFlowRuns();
                        FlowRun curFlowRun = flowRuns.get(flowRuns.size() - 1);

                        flowRunMap.put(curPageFlowCondition.getDefaultFlowId(), curFlowRun);
                        needNew = false;
                        break;
                    }
                }
                if (needNew) {
                    FlowInfo flowInfo = groupedFlowInfoByCondition.get(curPageFlowCondition.getConditionId()).stream().filter(flow -> flow.getDefaultType() && !flow.getDeleted()).findAny().get();
                    FlowRun flowRun = new FlowRun();
                    flowRun.setFlowId(flowInfo.getId());
                    flowRun.setFlowTypeCode(flowInfo.getFlowTypeCode());
                    flowRun.setDayPlanId(dayPlanId);
                    flowRun.setTrainsetId(matchTrainsetId);
                    TrainsetPostionCur trainsetPostionCur = trainsetPostionCurMap.get(matchTrainsetId);
                    flowRun.setTrackCode(trainsetPostionCur.getTrackCode());
                    flowRun.setTrackName(trainsetPostionCur.getTrackName());
                    flowRun.setHeadTrackPositionCode(trainsetPostionCur.getHeadDirectionPlaCode());
                    flowRun.setHeadTrackPositionName(trainsetPostionCur.getHeadDirectionPla());
                    flowRun.setTailTrackPositionCode(trainsetPostionCur.getTailDirectionPlaCode());
                    flowRun.setTailTrackPositionName(trainsetPostionCur.getTailDirectionPla());
                    flowRun.setUnitCode(unitCode);
                    flowRun.setState(FlowRunStateEnum.NOT_STARTED.getValue());
                    flowRunMap.put(flowInfo.getId(), flowRun);
                }

            }

            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

            //?????????????????????????????????
            List<FlowInfo> curPageMatchedFlowList = allMatchedFlowList.stream().filter(flowInfo -> flowTypeCodeByFlowPageCode.contains(flowInfo.getFlowTypeCode())).collect(Collectors.toList());

            TempFlowRunInfoGroup tempFlowRunInfoGroup = new TempFlowRunInfoGroup();
            tempFlowRunInfoGroup.setTrainsetId(matchTrainsetId);
            tempFlowRunInfoGroup.setMatchedFlowList(curPageMatchedFlowList);
            tempFlowRunInfoGroup.setFlowRunMap(flowRunMap);
            matchingInfoGroupList.add(tempFlowRunInfoGroup);

        }

        Set<String> finalExistAllFlowRunIdSet = flowRunList.stream().map(FlowRun::getId).collect(Collectors.toSet());

        for (TempFlowRunInfoGroup group : matchingInfoGroupList) {
            List<String> flowRunIds = group.getMatchedFlowList().stream().map(
                flowInfo -> group.getFlowRunMap().get(flowInfo.getId()).getId()
            ).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            finalExistAllFlowRunIdSet.addAll(flowRunIds);
        }

        Map<String, List<FlowRun>> finalFlowRunListGroupByTrainsetId = new HashMap<>();
        for (TempFlowRunInfoGroup group : matchingInfoGroupList) {
            String curTrainsetId = group.getTrainsetId();
            if (!finalFlowRunListGroupByTrainsetId.containsKey(curTrainsetId)) {
                finalFlowRunListGroupByTrainsetId.put(curTrainsetId, new ArrayList<>());
            }
            // ????????????????????????
            List<FlowRun> flowRuns = group.getMatchedFlowList().stream().map(flowInfo -> group.getFlowRunMap().get(flowInfo.getId())).collect(Collectors.toList());

            finalFlowRunListGroupByTrainsetId.get(curTrainsetId).addAll(flowRuns);
        }
        groupedFlowRunListByTrainsetId.forEach((curTrainsetId, flowRuns) -> {
            if (!finalFlowRunListGroupByTrainsetId.containsKey(curTrainsetId)) {
                finalFlowRunListGroupByTrainsetId.put(curTrainsetId, new ArrayList<>());
            }
            List<FlowRun> existTrainFlowRuns = finalFlowRunListGroupByTrainsetId.get(curTrainsetId);
            Set<String> existTrainFlowRunIds = existTrainFlowRuns.stream().map(FlowRun::getId).collect(Collectors.toSet());
            for (FlowRun flowRun : flowRuns) {
                if (!existTrainFlowRunIds.contains(flowRun.getId())) {
                    existTrainFlowRuns.add(flowRun);
                }
            }
        });

        QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
        queryNodeRecord.setDayPlanId(dayPlanId);
        queryNodeRecord.setFlowRunIds(finalExistAllFlowRunIdSet.toArray(new String[0]));

        //?????????????????????????????????????????????????????????
        List<NodeRecordInfo> allNodeRecords = nodeRecordService.getNodeRecordInfoList(queryNodeRecord);
        //???????????????id??????
        Map<String, List<NodeRecordInfo>> groupedNodeRecordInfoListByFlowRunId = allNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getFlowRunId));

        //????????????????????????????????????????????????????????????
        List<FlowRunWithExtraInfo> flowRunWithExtraInfos = this.getFlowRunWithExtraInfos(new ArrayList<String>() {{
            add(dayPlanId);
        }}, null, null, null, null, null, null, null, finalExistAllFlowRunIdSet.toArray(new String[0]), unitCode, null);
        //???????????????id??????
        Map<String, FlowRunWithExtraInfo> flowRunWithExtraInfoMap = CommonUtils.collectionToMap(flowRunWithExtraInfos, FlowRun::getId);

        //??????????????????????????????
        List<FlowRunRecordInfo> flowRunRecordInfos = flowRunRecordService.getFlowRunRecordsByForceEnd(unitCode, dayPlanId, null, null, null);
        //???????????????id??????
        Map<String, FlowRunRecordInfo> flowRunRecordInfoMaps = CommonUtils.collectionToMap(flowRunRecordInfos, FlowRunRecordInfo::getFlowRunId);

        List<FlowRunInfoGroup> flowRunInfoGroups = matchingInfoGroupList.stream().map(matchingInfoGroup -> {
            FlowRunInfoGroup flowRunInfoGroup = new FlowRunInfoGroup();
            String matchTrainsetId = matchingInfoGroup.getTrainsetId();

            List<RuntimeRole> curTrainsetRuntimeRoles = groupedRuntimeRoleByTrainsetId.get(matchTrainsetId);
            if (curTrainsetRuntimeRoles == null) {
                curTrainsetRuntimeRoles = new ArrayList<>();
            }
            if (groupedRuntimeRoleByTrainsetId.get(nullTrainset) != null) {
                curTrainsetRuntimeRoles.addAll(groupedRuntimeRoleByTrainsetId.get(nullTrainset));
            }
            List<RuntimeRole> finalCurTrainsetRuntimeRoles = curTrainsetRuntimeRoles;
            flowRunInfoGroup.setFlowRunInfoForSimpleShows(sortFlowRunInfoForSimpleShowList(
                finalFlowRunListGroupByTrainsetId.get(matchTrainsetId).stream().map(flowRun -> {
                    String flowRunId = flowRun.getId();
                    FlowRunWithExtraInfo curFlowRunWithExtraInfo;
                    if (flowRunWithExtraInfoMap.containsKey(flowRunId)) {
                        curFlowRunWithExtraInfo = flowRunWithExtraInfoMap.get(flowRunId);
                    } else {
                        curFlowRunWithExtraInfo = new FlowRunWithExtraInfo();
                        BeanUtils.copyProperties(flowRun, curFlowRunWithExtraInfo);
                    }
                    List<NodeRecordInfo> curFlowRunNodeRecordList;
                    if (groupedNodeRecordInfoListByFlowRunId.containsKey(flowRunId)) {
                        curFlowRunNodeRecordList = groupedNodeRecordInfoListByFlowRunId.get(flowRunId);
                    } else {
                        curFlowRunNodeRecordList = new ArrayList<>();
                    }
                    return getFlowRunInfoForSimpleShow(
                        curFlowRunWithExtraInfo,
                        flowInfoMap.get(flowRun.getFlowId()),
                        curFlowRunNodeRecordList,
                        staffId,
                        finalCurTrainsetRuntimeRoles,
                        flowRunRecordInfoMaps.get(flowRun.getId())
                    );
                }).collect(Collectors.toList()),
                switchTargetFlowRunIdList
            ));

            flowRunInfoGroup.setTrainsetId(matchTrainsetId);
            return flowRunInfoGroup;
        }).collect(Collectors.toList());

        Map<String, FlowRunInfoGroup> flowRunInfoGroupMap = CommonUtils.collectionToMap(flowRunInfoGroups, FlowRunInfoGroup::getTrainsetId);
        Map<String, TempFlowRunInfoGroup> tempFlowRunInfoGroupMap = CommonUtils.collectionToMap(matchingInfoGroupList, TempFlowRunInfoGroup::getTrainsetId);
        groupedFlowRunListByTrainsetId.forEach((trainsetId, flowRunsByTrainsetId) -> {
            //????????????????????????
            FlowRunInfoGroup flowRunInfoGroup = flowRunInfoGroupMap.get(trainsetId);//matchingInfoGroupList
            if (flowRunInfoGroup == null) {
                flowRunInfoGroup = new FlowRunInfoGroup();
                flowRunInfoGroup.setTrainsetId(trainsetId);
                List<FlowRunInfoForSimpleShow> flowRunInfoForSimpleShows = new ArrayList<>();
                //????????????????????????
                for (FlowRun flowRun : flowRunsByTrainsetId) {
                    TempFlowRunInfoGroup tempFlowRunInfoGroup = tempFlowRunInfoGroupMap.get(trainsetId);
                    Set<String> addedFlowRunIds;
                    if (tempFlowRunInfoGroup != null) {
                        addedFlowRunIds = tempFlowRunInfoGroup.getFlowRunMap().values().stream().map(v -> v.getId()).collect(Collectors.toSet());
                    } else {
                        addedFlowRunIds = new HashSet<>();
                    }
                    if (flowTypeCodeByFlowPageCode.contains(flowRun.getFlowTypeCode()) && !addedFlowRunIds.contains(flowRun.getId())) {
                        flowRunInfoForSimpleShows.add(getFlowRunInfoForSimpleShow(
                            flowRunWithExtraInfoMap.get(flowRun.getId()),
                            flowInfoMap.get(flowRun.getFlowId()),
                            groupedNodeRecordInfoListByFlowRunId.get(flowRun.getId()),
                            staffId,
                            groupedRuntimeRoleByTrainsetId.get(trainsetId),
                            flowRunRecordInfoMaps.get(flowRun.getId())
                        ));
                    }
                }
                flowRunInfoGroup.setFlowRunInfoForSimpleShows(flowRunInfoForSimpleShows);
                if (flowRunInfoGroup.getFlowRunInfoForSimpleShows().size() > 0) {
                    flowRunInfoGroups.add(flowRunInfoGroup);
                }

            }

        });


        List<FlowTypeInfoWithPackets> flowTypeAndPacketList = flowTypeService.getFlowTypeAndPacket(unitCode);
        StringBuilder errorWarnInfo = new StringBuilder();
        for (
            ErrorFlowFunInfo errorFlowFunInfo : errorFlowFunInfos) {

            Set<String> flowRunTypeCode = errorFlowFunInfo.getFlowRuns().stream().map(flowRun -> flowRun.getFlowTypeCode()).collect(Collectors.toSet());
            Set<String> flowRunFlowTypeNameList = flowTypeAndPacketList.stream().filter(flowTypeInfoWithPackets -> flowRunTypeCode.contains(flowTypeInfoWithPackets.getCode())).map(v -> v.getName()).collect(Collectors.toSet());

            Set<String> matchedFlowTypeNameList = flowTypeAndPacketList.stream().filter(flowTypeInfoWithPackets -> errorFlowFunInfo.getMatchedFlowTypeCodes().contains(flowTypeInfoWithPackets.getCode())).map(v -> v.getName()).collect(Collectors.toSet());
            errorWarnInfo.append(",").append(errorFlowFunInfo.getTrainsetId()).append("???????????????").append(flowRunFlowTypeNameList).append("???????????????????????????????????????????????????????????????").append(matchedFlowTypeNameList).append(",??????????????????????????????????????????????????????????????????????????????????????????");
        }

        for (
            FlowRunInfoGroup flowRunInfoGroup : flowRunInfoGroups) {
            for (FlowRunInfoForSimpleShow flowRunInfoForSimpleShow : flowRunInfoGroup.getFlowRunInfoForSimpleShows()) {
                for (NodeInfoForSimpleShow nodeInfoForSimpleShow : flowRunInfoForSimpleShow.getNodeList()) {
                    nodeInfoForSimpleShow.setNodeRecords(nodeInfoForSimpleShow.getNodeRecords().stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList()));
                }
            }
        }
        if (errorWarnInfo.length() > 0) {
            Log log = new Log();
            log.setModule("??????????????????");
            log.setType("2");
            log.setContent(errorWarnInfo.substring(1, errorWarnInfo.length()));
            logService.addLog(log);
        }

        FlowRunInfo flowRunInfo = new FlowRunInfo();
        flowRunInfo.setErrorFlowFunInfos(errorWarnInfo.length() == 0 ? null : errorWarnInfo.substring(1, errorWarnInfo.length()));
        //??????????????????????????????
        List<FlowRunInfoGroup> flowRunInfoGroupList = JSON.parseArray(JSON.toJSONString(flowRunInfoGroups, SerializerFeature.DisableCircularReferenceDetect), FlowRunInfoGroup.class);
        //0>-1>1
        flowRunInfo.setFlowRunInfoGroups(

            sortFlowRunInfoGroups(flowRunInfoGroupList, flowTypeAndPacketList, flowInfoList));
        return flowRunInfo;
    }

    public List<FlowRunInfoGroup> sortFlowRunInfoGroups(List<FlowRunInfoGroup> flowRunInfoGroupList, List<FlowTypeInfoWithPackets> flowTypeInfoWithPackets, List<FlowInfo> flowInfoList) {
        //??????????????????????????????>??????>??????>??????>???????????????
        //????????????
        Set<String> basicFlowTypes = flowTypeInfoWithPackets.stream().filter(v -> StringUtils.isBlank(v.getParentFlowTypeCode())).map(code -> {
            return EnumUtils.findEnum(BasicFlowTypeEnum.class, BasicFlowTypeEnum::getValue, code.getCode());
        }).collect(Collectors.toSet()).stream().map(v -> v.getValue()).collect(Collectors.toSet());

        //????????????
        Set<String> narrowFlowTypeList = flowTypeInfoWithPackets.stream().filter(v -> v.getConfigType() != null && v.getConfigType().equals(FlowTypeConfigTypeEnum.PACKET_NARROW.getValue())).collect(Collectors.toList()).stream().map(v -> v.getCode()).collect(Collectors.toSet());

        //????????????
        Set<String> independentFlowTypeList = flowTypeInfoWithPackets.stream().filter(v -> v.getConfigType() != null && v.getConfigType().equals(FlowTypeConfigTypeEnum.PACKET_INDEPENDENT.getValue())).collect(Collectors.toList()).stream().map(v -> v.getCode()).collect(Collectors.toSet());

        //????????????
        Set<String> existFlowTypeList = flowTypeInfoWithPackets.stream().filter(v -> v.getConfigType() != null && v.getConfigType().equals(FlowTypeConfigTypeEnum.PACKET_EXIST.getValue())).collect(Collectors.toList()).stream().map(v -> v.getCode()).collect(Collectors.toSet());

        List<String> flowIdList = flowInfoList.stream().filter(v -> v.getFlowTypeCode().equals(BasicFlowTypeEnum.REPAIR_ONE.getValue()) && StringUtils.isNotBlank(v.getRepairType()) && v.getRepairType().equals(RepairTypeEnum.MachineRepair.getValue())).map(v -> v.getId()).collect(Collectors.toList());

        //????????????>?????????>????????????
        flowRunInfoGroupList.forEach(flowRunInfoGroup -> {
            flowRunInfoGroup.getFlowRunInfoForSimpleShows().sort((o1, o2) -> {
                FlowRunStateLevel flowRunStateLevel1 = EnumUtils.findEnum(FlowRunStateLevel.class, FlowRunStateLevel::getValue, o1.getState());
                FlowRunStateLevel flowRunStateLevel2 = EnumUtils.findEnum(FlowRunStateLevel.class, FlowRunStateLevel::getValue, o2.getState());
                int state1 = flowRunStateLevel1.getLabel();
                int state2 = flowRunStateLevel2.getLabel();
                if (state1 == state2) {
                    String flowTypeCode1 = o1.getFlowTypeCode();
                    String flowTypeCode2 = o2.getFlowTypeCode();
                    int flowTypeLevel1 = basicFlowTypes.contains(flowTypeCode1) && !flowIdList.contains(o1.getFlowId()) ? 1 : existFlowTypeList.contains(flowTypeCode1) ? 2 : narrowFlowTypeList.contains(flowTypeCode1) ? 3 : independentFlowTypeList.contains(flowTypeCode1) ? 4 : 5;
                    int flowTypeLevel2 = basicFlowTypes.contains(flowTypeCode2) && !flowIdList.contains(o2.getFlowId()) ? 1 : existFlowTypeList.contains(flowTypeCode2) ? 2 : narrowFlowTypeList.contains(flowTypeCode2) ? 3 : independentFlowTypeList.contains(flowTypeCode2) ? 4 : 5;
                    if (flowTypeLevel1 > flowTypeLevel2) {
                        return 1;
                    } else if (flowTypeLevel1 < flowTypeLevel2) {
                        return -1;
                    }
                    return 0;
                } else if (state1 > state2) {
                    return 1;
                } else {
                    return -1;
                }
            });
        });
        return flowRunInfoGroupList;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Data
    static class TempFlowRunInfoGroup {
        private String trainsetId;
        /**
         * ????????????????????????????????????
         */
        private List<FlowInfo> matchedFlowList;
        /**
         * ?????????????????????????????????????????????????????????????????????????????????
         */
        private Map<String, FlowRun> flowRunMap;
    }

// /**
//  * ?????????????????????????????????????????????????????????????????????
//  * 1????????????????????????
//  * 2??????????????????????????????????????????
//  */
// @Data
// static class TempFlowRunWithFlowInfo {
//     private FlowInfo flowInfo;
//     private FlowRun flowRun;
// }

    @Data
    public static class ErrorFlowFunInfo {
        private String trainsetId;
        private List<FlowRun> flowRuns;
        private Set<String> matchedFlowTypeCodes;
    }

    @Data
    public static class MatchedResultByKeyWord {
        private String keyWord;
        private ZtTaskPacketEntity packetEntity;
    }

    @Data
    public static class MatchedResultByRepairType {
        private String repairType;
        private ZtTaskPacketEntity packetEntity;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param packetCode
     * @param matchedResultByConditions
     * @return
     */
    public MatchedResultByCondition getBestOne(String packetCode, List<MatchedResultByCondition> matchedResultByConditions) {
        if (matchedResultByConditions == null || matchedResultByConditions.size() == 0) {
            return null;
        } else if (matchedResultByConditions.size() == 1) {
            return matchedResultByConditions.get(0);
        }
        List<Comparator<MatchedResultByCondition>> comparators = new ArrayList<>();
        // ????????????(??????????????????????????????)
        comparators.add((o1, o2) -> {
            TrainConditionValue trainConditionValueO1 = o1.getFlowCondition().getTrainConditionValue();
            TrainConditionValue trainConditionValueO2 = o2.getFlowCondition().getTrainConditionValue();
            TrainInfoConditionLevelEnum minLevelO1 = FlowConfigSingleConditionUtil.getMinLevel(trainConditionValueO1);
            TrainInfoConditionLevelEnum minLevelO2 = FlowConfigSingleConditionUtil.getMinLevel(trainConditionValueO2);
            return minLevelO1.compareTo(minLevelO2);
        });
        //??????(????????????????????????) 1?????? -1??????
        comparators.add((o1, o2) -> {
            String trainConditionValueO1 = o1.getFlowCondition().getRepairType();
            String trainConditionValueO2 = o2.getFlowCondition().getRepairType();
            if (StringUtils.isBlank(trainConditionValueO1) && !StringUtils.isBlank(trainConditionValueO2)) {
                return 1;
            } else if (!StringUtils.isBlank(trainConditionValueO1) && StringUtils.isBlank(trainConditionValueO2)) {
                return -1;
            } else {
                return 0;
            }
        });
        // ?????????(??????????????????????????????)
        comparators.add((o1, o2) -> {
            List<MatchedResultByKeyWord> matchedResultByKeyWords1 = o1.getMatchedResultByKeyWordList().stream().filter(v -> v.getPacketEntity().getPacketCode().equals(packetCode)).collect(Collectors.toList());
            List<MatchedResultByKeyWord> matchedResultByKeyWords2 = o2.getMatchedResultByKeyWordList().stream().filter(v -> v.getPacketEntity().getPacketCode().equals(packetCode)).collect(Collectors.toList());
            return matchedResultByKeyWords2.size() - matchedResultByKeyWords1.size();
        });
        // ????????????????????????
        comparators.add((o1, o2) -> new Long(
                o2.getFlowCondition().getDefaultFlowCreateTime().getTime() - o1.getFlowCondition().getDefaultFlowCreateTime().getTime()
            ).intValue()
        );

        matchedResultByConditions.sort((v1, v2) -> {
            Iterator<Comparator<MatchedResultByCondition>> iterator = comparators.iterator();
            int rs = 0;
            while (rs == 0 && iterator.hasNext()) {
                rs = iterator.next().compare(v1, v2);
            }
            return rs;
        });
        return matchedResultByConditions.get(0);
    }

    @Data
    public static class MatchedResultByCondition {
        private String conditionId;
        private List<MatchedResultByKeyWord> matchedResultByKeyWordList;
        private List<MatchedResultByRepairType> matchedResultByRepairTypeList;
        private FlowCondition flowCondition;
    }

    public List<FlowRunInfoForSimpleShow> sortFlowRunInfoForSimpleShowList(List<FlowRunInfoForSimpleShow> flowRunInfoForSimpleShows, Set<String> switchFlowRunId) {
        Map<String, Integer> map = new HashMap<>();
        map.put(FlowRunStateEnum.RUNNING.getValue(), 1);
        map.put(FlowRunStateEnum.NOT_STARTED.getValue(), 2);
        map.put(FlowRunStateEnum.ENDED.getValue(), 3);

        List<FlowRunInfoForSimpleShow> switchFlowRunList = flowRunInfoForSimpleShows.stream().filter(flowRunInfoForSimpleShow -> switchFlowRunId.contains(flowRunInfoForSimpleShow.getId())).collect(Collectors.toList());
        flowRunInfoForSimpleShows.removeAll(switchFlowRunList);
        flowRunInfoForSimpleShows.sort((o1, o2) -> {
            int lever1 = map.get(o1.getState());
            int lever2 = map.get(o2.getState());
            if (lever1 != lever2) {
                return lever1 - lever2;
            } else {
                // ?????????????????????????????????????????????????????????
                if (!o1.getState().equals(FlowRunStateEnum.NOT_STARTED.getValue())) {
                    return new Long(o1.getStartTime().getTime() - o2.getStartTime().getTime()).intValue();
                } else {
                    return 0;
                }
            }

        });
        flowRunInfoForSimpleShows.addAll(switchFlowRunList);
        return flowRunInfoForSimpleShows;
    }

    /**
     * ???????????????????????????
     */
    public List<MatchedFlowTypeInTrain> getMatchedFlowTypeCode(List<ZtTaskPacketEntity> taskPacketEntityList, String unitCode) {
        List<MatchedFlowTypeInTrain> matchedFlowTypeInTrains = new ArrayList<>();
        //??????????????????????????????
        Map<String, List<ZtTaskPacketEntity>> groupedTaskPacketLists = taskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
        //?????????????????????????????????
        List<FlowTypeInfoWithPackets> flowTypeInfoWithPackets = flowTypeService.getFlowTypeAndPacket(unitCode);
        //????????????
        List<FlowTypeInfoWithPackets> independentFlowTypeList = flowTypeInfoWithPackets.stream().filter(v -> v.getConfigType() != null && v.getConfigType().equals(FlowTypeConfigTypeEnum.PACKET_INDEPENDENT.getValue())).collect(Collectors.toList());
        //????????????
        Set<BasicFlowTypeEnum> basicFlowTypes = flowTypeInfoWithPackets.stream().filter(v -> StringUtils.isBlank(v.getParentFlowTypeCode())).map(code -> {
            return EnumUtils.findEnum(BasicFlowTypeEnum.class, BasicFlowTypeEnum::getValue, code.getCode());
        }).collect(Collectors.toSet());
        //????????????
        Map<BasicFlowTypeEnum, List<FlowTypeInfoWithPackets>> groupedNarrowFlowTypeList = flowTypeInfoWithPackets.stream().filter(v -> v.getConfigType() != null && v.getConfigType().equals(FlowTypeConfigTypeEnum.PACKET_NARROW.getValue())).collect(Collectors.groupingBy((flowTypeInfo) -> {
            return EnumUtils.findEnum(BasicFlowTypeEnum.class, BasicFlowTypeEnum::getValue, flowTypeInfo.getParentFlowTypeCode());
        }));
        //??????????????????
        List<FlowTypeInfoWithPackets> existFlowTypeList = flowTypeInfoWithPackets.stream().filter(v -> v.getConfigType() != null && v.getConfigType().equals(FlowTypeConfigTypeEnum.PACKET_EXIST.getValue())).collect(Collectors.toList());


        for (List<ZtTaskPacketEntity> taskPacketList : groupedTaskPacketLists.values()) {
            String trainsetId = taskPacketList.get(0).getTrainsetId();
            //??????????????????????????????

            Set<String> matchedIndependentTypeInfoPacketCodes = new HashSet<>();
            for (FlowTypeInfoWithPackets independentTypeInfo : independentFlowTypeList) {
                //??????????????????????????????,??????????????????????????????????????????code
                if (FlowTypeConfigTypeEnum.PACKET_INDEPENDENT.isFlowTypeMatchedByTaskPackets(independentTypeInfo, taskPacketList)) {
                    MatchedFlowTypeInTrain matchedFlowTypeInTrain = new MatchedFlowTypeInTrain();
                    matchedFlowTypeInTrain.setTrainsetId(taskPacketList.get(0).getTrainsetId());
                    matchedFlowTypeInTrain.setFlowTypeCode(independentTypeInfo.getCode());
                    matchedFlowTypeInTrain.setPacketEntityList(
                        taskPacketList.stream().filter(
                            v -> FlowTypeConfigTypeEnum.PACKET_INDEPENDENT.isTaskPacketMatchedByFlowType(v, independentTypeInfo)
                        ).collect(Collectors.toList())
                    );
                    matchedFlowTypeInTrains.add(matchedFlowTypeInTrain);
                    matchedIndependentTypeInfoPacketCodes.addAll(matchedFlowTypeInTrain
                        .getPacketEntityList().stream()
                        .map(ZtTaskPacketEntity::getPacketCode)
                        .collect(Collectors.toList())
                    );
                }
            }


            //?????????????????????????????????
            List<ZtTaskPacketEntity> filteredTaskPacketList = taskPacketList.stream().filter(v -> !matchedIndependentTypeInfoPacketCodes.contains(v.getPacketCode())).collect(Collectors.toList());


            //?????????????????????????????????
            for (FlowTypeInfoWithPackets existFlowType : existFlowTypeList) {
                BasicFlowTypeEnum parentFlowTypeEnum = EnumUtils.findEnum(BasicFlowTypeEnum.class, BasicFlowTypeEnum::getValue, existFlowType.getParentFlowTypeCode());
                if (parentFlowTypeEnum != null && parentFlowTypeEnum.match(filteredTaskPacketList)) {
                    if (FlowTypeConfigTypeEnum.PACKET_EXIST.isFlowTypeMatchedByTaskPackets(existFlowType, filteredTaskPacketList)) {
                        MatchedFlowTypeInTrain matchedNarrowFlowTypeInTrain = new MatchedFlowTypeInTrain();
                        matchedNarrowFlowTypeInTrain.setTrainsetId(trainsetId);
                        matchedNarrowFlowTypeInTrain.setFlowTypeCode(existFlowType.getCode());
                        matchedNarrowFlowTypeInTrain.setPacketEntityList(filteredTaskPacketList.stream()
                            .filter(v -> FlowTypeConfigTypeEnum.PACKET_EXIST.isTaskPacketMatchedByFlowType(v, existFlowType))
                            .collect(Collectors.toList())
                        );
                        matchedFlowTypeInTrains.add(matchedNarrowFlowTypeInTrain);
                    }
                }
            }

            Set<BasicFlowTypeEnum> allBasicFlowTypeList = new HashSet<>();
            allBasicFlowTypeList.addAll(basicFlowTypes);
            allBasicFlowTypeList.addAll(groupedNarrowFlowTypeList.keySet());

            //???????????????????????????????????? ???????????????????????????????????????????????????????????????????????????
            for (BasicFlowTypeEnum basicTypeInfo : allBasicFlowTypeList) {
                String flowTypeCode = null;
                Set<String> matchedPacketCodesByFlowType = null;
                if (basicTypeInfo != null && !basicTypeInfo.equals(BasicFlowTypeEnum.PLANLESS_KEY) && basicTypeInfo.match(filteredTaskPacketList)) {
                    Set<String> filteredPacketCodes = filteredTaskPacketList.stream().filter(basicTypeInfo::judgeTask).collect(Collectors.toList()).stream().map(ZtTaskPacketEntity::getPacketCode).collect(Collectors.toSet());
                    List<ZtTaskPacketEntity> filterPacketList = taskPacketList.stream().filter(v -> filteredPacketCodes.contains(v.getPacketCode())).collect(Collectors.toList());
                    MatchSubFlowTypeResult matchSubFlowTypeResult = getSubNarrowFlowTypeInfoList(groupedNarrowFlowTypeList.get(basicTypeInfo), filterPacketList);
                    if (matchSubFlowTypeResult.getResult()) {
                        flowTypeCode = matchSubFlowTypeResult.getMatchedSubFlowTypeCode();
                        matchedPacketCodesByFlowType = matchSubFlowTypeResult.getFilteredSubPacketCodes();
                    } else if (basicFlowTypes.contains(basicTypeInfo)) {// ???????????????????????????????????????????????????????????????????????????????????????
                        flowTypeCode = basicTypeInfo.getValue();
                        matchedPacketCodesByFlowType = filteredPacketCodes;
                    }
                }

                //????????????????????????????????????
                if (flowTypeCode != null && matchedPacketCodesByFlowType != null) {
                    MatchedFlowTypeInTrain matchedNarrowFlowTypeInTrain = new MatchedFlowTypeInTrain();
                    matchedNarrowFlowTypeInTrain.setTrainsetId(trainsetId);
                    matchedNarrowFlowTypeInTrain.setFlowTypeCode(flowTypeCode);

                    matchedNarrowFlowTypeInTrain.setPacketEntityList(getPacketEntityListByPacketCodes(matchedPacketCodesByFlowType, taskPacketList));
                    matchedFlowTypeInTrains.add(matchedNarrowFlowTypeInTrain);
                }
            }
        }
        return matchedFlowTypeInTrains;
    }

    /**
     * ???????????????code???????????????
     */
    public List<ZtTaskPacketEntity> getPacketEntityListByPacketCodes(Set<String> packetCodes, List<ZtTaskPacketEntity> ztTaskPacketEntityList) {
        List<ZtTaskPacketEntity> packetEntities = new ArrayList<>();
        for (ZtTaskPacketEntity ztTaskPacketEntity : ztTaskPacketEntityList) {
            if (packetCodes.contains(ztTaskPacketEntity.getPacketCode())) {
                packetEntities.add(ztTaskPacketEntity);
            }
        }
        return packetEntities;
    }

    @Data
    static class MatchSubFlowTypeResult {
        private Boolean result = false;
        private String matchedSubFlowTypeCode;
        private Set<String> filteredSubPacketCodes;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param flowTypeInfoWithPacketList ??????????????????
     * @param taskPacketEntityList       ?????????????????????
     * @return
     */
    public MatchSubFlowTypeResult getSubNarrowFlowTypeInfoList(List<FlowTypeInfoWithPackets> flowTypeInfoWithPacketList, List<ZtTaskPacketEntity> taskPacketEntityList) {
        if (flowTypeInfoWithPacketList != null) {
            // ???????????????????????????????????????
            for (FlowTypeInfoWithPackets flowTypeInfoWithPackets : flowTypeInfoWithPacketList) {
                if (FlowTypeConfigTypeEnum.PACKET_NARROW.isFlowTypeMatchedByTaskPackets(
                    flowTypeInfoWithPackets,
                    taskPacketEntityList
                )) {
                    MatchSubFlowTypeResult matchSubFlowTypeResult = new MatchSubFlowTypeResult();
                    matchSubFlowTypeResult.setResult(true);
                    matchSubFlowTypeResult.setMatchedSubFlowTypeCode(flowTypeInfoWithPackets.getCode());
                    matchSubFlowTypeResult.setFilteredSubPacketCodes(
                        taskPacketEntityList.stream().filter(
                            v -> FlowTypeConfigTypeEnum.PACKET_NARROW.isTaskPacketMatchedByFlowType(v, flowTypeInfoWithPackets)
                        ).map(ZtTaskPacketEntity::getPacketCode).collect(Collectors.toSet())
                    );
                    return matchSubFlowTypeResult;
                }
            }
        }
        MatchSubFlowTypeResult matchSubFlowTypeResult = new MatchSubFlowTypeResult();
        matchSubFlowTypeResult.setResult(false);
        return matchSubFlowTypeResult;
    }

    @Data
    public static class MatchedFlowTypeInTrain {
        String trainsetId;
        String flowTypeCode;
        List<ZtTaskPacketEntity> packetEntityList;
    }

    @Override
    public FlowRunInfoForGraphShow getFlowDisposeGraph(String flowRunId) {
        List<FlowRunWithExtraInfo> flowRunWithExtraInfos = this.getFlowRunWithExtraInfos(null, null, null, null, null, null, null, null, new String[]{flowRunId}, null, null);
        if (flowRunWithExtraInfos.size() == 0) {
            throw new RuntimeException("????????????");
        }
        String unitCode = ContextUtils.getUnitCode();
        List<FlowRunRecordInfo> flowRunRecordInfos = flowRunRecordService.getFlowRunRecordsByForceEnd(unitCode, null, flowRunId, null, null);
        Map<String, FlowRunRecordInfo> flowRunRecordInfoByForceEndToMaps = CommonUtils.collectionToMap(flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.REJECT_END.getValue())).collect(Collectors.toList()), FlowRunRecordInfo::getFlowRunId);

        FlowRunWithExtraInfo flowRunWithExtraInfo = flowRunWithExtraInfos.get(0);
        FlowRunInfoForGraphShow flowRunInfoForGraphShow = new FlowRunInfoForGraphShow();
        //???????????????????????????????????????
        BeanUtils.copyProperties(flowRunWithExtraInfo, flowRunInfoForGraphShow);

        //??????????????????id??????????????????
        QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
        queryNodeRecord.setFlowRunIds(new String[]{flowRunId});
        List<NodeRecordInfo> nodeRecordInfos = nodeRecordService.getNodeRecordInfoList(queryNodeRecord);


        //??????????????????
        FlowInfo flowInfo = flowService.getFlowInfoByFlowId(flowRunWithExtraInfo.getFlowId());
        //????????????
        List<NodeInfo> sortedNodes = FlowUtil.getSortedNodes(flowInfo);
        flowInfo.setNodes(sortedNodes);

        List<RuntimeRole> runtimeRoles = this.getTaskPostList(flowRunWithExtraInfo.getDayPlanId(), flowRunWithExtraInfo.getUnitCode(), UserUtil.getUserInfo().getStaffId(), flowRunWithExtraInfo.getTrainsetId());

        FlowInfoWithNodeRecords flowInfoWithNodeRecords = new FlowInfoWithNodeRecords();
        BeanUtils.copyProperties(flowInfo, flowInfoWithNodeRecords);
        //?????????????????????????????????????????????
        List<NodeWithRecord> nodeWithRecords = new ArrayList<>();
        for (NodeInfo node : flowInfo.getNodes()) {
            NodeWithRecord nodeWithRecord = new NodeWithRecord();
            BeanUtils.copyProperties(node, nodeWithRecord);
            setNodeWithRecord(nodeWithRecord, nodeRecordInfos, null, runtimeRoles, flowInfo, flowRunInfoForGraphShow);
            nodeWithRecord.setNodeRecords(nodeWithRecord.getNodeRecords().stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList()));
            nodeWithRecords.add(nodeWithRecord);
        }
        flowInfoWithNodeRecords.setNodes(nodeWithRecords);
        flowRunInfoForGraphShow.setFlowInfo(flowInfoWithNodeRecords);
        flowRunInfoForGraphShow.setFlowRunRecordInfo(flowRunRecordInfoByForceEndToMaps.get(flowRunInfoForGraphShow.getId()));
        return flowRunInfoForGraphShow;
    }

    @Override
    public List<NodeInfoForSimpleShow> getFlowDisposeSimple(String flowRunId) {
        List<FlowRunWithExtraInfo> flowRunWithExtraInfos = this.getFlowRunWithExtraInfos(null, null, null, null, null, null, null, null, new String[]{flowRunId}, null, null);
        if (flowRunWithExtraInfos.size() == 0) {
            throw new RuntimeException("????????????");
        }
        FlowRunWithExtraInfo flowRunWithExtraInfo = flowRunWithExtraInfos.get(0);
        //??????????????????
        FlowInfo flowInfo = flowService.getFlowInfoByFlowId(flowRunWithExtraInfo.getFlowId());
        //????????????
        List<NodeInfo> sortedNodes = FlowUtil.getSortedNodes(flowInfo);
        flowInfo.setNodes(sortedNodes);

        //??????????????????id??????????????????
        QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
        queryNodeRecord.setFlowRunIds(new String[]{flowRunId});
        List<NodeRecordInfo> nodeRecordInfos = nodeRecordService.getNodeRecordInfoList(queryNodeRecord);

        //?????????????????????????????????????????????
        List<NodeInfoForSimpleShow> nodeInfoForSimpleShows = new ArrayList<>();
        for (NodeInfo node : flowInfo.getNodes()) {
            NodeInfoForSimpleShow nodeInfoForSimpleShow = new NodeInfoForSimpleShow();
            BeanUtils.copyProperties(node, nodeInfoForSimpleShow);
            setNodeWithRecord(nodeInfoForSimpleShow, nodeRecordInfos, UserUtil.getUserInfo().getStaffId(), new ArrayList<>(), flowInfo, flowRunWithExtraInfo);
            nodeInfoForSimpleShow.setNodeRecords(nodeInfoForSimpleShow.getNodeRecords().stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList()));

            //???????????????
            if (StringUtils.isNotBlank(node.getChildFlowId())) {
                List<NodeInfoForSimpleShow> subNodeInfoForSimpleShows = new ArrayList<>();
                FlowInfo subFlowInfo = flowService.getFlowInfoByFlowId(flowRunWithExtraInfo.getFlowId());
                List<NodeInfo> subSortedNodes = FlowUtil.getSortedNodes(flowInfo);
                subFlowInfo.setNodes(subSortedNodes);
                for (NodeInfo subFlowInfoNode : subFlowInfo.getNodes()) {
                    NodeInfoForSimpleShow subNodeInfoForSimpleShow = new NodeInfoForSimpleShow();
                    BeanUtils.copyProperties(subFlowInfoNode, subNodeInfoForSimpleShow);
                    setNodeWithRecord(subNodeInfoForSimpleShow, nodeRecordInfos, UserUtil.getUserInfo().getStaffId(), new ArrayList<>(), flowInfo, flowRunWithExtraInfo);
                    nodeInfoForSimpleShow.setNodeRecords(nodeInfoForSimpleShow.getNodeRecords().stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList()));
                    subNodeInfoForSimpleShows.add(subNodeInfoForSimpleShow);
                }
                nodeInfoForSimpleShow.setChildren(subNodeInfoForSimpleShows);
            }

            nodeInfoForSimpleShows.add(nodeInfoForSimpleShow);
        }
        return nodeInfoForSimpleShows;
    }

    @Override
    public List<FlowInfo> getSwitchoverFlowInfos(String flowRunId, String flowId, String unitCode) {
        //????????????????????????(??????????????????)
        if (StringUtils.isNotBlank(flowRunId)) {
            checkFlowRunEndCause(unitCode, flowRunId);
        }

        //??????flowId??????conditionId, ??????conditionId???????????????flowId???????????????
        List<Flow> flows = flowService.getConditionFlowsByFlowId(flowId, unitCode);
        if (flows.size() <= 0) {
            return new ArrayList<>();
        }
        String[] flowIds = flows.stream().map(v -> v.getId()).collect(Collectors.toList()).stream().toArray(String[]::new);
        QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
        queryFlowModelGeneral.setUnitCode(unitCode);
        queryFlowModelGeneral.setFlowIds(flowIds);
        queryFlowModelGeneral.setUsable(FlowUtil.booleanToString(true));
        List<FlowInfo> flowInfoList = flowService.getFlowInfoList(queryFlowModelGeneral);
        return flowInfoList;
    }

    @Override
    public List<FlowInfo> getForceSwitchoverFlow(String flowRunId, String flowId, String unitCode) {
        //??????flowRunId????????????????????????
        if (StringUtils.isNotBlank(flowRunId)) {
            checkFlowRunEndCause(unitCode, flowRunId);
        }
        Flow flow = flowService.getFlowByFlowId(flowId);
        String flowTypeCode = flow.getFlowTypeCode();

        List<String> flowTypeList = new ArrayList<>();
        List<FlowTypeWithSwitchover> flowTypeWithSwitchovers = Arrays.stream(FlowTypeWithSwitchover.values()).filter(v -> v.getValue().contains(flowTypeCode)).collect(Collectors.toList());
        if (flowTypeWithSwitchovers.size() > 0) {
            flowTypeList.addAll(Arrays.asList(flowTypeWithSwitchovers.get(0).getDescription().split(",")));
        } else {
            flowTypeList.add(flowTypeCode);
        }
        QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
        queryFlowModelGeneral.setUnitCode(unitCode);
        queryFlowModelGeneral.setUsable(FlowUtil.booleanToString(true));
        List<FlowInfo> flowInfoList = flowService.getFlowInfoList(queryFlowModelGeneral);
        return flowInfoList.stream().filter(flowInfo -> flowTypeList.contains(flowInfo.getFlowTypeCode())).filter(v -> !v.getId().equals(flowId)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void setSwitchoverFlow(FlowRunSwitchParam flowRunSwitchParam, String flowRunSwitchType) {
        FlowRun sourceFlowRun = flowRunSwitchParam.getFlowRun();
        User user = UserUtil.getUserInfo();
        if (StringUtils.isNotBlank(sourceFlowRun.getId())) {
            checkFlowRunEndCause(sourceFlowRun.getUnitCode(), sourceFlowRun.getId());
        } else {
            sourceFlowRun.setStartTime(new Date());
            sourceFlowRun.setStartWorkerName(user.getName());
            sourceFlowRun.setStartWorkerId(user.getStaffId());
            sourceFlowRun.setStartType(FlowRunStartTypeEnum.PERSON.getValue());
            flowRunMapper.insert(sourceFlowRun);
        }
        String sourceFlowId = sourceFlowRun.getFlowId();
        String targetFlowId = flowRunSwitchParam.getTargetFlowId();

        //??????????????????
        QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
        queryFlowModelGeneral.setUnitCode(sourceFlowRun.getUnitCode());
        queryFlowModelGeneral.setShowDeleted(true);
        queryFlowModelGeneral.setFlowIds(new String[]{sourceFlowId, targetFlowId});
        List<FlowInfo> flowInfoList = flowService.getFlowInfoList(queryFlowModelGeneral);

        List<FlowInfo> flowInfosBySourceFlowId = flowInfoList.stream().filter(v -> v.getId().equals(sourceFlowId)).collect(Collectors.toList());
        if (flowInfosBySourceFlowId.size() <= 0) {
            throw new RuntimeException("????????????????????????????????????!");
        }
        List<FlowInfo> flowInfosByTargetFlowId = flowInfoList.stream().filter(v -> v.getId().equals(targetFlowId)).collect(Collectors.toList());
        if (flowInfosByTargetFlowId.size() <= 0) {
            throw new RuntimeException("????????????????????????????????????!");
        }
        //?????????????????????????????????
        FlowInfo sourceFlowInfo = flowInfosBySourceFlowId.get(0);
        FlowInfo targetFlowInfo = flowInfosByTargetFlowId.get(0);

        //???????????????????????????????????????
        QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
        queryNodeRecord.setFlowRunIds(new String[]{sourceFlowRun.getId()});
        List<NodeRecordWithExtraInfo> sourceNodeRecordInfoList = nodeRecordMapper.getNodeRecordWithExtraInfo(queryNodeRecord);
        Map<String, List<NodeRecordWithExtraInfo>> groupSourceNodeRecordInfoByNodeId = sourceNodeRecordInfoList.stream().collect(
            Collectors.groupingBy(NodeRecordWithExtraInfo::getNodeId)
        );

        //????????????
        List<FlowUtil.MoveNodeInfo> moveNodeInfoList = FlowUtil.getMoveNodeInfos(sourceFlowInfo, targetFlowInfo);


        //?????????????????????
        FlowRunRecord flowRunRecord = new FlowRunRecord();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("remark", "??????????????????!");
        flowRunRecord.setRemark(JSONObject.toJSONString(jsonObject));
        flowRunRecord.setFlowRunId(sourceFlowRun.getId());
        flowRunRecord.setWorkerId(user.getStaffId());
        flowRunRecord.setWorkerName(user.getName());
        this.forceEndFlowRun(flowRunRecord, FlowRunRecordTypeEnum.FORCE_END.getValue());


        //?????????????????????
        FlowRun targetFlowRun = new FlowRun();
        BeanUtils.copyProperties(sourceFlowRun, targetFlowRun);
        targetFlowRun.setId(IdWorker.get32UUID());
        targetFlowRun.setStartTime(new Date());
        targetFlowRun.setFlowId(targetFlowId);
        targetFlowRun.setFlowTypeCode(targetFlowInfo.getFlowTypeCode());
        targetFlowRun.setState(FlowRunStateEnum.RUNNING.getValue());
        targetFlowRun.setId(IdWorker.get32UUID());
        flowRunMapper.insert(targetFlowRun);


        //?????????????????????????????????
        List<NodeRecordWithExtraInfo> addTargetNodeRecordWithExtraInfo = new ArrayList<>();
        moveNodeInfoList.forEach(moveNodeInfo -> {
            List<NodeRecordWithExtraInfo> nodeRecordWithExtraInfoByNodeId = groupSourceNodeRecordInfoByNodeId.get(moveNodeInfo.getSourceNodeId());
            if (nodeRecordWithExtraInfoByNodeId != null) {
                nodeRecordWithExtraInfoByNodeId.forEach(nodeRecordWithExtraInfo -> {
                    nodeRecordWithExtraInfo.setNodeId(moveNodeInfo.getTargetNodeId());
                    nodeRecordWithExtraInfo.setFlowRunId(targetFlowRun.getId());
                    nodeRecordWithExtraInfo.setId(IdWorker.get32UUID());
                    nodeRecordWithExtraInfo.getNodeRecordExtraInfoList().forEach(nodeRecordExtraInfo -> {
                        nodeRecordExtraInfo.setNodeRecordId(nodeRecordWithExtraInfo.getId());
                    });
                });
                addTargetNodeRecordWithExtraInfo.addAll(nodeRecordWithExtraInfoByNodeId);
            }
        });

        addTargetNodeRecordWithExtraInfo.forEach(nodeRecordWithExtraInfo -> {
            nodeRecordService.insertWithExtraInfo(nodeRecordWithExtraInfo);
        });

        //????????????????????????
        FlowRunSwitch flowRunSwitch = new FlowRunSwitch();
        flowRunSwitch.setDayPlanId(sourceFlowRun.getDayPlanId());
        flowRunSwitch.setSourceFlowRunId(sourceFlowRun.getId());
        flowRunSwitch.setSourceFlowId(sourceFlowId);
        flowRunSwitch.setSourceFlowTypeCode(sourceFlowRun.getFlowTypeCode());

        flowRunSwitch.setTargetFlowRunId(targetFlowRun.getId());
        flowRunSwitch.setTargetFlowId(targetFlowRun.getFlowId());
        flowRunSwitch.setTargetFlowTypeCode(targetFlowRun.getFlowTypeCode());

        flowRunSwitch.setType(flowRunSwitchType);
        flowRunSwitch.setSwitchTime(new Date());
        flowRunSwitch.setWorkerId(user.getStaffId());
        flowRunSwitch.setWorkerName(user.getName());
        flowRunSwitch.setTrainsetId(sourceFlowRun.getTrainsetId());
        flowRunSwitchMapper.insert(flowRunSwitch);

        WorkerInfo workerInfo = new WorkerInfo();
        User userInfo = UserUtil.getUserInfo();
        workerInfo.setWorkerId(userInfo.getStaffId());
        workerInfo.setWorkerName(userInfo.getName());
        SysOrgan departmentOrgan = user.getDepartmentOrgan();
        if (departmentOrgan != null) {
            workerInfo.setTeamCode(departmentOrgan.getOrganCode());
            workerInfo.setTeamName(departmentOrgan.getOrganName());
        }
        innerFlowRunService.updateFlowRunActiveNodesState(targetFlowInfo, targetFlowRun, workerInfo, NodeRecordOperationTypeEnum.SWITCH);
    }


    private NodeDisposeCheckResult checkCouldDispose(
        String nodeId,
        FlowInfo flowInfo,
        FlowRun flowRun,
        List<? extends NodeRecordInfo> curFlowRunNodeRecords,
        String staffId,
        String roleId,
        boolean checkPower,
        boolean confirmSkip,
        boolean confirmIgnoreMinIntervalRestrict,
        boolean confirmCoverNodeRecord,
        List<PostAndRole> postAndRoleList
    ) {
        NodeDisposeCheckResult nodeDisposeCheckResult;

        // ??????????????????
        FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);

        // ????????????????????????
        NodeInfo nodeInfo = flowInfoAnalysis.getNodeMap().get(nodeId);
        String flowRunId = flowRun.getId();

        NodeWithRecordBase<NodeRecordInfo> nodeWithRecord = new NodeWithRecordBase<>();
        BeanUtils.copyProperties(nodeInfo, nodeWithRecord);
        nodeWithRecord.setNodeRecords(curFlowRunNodeRecords.stream().filter(v -> v.getNodeId().equals(nodeId)).collect(Collectors.toList()));
        List<RuntimeRole> runtimeRoles = getTaskPostList(flowRun.getDayPlanId(), flowRun.getUnitCode(), staffId, flowRun.getTrainsetId());

        Supplier<List<NodeRoleConfig>> couldDisposedRolesGetter = CacheUtil.getCachedDataGetter(() -> {
            if (!checkPower) {
                return nodeInfo.getRoleConfigs();
            } else {
                Set<String> runtimeRoleIdSet = runtimeRoles.stream().map(RoleBase::getRoleId).collect(Collectors.toSet());
                return nodeInfo.getRoleConfigs().stream().filter(v -> runtimeRoleIdSet.contains(v.getRoleId())).collect(Collectors.toList());
            }
        });

        Runnable doCheckPower = () -> {
            if (checkPower) {
                // ????????????????????????
                if (!FlowUtil.hasPowerToDispose(FlowUtil.getPrePairNodeInfoIfAfterPairNode(nodeId, flowInfo), runtimeRoles, flowRun.getFlowTypeCode())) {
                    throw RestRequestException.normalFail("???????????????????????????????????????!");
                }
            }
            FlowUtil.CheckPairNodeDisposeRuntimeRoleResultEnum resultEnum = FlowUtil.getRuntimeRoleCheckerForPairNode(staffId, nodeId, flowInfo, curFlowRunNodeRecords, null, null).apply(roleId).getResultEnum();
            if (resultEnum.equals(FlowUtil.CheckPairNodeDisposeRuntimeRoleResultEnum.PAIR_NODE_ROLE_PASTED)) {
                String afterPairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId), 1);
                String afterPairNodeName = flowInfoAnalysis.getNodeMap().get(afterPairNodeId).getName();
                String errorMessage;
                if (roleId == null) {
                    if (couldDisposedRolesGetter.get().size() > 1) {
                        errorMessage = "?????????????????????????????????[" + afterPairNodeName + "]???????????????????????????????????????";
                    } else {
                        errorMessage = "??????????????????[" + afterPairNodeName + "]???????????????????????????????????????";
                    }
                } else {
                    errorMessage = "?????????????????????????????????[" + afterPairNodeName + "]???????????????????????????????????????";
                }
                throw RestRequestException.normalFail(errorMessage);
            } else if (resultEnum.equals(FlowUtil.CheckPairNodeDisposeRuntimeRoleResultEnum.PREVIOUS_PAIR_NODE_NO_ROLE_RECORD)) {
                String prePairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId), 0);
                String prePairNodeName = flowInfoAnalysis.getNodeMap().get(prePairNodeId).getName();
                String errorMessage;
                if (roleId == null) {
                    errorMessage = "???????????????[" + prePairNodeName + "]??????????????????????????????";
                } else {
                    errorMessage = "??????????????????????????????[" + prePairNodeName + "]?????????????????????????????????????????????";
                }
                throw RestRequestException.normalFail(errorMessage);
            }
        };

        if (org.springframework.util.StringUtils.isEmpty(flowRunId)) {// ????????????
            // ?????????????????????????????????
            if (flowInfoAnalysis.getPreviousNodes(nodeId).size() == 0) {
                nodeDisposeCheckResult = new NodeDisposeCheckResult();
                nodeDisposeCheckResult.setNeedConfirmSkippedNodes(false);
                nodeDisposeCheckResult.setNeedSkippedNodes(new ArrayList<>());
                nodeDisposeCheckResult.setDisposeAfterSkip(false);

                nodeDisposeCheckResult.setNeedConfirmIgnoreMinIntervalRestrict(false);
                nodeDisposeCheckResult.setConfirmIgnoreMinIntervalRestrictMessage("");

                nodeDisposeCheckResult.setNeedConfirmCoverNodeRecord(false);

                doCheckPower.run();

                return nodeDisposeCheckResult;
            } else {
                throw RestRequestException.normalFail("?????????????????????");
            }
        } else {
            if (FlowRunStateEnum.ENDED.getValue().equals(flowRun.getState())) {
                checkFlowRunEndCause(flowRun.getUnitCode(), flowRunId);
            }
            FlowUtil.CheckNodeDisposeResult checkNodeDisposeResult = FlowUtil.nodeCouldDisposeIgnoreNodeRoleConfigs(nodeId, flowInfo, curFlowRunNodeRecords, staffId);
            if (checkNodeDisposeResult.getResult()) {
                doCheckPower.run();

                nodeDisposeCheckResult = new NodeDisposeCheckResult();

                // ??????????????????
                nodeDisposeCheckResult.setDisposeAfterSkip(checkNodeDisposeResult.getDisposeAfterSkip() != null ? checkNodeDisposeResult.getDisposeAfterSkip() : false);
                // ????????????????????????
                // ??????????????????????????????????????????????????????????????????????????????????????????????????????
                nodeDisposeCheckResult.setNeedSkippedNodes(checkNodeDisposeResult.getSkippedNodes());
                nodeDisposeCheckResult.setNeedConfirmSkippedNodes(checkNodeDisposeResult.getSkippedNodes().size() > 0 && !confirmSkip);

                // ????????????????????????
                FlowUtil.MeetMinIntervalRestrict meetMinIntervalRestrict = FlowUtil.getMeetMinIntervalRestrict(nodeId, flowInfo, curFlowRunNodeRecords);
                if (meetMinIntervalRestrict.getResult() && !confirmIgnoreMinIntervalRestrict) {
                    nodeDisposeCheckResult.setNeedConfirmIgnoreMinIntervalRestrict(true);
                    nodeDisposeCheckResult.setConfirmIgnoreMinIntervalRestrictMessage(meetMinIntervalRestrict.getMinIntervalRestrictMessage());
                } else {
                    nodeDisposeCheckResult.setNeedConfirmIgnoreMinIntervalRestrict(false);
                }

                // ????????????????????????
                NodeRecordInfo nodeRecordInfoCurPersonDisposed = CommonUtils.find(nodeWithRecord.getNodeRecords(), v -> FlowUtil.nodeRecordPersonDisposeFilter(v, staffId, roleId));
                nodeDisposeCheckResult.setNeedConfirmCoverNodeRecord(nodeRecordInfoCurPersonDisposed != null && !confirmCoverNodeRecord);

                return nodeDisposeCheckResult;
            } else {
                if (checkNodeDisposeResult.getNodePasted()) {
                    throw RestRequestException.normalFail("??????????????????????????????????????????????????????????????????????????????????????????");
                }
                Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(nodeId);
                if (pairNodeIndex == null || pairNodeIndex == 0) {
                    String errorResult = FlowUtil.getNodeNotFinishedCause(FlowUtil.findCurrentNodePreviousNotFinishedNodeList(nodeId, flowInfo, curFlowRunNodeRecords, staffId), curFlowRunNodeRecords, postAndRoleList);
                    throw RestRequestException.normalFail(errorResult);
                } else {
                    PairNodeInfo pairNodeInfo = flowInfoAnalysis.getPairNodeInfoByNodeId(nodeId);
                    String prePairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, pairNodeInfo, 0);
                    boolean pairNodeMatchNodeRecode = curFlowRunNodeRecords.stream().anyMatch(v -> prePairNodeId.equals(v.getNodeId()) && v.getWorkerId().equals(staffId));
                    if (!pairNodeMatchNodeRecode) {
                        doCheckPower.run();
                    }
                    String errorResult = FlowUtil.getNodeNotFinishedCause(FlowUtil.findCurrentNodePreviousNotFinishedNodeList(nodeId, flowInfo, curFlowRunNodeRecords, staffId), curFlowRunNodeRecords, postAndRoleList);
                    throw RestRequestException.normalFail(errorResult);
                }
            }
        }
    }

    @Autowired
    NodeRecordMapper nodeRecordMapper;

    @Override
    public List<WorkTeam> getActuallyWorkTeams(String unitCode, String dayPlanId, String[] trainsetIds, String staffId) {
        User user = UserUtil.getUserInfo();
        if (!Objects.equals(user.getStaffId(), staffId)) {
            throw new Assert.IllegalParamException("?????????????????????staffId?????????????????????");
        }
        List<WorkTeam> postWorkTeams = trainsetIds != null && trainsetIds.length > 0 ? flowRunMapper.getTaskPostWorkTeamList(unitCode, dayPlanId, trainsetIds, staffId) : null;
        if (postWorkTeams != null && postWorkTeams.size() > 0) {
            // ??????????????????????????????????????????
            postWorkTeams = CommonUtils.getDistinctList(postWorkTeams, WorkTeam::getTeamCode);
            return postWorkTeams;
        } else {
            // ?????????????????????????????????
            WorkTeam dept = new WorkTeam();
            SysOrgan departmentOrgan = user.getDepartmentOrgan();
            if (departmentOrgan != null) {
                dept.setTeamCode(departmentOrgan.getOrganCode());
                dept.setTeamName(departmentOrgan.getOrganName());
                dept.setTeamShortName(departmentOrgan.getShortName());
                dept.setParentDeptCode(departmentOrgan.getParentTypeCode());
            }
            return Collections.singletonList(dept);
            // // ?????????????????????????????????????????????????????????
            // List<WorkTeam> allWorkTeams = new ArrayList<>();
            // if (user.getWorkTeam().getTeamCode() != null) {
            //     allWorkTeams.add(user.getWorkTeam());
            // }
            // allWorkTeams.addAll(user.getPartTimeWorkTeams());
            // String curUnitCode = ContextUtils.getUnitCode();
            // List<WorkTeam> curUnitWorkTeams = allWorkTeams.stream().filter(v -> curUnitCode.equals(v.getParentDeptCode())).collect(Collectors.toList());
            // if (curUnitWorkTeams.size() > 0) {
            //     return curUnitWorkTeams;
            // } else {
            //     return Collections.singletonList(user.getWorkTeam());
            // }
        }
    }

    private final LockUtil lockUtil = LockUtil.newInstance();

    @Override
    public NodeDisposeResult setNodeDispose(NodeDispose nodeDispose, List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfos, boolean checkPower) {
        Set<Object> lockKeys = new HashSet<>();
        try {
            List<PostAndRole> postAndRoles = postService.getPostAndRoleList();
            if (nodeDispose.getUploadedFileInfos() != null) {
                uploadedFileInfos.addAll(nodeDispose.getUploadedFileInfos());
            }

            String flowId = nodeDispose.getFlowId();
            Assert.notEmptyInnerFatal(flowId, "flowId????????????");
            String nodeId = nodeDispose.getNodeId();
            Assert.notEmptyInnerFatal(nodeId, "nodeId????????????");

            boolean isFlowRunIdEmpty = org.springframework.util.StringUtils.isEmpty(nodeDispose.getFlowRunId());

            if (isFlowRunIdEmpty && nodeDispose.getFlowRun() == null) {
                throw new RuntimeException("????????????????????????????????????????????????");
            }

            FlowRun flowRun = isFlowRunIdEmpty ? nodeDispose.getFlowRun() : selectById(nodeDispose.getFlowRunId());

            boolean insertedFlowRun;

            // ??????????????????flowRun??????key
            FlowRun flowRunLockKeyForInsertFlowRun;
            if (org.springframework.util.StringUtils.isEmpty(flowRun.getId())) {

                flowRunLockKeyForInsertFlowRun = FlowRun.builder().flowId(
                    flowRun.getFlowId()
                ).dayPlanId(
                    flowRun.getDayPlanId()
                ).trainsetId(
                    flowRun.getTrainsetId()
                ).unitCode(
                    flowRun.getUnitCode()
                ).state(FlowRunStateEnum.RUNNING.getValue()).build();

                // ?????????????????????????????????????????????????????????????????????
                lockKeys.add(flowRunLockKeyForInsertFlowRun);
                lockUtil.getDoLock(flowRunLockKeyForInsertFlowRun).lock();

                List<FlowRun> flowRuns = MybatisPlusUtils.selectList(
                    this,
                    MybatisPlusUtils.getEqColumnParamsFromEntity(flowRunLockKeyForInsertFlowRun, FlowRun.class)
                );
                if (flowRuns.size() > 0) {
                    if (flowRuns.size() > 1) {
                        logger.warn("????????????(???????????????????????????id???????????????????????????):" + JSON.toJSONString(flowRuns, true));
                    }
                    flowRun.setId(flowRuns.get(0).getId());
                    // ???????????????????????????????????????????????????????????????????????????????????????????????????
                    lockUtil.unlock(flowRunLockKeyForInsertFlowRun);
                    insertedFlowRun = true;
                } else {
                    flowRun.setId(UUID.randomUUID().toString());
                    insertedFlowRun = false;
                }
            } else {
                flowRunLockKeyForInsertFlowRun = null;
                insertedFlowRun = true;
            }

            // ??????????????????
            FlowInfo flowInfo = flowService.getFlowInfoByFlowId(flowId);

            // ??????????????????id
            String disposeRoleId = nodeDispose.getRoleInfo() != null ? nodeDispose.getRoleInfo().getRoleId() : null;

            Supplier<NodeDisposeCheckResult> innerCheckCouldDispose = () -> {
                // ????????????????????????????????????
                List<NodeRecordInfo> curFlowRunNodeRecords;
                QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
                queryNodeRecord.setFlowRunIds(new String[]{flowRun.getId()});
                curFlowRunNodeRecords = nodeRecordService.getNodeRecordInfoList(queryNodeRecord);

                return checkCouldDispose(
                    nodeId,
                    flowInfo,
                    flowRun,
                    curFlowRunNodeRecords,
                    nodeDispose.getWorkerId(),
                    disposeRoleId,
                    checkPower,
                    nodeDispose.getSkipFlag(),
                    nodeDispose.getConfirmIgnoreMinIntervalRestrict(),
                    nodeDispose.getConfirmCoverNodeRecord(),
                    postAndRoles
                );
            };
            // ????????????????????????
            NodeDisposeCheckResult nodeDisposeCheckResult1 = innerCheckCouldDispose.get();
            if (!nodeDisposeCheckResult1.checkDisposePasted()) {
                NodeDisposeResult nodeDisposeResult = new NodeDisposeResult();
                BeanUtils.copyProperties(nodeDisposeCheckResult1, nodeDisposeResult);
                nodeDisposeResult.setFlowRunFinished(false);
                return nodeDisposeResult;
            }
            // ?????????????????????(???????????????????????????)????????????????????????????????????
            String nodeDisposeLockKey = "setNodeDispose_" + flowRun.getId();
            lockUtil.getDoLock(nodeDisposeLockKey).lock();
            lockKeys.add(nodeDisposeLockKey);

            NodeDisposeCheckResult nodeDisposeCheckResult2 = innerCheckCouldDispose.get();
            if (!nodeDisposeCheckResult2.checkDisposePasted()) {
                NodeDisposeResult nodeDisposeResult = new NodeDisposeResult();
                BeanUtils.copyProperties(nodeDisposeCheckResult2, nodeDisposeResult);
                nodeDisposeResult.setFlowRunFinished(false);
                return nodeDisposeResult;
            }

            boolean firstDispose;
            if (insertedFlowRun) {
                // ?????????????????????, ???????????????????????????????????????
                firstDispose = !MybatisPlusUtils.selectExist(
                    nodeRecordMapper,
                    eqParam(NodeRecord::getFlowRunId, flowRun.getId())
                );
            } else {
                // ????????????????????????????????????????????????
                firstDispose = true;
            }

            if (!insertedFlowRun) {
                // ?????????????????????
                innerFlowRunService.insertFlowRun(flowRun, nodeDispose);
                Objects.requireNonNull(flowRunLockKeyForInsertFlowRun, "???????????????");
                lockUtil.unlock(flowRunLockKeyForInsertFlowRun);
            }

            ValueWrapper<Boolean> flowRunFinishedWrapper = new ValueWrapper<>();

            innerFlowRunService.doInOneTransactional(
                () -> {
                    // ????????????
                    innerFlowRunService.doSetNodeDispose(
                        nodeId,
                        flowInfo,
                        flowRun.getId(),
                        nodeDispose,
                        nodeDisposeCheckResult2.getDisposeAfterSkip(),
                        firstDispose,
                        uploadedFileInfos,
                        nodeDisposeCheckResult2.getNeedSkippedNodes() == null ? new String[0] : nodeDisposeCheckResult2.getNeedSkippedNodes().stream().map(BaseNode::getId).toArray(String[]::new)
                    );
                },
                () -> {
                    // ??????????????????
                    boolean flowRunFinished = innerFlowRunService.updateState(nodeId, flowInfo, flowRun.getId(), nodeDispose, NodeRecordOperationTypeEnum.DISPOSE);
                    flowRunFinishedWrapper.setValue(flowRunFinished);
                }
            );
            lockUtil.unlock(nodeDisposeLockKey);

            NodeDisposeResult nodeDisposeResult = new NodeDisposeResult();
            BeanUtils.copyProperties(nodeDisposeCheckResult1, nodeDisposeResult);
            nodeDisposeResult.setFlowRunFinished(flowRunFinishedWrapper.getValue());
            nodeDisposeResult.setFlowRunId(flowRun.getId());
            return nodeDisposeResult;
        } finally {
            // ????????????????????????????????????
            for (Object lockKey : lockKeys) {
                lockUtil.unlockAll(lockKey);
            }
        }
    }

    @Service
    public static class InnerFlowRunService {

        @Autowired
        @Lazy
        IFlowRunService flowRunService;

        @Autowired
        FlowRunMapper flowRunMapper;

        @Autowired
        IFlowRunRecordService flowRunRecordService;

        @Autowired
        INodeRecordService nodeRecordService;

        @Autowired
        NodeRecordMapper nodeRecordMapper;

        /**
         * ???????????????????????????
         *
         * @param flowRun
         * @param nodeDispose
         */
        @Transactional
        public void insertFlowRun(FlowRun flowRun, NodeDispose nodeDispose) {
            // ????????????????????????
            flowRun.setStartTime(new Date());
            flowRun.setStartType(FlowRunStartTypeEnum.AUTO.getValue());
            flowRun.setStartWorkerId(nodeDispose.getWorkerId());
            flowRun.setStartWorkerName(nodeDispose.getWorkerName());
            flowRun.setState(FlowRunStateEnum.RUNNING.getValue());
            flowRunService.insert(flowRun);
        }

        @Transactional
        public void doInOneTransactional(Runnable... runnableList) {
            for (Runnable runnable : runnableList) {
                runnable.run();
            }
        }

        @Transactional
        public void doSetNodeDispose(
            String nodeId,
            FlowInfo flowInfo,
            String flowRunId,
            WorkerInfo workerInfo,
            boolean disposeAfterSkip,
            boolean firstDispose,
            List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfos,
            String[] needSkippedNodeIds
        ) {
            if (workerInfo.getRoleInfo() == null || workerInfo.getRoleInfo().getRoleId() == null) {
                throw RestRequestException.normalFail("???????????????????????????????????????????????????");
            }

            // ??????????????????
            FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);

            Date now = new Date();

            // ??????????????????????????????????????????????????????????????????????????????????????????????????????
            if (firstDispose) {
                // ????????????????????????????????????????????????????????????????????????????????????, ?????????????????????????????????
                List<FlowInfoAnalysis.FlowSectionInfo> sections = flowInfoAnalysis.getSectionInfoListByStartNodeId(null);
                sections.add(flowInfoAnalysis.getTrunkSectionInfo());
                // ???????????????????????????????????????
                for (FlowInfoAnalysis.FlowSectionInfo section : sections) {
                    if (section.getSortedNodeIds().size() > 0) {
                        String curStartNodeId = section.getSortedNodeIds().get(0);
                        NodeRecord startNodeRecord = initDisposeNodeRecordWithExtraInfo(curStartNodeId, NodeRecordTypeEnum.NODE_START, flowRunId, now, workerInfo, null, null);
                        nodeRecordService.insert(startNodeRecord);
                        NodeRecordInfo startNodeRecordInfo = new NodeRecordInfo();
                        BeanUtils.copyProperties(startNodeRecord, startNodeRecordInfo);
                    }
                }
            }

            // ????????????????????????
            if (needSkippedNodeIds != null && needSkippedNodeIds.length > 0) {
                for (String needSkippedNodeId : needSkippedNodeIds) {
                    List<NodeRecordExtraInfo> skipExtra = NodeRecordExtraInfoTypeEnum.Skip.getInstance().transformToExtraInfoList(true);
                    NodeRecordWithExtraInfo nodeRecordWithExtraInfo = initDisposeNodeRecordWithExtraInfo(needSkippedNodeId, NodeRecordTypeEnum.PERSON, flowRunId, now, workerInfo, null, skipExtra);
                    nodeRecordService.insertWithExtraInfo(nodeRecordWithExtraInfo);
                }
            }

            // ??????????????????
            List<NodeRecordExtraInfo> extra = new ArrayList<>();
            // ??????????????????????????????????????????
            if (disposeAfterSkip) {
                extra.addAll(NodeRecordExtraInfoTypeEnum.DisposeAfterSkip.getInstance().transformToExtraInfoList(true));
            }
            NodeRecordWithExtraInfo nodeRecord = initDisposeNodeRecordWithExtraInfo(nodeId, NodeRecordTypeEnum.PERSON, flowRunId, now, workerInfo, uploadedFileInfos, extra);

            // ??????????????????????????????????????????????????????????????????????????????????????????????????????
            List<ColumnParam<NodeRecord>> columnParams = Arrays.asList(
                eqParam(NodeRecord::getWorkerId, nodeRecord.getWorkerId()),
                eqParam(NodeRecord::getRoleId, nodeRecord.getRoleId()),
                eqParam(NodeRecord::getNodeId, nodeRecord.getNodeId()),
                eqParam(NodeRecord::getFlowRunId, nodeRecord.getFlowRunId()),
                eqParam(NodeRecord::getType, NodeRecordTypeEnum.PERSON.getValue())
            );
            if (MybatisPlusUtils.selectExist(
                nodeRecordMapper,
                columnParams
            )) {
                MybatisPlusUtils.delete(nodeRecordMapper, columnParams);
            }
            nodeRecordService.insertWithExtraInfo(nodeRecord);

        }

        private NodeRecordWithExtraInfo initDisposeNodeRecordWithExtraInfo(String nodeId, NodeRecordTypeEnum nodeRecordTypeEnum, String flowRunId, Date recordTime, WorkerInfo workerInfo, List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfos, List<NodeRecordExtraInfo> extra) {
            return initNodeRecordWithExtraInfo(nodeId, nodeRecordTypeEnum, NodeRecordOperationTypeEnum.DISPOSE, flowRunId, recordTime, workerInfo, uploadedFileInfos, extra);
        }

        private NodeRecordWithExtraInfo initNodeRecordWithExtraInfo(String nodeId, NodeRecordTypeEnum nodeRecordTypeEnum, NodeRecordOperationTypeEnum nodeRecordOperationTypeEnum, String flowRunId, Date recordTime, WorkerInfo workerInfo, List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfos, List<NodeRecordExtraInfo> extra) {
            NodeRecordWithExtraInfo nodeRecord = new NodeRecordWithExtraInfo();
            nodeRecord.setId(UUID.randomUUID().toString());
            nodeRecord.setFlowRunId(flowRunId);
            nodeRecord.setNodeId(nodeId);
            nodeRecord.setType(nodeRecordTypeEnum.getValue());
            nodeRecord.setOperationType(nodeRecordOperationTypeEnum.getValue());
            nodeRecord.setRecordTime(recordTime);

            nodeRecord.setWorkerId(workerInfo.getWorkerId());
            nodeRecord.setWorkerName(workerInfo.getWorkerName());
            nodeRecord.setTeamCode(workerInfo.getTeamCode());
            nodeRecord.setTeamName(workerInfo.getTeamName());
            RoleInfo roleInfo = workerInfo.getRoleInfo();
            if (roleInfo != null) {
                nodeRecord.setRoleId(roleInfo.getRoleId());
                nodeRecord.setRoleName(roleInfo.getRoleName());
                nodeRecord.setRoleType(roleInfo.getType());
            }


            List<NodeRecordExtraInfo> nodeRecordExtraInfoList = new ArrayList<>();
            if (uploadedFileInfos != null) {
                nodeRecordExtraInfoList.addAll(NodeRecordExtraInfoTypeEnum.PictureUrl.getInstance().getExtraInfoListFromUploadInfo(
                    uploadedFileInfos
                ));
                nodeRecordExtraInfoList.addAll(NodeRecordExtraInfoTypeEnum.VideoUrl.getInstance().getExtraInfoListFromUploadInfo(
                    uploadedFileInfos
                ));
            }

            if (extra != null) {
                nodeRecordExtraInfoList.addAll(extra);
            }

            nodeRecordExtraInfoList.forEach(v -> {
                v.setNodeRecordId(nodeRecord.getId());
            });
            nodeRecord.setNodeRecordExtraInfoList(nodeRecordExtraInfoList);
            nodeRecord.setExtraInfoExist(FlowUtil.booleanToString(nodeRecordExtraInfoList.size() > 0));
            return nodeRecord;
        }

        /**
         * ???????????????????????????????????????????????????
         *
         * @param curNodeId ??????????????????id
         * @param flowInfo  ????????????
         * @param flowRunId ????????????id
         * @return ??????????????????
         */
        @Transactional
        public boolean updateState(String curNodeId, FlowInfo flowInfo, String flowRunId, WorkerInfo workerInfo, NodeRecordOperationTypeEnum nodeRecordOperationTypeEnum) {
            UpdateNodeRecordsUtil updateNodeRecordsUtil = UpdateNodeRecordsUtil.newInstanceAndInitialData(nodeRecordService, flowRunId);
            boolean flowRunFinished = updateNodeState(curNodeId, flowInfo, flowRunId, workerInfo, updateNodeRecordsUtil, nodeRecordOperationTypeEnum);
            updateNodeRecordsUtil.setChangeToDatabase();
            if (flowRunFinished) {
                // ??????????????????
                endFlowRun(flowRunId, workerInfo);
            }
            return flowRunFinished;
        }

        /**
         * ??????????????????
         */
        private void endFlowRun(String flowRunId, WorkerInfo workerInfo) {
            FlowRun flowRun = new FlowRun();
            flowRun.setId(flowRunId);
            flowRun.setState(FlowRunStateEnum.ENDED.getValue());
            flowRunMapper.updateById(flowRun);

            FlowRunRecord flowRunRecord = new FlowRunRecord();
            flowRunRecord.setFlowRunId(flowRunId);
            flowRunRecord.setId(UUID.randomUUID().toString());
            flowRunRecord.setRecordTime(new Date());
            flowRunRecord.setType(FlowRunRecordTypeEnum.NORMAL_END.getValue());
            flowRunRecord.setWorkerId(workerInfo.getWorkerId());
            flowRunRecord.setWorkerName(workerInfo.getWorkerName());

            flowRunRecordService.insert(flowRunRecord);
        }

        /**
         * ?????????????????????????????????????????????
         */
        @Transactional
        public void updateFlowRunActiveNodesState(FlowInfo flowInfo, FlowRun flowRun, WorkerInfo workerInfo, NodeRecordOperationTypeEnum nodeRecordOperationTypeEnum) {
            if (FlowRunStateEnum.ENDED.is(flowRun.getState())) {
                return;
            }
            String flowRunId = flowRun.getId();
            UpdateNodeRecordsUtil updateNodeRecordsUtil = UpdateNodeRecordsUtil.newInstanceAndInitialData(nodeRecordService, flowRunId);
            List<NodeInfo> sortedNodes = FlowUtil.getSortedNodes(flowInfo);
            boolean flowRunEnded = false;
            for (NodeInfo nodeInfo : sortedNodes) {
                String curNodeId = nodeInfo.getId();
                if (!FlowUtil.nodePasted(curNodeId, flowInfo, updateNodeRecordsUtil.getInitNodeRecords())) {
                    boolean flowRunFinished = updateNodeState(curNodeId, flowInfo, flowRunId, workerInfo, updateNodeRecordsUtil, nodeRecordOperationTypeEnum);
                    if (flowRunFinished && !flowRunEnded) {
                        // ??????????????????
                        endFlowRun(flowRunId, workerInfo);
                        flowRunEnded = true;
                    }
                }
            }
            updateNodeRecordsUtil.setChangeToDatabase();
        }


        /**
         * @return ????????????????????????
         */
        public boolean updateNodeState(String curNodeId, FlowInfo flowInfo, String flowRunId, WorkerInfo workerInfo, UpdateNodeRecordsUtil updateNodeRecordsUtil, NodeRecordOperationTypeEnum nodeRecordOperationTypeEnum) {
            // ????????????????????????????????????(?????????????????????????????????????????????????????????)???????????????????????????

            // ?????????????????????id??????
            List<NodeRecordInfo> initFlowRunNodeRecords = updateNodeRecordsUtil.getCurrentNodeRecords();
            Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = initFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));

            // ????????????????????????
            FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);
            NodeInfo nodeInfo = flowInfoAnalysis.getNodeMap().get(curNodeId);
            if (nodeInfo != null) {
                boolean checkNodeFinished = FlowUtil.checkNodeFinished(curNodeId, flowInfo, initFlowRunNodeRecords);
                boolean nodeHasFinishedRecord = FlowUtil.nodeFinished(groupedNodeRecords.get(curNodeId));
                boolean deletedAfterPairNodeFinishRecord = false;
                // ??????????????????????????????
                if (checkNodeFinished != nodeHasFinishedRecord) {
                    Integer pairNodeIndex = flowInfoAnalysis.getPairNodeIndexForSort(curNodeId);
                    if (!nodeHasFinishedRecord) {
                        Consumer<String> insertNodeFinishRecord = (nodeId) -> {
                            NodeRecordWithExtraInfo nodeFinishRecord = initNodeRecordWithExtraInfo(nodeId, NodeRecordTypeEnum.NODE_FINISH, nodeRecordOperationTypeEnum, flowRunId, new Date(), workerInfo, null, null);
                            NodeRecordInfo insert = FlowUtil.convertTo(nodeFinishRecord);
                            updateNodeRecordsUtil.insert(insert);
                            // nodeRecordService.insert(nodeFinishRecord);
                        };
                        // ??????????????????
                        insertNodeFinishRecord.accept(curNodeId);
                        if (pairNodeIndex != null && pairNodeIndex == 1) {// ???????????????????????????????????????????????????????????????????????????????????????????????????
                            String prePairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(curNodeId), 0);
                            insertNodeFinishRecord.accept(prePairNodeId);
                        }
                    } else {
                        Consumer<String> deleteNodeFinishRecord = (nodeId) -> {
                            updateNodeRecordsUtil.delete(
                                allMatch(
                                    eqParam(NodeRecord::getNodeId, nodeId),
                                    eqParam(NodeRecord::getType, NodeRecordTypeEnum.NODE_FINISH.getValue())
                                )
                            );
                            // MybatisPlusUtils.delete(
                            //     nodeRecordService,
                            //     eqParam(NodeRecord::getFlowRunId, flowRunId),
                            //     eqParam(NodeRecord::getNodeId, nodeId),
                            //     eqParam(NodeRecord::getType, typeNeedDelete.getValue())
                            // );
                        };
                        // ??????????????????
                        deleteNodeFinishRecord.accept(curNodeId);
                        if (pairNodeIndex != null && pairNodeIndex == 0) {// ????????????????????????????????????????????????????????????????????????????????????
                            String afterPairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(curNodeId), 1);
                            deleteNodeFinishRecord.accept(afterPairNodeId);
                            deletedAfterPairNodeFinishRecord = true;
                        }
                    }
                }

                List<NodeInfo> nextNodeInfoList = flowInfoAnalysis.getNextNodes(curNodeId);
                // ????????????????????????????????????????????????????????????
                if (nextNodeInfoList.size() > 0) {
                    updateNodeStartState(nextNodeInfoList, flowInfo, flowRunId, updateNodeRecordsUtil, workerInfo, nodeRecordOperationTypeEnum);
                    // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (deletedAfterPairNodeFinishRecord) {
                        String afterPairNodeId = FlowUtil.getPairNodeIdByIndex(flowInfo, flowInfoAnalysis.getPairNodeInfoByNodeId(curNodeId), 1);
                        updateNodeState(afterPairNodeId, flowInfo, flowRunId, workerInfo, updateNodeRecordsUtil, nodeRecordOperationTypeEnum);
                    }
                } else if (checkNodeFinished) {// ????????????????????????
                    // ?????????????????????????????????????????????????????????????????????
                    List<FlowInfoAnalysis.FlowSectionInfo> endSectionInfoList = flowInfoAnalysis.getSectionInfoListByEndNodeId(null);
                    endSectionInfoList.add(flowInfoAnalysis.getTrunkSectionInfo());
                    // ?????????????????????????????????????????????????????????
                    return endSectionInfoList.stream().noneMatch(flowSectionInfo -> {
                        if (flowSectionInfo.getSortedNodeIds().size() > 0) {
                            String endNodeId = flowSectionInfo.getSortedNodeIds().get(flowSectionInfo.getSortedNodeIds().size() - 1);
                            return !(
                                // ??????????????????id ?????? ????????????????????????????????? ????????????????????????????????????
                                curNodeId.equals(endNodeId) || FlowUtil.nodeFinished(groupedNodeRecords.get(endNodeId))
                            );
                        } else {
                            return false;
                        }
                    });
                }
            } else {
                throw new RuntimeException("?????????????????????????????????");
            }
            return false;
        }

        private void updateNodeStartState(List<NodeInfo> nodeInfoList, FlowInfo flowInfo, String flowRunId, UpdateNodeRecordsUtil updateNodeRecordsUtil, WorkerInfo workerInfo, NodeRecordOperationTypeEnum nodeRecordOperationTypeEnum) {
            // ?????????????????????id??????
            List<NodeRecordInfo> curFlowRunNodeRecords = updateNodeRecordsUtil.getCurrentNodeRecords();
            Map<String, ? extends List<? extends NodeRecordInfo>> groupedNodeRecords = curFlowRunNodeRecords.stream().collect(Collectors.groupingBy(NodeRecord::getNodeId));

            // ????????????????????????
            FlowInfoAnalysis flowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(flowInfo);

            for (NodeInfo nodeInfo : nodeInfoList) {
                String nodeId = nodeInfo.getId();
                List<? extends NodeRecordInfo> nextNodeRecords = groupedNodeRecords.get(nodeId);
                boolean nodeStarted = FlowUtil.nodeStarted(nodeId, flowInfo, curFlowRunNodeRecords, null);
                boolean nodeHasStartRecord = nextNodeRecords != null && nextNodeRecords.stream().anyMatch(v -> v.getType().equals(NodeRecordTypeEnum.NODE_START.getValue()));
                if (nodeStarted != nodeHasStartRecord) {
                    if (!nodeHasStartRecord) {
                        // ??????????????????????????????????????????????????????????????????
                        NodeRecordWithExtraInfo nextNodeStartRecord = initNodeRecordWithExtraInfo(nodeId, NodeRecordTypeEnum.NODE_START, nodeRecordOperationTypeEnum, flowRunId, new Date(), workerInfo, null, null);
                        NodeRecordInfo insert = FlowUtil.convertTo(nextNodeStartRecord);
                        updateNodeRecordsUtil.insert(insert);
                        // nodeRecordService.insert(nextNodeStartRecord);
                    } else {
                        // ???????????????????????????????????????????????????
                        updateNodeRecordsUtil.delete(
                            allMatch(
                                eqParam(NodeRecord::getNodeId, nodeId),
                                eqParam(NodeRecord::getType, NodeRecordTypeEnum.NODE_START.getValue())
                            )
                        );
                        // MybatisPlusUtils.delete(
                        //     nodeRecordService,
                        //     eqParam(NodeRecord::getFlowRunId, flowRunId),
                        //     eqParam(NodeRecord::getNodeId, nodeId),
                        //     eqParam(NodeRecord::getType, typeNeedDelete.getValue())
                        // );
                    }
                    // ????????????????????????????????????????????????
                    if (nodeInfo.getSkip()) {
                        updateNodeStartState(flowInfoAnalysis.getNextNodes(nodeId), flowInfo, flowRunId, updateNodeRecordsUtil, workerInfo, nodeRecordOperationTypeEnum);
                    }
                }
            }
        }

    }


    @Override
    public List<FlowRunWithExtraInfo> getFlowRunWithExtraInfos(List<String> dayPlanIds, List<String> flowTypeCodes, String flowRunId, String trainsetId, String flowRunState, Date startDate, Date endDate, String flowId, String[] flowRunIds, String unitCode, String dayPlanCode) {
        return flowRunMapper.getFlowRunWithExtraInfos(dayPlanIds, flowTypeCodes, flowRunId, trainsetId, flowRunState, startDate, endDate, flowId, flowRunIds, unitCode, dayPlanCode);
    }

    @Override
    public List<FlowRun> getFlowRunInfoListByDayPlanId(String dayPlanId, String trainsetId) {
        List<ColumnParam<FlowRun>> columnParamList = new ArrayList<>();
        columnParamList.add(eqParam(FlowRun::getDayPlanId, dayPlanId));
        // //????????????????????????????????????????????????
        // if (queryFlowRunState) {
        //     columnParamList.add(eqParam(FlowRun::getState, "0"));
        // }
        if (StringUtils.isNotBlank(trainsetId)) {
            columnParamList.add(eqParam(FlowRun::getTrainsetId, trainsetId));
        }
        return MybatisPlusUtils.selectList(
            flowRunMapper,
            columnParamList
        );
    }


    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param flowRun           ???????????????????????????????????????
     * @param flowInfo          ????????????
     * @param nodeRecords       ??????????????????
     * @param staffId           ?????????id???????????????????????????????????????????????????????????????????????????????????????null???
     * @param runtimeRoleList   ??????????????????
     * @param flowRunRecordInfo ????????????????????????
     */
    public FlowRunInfoForSimpleShow getFlowRunInfoForSimpleShow(FlowRunWithExtraInfo flowRun, FlowInfo flowInfo, List<NodeRecordInfo> nodeRecords, String staffId, List<RuntimeRole> runtimeRoleList, FlowRunRecordInfo flowRunRecordInfo) {
        FlowRunInfoForSimpleShow flowRunInfoForSimpleShow = new FlowRunInfoForSimpleShow();

        //??????????????????
        if (flowRunRecordInfo != null) {
            flowRunInfoForSimpleShow.setFlowRunRecordInfo(flowRunRecordInfo);
        }


        //???????????????????????????????????????
        BeanUtils.copyProperties(flowRun, flowRunInfoForSimpleShow);

        //??????????????????
        BaseFlow baseFlow = new BaseFlow();
        BeanUtils.copyProperties(flowInfo, baseFlow);
        flowRunInfoForSimpleShow.setFlowConfig(baseFlow);

        List<NodeInfo> sortedNodes = FlowUtil.getSortedNodes(flowInfo);


        Map<String, SubflowInfo> subFlowMap = CommonUtils.collectionToMap(flowInfo.getSubflowInfoList(), BaseFlow::getId);
        if (sortedNodes.contains(null)) {
            logger.warn("???????????????" + JSON.toJSONString(flowInfo, true));
            sortedNodes.remove(null);
        }

        List<NodeInfoForSimpleShow> nodesWhichHaveChildren = sortedNodes.stream().map(nodeInfo -> {
            NodeInfoForSimpleShow nodeInfoForSimpleShow = new NodeInfoForSimpleShow();
            BeanUtils.copyProperties(nodeInfo, nodeInfoForSimpleShow);

            String subFLowId = nodeInfoForSimpleShow.getChildFlowId();
            if (nodeInfoForSimpleShow.getChildFlowId() != null) {
                SubflowInfo subflowInfo = subFlowMap.get(subFLowId);
                FlowInfo transSubflowInfo = new FlowInfo();
                BeanUtils.copyProperties(subflowInfo, transSubflowInfo);
                transSubflowInfo.setNodeVectors(subflowInfo.getNodeVectors().stream().map(nodeVector -> {
                    NodeVectorWithExtraInfo nodeVectorExtraInfo = new NodeVectorWithExtraInfo();
                    BeanUtils.copyProperties(nodeVector, nodeVectorExtraInfo);
                    return nodeVectorExtraInfo;
                }).collect(Collectors.toList()));
                FlowInfoAnalysis subflowInfoAnalysis = FlowUtil.getFlowInfoAnalysis(transSubflowInfo);
                nodeInfoForSimpleShow.setChildren(subflowInfoAnalysis.getTrunkSectionInfo().getSortedNodeIds().stream().map(v -> {
                    NodeInfoForSimpleShow subNodeInfoForSimpleShow = new NodeInfoForSimpleShow();
                    BeanUtils.copyProperties(subflowInfoAnalysis.getNodeMap().get(v), subNodeInfoForSimpleShow);
                    return subNodeInfoForSimpleShow;
                }).collect(Collectors.toList()));
            }
            return nodeInfoForSimpleShow;
        }).collect(Collectors.toList());

        List<NodeInfoForSimpleShow> nodeInfoForSimpleShowList = new ArrayList<>();
        for (NodeInfoForSimpleShow nodesWhichHaveChild : nodesWhichHaveChildren) {
            setNodeWithRecord(nodesWhichHaveChild, nodeRecords, staffId, runtimeRoleList, flowInfo, flowRun);
            //??????????????????????????????
            if (nodesWhichHaveChild.getChildren() != null) {
                List<NodeInfoForSimpleShow> childNodeInfoForSimpleShowList = new ArrayList<>();
                for (NodeInfoForSimpleShow child : nodesWhichHaveChild.getChildren()) {
                    setNodeWithRecord(child, nodeRecords, staffId, runtimeRoleList, flowInfo, flowRun);
                    childNodeInfoForSimpleShowList.add(child);
                }
                nodesWhichHaveChild.setChildren(childNodeInfoForSimpleShowList);
            }

            nodeInfoForSimpleShowList.add(nodesWhichHaveChild);
        }

        flowRunInfoForSimpleShow.setNodeList(nodeInfoForSimpleShowList);

        return flowRunInfoForSimpleShow;
    }

    /**
     * ?????????????????????????????????
     *
     * @param nodeWithRecord  ??????
     * @param nodeRecords     ????????????????????????????????????????????????????????????
     * @param staffId         ?????????id(??????????????????????????????????????????)
     * @param runtimeRoleList ???????????????????????????????????????????????????????????????????????????????????????????????????
     * @param flowInfo        ??????????????????
     * @param flowRun         ????????????????????????
     */
    public void setNodeWithRecord(NodeWithRecord nodeWithRecord, List<NodeRecordInfo> nodeRecords, String staffId, List<RuntimeRole> runtimeRoleList, FlowInfo flowInfo, FlowRun flowRun) {
        // ???????????????????????????????????????
        nodeWithRecord.setNodeRecords(nodeRecords.stream().filter(v -> v.getNodeId().equals(nodeWithRecord.getId()) && v.getFlowRunId().equals(flowRun.getId())).collect(Collectors.toList()));
        if (FlowRunStateEnum.ENDED.getValue().equals(flowRun.getState())) {
            nodeWithRecord.setCouldDisposeRoleIds(new ArrayList<>());
        } else {
            // ????????????????????????
            nodeWithRecord.setCouldDisposeRoleIds(FlowUtil.getNodeCouldDisposeRuntimeRoleIds(nodeWithRecord.getId(), flowInfo, nodeRecords, staffId, runtimeRoleList));
        }

        // ??????????????????
        nodeWithRecord.setFinished(FlowUtil.runtimeNodeFinished(
            nodeWithRecord.getId(),
            flowInfo,
            nodeRecords,
            staffId,
            runtimeRoleList
        ));
    }


    /**
     * ?????????????????????????????????????????????
     */
    private Set<String> getFlowTypeCodesByFlowPageCode(String flowPageCode, String unitCode) {
        return flowTypeService.getFlowTypesByFlowPageCode(flowPageCode, unitCode).stream().map(BaseFlowType::getCode).collect(Collectors.toSet());
    }

    @Override
    public List<RuntimeRole> getTaskPostList(String dayPlanId, String unitCode, String staffId, String trainsetId) {
        List<RuntimeRole> runtimeRoles = new ArrayList<>();
        boolean personPostByTask = FlowDatabaseConfigUtil.isPersonPostByTask();
        if (personPostByTask) {
            if (StringUtils.isNotBlank(dayPlanId) && StringUtils.isNotBlank(unitCode) && StringUtils.isNotBlank(staffId)) {
                List<RuntimeRole> taskPostList = flowRunMapper.getTaskPostList(dayPlanId, unitCode, staffId, trainsetId);
                taskPostList.forEach(v -> v.setType(RoleTypeEnum.POST_ROLE.getValue()));
                runtimeRoles.addAll(taskPostList);
            }
        } else {
            if (StringUtils.isNotBlank(unitCode) && StringUtils.isNotBlank(staffId)) {
                List<RuntimeRole> taskPostList = flowRunMapper.getPostByStaffId(unitCode, staffId);
                taskPostList.forEach(v -> v.setType(RoleTypeEnum.POST_ROLE.getValue()));
                runtimeRoles.addAll(taskPostList);
            }
        }

        User userInfo = UserUtil.getUserInfo();
        if (!Objects.equals(userInfo.getStaffId(), staffId)) {
            throw new RuntimeException("????????????????????????staffId?????????staffId??????????????????");
        }
        List<String> roles = userInfo.getRoles();
        runtimeRoles.addAll(roles.stream().map(v -> {
            RuntimeRole runtimeRole = new RuntimeRole();
            runtimeRole.setRoleId(v);
            runtimeRole.setType(RoleTypeEnum.SYS_ROLE.getValue());
            return runtimeRole;
        }).collect(Collectors.toList()));
        return runtimeRoles;
    }

    @Override
    @Transactional
    public String setKeyWorkFlow(MultipartHttpServletRequest multipartHttpServletRequest) throws ParseException {
        //???????????????
        MultiValueMap<String, MultipartFile> multiFileMap = multipartHttpServletRequest.getMultiFileMap();
        String dateDirName = DateTimeUtil.format(new Date(), Constants.DEFAULT_DATE_FORMAT);
        String dirPrefix = RepairWorkFlowConstants.UPLOAD_PATH + "/" + RepairWorkFlowConstants.KEY_WORK_MODULE_NAME + "/" + dateDirName;
        List<UploadedFileInfo> uploadedFileInfos = UpLoadFileUtils.uploadImages(multipartHttpServletRequest, dirPrefix + "/images");

        //??????????????????
        String keyWorkParam = multipartHttpServletRequest.getParameter("KeyWorkParam");
        KeyWorkFlowRunInfo keyWorkConfig = JSONObject.parseObject(keyWorkParam, KeyWorkFlowRunInfo.class);
        keyWorkConfig.setDataSource(KeyWorkDateSourceEnum.PURE_MANUAL.getValue());
        List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList = new ArrayList<>();
        keyWorkFlowRunInfoList.add(keyWorkConfig);
        if (keyWorkConfig.getUploadedFileInfos() != null) {
            uploadedFileInfos.addAll(keyWorkConfig.getUploadedFileInfos());
        }
        return addKeyWorkFlowRunInfo(keyWorkFlowRunInfoList, uploadedFileInfos);

    }


    @Override
    public List<KeyWorkFlowRunInfo> getKeyWorkFlowRunInfoList(List<String> dayPlanIds, String trainType, String trainTemplate, String trainsetId, Date startTime, Date endTime, String flowName, String flowRunId, String unitCode, Boolean checkPower, String flowRunState, String content, boolean queryPastFlow, boolean monitoringShowForceEndFlowRun, boolean keyWorkMonitor) {
        String staffId = UserUtil.getUserInfo().getStaffId();
        List<RuntimeRole> runtimeRoles = new ArrayList<>();
        if (checkPower) {
            //????????????
            runtimeRoles = this.getTaskPostList(null, unitCode, staffId, null);
        }

        QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
        queryFlowModelGeneral.setUnitCode(unitCode);
        queryFlowModelGeneral.setShowDeleted(queryPastFlow);
        queryFlowModelGeneral.setFlowName(flowName);
        // ?????????????????????????????????
        List<FlowInfo> allFlowInfoList = flowService.getFlowInfoList(queryFlowModelGeneral);


        //???????????????????????????????????????(????????????????????????)
        String state = flowRunState == null ? null : flowRunState.equals("2") || flowRunState.equals("3") ? "1" : flowRunState;

        //???????????????????????????????????????????????????
        List<FlowRunWithExtraInfo> flowRunWithExtraInfoList = this.getFlowRunWithExtraInfos(dayPlanIds, Arrays.asList(BasicFlowTypeEnum.PLANLESS_KEY.getValue().split(",")), flowRunId, trainsetId, state, startTime, endTime, null, null, unitCode, null);
        //????????????????????????
        List<TrainsetBaseInfo> trainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();

        Set<String> queryTrainsetIdList = new HashSet<>();
        if (StringUtils.isBlank(trainsetId)) {
            if (StringUtils.isNotBlank(trainTemplate)) {
                queryTrainsetIdList.addAll(trainsetBaseInfos.stream().filter(v -> v.getTraintempid().equals(trainTemplate)).collect(Collectors.toSet()).stream().map(v -> v.getTrainsetid()).collect(Collectors.toSet()));
            } else {
                if (StringUtils.isNotBlank(trainType)) {
                    queryTrainsetIdList.addAll(trainsetBaseInfos.stream().filter(v -> v.getTraintype().equals(trainType)).collect(Collectors.toSet()).stream().map(v -> v.getTrainsetid()).collect(Collectors.toSet()));
                }
            }
        }

        if (queryTrainsetIdList.size() > 0) {
            flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> queryTrainsetIdList.contains(v.getTrainsetId())).collect(Collectors.toList());
        }

        //?????????/??????/?????????????????????
        List<FlowRunRecordInfo> flowRunRecordInfos = flowRunRecordService.getFlowRunRecordsByForceEnd(unitCode, null, null, startTime, endTime);
        //???????????????????????????????????????id??????
        Map<String, FlowRunRecordInfo> flowRunRecordInfoByForceEndToMaps = CommonUtils.collectionToMap(flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.REJECT_END.getValue())).collect(Collectors.toList()), FlowRunRecordInfo::getFlowRunId);
        //???????????????????????????????????????id??????
        Map<String, FlowRunRecordInfo> flowRunRecordInfoByRevokeEndToMaps = CommonUtils.collectionToMap(flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.REVOKE_END.getValue())).collect(Collectors.toList()), FlowRunRecordInfo::getFlowRunId);

        //?????????????????????????????????????????????
        if (keyWorkMonitor) {
            flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowRunRecordInfoByRevokeEndToMaps.get(v.getId()) == null).collect(Collectors.toList());
            if (!monitoringShowForceEndFlowRun) {
                flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowRunRecordInfoByForceEndToMaps.get(v.getId()) == null).collect(Collectors.toList());
            }
        }
        if (!keyWorkMonitor) {
            //????????????
            if (flowRunState != null && flowRunState.equals("2")) {
                flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowRunRecordInfoByForceEndToMaps.get(v.getId()) != null).collect(Collectors.toList());
            }
            //????????????
            if (flowRunState != null && flowRunState.equals("3")) {
                flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowRunRecordInfoByRevokeEndToMaps.get(v.getId()) != null).collect(Collectors.toList());
            }

            if (flowRunState != null && flowRunState.equals("1")) {
                flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowRunRecordInfoByForceEndToMaps.get(v.getId()) == null && flowRunRecordInfoByRevokeEndToMaps.get(v.getId()) == null).collect(Collectors.toList());
            }
        }


        List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList = new ArrayList<>();


        Map<String, TrainsetBaseInfo> trainsetBaseInfoMap = CommonUtils.collectionToMap(trainsetBaseInfos, TrainsetBaseInfo::getTrainsetid);
        for (FlowRunWithExtraInfo flowRunWithExtraInfo : flowRunWithExtraInfoList) {
            List<FlowInfo> flowInfos = allFlowInfoList.stream().filter(v -> v.getId().equals(flowRunWithExtraInfo.getFlowId())).collect(Collectors.toList());
            if (flowInfos.size() > 0) {
                FlowInfo flowInfo = allFlowInfoList.stream().filter(v -> v.getId().equals(flowRunWithExtraInfo.getFlowId())).collect(Collectors.toList()).get(0);
                List<NodeInfo> sortedNodes = FlowUtil.getSortedNodes(flowInfo);
                flowInfo.setNodes(sortedNodes);
                //?????????????????????????????????
                QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
                queryNodeRecord.setFlowRunIds(new String[]{flowRunWithExtraInfo.getId()});
                List<NodeRecordInfo> nodeRecordWithExtraInfos = nodeRecordService.getNodeRecordInfoList(queryNodeRecord);
                KeyWorkFlowRunInfo keyWorkFlowRunInfo = new KeyWorkFlowRunInfo();
                BeanUtils.copyProperties(flowRunWithExtraInfo, keyWorkFlowRunInfo);

                FlowRunRecordInfo forceEndMapByFlowRunId = flowRunRecordInfoByForceEndToMaps.get(flowRunWithExtraInfo.getId());
                if (forceEndMapByFlowRunId != null) {
                    //??????????????????
                    keyWorkFlowRunInfo.setFlowRunRecordInfo(forceEndMapByFlowRunId);
                    keyWorkFlowRunInfo.setState("2");
                }

                FlowRunRecordInfo revokeEndMapByFlowRunId = flowRunRecordInfoByRevokeEndToMaps.get(flowRunWithExtraInfo.getId());
                if (revokeEndMapByFlowRunId != null) {
                    //??????????????????
                    keyWorkFlowRunInfo.setFlowRunRecordInfo(revokeEndMapByFlowRunId);
                    keyWorkFlowRunInfo.setState("3");
                }

                //???????????????
                if (staffId.equals(flowRunWithExtraInfo.getStartWorkerId()) && nodeRecordWithExtraInfos.size() == 0 && flowRunWithExtraInfo.getState().equals("0")) {
                    keyWorkFlowRunInfo.setRevoke(true);
                }


                //????????????
                keyWorkFlowRunInfo = parseFlowRunExtraInfo(keyWorkFlowRunInfo, flowRunWithExtraInfo.getFlowRunExtraInfoList());
                //??????????????????????????????,????????????????????????
                if ((StringUtils.isBlank(content)) || (StringUtils.isNotBlank(content) && keyWorkFlowRunInfo.getContent().contains(content))) {
                    //??????????????????
                    List<NodeWithRecord> nodeWithRecords = new ArrayList<>();
                    for (NodeInfo nodeWithExtraInfo : flowInfo.getNodes()) {
                        NodeInfoForSimpleShow nodeInfoForSimpleShow = new NodeInfoForSimpleShow();
                        BeanUtils.copyProperties(nodeWithExtraInfo, nodeInfoForSimpleShow);
                        //????????????????????????
                        setNodeWithRecord(nodeInfoForSimpleShow, nodeRecordWithExtraInfos, staffId, runtimeRoles, flowInfo, flowRunWithExtraInfo);
                        //?????????person??????????????????(??????????????????)?????????????????????
                        nodeInfoForSimpleShow.setNodeRecords(nodeInfoForSimpleShow.getNodeRecords().stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList()));
                        nodeWithRecords.add(nodeInfoForSimpleShow);
                    }
                    TrainsetBaseInfo trainsetBaseInfo = trainsetBaseInfoMap.get(keyWorkFlowRunInfo.getTrainsetId());
                    if (trainsetBaseInfo != null) {
                        keyWorkFlowRunInfo.setTrainsetName(trainsetBaseInfo.getTrainsetname());
                    }
                    keyWorkFlowRunInfo.setFlowName(flowInfo.getName());
                    keyWorkFlowRunInfo.setNodeWithRecords(nodeWithRecords);
                    keyWorkFlowRunInfoList.add(keyWorkFlowRunInfo);
                }
            }
        }
        //???????????????????????????????????????
        List<KeyWorkFlowRunInfo> keyWorkFlowRunInfos = JSON.parseArray(JSON.toJSONString(keyWorkFlowRunInfoList, SerializerFeature.DisableCircularReferenceDetect), KeyWorkFlowRunInfo.class);
        return keyWorkFlowRunInfos;
    }

    /**
     * ????????????????????????????????????
     */
    public void parseAddFlowRunExtraInfo(KeyWorkFlowRunInfo keyWorkConfig, List<UploadedFileInfo> uploadedFileInfos, String flowRunId) {
        List<FlowRunExtraInfo> flowRunExtraInfos = new ArrayList<>();
        FlowRunExtraInfo flowRunExtraInfo;
        if (keyWorkConfig.getContent() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("CONTENT");
            flowRunExtraInfo.setValue(keyWorkConfig.getContent());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getDataSource() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("DATA_SOURCE");
            flowRunExtraInfo.setValue(keyWorkConfig.getDataSource());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }
        if (keyWorkConfig.getRemark() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("REMARK");
            flowRunExtraInfo.setValue(keyWorkConfig.getRemark());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getFunctionClass() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("FUNCTION_CLASS");
            flowRunExtraInfo.setValue(keyWorkConfig.getFunctionClass());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getBatchBomNodeCode() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("BATCH_BOM_NODE_CODE");
            flowRunExtraInfo.setValue(keyWorkConfig.getBatchBomNodeCode());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getKeyWorkType() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("KEY_WORK_TYPE");
            flowRunExtraInfo.setValue(keyWorkConfig.getKeyWorkType());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getCarNoList() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("CAR");
            flowRunExtraInfo.setValue(String.join(",", keyWorkConfig.getCarNoList()));
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getPosition() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("POSITION");
            flowRunExtraInfo.setValue(keyWorkConfig.getPosition());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getWorkEnv() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("WORK_ENV");
            flowRunExtraInfo.setValue(keyWorkConfig.getWorkEnv());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getTeamCode() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("TEAMCODE");
            flowRunExtraInfo.setValue(keyWorkConfig.getTeamCode());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getTeamName() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("TEAMNAME");
            flowRunExtraInfo.setValue(keyWorkConfig.getTeamName());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }

        if (keyWorkConfig.getFaultId() != null) {
            flowRunExtraInfo = new FlowRunExtraInfo();
            flowRunExtraInfo.setType("FAULT_ID");
            flowRunExtraInfo.setValue(keyWorkConfig.getFaultId());
            flowRunExtraInfos.add(flowRunExtraInfo);
        }
        List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoList = keyWorkConfig.getUploadedFileInfos();
        if (uploadedFileInfoList != null) {
            int sort = 1;
            for (UploadedFileInfo uploadedFileInfo : uploadedFileInfoList) {
                flowRunExtraInfo = new FlowRunExtraInfo();
                flowRunExtraInfo.setType("PICTURE_URL");
                flowRunExtraInfo.setValue(uploadedFileInfo.getRelativeUrl() + "," + uploadedFileInfo.getRelativePath() + "," + sort);
                flowRunExtraInfos.add(flowRunExtraInfo);
                sort++;
            }
        }
        if (uploadedFileInfos != null) {
            int sort = 1;
            for (UploadedFileInfo uploadedFileInfo : uploadedFileInfos) {
                flowRunExtraInfo = new FlowRunExtraInfo();
                flowRunExtraInfo.setType("PICTURE_URL");
                flowRunExtraInfo.setValue(uploadedFileInfo.getRelativeUrl() + "," + uploadedFileInfo.getRelativePath() + "," + sort);
                flowRunExtraInfos.add(flowRunExtraInfo);
                sort++;
            }
        }

        //????????????????????????
        for (FlowRunExtraInfo runExtraInfo : flowRunExtraInfos) {
            runExtraInfo.setFlowRunId(flowRunId);
            flowRunExtraInfoService.addFlowRunExtraInfo(runExtraInfo);
        }
    }

    /**
     * ????????????????????????
     */
    public KeyWorkFlowRunInfo parseFlowRunExtraInfo(KeyWorkFlowRunInfo keyWorkFlowRunInfo, List<FlowRunExtraInfo> flowRunExtraInfos) {
        List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFilePathInfos = new ArrayList<>();
        for (FlowRunExtraInfo flowRunExtraInfo : flowRunExtraInfos) {
            switch (flowRunExtraInfo.getType()) {
                case "CONTENT":
                    keyWorkFlowRunInfo.setContent(flowRunExtraInfo.getValue());
                    break;
                case "REMARK":
                    keyWorkFlowRunInfo.setRemark(flowRunExtraInfo.getValue());
                    break;
                case "FUNCTION_CLASS":
                    keyWorkFlowRunInfo.setFunctionClass(flowRunExtraInfo.getValue());
                    break;
                case "BATCH_BOM_NODE_CODE":
                    keyWorkFlowRunInfo.setBatchBomNodeCode(flowRunExtraInfo.getValue());
                    break;
                case "KEY_WORK_TYPE":
                    keyWorkFlowRunInfo.setKeyWorkType(flowRunExtraInfo.getValue());
                    break;
                case "CAR":
                    if (flowRunExtraInfo.getValue() != null) {
                        keyWorkFlowRunInfo.setCarNoList(Arrays.asList(flowRunExtraInfo.getValue().split(",")));
                    }
                    break;
                case "POSITION":
                    keyWorkFlowRunInfo.setPosition(flowRunExtraInfo.getValue());
                    break;
                case "WORK_ENV":
                    keyWorkFlowRunInfo.setWorkEnv(flowRunExtraInfo.getValue());
                    break;
                case "TEAMCODE":
                    keyWorkFlowRunInfo.setTeamCode(flowRunExtraInfo.getValue());
                    break;
                case "TEAMNAME":
                    keyWorkFlowRunInfo.setTeamName(flowRunExtraInfo.getValue());
                    break;
                case "PICTURE_URL":
                    UploadedFileInfoWithPayload<UploadFileType> uploadFileTypeUploadedFileInfoWithPayload = new UploadedFileInfoWithPayload<>();
                    List<String> pictureUrls = Arrays.asList(flowRunExtraInfo.getValue().split(","));
                    if (pictureUrls.size() > 0) {
                        uploadFileTypeUploadedFileInfoWithPayload.setRelativeUrl(pictureUrls.get(0));
                        uploadFileTypeUploadedFileInfoWithPayload.setRelativePath(pictureUrls.get(1));
                        if (pictureUrls.size() > 2) {
                            uploadFileTypeUploadedFileInfoWithPayload.setIndexOfList(Integer.parseInt(pictureUrls.get(2)));
                        }
                        uploadedFilePathInfos.add(uploadFileTypeUploadedFileInfoWithPayload);
                    }
                    break;
            }
        }
        uploadedFilePathInfos.sort(Comparator.comparing(UploadedFileInfoWithPayload<UploadFileType>::getIndexOfList));
        keyWorkFlowRunInfo.setUploadedFilePathInfos(uploadedFilePathInfos);
        return keyWorkFlowRunInfo;
    }

    @Override
    public List<KeyWorkFlowRunWithFault> getKeyWorkFlowByFault(List<FaultWithKeyWork> faultWithKeyWorks) {
        List<KeyWorkFlowRunWithFault> keyWorkFlowRunInfoList = new ArrayList<>();
        for (FaultWithKeyWork faultWithKeyWork : faultWithKeyWorks) {
            KeyWorkFlowRunWithFault keyWorkFlowRunInfo = new KeyWorkFlowRunWithFault();
            BeanUtils.copyProperties(faultWithKeyWork, keyWorkFlowRunInfo);
            if (faultWithKeyWork.getCarNo().equals("??????")) {
                keyWorkFlowRunInfo.setCarNoList(new ArrayList<>());
            } else if (StringUtils.isNotBlank(faultWithKeyWork.getCarNo())) {
                List<String> carNoList = new ArrayList<>();
                String carNo = faultWithKeyWork.getCarNo();
                carNoList.add(carNo.substring(carNo.length() - 2, carNo.length()));
                keyWorkFlowRunInfo.setCarNoList(carNoList);
            }
            keyWorkFlowRunInfo.setFunctionClass(faultWithKeyWork.getSysFunctionCode());
            keyWorkFlowRunInfo.setContent(faultWithKeyWork.getFaultDescription());
            keyWorkFlowRunInfo.setDataSource(KeyWorkDateSourceEnum.FAULT.getValue());
            keyWorkFlowRunInfo.setBatchBomNodeCode(faultWithKeyWork.getNodeCode());
            keyWorkFlowRunInfoList.add(keyWorkFlowRunInfo);
        }
        return keyWorkFlowRunInfoList;
    }


    @Override
    @Transactional
    public void setKeyWorkFlowByFault(List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList) throws ParseException {
        keyWorkFlowRunInfoList.forEach(keyWorkFlowRunInfo -> {
            keyWorkFlowRunInfo.setUploadedFileInfos(uploadFaultImageToKeyWork(keyWorkFlowRunInfo.getFaultId()));
        });
        this.addKeyWorkFlowRunInfo(keyWorkFlowRunInfoList, null);
    }

    public List<UploadedFileInfoWithPayload<UploadFileType>> uploadFaultImageToKeyWork(String faultId) {
        List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoWithPayloads = new ArrayList<>();
        //???????????????
        List<FaultFile> faultFiles = remoteService.getFaultFileByFaultId(faultId).stream().filter(v -> StringUtils.isNotBlank(v.getFileName())).collect(Collectors.toList());
        faultFiles.forEach(faultFile -> {
            String faultFileAddress = faultProperties.getFileAddress();
            File file = new File(faultFileAddress + faultFile.getFileName());
            FileItemFactory fileItemFactory = new DiskFileItemFactory(16, null);
            FileItem item = fileItemFactory.createItem(file.getName(), "text/plain", true, file.getName());

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                OutputStream outputStream = item.getOutputStream();
                while ((bytesRead = fileInputStream.read(buffer, 0, 8192)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            MultipartFile multipartFile = new CommonsMultipartFile(item);
            if (UpLoadFileUtils.isImage(multipartFile)) {
                String dateDirName = DateTimeUtil.format(new Date(), Constants.DEFAULT_DATE_FORMAT);
                String dirPrefix = RepairWorkFlowConstants.UPLOAD_PATH + "/" + RepairWorkFlowConstants.KEY_WORK_MODULE_NAME + "/" + dateDirName;
                uploadedFileInfoWithPayloads.add(UpLoadFileUtils.UploadMultipartFile(multipartFile, dirPrefix + "/images"));
            }
        });
        return uploadedFileInfoWithPayloads;
    }

    @Transactional
    public String addKeyWorkFlowRunInfo(List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList, List<UploadedFileInfo> uploadedFileInfos) throws ParseException {
        String returnFlowId = "";
        String dayPlanId = DayPlanUtil.getDayPlanId(ContextUtils.getUnitCode());
        List<TrainsetBaseInfo> trainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
        String[] trainsetIds = keyWorkFlowRunInfoList.stream().map(FlowRun::getTrainsetId).distinct().toArray(String[]::new);
        List<TrainsetPostionCur> trainsetPostionCurList = trainsetPostIonCurService.getByTrainsetIds(trainsetIds);
        Map<String, TrainsetPostionCur> trainsetPostionCurMap = CommonUtils.collectionToMap(trainsetPostionCurList, TrainsetPostionCur::getTrainsetId);

        Boolean verifyTrackByTrainSet = FlowDatabaseConfigUtil.isVerifyTrackByTrainSet();
        if (verifyTrackByTrainSet) {
            for (String trainsetId : trainsetIds) {
                if (!trainsetPostionCurMap.containsKey(trainsetId)) {
                    String trainSetName = trainsetBaseInfos.stream().filter(v -> v.getTrainsetid().equals(trainsetId)).collect(Collectors.toList()).get(0).getTrainsetname();
                    throw new RuntimeException("??????" + trainSetName + "??????????????????????????????????????????????????????????????????????????????????????????");
                }
            }
        }


        for (KeyWorkFlowRunInfo keyWorkFlowRunInfo : keyWorkFlowRunInfoList) {
            String trainsetId = keyWorkFlowRunInfo.getTrainsetId();
            TrainsetBaseInfo trainsetBaseInfo = trainsetBaseInfos.stream().filter(v -> v.getTrainsetid().equals(trainsetId)).collect(Collectors.toList()).get(0);
            String trainType = trainsetBaseInfo.getTraintype();
            String trainTemplate = trainsetBaseInfo.getTraintempid();

            //????????????????????????????????????????????????
            Set<String> flowTypeCode = new HashSet<>();
            flowTypeCode.add(BasicFlowTypeEnum.PLANLESS_KEY.getValue());
            QueryFlowModelGeneral queryFlowModelGeneral = new QueryFlowModelGeneral();
            queryFlowModelGeneral.setFlowTypeCodes(flowTypeCode.toArray(new String[0]));
            queryFlowModelGeneral.setUnitCode(ContextUtils.getUnitCode());
            queryFlowModelGeneral.setUsable(FlowUtil.booleanToString(true));
            List<FlowInfo> flowInfoList = flowService.getFlowInfoList(queryFlowModelGeneral).stream().filter(v -> v.getDefaultType()).collect(Collectors.toList());

            String flowId = null;
            int level = -1;
            for (FlowInfo flowInfo : flowInfoList) {
                if (flowInfo.getTrainTemplates().size() == 0 && flowInfo.getTrainTypes().size() == 0 && flowInfo.getTrainTypes().size() == 0) {
                    if (level < 0) {
                        flowId = flowInfo.getId();
                        level = 0;
                    }
                }
                if (flowInfo.getTrainTypes().contains(trainType) && flowInfo.getTrainTemplates().size() == 0) {
                    if (level < 1) {
                        flowId = flowInfo.getId();
                        level = 1;
                    }
                }
                if (flowInfo.getTrainTemplates().contains(trainTemplate) && flowInfo.getTrainsetIds().size() == 0) {
                    if (level < 2) {
                        flowId = flowInfo.getId();
                        level = 2;
                    }
                }
                if (flowInfo.getTrainsetIds().contains(trainsetId)) {
                    flowId = flowInfo.getId();
                    break;
                }
            }
            if (flowId == null) {
                throw new RuntimeException("????????????????????????????????????????????????");
            }

            FlowRun flowRun = new FlowRun();
            flowRun.setDayPlanId(keyWorkFlowRunInfo.getDayPlanId());
            flowRun.setId(keyWorkFlowRunInfo.getId());
            flowRun.setTrainsetId(trainsetId);
            flowRun.setFlowId(flowId);
            flowRun.setStartWorkerId(UserUtil.getUserInfo().getStaffId());
            flowRun.setStartTime(new Date());
            flowRun.setStartWorkerName(UserUtil.getUserInfo().getName());
            flowRun.setStartType(FlowRunStartTypeEnum.PERSON.getValue());
            flowRun.setState(FlowRunStateEnum.RUNNING.getValue());
            flowRun.setFlowTypeCode(BasicFlowTypeEnum.PLANLESS_KEY.getValue());
            flowRun.setDayPlanId(dayPlanId);

            if (verifyTrackByTrainSet) {
                TrainsetPostionCur trainsetPostionCur = trainsetPostionCurMap.get(trainsetId);
                flowRun.setTrackCode(trainsetPostionCur.getTrackCode());
                flowRun.setTrackName(trainsetPostionCur.getTrackName());
                flowRun.setHeadTrackPositionCode(trainsetPostionCur.getHeadDirectionPlaCode());
                flowRun.setHeadTrackPositionName(trainsetPostionCur.getHeadDirectionPla());
                flowRun.setTailTrackPositionCode(trainsetPostionCur.getTailDirectionPlaCode());
                flowRun.setTailTrackPositionName(trainsetPostionCur.getTailDirectionPla());
            }


            flowRun.setUnitCode(ContextUtils.getUnitCode());
            this.insert(flowRun);
            this.parseAddFlowRunExtraInfo(keyWorkFlowRunInfo, uploadedFileInfos, flowRun.getId());
            returnFlowId = flowRun.getId();
        }
        return returnFlowId;
    }

    @Override
    public Set<String> getFaultIdList() {
        String unitCode = ContextUtils.getUnitCode();
        return flowRunMapper.getFaultIdList(unitCode);
    }

    @Override
    public List<KeyWorkFlowRunWithTrainset> getKeyWorkFlowRunWithTrainsetList(List<String> dayPlanIds, String unitCode, String flowId, Boolean showForceEndFlowRun) {
        List<TrackPowerStateCur> trackPowerStateCurs = trackPowerStateCurService.getAllTrackPowers(unitCode);
        Set<String> trackCodes = trackPowerStateCurs.stream().map(v -> v.getTrackCode()).collect(Collectors.toSet());
        //??????????????????????????????+??????????????????id
        String dayPlanId = DayPlanUtil.getDayPlanId(unitCode);
        List<String> queryAllDayPlanIds = new ArrayList<>();
        queryAllDayPlanIds.addAll(dayPlanIds);
        queryAllDayPlanIds.add(dayPlanId);



        List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList = getKeyWorkFlowRunInfoList(queryAllDayPlanIds, null, null, null, null, null, flowId, null, unitCode, false, null, null, true, showForceEndFlowRun, true);
        keyWorkFlowRunInfoList = keyWorkFlowRunInfoList.stream().filter(v -> (dayPlanIds.contains(v.getDayPlanId()) && v.getState().equals("0")) || v.getDayPlanId().equals(dayPlanId)).collect(Collectors.toList());
        Set<String> trainsetIds = keyWorkFlowRunInfoList.stream().map(v -> v.getTrainsetId()).collect(Collectors.toSet());

        //?????????person???????????????????????????????????????
        for (KeyWorkFlowRunInfo keyWorkFlowRunInfo : keyWorkFlowRunInfoList) {
            for (NodeWithRecord nodeWithRecord : keyWorkFlowRunInfo.getNodeWithRecords()) {
                nodeWithRecord.setNodeRecords(nodeWithRecord.getNodeRecords().stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList()));
            }
        }

        //???????????????????????????????????????
        List<TrainsetPostionCur> trainsetPostIonCurs = trainsetPostIonCurService.getTrainsetPostion(trackCodes, unitCode);
        trainsetPostIonCurs = trainsetPostIonCurs.stream().filter(v -> trainsetIds.contains(v.getTrainsetId())).collect(Collectors.toList());
        List<KeyWorkFlowRunWithTrainset> keyWorkFlowRunWithTrainsetList = new ArrayList<>();
        for (TrainsetPostionCur trainsetPostIonCur : trainsetPostIonCurs) {
            String trainsetId = trainsetPostIonCur.getTrainsetId();
            KeyWorkFlowRunWithTrainset keyWorkFlowRunWithTrainset = new KeyWorkFlowRunWithTrainset();
            BeanUtils.copyProperties(trainsetPostIonCur, keyWorkFlowRunWithTrainset);
            keyWorkFlowRunWithTrainset.setKeyWorkFlowRunInfos(keyWorkFlowRunInfoList.stream().filter(v -> v.getTrainsetId().equals(trainsetId)).collect(Collectors.toList()));
            keyWorkFlowRunWithTrainsetList.add(keyWorkFlowRunWithTrainset);
        }
        keyWorkFlowRunWithTrainsetList.forEach(keyWorkFlowRunWithTrainset -> {
            keyWorkFlowRunWithTrainset.getKeyWorkFlowRunInfos().sort(((o1, o2) -> {
                Integer state1 = Integer.parseInt(o1.getState());
                Integer state2 = Integer.parseInt(o2.getState());
                if (state1 > state2) {
                    return 1;
                } else if (state1 < state2) {
                    return -1;
                } else {
                    Date date1 = o1.getStartTime();
                    Date date2 = o2.getStartTime();
                    if (date1.after(date2)) {
                        return 1;
                    } else if (date2.after(date1)) {
                        return -1;
                    }
                    return 0;
                }

            }));
        });
        return keyWorkFlowRunWithTrainsetList;
    }

    @Override
    public List<TemporaryFlowRunWithTrainset> getTemporaryFlowRunWithTrainsetList(List<String> dayPlanIds, String unitCode) {
        List<TrackPowerStateCur> trackPowerStateCurs = trackPowerStateCurService.getAllTrackPowers(unitCode);
        Set<String> trackCodes = trackPowerStateCurs.stream().map(v -> v.getTrackCode()).collect(Collectors.toSet());

        List<TemporaryFlowRunWithTrainset> temporaryFlowRunWithTrainsetsByDayPlanIds = new ArrayList<>();
        for (String dayPlanId : dayPlanIds) {
            //????????????????????????????????????
            FlowRunInfo flowRunInfo = getFlowRunInfoByTrainset(FlowPageCodeEnum.TEMPORARY.getValue(), unitCode, dayPlanId, "", null, new ArrayList<>());
            List<FlowRunInfoGroup> flowRunInfoGroups = flowRunInfo.getFlowRunInfoGroups();

            //?????????????????????????????????
            Set<String> trainsetIds = flowRunInfoGroups.stream().filter(v -> v.getFlowRunInfoForSimpleShows().size() > 0).map(v -> v.getTrainsetId()).collect(Collectors.toSet());

            //???????????????????????????????????????
            List<TrainsetPostionCur> trainsetPostIonCurs = trainsetPostIonCurService.getTrainsetPostion(trackCodes, unitCode);
            //?????????????????????????????????
            List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, "", "", "", unitCode);

            //?????????????????????????????????????????????
            trainsetPostIonCurs = trainsetPostIonCurs.stream().filter(v -> trainsetIds.contains(v.getTrainsetId())).collect(Collectors.toList());

            List<TemporaryFlowRunWithTrainset> temporaryFlowRunWithTrainsets = new ArrayList<>();
            for (TrainsetPostionCur trainsetPostIonCur : trainsetPostIonCurs) {
                String trainsetId = trainsetPostIonCur.getTrainsetId();
                TemporaryFlowRunWithTrainset temporaryFlowRunWithTrainset = new TemporaryFlowRunWithTrainset();
                BeanUtils.copyProperties(trainsetPostIonCur, temporaryFlowRunWithTrainset);
                List<NodeInfoForSimpleShow> nodeInfoForSimpleShows = new ArrayList<>();
                //??????????????????????????????
                List<FlowRunInfoGroup> flowRunInfoGroupsByTrainsetId = flowRunInfoGroups.stream().filter(v -> v.getTrainsetId().equals(trainsetId)).collect(Collectors.toList());
                for (FlowRunInfoGroup flowRunInfoGroup : flowRunInfoGroupsByTrainsetId) {
                    for (FlowRunInfoForSimpleShow flowRunInfoForSimpleShow : flowRunInfoGroup.getFlowRunInfoForSimpleShows()) {
                        nodeInfoForSimpleShows = flowRunInfoForSimpleShow.getNodeList();
                    }
                }
                //?????????????????????????????????
                temporaryFlowRunWithTrainset.setTaskPacketEntities(taskPacketEntityList.stream().filter(v -> v.getTrainsetId().equals(trainsetId) && v.getPacketTypeCode().equals("3")).collect(Collectors.toList()));
                for (ZtTaskPacketEntity taskPacketEntity : temporaryFlowRunWithTrainset.getTaskPacketEntities()) {
                    List<XzyMTaskallotperson> taskallotpersonList = taskAllotPersonService.getPersonByPacket(taskPacketEntity.getPacketCode(), dayPlanId, taskPacketEntity.getTrainsetId());
                    List<String> workerNameList = taskallotpersonList.stream().map(v -> v.getWorkerName()).collect(Collectors.toList());
                    workerNameList = CommonUtils.getDistinctList(workerNameList, item -> item);
                    String workNameStr = workerNameList.toString();
                    taskPacketEntity.setWorkers(workNameStr.toString().substring(1, workNameStr.length() - 1));
                }
                temporaryFlowRunWithTrainset.setNodeInfoForSimpleShows(nodeInfoForSimpleShows);
                temporaryFlowRunWithTrainsets.add(temporaryFlowRunWithTrainset);
            }
            temporaryFlowRunWithTrainsetsByDayPlanIds.addAll(temporaryFlowRunWithTrainsets);
        }

        List<TemporaryFlowRunWithTrainset> temporaryFlowRunWithTrainsetListCopy = JSON.parseArray(JSON.toJSONString(temporaryFlowRunWithTrainsetsByDayPlanIds, SerializerFeature.DisableCircularReferenceDetect), TemporaryFlowRunWithTrainset.class);
        return temporaryFlowRunWithTrainsetListCopy;
    }

    @Transactional
    @Override
    public void forceEndFlowRun(FlowRunRecord flowRunRecord, String recordType) {
        Assert.notEmpty(flowRunRecord.getFlowRunId(), "????????????id????????????");
        //?????????????????????
        if (recordType.equals(FlowRunRecordTypeEnum.REJECT_END.getValue())) {
            Assert.notEmpty(flowRunRecord.getRemark(), "??????????????????");
        }
        Assert.notEmpty(flowRunRecord.getWorkerId(), "??????id????????????");
        Assert.notEmpty(flowRunRecord.getWorkerName(), "?????????????????????");

        if (
            MybatisPlusUtils.selectExist(
                this,
                eqParam(FlowRun::getId, flowRunRecord.getFlowRunId()),
                eqParam(FlowRun::getState, FlowRunStateEnum.ENDED.getValue())
            )
        ) {
            throw new RuntimeException("?????????????????????????????????????????????");
        }

        FlowRun flowRun = new FlowRun();
        flowRun.setId(flowRunRecord.getFlowRunId());
        flowRun.setState(FlowRunStateEnum.ENDED.getValue());
        if (!this.updateById(flowRun)) {
            throw new RuntimeException("??????????????????????????????????????????????????????");
        }

        flowRunRecord.setId(UUID.randomUUID().toString());
        flowRunRecord.setType(recordType);
        flowRunRecord.setRecordTime(new Date());
        flowRunRecordService.insert(flowRunRecord);
    }


    @Override
    public FlowRunInfoWithStatistics getFlowRunRecordList(String unitCode, String trainsetId, String flowTypeCode, String teamCode, String workId, String flowRunState, String startTime, String endTime, String flowName, boolean queryPastFlow, String flowPageCode, String dayPlanCode, boolean nodeAllRecord) throws ParseException {
        //flowPageCode???????????? ??????flowTypeCode?????? ??????flowPageCode?????????????????????????????????flowPageCode???????????????
        List<String> flowTypeCodes = new ArrayList<>();
        //???????????????????????????flowPageCode????????????/???????????????????????????
        if (StringUtils.isNotBlank(flowTypeCode)) {
            flowTypeCodes.add(flowTypeCode);
        } else {
            if (StringUtils.isBlank(flowPageCode)) { //??????????????????
                flowTypeCodes = flowTypeService.getFlowTypeList(unitCode).stream().map(v -> v.getCode()).collect(Collectors.toList());
            } else { //???????????????
                List<BaseFlowType> flowTypeList = flowTypeService.getFlowTypesByFlowPageCode(flowPageCode, unitCode);
                flowTypeCodes.addAll(flowTypeList.stream().map(v -> v.getCode()).collect(Collectors.toList()));
            }
        }

        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isNotBlank(startTime)) {
            startDate = simpleDateFormat.parse(startTime);
        }
        if (StringUtils.isNotBlank(endTime)) {
            endDate = simpleDateFormat.parse(endTime);
        }
        //????????????????????????
        Flow paramFlow = new Flow();
        if (StringUtils.isNotBlank(flowName)) {
            paramFlow.setName(flowName);
        }
        if (!queryPastFlow) {
            paramFlow.setDeleted(FlowUtil.booleanToString(false));
        }
        paramFlow.setUnitCode(unitCode);
        List<Flow> flowList = flowMapper.selectList(new EntityWrapper<>(paramFlow));


        Set<String> flowIds = flowList.stream().map(v -> v.getId()).collect(Collectors.toSet());
        String queryDataFlowRunState = null;
        //?????????????????????,?????????????????????
        if (StringUtils.isNotBlank(flowRunState) && (flowRunState.equals("2") || flowRunState.equals("3"))) {
            queryDataFlowRunState = "1";
        } else {
            queryDataFlowRunState = flowRunState;
        }


        //??????????????????
        List<FlowRunWithExtraInfo> flowRunWithExtraInfoList = this.getFlowRunWithExtraInfos(null, flowTypeCodes, null, trainsetId, queryDataFlowRunState, startDate, endDate, null, null, unitCode, dayPlanCode);
        //????????????????????????????????????
        flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowIds.contains(v.getFlowId())).collect(Collectors.toList());


        //?????????/??????/?????????????????????
        List<FlowRunRecordInfo> flowRunRecordInfos = flowRunRecordService.getFlowRunRecordsByForceEnd(unitCode, null, null, null, null);
        //???????????????????????????????????????id??????
        List<String> flowRunIdByForceEndToMaps = flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.REJECT_END.getValue())).map(n -> n.getFlowRunId()).collect(Collectors.toList());
        //???????????????????????????????????????id??????
        List<String> flowRunIdByRevokeEndToMaps = flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.REVOKE_END.getValue())).map(n -> n.getFlowRunId()).collect(Collectors.toList());
        if (StringUtils.isNotBlank(flowRunState)) {
            if (flowRunState.equals("2")) {
                flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowRunIdByForceEndToMaps.contains(v.getId())).collect(Collectors.toList());
            }
            if (flowRunState.equals("3")) {
                flowRunWithExtraInfoList = flowRunWithExtraInfoList.stream().filter(v -> flowRunIdByRevokeEndToMaps.contains(v.getId())).collect(Collectors.toList());
            }
        }


        List<String> flowRunIds = flowRunWithExtraInfoList.stream().map(v -> v.getId()).collect(Collectors.toList());


        //???????????????????????????????????????
        QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
        queryNodeRecord.setFlowRunIds(flowRunIds.stream().toArray(String[]::new));
        List<NodeRecordInfo> nodeRecordInfos = nodeRecordService.getNodeRecordInfoList(queryNodeRecord);

        List<FlowRunInfoWithKeyWorkBase> flowRunInfoWithKeyWorkBases = new ArrayList<>();
        for (FlowRunWithExtraInfo flowRunWithExtraInfo : flowRunWithExtraInfoList) {
            Set<Boolean> hasNodeRecordWorkIdAndTeamCodes = new HashSet<>();
            String curFlowId = flowRunWithExtraInfo.getFlowId();
            FlowRunInfoWithKeyWorkBase flowRunInfoWithKeyWorkBase = new FlowRunInfoWithKeyWorkBase();
            BeanUtils.copyProperties(flowRunWithExtraInfo, flowRunInfoWithKeyWorkBase);
            //????????????????????????
            Flow flow = flowService.getFlowByFlowId(curFlowId);
            flowRunInfoWithKeyWorkBase.setFlowConfig(flow);
            //????????????????????????
            FlowInfo flowInfo = flowService.getFlowInfoByFlowId(curFlowId);
            flowInfo.setNodes(FlowUtil.getSortedNodes(flowInfo));

            List<NodeInfoForSimpleShow> nodeWithRecords = new ArrayList<>();

            for (NodeInfo node : flowInfo.getNodes()) {
                NodeInfoForSimpleShow nodeInfoForSimpleShow = getNodeInfoForSimpleShowByNode(node, nodeRecordInfos, flowInfo, flowRunWithExtraInfo);
                //???????????????
                String childFlowId = node.getChildFlowId();
                if (StringUtils.isNotBlank(childFlowId)) {
                    List<NodeInfoForSimpleShow> subNodeInfoForSimpleShows = new ArrayList<>();
                    //??????????????????????????????????????????
                    FlowInfo subFlowInfo = flowService.getFlowInfoByFlowId(childFlowId);
                    subFlowInfo.setNodes(FlowUtil.getSortedNodes(subFlowInfo));
                    for (NodeInfo subFlowInfoNode : subFlowInfo.getNodes()) {
                        NodeInfoForSimpleShow subNodeInfoForSimpleShow = getNodeInfoForSimpleShowByNode(subFlowInfoNode, nodeRecordInfos, subFlowInfo, flowRunWithExtraInfo);
                        boolean hasWorkIdAndTeamCode = hasNodeRecordWorkIdAndTeamCode(workId, teamCode, flowRunIdByRevokeEndToMaps.contains(flowRunWithExtraInfo.getId()), subNodeInfoForSimpleShow);
                        hasNodeRecordWorkIdAndTeamCodes.add(hasWorkIdAndTeamCode);
                        if (hasWorkIdAndTeamCode) {
                            nodeWithRecords.add(subNodeInfoForSimpleShow);
                        }
                    }
                    nodeInfoForSimpleShow.setChildren(subNodeInfoForSimpleShows);
                }
                //???????????????????????????
                boolean hasWorkIdAndTeamCode = hasNodeRecordWorkIdAndTeamCode(workId, teamCode, flowRunIdByRevokeEndToMaps.contains(flowRunWithExtraInfo.getId()), nodeInfoForSimpleShow);
                hasNodeRecordWorkIdAndTeamCodes.add(hasWorkIdAndTeamCode);
                if (hasWorkIdAndTeamCode) {
                    if (!nodeAllRecord) {
                        if (StringUtils.isNotBlank(teamCode)) {
                            nodeInfoForSimpleShow.setNodeRecords(nodeInfoForSimpleShow.getNodeRecords().stream().filter(v -> v.getTeamCode().equals(teamCode)).collect(Collectors.toList()));
                        }
                        if (StringUtils.isNotBlank(workId)) {
                            nodeInfoForSimpleShow.setNodeRecords(nodeInfoForSimpleShow.getNodeRecords().stream().filter(v -> v.getWorkerId().equals(workId)).collect(Collectors.toList()));
                        }
                    }
                    nodeWithRecords.add(nodeInfoForSimpleShow);
                }
            }
            flowRunInfoWithKeyWorkBase.setNodeList(nodeWithRecords);
            if (hasNodeRecordWorkIdAndTeamCodes.contains(true)) {
                KeyWorkFlowRunInfo keyWorkFlowRunInfo = new KeyWorkFlowRunInfo();
                parseFlowRunExtraInfo(keyWorkFlowRunInfo, flowRunInfoWithKeyWorkBase.getFlowRunExtraInfoList());
                flowRunInfoWithKeyWorkBase.setContent(keyWorkFlowRunInfo.getContent());
                flowRunInfoWithKeyWorkBase.setCarNoList(keyWorkFlowRunInfo.getCarNoList());
                flowRunInfoWithKeyWorkBases.add(flowRunInfoWithKeyWorkBase);
            }

        }

        List<FlowRunInfoWithKeyWorkBase> flowRunInfoForSimpleShowCopy = JSON.parseArray(JSON.toJSONString(flowRunInfoWithKeyWorkBases, SerializerFeature.DisableCircularReferenceDetect), FlowRunInfoWithKeyWorkBase.class);


        Map<String, List<FlowRunInfoWithKeyWorkBase>> groupedFlowRunByFlowTypeCode = flowRunInfoWithKeyWorkBases.stream().collect(
            Collectors.groupingBy(FlowRunInfoWithKeyWorkBase::getFlowTypeCode)
        );
        List<BaseFlowType> flowTypes = flowTypeService.getFlowTypeList(unitCode);
        Map<String, BaseFlowType> flowTypeMap = CommonUtils.collectionToMap(flowTypes, BaseFlowType::getCode);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("????????????" + flowRunInfoWithKeyWorkBases.size() + "???????????????");
        for (String key : groupedFlowRunByFlowTypeCode.keySet()) {
            BaseFlowType flowType = flowTypeMap.get(key);
            stringBuffer.append("," + flowType.getName() + "???" + groupedFlowRunByFlowTypeCode.get(key).size() + "???");
        }
        FlowRunInfoWithStatistics flowRunInfoWithStatistics = new FlowRunInfoWithStatistics();
        flowRunInfoWithStatistics.setFlowRunInfoWithKeyWorkBases(flowRunInfoForSimpleShowCopy);
        flowRunInfoWithStatistics.setStatisticsResult(stringBuffer.toString());

        List<String> trainsetIdList = flowRunInfoWithStatistics.getFlowRunInfoWithKeyWorkBases().stream().map(v -> v.getTrainsetId()).collect(Collectors.toList());
        float standardGroup = 0;
        for (String queryTrainsetId : trainsetIdList) {
            List<String> carNoList = remoteService.getCarno(queryTrainsetId);
            standardGroup = standardGroup + (float) carNoList.size() / 8;
        }
        flowRunInfoWithStatistics.setStandardGroupResult("??????" + trainsetIdList.size() + "?????????," + standardGroup + "?????????");
        return flowRunInfoWithStatistics;
    }

    private NodeInfoForSimpleShow getNodeInfoForSimpleShowByNode(NodeInfo node, List<NodeRecordInfo> nodeRecordInfos, FlowInfo flowInfo, FlowRun flowRun) {
        NodeInfoForSimpleShow nodeInfoForSimpleShow = new NodeInfoForSimpleShow();
        BeanUtils.copyProperties(node, nodeInfoForSimpleShow);
        setNodeWithRecord(nodeInfoForSimpleShow, nodeRecordInfos, UserUtil.getUserInfo().getStaffId(), new ArrayList<>(), flowInfo, flowRun);
        nodeInfoForSimpleShow.setNodeRecords(nodeInfoForSimpleShow.getNodeRecords().stream().filter(FlowUtil::normalTypeNodeRecordFilter).collect(Collectors.toList()));
        return nodeInfoForSimpleShow;
    }

    public boolean hasNodeRecordWorkIdAndTeamCode(String workId, String teamCode, Boolean RevokeEnd, NodeInfoForSimpleShow nodeInfoForSimpleShow) {
        boolean hasNodeRecordWorkId = true;
        boolean hasNodeRecordTeamCode = true;

        if (nodeInfoForSimpleShow.getNodeRecords().size() == 0 && !RevokeEnd) {
            return false;
        }

        if (StringUtils.isNotBlank(workId)) {
            hasNodeRecordWorkId = nodeInfoForSimpleShow.getNodeRecords().stream().anyMatch(v -> v.getWorkerId().equals(workId));
        }
        if (StringUtils.isNotBlank(teamCode)) {
            hasNodeRecordTeamCode = nodeInfoForSimpleShow.getNodeRecords().stream().anyMatch(v -> v.getTeamCode().equals(teamCode));
        }
        return hasNodeRecordWorkId && hasNodeRecordTeamCode;
    }

    @Override
    public List<TrainsetBaseInfo> getTrainsetByDateAndFlowTypeCode(String unitCode, String flowTypeCode, String startDate, String endDate, String flowPageCode) throws ParseException {
        List<String> flowTypeCodes = new ArrayList<>();
        if (StringUtils.isNotBlank(flowTypeCode)) {
            flowTypeCodes.add(flowTypeCode);
        } else {
            if (StringUtils.isBlank(flowPageCode)) { //??????????????????
                flowTypeCodes = flowTypeService.getFlowTypeList(unitCode).stream().map(v -> v.getCode()).collect(Collectors.toList());
            } else { //???????????????
                List<BaseFlowType> flowTypeList = flowTypeService.getFlowTypesByFlowPageCode(flowPageCode, unitCode);
                flowTypeCodes.addAll(flowTypeList.stream().map(v -> v.getCode()).collect(Collectors.toList()));
            }
        }
        Date startTime = null;
        Date endTime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isNotBlank(startDate)) {
            startTime = simpleDateFormat.parse(startDate);
        }
        if (StringUtils.isNotBlank(endDate)) {
            endTime = simpleDateFormat.parse(endDate);
        }
        List<FlowRunWithExtraInfo> flowRunWithExtraInfoList = this.getFlowRunWithExtraInfos(null, flowTypeCodes, null, null, null, startTime, endTime, null, null, unitCode, null);
        Set<String> trainSetIds = flowRunWithExtraInfoList.stream().map(v -> v.getTrainsetId()).collect(Collectors.toSet());
        List<TrainsetBaseInfo> trainsetBaseInfos = CacheUtil.getDataUseThreadCache(
            "remoteService.getTrainsetListReceived_" + ContextUtils.getUnitCode(),
            () -> remoteService.getTrainsetList()
        );
        trainsetBaseInfos = trainsetBaseInfos.stream().filter(v -> trainSetIds.contains(v.getTrainsetid())).collect(Collectors.toList());
        return trainsetBaseInfos;
    }

    @Override
    public FlowRunInfo getFlowRunInfosByTrainMonitor(String flowPageCode, String unitCode, String dayPlanId, String trainsetIdListStr, boolean showDayRepairTask) {
        //???????????????????????????????????????
        Set<String> flowTypeCodeByFlowPageCode = getFlowTypeCodesByFlowPageCode(flowPageCode, unitCode);

        List<String> trainsetIdList;
        if (StringUtils.isNotBlank(trainsetIdListStr)) {
            trainsetIdList = Arrays.asList(trainsetIdListStr.split(","));
        } else {
            trainsetIdList = new ArrayList<>();
        }
        //???????????????ID???????????????????????????????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList;
        if (trainsetIdList.size() == 1) {
            taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetIdListStr, "", "", unitCode);
        } else {
            taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, "", "", "", unitCode);
            taskPacketEntityList = taskPacketEntityList.stream().filter(v -> trainsetIdList.contains(v.getTrainsetId())).collect(Collectors.toList());
        }

        Map<String, List<ZtTaskPacketEntity>> groupedTaskPacketByTrainsetId = taskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
        FlowRunInfo flowRunInfo = new FlowRunInfo();
        List<FlowRunInfoGroup> flowRunInfoGroups = new ArrayList<>();
        //??????????????????
        if (showDayRepairTask) {
            flowRunInfo = this.getFlowRunInfoByTrainset(flowPageCode, unitCode, dayPlanId, trainsetIdListStr, null, new ArrayList<>());
        } else {
            //???????????????????????????
            FlowRunInfo flowRunInfoByDayPlanId = this.getFlowRunInfoByTrainset(flowPageCode, unitCode, dayPlanId, trainsetIdListStr, null, new ArrayList<>());
            Map<String, List<FlowRunInfoGroup>> groupedFlowRunInfoByDayPlanIdByTrainsetId = flowRunInfoByDayPlanId.getFlowRunInfoGroups().stream().collect(Collectors.groupingBy(FlowRunInfoGroup::getTrainsetId));
            //??????????????????????????????
            String lastDayPlanId = FlowUtil.getLastDayPlanId(dayPlanId);
            FlowRunInfo flowRunInfoByLastDayPlanId = this.getFlowRunInfoByTrainset(flowPageCode, unitCode, lastDayPlanId, trainsetIdListStr, null, new ArrayList<>());
            Map<String, List<FlowRunInfoGroup>> groupedFlowRunInfoByLastDayPlanId = flowRunInfoByLastDayPlanId.getFlowRunInfoGroups().stream().collect(Collectors.groupingBy(FlowRunInfoGroup::getTrainsetId));

            for (String trainsetId : trainsetIdList) {
                //?????????????????????
                List<ZtTaskPacketEntity> taskPacketEntities = groupedTaskPacketByTrainsetId.get(trainsetId);
                Set<String> triggerFlowTypes;
                if (taskPacketEntities != null) {
                    //??????????????????????????????????????????
                    List<MatchedFlowTypeInTrain> triggerFlowTypeCodeList = getMatchedFlowTypeCode(taskPacketEntities, unitCode);
                    triggerFlowTypes = triggerFlowTypeCodeList.stream().map(v -> v.getFlowTypeCode()).collect(Collectors.toSet());
                } else {
                    triggerFlowTypes = new HashSet<>();
                }
                //?????????????????????????????????????????????
                boolean hasFlowTypeByFlowPageCode = flowTypeCodeByFlowPageCode.stream().anyMatch(v -> triggerFlowTypes.contains(v));
                if (hasFlowTypeByFlowPageCode) {
                    //????????????????????????????????????
                    List<FlowRunInfoGroup> flowRunInfoGroupsByDayPlanId = groupedFlowRunInfoByDayPlanIdByTrainsetId.get(trainsetId);
                    if (flowRunInfoGroupsByDayPlanId != null && flowRunInfoGroupsByDayPlanId.size() > 0) {
                        flowRunInfoGroups.addAll(flowRunInfoGroupsByDayPlanId);
                    }
                } else {
                    //????????????????????????????????????
                    List<FlowRunInfoGroup> flowRunInfoGroupsByLastDayPlanId = groupedFlowRunInfoByLastDayPlanId.get(trainsetId);
                    if (flowRunInfoGroupsByLastDayPlanId != null && flowRunInfoGroupsByLastDayPlanId.size() > 0) {
                        flowRunInfoGroups.addAll(flowRunInfoGroupsByLastDayPlanId);
                    }
                }
            }
            flowRunInfo.setFlowRunInfoGroups(flowRunInfoGroups);
        }

        //??????????????????
        List<FlowTypeInfoWithPackets> flowTypeInfoWithPackets = flowTypeService.getFlowTypeAndPacket(unitCode);
        //????????????
        List<FlowInfo> flowInfoList = flowService.getFlowInfoList(QueryFlowModelGeneral.builder().unitCode(unitCode).build());

        flowRunInfo.setFlowRunInfoGroups(sortFlowRunInfoGroups(flowRunInfo.getFlowRunInfoGroups(), flowTypeInfoWithPackets, flowInfoList));
        return flowRunInfo;
    }

    @Override
    public List<String> getFlowRunList(Date startTime, Date endTime, String unitCode, String flowCode) {
        return flowRunMapper.getFlowRunList(startTime, endTime, unitCode, flowCode);
    }

    @Override
    public List<TrainsetBaseInfoWithTrack> getTrainsetListByTrack(String unitCode) {
        List<TrackPowerStateCur> trackPowerStateCurs = trackPowerStateCurService.getAllTrackPowers(unitCode);
        Set<String> trackCodes = trackPowerStateCurs.stream().map(v -> v.getTrackCode()).collect(Collectors.toSet());
        //?????????????????????
        List<TrainsetPostionCur> trainsetPostIonCurs = trainsetPostIonCurService.getTrainsetPostion(trackCodes, unitCode);
        List<String> existsTrackTrainsetId = trainsetPostIonCurs.stream().map(v -> v.getTrainsetId()).collect(Collectors.toList());

        List<TrainsetBaseInfo> processCarPartEntityList = remoteService.getTrainsetListReceived(unitCode);
        List<TrainsetBaseInfoWithTrack> trainsetBaseInfoWithTracks = new ArrayList<>();
        processCarPartEntityList.forEach(trainsetBaseInfo -> {
            TrainsetBaseInfoWithTrack trainsetBaseInfoWithTrack = new TrainsetBaseInfoWithTrack();
            BeanUtils.copyProperties(trainsetBaseInfo, trainsetBaseInfoWithTrack);
            if (existsTrackTrainsetId.contains(trainsetBaseInfoWithTrack.getTrainsetid())) {
                trainsetBaseInfoWithTrack.setHasTrack(true);
            } else {
                trainsetBaseInfoWithTrack.setHasTrack(false);
            }
            trainsetBaseInfoWithTracks.add(trainsetBaseInfoWithTrack);

        });
        return trainsetBaseInfoWithTracks;
    }

    /**
     * ??????????????????????????????
     */
    public void checkFlowRunEndCause(String unitCode, String flowRunId) {
        FlowRun flowRun = flowRunMapper.selectOne(FlowRun.builder().id(flowRunId).unitCode(unitCode).build());
        if (FlowRunStateEnum.ENDED.getValue().equals(flowRun.getState())) {
            List<FlowRunRecordInfo> flowRunRecordInfos = flowRunRecordService.getFlowRunRecordsByForceEnd(unitCode, null, flowRunId, null, null);
            //???????????????????????????????????????id??????
            Map<String, FlowRunRecordInfo> flowRunRecordInfoByRejectEndToMaps = CommonUtils.collectionToMap(flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.REJECT_END.getValue())).collect(Collectors.toList()), FlowRunRecordInfo::getFlowRunId);
            //???????????????????????????????????????id??????
            Map<String, FlowRunRecordInfo> flowRunRecordInfoByRevokeEndToMaps = CommonUtils.collectionToMap(flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.REVOKE_END.getValue())).collect(Collectors.toList()), FlowRunRecordInfo::getFlowRunId);
            //???????????????
            Map<String, FlowRunRecordInfo> flowRunRecordInfoByForceEndToMaps = CommonUtils.collectionToMap(flowRunRecordInfos.stream().filter(v -> v.getType().equals(FlowRunRecordTypeEnum.FORCE_END.getValue())).collect(Collectors.toList()), FlowRunRecordInfo::getFlowRunId);

            if (flowRunRecordInfoByRejectEndToMaps.get(flowRunId) != null) {
                throw RestRequestException.normalFail("??????????????????????????????!");
            } else if (flowRunRecordInfoByRevokeEndToMaps.get(flowRunId) != null) {
                throw RestRequestException.normalFail("??????????????????????????????!");
            } else if (flowRunRecordInfoByForceEndToMaps.get(flowRunId) != null) {
                throw RestRequestException.normalFail("????????????????????????????????????!");
            }
            throw RestRequestException.normalFail("??????????????????????????????!");
        }
    }

    @Override
    public List<FaultWithKeyWork> getCenterFaultInfo(FaultSearchWithKeyWork faultSearchWithKeyWork) {
        List<FaultWithKeyWork> faultWithKeyWorks = remoteService.getFaultInfoByPlanlessKey(faultSearchWithKeyWork);
        //??????????????????????????????
        faultWithKeyWorks = faultWithKeyWorks.stream().filter(v -> !v.getDealWithDesc().equals("?????????")).collect(Collectors.toList());

        //??????????????????????????????
        Set<String> faultIds = this.getFaultIdList();
        faultWithKeyWorks.forEach(faultWithKeyWork -> {
            if (faultIds.contains(faultWithKeyWork.getFaultId())) {
                faultWithKeyWork.setConvertKeyWork(true);
            } else {
                faultWithKeyWork.setConvertKeyWork(false);
            }
        });
        Boolean convertKeyWork = faultSearchWithKeyWork.getConvertKeyWork();
        if (convertKeyWork != null) {
            faultWithKeyWorks = faultWithKeyWorks.stream().filter(v -> v.getConvertKeyWork() == convertKeyWork).collect(Collectors.toList());
        }
        return faultWithKeyWorks;
    }

    private List<UploadedFileInfoWithPayload<UploadFileType>> setNodeDisposeUploadFiles(HttpServletRequest httpServletRequest) {
        String dateDirName = DateTimeUtil.format(new Date(), Constants.DEFAULT_DATE_FORMAT);
        String dirPrefix = RepairWorkFlowConstants.UPLOAD_PATH + "/" + RepairWorkFlowConstants.NODE_DISPOSE_MODULE_NAME + "/" + dateDirName;
        return UpLoadFileUtils.uploadFilesUsePayload(httpServletRequest, v -> {
            if (UpLoadFileUtils.isImage(v)) {
                return dirPrefix + "/images";
            } else if (UpLoadFileUtils.isVideo(v)) {
                return dirPrefix + "/videos";
            } else {
                return dirPrefix + "/error";
            }
        }, (v) -> UpLoadFileUtils.isVideo(v) || UpLoadFileUtils.isImage(v), v -> {
            if (UpLoadFileUtils.isImage(v)) {
                return UploadFileType.IMAGE;
            } else if (UpLoadFileUtils.isVideo(v)) {
                return UploadFileType.VIDEO;
            } else {
                return null;
            }
        });
    }
}
