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

    //中台服务
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
        logger.info("---------------------------------------------------------调用派工查询---------------------------------------------------------");
        List<ZtTaskPacketEntity> taskPacketEntityList = new ArrayList<>();
        List<XzyMTaskcarpart> xzyMTaskcarpartList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(unitCode)&&!ObjectUtils.isEmpty(dayplanId)&&!ObjectUtils.isEmpty(deptCode)){
            // 根据条件查询所有任务
            taskPacketEntityList = getTaskPacketEntityList(dayplanId, unitCode, deptCode);
            // 查询 xzy_m_taskcarpart 按照 dayPlanID unitCode deptCode 获取任务部件辆序数据
            xzyMTaskcarpartList = getMTaskcarpartList(dayplanId, unitCode, deptCode);
        }

        //获取派工显示字典表集合数据
        List<XzyBTaskallotshowDict> xzyBTaskallotshowDictList = xzyBTaskallotshowDictService.getShowDictByTaskAllotType("");

        //获取项目类型字典表
        List<XzyBTaskallottypeDict> xzyBTaskallottypeDictList = MybatisPlusUtils.selectList(
            taskallottypeDictService,
            eqParam(XzyBTaskallottypeDict::getcFlag, "1")
        );

        //根据编组数量查询一级修派工配置模板
        List<XzyCOneallotCarConfig> allotCarConfigs = getTrainsetDetialInfo(null, unitCode, deptCode);
        List<XzyCOneallotCarConfig> xzyCOneallotCarConfigList = getOneAllotConfigList(unitCode,deptCode);


        //根据模板id获取一级修派工配置模板
        List<XzyCOneallotTemplate> templateList = getOneAllotTemplates(null);

        // 通过 xzyMTaskcarpartList 集合获取参数
        logger.info("1.准备取出派工辆序部件集合中的包、项、部门id集合...");
        List<String> processids = xzyMTaskcarpartList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
        List<String> packetids = xzyMTaskcarpartList.stream().map(t -> t.getTaskAllotPacketId()).distinct().collect(Collectors.toList());
        List<String> deptids = xzyMTaskcarpartList.stream().map(t -> t.getTaskAllotDeptId()).distinct().collect(Collectors.toList());
        logger.info("包、项、部门id集合获取完成");
        // 获取派工部门
        List<XzyMTaskallotdept> depts = new ArrayList<>();
        if(!CollectionUtils.isEmpty(deptids)){
            depts = getTaskAllotDepts(deptids);
        }
        // 获取派工人员
        List<XzyMTaskallotperson> persons = new ArrayList<>();
        if(!CollectionUtils.isEmpty(processids)){
            persons = getPersons(processids);
        }
        // 获取派工作业包
        List<XzyMTaskallotpacket> packets = new ArrayList<>();
        if(!CollectionUtils.isEmpty(packetids)){
            packets = getTaskAllotPackets(packetids);
        }
        // 准备筛选条件集合
        List<QueryTrainType> queryTrainTypeList = new ArrayList<>();
        logger.info("2.准备组装派工辆序部件数据，将人员和部门放入辆序部件下...");
        for (XzyMTaskcarpart carpart : xzyMTaskcarpartList) {
            String processid = carpart.getProcessId();
            String deptid = carpart.getTaskAllotDeptId();
            // 挂人员
            List<XzyMTaskallotperson> workerList = persons.stream().filter(t -> processid.equals(t.getProcessId())).collect(Collectors.toList());
            carpart.setWorkerList(workerList);
            // 挂部门
            List<XzyMTaskallotdept> deptList = depts.stream().filter(t -> deptid.equals(t.getTaskAllotDeptId())).collect(Collectors.toList());
            if (deptList != null && deptList.size() > 0) {
                carpart.setXzyMTaskallotdept(deptList.get(0));
            }
        }
        logger.info("派工辆序部件数据组装完成");
        // xzyMTaskcarpartList , taskPacketEntityList 都已经到手了
        List<AllotData> AllotDatas = new ArrayList<AllotData>();
        //车组详细信息map集合
        Map<String, TrainsetInfo> trainsetInfoMap = new HashMap<>();
        if (taskPacketEntityList != null && taskPacketEntityList.size() > 0) {
            logger.info("3.准备构建检修任务数据...");
            // 循环沈明雷的计划数据
            for (ZtTaskPacketEntity task : taskPacketEntityList) {
                AllotData allotData = new AllotData();
                allotData.setTaskRepairCode(task.getTaskRepairCode());
                allotData.setWorkerList(new ArrayList<XzyMTaskallotperson>());
                allotData.setId(UUID.randomUUID().toString());
                allotData.setTrainsetId(task.getTrainsetId());
                allotData.setTrainsetName(task.getTrainsetName());
                // 获取作业类型
                String itemTypeCode = packetTypeChangeToItemType(task.getPacketTypeCode(), task.getTaskRepairCode());
                XzyBTaskallottypeDict worktypeDict = new XzyBTaskallottypeDict();
                if (StringUtils.isNotEmpty(itemTypeCode)) {
                    //项目类型字典表
                    List<XzyBTaskallottypeDict> typeDictFilterList = xzyBTaskallottypeDictList.stream().filter(t -> itemTypeCode.equals(t.getsTaskallottypecode())).collect(Collectors.toList());
                    if (typeDictFilterList != null && typeDictFilterList.size() > 0) {
                        worktypeDict = typeDictFilterList.get(0);
                    }
                    //派工显示内容字典
                    List<XzyBTaskallotshowDict> showDictFilterList = xzyBTaskallotshowDictList.stream().filter(t -> itemTypeCode.equals(t.getItemTypeCode())).collect(Collectors.toList());
                    if (showDictFilterList != null && showDictFilterList.size() > 0) {
                        allotData.setShowMode(showDictFilterList.get(0).getsTaskallotshowcode());
                    }
                }
                allotData.setWorkTypeCode(worktypeDict.getsTaskallottypecode());
                allotData.setWorkTypeName(worktypeDict.getsTaskallottypename());
                allotData.setAllotStateCode("0");
                allotData.setAllotStateName("未派工");
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
                // 派工包id
                String onlyPacketid = null;
                if (packetlist != null && packetlist.size() > 0) {
                    // 只能有一个
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
                // 存储派工人数
                List<Integer> workersCount = new ArrayList<Integer>();
                // 日计划有项目的情况下
                if (taskItems != null && taskItems.size() > 0) {
                    // 一级修的情况
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
                        // 设置 显示模式
                        Map<String, List<ZtTaskItemEntity>> groupItemCode = taskItems.stream().collect(Collectors.groupingBy(ZtTaskItemEntity::getItemCode));
                        for (String itemCode : groupItemCode.keySet()) {
                            List<ZtTaskItemEntity> taskItemsGroup = groupItemCode.get(itemCode);
                            // 取出每个项目Code集合的 辆序
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
                                    taskallotItem.setDisplayItemName("[辆序：" + taskcarpart.getCarNo() + "]" + taskcarpart.getItemName());
                                    if (!ObjectUtils.isEmpty(taskcarpart.getCarNo())&&!taskcarpart.getCarNo().equals("全列")) {
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
                    // 设置 显示模式
                    // 日计划没有项目的情况下
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
                //给辆序排序，将00辆序排到最后边
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
                // 拿 派工人员
                List<List<XzyMTaskallotperson>> getWorkers = packet.getTaskallotItemList().stream().filter(t -> t.getWorkerList() != null).distinct().map(t -> t.getWorkerList()).collect(Collectors.toList());
                // 拿 项目集合
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

                // 去重的人员集合
                List<XzyMTaskallotperson> distinctPesons = new ArrayList<XzyMTaskallotperson>();
                // 设置包
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
                    allotData.setAllotStateName("部分派工");
                } else if (!isNull && isNotNull) {
                    allotData.setAllotStateCode("1");
                    allotData.setAllotStateName("已派工");
                } else {
                    allotData.setAllotStateCode("0");
                    allotData.setAllotStateName("未派工");
                }

                AllotDatas.add(allotData);
            }
            logger.info("检修任务数据构建完成");
        }
        logger.info("准备整理查询条件集合...");
        //查询结果排序
        Collections.sort(AllotDatas, Comparator.comparing(AllotData::getTrainsetName));

        /** 将此段代码抽到一个方法中，暂时将这段代码注释掉
        //查询条件集合去重&排序
        List<QueryTrainType> queryTrainTypes = new ArrayList<>();
        List<String> trainTypeCodes = new ArrayList<>();
        //遍历车型集合，组织queryList条件
        for (QueryTrainType queryTrainType : queryTrainTypeList) {
            List<QueryTrainset> queryTrainsetList = queryTrainType.getQueryTrainsetList();
            //新车型集合是否为空
            if (queryTrainTypes != null && queryTrainTypes.size() > 0) {
                //若不为空则判断新车型集合是否包含该车型实体
                if (trainTypeCodes.contains(queryTrainType.getTrainsetTypeCode())) {
                    //如果包含，则遍历新车型集合，取出与本车型相同的实体，把本车型下的车组放入新车组实体中
                    for (QueryTrainType trainTypeEntity : queryTrainTypes) {
                        if (trainTypeEntity.getTrainsetTypeCode().equals(queryTrainType.getTrainsetTypeCode())) {
                            List<QueryTrainset> queryTrainsets = trainTypeEntity.getQueryTrainsetList();
                            queryTrainsets.addAll(queryTrainsetList);
                        }
                    }
                } else {
                    //若不包含则直接添加
                    queryTrainTypes.add(queryTrainType);
                    trainTypeCodes.add(queryTrainType.getTrainsetTypeCode());
                }
            } else {
                //若为空则直接添加
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
                //新集合不为空
                if (queryTrainsets != null && queryTrainsets.size() > 0) {
                    //新集合包含该实体
                    if (trainsetIds.contains(trainsetId)) {
                        //遍历新集合
                        for (QueryTrainset queryTrainset : queryTrainsets) {
                            //旧实体中的任务集合添加到新实体中
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
        logger.info("查询条件集合整理完成");
        // 获取派工字典
        List<AllotTypeDict> allotTypeDicts = getAllotTypeDict();
        //处理二级修专业分工显示情况
        List<AllotData> testData = new ArrayList<>();


        List<QueryTrainType> queryTrainTypes = new ArrayList<>();//查询条件
        //根据显示模式组织数据
        if(!CollectionUtils.isEmpty(AllotDatas)){
            if("2".equals(mode)){
                testData = getDivisionWorkTwo(AllotDatas);
            }else{
                testData = getTwo(mode, AllotDatas);
            }
            //最终数据组织完成之后再组织queryList查询条件
            if(!CollectionUtils.isEmpty(testData)){
                for(AllotData allotData:testData){
                    setQuery(allotData, queryTrainTypeList);
                }
                queryTrainTypes = this.getQueryTrainTypes(queryTrainTypeList);
            }
        }
        //处理项目是否能够重新派工（记录单工长签字之后就不能修改）
        if(!CollectionUtils.isEmpty(testData)){
            //根据日计划、班组、运用所查询记录单回填主表
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
        //处理重复对象返回前端引用问题
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(testData, SerializerFeature.DisableCircularReferenceDetect));
        List<AllotData> resData = jsonArray.toJavaList(AllotData.class);
        result.put("data", resData);
        result.put("queryList", queryTrainTypes);
        result.put("allotTypeDict", allotTypeDicts);
        result.put("taskAllotTypeDict", xzyBTaskallottypeDictList);
        result.put("msg", "操作成功");
        result.put("code", 1);
        return result;
    }

    //遍历车型集合，组织queryList条件
    public List<QueryTrainType> getQueryTrainTypes(List<QueryTrainType> queryTrainTypeList){
        List<QueryTrainType> queryTrainTypes = new ArrayList<>();
        List<String> trainTypeCodes = new ArrayList<>();
        for (QueryTrainType queryTrainType : queryTrainTypeList) {
            List<QueryTrainset> queryTrainsetList = queryTrainType.getQueryTrainsetList();
            //新车型集合是否为空
            if (queryTrainTypes != null && queryTrainTypes.size() > 0) {
                //若不为空则判断新车型集合是否包含该车型实体
                if (trainTypeCodes.contains(queryTrainType.getTrainsetTypeCode())) {
                    //如果包含，则遍历新车型集合，取出与本车型相同的实体，把本车型下的车组放入新车组实体中
                    for (QueryTrainType trainTypeEntity : queryTrainTypes) {
                        if (trainTypeEntity.getTrainsetTypeCode().equals(queryTrainType.getTrainsetTypeCode())) {
                            List<QueryTrainset> queryTrainsets = trainTypeEntity.getQueryTrainsetList();
                            queryTrainsets.addAll(queryTrainsetList);
                        }
                    }
                } else {
                    //若不包含则直接添加
                    queryTrainTypes.add(queryTrainType);
                    trainTypeCodes.add(queryTrainType.getTrainsetTypeCode());
                }
            } else {
                //若为空则直接添加
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
                //新集合不为空
                if (queryTrainsets != null && queryTrainsets.size() > 0) {
                    //新集合包含该实体
                    if (trainsetIds.contains(trainsetId)) {
                        //遍历新集合
                        for (QueryTrainset queryTrainset : queryTrainsets) {
                            //旧实体中的任务集合添加到新实体中
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
     * @desc: 获取派工查询条件
     */
    @Override
    public JSONObject getQuery(String unitCode, String dayplanId, String deptCode, String taskAllotType, String mode){
        JSONObject result = new JSONObject();
        //1.获取项目类型字典表
        List<XzyBTaskallottypeDict> xzyBTaskallottypeDictList = MybatisPlusUtils.selectList(
            taskallottypeDictService,
            eqParam(XzyBTaskallottypeDict::getcFlag, "1")
        );
        result.put("taskAllotTypeDict",xzyBTaskallottypeDictList);
        //2.获取派工状态字典
        List<AllotTypeDict> allotTypeDicts = getAllotTypeDict();
        result.put("allotTypeDict", allotTypeDicts);
        //3.获取日计划作业包信息
        List<QueryTrainType> queryTrainTypes = new ArrayList<>();
        List<ZtTaskPacketEntity> taskPacketEntityList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(unitCode)&&!ObjectUtils.isEmpty(dayplanId)&&!ObjectUtils.isEmpty(deptCode)){
            // 根据条件查询所有任务
            taskPacketEntityList = getTaskPacketEntityList(dayplanId, unitCode, deptCode);
            if(!CollectionUtils.isEmpty(taskPacketEntityList)){
                List<QueryTrainType> queryTrainTypeList = new ArrayList<>();
                //获取所有车组对应的车型
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
            Boolean isHavePerson = false;// 默认 没有人
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
            logger.error("获取派工部门失败，接口：getDeptByDeptCode，" + deptCode);
        }
        XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
        if (taskallotdeptList != null && taskallotdeptList.size() > 0) {
            taskallotdept = taskallotdeptList.get(0);
        }
        //将前端传过来的一堆字符串转为实体对象
        List<AllotData> allotDataList = getAllotData(mode, str);

        //处理AllotData集合，如果有专业分工的包，则将专业分工的包拆分成功各个作业包
        List<AllotData> newAllotDataList = new ArrayList<>();
        if("2".equals(mode)){
            newAllotDataList = this.splitDivisionWorkTwo(allotDataList);
        }else{
            newAllotDataList = allotDataList;
        }

        //调用计划服务，计划中项目状态回写+工长认领故障和复核任务回写计划
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
            result.put("msg", "操作成功");
            result.put("code", 1);
        }else{
            result.put("msg", "操作失败");
            result.put("code", -1);
        }
        return result;
    }

    //将专业分工包拆分为正确的二级修包
    public List<AllotData> splitDivisionWorkTwo(List<AllotData> allotDataList){
        List<AllotData> newAllotDataList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(allotDataList)){
            //1.获取配置表专业分工配置包类型
            ConfigParamsModel configParamsModel = new ConfigParamsModel();
            configParamsModel.setType("TASKALLOT");
            configParamsModel.setName("AGGPACKETTYPE");
            List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
            XzyCConfig xzyCConfig = Optional.ofNullable(configs).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(xzyCConfig)){
                String packetTypeCode = xzyCConfig.getParamValue();
                //2.将专业分工的数据择出来
                List<AllotData> otherAllotDataList = allotDataList.stream().filter(t -> !packetTypeCode.equals(t.getWorkTypeCode())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(otherAllotDataList)){
                    newAllotDataList.addAll(otherAllotDataList);
                    allotDataList.removeAll(otherAllotDataList);
                }
                //3.根据车组id进行分组
                Map<String, List<AllotData>> groupMap = allotDataList.stream().collect(Collectors.groupingBy(AllotData::getTrainsetId));
                if(!CollectionUtils.isEmpty(groupMap)){
                    groupMap.forEach((trainsetId,groupList)->{
                        //4.获取所有的item
                        List<XzyMTaskallotItem> totalItemList = groupList.stream().filter(t -> !ObjectUtils.isEmpty(t.getPacket()) && !CollectionUtils.isEmpty(t.getPacket().getTaskallotItemList())).flatMap(v -> v.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                        if(!CollectionUtils.isEmpty(totalItemList)){
                            //5.将项目根据包信息进行分组
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
                            //6.循环根据包信息分组的map组装allotdata数据
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
     * @author: 冯帅
     * @desc: 调用计划服务，计划中项目状态回写+工长认领故障和复核任务回写计划
     * @date: 2021/9/10
     * @param: [allotDataList]
     * @return: boolean
     */
    public boolean setTaskDataAndStatus(List<AllotData> allotDataList){
        String unitCode = "";//运用所编码
        String deptCode = "";//班组编码
        String deptName = "";//班组名称
        String dayPlanId = "";//日计划id
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = new ArrayList<>();
        List<RepairPacketStatu> repairPacketStatuList = new ArrayList<>();//更新计划中项目状态实体集合
        for (AllotData allotData : allotDataList) {
            XzyMTaskallotpacket packet = allotData.getPacket();//作业包
            List<XzyMTaskallotItem> taskallotItemList = new ArrayList<>();//作业包中的项目集合
            String trainSetId = allotData.getTrainsetId();//车组ID
            String trainSetName = allotData.getTrainsetName();//车组名称
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
                ztTaskPacketEntity.setRemark("工长认领");
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
                    //如果是故障/复核任务的新项目，就组织添加故障/复核接口的实体数据
                    if(("5".equals(allotData.getWorkTypeCode())|| "11".equals(allotData.getWorkTypeCode()))&&
                        "1".equals(taskallotItem.getNewItem())&&!CollectionUtils.isEmpty(xzyMTaskcarpartList)){
                        for (XzyMTaskcarpart xzyMTaskcarpart : xzyMTaskcarpartList) {
                            ZtTaskItemEntity ztTaskItemEntity = new ZtTaskItemEntity();
                            ztTaskItemEntity.setTrainsetId(trainSetId);
                            ztTaskItemEntity.setTrainsetName(trainSetName);
                            ztTaskItemEntity.setDayplanId(xzyMTaskcarpart.getDayPlanID());
                            ztTaskPacketEntity.setDayplanId(xzyMTaskcarpart.getDayPlanID());//作业包信息实体日计划赋值
                            ztTaskItemEntity.setItemCode(xzyMTaskcarpart.getItemCode());
                            ztTaskItemEntity.setItemName(xzyMTaskcarpart.getItemName());
                            ztTaskItemEntity.setItemType(xzyMTaskcarpart.getRepairType());
                            ztTaskItemEntity.setArrangeType(xzyMTaskcarpart.getArrageType());
                            ztTaskItemEntity.setCarNo(xzyMTaskcarpart.getCarNo());
                            ztTaskItemEntity.setRemark("工长认领");
                            ztTaskItemEntity.setDepotCode(unitCode);
                            ztTaskItemEntity.setTaskId(packet.getStaskId());
                            ztTaskItemEntity.setStatus("4");
                            lstTaskItemInfo.add(ztTaskItemEntity);
                        }
                    }else{//如果是故障/复核之外的其它项目，并且没有新项目，则组织更新计划状态实体
                        RepairPacketStatu repairPacketStatu = new RepairPacketStatu();
                        repairPacketStatu.setSTrainsetid(allotData.getTrainsetId());
                        repairPacketStatu.setSDayplanid(dayPlanId);
                        repairPacketStatu.setSItemcode(taskallotItem.getItemCode());
                        repairPacketStatu.setSDeptcode(unitCode);
                        repairPacketStatu.setSPacketcode(allotData.getPacket().getPacketCode());
                        repairPacketStatu.setCStatue("4");//已派工状态
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
        //调用计划服务-批量添加故障复核任务接口
        try {
            //将没有新项目的实体过滤掉
            if(!CollectionUtils.isEmpty(ztTaskPacketEntityList)){
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t->!CollectionUtils.isEmpty(t.getLstTaskItemInfo())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(ztTaskPacketEntityList)){
                    JSONObject addRes = remoteService.addFaultReCheckPacketList(ztTaskPacketEntityList);
                }
            }
        } catch (Exception ex) {
            logger.error("调用计划批量添加故障复核任务接口失败:" + ex.getMessage());
        }
        //调用计划服务-更新任务状态接口
        try {
            if(!CollectionUtils.isEmpty(repairPacketStatuList)){
                JSONObject updateRes = remoteService.updateRepairPacketStatus(repairPacketStatuList);
            }
        } catch (Exception ex) {
            logger.error("调用计划更新任务状态接口失败:" + ex.getMessage());
        }
        return true;
    }


    public List<AllotData> getAllotData(String mode, String str) {
        List<AllotData> allotDataList = JSONArray.parseArray(JSON.toJSONString(JSON.parse(str)), AllotData.class);
        if (mode.equals("2")) {
            List<AllotData> clearList = new ArrayList<>();
            //查看作业任务等于其他的
            //allotData其他的 data真实的
            for (AllotData allotData : allotDataList) {
                if (allotData.getWorkTaskName().equals("其他")) {
                    //根据父id区分
                    for (AllotData data : allotDataList) {
                        if (data.getId().equals(allotData.getFatherId())) {
                            clearList.add(allotData);
                            //xzyMTaskallotItem项目其他
                            int i = 0;
                            for (XzyMTaskallotItem xzyMTaskallotItem : allotData.getPacket().getTaskallotItemList()) {
                                //mTaskallotItem本项目
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
                                                //得到其他的辆续
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

    // 一级修的情况
    public List<XzyMTaskallotItem> getOne(List<XzyMTaskcarpart> xzyMTaskcarpartList, ZtTaskPacketEntity task, ZtTaskItemEntity item, String unitCode, String onlyPacketid, String trainsetType, String marshalcount, List<XzyCOneallotCarConfig> xzyCOneallotCarConfigList, List<XzyCOneallotTemplate> templateList) {
        String dayPlanId = item.getDayplanId();
        String trainsetId = item.getTrainsetId();
        String itemCode = item.getItemCode();
        String carno = item.getCarNo();
        String partName = item.getPositionName();
        String deptCode = task.getRepairDeptCode();
        String deptName = task.getRepairDeptName();
        List<XzyMTaskcarpart> carpartList = new ArrayList<>();
        if (StringUtils.isNotEmpty(carno) && !carno.equals("全列")) {
            carpartList = xzyMTaskcarpartList.stream().filter(t -> dayPlanId.equals(t.getDayPlanID()) && trainsetId.equals(t.getTrainsetId()) && itemCode.equals(t.getItemCode()) && carno.equals(t.getCarNo())).collect(Collectors.toList());
        } else {
            carpartList = xzyMTaskcarpartList.stream().filter(t -> dayPlanId.equals(t.getDayPlanID()) && trainsetId.equals(t.getTrainsetId()) && itemCode.equals(t.getItemCode())).collect(Collectors.toList());
        }
        // 根据作业包中的车组id查询车组履历，获取编组数量
        int marshalcountInt = Integer.parseInt(marshalcount);
        LinkedList<String> carNos = new LinkedList<String>();
        for (int index = 1; index < marshalcountInt; index++) {
            carNos.add("0" + index);
        }
        carNos.add("00");
        // 根据编组数量查询一级修派工配置模板
        if(xzyCOneallotCarConfigList!=null&&xzyCOneallotCarConfigList.size()>0){
            xzyCOneallotCarConfigList = xzyCOneallotCarConfigList.stream().filter(t -> marshalcount.equals(t.getMarshalNum())).collect(Collectors.toList());
            //用户自定义的配置
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
                // 现在构造XzyMTaskcarpart
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
                            //等于空的情况
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
        } else {// 没有配置的情况
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

    // 根据作业包中的车组id查询车组履历，获取编组数量
    public TrainsetInfo getTrainsetDetialInfo(String trainsetID) {
        logger.info("准备调用履历接口获取编组数量...");
        logger.info("接口：getTrainsetDetialInfo，参数：trainsetid = " + trainsetID);
        TrainsetInfo trainsetInfo = remoteService.getTrainsetDetialInfo(trainsetID);
        logger.info("调用履历接口获取车组信息完成...");
//        String marshalcount = trainsetInfo.getIMarshalcount();
//        logger.trace("根据车组id获取履历,获取到编组数量："+marshalcount);
        logger.info("获取编组数量完成");
        return trainsetInfo;
    }

    // 获取模版辆序
    List<XzyCOneallotTemplate> getOneAllotTemplates(String templateid) {
        Map<String, String> map = new HashMap<>();
        logger.info("准备调用获取派工模板辆序接口...");
        map.put("templateid", templateid);
        List<XzyCOneallotTemplate> xzyCOneallotTemplateList = new ArrayList<>();
        try {
            //fs
            xzyCOneallotTemplateList = xzyCOneallotConfigService.getOneAllotTemplates(map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("获取派工模板辆序失败，接口：getOneAllotTemplates，" + templateid);
        }
        logger.info("获取派工配置模板辆序，数量：" + xzyCOneallotTemplateList.size());
        logger.info("获取派工模板辆序完成");
        return xzyCOneallotTemplateList;
    }

    // 根据编组数量查询一级修派工配置模板
    public List<XzyCOneallotCarConfig> getTrainsetDetialInfo(String marshalcount, String unitCode, String deptCode) {
        logger.info("准备调用获取派工配置接口...");
        Map<String, String> map = new HashMap<>();
        map.put("marshalNum", marshalcount);
        map.put("unitCode", unitCode);
        map.put("deptCode", deptCode);
        List<XzyCOneallotCarConfig> xzyCOneallotTemplateList = new ArrayList<>();
        try {
            xzyCOneallotTemplateList = xzyCOneallotConfigService.getOneAllotConfigs(map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("获取派工配置失败，接口：getOneAllotConfigs" + unitCode + "," + deptCode + "," + marshalcount);
        }
        logger.info("获取派工配置，数量：" + xzyCOneallotTemplateList.size());
        logger.info("派工配置获取完成");
        return xzyCOneallotTemplateList;
    }

    //获取一级修派工配置信息
    public List<XzyCOneallotCarConfig> getOneAllotConfigList(String unitCode, String deptCode){
        List<XzyCOneallotCarConfig> xzyCOneallotCarConfigList = new ArrayList<>();
        try {
            xzyCOneallotCarConfigList = xzyCOneallotConfigService.getOneAllotConfigList(unitCode,deptCode);
        }catch (Exception ex){
            logger.error(ex.getMessage());
        }
        return xzyCOneallotCarConfigList;
    }


    // 获取派工字典
    public List<AllotTypeDict> getAllotTypeDict() {
        List<AllotTypeDict> allotTypeDictList = new ArrayList<>();
        AllotTypeDict allotTypeDict = new AllotTypeDict();
        allotTypeDict.setCode("0");
        allotTypeDict.setName("未派工");
        allotTypeDictList.add(allotTypeDict);
        AllotTypeDict allotTypeDict3 = new AllotTypeDict();
        allotTypeDict3.setCode("3");
        allotTypeDict3.setName("已分配");
        allotTypeDictList.add(allotTypeDict3);
        AllotTypeDict allotTypeDict2 = new AllotTypeDict();
        allotTypeDict2.setCode("2");
        allotTypeDict2.setName("部分派工");
        allotTypeDictList.add(allotTypeDict2);
        AllotTypeDict allotTypeDict1 = new AllotTypeDict();
        allotTypeDict1.setCode("1");
        allotTypeDict1.setName("已派工");
        allotTypeDictList.add(allotTypeDict1);
        return allotTypeDictList;
    }

    public List<ZtTaskPacketEntity> getTaskPacketEntityList(String dayplanId, String unitCode, String deptCode) {
        List<AllotData> allotDataList = new ArrayList<>();
        logger.info("准备调用获取计划任务接口...");
        MultiValueMap<String, String> querypacketMap = new LinkedMultiValueMap<>();
        Map<String, String> packetMap = new HashMap<>();
        packetMap.put("dayPlanId", dayplanId);
        packetMap.put("lstPacketTypeCode", null);
        packetMap.put("repairDeptCode", deptCode);
        packetMap.put("deptCode", unitCode);
        packetMap.put("trainsetId", null);
        querypacketMap.setAll(packetMap);
        List<JSONObject> jsonObjects = new ArrayList<>();
        logger.info("接口：getPacketTaskByCondition，参数：" + querypacketMap);
        try {
            jsonObjects = new RestRequestKitUseLogger<List<JSONObject>>(RepairTaskId, logger) {
            }.postObjectByForm("/Task/getPacketTaskByCondition", querypacketMap);
        } catch (Exception e) {
            logger.info("调用获取计划任务接口失败", e);
        }
        List<ZtTaskPacketEntity> taskPacketEntityList = JSONArray.parseArray(jsonObjects.toString(),
            ZtTaskPacketEntity.class);
        logger.trace("调用获取计划任务接口，获取到计划包：" + jsonObjects);
        logger.info("获取计划完成");
        return taskPacketEntityList;
    }

    // 查询 xzy_m_taskcarpart 按照 dayPlanID unitCode deptCode 获取任务部件辆序数据
    public List<XzyMTaskcarpart> getMTaskcarpartList(String dayplanId, String unitCode, String deptCode) {
        logger.info("准备调用获取任务部件辆序接口...");
        Map<String, String> queryCarPartMap = new HashMap<>();
        queryCarPartMap.put("dayPlanID", dayplanId);
        queryCarPartMap.put("workTeamID", deptCode);
        queryCarPartMap.put("unitCode", unitCode);
        List<XzyMTaskcarpart> xzyMTaskcarpartList = taskcarpartService.getCarPartLists(queryCarPartMap);
        logger.info("获取任务部件辆序，数量：" + xzyMTaskcarpartList.size());
        logger.info("任务部件辆序获取完成");
        return xzyMTaskcarpartList;
    }

    // 获取根据条件获取派工部门
    public List<XzyMTaskallotdept> getTaskAllotDepts(List<String> deptids) {
        logger.info("准备调用获取派工部门接口...");
        String selectStr = "('" + String.join("','", deptids) + "')";
        Map<String, String> queryCarPartMap = new HashMap<>();
        queryCarPartMap.put("deptids", selectStr);
        List<XzyMTaskallotdept> taskallotdeptList = new ArrayList<>();
        try {
            taskallotdeptList = taskAllotDeptService.getTaskAllotDepts(queryCarPartMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("获取派工部门失败，接口：getTaskAllotDepts，" + selectStr);
        }
        logger.info("获取派工部门，数量：" + taskallotdeptList.size());
        logger.info("派工部门获取完成");
        return taskallotdeptList;
    }


    /***
     * @author: 根据二级修派工配置，将二级修包显示方式拆分为专业分工模式
     * @date: 2022/3/7
     * @param: [allotDatas：派工数据]
     */
    public List<AllotData> getDivisionWorkTwo(List<AllotData> allotDataList) {
        logger.info("getDivisionWorkTwo开始处理二级修专业分工项目拆分");
        List<AllotData> allotDataListNew = new ArrayList<>();
        //1.查询配置表专业分工配置包类型
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("TASKALLOT");
        configParamsModel.setName("AGGPACKETTYPE");
        List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
        XzyCConfig xzyCConfig = Optional.ofNullable(configs).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
        //2.将二级修包之外的包剔除出来并添加到新的返回结果中取
        if(!CollectionUtils.isEmpty(allotDataList)){
            //2.1将二级修包之外的包过滤出来
            List<AllotData> notTwoAllotDataList = allotDataList.stream().filter(t -> !"2".equals(t.getTaskRepairCode())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(notTwoAllotDataList)){
                //2.2将过滤出来的集合添加到新的返回结果中
                allotDataListNew.addAll(notTwoAllotDataList);
                //2.3将原集合中这些数据剔除出去
                allotDataList.removeAll(notTwoAllotDataList);
            }
            //3.将二级修包集合根据车组id进行分组
            Map<String, List<AllotData>> groupMap = allotDataList.stream().collect(Collectors.groupingBy(AllotData::getTrainsetId));
            if(!CollectionUtils.isEmpty(groupMap)){
                //4.循环分组，根据车组id拿到专业分工配置
                groupMap.forEach((trainsetId, groupList) -> {
                    if(!ObjectUtils.isEmpty(xzyCConfig)){
                        //4.1获取专业分工包类型编码
                        String aggPacketType = xzyCConfig.getParamValue();
                        //4.2取分组集合集合的第一个，用来组织作业包相关信息
                        AllotData allotData = groupList.stream().findFirst().orElse(null);
                        XzyMTaskallotpacket packet = allotData.getPacket();
                        //4.3根据车组id调用履历接口，拿到车型、批次信息
                        String trainsetType = "";
                        String trainsetBatch = "";
                        if(!ObjectUtils.isEmpty(allotData)){
                            //4.4调用履历接口获取车组的车型和批次
                            TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
                            if(!ObjectUtils.isEmpty(trainsetBaseInfo)){
                                trainsetType = trainsetBaseInfo.getTraintype();
                                trainsetBatch = trainsetBaseInfo.getTraintempid();
                            }
                            //4.5在获取到的车型和批次不为空的情况下，取根据车型批次获取专业分工的配置
                            if(!ObjectUtils.isEmpty(trainsetType)&&!ObjectUtils.isEmpty(trainsetBatch)){
                                AggItemConfigModel aggItemConfig = new AggItemConfigModel();
                                aggItemConfig.setTrainType(trainsetType);
                                aggItemConfig.setStageId(trainsetBatch);
                                List<AggItemConfigModel> aggConfigList = iAggItemConfigService.getAggItemConfigList(aggItemConfig);
                                //5.如果专业分工不为空的话，则循环专业分工配置，组织专业分工的包
                                if(!CollectionUtils.isEmpty(aggConfigList)){
                                    //5.1获取该车下所有的二级修项目集合
                                    List<XzyMTaskallotItem> totalItemList = groupList.stream().filter(v->!ObjectUtils.isEmpty(v.getPacket())&&!CollectionUtils.isEmpty(v.getPacket().getTaskallotItemList())).flatMap(t -> t.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                                    if(!CollectionUtils.isEmpty(totalItemList)){
                                        //5.2一个项目可能会在多个专业分工配置中（辆序不同），所以循环该车型批次下所有的专业分工配置，组织专业分工包
                                        for(AggItemConfigModel aggConfigModel:aggConfigList){
                                            //5.3获取配置的辆序集合
                                            String cars = aggConfigModel.getAggPacketCar();
                                            List<String> carList = new ArrayList<>();
                                            if(!ObjectUtils.isEmpty(cars)){
                                                String[] carArray = cars.split(",");
                                                carList = CollectionUtils.arrayToList(carArray);
                                            }
                                            //5.4获取专业分工配置的项目集合
                                            List<AggItemConfigItem> aggConfigItemList = aggConfigModel.getAggItemConfigItems();
                                            //5.5获取配置的专业分工信息
                                            String aggPacketCode = aggConfigModel.getAggPacketCode();//专业分工包编码
                                            String aggPacketName = aggConfigModel.getAggPacketName();//专业分工包名称
                                            if(!CollectionUtils.isEmpty(aggConfigItemList)&&!CollectionUtils.isEmpty(carList)){
                                                //5.6组织专业分工对象
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
                                                List<XzyMTaskallotItem> divisionWorkItemList = new ArrayList<>();//定义专业分工项目集合
                                                //5.7 循环所有的项目
                                                Integer exitNum = 0;//在专业分工中存在的项目数量
                                                if(!CollectionUtils.isEmpty(totalItemList)){
                                                    Iterator<XzyMTaskallotItem> itemIterator = totalItemList.iterator();
                                                    while (itemIterator.hasNext()){
                                                        XzyMTaskallotItem allotItem = itemIterator.next();
                                                        String itemCode = allotItem.getItemCode();
                                                        String itemName = allotItem.getItemName();
                                                        boolean exitDivision = aggConfigItemList.stream().anyMatch(t -> itemCode.equals(t.getItemCode()) && itemName.equals(t.getItemName()));
                                                        //5.8如果该项目在专业分工项目配置中存在，则将该项目放加到专业分工项目集合中
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
                                                                            //5.9放到专业分工项目集合中的辆序就从所有辆序集合中移除掉
                                                                            iterator.remove();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            divisionWorkItem.setXzyMTaskcarpartList(divisionCarpartList);
                                                            divisionWorkItem.setId(UUID.randomUUID().toString());
                                                            //5.10设置项目级别的派工人员信息
                                                            List<XzyMTaskallotperson> divisionItemWorkerList = divisionCarpartList.stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                            List<XzyMTaskallotperson> distinctDivisionItemWorkerList = CommonUtils.getDistinctList(divisionItemWorkerList, XzyMTaskallotperson::getWorkerID);
                                                            divisionWorkItem.setWorkerList(distinctDivisionItemWorkerList);
                                                            divisionWorkItemList.add(divisionWorkItem);
                                                            //5.11如果该项目下所有的辆序都被移到专业分工项目里了，那么将此项目从总项目集合中移除掉
                                                            if(CollectionUtils.isEmpty(allotItem.getXzyMTaskcarpartList())){
                                                                itemIterator.remove();
                                                            }
                                                        }
                                                    }
                                                    //5.12设置该专业分工包，包级别的派工人员信息
                                                    divisionWorkData.getPacket().setTaskallotItemList(divisionWorkItemList);
                                                    List<XzyMTaskallotperson> divisionWrokerList = divisionWorkData.getPacket().getTaskallotItemList().stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                    List<XzyMTaskallotperson> distinctDivisionWorkerList = CommonUtils.getDistinctList(divisionWrokerList, XzyMTaskallotperson::getWorkerID);
                                                    divisionWorkData.setWorkerList(distinctDivisionWorkerList);
                                                    String allotStateCode = this.getAllotStateCode(divisionWorkItemList);
                                                    divisionWorkData.setAllotStateCode(allotStateCode);
                                                    if("0".equals(allotStateCode)){
                                                        divisionWorkData.setAllotStateName("未派工");
                                                    }else if("1".equals(allotStateCode)){
                                                        divisionWorkData.setAllotStateName("已派工");
                                                    }else{
                                                        divisionWorkData.setAllotStateName("部分派工");
                                                    }
                                                    if(exitNum>0){//该车下边所有的二级修项目，在专业分工项目配置中存在1个及以上，即添加专业分工的包
                                                        allotDataListNew.add(divisionWorkData);
                                                    }
                                                }
                                            }
                                        }
                                        //6.组织"其他"包
                                        if(!CollectionUtils.isEmpty(totalItemList)){
                                            //6.1一个车型批次下有可能有多个"专业分工"包，但只能有一个“其他”包，所以"其他"包在专业分工配置集合循环的外边组织
                                            AllotData otherData = new AllotData();
                                            BeanUtils.copyProperties(allotData,otherData);
                                            XzyMTaskallotpacket otherPacket = new XzyMTaskallotpacket();
                                            otherPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                            otherPacket.setStaskId(packet.getStaskId());
                                            otherPacket.setMainCyc(packet.getMainCyc());
                                            //6.2所有专业分工的包编码拼接作为"其他"包的包编码
                                            List<String> packetCodeList = aggConfigList.stream().map(t -> t.getAggPacketCode()).collect(Collectors.toList());
                                            if(!CollectionUtils.isEmpty(packetCodeList)){
                                                otherPacket.setPacketCode(String.join("_",packetCodeList));
                                            }
                                            otherPacket.setPacketName("其他");
                                            otherPacket.setPacketType(aggPacketType);
                                            otherPacket.setTaskallotItemList(null);
                                            otherData.setId(UUID.randomUUID().toString());
                                            otherData.setPacket(otherPacket);
                                            otherData.setWorkTaskName("其他");
                                            otherData.setWorkTypeCode(aggPacketType);
                                            //6.3设置"其他"包的项目派工人员及项目信息
                                            for(XzyMTaskallotItem otherItem :totalItemList){
                                                List<XzyMTaskcarpart> otherCarpartList = otherItem.getXzyMTaskcarpartList();
                                                if(!CollectionUtils.isEmpty(otherCarpartList)){
                                                    List<XzyMTaskallotperson> otherItemWorkerList = otherCarpartList.stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                                    List<XzyMTaskallotperson> distinctOtherItemWorkerList = CommonUtils.getDistinctList(otherItemWorkerList, XzyMTaskallotperson::getWorkerID);
                                                    otherItem.setWorkerList(distinctOtherItemWorkerList);
                                                }
                                            }
                                            otherData.getPacket().setTaskallotItemList(totalItemList);
                                            //6.4设置该车下"其他"包，包级别的派工人员信息
                                            List<XzyMTaskallotperson> otherWorkerList = totalItemList.stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                            List<XzyMTaskallotperson> distinctOtherWorkerList = CommonUtils.getDistinctList(otherWorkerList, XzyMTaskallotperson::getWorkerID);
                                            otherData.setWorkerList(distinctOtherWorkerList);
                                            //6.5重新计算"其他"包的派工状态
                                            String allotStateCode = this.getAllotStateCode(totalItemList);
                                            otherData.setAllotStateCode(allotStateCode);
                                            if("0".equals(allotStateCode)){
                                                otherData.setAllotStateName("未派工");
                                            }else if("1".equals(allotStateCode)){
                                                otherData.setAllotStateName("已派工");
                                            }else{
                                                otherData.setAllotStateName("部分派工");
                                            }
                                            if(!CollectionUtils.isEmpty(totalItemList)){//如果'其他'包中的项目集合不为空，才将'其他'包添加到包集合中
                                                allotDataListNew.add(otherData);
                                            }
                                        }
                                    }else{
                                        logger.info("getDivisionWorkTwo方法拆分专业分工——该车下边的所有二级修的包都没有二级修的项目");
                                        //不做处理，直接将该车下的二级修项目都添加到新的返回集合中
                                        allotDataListNew.addAll(groupList);
                                    }
                                    /** 因一个车型批次下有可能有多个专业分工配置，且一个项目可以在多个专业分工配置中存在（辆序不同），固将此段代码暂时注释掉
                                    AggItemConfigModel aggConfigModel = aggConfigList.get(0);
                                    if(!ObjectUtils.isEmpty(aggConfigModel)){
                                        //3.3获取配置的辆序集合
                                        String cars = aggConfigModel.getAggPacketCar();
                                        List<String> carList = new ArrayList<>();
                                        if(!ObjectUtils.isEmpty(cars)){
                                            String[] carArray = cars.split(",");
                                            carList = CollectionUtils.arrayToList(carArray);
                                        }
                                        //3.3获取配置的项目集合
                                        List<AggItemConfigItem> aggConfigItemList = aggConfigModel.getAggItemConfigItems();
                                        //3.4获取配置的专业分工信息集合
                                        String aggPacketCode = aggConfigModel.getAggPacketCode();//专业分工包编码
                                        String aggPacketName = aggConfigModel.getAggPacketName();//专业分工包名称
                                        String aggPacketType = xzyCConfig.getParamValue();
                                        if(!CollectionUtils.isEmpty(aggConfigItemList)&&!CollectionUtils.isEmpty(carList)){
                                            //4.组织专业分工和其他对象
                                            //4.1定义"专业分工"对象,并将他的项目对象集合置空
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
                                            //4.2"其他"对象,并将他的项目对象集合置空
                                            AllotData otherData = new AllotData();
                                            BeanUtils.copyProperties(allotData,otherData);
                                            XzyMTaskallotpacket otherPacket = new XzyMTaskallotpacket();
                                            otherPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                            otherPacket.setStaskId(packet.getStaskId());
                                            otherPacket.setMainCyc(packet.getMainCyc());
                                            otherPacket.setPacketCode(aggPacketCode);
                                            otherPacket.setPacketName("其他");
                                            otherPacket.setPacketType(aggPacketType);
                                            otherPacket.setTaskallotItemList(null);
                                            otherData.setId(UUID.randomUUID().toString());
                                            otherData.setPacket(otherPacket);
                                            otherData.setWorkTaskName("其他");
                                            otherData.setWorkTypeCode(aggPacketType);
                                            //4.3定义"专业分工"和"其他"项目集合
                                            List<XzyMTaskallotItem> divisionWorkItemList = new ArrayList<>();
                                            List<XzyMTaskallotItem> otherItemList = new ArrayList<>();
                                            //4.4获取该车下所有的二级修项目集合
                                            List<XzyMTaskallotItem> totalItemList = groupList.stream().filter(v->!ObjectUtils.isEmpty(v.getPacket())&&!CollectionUtils.isEmpty(v.getPacket().getTaskallotItemList())).flatMap(t -> t.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                                            if(!CollectionUtils.isEmpty(totalItemList)){
                                                //4.5循环所有的项目
                                                Integer exitNum = 0;//在专业分工中存在的项目数量
                                                for(XzyMTaskallotItem allotItem:totalItemList){
                                                    String itemCode = allotItem.getItemCode();
                                                    String itemName = allotItem.getItemName();
                                                    boolean exitDivision = aggConfigItemList.stream().anyMatch(t -> itemCode.equals(t.getItemCode()) && itemName.equals(t.getItemName()));
                                                    //4.6如果该项目在专业分工项目配置中存在，则将该项目放加到专业分工项目集合中，否则放到其他项目集合中
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
                                                        //4.7如果其他项目辆序集合不为空，则表示并不是所有的辆序都放在专业分工项目中，需要新建项目对象，将这些其他的辆序集合放到其他项目下
                                                        if(!CollectionUtils.isEmpty(otherCarpartList)){
                                                            XzyMTaskallotItem otherItem = new XzyMTaskallotItem();
                                                            BeanUtils.copyProperties(allotItem,otherItem);
                                                            otherItem.setXzyMTaskcarpartList(otherCarpartList);
                                                            otherItemList.add(otherItem);
                                                        }
                                                    }else{//如果该项目在专业分工项目配置中不存在，则将此项目直接添加到"其他"项目集合中
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
                                                if(exitNum>0){//该车下边所有的二级修项目，在专业分工项目配置中存在1个及以上，即添加专业分工的包
                                                    allotDataListNew.add(divisionWorkData);
                                                }
                                                if(!CollectionUtils.isEmpty(otherItemList)){//如果'其他'包中的项目集合不为空，才将'其他'包添加到包集合中
                                                    allotDataListNew.add(otherData);
                                                }
                                            }else{
                                                logger.info("getDivisionWorkTwo方法拆分专业分工——该车下边的所有二级修的包都没有二级修的项目");
                                                //不做处理，直接将该车下的二级修项目都添加到新的返回集合中
                                                allotDataListNew.addAll(groupList);
                                            }
                                        }else{
                                            logger.info("getDivisionWorkTwo方法拆分专业分工——专业分工配置中的项目配置为空或辆序配置为空");
                                            //不做处理，直接将该车下的二级修项目都添加到新的返回集合中
                                            allotDataListNew.addAll(groupList);
                                        }
                                    }else{
                                        logger.info("根据车型批次获取到的专业分工配置集合第一个对象为空");
                                        //不做处理，直接将该车下的二级修项目都添加到新的返回集合中
                                        allotDataListNew.addAll(groupList);
                                    }**/
                                }else{
                                    logger.info("getDivisionWorkTwo方法拆分专业分工——根据车型批次获取到的专业分工配置集合为空");
                                    //将这个车下的所有二级修项目都放到一个"其他"包中
                                    AllotData otherData = new AllotData();
                                    BeanUtils.copyProperties(allotData,otherData);
                                    XzyMTaskallotpacket otherPacket = new XzyMTaskallotpacket();
                                    otherPacket.setTaskAllotPacketId(packet.getTaskAllotPacketId());
                                    otherPacket.setStaskId(packet.getStaskId());
                                    otherPacket.setMainCyc(packet.getMainCyc());
                                    otherPacket.setPacketCode("zyfg");
                                    otherPacket.setPacketName("其他");
                                    otherPacket.setPacketType(xzyCConfig.getParamValue());
                                    otherPacket.setTaskallotItemList(null);
                                    otherData.setId(UUID.randomUUID().toString());
                                    otherData.setPacket(otherPacket);
                                    otherData.setWorkTaskName("其他");
                                    otherData.setWorkTypeCode(xzyCConfig.getParamValue());
                                    List<XzyMTaskallotItem> otherAllItem = Optional.ofNullable(groupList).orElseGet(ArrayList::new).stream().flatMap(t -> t.getPacket().getTaskallotItemList().stream()).collect(Collectors.toList());
                                    otherPacket.setTaskallotItemList(otherAllItem);
                                    otherData.setPacket(otherPacket);
                                    List<XzyMTaskallotperson> otherWorkerList = otherData.getPacket().getTaskallotItemList().stream().flatMap(t -> t.getWorkerList().stream()).collect(Collectors.toList());
                                    List<XzyMTaskallotperson> distinctOtherWorkerList = CommonUtils.getDistinctList(otherWorkerList, XzyMTaskallotperson::getWorkerID);
                                    otherData.setWorkerList(distinctOtherWorkerList);
                                    //6.5重新计算"其他"包的派工状态
                                    String allotStateCode = this.getAllotStateCode(otherAllItem);
                                    otherData.setAllotStateCode(allotStateCode);
                                    if("0".equals(allotStateCode)){
                                        otherData.setAllotStateName("未派工");
                                    }else if("1".equals(allotStateCode)){
                                        otherData.setAllotStateName("已派工");
                                    }else{
                                        otherData.setAllotStateName("部分派工");
                                    }
                                    allotDataListNew.add(otherData);
                                }
                            }else{
                                logger.info("getDivisionWorkTwo方法拆分专业分工——车型或者批次为空");
                                //不做处理，直接将该车下的二级修项目都添加到新的返回集合中
                                allotDataListNew.addAll(groupList);
                            }
                        }else {
                            logger.info("getDivisionWorkTwo方法拆分专业分工——取分组集合集合的第一个为空");
                            //不做处理，直接将该车下的二级修项目都添加到新的返回集合中
                            allotDataListNew.addAll(groupList);
                        }
                    }else{
                        logger.info("getDivisionWorkTwo方法读取xzy_c_config配置表中专业分工包类型配置为空");
                        //不做处理，直接将该车下的二级修项目都添加到新的返回集合中
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
            //5.13重新计算该包的派工状态
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
                //根据车组号查询车型批次
                TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);

                AggItemConfigModel aggItemConfig = new AggItemConfigModel();
                aggItemConfig.setTrainType(trainsetBaseInfo.getTraintype());
                aggItemConfig.setStageId(trainsetBaseInfo.getTraintempid());
                List<AggItemConfigModel> aggItemConfigList = iAggItemConfigService.getAggItemConfigList(aggItemConfig);

                AllotData allotDataOther = new AllotData();
                allotDataOther.setShowMode(allotDataList.get(0).getShowMode());
                allotDataOther.setWorkTypeName(allotDataList.get(0).getWorkTypeName());
                allotDataOther.setTrainsetName(allotDataList.get(0).getTrainsetName());
                allotDataOther.setWorkTaskName("其他");
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
                    //专业分工项目
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
                                            //辆序
                                            XzyMTaskcarpart carPart = new XzyMTaskcarpart();
                                            BeanUtils.copyProperties(xzyMTaskcarpart,carPart);
                                            taskcarparts.add(carPart);
                                            //作业人员
                                            workList.addAll(xzyMTaskcarpart.getWorkerList());
                                        } else {
                                            //其他辆序
                                            XzyMTaskcarpart carPart = new XzyMTaskcarpart();
                                            BeanUtils.copyProperties(xzyMTaskcarpart,carPart);
                                            taskcarpartList.add(carPart);
                                            //其他作业人员
                                            otherWorkerList.addAll(xzyMTaskcarpart.getWorkerList());
                                        }
                                    }
                                }
                                //专业分工项目
                                getItemList(taskallotItemList, aggItemConfigItem, taskcarparts);
                                //其他项目
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
            allotData.setAllotStateName("未派工");
            allotData.setAllotStateCode("0");
        } else if (!CollectionUtils.isEmpty(workList) && collect.stream().anyMatch(t -> CollectionUtils.isEmpty(t.getWorkerList()))) {
            allotData.setAllotStateName("部分派工");
            allotData.setAllotStateCode("2");
        } else {
            allotData.setAllotStateName("已派工");
            allotData.setAllotStateCode("1");
        }
        List<XzyMTaskallotperson> workerList = workList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(XzyMTaskallotperson::getWorkerID))), ArrayList::new));
        allotData.setWorkerList(workerList);
    }

    // 设置查询条件
    public void setQuery(AllotData allotData, List<QueryTrainType> queryTrainTypeList) {
        String trainsetID = allotData.getTrainsetId();
        String trainsetName = allotData.getTrainsetName();
        String packetCode = allotData.getPacket().getPacketCode();
        String packetName = allotData.getPacket().getPacketName();
        // 查询车型条件
        List<QueryTrainType> queryTrainsetType = queryTrainTypeList.stream()
            .filter(t -> t.getTrainsetTypeCode().equals(allotData.getTrainsetTypeCode()))
            .collect(Collectors.toList());
        // 为空
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
        } else {// 不为空

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

    // 日计划没有项目的情况下
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
        // 日计划没有项目的情况下
        XzyMTaskallotItem taskallotItem = new XzyMTaskallotItem();
        taskallotItem.setItemCode(task.getPacketCode());
        taskallotItem.setItemName(task.getPacketName());
        // 设置派工任务
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
        carpart.setCarNo("全列");
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

    // 根据 作业包类型 转换 项目类型 ， 包类型 修程
    public String packetTypeChangeToItemType(String packetType, String maincyc) {

        if ("1".equals(packetType) && "1".equals(maincyc)) {
            return "0";
        } else {
            return packetType;
        }
    }

    // 日计划有项目的情况下
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
            carpart.setDisplayCarPartName("[辆序：" + carpart.getCarNo() + "]" + carpart.getItemName());
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

    // 获取派工人员
    public List<XzyMTaskallotperson> getPersons(List<String> processIds) {
        logger.info("准备调用获取派工人员接口...");
        TaskAllotPersonQueryParamModel taskAllotPersonQueryParamModel = new TaskAllotPersonQueryParamModel();
        taskAllotPersonQueryParamModel.setProcessIds(processIds);
        List<XzyMTaskallotperson> personsList = new ArrayList<>();
        try {
            personsList = taskAllotPersonService.getPersons(taskAllotPersonQueryParamModel);
        } catch (Exception e) {
            logger.error("获取派工人员失败，接口：getPersons", e);
        }
        logger.info("获取派工人员，数量：" + personsList.size());
        logger.info("派工人员获取完成");
        return personsList;
    }

    // 获取根据条件获取作业任务包
    public List<XzyMTaskallotpacket> getTaskAllotPackets(List<String> packetids) {
        logger.info("准备调用获取派工包接口...");
        String selectStr = "('" + String.join("','", packetids) + "')";
        Map<String, String> queryCarPartMap = new HashMap<>();
        queryCarPartMap.put("packetids", selectStr);
        List<XzyMTaskallotpacket> xzyMTaskallotpacketList = new ArrayList<>();
        List<JSONObject> getTaskAllotPacketsJson = new ArrayList<>();
        try {
            xzyMTaskallotpacketList = taskallotpacketService.getTaskAllotPackets(queryCarPartMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("获取派工包失败，接口：getTaskAllotPackets，" + selectStr);
        }
        logger.info("获取派工包，数量：" + xzyMTaskallotpacketList.size());
        logger.info("派工包获取完成");
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
        //1.调用中台接口获取数据
        JSONObject res = new RestRequestKitUseLogger<JSONObject>(midGroundServiceId, logger) {
        }.postObject("/xzyMTaskallot/getTaskAllotDataByDate", jsonObject);
        //2.组织数据
        List<TaskAllotPacketEntity> packetList = res.getJSONArray("data").toJavaList(TaskAllotPacketEntity.class);
        if (packetList != null && packetList.size() > 0) {
            packetList.stream().map(packetItem -> {//3.循环包集合
                List<TaskAllotItemEntity> itemList = packetItem.getTaskAllotItemEntityList();
                //4.将项目按照项目编码和项目名称分组
                Map<ProcessGroupKey, List<TaskAllotItemEntity>> groupList = itemList.stream().collect(Collectors.groupingBy(v -> {
                    ProcessGroupKey key = new ProcessGroupKey();
                    key.setItemCode(v.getItemCode());
                    key.setItemName(v.getItemName());
                    return key;
                }));
                //5.循环分组
                groupList.forEach((processGroupKey, groupItem) -> {
                    List<QueryTaskAllot> queryTaskAllots = new ArrayList<>();
                    Map<String, TaskAllotPersonEntity> personMap = new HashMap<>();
                    //6.循环项目分组集合
                    groupItem.stream().map(item -> {
                        List<TaskAllotPersonEntity> personList = item.getTaskAllotPersonEntityList();
                        //7.循环人员集合
                        personList.stream().map(personItem -> {
                            //将去重的人员拿出来
                            personMap.put(personItem.getWorkerId(), personItem);
                            return personItem;
                        }).collect(Collectors.toList());
                        return item;
                    }).collect(Collectors.toList());
                    //8.循环去重人员
                    for (String key : personMap.keySet()) {
                        TaskAllotPersonEntity personEntity = personMap.get(key);
                        List<TaskAllotItemEntity> filterItemList = groupItem.stream().filter(t -> t.getTaskAllotPersonEntityList().contains(personEntity)).collect(Collectors.toList());
                        //组织返回对象
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
                        //将该人员的岗位集合根据岗位名称排下序，然后拼接到人员名称后边
                        List<TaskAllotPersonPostEntity> postEntityList = personEntity.getTaskAllotPersonPostEntityList();
                        String workerNameAndPost = personEntity.getWorkerName();
                        if(!CollectionUtils.isEmpty(postEntityList)){
                            postEntityList = postEntityList.stream().sorted(Comparator.comparing(TaskAllotPersonPostEntity::getPostId)).collect(Collectors.toList());
                            List<String> postNameList = postEntityList.stream().map(t -> t.getPostName()).collect(Collectors.toList());
                            String postNameStrs = StringUtils.join(postNameList,",");
                            workerNameAndPost +="（"+postNameStrs+"）";
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
                        //组织返回对象
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
