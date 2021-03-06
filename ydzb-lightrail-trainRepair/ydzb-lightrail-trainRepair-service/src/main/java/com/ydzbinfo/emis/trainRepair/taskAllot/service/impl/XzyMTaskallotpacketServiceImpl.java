package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistSummaryService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistIntegration;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyMTaskallotpacketMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.utils.ProcessArrageTypeEnum;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.CustomServiceNameEnum;
import com.ydzbinfo.emis.utils.RestRequestKitUseLogger;
import com.ydzbinfo.emis.utils.ServiceNameEnum;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

@Service
public class XzyMTaskallotpacketServiceImpl implements XzyMTaskallotpacketService {

    @Resource
    XzyMTaskallotpacketMapper taskallotpacketMapper;

    @Autowired
    IXzyBTaskallottypeDictService taskallottypeDictService;

    @Autowired
    XzyMTaskcarpartService taskcarpartService;

    @Resource
    IRemoteService remoteService;

    @Resource
    IAggItemConfigService iAggItemConfigService;

    @Autowired
    XzyMTaskallotpacketService taskallotpacketService;

    @Autowired
    XzyMTaskAllotPersonService taskAllotPersonService;

    @Autowired
    IXzyBTaskallotshowDictService xzyBTaskallotshowDictService;

    @Autowired
    XzyMTaskAllotDeptService taskAllotDeptService;

    @Autowired
    IXzyCOneallotConfigService xzyCOneallotConfigService;

    @Autowired
    IXzyCOneallotTemplateService oneallotTemplateService;

    @Autowired
    IChecklistSummaryService checklistSummaryService;

    @Autowired
    private IRepairMidGroundService midGroundService;

    protected static final Logger logger = LoggerFactory.getLogger(XzyMTaskallotpacketServiceImpl.class);

    private final String RepairTaskId = ServiceNameEnum.RepairTaskService.getId();

    //????????????
    private final String midGroundServiceId = CustomServiceNameEnum.MidGroundService.getId();

    @Override
    public List<XzyMTaskallotpacket> getTaskAllotPacketByTaskId(List<String> taskIds) {
        return taskallotpacketMapper.getTaskAllotPacketByTaskId(taskIds);
    }

    @Override
    public int setTaskAllotPacket(XzyMTaskallotpacket taskallotpacket) {
        return taskallotpacketMapper.setTaskAllotPacket(taskallotpacket);
    }


    @Override
    public List<XzyMTaskallotpacket> getTaskAllotPacketById(String packetId) {
        return taskallotpacketMapper.getTaskAllotPacketById(packetId);
    }

    @Override
    public int deleteAll(String packetId) {
        return taskallotpacketMapper.deleteAll(packetId);
    }

    @Override
    public int deletePackets(Map<String, String> map) {
        return taskallotpacketMapper.deletePackets(map);
    }

    @Override
    public List<XzyMTaskallotpacket> getTaskAllotPackets(Map<String, String> map) {
        return taskallotpacketMapper.getTaskAllotPackets(map);
    }

    @Override
    public JSONObject getRepairTask(String unitCode, String dayplanId, String deptCode, String taskAllotType, String mode) {
        JSONObject result = new JSONObject();
        logger.info("---------------------------------------------------------??????????????????---------------------------------------------------------");
        List<ZtTaskPacketEntity> taskPacketEntityList = new ArrayList<>();
        List<XzyMTaskcarpart> xzyMTaskcarpartList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(unitCode)&&!ObjectUtils.isEmpty(dayplanId)&&!ObjectUtils.isEmpty(deptCode)){
            // ??????????????????????????????
            taskPacketEntityList = getTaskPacketEntityList(dayplanId, unitCode, deptCode);
            // ?????? xzy_m_taskcarpart ?????? dayPlanID unitCode deptCode ??????????????????????????????
            xzyMTaskcarpartList = getMTaskcarpartList(dayplanId, unitCode, deptCode);
        }

        //???????????????????????????????????????
        List<XzyBTaskallotshowDict> xzyBTaskallotshowDictList = xzyBTaskallotshowDictService.getShowDictByTaskAllotType("");

        //???????????????????????????
        List<XzyBTaskallottypeDict> xzyBTaskallottypeDictList = MybatisPlusUtils.selectList(
            taskallottypeDictService,
            eqParam(XzyBTaskallottypeDict::getcFlag, "1")
        );

        //???????????????????????????????????????????????????
        List<XzyCOneallotCarConfig> allotCarConfigs = getTrainsetDetialInfo(null, unitCode, deptCode);
        List<XzyCOneallotCarConfig> xzyCOneallotCarConfigList = getOneAllotConfigList(unitCode,deptCode);


        //????????????id?????????????????????????????????
        List<XzyCOneallotTemplate> templateList = getOneAllotTemplates(null);

        // ?????? xzyMTaskcarpartList ??????????????????
        logger.info("1.????????????????????????????????????????????????????????????id??????...");
        List<String> processids = xzyMTaskcarpartList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
        List<String> packetids = xzyMTaskcarpartList.stream().map(t -> t.getTaskAllotPacketId()).distinct().collect(Collectors.toList());
        List<String> deptids = xzyMTaskcarpartList.stream().map(t -> t.getTaskAllotDeptId()).distinct().collect(Collectors.toList());
        logger.info("??????????????????id??????????????????");
        // ??????????????????
        List<XzyMTaskallotdept> depts = new ArrayList<>();
        if(!CollectionUtils.isEmpty(deptids)){
            depts = getTaskAllotDepts(deptids);
        }
        // ??????????????????
        List<XzyMTaskallotperson> persons = new ArrayList<>();
        if(!CollectionUtils.isEmpty(processids)){
            persons = getPersons(processids);
        }
        // ?????????????????????
        List<XzyMTaskallotpacket> packets = new ArrayList<>();
        if(!CollectionUtils.isEmpty(packetids)){
            packets = getTaskAllotPackets(packetids);
        }
        // ????????????????????????
        List<QueryTrainType> queryTrainTypeList = new ArrayList<>();
        logger.info("2.??????????????????????????????????????????????????????????????????????????????...");
        for (XzyMTaskcarpart carpart : xzyMTaskcarpartList) {
            String processid = carpart.getProcessId();
            String deptid = carpart.getTaskAllotDeptId();
            // ?????????
            List<XzyMTaskallotperson> workerList = persons.stream().filter(t -> processid.equals(t.getProcessId())).collect(Collectors.toList());
            carpart.setWorkerList(workerList);
            // ?????????
            List<XzyMTaskallotdept> deptList = depts.stream().filter(t -> deptid.equals(t.getTaskAllotDeptId())).collect(Collectors.toList());
            if (deptList != null && deptList.size() > 0) {
                carpart.setXzyMTaskallotdept(deptList.get(0));
            }
        }
        logger.info("????????????????????????????????????");
        // xzyMTaskcarpartList , taskPacketEntityList ??????????????????
        List<AllotData> AllotDatas = new ArrayList<AllotData>();
        //??????????????????map??????
        Map<String, TrainsetInfo> trainsetInfoMap = new HashMap<>();
        if (taskPacketEntityList != null && taskPacketEntityList.size() > 0) {
            logger.info("3.??????????????????????????????...");
            // ??????????????????????????????
            for (ZtTaskPacketEntity task : taskPacketEntityList) {
                AllotData allotData = new AllotData();
                allotData.setTaskRepairCode(task.getTaskRepairCode());
                allotData.setWorkerList(new ArrayList<XzyMTaskallotperson>());
                allotData.setId(UUID.randomUUID().toString());
                allotData.setTrainsetId(task.getTrainsetId());
                allotData.setTrainsetName(task.getTrainsetName());
                // ??????????????????
                String itemTypeCode = packetTypeChangeToItemType(task.getPacketTypeCode(), task.getTaskRepairCode());
                XzyBTaskallottypeDict worktypeDict = new XzyBTaskallottypeDict();
                if (StringUtils.isNotEmpty(itemTypeCode)) {
                    //?????????????????????
                    List<XzyBTaskallottypeDict> typeDictFilterList = xzyBTaskallottypeDictList.stream().filter(t -> itemTypeCode.equals(t.getsTaskallottypecode())).collect(Collectors.toList());
                    if (typeDictFilterList != null && typeDictFilterList.size() > 0) {
                        worktypeDict = typeDictFilterList.get(0);
                    }
                    //????????????????????????
                    List<XzyBTaskallotshowDict> showDictFilterList = xzyBTaskallotshowDictList.stream().filter(t -> itemTypeCode.equals(t.getItemTypeCode())).collect(Collectors.toList());
                    if (showDictFilterList != null && showDictFilterList.size() > 0) {
                        allotData.setShowMode(showDictFilterList.get(0).getsTaskallotshowcode());
                    }
                }
                allotData.setWorkTypeCode(worktypeDict.getsTaskallottypecode());
                allotData.setWorkTypeName(worktypeDict.getsTaskallottypename());
                allotData.setAllotStateCode("0");
                allotData.setAllotStateName("?????????");
                allotData.setWorkTaskCode(task.getPacketCode());
                allotData.setWorkTaskName(task.getPacketName());
                TrainsetInfo trainsetInfo = null;
                String marshalcount = "";
                if (!trainsetInfoMap.isEmpty() && trainsetInfoMap.get(task.getTrainsetId()) != null) {
                    trainsetInfo = trainsetInfoMap.get(task.getTrainsetId());
                } else {
                    trainsetInfo = getTrainsetDetialInfo(task.getTrainsetId());
                    trainsetInfoMap.put(task.getTrainsetId(), trainsetInfo);
                }
                if (trainsetInfo != null) {
                    marshalcount = trainsetInfo.getIMarshalcount();
                    allotData.setMarshal(marshalcount);
                    allotData.setTrainsetTypeCode(trainsetInfo.getTraintype());
                    allotData.setTrainsetTypeName(trainsetInfo.getTraintype());
                }
                XzyMTaskallotpacket packet = new XzyMTaskallotpacket();
                String packetCode = task.getPacketCode();
                List<XzyMTaskallotpacket> packetlist = packets.stream().filter(t -> packetCode.equals(t.getPacketCode())).collect(Collectors.toList());
                // ?????????id
                String onlyPacketid = null;
                if (packetlist != null && packetlist.size() > 0) {
                    // ???????????????
                    onlyPacketid = packetlist.get(0).getTaskAllotPacketId();
                    packet.setTaskAllotPacketId(onlyPacketid);
                }
                packet.setPacketCode(task.getPacketCode());
                packet.setPacketName(task.getPacketName());
                packet.setStaskId(task.getTaskId());
                packet.setPacketType(task.getPacketTypeCode());
                packet.setMainCyc(task.getTaskRepairCode());

                List<ZtTaskItemEntity> taskItems = task.getLstTaskItemInfo();
                List<XzyMTaskallotItem> taskallotItems = new ArrayList<XzyMTaskallotItem>();
                // ??????????????????
                List<Integer> workersCount = new ArrayList<Integer>();
                // ??????????????????????????????
                if (taskItems != null && taskItems.size() > 0) {
                    // ??????????????????
                    List<ZtTaskItemEntity> oneItems = taskItems.stream().filter(t -> "0".equals(t.getItemType())).collect(Collectors.toList());
                    if (oneItems != null && oneItems.size() > 0) {
                        ZtTaskItemEntity taskItem = oneItems.get(0);
                        List<XzyMTaskallotItem> items = getOne(xzyMTaskcarpartList, task, taskItem, unitCode, onlyPacketid, allotData.getTrainsetTypeName(), marshalcount, xzyCOneallotCarConfigList, templateList);
                        for (XzyMTaskallotItem taskallotItem : items) {
                            List<XzyMTaskcarpart> taskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                            for (XzyMTaskcarpart taskcarpart : taskcarpartList) {
                                if (taskcarpart.getWorkerList() == null || taskcarpart.getWorkerList().size() == 0) {
                                    workersCount.add(0);
                                } else {
                                    int personCount = taskcarpart.getWorkerList().size();
                                    if (!workersCount.contains(1)) {
                                        workersCount.add(1);
                                    }
                                }
                            }
                        }
                        taskallotItems.addAll(items);
                    } else {
                        // ?????? ????????????
                        Map<String, List<ZtTaskItemEntity>> groupItemCode = taskItems.stream().collect(Collectors.groupingBy(ZtTaskItemEntity::getItemCode));
                        for (String itemCode : groupItemCode.keySet()) {
                            List<ZtTaskItemEntity> taskItemsGroup = groupItemCode.get(itemCode);
                            // ??????????????????Code????????? ??????
                            List<String> carNos = taskItemsGroup.stream().map(t -> t.getCarNo()).collect(Collectors.toList());
                            ZtTaskItemEntity itemEnity = taskItemsGroup.get(0);
                            XzyMTaskallotItem taskallotItem = new XzyMTaskallotItem();
                            taskallotItem.setItemCode(itemEnity.getItemCode());
                            taskallotItem.setItemName(itemEnity.getItemName());
                            taskallotItem.setDisplayItemName(itemEnity.getItemName());
                            List<XzyMTaskcarpart> taskcarparts = new ArrayList<XzyMTaskcarpart>();
                            for (ZtTaskItemEntity taskItem : taskItemsGroup) {
                                XzyMTaskcarpart taskcarpart = getXzyMTaskcarpart(task, taskItem, xzyMTaskcarpartList,onlyPacketid, allotData.getTrainsetTypeName());
                                if (taskItem.getItemType().equals("5")) {
                                    taskallotItem.setDisplayItemName("[?????????" + taskcarpart.getCarNo() + "]" + taskcarpart.getItemName());
                                    if (!ObjectUtils.isEmpty(taskcarpart.getCarNo())&&!taskcarpart.getCarNo().equals("??????")) {
                                        taskallotItem.setSort(Integer.parseInt(taskcarpart.getCarNo()));
                                    }
                                }
                                if (taskcarpart.getWorkerList() == null || taskcarpart.getWorkerList().size() == 0) {
                                    workersCount.add(0);
                                } else {
                                    int personCount = taskcarpart.getWorkerList().size();
                                    if (!workersCount.contains(1)) {
                                        workersCount.add(1);
                                    }
                                }
                                taskcarparts.add(taskcarpart);
                            }
                            List<XzyMTaskallotperson> personList = new ArrayList<XzyMTaskallotperson>();
                            List<List<XzyMTaskallotperson>> getworkers = taskcarparts.stream().filter(t -> t.getWorkerList() != null).map(t -> t.getWorkerList()).collect(Collectors.toList());
                            for (List<XzyMTaskallotperson> workers : getworkers) {
                                if (workers != null && workers.size() > 0) {
                                    for (XzyMTaskallotperson person : workers) {
                                        String wokerName = person.getWorkerName();
                                        List<XzyMTaskallotperson> filterlist = personList.stream().filter(t -> wokerName.equals(t.getWorkerName())).collect(Collectors.toList());
                                        if (filterlist == null || filterlist.size() == 0) {
                                            personList.add(person);
                                        }
                                    }
                                }
                            }
                            if (personList.size() > 0) {
                                personList = personList.stream().distinct().collect(Collectors.toList());
                            }
                            taskallotItem.setWorkerList(personList);
                            taskallotItem.setXzyMTaskcarpartList(taskcarparts);
                            taskallotItems.add(taskallotItem);
                        }
                    }

                } else {
                    // ?????? ????????????
                    // ?????????????????????????????????
                    XzyMTaskallotItem taskallotItem = setNullItemForXzyMTaskallotItem(task, xzyMTaskcarpartList, onlyPacketid, allotData.getTrainsetTypeName());
                    List<XzyMTaskcarpart> taskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                    for (XzyMTaskcarpart taskcarpart : taskcarpartList) {
                        if (taskcarpart.getWorkerList() == null || taskcarpart.getWorkerList().size() == 0) {
                            workersCount.add(0);
                        } else {
                            int personCount = taskcarpart.getWorkerList().size();
                            if (!workersCount.contains(1)) {
                                workersCount.add(1);
                            }
                        }
                    }
                    taskallotItem.setDisplayItemName(task.getPacketName());
                    List<XzyMTaskallotperson> personList = new ArrayList<XzyMTaskallotperson>();
                    List<XzyMTaskallotperson> getworkers = taskallotItem.getWorkerList();
                    if (getworkers != null && getworkers.size() > 0) {
                        for (XzyMTaskallotperson person : getworkers) {
                            String wokerName = person.getWorkerName();
                            List<XzyMTaskallotperson> filterlist = personList.stream().filter(t -> wokerName.equals(t.getWorkerName())).collect(Collectors.toList());
                            if (filterlist == null || filterlist.size() == 0) {
                                personList.add(person);
                            }
                        }
                    }
                    if (personList.size() > 0) {
                        personList = personList.stream().distinct().collect(Collectors.toList());
                    }
                    taskallotItem.setWorkerList(personList);
                    taskallotItems.add(taskallotItem);
                }
                //?????????????????????00?????????????????????
                for (XzyMTaskallotItem taskallotItem : taskallotItems) {
                    taskallotItem.setId(UUID.randomUUID().toString());
                    List<XzyMTaskcarpart> taskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                    if(!CollectionUtils.isEmpty(taskcarpartList)){
                        List<XzyMTaskcarpart> filterCarPartList = taskcarpartList.stream().filter(t->"00".equals(t.getCarNo())).collect(Collectors.toList());
                        if(!CollectionUtils.isEmpty(filterCarPartList)){
                            taskcarpartList.removeAll(filterCarPartList);
                            taskcarpartList.addAll(filterCarPartList);
                        }
                    }
                }
                if (packet.getPacketType().equals("5") || packet.getPacketType().equals("1")) {
                    Comparator<XzyMTaskallotItem> byItemName = Comparator.comparing(XzyMTaskallotItem::getItemName);
                    Comparator<XzyMTaskallotItem> byCarNo = Comparator.comparing(XzyMTaskallotItem::getSort);
                    taskallotItems.sort(byItemName.thenComparing(byCarNo));
                }
                packet.setTaskallotItemList(taskallotItems);
                // ??? ????????????
                List<List<XzyMTaskallotperson>> getWorkers = packet.getTaskallotItemList().stream().filter(t -> t.getWorkerList() != null).distinct().map(t -> t.getWorkerList()).collect(Collectors.toList());
                // ??? ????????????
                List<List<XzyMTaskcarpart>> getCarparts = packet.getTaskallotItemList().stream().filter(t -> t.getXzyMTaskcarpartList() != null).map(t -> t.getXzyMTaskcarpartList()).collect(Collectors.toList());
                List<XzyMTaskallotperson> getWorkerList = new ArrayList<XzyMTaskallotperson>();
                List<XzyMTaskcarpart> getCarpartsList = new ArrayList<XzyMTaskcarpart>();
                if (getWorkers != null && getWorkers.size() > 0) {
                    for (List<XzyMTaskallotperson> workers : getWorkers) {
                        getWorkerList.addAll(workers);
                    }
                }
                if (getCarparts != null && getCarparts.size() > 0) {
                    for (List<XzyMTaskcarpart> carparts : getCarparts) {
                        getCarpartsList.addAll(carparts);
                    }
                }

                // ?????????????????????
                List<XzyMTaskallotperson> distinctPesons = new ArrayList<XzyMTaskallotperson>();
                // ?????????
                allotData.setPacket(packet);
                String itemType = packet.getPacketType();
                if (getWorkerList != null && getWorkerList.size() > 0) {
                    for (XzyMTaskallotperson person : getWorkerList) {
                        String workerid = person.getWorkerID();
                        String workername = person.getWorkerName();
                        List<XzyMTaskallotperson> personList = distinctPesons.stream().filter(t -> workerid.equals(t.getWorkerID()) && workername.equals(t.getWorkerName()))
                            .collect(Collectors.toList());
                        if (personList != null && personList.size() == 0) {
                            distinctPesons.add(person);
                        }
                    }

                }
                allotData.setWorkerList(distinctPesons);
                boolean isNull = false;
                boolean isNotNull = false;

                if (workersCount.contains(0)) {
                    isNull = true;
                }
                if (workersCount.contains(1)) {
                    isNotNull = true;
                }

                if (isNotNull && isNull) {
                    allotData.setAllotStateCode("2");
                    allotData.setAllotStateName("????????????");
                } else if (!isNull && isNotNull) {
                    allotData.setAllotStateCode("1");
                    allotData.setAllotStateName("?????????");
                } else {
                    allotData.setAllotStateCode("0");
                    allotData.setAllotStateName("?????????");
                }

                AllotDatas.add(allotData);
            }
            logger.info("??????????????????????????????");
        }
        logger.info("??????????????????????????????...");
        //??????????????????
        Collections.sort(AllotDatas, Comparator.comparing(AllotData::getTrainsetName));

        /** ?????????????????????????????????????????????????????????????????????
        //????????????????????????&??????
        List<QueryTrainType> queryTrainTypes = new ArrayList<>();
        List<String> trainTypeCodes = new ArrayList<>();
        //???????????????????????????queryList??????
        for (QueryTrainType queryTrainType : queryTrainTypeList) {
            List<QueryTrainset> queryTrainsetList = queryTrainType.getQueryTrainsetList();
            //???????????????????????????
            if (queryTrainTypes != null && queryTrainTypes.size() > 0) {
                //???????????????????????????????????????????????????????????????
                if (trainTypeCodes.contains(queryTrainType.getTrainsetTypeCode())) {
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    for (QueryTrainType trainTypeEntity : queryTrainTypes) {
                        if (trainTypeEntity.getTrainsetTypeCode().equals(queryTrainType.getTrainsetTypeCode())) {
                            List<QueryTrainset> queryTrainsets = trainTypeEntity.getQueryTrainsetList();
                            queryTrainsets.addAll(queryTrainsetList);
                        }
                    }
                } else {
                    //???????????????????????????
                    queryTrainTypes.add(queryTrainType);
                    trainTypeCodes.add(queryTrainType.getTrainsetTypeCode());
                }
            } else {
                //????????????????????????
                queryTrainTypes.add(queryTrainType);
                trainTypeCodes.add(queryTrainType.getTrainsetTypeCode());
            }
        }
        for (QueryTrainType trainType : queryTrainTypes) {
            List<QueryTrainset> queryTrainsetList = trainType.getQueryTrainsetList();
            List<QueryTrainset> queryTrainsets = new ArrayList<>();
            List<String> trainsetIds = new ArrayList<>();
            for (QueryTrainset trainset : queryTrainsetList) {
                List<QueryWorkTask> workTaskList = trainset.getQueryWorkTaskList();
                String trainsetId = trainset.getTrainsetId();
                //??????????????????
                if (queryTrainsets != null && queryTrainsets.size() > 0) {
                    //????????????????????????
                    if (trainsetIds.contains(trainsetId)) {
                        //???????????????
                        for (QueryTrainset queryTrainset : queryTrainsets) {
                            //????????????????????????????????????????????????
                            if (queryTrainset.getTrainsetId().equals(trainsetId)) {
                                List<QueryWorkTask> workTasks = queryTrainset.getQueryWorkTaskList();
                                workTasks.addAll(workTaskList);
                            }
                        }
                    } else {
                        queryTrainsets.add(trainset);
                        trainsetIds.add(trainsetId);
                    }
                } else {
                    queryTrainsets.add(trainset);
                    trainsetIds.add(trainsetId);
                }
            }
            trainType.setQueryTrainsetList(queryTrainsets);
            Collections.sort(queryTrainsets, Comparator.comparing(QueryTrainset::getTrainsetName));
        }**/
        logger.info("??????????????????????????????");
        // ??????????????????
        List<AllotTypeDict> allotTypeDicts = getAllotTypeDict();
        //???????????????????????????????????????
        List<AllotData> testData = new ArrayList<>();


        List<QueryTrainType> queryTrainTypes = new ArrayList<>();//????????????
        //??????????????????????????????
        if(!CollectionUtils.isEmpty(AllotDatas)){
            if("2".equals(mode)){
                testData = getDivisionWorkTwo(AllotDatas);
            }else{
                testData = getTwo(mode, AllotDatas);
            }
            //???????????????????????????????????????queryList????????????
            if(!CollectionUtils.isEmpty(testData)){
                for(AllotData allotData:testData){
                    setQuery(allotData, queryTrainTypeList);
                }
                queryTrainTypes = this.getQueryTrainTypes(queryTrainTypeList);
            }
        }
        //????????????????????????????????????????????????????????????????????????????????????
        if(!CollectionUtils.isEmpty(testData)){
            //???????????????????????????????????????????????????????????????
            List<ColumnParam<ChecklistSummary>> columnParamList = new ArrayList<>();
            if(!ObjectUtils.isEmpty(dayplanId)&&!ObjectUtils.isEmpty(unitCode)&&!ObjectUtils.isEmpty(deptCode)){
                columnParamList.add(eqParam(ChecklistSummary::getDayPlanId, dayplanId));
                columnParamList.add(eqParam(ChecklistSummary::getUnitCode, unitCode));
                columnParamList.add(eqParam(ChecklistSummary::getDeptCode, deptCode));
            }
            List<ChecklistSummary> checklistSummaryList = MybatisPlusUtils.selectList(
                checklistSummaryService,
                columnParamList
            );
            if(!CollectionUtils.isEmpty(checklistSummaryList)){
                for(AllotData allotData:testData){
                    XzyMTaskallotpacket packet = allotData.getPacket();
                    if(!ObjectUtils.isEmpty(packet)){
                        List<XzyMTaskallotItem> taskallotItemList = packet.getTaskallotItemList();
                        if(!CollectionUtils.isEmpty(taskallotItemList)){
                            for(XzyMTaskallotItem taskallotItem:taskallotItemList){
                                ChecklistSummary checklistSummary = checklistSummaryList.stream().filter(t -> t.getItemCode().equals(taskallotItem.getItemCode()) && t.getTrainsetId().equals(allotData.getTrainsetId())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(checklistSummary)){
                                    if(!ObjectUtils.isEmpty(checklistSummary.getForemanSignCode())||!ObjectUtils.isEmpty(checklistSummary.getForemanSignName())){
                                        taskallotItem.setFillType("1");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //??????????????????????????????????????????
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(testData, SerializerFeature.DisableCircularReferenceDetect));
        List<AllotData> resData = jsonArray.toJavaList(AllotData.class);
        result.put("data", resData);
        result.put("queryList", queryTrainTypes);
        result.put("allotTypeDict", allotTypeDicts);
        result.put("taskAllotTypeDict", xzyBTaskallottypeDictList);
        result.put("msg", "????????????");
        result.put("code", 1);
        return result;
    }

    //???????????????????????????queryList??????
    public List<QueryTrainType> getQueryTrainTypes(List<QueryTrainType> queryTrainTypeList){
        List<QueryTrainType> queryTrainTypes = new ArrayList<>();
        List<String> trainTypeCodes = new ArrayList<>();
        for (QueryTrainType queryTrainType : queryTrainTypeList) {
            List<QueryTrainset> queryTrainsetList = queryTrainType.getQueryTrainsetList();
            //???????????????????????????
            if (queryTrainTypes != null && queryTrainTypes.size() > 0) {
                //???????????????????????????????????????????????????????????????
                if (trainTypeCodes.contains(queryTrainType.getTrainsetTypeCode())) {
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    for (QueryTrainType trainTypeEntity : queryTrainTypes) {
                        if (trainTypeEntity.getTrainsetTypeCode().equals(queryTrainType.getTrainsetTypeCode())) {
                            List<QueryTrainset> queryTrainsets = trainTypeEntity.getQueryTrainsetList();
                            queryTrainsets.addAll(queryTrainsetList);
                        }
                    }
                } else {
                    //???????????????????????????
                    queryTrainTypes.add(queryTrainType);
                    trainTypeCodes.add(queryTrainType.getTrainsetTypeCode());
                }
            } else {
                //????????????????????????
                queryTrainTypes.add(queryTrainType);
                trainTypeCodes.add(queryTrainType.getTrainsetTypeCode());
            }
        }
        for (QueryTrainType trainType : queryTrainTypes) {
            List<QueryTrainset> queryTrainsetList = trainType.getQueryTrainsetList();
            List<QueryTrainset> queryTrainsets = new ArrayList<>();
            List<String> trainsetIds = new ArrayList<>();
            for (QueryTrainset trainset : queryTrainsetList) {
                List<QueryWorkTask> workTaskList = trainset.getQueryWorkTaskList();
                String trainsetId = trainset.getTrainsetId();
                //??????????????????
                if (queryTrainsets != null && queryTrainsets.size() > 0) {
                    //????????????????????????
                    if (trainsetIds.contains(trainsetId)) {
                        //???????????????
                        for (QueryTrainset queryTrainset : queryTrainsets) {
                            //????????????????????????????????????????????????
                            if (queryTrainset.getTrainsetId().equals(trainsetId)) {
                                List<QueryWorkTask> workTasks = queryTrainset.getQueryWorkTaskList();
                                workTasks.addAll(workTaskList);
                            }
                        }
                    } else {
                        queryTrainsets.add(trainset);
                        trainsetIds.add(trainsetId);
                    }
                } else {
                    queryTrainsets.add(trainset);
                    trainsetIds.add(trainsetId);
                }
            }
            trainType.setQueryTrainsetList(queryTrainsets);
            Collections.sort(queryTrainsets, Comparator.comparing(QueryTrainset::getTrainsetName));
        }
        return queryTrainTypes;
    }

    /***
     * @author:
     * @date: 2022/3/11
     * @desc: ????????????????????????
     */
    @Override
    public JSONObject getQuery(String unitCode, String dayplanId, String deptCode, String taskAllotType, String mode){
        JSONObject result = new JSONObject();
        //1.???????????????????????????
        List<XzyBTaskallottypeDict> xzyBTaskallottypeDictList = MybatisPlusUtils.selectList(
            taskallottypeDictService,
            eqParam(XzyBTaskallottypeDict::getcFlag, "1")
        );
        result.put("taskAllotTypeDict",xzyBTaskallottypeDictList);
        //2.????????????????????????
        List<AllotTypeDict> allotTypeDicts = getAllotTypeDict();
        result.put("allotTypeDict", allotTypeDicts);
        //3.??????????????????????????????
        List<QueryTrainType> queryTrainTypes = new ArrayList<>();
        List<ZtTaskPacketEntity> taskPacketEntityList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(unitCode)&&!ObjectUtils.isEmpty(dayplanId)&&!ObjectUtils.isEmpty(deptCode)){
            // ??????????????????????????????
            taskPacketEntityList = getTaskPacketEntityList(dayplanId, unitCode, deptCode);
            if(!CollectionUtils.isEmpty(taskPacketEntityList)){
                List<QueryTrainType> queryTrainTypeList = new ArrayList<>();
                //?????????????????????????????????
                List<String> trainsetIds = taskPacketEntityList.stream().map(t->t.getTrainsetId()).collect(Collectors.toList());
                List<TrainsetBaseInfo> trainsetBaseInfoList = new ArrayList<>();
                if(!CollectionUtils.isEmpty(trainsetIds)){
                    trainsetBaseInfoList = remoteService.getTrainsetBaseInfoByIds(trainsetIds);
                }
                for(ZtTaskPacketEntity currentPacketEntity:taskPacketEntityList){
                    AllotData allotData = new AllotData();
                    allotData.setTrainsetId(currentPacketEntity.getTrainsetId());
                    allotData.setTrainsetName(currentPacketEntity.getTrainsetName());
                    TrainsetBaseInfo trainsetBaseInfo = Optional.ofNullable(trainsetBaseInfoList).orElseGet(ArrayList::new).stream().filter(t -> currentPacketEntity.getTrainsetId().equals(t.getTrainsetid())).findFirst().orElse(null);
                    if(!ObjectUtils.isEmpty(trainsetBaseInfo)){
                        allotData.setTrainsetTypeCode(trainsetBaseInfo.getTraintype());
                        allotData.setTrainsetTypeName(trainsetBaseInfo.getTraintype());
                    }
                    XzyMTaskallotpacket packet = new XzyMTaskallotpacket();
                    packet.setPacketCode(currentPacketEntity.getPacketCode());
                    packet.setPacketName(currentPacketEntity.getPacketName());
                    packet.setPacketType(currentPacketEntity.getPacketTypeCode());
                    allotData.setPacket(packet);
                    setQuery(allotData, queryTrainTypeList);
                }
                queryTrainTypes = this.getQueryTrainTypes(queryTrainTypeList);
            }
        }
        result.put("queryList",queryTrainTypes);
        return result;
    }

    @Override
    public int setTaskAllotPackets(List<XzyMTaskallotpacket> taskallotpackets) {
        String dayPlanID = null;
        String unitCode = null;
        XzyMTaskallotdept dept = null;
        List<XzyMTaskallotpacket> taskallotpackets1 = new ArrayList<>();
        List<XzyMTaskcarpart> taskcarpartList1 = new ArrayList<>();
        for (XzyMTaskallotpacket taskallotpacket : taskallotpackets) {
            String packid = UUID.randomUUID().toString();
            XzyMTaskallotpacket xzyMTaskallotpacket = new XzyMTaskallotpacket();
            BeanUtils.copyProperties(taskallotpacket, xzyMTaskallotpacket);
            String taskid = taskallotpacket.getStaskId();
            if (taskid == null || taskid.equals("")) {
                xzyMTaskallotpacket.setStaskId(UUID.randomUUID().toString());
            } else {
                xzyMTaskallotpacket.setStaskId(taskid);
            }
            xzyMTaskallotpacket.setTaskAllotPacketId(packid);
            Boolean isHavePerson = false;// ?????? ?????????
            for (XzyMTaskallotItem taskallotItem : taskallotpacket.getTaskallotItemList()) {
                List<XzyMTaskcarpart> taskCarPartList = taskallotItem.getXzyMTaskcarpartList();
                for (XzyMTaskcarpart car : taskCarPartList) {
                    List<XzyMTaskallotperson> workerlist = car.getWorkerList();
                    if (workerlist != null && workerlist.size() > 0) {
                        isHavePerson = true;
                        car.setTaskAllotPacketId(packid);
                        taskcarpartList1.add(car);
                    }
                }
            }
            if (isHavePerson) {
                taskallotpackets1.add(xzyMTaskallotpacket);
            }
        }
        boolean res = taskcarpartService.setTaskAllotData(taskallotpackets1, taskcarpartList1);
        return res?1:0;
    }

    @Override
    public Map setTaskAllot(Map<String, Object> map) {

        Map<String, Object> result = new HashMap<>();
        String str = (String) map.get("data");
        String unitCode = (String) map.get("unitCode");
        String deptCode = (String) map.get("deptCode");
        String mode = (String) map.get("mode");
        List<XzyMTaskallotdept> taskallotdeptList = new ArrayList<>();
        try {
            Map<String, String> param = new HashMap<>();
            param.put("deptCode", deptCode);
            taskallotdeptList = taskAllotDeptService.getTaskAllotDepts(param);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("????????????????????????????????????getDeptByDeptCode???" + deptCode);
        }
        XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
        if (taskallotdeptList != null && taskallotdeptList.size() > 0) {
            taskallotdept = taskallotdeptList.get(0);
        }
        //??????????????????????????????????????????????????????
        List<AllotData> allotDataList = getAllotData(mode, str);

        //??????AllotData??????????????????????????????????????????????????????????????????????????????????????????
        List<AllotData> newAllotDataList = new ArrayList<>();
        if("2".equals(mode)){
            newAllotDataList = this.splitDivisionWorkTwo(allotDataList);
        }else{
            newAllotDataList = allotDataList;
        }

        //????????????????????????????????????????????????+?????????????????????????????????????????????
        if(newAllotDataList!=null&&newAllotDataList.size()>0){
            this.setTaskDataAndStatus(newAllotDataList);
        }

        List<XzyMTaskallotpacket> packets = Optional.ofNullable(newAllotDataList).orElseGet(ArrayList::new).stream().map(t -> t.getPacket()).collect(Collectors.toList());
        for (XzyMTaskallotpacket taskallotpacket : packets) {
            List<XzyMTaskallotItem> taskallotItemList = taskallotpacket.getTaskallotItemList();
            for (XzyMTaskallotItem taskallotItem : taskallotItemList) {
                List<XzyMTaskcarpart> xzyMTaskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                for (XzyMTaskcarpart taskcarpart : xzyMTaskcarpartList) {
                    if (taskcarpart.getXzyMTaskallotdept() == null || ((taskcarpart.getXzyMTaskallotdept() != null && StringUtils.isEmpty(taskcarpart.getXzyMTaskallotdept().getDeptCode())) && StringUtils.isEmpty(taskcarpart.getTaskAllotDeptId()))) {
                        taskcarpart.setTaskAllotDeptId(taskallotdept.getTaskAllotDeptId());
                        taskcarpart.setXzyMTaskallotdept(taskallotdept);
                    }
                }
            }
        }
        Object setPacketRes = new Object();
        int insertFlag = setTaskAllotPackets(packets);
        if(1==insertFlag){
            result.put("msg", "????????????");
            result.put("code", 1);
        }else{
            result.put("msg", "????????????");
            result.put("code", -1);
        }
        return result;
    }

    //????????????????????????????????????????????????
    public List<AllotData> splitDivisionWorkTwo(List<AllotData> allotDataList){
        List<AllotData> newAllotDataList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(allotDataList)){
            //1.??????????????????????????????????????????
            ConfigParamsModel configParamsModel = new ConfigParamsModel();
            configParamsModel.setType("TASKALLOT");
            configParamsModel.setName("AGGPACKETTYPE");
            List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
            XzyCConfig xzyCConfig = Optional.ofNullable(configs).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(xzyCConfig)){
                String packetTypeCode = xzyCConfig.getParamValue();
                //2.?????????????????????????????????
                List<AllotData> otherAllotDataList = allotDataList.stream().filter(t -> !packetTypeCode.equals(t.getWorkTypeCode())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(otherAllotDataList)){
                    newAllotDataList.addAll(otherAllotDataList);
                    allotDataList.removeAll(otherAllotDataList);
                }
                //3.????????????id????????????
                Map<String, List<AllotData>> groupMap = allotDataList.stream().collect(Collectors.groupingBy(AllotData::getTrainsetId));
                if(!CollectionUtils.isEmpty(groupMap)){
                    groupMap.forEach((trainsetId,groupList)->{
                        //4.???????????????item
                        List<XzyMTaskallotItem> totalItemList = groupList.stream().filter(t -> !ObjectUtils.isEmpty(t.getPacket()) && !CollectionUtils.isEmpty(t.getPacket().getTaskallotItemList())).flatMap(v -> v.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                        if(!CollectionUtils.isEmpty(totalItemList)){
                            //5.????????????????????????????????????
                            Map<String,XzyMTaskallotpacket> packetInfoMap = new HashMap<>();
                            for(XzyMTaskallotItem taskallotItem:totalItemList){
                                List<XzyMTaskcarpart> xzyMTaskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                                XzyMTaskcarpart taskcarpart = Optional.ofNullable(xzyMTaskcarpartList).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(taskcarpart)){
                                    String reallyPacketCode = taskcarpart.getPacketCode();
                                    String reallyPacketName = taskcarpart.getPacketName();
                                    String reallyPacketType = taskcarpart.getPacketType();
                                    XzyMTaskallotpacket reallyPacket = new XzyMTaskallotpacket();
                                    reallyPacket.setPacketCode(reallyPacketCode);
                                    reallyPacket.setPacketName(reallyPacketName);
                                    reallyPacket.setPacketType(reallyPacketType);
                                    packetInfoMap.put(reallyPacketCode,reallyPacket);
                                }
                            }
                            //6.??????????????????????????????map??????allotdata??????
                            packetInfoMap.forEach((packetCode,packet)->{
                                AllotData newAllotData = new AllotData();
                                packet.setMainCyc("2");
                                newAllotData.setWorkTaskCode(packet.getPacketCode());
                                newAllotData.setWorkTaskName(packet.getPacketName());
                                List<XzyMTaskallotItem> newTaskAllotItemList = new ArrayList<>();
                                for(XzyMTaskallotItem taskallotItem:totalItemList){
                                    List<XzyMTaskcarpart> xzyMTaskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                                    XzyMTaskcarpart taskcarpart = Optional.ofNullable(xzyMTaskcarpartList).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
                                    if(!ObjectUtils.isEmpty(taskcarpart)&&taskcarpart.getPacketCode().equals(packet.getPacketCode())){
                                        newTaskAllotItemList.add(taskallotItem);
                                    }
                                }
                                packet.setTaskallotItemList(newTaskAllotItemList);
                                newAllotData.setPacket(packet);
                                newAllotDataList.add(newAllotData);
                            });
                        }
                    });
                }
            }
        }
        return newAllotDataList;
    }

    /***
     * @author: ??????
     * @desc: ????????????????????????????????????????????????+?????????????????????????????????????????????
     * @date: 2021/9/10
     * @param: [allotDataList]
     * @return: boolean
     */
    public boolean setTaskDataAndStatus(List<AllotData> allotDataList){
        String unitCode = "";//???????????????
        String deptCode = "";//????????????
        String deptName = "";//????????????
        String dayPlanId = "";//?????????id
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = new ArrayList<>();
        List<RepairPacketStatu> repairPacketStatuList = new ArrayList<>();//???????????????????????????????????????
        for (AllotData allotData : allotDataList) {
            XzyMTaskallotpacket packet = allotData.getPacket();//?????????
            List<XzyMTaskallotItem> taskallotItemList = new ArrayList<>();//???????????????????????????
            String trainSetId = allotData.getTrainsetId();//??????ID
            String trainSetName = allotData.getTrainsetName();//????????????
            if (!ObjectUtils.isEmpty(packet)) {
                taskallotItemList = packet.getTaskallotItemList();
            }
            if(!CollectionUtils.isEmpty(taskallotItemList)){
                ZtTaskPacketEntity ztTaskPacketEntity = new ZtTaskPacketEntity();
                ztTaskPacketEntity.setTaskRepairCode(packet.getMainCyc());
                ztTaskPacketEntity.setTrainsetId(allotData.getTrainsetId());
                ztTaskPacketEntity.setTrainsetName(allotData.getTrainsetName());
                ztTaskPacketEntity.setPacketCode(packet.getPacketCode());
                ztTaskPacketEntity.setPacketName(packet.getPacketName());
                ztTaskPacketEntity.setPacketTypeCode(packet.getPacketType());
                ztTaskPacketEntity.setRemark("????????????");
                ztTaskPacketEntity.setStatus("4");
                ztTaskPacketEntity.setTaskId(packet.getStaskId());
                List<ZtTaskItemEntity> lstTaskItemInfo = new ArrayList<>();
                for(XzyMTaskallotItem taskallotItem : taskallotItemList){
                    List<XzyMTaskcarpart> xzyMTaskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                    if(!CollectionUtils.isEmpty(xzyMTaskcarpartList)){
                        unitCode = xzyMTaskcarpartList.get(0).getUnitCode();
                        deptCode = xzyMTaskcarpartList.get(0).getXzyMTaskallotdept().getDeptCode();
                        deptName=xzyMTaskcarpartList.get(0).getXzyMTaskallotdept().getDeptName();
                        dayPlanId = xzyMTaskcarpartList.get(0).getDayPlanID();
                    }
                    //???????????????/????????????????????????????????????????????????/???????????????????????????
                    if(("5".equals(allotData.getWorkTypeCode())|| "11".equals(allotData.getWorkTypeCode()))&&
                        "1".equals(taskallotItem.getNewItem())&&!CollectionUtils.isEmpty(xzyMTaskcarpartList)){
                        for (XzyMTaskcarpart xzyMTaskcarpart : xzyMTaskcarpartList) {
                            ZtTaskItemEntity ztTaskItemEntity = new ZtTaskItemEntity();
                            ztTaskItemEntity.setTrainsetId(trainSetId);
                            ztTaskItemEntity.setTrainsetName(trainSetName);
                            ztTaskItemEntity.setDayplanId(xzyMTaskcarpart.getDayPlanID());
                            ztTaskPacketEntity.setDayplanId(xzyMTaskcarpart.getDayPlanID());//????????????????????????????????????
                            ztTaskItemEntity.setItemCode(xzyMTaskcarpart.getItemCode());
                            ztTaskItemEntity.setItemName(xzyMTaskcarpart.getItemName());
                            ztTaskItemEntity.setItemType(xzyMTaskcarpart.getRepairType());
                            ztTaskItemEntity.setArrangeType(xzyMTaskcarpart.getArrageType());
                            ztTaskItemEntity.setCarNo(xzyMTaskcarpart.getCarNo());
                            ztTaskItemEntity.setRemark("????????????");
                            ztTaskItemEntity.setDepotCode(unitCode);
                            ztTaskItemEntity.setTaskId(packet.getStaskId());
                            ztTaskItemEntity.setStatus("4");
                            lstTaskItemInfo.add(ztTaskItemEntity);
                        }
                    }else{//???????????????/???????????????????????????????????????????????????????????????????????????????????????
                        RepairPacketStatu repairPacketStatu = new RepairPacketStatu();
                        repairPacketStatu.setSTrainsetid(allotData.getTrainsetId());
                        repairPacketStatu.setSDayplanid(dayPlanId);
                        repairPacketStatu.setSItemcode(taskallotItem.getItemCode());
                        repairPacketStatu.setSDeptcode(unitCode);
                        repairPacketStatu.setSPacketcode(allotData.getPacket().getPacketCode());
                        repairPacketStatu.setCStatue("4");//???????????????
                        repairPacketStatuList.add(repairPacketStatu);
                    }
                }
                ztTaskPacketEntity.setDepotCode(unitCode);
                ztTaskPacketEntity.setRepairDeptCode(deptCode);
                ztTaskPacketEntity.setRepairDeptName(deptName);
                ztTaskPacketEntity.setDayplanId(dayPlanId);
                ztTaskPacketEntity.setLstTaskItemInfo(lstTaskItemInfo);
                ztTaskPacketEntityList.add(ztTaskPacketEntity);
            }
        }
        //??????????????????-????????????????????????????????????
        try {
            //????????????????????????????????????
            if(!CollectionUtils.isEmpty(ztTaskPacketEntityList)){
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t->!CollectionUtils.isEmpty(t.getLstTaskItemInfo())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(ztTaskPacketEntityList)){
                    JSONObject addRes = remoteService.addFaultReCheckPacketList(ztTaskPacketEntityList);
                }
            }
        } catch (Exception ex) {
            logger.error("??????????????????????????????????????????????????????:" + ex.getMessage());
        }
        //??????????????????-????????????????????????
        try {
            if(!CollectionUtils.isEmpty(repairPacketStatuList)){
                JSONObject updateRes = remoteService.updateRepairPacketStatus(repairPacketStatuList);
            }
        } catch (Exception ex) {
            logger.error("??????????????????????????????????????????:" + ex.getMessage());
        }
        return true;
    }


    public List<AllotData> getAllotData(String mode, String str) {
        List<AllotData> allotDataList = JSONArray.parseArray(JSON.toJSONString(JSON.parse(str)), AllotData.class);
        if (mode.equals("2")) {
            List<AllotData> clearList = new ArrayList<>();
            //?????????????????????????????????
            //allotData????????? data?????????
            for (AllotData allotData : allotDataList) {
                if (allotData.getWorkTaskName().equals("??????")) {
                    //?????????id??????
                    for (AllotData data : allotDataList) {
                        if (data.getId().equals(allotData.getFatherId())) {
                            clearList.add(allotData);
                            //xzyMTaskallotItem????????????
                            int i = 0;
                            for (XzyMTaskallotItem xzyMTaskallotItem : allotData.getPacket().getTaskallotItemList()) {
                                //mTaskallotItem?????????
                                for (XzyMTaskallotItem mTaskallotItem : data.getPacket().getTaskallotItemList()) {
                                    if (mTaskallotItem.getId().equals(xzyMTaskallotItem.getFatherId())) {
                                        if (mTaskallotItem.getXzyMTaskcarpartList().size() == 0) {
                                            for (XzyMTaskcarpart xzyMTaskcarpart : xzyMTaskallotItem.getXzyMTaskcarpartList()) {
                                                mTaskallotItem.getXzyMTaskcarpartList().add(xzyMTaskcarpart);
                                                mTaskallotItem.setXzyMTaskcarpartList(mTaskallotItem.getXzyMTaskcarpartList());
                                                data.getPacket().getTaskallotItemList().get(i).setXzyMTaskcarpartList(mTaskallotItem.getXzyMTaskcarpartList());
                                            }
                                        } else {
                                            for (XzyMTaskcarpart xzyMTaskcarpart : mTaskallotItem.getXzyMTaskcarpartList()) {
                                                //?????????????????????
                                                xzyMTaskallotItem.getXzyMTaskcarpartList().add(xzyMTaskcarpart);
                                                mTaskallotItem.setXzyMTaskcarpartList(xzyMTaskallotItem.getXzyMTaskcarpartList());
                                                data.getPacket().getTaskallotItemList().get(i).setXzyMTaskcarpartList(mTaskallotItem.getXzyMTaskcarpartList());
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (AllotData allotData : clearList) {
                allotDataList.remove(allotData);
            }
        }
        return allotDataList;
    }

    // ??????????????????
    public List<XzyMTaskallotItem> getOne(List<XzyMTaskcarpart> xzyMTaskcarpartList, ZtTaskPacketEntity task, ZtTaskItemEntity item, String unitCode, String onlyPacketid, String trainsetType, String marshalcount, List<XzyCOneallotCarConfig> xzyCOneallotCarConfigList, List<XzyCOneallotTemplate> templateList) {
        String dayPlanId = item.getDayplanId();
        String trainsetId = item.getTrainsetId();
        String itemCode = item.getItemCode();
        String carno = item.getCarNo();
        String partName = item.getPositionName();
        String deptCode = task.getRepairDeptCode();
        String deptName = task.getRepairDeptName();
        List<XzyMTaskcarpart> carpartList = new ArrayList<>();
        if (StringUtils.isNotEmpty(carno) && !carno.equals("??????")) {
            carpartList = xzyMTaskcarpartList.stream().filter(t -> dayPlanId.equals(t.getDayPlanID()) && trainsetId.equals(t.getTrainsetId()) && itemCode.equals(t.getItemCode()) && carno.equals(t.getCarNo())).collect(Collectors.toList());
        } else {
            carpartList = xzyMTaskcarpartList.stream().filter(t -> dayPlanId.equals(t.getDayPlanID()) && trainsetId.equals(t.getTrainsetId()) && itemCode.equals(t.getItemCode())).collect(Collectors.toList());
        }
        // ???????????????????????????id???????????????????????????????????????
        int marshalcountInt = Integer.parseInt(marshalcount);
        LinkedList<String> carNos = new LinkedList<String>();
        for (int index = 1; index < marshalcountInt; index++) {
            carNos.add("0" + index);
        }
        carNos.add("00");
        // ???????????????????????????????????????????????????
        if(xzyCOneallotCarConfigList!=null&&xzyCOneallotCarConfigList.size()>0){
            xzyCOneallotCarConfigList = xzyCOneallotCarConfigList.stream().filter(t -> marshalcount.equals(t.getMarshalNum())).collect(Collectors.toList());
            //????????????????????????
            List<XzyCOneallotCarConfig> zdyConfigList = xzyCOneallotCarConfigList.stream().filter(t->StringUtils.isNoneBlank(t.getConfigId())&&!"".equals(t.getConfigId())).collect(Collectors.toList());
            if(zdyConfigList!=null&&zdyConfigList.size()>0){
                xzyCOneallotCarConfigList = zdyConfigList;
            }else{
                xzyCOneallotCarConfigList = xzyCOneallotCarConfigList.stream().filter(t->"1".equals(t.getsDefault())).collect(Collectors.toList());
            }
        }
        if (templateList != null && templateList.size() > 0) {
            //templateList = templateList.stream().filter(t -> "1".equals(t.getsDefault())).collect(Collectors.toList());
        }
        List<XzyMTaskallotItem> taskallotItems = new ArrayList<XzyMTaskallotItem>();
        if (xzyCOneallotCarConfigList != null && xzyCOneallotCarConfigList.size() > 0) {
            Map<String, List<XzyCOneallotCarConfig>> carConfigGroups = xzyCOneallotCarConfigList.stream().collect(Collectors.groupingBy(XzyCOneallotCarConfig::getsTemplateid));
            for (String templateid : carConfigGroups.keySet()) {
                List<XzyCOneallotCarConfig> carConfiglist = carConfigGroups.get(templateid);
                XzyMTaskallotItem taskallotItem = new XzyMTaskallotItem();
                taskallotItem.setItemCode(item.getItemCode());
                taskallotItem.setItemName(item.getItemName());
                List<XzyCOneallotTemplate> templates = templateList.stream().filter(t -> templateid.equals(t.getsTemplateid())).collect(Collectors.toList());
                if (templates != null && templates.size() > 0) {
                    String carlist = templates.get(0).getsCarnolist();
                    taskallotItem.setSort(Integer.valueOf(templates.get(0).getsGroupsort()));
                    taskallotItem.setDisplayItemName(item.getItemName() + "(" + carlist + ")");
                } else {
                    taskallotItem.setDisplayItemName(item.getItemName());
                }
                List<XzyMTaskcarpart> carparts = new ArrayList<XzyMTaskcarpart>();
                // ????????????XzyMTaskcarpart
                for (XzyCOneallotCarConfig carConfig : carConfiglist) {
                    XzyMTaskcarpart carpart = new XzyMTaskcarpart();
                    if (carpartList == null && carpartList.size() == 0) {
                        carpart.setProcessId(null);
                        XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
                        taskallotdept.setDeptCode(deptCode);
                        taskallotdept.setDeptName(deptName);
                        taskallotdept.setTaskAllotDeptId("");
                        carpart.setXzyMTaskallotdept(taskallotdept);
                        carpart.setWorkerList(new ArrayList<XzyMTaskallotperson>());
                    } else {
                        String sCarNo = carConfig.getsCarno();
                        List<XzyMTaskcarpart> queryCarList = carpartList.stream().filter(t -> sCarNo.equals(t.getCarNo())).collect(Collectors.toList());
                        if (queryCarList != null && queryCarList.size() > 0) {
                            XzyMTaskcarpart taskcar = queryCarList.get(0);
                            carpart.setProcessId(taskcar.getProcessId());
                            if (taskcar.getTaskAllotDeptId() == null || taskcar.getXzyMTaskallotdept() == null) {
                                XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
                                taskallotdept.setDeptCode(deptCode);
                                taskallotdept.setDeptName(deptName);
                                taskallotdept.setTaskAllotDeptId("");
                                carpart.setXzyMTaskallotdept(taskallotdept);
                            } else {
                                carpart.setTaskAllotDeptId(taskcar.getTaskAllotDeptId());
                                carpart.setXzyMTaskallotdept(taskcar.getXzyMTaskallotdept());
                            }
                            carpart.setWorkerList(taskcar.getWorkerList());
                        } else {
                            //??????????????????
                            XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
                            taskallotdept.setDeptCode(deptCode);
                            taskallotdept.setDeptName(deptName);
                            taskallotdept.setTaskAllotDeptId("");
                            carpart.setXzyMTaskallotdept(taskallotdept);
                        }
                    }
                    carpart.setPacketCode(task.getPacketCode());
                    carpart.setPacketName(task.getPacketName());
                    carpart.setPacketType(task.getPacketTypeCode());
                    carpart.setDayPlanID(item.getDayplanId());
                    carpart.setUnitCode(item.getDepotCode());
                    carpart.setUnitName("");
                    carpart.setItemCode(item.getItemCode());
                    carpart.setItemName(item.getItemName());
                    carpart.setRepairType(item.getItemType());
                    carpart.setPartName(item.getPositionName());
                    carpart.setArrageType(StringUtils.isEmpty(item.getArrangeType()) ? ProcessArrageTypeEnum.CAR.getValue() : item.getArrangeType());
                    carpart.setDataSource("");
                    carpart.setPartType(item.getKeyPartTypeId());
                    carpart.setPartPosition(item.getPositionCode());
                    carpart.setLocationCode(item.getLocationCode());
                    carpart.setLocationName(item.getLocationName());
                    carpart.setRemark("");
                    carpart.setTaskAllotPacketId(onlyPacketid);
                    carpart.setCarNo(carConfig.getsCarno());
                    carpart.setTrainsetType(trainsetType);
                    carpart.setTaskItemId(item.getTaskItemId());
                    carpart.setMainCyc(task.getTaskRepairCode());
                    carpart.setTrainsetId(task.getTrainsetId());
                    carpart.setTrainsetName(task.getTrainsetName());
                    if (carpart.getArrageType().equals(ProcessArrageTypeEnum.PART.getValue())) {
                        carpart.setDisplayCarPartName(carpart.getPartName());
                        carpart.setPartPosition(item.getLocationCode());
                    } else {
                        carpart.setDisplayCarPartName(carpart.getCarNo());
                    }
                    carparts.add(carpart);
                }
                taskallotItem.setXzyMTaskcarpartList(carparts);
                taskallotItems.add(taskallotItem);
            }
        } else {// ?????????????????????
            XzyMTaskallotItem taskallotItem = new XzyMTaskallotItem();
            taskallotItem.setItemCode(item.getItemCode());
            taskallotItem.setItemName(item.getItemName());
            List<XzyMTaskcarpart> carparts = new ArrayList<XzyMTaskcarpart>();
            for (String carNo : carNos) {
                XzyMTaskcarpart carpart = new XzyMTaskcarpart();
                if (carpartList == null && carpartList.size() == 0) {
                    carpart.setProcessId(null);
                    XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
                    taskallotdept.setDeptCode(deptCode);
                    taskallotdept.setDeptName(deptName);
                    taskallotdept.setTaskAllotDeptId("");
                    carpart.setXzyMTaskallotdept(taskallotdept);
                    carpart.setWorkerList(new ArrayList<>());
                } else {
                    List<XzyMTaskcarpart> queryCarList = carpartList.stream().filter(t -> carNo.equals(t.getCarNo())).collect(Collectors.toList());
                    if (queryCarList != null && queryCarList.size() > 0) {
                        XzyMTaskcarpart taskcar = queryCarList.get(0);
                        carpart.setProcessId(taskcar.getProcessId());
                        if (taskcar.getTaskAllotDeptId() == null || taskcar.getXzyMTaskallotdept() == null) {
                            XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
                            taskallotdept.setDeptCode(deptCode);
                            taskallotdept.setDeptName(deptName);
                            taskallotdept.setTaskAllotDeptId("");
                            carpart.setXzyMTaskallotdept(taskallotdept);
                        } else {
                            carpart.setTaskAllotDeptId(taskcar.getTaskAllotDeptId());
                            carpart.setXzyMTaskallotdept(taskcar.getXzyMTaskallotdept());
                        }
                        carpart.setWorkerList(taskcar.getWorkerList());
                    } else {
                        XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
                        taskallotdept.setDeptCode(deptCode);
                        taskallotdept.setDeptName(deptName);
                        taskallotdept.setTaskAllotDeptId("");
                        carpart.setXzyMTaskallotdept(taskallotdept);
                    }
                }
                carpart.setPacketCode(task.getPacketCode());
                carpart.setPacketName(task.getPacketName());
                carpart.setPacketType(task.getPacketTypeCode());
                carpart.setDayPlanID(item.getDayplanId());
                carpart.setUnitCode(item.getDepotCode());
                carpart.setUnitName("");
                carpart.setItemCode(item.getItemCode());
                carpart.setItemName(item.getItemName());
                carpart.setRepairType(item.getItemType());
                carpart.setPartName(item.getPositionName());
                carpart.setArrageType(StringUtils.isEmpty(item.getArrangeType()) ? ProcessArrageTypeEnum.CAR.getValue() : item.getArrangeType());
                carpart.setDataSource("");
                carpart.setPartType(item.getKeyPartTypeId());
                carpart.setPartPosition(item.getPositionCode());
                carpart.setLocationCode(item.getLocationCode());
                carpart.setLocationName(item.getLocationName());
                carpart.setRemark("");
                carpart.setTaskAllotPacketId(onlyPacketid);
                carpart.setCarNo(carNo);
                carpart.setTrainsetType(trainsetType);
                carpart.setTaskItemId(item.getTaskItemId());
                carpart.setMainCyc(task.getTaskRepairCode());
                carpart.setTrainsetId(task.getTrainsetId());
                carpart.setTrainsetName(task.getTrainsetName());
                if (carpart.getArrageType().equals(ProcessArrageTypeEnum.PART.getValue())) {
                    carpart.setDisplayCarPartName(carpart.getPartName());
                } else {
                    carpart.setDisplayCarPartName(carpart.getCarNo());
                }
                carparts.add(carpart);
            }
            taskallotItem.setXzyMTaskcarpartList(carparts);
            taskallotItems.add(taskallotItem);
        }
        for (XzyMTaskallotItem taskallotItem : taskallotItems) {
            List<XzyMTaskallotperson> personList = new ArrayList<XzyMTaskallotperson>();
            List<List<XzyMTaskallotperson>> getworkers = taskallotItem.getXzyMTaskcarpartList().stream().filter(t -> t.getWorkerList() != null).map(t -> t.getWorkerList()).collect(Collectors.toList());
            for (List<XzyMTaskallotperson> workers : getworkers) {
                if (workers != null && workers.size() > 0) {
                    for (XzyMTaskallotperson person : workers) {
                        String wokerName = person.getWorkerName();
                        List<XzyMTaskallotperson> filterlist = personList.stream().filter(t -> wokerName.equals(t.getWorkerName())).collect(Collectors.toList());
                        if (filterlist == null || filterlist.size() == 0) {
                            personList.add(person);
                        }
                    }
                }
            }
            if (personList.size() > 0) {
                personList = personList.stream().distinct().collect(Collectors.toList());
            }
            taskallotItem.setWorkerList(personList);
        }
        return taskallotItems;
    }

    // ???????????????????????????id???????????????????????????????????????
    public TrainsetInfo getTrainsetDetialInfo(String trainsetID) {
        logger.info("??????????????????????????????????????????...");
        logger.info("?????????getTrainsetDetialInfo????????????trainsetid = " + trainsetID);
        TrainsetInfo trainsetInfo = remoteService.getTrainsetDetialInfo(trainsetID);
        logger.info("??????????????????????????????????????????...");
//        String marshalcount = trainsetInfo.getIMarshalcount();
//        logger.trace("????????????id????????????,????????????????????????"+marshalcount);
        logger.info("????????????????????????");
        return trainsetInfo;
    }

    // ??????????????????
    List<XzyCOneallotTemplate> getOneAllotTemplates(String templateid) {
        Map<String, String> map = new HashMap<>();
        logger.info("??????????????????????????????????????????...");
        map.put("templateid", templateid);
        List<XzyCOneallotTemplate> xzyCOneallotTemplateList = new ArrayList<>();
        try {
            //fs
            xzyCOneallotTemplateList = xzyCOneallotConfigService.getOneAllotTemplates(map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("??????????????????????????????????????????getOneAllotTemplates???" + templateid);
        }
        logger.info("??????????????????????????????????????????" + xzyCOneallotTemplateList.size());
        logger.info("??????????????????????????????");
        return xzyCOneallotTemplateList;
    }

    // ???????????????????????????????????????????????????
    public List<XzyCOneallotCarConfig> getTrainsetDetialInfo(String marshalcount, String unitCode, String deptCode) {
        logger.info("????????????????????????????????????...");
        Map<String, String> map = new HashMap<>();
        map.put("marshalNum", marshalcount);
        map.put("unitCode", unitCode);
        map.put("deptCode", deptCode);
        List<XzyCOneallotCarConfig> xzyCOneallotTemplateList = new ArrayList<>();
        try {
            xzyCOneallotTemplateList = xzyCOneallotConfigService.getOneAllotConfigs(map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("????????????????????????????????????getOneAllotConfigs" + unitCode + "," + deptCode + "," + marshalcount);
        }
        logger.info("??????????????????????????????" + xzyCOneallotTemplateList.size());
        logger.info("????????????????????????");
        return xzyCOneallotTemplateList;
    }

    //?????????????????????????????????
    public List<XzyCOneallotCarConfig> getOneAllotConfigList(String unitCode, String deptCode){
        List<XzyCOneallotCarConfig> xzyCOneallotCarConfigList = new ArrayList<>();
        try {
            xzyCOneallotCarConfigList = xzyCOneallotConfigService.getOneAllotConfigList(unitCode,deptCode);
        }catch (Exception ex){
            logger.error(ex.getMessage());
        }
        return xzyCOneallotCarConfigList;
    }


    // ??????????????????
    public List<AllotTypeDict> getAllotTypeDict() {
        List<AllotTypeDict> allotTypeDictList = new ArrayList<>();
        AllotTypeDict allotTypeDict = new AllotTypeDict();
        allotTypeDict.setCode("0");
        allotTypeDict.setName("?????????");
        allotTypeDictList.add(allotTypeDict);
        AllotTypeDict allotTypeDict3 = new AllotTypeDict();
        allotTypeDict3.setCode("3");
        allotTypeDict3.setName("?????????");
        allotTypeDictList.add(allotTypeDict3);
        AllotTypeDict allotTypeDict2 = new AllotTypeDict();
        allotTypeDict2.setCode("2");
        allotTypeDict2.setName("????????????");
        allotTypeDictList.add(allotTypeDict2);
        AllotTypeDict allotTypeDict1 = new AllotTypeDict();
        allotTypeDict1.setCode("1");
        allotTypeDict1.setName("?????????");
        allotTypeDictList.add(allotTypeDict1);
        return allotTypeDictList;
    }

    public List<ZtTaskPacketEntity> getTaskPacketEntityList(String dayplanId, String unitCode, String deptCode) {
        List<AllotData> allotDataList = new ArrayList<>();
        logger.info("????????????????????????????????????...");
        MultiValueMap<String, String> querypacketMap = new LinkedMultiValueMap<>();
        Map<String, String> packetMap = new HashMap<>();
        packetMap.put("dayPlanId", dayplanId);
        packetMap.put("lstPacketTypeCode", null);
        packetMap.put("repairDeptCode", deptCode);
        packetMap.put("deptCode", unitCode);
        packetMap.put("trainsetId", null);
        querypacketMap.setAll(packetMap);
        List<JSONObject> jsonObjects = new ArrayList<>();
        logger.info("?????????getPacketTaskByCondition????????????" + querypacketMap);
        try {
            jsonObjects = new RestRequestKitUseLogger<List<JSONObject>>(RepairTaskId, logger) {
            }.postObjectByForm("/Task/getPacketTaskByCondition", querypacketMap);
        } catch (Exception e) {
            logger.info("????????????????????????????????????", e);
        }
        List<ZtTaskPacketEntity> taskPacketEntityList = JSONArray.parseArray(jsonObjects.toString(),
            ZtTaskPacketEntity.class);
        logger.trace("??????????????????????????????????????????????????????" + jsonObjects);
        logger.info("??????????????????");
        return taskPacketEntityList;
    }

    // ?????? xzy_m_taskcarpart ?????? dayPlanID unitCode deptCode ??????????????????????????????
    public List<XzyMTaskcarpart> getMTaskcarpartList(String dayplanId, String unitCode, String deptCode) {
        logger.info("??????????????????????????????????????????...");
        Map<String, String> queryCarPartMap = new HashMap<>();
        queryCarPartMap.put("dayPlanID", dayplanId);
        queryCarPartMap.put("workTeamID", deptCode);
        queryCarPartMap.put("unitCode", unitCode);
        List<XzyMTaskcarpart> xzyMTaskcarpartList = taskcarpartService.getCarPartLists(queryCarPartMap);
        logger.info("????????????????????????????????????" + xzyMTaskcarpartList.size());
        logger.info("??????????????????????????????");
        return xzyMTaskcarpartList;
    }

    // ????????????????????????????????????
    public List<XzyMTaskallotdept> getTaskAllotDepts(List<String> deptids) {
        logger.info("????????????????????????????????????...");
        String selectStr = "('" + String.join("','", deptids) + "')";
        Map<String, String> queryCarPartMap = new HashMap<>();
        queryCarPartMap.put("deptids", selectStr);
        List<XzyMTaskallotdept> taskallotdeptList = new ArrayList<>();
        try {
            taskallotdeptList = taskAllotDeptService.getTaskAllotDepts(queryCarPartMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("????????????????????????????????????getTaskAllotDepts???" + selectStr);
        }
        logger.info("??????????????????????????????" + taskallotdeptList.size());
        logger.info("????????????????????????");
        return taskallotdeptList;
    }


    /***
     * @author: ????????????????????????????????????????????????????????????????????????????????????
     * @date: 2022/3/7
     * @param: [allotDatas???????????????]
     */
    public List<AllotData> getDivisionWorkTwo(List<AllotData> allotDataList) {
        logger.info("getDivisionWorkTwo?????????????????????????????????????????????");
        List<AllotData> allotDataListNew = new ArrayList<>();
        //1.??????????????????????????????????????????
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("TASKALLOT");
        configParamsModel.setName("AGGPACKETTYPE");
        List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
        XzyCConfig xzyCConfig = Optional.ofNullable(configs).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
        //2.???????????????????????????????????????????????????????????????????????????
        if(!CollectionUtils.isEmpty(allotDataList)){
            //2.1???????????????????????????????????????
            List<AllotData> notTwoAllotDataList = allotDataList.stream().filter(t -> !"2".equals(t.getTaskRepairCode())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(notTwoAllotDataList)){
                //2.2??????????????????????????????????????????????????????
                allotDataListNew.addAll(notTwoAllotDataList);
                //2.3???????????????????????????????????????
                allotDataList.removeAll(notTwoAllotDataList);
            }
            //3.?????????????????????????????????id????????????
            Map<String, List<AllotData>> groupMap = allotDataList.stream().collect(Collectors.groupingBy(AllotData::getTrainsetId));
            if(!CollectionUtils.isEmpty(groupMap)){
                //4.???????????????????????????id????????????????????????
                groupMap.forEach((trainsetId, groupList) -> {
                    if(!ObjectUtils.isEmpty(xzyCConfig)){
                        //4.1?????????????????????????????????
                        String aggPacketType = xzyCConfig.getParamValue();
                        //4.2?????????????????????????????????????????????????????????????????????
                        AllotData allotData = groupList.stream().findFirst().orElse(null);
                        XzyMTaskallotpacket packet = allotData.getPacket();
                        //4.3????????????id????????????????????????????????????????????????
                        String trainsetType = "";
                        String trainsetBatch = "";
                        if(!ObjectUtils.isEmpty(allotData)){
                            //4.4????????????????????????????????????????????????
                            TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
                            if(!ObjectUtils.isEmpty(trainsetBaseInfo)){
                                trainsetType = trainsetBaseInfo.getTraintype();
                                trainsetBatch = trainsetBaseInfo.getTraintempid();
                            }
                            //4.5??????????????????????????????????????????????????????????????????????????????????????????????????????
                            if(!ObjectUtils.isEmpty(trainsetType)&&!ObjectUtils.isEmpty(trainsetBatch)){
                                AggItemConfigModel aggItemConfig = new AggItemConfigModel();
                                aggItemConfig.setTrainType(trainsetType);
                                aggItemConfig.setStageId(trainsetBatch);
                                List<AggItemConfigModel> aggConfigList = iAggItemConfigService.getAggItemConfigList(aggItemConfig);
                                //5.??????????????????????????????????????????????????????????????????????????????????????????
                                if(!CollectionUtils.isEmpty(aggConfigList)){
                                    //5.1?????????????????????????????????????????????
                                    List<XzyMTaskallotItem> totalItemList = groupList.stream().filter(v->!ObjectUtils.isEmpty(v.getPacket())&&!CollectionUtils.isEmpty(v.getPacket().getTaskallotItemList())).flatMap(t -> t.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                                    if(!CollectionUtils.isEmpty(totalItemList)){
                                        //5.2?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                        for(AggItemConfigModel aggConfigModel:aggConfigList){
                                            //5.3???????????????????????????
                                            String cars = aggConfigModel.getAggPacketCar();
                                            List<String> carList = new ArrayList<>();
                                            if(!ObjectUtils.isEmpty(cars)){
                                                String[] carArray = cars.split(",");
                                                carList = CollectionUtils.arrayToList(carArray);
                                            }
                                            //5.4???????????????????????????????????????
                                            List<AggItemConfigItem> aggConfigItemList = aggConfigModel.getAggItemConfigItems();
                                            //5.5?????????????????????????????????
                                            String aggPacketCode = aggConfigModel.getAggPacketCode();//?????????????????????
                                            String aggPacketName = aggConfigModel.getAggPacketName();//?????????????????????
                                            if(!CollectionUtils.isEmpty(aggConfigItemList)&&!CollectionUtils.isEmpty(carList)){
                                                //5.6????????????????????????
                                                AllotData divisionWorkData = new AllotData();
                                                BeanUtils.copyProperties(allotData,divisionWorkData);
                                                XzyMTaskallotpacket divisionWorkPacket = new XzyMTaskallotpacket();
                                                divisionWorkPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                                divisionWorkPacket.setStaskId(packet.getStaskId());
                                                divisionWorkPacket.setMainCyc(packet.getMainCyc());
                                                divisionWorkPacket.setPacketCode(aggPacketCode);
                                                divisionWorkPacket.setPacketName(aggPacketName);
                                                divisionWorkPacket.setPacketType(aggPacketType);
                                                divisionWorkData.setId(UUID.randomUUID().toString());
                                                divisionWorkData.setPacket(divisionWorkPacket);
                                                divisionWorkData.setWorkTaskCode(aggPacketCode);
                                                divisionWorkData.setWorkTaskName(aggPacketName);
                                                divisionWorkData.setWorkTypeCode(aggPacketType);
                                                List<XzyMTaskallotItem> divisionWorkItemList = new ArrayList<>();//??????????????????????????????
                                                //5.7 ?????????????????????
                                                Integer exitNum = 0;//???????????????????????????????????????
                                                if(!CollectionUtils.isEmpty(totalItemList)){
                                                    Iterator<XzyMTaskallotItem> itemIterator = totalItemList.iterator();
                                                    while (itemIterator.hasNext()){
                                                        XzyMTaskallotItem allotItem = itemIterator.next();
                                                        String itemCode = allotItem.getItemCode();
                                                        String itemName = allotItem.getItemName();
                                                        boolean exitDivision = aggConfigItemList.stream().anyMatch(t -> itemCode.equals(t.getItemCode()) && itemName.equals(t.getItemName()));
                                                        //5.8?????????????????????????????????????????????????????????????????????????????????????????????????????????
                                                        if(exitDivision){
                                                            exitNum++;
                                                            XzyMTaskallotItem divisionWorkItem = new XzyMTaskallotItem();
                                                            BeanUtils.copyProperties(allotItem,divisionWorkItem);
                                                            List<XzyMTaskcarpart> itemTotalCarpartList =  divisionWorkItem.getXzyMTaskcarpartList();
                                                            List<XzyMTaskcarpart> divisionCarpartList = new ArrayList<>();
                                                            if(!CollectionUtils.isEmpty(itemTotalCarpartList)){
                                                                Iterator<XzyMTaskcarpart> iterator = itemTotalCarpartList.iterator();
                                                                while (iterator.hasNext()){
                                                                    XzyMTaskcarpart taskcarpart = iterator.next();
                                                                    if(!ObjectUtils.isEmpty(taskcarpart)){
                                                                        String carNo = taskcarpart.getCarNo();
                                                                        if(carList.contains(carNo)){
                                                                            taskcarpart.setTaskItemId(UUID.randomUUID().toString());
                                                                            divisionCarpartList.add(taskcarpart);
                                                                            //5.9??????????????????????????????????????????????????????????????????????????????
                                                                            iterator.remove();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            divisionWorkItem.setXzyMTaskcarpartList(divisionCarpartList);
                                                            divisionWorkItem.setId(UUID.randomUUID().toString());
                                                            //5.10???????????????????????????????????????
                                                            List<XzyMTaskallotperson> divisionItemWorkerList = divisionCarpartList.stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                            List<XzyMTaskallotperson> distinctDivisionItemWorkerList = CommonUtils.getDistinctList(divisionItemWorkerList, XzyMTaskallotperson::getWorkerID);
                                                            divisionWorkItem.setWorkerList(distinctDivisionItemWorkerList);
                                                            divisionWorkItemList.add(divisionWorkItem);
                                                            //5.11????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                                            if(CollectionUtils.isEmpty(allotItem.getXzyMTaskcarpartList())){
                                                                itemIterator.remove();
                                                            }
                                                        }
                                                    }
                                                    //5.12?????????????????????????????????????????????????????????
                                                    divisionWorkData.getPacket().setTaskallotItemList(divisionWorkItemList);
                                                    List<XzyMTaskallotperson> divisionWrokerList = divisionWorkData.getPacket().getTaskallotItemList().stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                    List<XzyMTaskallotperson> distinctDivisionWorkerList = CommonUtils.getDistinctList(divisionWrokerList, XzyMTaskallotperson::getWorkerID);
                                                    divisionWorkData.setWorkerList(distinctDivisionWorkerList);
                                                    String allotStateCode = this.getAllotStateCode(divisionWorkItemList);
                                                    divisionWorkData.setAllotStateCode(allotStateCode);
                                                    if("0".equals(allotStateCode)){
                                                        divisionWorkData.setAllotStateName("?????????");
                                                    }else if("1".equals(allotStateCode)){
                                                        divisionWorkData.setAllotStateName("?????????");
                                                    }else{
                                                        divisionWorkData.setAllotStateName("????????????");
                                                    }
                                                    if(exitNum>0){//???????????????????????????????????????????????????????????????????????????1??????????????????????????????????????????
                                                        allotDataListNew.add(divisionWorkData);
                                                    }
                                                }
                                            }
                                        }
                                        //6.??????"??????"???
                                        if(!CollectionUtils.isEmpty(totalItemList)){
                                            //6.1???????????????????????????????????????"????????????"????????????????????????????????????????????????"??????"???????????????????????????????????????????????????
                                            AllotData otherData = new AllotData();
                                            BeanUtils.copyProperties(allotData,otherData);
                                            XzyMTaskallotpacket otherPacket = new XzyMTaskallotpacket();
                                            otherPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                            otherPacket.setStaskId(packet.getStaskId());
                                            otherPacket.setMainCyc(packet.getMainCyc());
                                            //6.2??????????????????????????????????????????"??????"???????????????
                                            List<String> packetCodeList = aggConfigList.stream().map(t -> t.getAggPacketCode()).collect(Collectors.toList());
                                            if(!CollectionUtils.isEmpty(packetCodeList)){
                                                otherPacket.setPacketCode(String.join("_",packetCodeList));
                                            }
                                            otherPacket.setPacketName("??????");
                                            otherPacket.setPacketType(aggPacketType);
                                            otherPacket.setTaskallotItemList(null);
                                            otherData.setId(UUID.randomUUID().toString());
                                            otherData.setPacket(otherPacket);
                                            otherData.setWorkTaskName("??????");
                                            otherData.setWorkTypeCode(aggPacketType);
                                            //6.3??????"??????"???????????????????????????????????????
                                            for(XzyMTaskallotItem otherItem :totalItemList){
                                                List<XzyMTaskcarpart> otherCarpartList = otherItem.getXzyMTaskcarpartList();
                                                if(!CollectionUtils.isEmpty(otherCarpartList)){
                                                    List<XzyMTaskallotperson> otherItemWorkerList = otherCarpartList.stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                    List<XzyMTaskallotperson> distinctOtherItemWorkerList = CommonUtils.getDistinctList(otherItemWorkerList, XzyMTaskallotperson::getWorkerID);
                                                    otherItem.setWorkerList(distinctOtherItemWorkerList);
                                                }
                                            }
                                            otherData.getPacket().setTaskallotItemList(totalItemList);
                                            //6.4???????????????"??????"????????????????????????????????????
                                            List<XzyMTaskallotperson> otherWorkerList = totalItemList.stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                            List<XzyMTaskallotperson> distinctOtherWorkerList = CommonUtils.getDistinctList(otherWorkerList, XzyMTaskallotperson::getWorkerID);
                                            otherData.setWorkerList(distinctOtherWorkerList);
                                            //6.5????????????"??????"??????????????????
                                            String allotStateCode = this.getAllotStateCode(totalItemList);
                                            otherData.setAllotStateCode(allotStateCode);
                                            if("0".equals(allotStateCode)){
                                                otherData.setAllotStateName("?????????");
                                            }else if("1".equals(allotStateCode)){
                                                otherData.setAllotStateName("?????????");
                                            }else{
                                                otherData.setAllotStateName("????????????");
                                            }
                                            if(!CollectionUtils.isEmpty(totalItemList)){//??????'??????'???????????????????????????????????????'??????'????????????????????????
                                                allotDataListNew.add(otherData);
                                            }
                                        }
                                    }else{
                                        logger.info("getDivisionWorkTwo?????????????????????????????????????????????????????????????????????????????????????????????");
                                        //????????????????????????????????????????????????????????????????????????????????????
                                        allotDataListNew.addAll(groupList);
                                    }
                                    /** ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                    AggItemConfigModel aggConfigModel = aggConfigList.get(0);
                                    if(!ObjectUtils.isEmpty(aggConfigModel)){
                                        //3.3???????????????????????????
                                        String cars = aggConfigModel.getAggPacketCar();
                                        List<String> carList = new ArrayList<>();
                                        if(!ObjectUtils.isEmpty(cars)){
                                            String[] carArray = cars.split(",");
                                            carList = CollectionUtils.arrayToList(carArray);
                                        }
                                        //3.3???????????????????????????
                                        List<AggItemConfigItem> aggConfigItemList = aggConfigModel.getAggItemConfigItems();
                                        //3.4???????????????????????????????????????
                                        String aggPacketCode = aggConfigModel.getAggPacketCode();//?????????????????????
                                        String aggPacketName = aggConfigModel.getAggPacketName();//?????????????????????
                                        String aggPacketType = xzyCConfig.getParamValue();
                                        if(!CollectionUtils.isEmpty(aggConfigItemList)&&!CollectionUtils.isEmpty(carList)){
                                            //4.?????????????????????????????????
                                            //4.1??????"????????????"??????,????????????????????????????????????
                                            AllotData divisionWorkData = new AllotData();
                                            BeanUtils.copyProperties(allotData,divisionWorkData);
                                            XzyMTaskallotpacket divisionWorkPacket = new XzyMTaskallotpacket();
                                            divisionWorkPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                            divisionWorkPacket.setStaskId(packet.getStaskId());
                                            divisionWorkPacket.setMainCyc(packet.getMainCyc());
                                            divisionWorkPacket.setPacketCode(aggPacketCode);
                                            divisionWorkPacket.setPacketName(aggPacketName);
                                            divisionWorkPacket.setPacketType(aggPacketType);
                                            divisionWorkData.setId(UUID.randomUUID().toString());
                                            divisionWorkData.setPacket(divisionWorkPacket);
                                            divisionWorkData.setWorkTaskCode(aggPacketCode);
                                            divisionWorkData.setWorkTaskName(aggPacketName);
                                            divisionWorkData.setWorkTypeCode(aggPacketType);
                                            //4.2"??????"??????,????????????????????????????????????
                                            AllotData otherData = new AllotData();
                                            BeanUtils.copyProperties(allotData,otherData);
                                            XzyMTaskallotpacket otherPacket = new XzyMTaskallotpacket();
                                            otherPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                            otherPacket.setStaskId(packet.getStaskId());
                                            otherPacket.setMainCyc(packet.getMainCyc());
                                            otherPacket.setPacketCode(aggPacketCode);
                                            otherPacket.setPacketName("??????");
                                            otherPacket.setPacketType(aggPacketType);
                                            otherPacket.setTaskallotItemList(null);
                                            otherData.setId(UUID.randomUUID().toString());
                                            otherData.setPacket(otherPacket);
                                            otherData.setWorkTaskName("??????");
                                            otherData.setWorkTypeCode(aggPacketType);
                                            //4.3??????"????????????"???"??????"????????????
                                            List<XzyMTaskallotItem> divisionWorkItemList = new ArrayList<>();
                                            List<XzyMTaskallotItem> otherItemList = new ArrayList<>();
                                            //4.4?????????????????????????????????????????????
                                            List<XzyMTaskallotItem> totalItemList = groupList.stream().filter(v->!ObjectUtils.isEmpty(v.getPacket())&&!CollectionUtils.isEmpty(v.getPacket().getTaskallotItemList())).flatMap(t -> t.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                                            if(!CollectionUtils.isEmpty(totalItemList)){
                                                //4.5?????????????????????
                                                Integer exitNum = 0;//???????????????????????????????????????
                                                for(XzyMTaskallotItem allotItem:totalItemList){
                                                    String itemCode = allotItem.getItemCode();
                                                    String itemName = allotItem.getItemName();
                                                    boolean exitDivision = aggConfigItemList.stream().anyMatch(t -> itemCode.equals(t.getItemCode()) && itemName.equals(t.getItemName()));
                                                    //4.6?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                                    if(exitDivision){
                                                        exitNum++;
                                                        XzyMTaskallotItem divisionWorkItem = new XzyMTaskallotItem();
                                                        BeanUtils.copyProperties(allotItem,divisionWorkItem);
                                                        List<XzyMTaskcarpart> divisionCarpartList = divisionWorkItem.getXzyMTaskcarpartList();
                                                        List<XzyMTaskcarpart> otherCarpartList = new ArrayList<>();
                                                        if(!CollectionUtils.isEmpty(divisionCarpartList)){
                                                            Iterator<XzyMTaskcarpart> iterator = divisionCarpartList.iterator();
                                                            while (iterator.hasNext()){
                                                                XzyMTaskcarpart taskcarpart = iterator.next();
                                                                if(!ObjectUtils.isEmpty(taskcarpart)){
                                                                    String carNo = taskcarpart.getCarNo();
                                                                    if(!carList.contains(carNo)){
                                                                        otherCarpartList.add(taskcarpart);
                                                                        iterator.remove();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        divisionWorkItem.setXzyMTaskcarpartList(divisionCarpartList);
                                                        divisionWorkItemList.add(divisionWorkItem);
                                                        //4.7??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                                        if(!CollectionUtils.isEmpty(otherCarpartList)){
                                                            XzyMTaskallotItem otherItem = new XzyMTaskallotItem();
                                                            BeanUtils.copyProperties(allotItem,otherItem);
                                                            otherItem.setXzyMTaskcarpartList(otherCarpartList);
                                                            otherItemList.add(otherItem);
                                                        }
                                                    }else{//???????????????????????????????????????????????????????????????????????????????????????"??????"???????????????
                                                        otherItemList.add(allotItem);
                                                    }
                                                }
                                                divisionWorkData.getPacket().setTaskallotItemList(divisionWorkItemList);
                                                List<XzyMTaskallotperson> divisionWrokerList = divisionWorkData.getPacket().getTaskallotItemList().stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                if(!CollectionUtils.isEmpty(divisionWrokerList)){
                                                    List<XzyMTaskallotperson> distinctDivisionWorkerList = CommonUtils.getDistinctList(divisionWrokerList, XzyMTaskallotperson::getWorkerID);
                                                    divisionWorkData.setWorkerList(distinctDivisionWorkerList);
                                                }
                                                otherData.getPacket().setTaskallotItemList(otherItemList);
                                                List<XzyMTaskallotperson> otherWorkerList = otherData.getPacket().getTaskallotItemList().stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                if(!CollectionUtils.isEmpty(otherWorkerList)){
                                                    List<XzyMTaskallotperson> distinctOtherWorkerList = CommonUtils.getDistinctList(otherWorkerList, XzyMTaskallotperson::getWorkerID);
                                                    otherData.setWorkerList(distinctOtherWorkerList);
                                                }
                                                if(exitNum>0){//???????????????????????????????????????????????????????????????????????????1??????????????????????????????????????????
                                                    allotDataListNew.add(divisionWorkData);
                                                }
                                                if(!CollectionUtils.isEmpty(otherItemList)){//??????'??????'???????????????????????????????????????'??????'????????????????????????
                                                    allotDataListNew.add(otherData);
                                                }
                                            }else{
                                                logger.info("getDivisionWorkTwo?????????????????????????????????????????????????????????????????????????????????????????????");
                                                //????????????????????????????????????????????????????????????????????????????????????
                                                allotDataListNew.addAll(groupList);
                                            }
                                        }else{
                                            logger.info("getDivisionWorkTwo?????????????????????????????????????????????????????????????????????????????????????????????");
                                            //????????????????????????????????????????????????????????????????????????????????????
                                            allotDataListNew.addAll(groupList);
                                        }
                                    }else{
                                        logger.info("???????????????????????????????????????????????????????????????????????????");
                                        //????????????????????????????????????????????????????????????????????????????????????
                                        allotDataListNew.addAll(groupList);
                                    }**/
                                }else{
                                    logger.info("getDivisionWorkTwo??????????????????????????????????????????????????????????????????????????????????????????");
                                    //??????????????????????????????????????????????????????"??????"??????
                                    AllotData otherData = new AllotData();
                                    BeanUtils.copyProperties(allotData,otherData);
                                    XzyMTaskallotpacket otherPacket = new XzyMTaskallotpacket();
                                    otherPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                    otherPacket.setStaskId(packet.getStaskId());
                                    otherPacket.setMainCyc(packet.getMainCyc());
                                    otherPacket.setPacketCode("zyfg");
                                    otherPacket.setPacketName("??????");
                                    otherPacket.setPacketType(xzyCConfig.getParamValue());
                                    otherPacket.setTaskallotItemList(null);
                                    otherData.setId(UUID.randomUUID().toString());
                                    otherData.setPacket(otherPacket);
                                    otherData.setWorkTaskName("??????");
                                    otherData.setWorkTypeCode(xzyCConfig.getParamValue());
                                    List<XzyMTaskallotItem> otherAllItem = Optional.ofNullable(groupList).orElseGet(ArrayList::new).stream().flatMap(t -> t.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                                    otherPacket.setTaskallotItemList(otherAllItem);
                                    otherData.setPacket(otherPacket);
                                    List<XzyMTaskallotperson> otherWorkerList = otherData.getPacket().getTaskallotItemList().stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                    List<XzyMTaskallotperson> distinctOtherWorkerList = CommonUtils.getDistinctList(otherWorkerList, XzyMTaskallotperson::getWorkerID);
                                    otherData.setWorkerList(distinctOtherWorkerList);
                                    //6.5????????????"??????"??????????????????
                                    String allotStateCode = this.getAllotStateCode(otherAllItem);
                                    otherData.setAllotStateCode(allotStateCode);
                                    if("0".equals(allotStateCode)){
                                        otherData.setAllotStateName("?????????");
                                    }else if("1".equals(allotStateCode)){
                                        otherData.setAllotStateName("?????????");
                                    }else{
                                        otherData.setAllotStateName("????????????");
                                    }
                                    allotDataListNew.add(otherData);
                                }
                            }else{
                                logger.info("getDivisionWorkTwo??????????????????????????????????????????????????????");
                                //????????????????????????????????????????????????????????????????????????????????????
                                allotDataListNew.addAll(groupList);
                            }
                        }else {
                            logger.info("getDivisionWorkTwo?????????????????????????????????????????????????????????????????????");
                            //????????????????????????????????????????????????????????????????????????????????????
                            allotDataListNew.addAll(groupList);
                        }
                    }else{
                        logger.info("getDivisionWorkTwo????????????xzy_c_config?????????????????????????????????????????????");
                        //????????????????????????????????????????????????????????????????????????????????????
                        allotDataListNew.addAll(groupList);
                    }
                });
            }
        }
        return allotDataListNew;
    }

    public String getAllotStateCode(List<XzyMTaskallotItem> divisionWorkItemList){
        String result = "0";
        if(!CollectionUtils.isEmpty(divisionWorkItemList)){
            //5.13?????????????????????????????????
            boolean allMatch = divisionWorkItemList.stream().allMatch(t -> CollectionUtils.isEmpty(t.getWorkerList()));
            if(allMatch){
                result = "0";
            }else{
                boolean anyMatch = divisionWorkItemList.stream().anyMatch(t -> CollectionUtils.isEmpty(t.getWorkerList()));
                if(anyMatch){
                    result = "2";
                }else{
                    result = "1";
                }
            }
        }
        return result;
    }

    public List<AllotData> getTwo(String mode, List<AllotData> allotDatas) {
        List<AllotData> allotDataNew = new ArrayList<>();
        if (mode.equals("2")) {
            Map<String, List<AllotData>> map = allotDatas.stream().filter(allotData -> "2".equals(allotData.getTaskRepairCode())).collect(Collectors.groupingBy(AllotData::getTrainsetId));
            map.forEach((trainsetId, allotDataList) -> {
                AllotData allotData = allotDataList.stream().findFirst().orElse(null);
                //?????????????????????????????????
                TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);

                AggItemConfigModel aggItemConfig = new AggItemConfigModel();
                aggItemConfig.setTrainType(trainsetBaseInfo.getTraintype());
                aggItemConfig.setStageId(trainsetBaseInfo.getTraintempid());
                List<AggItemConfigModel> aggItemConfigList = iAggItemConfigService.getAggItemConfigList(aggItemConfig);

                AllotData allotDataOther = new AllotData();
                allotDataOther.setShowMode(allotDataList.get(0).getShowMode());
                allotDataOther.setWorkTypeName(allotDataList.get(0).getWorkTypeName());
                allotDataOther.setTrainsetName(allotDataList.get(0).getTrainsetName());
                allotDataOther.setWorkTaskName("??????");
                allotDataOther.setId(UUID.randomUUID().toString());

                List<XzyMTaskallotpacket> taskallotpackets = allotDataList.stream().map(AllotData::getPacket).collect(Collectors.toList());
                List<XzyMTaskallotItem> itemList = taskallotpackets.stream().map(XzyMTaskallotpacket::getTaskallotItemList).flatMap(Collection::stream).collect(Collectors.toList());
                List<XzyMTaskcarpart> xzyMTaskcarparts = itemList.stream().map(XzyMTaskallotItem::getXzyMTaskcarpartList).flatMap(Collection::stream).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(aggItemConfigList)) {
                    XzyMTaskallotpacket xzyMTaskallotpacket = new XzyMTaskallotpacket();
                    if(!ObjectUtils.isEmpty(allotData)){
                        xzyMTaskallotpacket.setPacketCode(allotData.getPacket().getPacketCode());
                        xzyMTaskallotpacket.setPacketType(allotData.getPacket().getPacketType());
                        xzyMTaskallotpacket.setMainCyc(allotData.getTaskRepairCode());
                        xzyMTaskallotpacket.setPacketName(allotData.getPacket().getPacketName());
                    }
                    xzyMTaskallotpacket.setTaskallotItemList(itemList);
                    allotDataOther.setPacket(xzyMTaskallotpacket);
                    List<XzyMTaskallotperson> workList = xzyMTaskcarparts.stream().map(XzyMTaskcarpart::getWorkerList).flatMap(Collection::stream).collect(Collectors.toList());
                    getAllotState(allotDataOther, workList);
                    allotDataNew.add(allotDataOther);
                } else {
                    //??????????????????
                    Map<String, List<AggItemConfigModel>> aggItemMap = aggItemConfigList.stream().collect(Collectors.groupingBy(AggItemConfigModel::getAggPacketName));
                    List<XzyMTaskallotItem> otherItemList = new ArrayList<>();
                    List<XzyMTaskallotperson> otherWorkerList = new ArrayList<>();
                    aggItemMap.forEach((aggPacketCode, aggItemConfigModels) -> {
                        List<XzyMTaskallotperson> workList = new ArrayList<>();
                        List<XzyMTaskallotItem> taskallotItemList = new ArrayList<>();
                        for (AggItemConfigModel aggItemConfigModel : aggItemConfigModels) {
                            for (AggItemConfigItem aggItemConfigItem : aggItemConfigModel.getAggItemConfigItems()) {
                                List<XzyMTaskcarpart> taskcarparts = new ArrayList<>();
                                List<XzyMTaskcarpart> taskcarpartList = new ArrayList<>();
                                for (XzyMTaskcarpart xzyMTaskcarpart : xzyMTaskcarparts) {
                                    if (xzyMTaskcarpart.getItemCode().equals(aggItemConfigItem.getItemCode())) {
                                        if (aggItemConfigModel.getAggPacketCar().contains(xzyMTaskcarpart.getCarNo())) {
                                            //??????
                                            XzyMTaskcarpart carPart = new XzyMTaskcarpart();
                                            BeanUtils.copyProperties(xzyMTaskcarpart,carPart);
                                            taskcarparts.add(carPart);
                                            //????????????
                                            workList.addAll(xzyMTaskcarpart.getWorkerList());
                                        } else {
                                            //????????????
                                            XzyMTaskcarpart carPart = new XzyMTaskcarpart();
                                            BeanUtils.copyProperties(xzyMTaskcarpart,carPart);
                                            taskcarpartList.add(carPart);
                                            //??????????????????
                                            otherWorkerList.addAll(xzyMTaskcarpart.getWorkerList());
                                        }
                                    }
                                }
                                //??????????????????
                                getItemList(taskallotItemList, aggItemConfigItem, taskcarparts);
                                //????????????
                                getItemList(otherItemList, aggItemConfigItem, taskcarpartList);
                            }
                        }
                        AllotData allotDataAgg = new AllotData();
                        allotDataAgg.setWorkTaskName(aggItemConfigModels.get(0).getAggPacketName());
                        allotDataAgg.setId(UUID.randomUUID().toString());
                        XzyMTaskallotpacket xzyMTaskallotpacket = new XzyMTaskallotpacket();
                        if(!ObjectUtils.isEmpty(allotData)){
                            allotDataAgg.setShowMode(allotData.getShowMode());
                            allotDataAgg.setWorkTypeName(allotData.getWorkTypeName());
                            allotDataAgg.setTrainsetName(allotData.getTrainsetName());
                            xzyMTaskallotpacket.setPacketCode(allotData.getPacket().getPacketCode());
                            xzyMTaskallotpacket.setPacketType(allotData.getPacket().getPacketType());
                            xzyMTaskallotpacket.setMainCyc(allotData.getTaskRepairCode());
                            xzyMTaskallotpacket.setPacketName(allotData.getPacket().getPacketName());
                        }
                        xzyMTaskallotpacket.setTaskallotItemList(taskallotItemList);
                        allotDataAgg.setPacket(xzyMTaskallotpacket);
                        if (!CollectionUtils.isEmpty(taskallotItemList)) {
                            allotDataNew.add(allotDataAgg);
                        }
                        getAllotState(allotDataAgg, workList);
                    });
                    List<AggItemConfigItem> aggItems = Optional.ofNullable(aggItemConfigList).orElseGet(ArrayList::new).stream().map(AggItemConfigModel::getAggItemConfigItems).flatMap(Collection::stream).collect(Collectors.toList());
                    List<String> itemCodes = Optional.ofNullable(aggItems).orElseGet(ArrayList::new).stream().map(AggItemConfigItem::getItemCode).collect(Collectors.toList());
                    for (XzyMTaskallotItem xzyMTaskallotItem : itemList) {
                        if (!CollectionUtils.isEmpty(itemCodes)&&!itemCodes.contains(xzyMTaskallotItem.getItemCode())) {
                            otherItemList.add(xzyMTaskallotItem);
                            List<XzyMTaskallotperson> workerList = xzyMTaskallotItem.getXzyMTaskcarpartList().stream().map(XzyMTaskcarpart::getWorkerList).flatMap(Collection::stream).collect(Collectors.toList());
                            otherWorkerList.addAll(workerList);
                        }
                    }

                    XzyMTaskallotpacket otherTaskallotpacket = new XzyMTaskallotpacket();
                    if(!ObjectUtils.isEmpty(allotData)){
                        otherTaskallotpacket.setPacketCode(allotData.getPacket().getPacketCode());
                        otherTaskallotpacket.setPacketType(allotData.getPacket().getPacketType());
                        otherTaskallotpacket.setMainCyc(allotData.getTaskRepairCode());
                        otherTaskallotpacket.setPacketName(allotData.getPacket().getPacketName());
                    }
                    otherTaskallotpacket.setTaskallotItemList(otherItemList);
                    allotDataOther.setPacket(otherTaskallotpacket);
                    getAllotState(allotDataOther, otherWorkerList);
                    if (!CollectionUtils.isEmpty(otherItemList)) {
                        allotDataNew.add(allotDataOther);
                    }
                }
            });
            allotDatas.forEach(allotData -> {
                if (!"2".equals(allotData.getTaskRepairCode())) {
                    allotDataNew.add(allotData);
                }
            });
            return allotDataNew;
        }
        return allotDatas;
    }

    private void getItemList(List<XzyMTaskallotItem> otherItemList, AggItemConfigItem aggItemConfigItem, List<XzyMTaskcarpart> taskcarpartList) {
        XzyMTaskallotItem other = new XzyMTaskallotItem();
        other.setXzyMTaskcarpartList(taskcarpartList);
        other.setId(UUID.randomUUID().toString());
        other.setDisplayItemName(aggItemConfigItem.getItemName());
        List<XzyMTaskallotperson> workerList = taskcarpartList.stream().map(XzyMTaskcarpart::getWorkerList).flatMap(Collection::stream)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(XzyMTaskallotperson::getWorkerID))), ArrayList::new));
        other.setWorkerList(workerList);
        if (!CollectionUtils.isEmpty(taskcarpartList)) {
            otherItemList.add(other);
        }
    }

    private void getAllotState(AllotData allotData, List<XzyMTaskallotperson> workList) {
        List<XzyMTaskcarpart> collect = allotData.getPacket().getTaskallotItemList().stream().map(XzyMTaskallotItem::getXzyMTaskcarpartList).flatMap(Collection::stream).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(workList)) {
            allotData.setAllotStateName("?????????");
            allotData.setAllotStateCode("0");
        } else if (!CollectionUtils.isEmpty(workList) && collect.stream().anyMatch(t -> CollectionUtils.isEmpty(t.getWorkerList()))) {
            allotData.setAllotStateName("????????????");
            allotData.setAllotStateCode("2");
        } else {
            allotData.setAllotStateName("?????????");
            allotData.setAllotStateCode("1");
        }
        List<XzyMTaskallotperson> workerList = workList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(XzyMTaskallotperson::getWorkerID))), ArrayList::new));
        allotData.setWorkerList(workerList);
    }

    // ??????????????????
    public void setQuery(AllotData allotData, List<QueryTrainType> queryTrainTypeList) {
        String trainsetID = allotData.getTrainsetId();
        String trainsetName = allotData.getTrainsetName();
        String packetCode = allotData.getPacket().getPacketCode();
        String packetName = allotData.getPacket().getPacketName();
        // ??????????????????
        List<QueryTrainType> queryTrainsetType = queryTrainTypeList.stream()
            .filter(t -> t.getTrainsetTypeCode().equals(allotData.getTrainsetTypeCode()))
            .collect(Collectors.toList());
        // ??????
        if (queryTrainsetType == null || queryTrainsetType.size() == 0) {
            QueryTrainType queryTrainType = new QueryTrainType();
            queryTrainType.setTrainsetTypeCode(allotData.getTrainsetTypeCode());
            queryTrainType.setTrainsetTypeName(allotData.getTrainsetTypeName());
            List<QueryTrainset> TrainsetList = new ArrayList<QueryTrainset>();
            QueryTrainset queryTrainset = new QueryTrainset();
            queryTrainset.setTrainsetId(trainsetID);
            queryTrainset.setTrainsetName(trainsetName);
            TrainsetList.add(queryTrainset);
            List<QueryWorkTask> queryWorkTaskLists = new ArrayList<QueryWorkTask>();
            QueryWorkTask queryWorkTask = new QueryWorkTask();
            queryWorkTask.setWorkTaskCode(packetCode);
            queryWorkTask.setWorkTaskName(packetName);
            queryWorkTaskLists.add(queryWorkTask);
            queryTrainset.setQueryWorkTaskList(queryWorkTaskLists);
            queryTrainType.setQueryTrainsetList(TrainsetList);
            queryTrainTypeList.add(queryTrainType);
        } else {// ?????????

//			QueryTrainType queryTrainType = queryTrainsetType.get(0);
            QueryTrainType queryTrainType = new QueryTrainType();
            queryTrainType.setTrainsetTypeCode(allotData.getTrainsetTypeCode());
            queryTrainType.setTrainsetTypeName(allotData.getTrainsetTypeName());
            List<QueryTrainset> TrainsetList = new ArrayList<QueryTrainset>();
            QueryTrainset queryTrainset = new QueryTrainset();
            queryTrainset.setTrainsetId(trainsetID);
            queryTrainset.setTrainsetName(trainsetName);
            TrainsetList.add(queryTrainset);
            List<QueryWorkTask> queryWorkTaskLists = new ArrayList<QueryWorkTask>();
            QueryWorkTask queryWorkTask = new QueryWorkTask();
            queryWorkTask.setWorkTaskCode(packetCode);
            queryWorkTask.setWorkTaskName(packetName);
            queryWorkTaskLists.add(queryWorkTask);
            queryTrainset.setQueryWorkTaskList(queryWorkTaskLists);
            queryTrainType.setQueryTrainsetList(TrainsetList);
            queryTrainTypeList.add(queryTrainType);
        }

    }

    public List<AggItemConfigModel> getAggPacketCars(TrainsetBaseInfo trainsetBaseInfo, XzyMTaskallotItem xzyMTaskallotItem) {
        AggItemConfigModel aggItemConfig = new AggItemConfigModel();
        aggItemConfig.setTrainType(trainsetBaseInfo.getTraintype());
        //aggItemConfig.setStageId(trainsetBaseInfo.getTraintempid());
//        aggItemConfig.setItemCode(xzyMTaskallotItem.getItemCode());
//        aggItemConfig.setItemName(xzyMTaskallotItem.getItemName());
        return iAggItemConfigService.getAggItemConfigList(aggItemConfig);
/*        List<String> aggPacketCars = new ArrayList<>();
        for (AggItemConfigModel item : aggItemConfigs) {
            if (item.getAggPacketCar() != null && !item.getAggPacketCar().equals("")) {
                List<String> l = Arrays.asList(item.getAggPacketCar().split(","));
                aggPacketCars.addAll(l);
            }
        }
        return aggPacketCars;*/
    }

    // ?????????????????????????????????
    public XzyMTaskallotItem setNullItemForXzyMTaskallotItem(ZtTaskPacketEntity task, List<XzyMTaskcarpart> xzyMTaskcarpartList, String onlyPacketid, String trainsetType) {
        String taskid = task.getTaskId();
        String dayPlanId = task.getDayplanId();
        String trainsetId = task.getTrainsetId();
        String itemCode = task.getPacketCode();
        List<XzyMTaskcarpart> carpartList = xzyMTaskcarpartList.stream().filter(t -> dayPlanId.equals(t.getDayPlanID()) && trainsetId.equals(t.getTrainsetId()) && itemCode.equals(t.getItemCode())).collect(Collectors.toList());
        XzyMTaskcarpart queryCarpart = null;
        if (carpartList != null && carpartList.size() > 0) {
            queryCarpart = carpartList.get(0);
        }
        // ?????????????????????????????????
        XzyMTaskallotItem taskallotItem = new XzyMTaskallotItem();
        taskallotItem.setItemCode(task.getPacketCode());
        taskallotItem.setItemName(task.getPacketName());
        // ??????????????????
        List<XzyMTaskcarpart> taskcarpartList = new ArrayList<XzyMTaskcarpart>();
        XzyMTaskcarpart carpart = new XzyMTaskcarpart();
        carpart.setPacketCode(task.getPacketCode());
        carpart.setPacketName(task.getPacketName());
        carpart.setPacketType(task.getPacketTypeCode());
        carpart.setProcessId(queryCarpart == null ? null : queryCarpart.getProcessId());
        carpart.setDayPlanID(task.getDayplanId());
        carpart.setUnitCode(task.getDepotCode());
        carpart.setUnitName("");
        carpart.setItemCode(taskallotItem.getItemCode());
        carpart.setItemName(taskallotItem.getItemName());
        carpart.setRepairType(task.getPacketTypeCode());
        carpart.setArrageType(ProcessArrageTypeEnum.CAR.getValue());
        carpart.setDataSource("");
        carpart.setPartType("");
        carpart.setPartPosition("");
        carpart.setRemark("");
        carpart.setPartName("");
        carpart.setTaskAllotPacketId(onlyPacketid);
        if ((queryCarpart == null) || (queryCarpart != null && queryCarpart.getXzyMTaskallotdept().getDeptCode() == null) || queryCarpart.getXzyMTaskallotdept() == null) {
            XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
            taskallotdept.setDeptCode(task.getRepairDeptCode());
            taskallotdept.setDeptName(task.getRepairDeptName());
            carpart.setXzyMTaskallotdept(taskallotdept);
        } else {
            carpart.setTaskAllotDeptId(queryCarpart.getTaskAllotDeptId());
            XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
            taskallotdept.setDeptCode(task.getRepairDeptCode());
            taskallotdept.setDeptName(task.getRepairDeptName());
            carpart.setXzyMTaskallotdept(taskallotdept);
            carpart.setXzyMTaskallotdept(taskallotdept);
        }
        carpart.setCarNo("??????");
        carpart.setTrainsetType(trainsetType);
        carpart.setTaskItemId(taskid);
        carpart.setMainCyc(task.getTaskRepairCode());
        if ((queryCarpart == null) || (queryCarpart != null && queryCarpart.getWorkerList() == null)
            || queryCarpart.getWorkerList().size() == 0) {
            carpart.setWorkerList(new ArrayList<XzyMTaskallotperson>());
        } else {
            carpart.setWorkerList(queryCarpart.getWorkerList());
        }
        if (carpart.getArrageType().equals(ProcessArrageTypeEnum.PART.getValue())) {
            carpart.setDisplayCarPartName(carpart.getPartName());
        } else {
            carpart.setDisplayCarPartName(carpart.getCarNo());
        }
        carpart.setTrainsetId(task.getTrainsetId());
        carpart.setTrainsetName(task.getTrainsetName());
        taskcarpartList.add(carpart);
        taskallotItem.setXzyMTaskcarpartList(taskcarpartList);
        taskallotItem.setWorkerList(carpart.getWorkerList());
        return taskallotItem;
    }

    // ?????? ??????????????? ?????? ???????????? ??? ????????? ??????
    public String packetTypeChangeToItemType(String packetType, String maincyc) {

        if ("1".equals(packetType) && "1".equals(maincyc)) {
            return "0";
        } else {
            return packetType;
        }
    }

    // ??????????????????????????????
    public XzyMTaskcarpart getXzyMTaskcarpart(ZtTaskPacketEntity task, ZtTaskItemEntity item, List<XzyMTaskcarpart> xzyMTaskcarpartList,String onlyPacketid, String trainsetType) {
        String dayPlanId = item.getDayplanId();
        String trainsetId = item.getTrainsetId();
        String itemCode = item.getItemCode();
        String carno = item.getCarNo()==null?"":item.getCarNo();
        String partName = item.getPositionName();
        String deptCode = task.getRepairDeptCode();
        String deptName = task.getRepairDeptName();
        List<XzyMTaskcarpart> carpartList = new ArrayList<>();
        if (StringUtils.isNotEmpty(carno)||(StringUtils.isEmpty(carno)&&"12".equals(item.getItemType()))) {
            carpartList = xzyMTaskcarpartList.stream().filter(t -> dayPlanId.equals(t.getDayPlanID()) && trainsetId.equals(t.getTrainsetId()) && itemCode.equals(t.getItemCode()) && carno.equals(t.getCarNo()==null?"":t.getCarNo())).collect(Collectors.toList());
        } else {
            carpartList = xzyMTaskcarpartList.stream().filter(t -> dayPlanId.equals(t.getDayPlanID()) && trainsetId.equals(t.getTrainsetId()) && itemCode.equals(t.getItemCode()) && partName.equals(t.getPartName())).collect(Collectors.toList());
        }
        XzyMTaskcarpart queryCarpart = null;
        if (carpartList != null && carpartList.size() > 0) {
            queryCarpart = carpartList.get(0);
        }
        XzyMTaskcarpart carpart = new XzyMTaskcarpart();
        carpart.setPacketCode(task.getPacketCode());
        carpart.setPacketName(task.getPacketName());
        carpart.setPacketType(task.getPacketTypeCode());
        carpart.setProcessId(queryCarpart == null ? null : queryCarpart.getProcessId());
        carpart.setDayPlanID(task.getDayplanId());
        carpart.setUnitCode(task.getDepotCode());
        carpart.setUnitName("");
        carpart.setItemCode(item.getItemCode());
        carpart.setItemName(item.getItemName());
        carpart.setRepairType(item.getItemType());
        carpart.setArrageType(StringUtils.isEmpty(item.getArrangeType()) ? ProcessArrageTypeEnum.CAR.getValue() : item.getArrangeType());
        carpart.setDataSource("");
        carpart.setPartType(item.getKeyPartTypeId());
        carpart.setPartPosition(item.getPositionCode());
        carpart.setLocationCode(item.getLocationCode());
        carpart.setLocationName(item.getLocationName());
        carpart.setRemark("");
        carpart.setPartName(item.getPositionName());
        carpart.setTaskAllotPacketId(onlyPacketid);
        carpart.setCarNo(item.getCarNo());
        carpart.setTrainsetType(trainsetType);
        if (carpart.getArrageType().equals(ProcessArrageTypeEnum.PART.getValue())) {
            carpart.setDisplayCarPartName(carpart.getPartName());
        } else if (carpart.getRepairType().equals("5")) {
            carpart.setDisplayCarPartName("[?????????" + carpart.getCarNo() + "]" + carpart.getItemName());
        } else {
            carpart.setDisplayCarPartName(carpart.getCarNo());
        }

        String taskItemID = item.getTaskItemId();
        if (taskItemID == null || taskItemID.equals("")) {
            carpart.setTaskItemId(UUID.randomUUID().toString());
        } else {
            carpart.setTaskItemId(item.getTaskItemId());
        }
        if ((queryCarpart == null) || (queryCarpart != null && queryCarpart.getTaskAllotDeptId() == null) ||
            queryCarpart.getXzyMTaskallotdept() == null) {
            XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
            taskallotdept.setDeptCode(deptCode);
            taskallotdept.setDeptName(deptName);
            taskallotdept.setTaskAllotDeptId("");
            carpart.setXzyMTaskallotdept(taskallotdept);
        } else {
            carpart.setTaskAllotDeptId(queryCarpart.getTaskAllotDeptId());
            carpart.setXzyMTaskallotdept(queryCarpart.getXzyMTaskallotdept());
        }
        carpart.setMainCyc(task.getTaskRepairCode());
        if ((queryCarpart == null) || (queryCarpart != null && queryCarpart.getWorkerList() == null)
            || queryCarpart.getWorkerList().size() == 0) {
            carpart.setWorkerList(new ArrayList<XzyMTaskallotperson>());
        } else {
            carpart.setWorkerList(queryCarpart.getWorkerList());
        }
        carpart.setTrainsetId(task.getTrainsetId());
        carpart.setTrainsetName(task.getTrainsetName());
        return carpart;
    }

    // ??????????????????
    public List<XzyMTaskallotperson> getPersons(List<String> processIds) {
        logger.info("????????????????????????????????????...");
        TaskAllotPersonQueryParamModel taskAllotPersonQueryParamModel = new TaskAllotPersonQueryParamModel();
        taskAllotPersonQueryParamModel.setProcessIds(processIds);
        List<XzyMTaskallotperson> personsList = new ArrayList<>();
        try {
            personsList = taskAllotPersonService.getPersons(taskAllotPersonQueryParamModel);
        } catch (Exception e) {
            logger.error("????????????????????????????????????getPersons", e);
        }
        logger.info("??????????????????????????????" + personsList.size());
        logger.info("????????????????????????");
        return personsList;
    }

    // ???????????????????????????????????????
    public List<XzyMTaskallotpacket> getTaskAllotPackets(List<String> packetids) {
        logger.info("?????????????????????????????????...");
        String selectStr = "('" + String.join("','", packetids) + "')";
        Map<String, String> queryCarPartMap = new HashMap<>();
        queryCarPartMap.put("packetids", selectStr);
        List<XzyMTaskallotpacket> xzyMTaskallotpacketList = new ArrayList<>();
        List<JSONObject> getTaskAllotPacketsJson = new ArrayList<>();
        try {
            xzyMTaskallotpacketList = taskallotpacketService.getTaskAllotPackets(queryCarPartMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("?????????????????????????????????getTaskAllotPackets???" + selectStr);
        }
        logger.info("???????????????????????????" + xzyMTaskallotpacketList.size());
        logger.info("?????????????????????");
        return xzyMTaskallotpacketList;
    }

    @Data
    public static class ProcessGroupKey {
        private String itemCode;
        private String itemName;
        private String carNoStrs;
    }

    @Override
    public List<QueryTaskAllot> getQueryTaskAllotList(JSONObject jsonObject) {
        String itemName = jsonObject.getString("itemName");
        String workerName = jsonObject.getString("workerName");
        List<QueryTaskAllot> queryTaskAllotList = new ArrayList<>();
        //1.??????????????????????????????
        JSONObject res = new RestRequestKitUseLogger<JSONObject>(midGroundServiceId, logger) {
        }.postObject("/xzyMTaskallot/getTaskAllotDataByDate", jsonObject);
        //2.????????????
        List<TaskAllotPacketEntity> packetList = res.getJSONArray("data").toJavaList(TaskAllotPacketEntity.class);
        if (packetList != null && packetList.size() > 0) {
            packetList.stream().map(packetItem -> {//3.???????????????
                List<TaskAllotItemEntity> itemList = packetItem.getTaskAllotItemEntityList();
                //4.????????????????????????????????????????????????
                Map<ProcessGroupKey, List<TaskAllotItemEntity>> groupList = itemList.stream().collect(Collectors.groupingBy(v -> {
                    ProcessGroupKey key = new ProcessGroupKey();
                    key.setItemCode(v.getItemCode());
                    key.setItemName(v.getItemName());
                    return key;
                }));
                //5.????????????
                groupList.forEach((processGroupKey, groupItem) -> {
                    List<QueryTaskAllot> queryTaskAllots = new ArrayList<>();
                    Map<String, TaskAllotPersonEntity> personMap = new HashMap<>();
                    //6.????????????????????????
                    groupItem.stream().map(item -> {
                        List<TaskAllotPersonEntity> personList = item.getTaskAllotPersonEntityList();
                        //7.??????????????????
                        personList.stream().map(personItem -> {
                            //???????????????????????????
                            personMap.put(personItem.getWorkerId(), personItem);
                            return personItem;
                        }).collect(Collectors.toList());
                        return item;
                    }).collect(Collectors.toList());
                    //8.??????????????????
                    for (String key : personMap.keySet()) {
                        TaskAllotPersonEntity personEntity = personMap.get(key);
                        List<TaskAllotItemEntity> filterItemList = groupItem.stream().filter(t -> t.getTaskAllotPersonEntityList().contains(personEntity)).collect(Collectors.toList());
                        //??????????????????
                        QueryTaskAllot queryTaskAllot = new QueryTaskAllot();
                        queryTaskAllot.setUnitCode(packetItem.getUnitCode());
                        queryTaskAllot.setUnitName(packetItem.getUnitName());
                        queryTaskAllot.setTrainsetName(packetItem.getTrainsetName());
                        queryTaskAllot.setTrainsetId(packetItem.getTrainsetId());
                        queryTaskAllot.setDayPlanId(packetItem.getDayPlanId());
                        queryTaskAllot.setDeptCode(packetItem.getDeptCode());
                        queryTaskAllot.setDeptName(packetItem.getDeptName());
                        queryTaskAllot.setPacketCode(packetItem.getPacketCode());
                        queryTaskAllot.setPacketName(packetItem.getPacketName());
                        queryTaskAllot.setPacketType(packetItem.getPacketType());
                        queryTaskAllot.setItemCode(processGroupKey.getItemCode());
                        queryTaskAllot.setItemName(processGroupKey.getItemName());
                        queryTaskAllot.setWorkerId(personEntity.getWorkerId());
                        queryTaskAllot.setRepairType(packetItem.getMainCyc());
                        //??????????????????????????????????????????????????????????????????????????????????????????
                        List<TaskAllotPersonPostEntity> postEntityList = personEntity.getTaskAllotPersonPostEntityList();
                        String workerNameAndPost = personEntity.getWorkerName();
                        if(!CollectionUtils.isEmpty(postEntityList)){
                            postEntityList = postEntityList.stream().sorted(Comparator.comparing(TaskAllotPersonPostEntity::getPostId)).collect(Collectors.toList());
                            List<String> postNameList = postEntityList.stream().map(t -> t.getPostName()).collect(Collectors.toList());
                            String postNameStrs = StringUtils.join(postNameList,",");
                            workerNameAndPost +="???"+postNameStrs+"???";
                        }
                        queryTaskAllot.setWorkerName(workerNameAndPost);
                        List<String> carNos = new ArrayList<>();
                        TaskAllotItemEntity taskAllotItem = groupItem.stream().findFirst().get();
                        if(!ObjectUtils.isEmpty(taskAllotItem)&&"1".equals(taskAllotItem.getArrageType())){
                            carNos = groupItem.stream().map(t->t.getPartName()).collect(Collectors.toList());
                            Collections.sort(carNos);
                            List<String> filterCarNos = groupItem.stream().filter(t->"00".equals(t.getCarNo())).map(t->t.getPartName()).collect(Collectors.toList());
                            if(!CollectionUtils.isEmpty(filterCarNos)){
                                carNos.removeAll(filterCarNos);
                                carNos.addAll(filterCarNos);
                            }
                        }else{
                            carNos = groupItem.stream().map(t -> t.getCarNo()).collect(Collectors.toList());
                            Collections.sort(carNos);
                            if (carNos.contains("00")) {
                                carNos.remove("00");
                                carNos.add("00");
                            }
                        }
                        queryTaskAllot.setCarNos(carNos);
                        queryTaskAllot.setCarNoStrs(carNos.stream().collect(Collectors.joining(",")));
                        boolean hasAddList = false;
                        hasAddList = (itemName != null && queryTaskAllot.getItemName().contains(itemName)) || itemName == null;

                        if (hasAddList) {
                            hasAddList = (workerName != null && queryTaskAllot.getWorkerName().contains(workerName)) || workerName == null;
                        }
                        if (hasAddList) {
                            queryTaskAllots.add(queryTaskAllot);
                            // queryTaskAllotList.add(queryTaskAllot);
                        }
                    }
                    Map<ProcessGroupKey, List<QueryTaskAllot>> queryGroup = queryTaskAllots.stream().collect(Collectors.groupingBy(v -> {
                        ProcessGroupKey key = new ProcessGroupKey();
                        key.setItemCode(v.getItemCode());
                        key.setItemName(v.getItemName());
                        key.setCarNoStrs(v.getCarNoStrs());
                        return key;
                    }));
                    queryGroup.forEach((processGroupKey1, queryTaskAllots1) -> {
                        //??????????????????
                        QueryTaskAllot resItem = new QueryTaskAllot();
                        resItem.setUnitCode(packetItem.getUnitCode());
                        resItem.setUnitName(packetItem.getUnitName());
                        resItem.setTrainsetName(packetItem.getTrainsetName());
                        resItem.setTrainsetId(packetItem.getTrainsetId());
                        resItem.setDayPlanId(packetItem.getDayPlanId());
                        resItem.setDeptCode(packetItem.getDeptCode());
                        resItem.setDeptName(packetItem.getDeptName());
                        resItem.setPacketCode(packetItem.getPacketCode());
                        resItem.setPacketName(packetItem.getPacketName());
                        resItem.setPacketType(packetItem.getPacketType());
                        resItem.setItemCode(processGroupKey.getItemCode());
                        resItem.setItemName(processGroupKey.getItemName());
                        resItem.setRepairType(packetItem.getMainCyc());
                        resItem.setCarNos(Arrays.asList(processGroupKey1.getCarNoStrs()));
                        resItem.setWorkerNameList(queryTaskAllots1.stream().map(t->t.getWorkerName()).collect(Collectors.toList()));
                        queryTaskAllotList.add(resItem);
                        return;
                    });
                });
                return packetItem;
            }).collect(Collectors.toList());
        }
        return queryTaskAllotList;
    }

    @Override
    public List<RepairItemVo> selectRepairItemList(Map map, Page page) {
        return taskallotpacketMapper.selectRepairItemList(map,page);
    }
}
