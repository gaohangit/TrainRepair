package com.ydzbinfo.emis.common.taskAllot.service.impl;

import com.jxdinfo.hussar.common.userutil.UserUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.common.taskAllot.dao.TaskAllotPersonPostMapper;
import com.ydzbinfo.emis.common.taskAllot.dao.XzyMTaskAllotMapper;
import com.ydzbinfo.emis.common.taskAllot.service.*;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotItemEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonPostEntity;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.hussar.system.bsp.organ.SysStaff;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.inParam;

@Service
public class TaskAllotServiceImpl implements XzyMTaskAllotService {
    //日志
    private final static Logger logger = LoggerFactory.getLogger(TaskAllotServiceImpl.class);

    @Autowired
    XzyMTaskAllotMapper xzyMTaskAllotMapper;

    //派工包表服务
    @Autowired
    ITaskAllotPacketService taskAllotPacketService;

    //派工部门表服务
    @Autowired
    ITaskAllotDeptService taskAllotDeptService;

    //派工辆序表服务
    @Autowired
    ITaskCarPartService taskCarPartService;

    //派工人员表服务
    @Autowired
    ITaskAllotPersonService taskAllotPersonService;

    //派工岗位表服务
    @Autowired
    ITaskAllotPersonPostService taskAllotPersonPostService;

    //派工岗位表服务
    @Autowired
    TaskAllotPersonPostMapper taskAllotPersonPostMapper;

    //派工包历史表服务
    @Autowired
    ITaskAllotPacketHisService taskAllotPacketHisService;

    //派工部门历史表服务
    @Autowired
    ITaskAllotDeptHisService taskAllotDeptHisService;

    //派工辆序历史表服务
    @Autowired
    ITaskCarPartHisService taskCarPartHisService;

    //派工人员历史表服务
    @Autowired
    ITaskAllotPersonHisService taskAllotPersonHisService;

    //派工岗位历史表服务
    @Autowired
    ITaskAllotPersonPostHisService taskAllotPersonPostHisService;

    @Autowired
    IRemoteService remoteService;
    //写旧系统派工数据
    @Autowired
    TaskPacketItem taskPacketItem;

    private Integer id = 1;


    /**
     * 获取派工数据
     */
    public List<TaskAllotPacketEntity> getTaskAllotData(String unitCode, String dayPlanId, String startDate, String endDate, String repairType, List<String> trainsetTypeList, List<String> trainsetIdList, List<String> itemCodeList, List<String> branchCodeList, List<String> workIdList, String packetName) {
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
        Date beginTime = new Date();
        taskAllotPacketEntityList = xzyMTaskAllotMapper.getTaskAllotData(unitCode, dayPlanId, startDate, endDate, repairType, trainsetTypeList, trainsetIdList, itemCodeList, branchCodeList, workIdList, MybatisOgnlUtils.replaceWildcardChars(packetName));
        Date time1 = new Date();
        logger.info("getTaskAllotData查询数据库消耗时间为：" + (time1.getTime() - beginTime.getTime()));
        //处理辆序排序问题，将00辆序排在最后边
        Date beginTime2 = new Date();
        if (!CollectionUtils.isEmpty(taskAllotPacketEntityList)) {
            taskAllotPacketEntityList.stream().map(packetEntity -> {
                List<TaskAllotItemEntity> taskAllotItemEntityList = packetEntity.getTaskAllotItemEntityList();
                if (!CollectionUtils.isEmpty(taskAllotItemEntityList)) {
                    List<TaskAllotItemEntity> filterItemList = taskAllotItemEntityList.stream().filter(t -> "00".equals(t.getCarNo())).collect(Collectors.toList());
                    Collections.sort(filterItemList, new Comparator<TaskAllotItemEntity>() {
                        @Override
                        public int compare(TaskAllotItemEntity o1, TaskAllotItemEntity o2) {
                            int res = 0;
                            if (!ObjectUtils.isEmpty(o1.getPartName()) && !ObjectUtils.isEmpty(o2.getPartName())) {
                                res = o1.getPartName().compareTo(o2.getPartName());
                            }
                            return res;
                        }
                    });
                    if (!CollectionUtils.isEmpty(filterItemList)) {
                        taskAllotItemEntityList.removeAll(filterItemList);
                        taskAllotItemEntityList.addAll(filterItemList);
                    }
                }
                return null;
            }).collect(Collectors.toList());
        }
        Date time2 = new Date();
        logger.info("getTaskAllotData数据排序消耗时间为：" + (time2.getTime() - time1.getTime()));
        logger.info("getTaskAllotData接口总消耗时间为：" + (time2.getTime() - beginTime.getTime()));
        return taskAllotPacketEntityList;
    }

    /**
     * 获取派工数据（到人）
     */
    public List<TaskAllotPacketEntity> getTaskAllotDataToPerson(String unitCode, String dayPlanId, String startDate, String endDate, String repairType, List<String> trainsetTypeList, List<String> trainsetIdList, List<String> itemCodeList, List<String> branchCodeList, List<String> workIdList, String packetName) {
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
        Date beginTime = new Date();
        taskAllotPacketEntityList = xzyMTaskAllotMapper.getTaskAllotDataToPerson(unitCode, dayPlanId, startDate, endDate, repairType, trainsetTypeList, trainsetIdList, itemCodeList, branchCodeList, workIdList, MybatisOgnlUtils.replaceWildcardChars(packetName));
        Date time1 = new Date();
        logger.info("getTaskAllotDataToPerson查询数据库消耗时间为：" + (time1.getTime() - beginTime.getTime()));
        //处理辆序排序问题，将00辆序排在最后边
        Date beginTime2 = new Date();
        if (!CollectionUtils.isEmpty(taskAllotPacketEntityList)) {
            taskAllotPacketEntityList.stream().map(packetEntity -> {
                List<TaskAllotItemEntity> taskAllotItemEntityList = packetEntity.getTaskAllotItemEntityList();
                if (!CollectionUtils.isEmpty(taskAllotItemEntityList)) {
                    List<TaskAllotItemEntity> filterItemList = new ArrayList<>();
                    //根据人员编码过滤
                    for(TaskAllotItemEntity itemEntity:taskAllotItemEntityList){
                        List<TaskAllotPersonEntity> personEntityList = itemEntity.getTaskAllotPersonEntityList();
                        if(!CollectionUtils.isEmpty(personEntityList)){
                            List<String> workIds = personEntityList.stream().map(v -> v.getWorkerId()).collect(Collectors.toList());
                            if(!CollectionUtils.isEmpty(workIds)&&!CollectionUtils.isEmpty(workIdList)){
                                //取交集
                                workIds.retainAll(workIdList);
                                if(!CollectionUtils.isEmpty(workIds)){
                                    filterItemList.add(itemEntity);
                                }
                            }
                        }
                    }
                    //将00辆序的项目过滤处理
                    filterItemList = taskAllotItemEntityList.stream().filter(t -> "00".equals(t.getCarNo())).collect(Collectors.toList());
                    Collections.sort(filterItemList, new Comparator<TaskAllotItemEntity>() {
                        @Override
                        public int compare(TaskAllotItemEntity o1, TaskAllotItemEntity o2) {
                            int res = 0;
                            if (!ObjectUtils.isEmpty(o1.getPartName()) && !ObjectUtils.isEmpty(o2.getPartName())) {
                                res = o1.getPartName().compareTo(o2.getPartName());
                            }
                            return res;
                        }
                    });
                    if (!CollectionUtils.isEmpty(filterItemList)) {
                        taskAllotItemEntityList.removeAll(filterItemList);
                        taskAllotItemEntityList.addAll(filterItemList);
                    }
                }
                return null;
            }).collect(Collectors.toList());
        }
        Date time2 = new Date();
        logger.info("getTaskAllotDataToPerson数据排序消耗时间为：" + (time2.getTime() - time1.getTime()));
        logger.info("getTaskAllotDataToPerson接口总消耗时间为：" + (time2.getTime() - beginTime.getTime()));
        return taskAllotPacketEntityList;
    }


    /**
     * 获取派工数据（到项目）
     */
    public List<TaskAllotPacketEntity> getTaskAllotDataToItem(String unitCode, String dayPlanId,String repairType, List<String> trainsetTypeList, List<String> trainsetIdList, List<String> itemCodeList, List<String> branchCodeList, String packetName) {
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
        Date beginTime = new Date();
        taskAllotPacketEntityList = xzyMTaskAllotMapper.getTaskAllotDataToItem(unitCode, dayPlanId, repairType, trainsetTypeList, trainsetIdList, itemCodeList, branchCodeList, MybatisOgnlUtils.replaceWildcardChars(packetName));
        Date time1 = new Date();
        logger.info("getTaskAllotDataToItem查询数据库消耗时间为：" + (time1.getTime() - beginTime.getTime()));
        //处理辆序排序问题，将00辆序排在最后边
        if (!CollectionUtils.isEmpty(taskAllotPacketEntityList)) {
            taskAllotPacketEntityList.stream().map(packetEntity -> {
                List<TaskAllotItemEntity> taskAllotItemEntityList = packetEntity.getTaskAllotItemEntityList();
                if (!CollectionUtils.isEmpty(taskAllotItemEntityList)) {
                    List<TaskAllotItemEntity> filterItemList = new ArrayList<>();
                    //将00辆序的项目过滤处理
                    filterItemList = taskAllotItemEntityList.stream().filter(t -> "00".equals(t.getCarNo())).collect(Collectors.toList());
                    Collections.sort(filterItemList, new Comparator<TaskAllotItemEntity>() {
                        @Override
                        public int compare(TaskAllotItemEntity o1, TaskAllotItemEntity o2) {
                            int res = 0;
                            if (!ObjectUtils.isEmpty(o1.getPartName()) && !ObjectUtils.isEmpty(o2.getPartName())) {
                                res = o1.getPartName().compareTo(o2.getPartName());
                            }
                            return res;
                        }
                    });
                    if (!CollectionUtils.isEmpty(filterItemList)) {
                        taskAllotItemEntityList.removeAll(filterItemList);
                        taskAllotItemEntityList.addAll(filterItemList);
                    }
                }
                return null;
            }).collect(Collectors.toList());
        }
        Date time2 = new Date();
        logger.info("getTaskAllotDataToItem数据排序消耗时间为：" + (time2.getTime() - time1.getTime()));
        logger.info("getTaskAllotDataToItem接口总消耗时间为：" + (time2.getTime() - beginTime.getTime()));
        return taskAllotPacketEntityList;
    }


    /**
     * 获取派工车组
     */
    @Override
    public List<String> getTaskAllotTrainsetList(String unitCode,String dayPlanId,String branchCode,String mainCyc){
        List<String> res = new ArrayList<>();
        res = xzyMTaskAllotMapper.getTaskAllotTrainsetList(unitCode, dayPlanId, branchCode, mainCyc);
        return res;
    }

    /**
     * 获取机检一级修派工车组
     */
    @Override
    public List<String> getTaskAllotEquipmentTrainsetList(String unitCode,String dayPlanId,String branchCode){
        List<String> res = new ArrayList<>();
        res = xzyMTaskAllotMapper.getTaskAllotEquipmentTrainsetList(unitCode, dayPlanId, branchCode);
        return res;
    }

    /**
     * 获取人员-岗位配置
     */
    @Override
    public List<PostModel> getPersonPostList(String unitCode,String deptCode,String branchCode){
        return xzyMTaskAllotMapper.getPersonPostList(unitCode,deptCode,branchCode);
    }



    /**
     * 插入派工数据——仅限一个运用所+班次+班组
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean setTaskAllotData(List<TaskAllotPacketEntity> taskAllotPacketList) {
        boolean insertRes = true;
        //1.删除已有数据
        //1.1获取日计划ID、运用所编码
        String dayPlanId = taskAllotPacketList.stream().findFirst().get().getDayPlanId();
        String unitCode = taskAllotPacketList.stream().findFirst().get().getUnitCode();
        String deptCode = taskAllotPacketList.stream().findFirst().get().getDeptCode();
        List<String> deptCodeList = new ArrayList<>();
        deptCodeList.add(deptCode);
        String publishCode = "";
        //1.2将要删除的辆序表数据查询出来（根据运用所编码、日计划ID、班组编码、班组名称）
        Date beginTime = new Date();
        List<TaskCarPart> delTaskCarPartList = xzyMTaskAllotMapper.getCarPartList(dayPlanId, unitCode, deptCodeList);
        if (delTaskCarPartList != null && delTaskCarPartList.size() > 0) {
            //派工编码集合
            publishCode = delTaskCarPartList.stream().filter(t->!ObjectUtils.isEmpty(t.getPublishCode())).map(t -> t.getPublishCode()).findFirst().orElse("");
            if (StringUtils.isNotBlank(publishCode)) {
                List<String> publishCodeList = new ArrayList<>();
                publishCodeList.add(publishCode);
                this.deleteTaskAllotDataByPublishCode(publishCodeList);
            }else{
                this.deleteTaskAllotDataById(delTaskCarPartList);
            }
        }
        Date time1 = new Date();
        logger.info("setTaskAllotData删除数据消耗的时间为：" + (time1.getTime() - beginTime.getTime()));

        //2.组织要插入的数据
        List<TaskAllotPacket> insertPacketList = new ArrayList<>();//插入派工包数据集合
        List<TaskCarPart> insertCarPartList = new ArrayList<>();//插入辆序表数据集合
        List<TaskAllotPerson> insertPersonList = new ArrayList<>();//插入派工人员表数据集合
        List<TaskAllotPersonPost> insertPostList = new ArrayList<>();//插入派工岗位表数据集合
        List<TaskAllotDept> insertDeptList = new ArrayList<>();//插入派工部门表数据集合

        List<TaskAllotPacketHis> insertPacketHisList = new ArrayList<>();//插入派工包历史表数据集合
        List<TaskCarPartHis> insertCarPartHisList = new ArrayList<>();//插入辆序历史表数据集合
        List<TaskAllotPersonHis> insertPersonHisList = new ArrayList<>();//插入派工人员历史表数据集合
        List<TaskAllotPersonPostHis> insertPostHisList = new ArrayList<>();//插入派工岗位历史表数据集合
        List<TaskAllotDeptHis> insertDeptHisList = new ArrayList<>();//插入派工部门历史表数据集合
        if(StringUtils.isBlank(publishCode)){
            publishCode = dayPlanId+unitCode+deptCode;
        }
        if (!CollectionUtils.isEmpty(taskAllotPacketList)) {
            for (TaskAllotPacketEntity packetItem : taskAllotPacketList) {
                //2.1派工包数据
                TaskAllotPacket insertPacketItem = new TaskAllotPacket();
                BeanUtils.copyProperties(packetItem,insertPacketItem);
                String insertPacketId = UUID.randomUUID().toString();
                insertPacketItem.setTaskAllotPacketId(insertPacketId);
                insertPacketItem.setPublishCode(publishCode);
                insertPacketList.add(insertPacketItem);
                //派工包历史表
                TaskAllotPacketHis taskAllotPacketHis = new TaskAllotPacketHis();
                BeanUtils.copyProperties(insertPacketItem, taskAllotPacketHis);
                insertPacketHisList.add(taskAllotPacketHis);
                //2.2派工部门数据
                TaskAllotDept insertDeptItem = new TaskAllotDept();
                String insertDeptId = UUID.randomUUID().toString();
                insertDeptItem.setTaskAllotDeptId(insertDeptId);
                insertDeptItem.setDeptCode(packetItem.getDeptCode());
                insertDeptItem.setDeptName(packetItem.getDeptName());
                insertDeptItem.setPublishCode(publishCode);
                insertDeptList.add(insertDeptItem);
                //派工部门历史表
                TaskAllotDeptHis taskAllotDeptHis = new TaskAllotDeptHis();
                BeanUtils.copyProperties(insertDeptItem, taskAllotDeptHis);
                insertDeptHisList.add(taskAllotDeptHis);
                //2.3派工辆序表数据
                List<TaskAllotItemEntity> taskAllotItemEntityList = packetItem.getTaskAllotItemEntityList();
                if (taskAllotItemEntityList != null && taskAllotItemEntityList.size() > 0) {
                    for (TaskAllotItemEntity allotItem : taskAllotItemEntityList) {
                        TaskCarPart insertCarPart = new TaskCarPart();
                        BeanUtils.copyProperties(allotItem,insertCarPart);
                        String insertProcessId = UUID.randomUUID().toString();
                        insertCarPart.setProcessId(insertProcessId);
                        insertCarPart.setTaskAllotPacketId(insertPacketId);
                        insertCarPart.setTaskAllotDeptId(insertDeptId);
                        insertCarPart.setDayPlanId(packetItem.getDayPlanId());
                        insertCarPart.setTrainsetId(packetItem.getTrainsetId());
                        insertCarPart.setTrainsetName(packetItem.getTrainsetName());
                        insertCarPart.setUnitCode(packetItem.getUnitCode());
                        insertCarPart.setUnitName(packetItem.getUnitName());
                        insertCarPart.setRepairType(allotItem.getItemType());
                        insertCarPart.setMainCyc(packetItem.getMainCyc());
                        insertCarPart.setRecordTime(packetItem.getRecordTime());
                        insertCarPart.setRecorderCode(packetItem.getRecordCode());
                        insertCarPart.setRecorderName(packetItem.getRecordName());
                        insertCarPart.setPublishCode(publishCode);
                        insertCarPartList.add(insertCarPart);
                        //派工辆序历史表
                        TaskCarPartHis taskCarPartHis = new TaskCarPartHis();
                        BeanUtils.copyProperties(insertCarPart, taskCarPartHis);
                        insertCarPartHisList.add(taskCarPartHis);
                        //2.4派工人员数据
                        List<TaskAllotPersonEntity> taskAllotPersonEntityList = allotItem.getTaskAllotPersonEntityList();
                        if (taskAllotPersonEntityList != null && taskAllotPersonEntityList.size() > 0) {
                            for (TaskAllotPersonEntity personItem : taskAllotPersonEntityList) {
                                TaskAllotPerson insertPersonItem = new TaskAllotPerson();
                                BeanUtils.copyProperties(personItem,insertPersonItem);
                                String isnertPersonId = UUID.randomUUID().toString();
                                insertPersonItem.setProcessId(insertProcessId);
                                insertPersonItem.setTaskAllotPersonId(isnertPersonId);
                                insertPersonItem.setPublishCode(publishCode);
                                insertPersonList.add(insertPersonItem);
                                //派工人员历史表
                                TaskAllotPersonHis taskAllotPersonHis = new TaskAllotPersonHis();
                                BeanUtils.copyProperties(insertPersonItem, taskAllotPersonHis);
                                insertPersonHisList.add(taskAllotPersonHis);
                                //2.5派工岗位数据
                                List<TaskAllotPersonPostEntity> taskAllotPersonPostEntityList = personItem.getTaskAllotPersonPostEntityList();
                                if (taskAllotPersonPostEntityList != null && taskAllotPersonPostEntityList.size() > 0) {
                                    for (TaskAllotPersonPostEntity postItem : taskAllotPersonPostEntityList) {
                                        TaskAllotPersonPost insertPost = new TaskAllotPersonPost();
                                        BeanUtils.copyProperties(postItem,insertPost);
                                        String id = UUID.randomUUID().toString();
                                        insertPost.setId(id);
                                        insertPost.setTaskAllotPersonId(isnertPersonId);
                                        insertPost.setPublishCode(publishCode);
                                        insertPostList.add(insertPost);
                                        //派工人员岗位历史表
                                        TaskAllotPersonPostHis taskAllotPersonPostHis = new TaskAllotPersonPostHis();
                                        BeanUtils.copyProperties(insertPost, taskAllotPersonPostHis);
                                        insertPostHisList.add(taskAllotPersonPostHis);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Date time2 = new Date();
        logger.info("setTaskAllotData组织数据消耗的时间为：" + (time2.getTime() - time1.getTime()));
        //3.插入数据
        if (insertPacketList.size() > 0) {
            //3.1插入派工包数据
            taskAllotPacketService.insertBatch(insertPacketList);
            //3.2插入派工包历史表数据
            taskAllotPacketHisService.insertBatch(insertPacketHisList);
        }
        if (insertDeptList.size() > 0) {
            //3.3插入派工部门数据
            taskAllotDeptService.insertBatch(insertDeptList);
            //3.4插入派工部门历史表数据
            taskAllotDeptHisService.insertBatch(insertDeptHisList);
        }
        if (insertCarPartList.size() > 0) {
            //3.5插入派工辆序数据
            taskCarPartService.insertBatch(insertCarPartList);
            //3.6插入派工辆序历史数据
            taskCarPartHisService.insertBatch(insertCarPartHisList);
        }
        if (insertPersonList.size() > 0) {
            //3.7插入派工人员数据
            taskAllotPersonService.insertBatch(insertPersonList);
            //3.8插入派工人员历史表数据
            taskAllotPersonHisService.insertBatch(insertPersonHisList);
        }
        if (insertPostList.size() > 0) {
            //3.9插入派工岗位数据
            taskAllotPersonPostService.insertBatch(insertPostList);
            //3.10插入派工岗位历史表数据
            taskAllotPersonPostHisService.insertBatch(insertPostHisList);
        }
        taskPacketItem.setTaskAllotItems(taskAllotPacketList);
        Date time3 = new Date();
        logger.info("setTaskAllotData插入数据消耗的时间为：" + (time3.getTime() - time2.getTime()));
        logger.info("setTaskAllotData本次接口调用总消耗时间为：" + (time3.getTime() - beginTime.getTime()));
        return insertRes;
    }

    /**
     * 插入派工数据——仅限机检一级修
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean setTaskAllotDataByDayPlanId(String dayPlanId) {
        boolean insertRes = true;
        if(!ObjectUtils.isEmpty(dayPlanId)){
            //1.获取当前运用所编码和运用所名称
            String unitCode = ContextUtils.getUnitCode();
            Date currentDate = new Date();
            //2.从计划接口获取机检一级修
            List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, null, "16", "", unitCode);
            if(!CollectionUtils.isEmpty(taskPacketEntityList)){
                //获取所有车组对应的车型
                List<String> trainsetIds = taskPacketEntityList.stream().map(t->t.getTrainsetId()).collect(Collectors.toList());
                List<TrainsetBaseInfo> trainsetBaseInfoList = new ArrayList<>();
                if(!CollectionUtils.isEmpty(trainsetIds)){
                    trainsetBaseInfoList = remoteService.getTrainsetBaseInfoByIds(trainsetIds);
                }
                //3.组织要插入的数据
                List<TaskAllotPacket> insertPacketList = new ArrayList<>();//插入派工包数据集合
                List<TaskCarPart> insertCarPartList = new ArrayList<>();//插入辆序表数据集合
                List<TaskAllotPerson> insertPersonList = new ArrayList<>();//插入派工人员表数据集合
                List<TaskAllotPersonPost> insertPostList = new ArrayList<>();//插入派工岗位表数据集合
                List<TaskAllotDept> insertDeptList = new ArrayList<>();//插入派工部门表数据集合
                List<TaskAllotPacketHis> insertPacketHisList = new ArrayList<>();//插入派工包历史表数据集合
                List<TaskCarPartHis> insertCarPartHisList = new ArrayList<>();//插入辆序历史表数据集合
                List<TaskAllotPersonHis> insertPersonHisList = new ArrayList<>();//插入派工人员历史表数据集合
                List<TaskAllotPersonPostHis> insertPostHisList = new ArrayList<>();//插入派工岗位历史表数据集合
                List<TaskAllotDeptHis> insertDeptHisList = new ArrayList<>();//插入派工部门历史表数据集合
                List<String> deptCodeList = taskPacketEntityList.stream().map(t->t.getRepairDeptCode()).collect(Collectors.toList());
                //4.从数据库中查询是否已经派工
                List<TaskCarPart> taskCarPartList = xzyMTaskAllotMapper.getCarPartList(dayPlanId, unitCode, deptCodeList);
                taskCarPartList = Optional.ofNullable(taskCarPartList).orElseGet(ArrayList::new).stream().filter(t->"16".equals(t.getRepairType())).collect(Collectors.toList());
                //5.循环计划包集合，如果carpart表中有该包的派工数据，则说明已经派过工，不需要再派工
                for(ZtTaskPacketEntity ztTaskPacketEntity:taskPacketEntityList){
                    String publishCode = dayPlanId+unitCode+ztTaskPacketEntity.getRepairDeptCode();
                    TaskCarPart taskCarPart = taskCarPartList.stream().filter(t -> ztTaskPacketEntity.getTrainsetId().equals(t.getTrainsetId())&&t.getPublishCode().equals(publishCode)).findFirst().orElse(null);
                    if(ObjectUtils.isEmpty(taskCarPart)){
                        //5.1组织作业包表数据
                        TaskAllotPacket insertPacket = new TaskAllotPacket();
                        String packetId = UUID.randomUUID().toString();
                        insertPacket.setTaskAllotPacketId(packetId);
                        insertPacket.setTaskId(ztTaskPacketEntity.getTaskId());
                        insertPacket.setPacketCode(ztTaskPacketEntity.getPacketCode());
                        insertPacket.setPacketName(ztTaskPacketEntity.getPacketName());
                        insertPacket.setPacketType(ztTaskPacketEntity.getPacketTypeCode());
                        insertPacket.setPublishCode(publishCode);
                        insertPacketList.add(insertPacket);
                        TaskAllotPacketHis insertPacketHis = new TaskAllotPacketHis();
                        BeanUtils.copyProperties(insertPacket,insertPacketHis);
                        insertPacketHisList.add(insertPacketHis);
                        //5.2组织部门表数据
                        TaskAllotDept insertDept = new TaskAllotDept();
                        String deptId = UUID.randomUUID().toString();
                        insertDept.setTaskAllotDeptId(deptId);
                        insertDept.setDeptCode(ztTaskPacketEntity.getRepairDeptCode());
                        insertDept.setDeptName(ztTaskPacketEntity.getRepairDeptName());
                        insertDept.setPublishCode(publishCode);
                        insertDeptList.add(insertDept);
                        TaskAllotDeptHis insertDeptHis = new TaskAllotDeptHis();
                        BeanUtils.copyProperties(insertDept,insertDeptHis);
                        insertDeptHisList.add(insertDeptHis);
                        //5.3组织辆序数据
                        TaskCarPart insertCarPart = new TaskCarPart();
                        String carPartId = UUID.randomUUID().toString();
                        insertCarPart.setProcessId(carPartId);
                        insertCarPart.setTaskAllotPacketId(packetId);
                        insertCarPart.setTaskAllotDeptId(deptId);
                        insertCarPart.setDayPlanId(dayPlanId);
                        insertCarPart.setTrainsetId(ztTaskPacketEntity.getTrainsetId());
                        insertCarPart.setTrainsetName(ztTaskPacketEntity.getTrainsetName());
                        if(!ObjectUtils.isEmpty(ztTaskPacketEntity.getTrainsetId())){
                            TrainsetBaseInfo trainsetBaseInfo = Optional.ofNullable(trainsetBaseInfoList).orElseGet(ArrayList::new).stream().filter(t -> ztTaskPacketEntity.getTrainsetId().equals(t.getTrainsetid())).findFirst().orElse(null);
                            if(!ObjectUtils.isEmpty(trainsetBaseInfo)){
                                insertCarPart.setTrainsetType(trainsetBaseInfo.getTraintype());
                            }
                        }
                        insertCarPart.setUnitCode(unitCode);
                        insertCarPart.setItemCode(ztTaskPacketEntity.getPacketCode());
                        insertCarPart.setItemName(ztTaskPacketEntity.getPacketName());
                        insertCarPart.setRepairType(ztTaskPacketEntity.getPacketTypeCode());
                        insertCarPart.setArrageType("0");
                        insertCarPart.setCarNo("全列");
                        insertCarPart.setTaskItemId(ztTaskPacketEntity.getTaskId());
                        insertCarPart.setMainCyc("1");
                        insertCarPart.setRepairMode("2");
                        insertCarPart.setRecorderName("系统管理员");
                        insertCarPart.setRecordTime(currentDate);
                        insertCarPart.setPublishCode(publishCode);
                        insertCarPartList.add(insertCarPart);
                        TaskCarPartHis insertCarPartHis = new TaskCarPartHis();
                        BeanUtils.copyProperties(insertCarPart,insertCarPartHis);
                        insertCarPartHisList.add(insertCarPartHis);
                        //5.4组织人员数据
                        //获取本班组下所有人员
                        if(StringUtils.isNotBlank(ztTaskPacketEntity.getRepairDeptCode())){
                            List<SysStaff> workerList = UserUtil.getStaffListStruByDept(ztTaskPacketEntity.getRepairDeptCode());
                            if (!CollectionUtils.isEmpty(workerList)) {
                                workerList = workerList.stream().filter(t -> (!StringUtils.isBlank(t.getStaffId())) || (!StringUtils.isBlank(t.getName()))).collect(Collectors.toList());
                                workerList = CommonUtils.getDistinctList(workerList, t -> t.getStaffId());
                                for(SysStaff sysStaff:workerList){
                                    TaskAllotPerson insertPerson = new TaskAllotPerson();
                                    String personId = UUID.randomUUID().toString();
                                    insertPerson.setProcessId(carPartId);
                                    insertPerson.setTaskAllotPersonId(personId);
                                    insertPerson.setPublishCode(publishCode);
                                    insertPerson.setWorkerId(sysStaff.getStaffId());
                                    insertPerson.setWorkerName(sysStaff.getName());
                                    insertPersonList.add(insertPerson);
                                    TaskAllotPersonHis insertPersonHis = new TaskAllotPersonHis();
                                    BeanUtils.copyProperties(insertPerson,insertPersonHis);
                                    insertPersonHisList.add(insertPersonHis);
                                }
                            }
                        }
                    }
                }
                //6.插入数据
                if (insertPacketList.size() > 0) {
                    //3.1插入派工包数据
                    taskAllotPacketService.insertBatch(insertPacketList);
                    //3.2插入派工包历史表数据
                    taskAllotPacketHisService.insertBatch(insertPacketHisList);
                }
                if (insertDeptList.size() > 0) {
                    //3.3插入派工部门数据
                    taskAllotDeptService.insertBatch(insertDeptList);
                    //3.4插入派工部门历史表数据
                    taskAllotDeptHisService.insertBatch(insertDeptHisList);
                }
                if (insertCarPartList.size() > 0) {
                    //3.5插入派工辆序数据
                    taskCarPartService.insertBatch(insertCarPartList);
                    //3.6插入派工辆序历史数据
                    taskCarPartHisService.insertBatch(insertCarPartHisList);
                }
                if (insertPersonList.size() > 0) {
                    //3.7插入派工人员数据
                    taskAllotPersonService.insertBatch(insertPersonList);
                    //3.8插入派工人员历史表数据
                    taskAllotPersonHisService.insertBatch(insertPersonHisList);
                }
                if (insertPostList.size() > 0) {
                    //3.9插入派工岗位数据
                    taskAllotPersonPostService.insertBatch(insertPostList);
                    //3.10插入派工岗位历史表数据
                    taskAllotPersonPostHisService.insertBatch(insertPostHisList);
                }
                taskPacketItem.setTechniqueTask(taskPacketEntityList);
            }
        }
        return insertRes;
    }


    /**
     * 插入派工数据——增量保存
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean setTaskAllotDataIncrement(List<TaskAllotPacketEntity> taskAllotPacketList){
        boolean result = true;
        if(!CollectionUtils.isEmpty(taskAllotPacketList)){
            //1.获取第一个包实体对象，以此来获取 运用所编码、日计划id
            TaskAllotPacketEntity packetEntity = taskAllotPacketList.stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(packetEntity)){
                //2.定义要插入和要删除的实体集合
                List<TaskAllotPacket> insertPacketList = new ArrayList<>();//插入派工包数据集合
                List<TaskAllotDept> insertDeptList = new ArrayList<>();//插入派工部门表数据集合
                List<TaskCarPart> insertCarPartList = new ArrayList<>();//插入辆序表数据集合
                List<TaskAllotPerson> insertPersonList = new ArrayList<>();//插入派工人员表数据集合
                List<TaskAllotPersonPost> insertPostList = new ArrayList<>();//插入派工岗位表数据集合
                List<String> delPacketIdList = new ArrayList<>();//要删除的派工包表主键集合
                List<String> delDeptIdList = new ArrayList<>();//要删除的部门表主键集合
                List<String> delItemIdList = new ArrayList<>();//要删除的项目表主键集合
                List<String> delPersonIdList = new ArrayList<>();//要删除的人员表主键集合
                List<String> delPostIdList = new ArrayList<>();//要删除的岗位表主键集合
                String unitCode = packetEntity.getUnitCode();//运用所编码
                String unitName = packetEntity.getUnitName();//运用所名称
                String dayPlanId = packetEntity.getDayPlanId();//日计划id
                //3.从数据库中获取该运用所下所有的人员-岗位数据
                List<PostModel> personPostList = this.getPersonPostList(unitCode, "", "");
                //3.根据运用所编码、日计划id从数据库中获取已经派工了的数据
                List<TaskAllotPacketEntity> exitPacketList = this.getTaskAllotDataToPerson(unitCode, dayPlanId, "", "", "", null, null, null, null, null, "");
                if(!CollectionUtils.isEmpty(exitPacketList)){
                    for(TaskAllotPacketEntity packet:taskAllotPacketList){
                        String packetId = packet.getTaskAllotPacketId();
                        String deptId = packet.getTaskAllotDeptId();
                        String packetTaskId = packet.getTaskId();
                        String trainsetId = packet.getTrainsetId();
                        String packetCode = packet.getPacketCode();
                        String deptCode = packet.getDeptCode();
                        String publishCode = dayPlanId+unitCode+packet.getDeptCode();
                        //用包id和部门id进行匹配判断
                        // TaskAllotPacketEntity exitPacket = exitPacketList.stream().filter(t -> t.getTaskAllotPacketId().equals(packetId) && t.getTaskAllotDeptId().equals(deptId)).findFirst().orElse(null);
                        //同一运用所、日计划下，用车组、包编码、部门编码进行匹配，查看数据库中是否存在该包
                        TaskAllotPacketEntity exitPacket = exitPacketList.stream().filter(t -> trainsetId.equals(t.getTrainsetId()) && packetCode.equals(t.getPacketCode()) && deptCode.equals(t.getDepotCode())&&packetTaskId.equals(t.getTaskId())).findFirst().orElse(null);
                        if(!ObjectUtils.isEmpty(exitPacket)){//该包在数据库中存在
                            String exitPacketId = exitPacket.getTaskAllotPacketId();
                            String exitDeptId = exitPacket.getTaskAllotDeptId();
                            String exitTrainsetId = exitPacket.getTrainsetId();
                            String exitTrainsetName = exitPacket.getTrainsetName();
                            //循环该包下所有的项目
                            List<TaskAllotItemEntity> itemEntityList = packet.getTaskAllotItemEntityList();//入参的项目
                            List<TaskAllotItemEntity> exitItemEntityList = exitPacket.getTaskAllotItemEntityList();//数据库中已存在的项目
                            if(!CollectionUtils.isEmpty(exitItemEntityList)){
                                if(!CollectionUtils.isEmpty(itemEntityList)){
                                    //循环入参的项目集合
                                    for(TaskAllotItemEntity itemEntity:itemEntityList){
                                        String itemId = itemEntity.getId();
                                        String taskItemId = itemEntity.getTaskItemId();
                                        String arrageType = itemEntity.getArrageType();
                                        String itemCode = itemEntity.getItemCode();
                                        String carNo = itemEntity.getCarNo();
                                        String partType = itemEntity.getPartType();
                                        String partName = itemEntity.getPartName();
                                        List<TaskAllotPersonEntity> taskAllotPersonEntityList = itemEntity.getTaskAllotPersonEntityList();//派工人员集合
                                        // TaskAllotItemEntity exitItem = exitItemEntityList.stream().filter(t -> t.getId().equals(itemId)).findFirst().orElse(null);
                                        TaskAllotItemEntity exitItem = null;
                                        if("0".equals(arrageType)){
                                            exitItem = exitItemEntityList.stream().filter(v->taskItemId.equals(v.getTaskItemId())&&itemCode.equals(v.getItemCode())&&carNo.equals(v.getCarNo())).findFirst().orElse(null);
                                        }else if("1".equals(arrageType)){
                                            exitItem = exitItemEntityList.stream().filter(v->taskItemId.equals(v.getTaskItemId())&&itemCode.equals(v.getItemCode())&&carNo.equals(v.getCarNo())&&partType.equals(v.getPartType())&&partName.equals(v.getPartName())).findFirst().orElse(null);
                                        }else{
                                            logger.error("setTaskAllotDataIncrement方法-入参错误：["+packetEntity.getPacketName()+"]包下["+exitItem.getItemName()+"]项目作业类型错误_arrageType:"+arrageType);
                                            throw new RuntimeException("setTaskAllotDataIncrement方法-入参错误：["+packetEntity.getPacketName()+"]包下["+exitItem.getItemName()+"]项目作业类型错误_arrageType:"+arrageType);
                                        }
                                        if(!ObjectUtils.isEmpty(exitItem)){//数据中存在该项目
                                            String exitItemId = exitItem.getId();
                                            //先将数据库中该项目下的人员及岗位数据删除
                                            List<TaskAllotPersonEntity> exitPersonEntityList = exitItem.getTaskAllotPersonEntityList();
                                            if(!CollectionUtils.isEmpty(exitPersonEntityList)){
                                                List<String> exitPersonIdList = exitPersonEntityList.stream().map(t -> t.getSId()).collect(Collectors.toList());
                                                if(!CollectionUtils.isEmpty(exitPersonIdList)){
                                                    delPersonIdList.addAll(exitPersonIdList);
                                                }
                                            }else{
                                                logger.error("setTaskAllotDataIncrement方法-数据库错误：["+packetEntity.getPacketName()+"]包下["+exitItem.getItemName()+"]项目下人员为空!");
                                                throw new RuntimeException("数据库错误：["+packetEntity.getPacketName()+"]包下["+exitItem.getItemName()+"]项目下人员为空!");
                                            }
                                            //如果入参中该项目下人员为空，则将数据库中存在的该项目数据也删除
                                            if(CollectionUtils.isEmpty(taskAllotPersonEntityList)){
                                                delItemIdList.add(exitItemId);
                                                boolean b = exitItemEntityList.removeIf(t -> t.getId().equals(exitItemId));
                                            }else{//组织该项目的人员和岗位数据插入到数据库中
                                                this.organizePersonAndPostData(exitItemId,publishCode,taskAllotPersonEntityList,personPostList,insertPersonList,insertPostList);
                                            }
                                        }else{//数据库中不存在该项目，则将该项目及它下边的人和岗位数据插入到数据库中
                                            if(!CollectionUtils.isEmpty(taskAllotPersonEntityList)){
                                                String processId = UUID.randomUUID().toString();//辆序表主键
                                                TaskCarPart insertCarPart = new TaskCarPart();
                                                BeanUtils.copyProperties(itemEntity,insertCarPart);
                                                insertCarPart.setProcessId(processId);
                                                insertCarPart.setPublishCode(publishCode);
                                                insertCarPart.setTaskAllotPacketId(exitPacketId);
                                                insertCarPart.setTaskAllotDeptId(exitDeptId);
                                                insertCarPart.setUnitCode(unitCode);
                                                insertCarPart.setUnitName(unitName);
                                                insertCarPart.setTrainsetId(exitTrainsetId);
                                                insertCarPart.setTrainsetName(exitTrainsetName);
                                                insertCarPart.setDayPlanId(dayPlanId);
                                                insertCarPart.setRepairType(itemEntity.getItemType());
                                                insertCarPart.setMainCyc(packet.getMainCyc());
                                                insertCarPart.setRecorderCode(packet.getRecordCode());
                                                insertCarPart.setRecorderName(packet.getRecordName());
                                                insertCarPart.setRecordTime(packet.getRecordTime());
                                                insertCarPartList.add(insertCarPart);
                                                this.organizePersonAndPostData(processId,publishCode,taskAllotPersonEntityList,personPostList,insertPersonList,insertPostList);
                                            }else{
                                                logger.error("setTaskAllotDataIncrement方法入参错误：["+packetEntity.getPacketName()+"]包下["+exitItem.getItemName()+"]项目下人员为空!");
                                                throw new RuntimeException("入参错误：["+packetEntity.getPacketName()+"]包下["+exitItem.getItemName()+"]项目下人员为空!");
                                            }
                                        }
                                    }
                                    //如果已存在的项目集合全部被清空，则将该派工包和部门表的数据也从数据库中删除
                                    if(CollectionUtils.isEmpty(exitItemEntityList)){
                                        delPacketIdList.add(exitPacketId);
                                        delDeptIdList.add(exitDeptId);
                                    }
                                }else{
                                    logger.error("setTaskAllotDataIncrement方法入参错误：["+packetEntity.getPacketName()+"]包实体下项目集合为空!");
                                    throw new RuntimeException("入参错误：["+packetEntity.getPacketName()+"]包实体下项目集合为空!");
                                }
                            }else{
                                logger.error("setTaskAllotDataIncrement方法数据库错误：在数据库存在的包["+exitPacket.getPacketName()+"]下没有项目!");
                                throw new RuntimeException("数据库错误：在数据库存在的包["+exitPacket.getPacketName()+"]下没有获取到项目!");
                            }
                        }else{//该包在数据库中不存在
                            //将该包派工
                            List<TaskAllotPacketEntity> taskAllotPacketTempList = new ArrayList<>();
                            taskAllotPacketTempList.add(packet);
                            this.organizeAllData(taskAllotPacketTempList,personPostList,insertPacketList,insertDeptList,insertCarPartList,insertPersonList,insertPostList);
                        }
                    }
                }else{//如果查询数据库的派工数据集合为空，则直接将入参的所有数据进行派工
                    this.organizeAllData(taskAllotPacketList,personPostList,insertPacketList,insertDeptList,insertCarPartList,insertPersonList,insertPostList);
                }
                //将数据持久化到数据库中
                //删除数据
                if(!CollectionUtils.isEmpty(delPacketIdList)){
                    //删除派工包表数据
                    MybatisPlusUtils.delete(taskAllotPacketService,inParam(TaskAllotPacket::getTaskAllotPacketId,delPacketIdList));
                }
                if(!CollectionUtils.isEmpty(delDeptIdList)){
                    //删除派工部门表数据
                    MybatisPlusUtils.delete(taskAllotDeptService,inParam(TaskAllotDept::getTaskAllotDeptId,delDeptIdList));
                }
                if(!CollectionUtils.isEmpty(delItemIdList)){
                    //删除辆序表数据
                    MybatisPlusUtils.delete(taskCarPartService,inParam(TaskCarPart::getProcessId,delItemIdList));
                }
                if(!CollectionUtils.isEmpty(delPersonIdList)){
                    //删除人员表数据
                    MybatisPlusUtils.delete(taskAllotPersonService,inParam(TaskAllotPerson::getTaskAllotPersonId,delPersonIdList));
                    //删除岗位表数据
                    MybatisPlusUtils.delete(taskAllotPersonPostService,inParam(TaskAllotPersonPost::getTaskAllotPersonId,delPersonIdList));
                }
                //插入数据
                if (insertPacketList.size() > 0) {
                    //插入派工包数据
                    taskAllotPacketService.insertBatch(insertPacketList);
                }
                if (insertDeptList.size() > 0) {
                    //插入派工部门数据
                    taskAllotDeptService.insertBatch(insertDeptList);
                }
                if (insertCarPartList.size() > 0) {
                    //插入派工辆序数据
                    taskCarPartService.insertBatch(insertCarPartList);
                }
                if (insertPersonList.size() > 0) {
                    //插入派工人员数据
                    taskAllotPersonService.insertBatch(insertPersonList);
                }
                if (insertPostList.size() > 0) {
                    //插入派工岗位数据
                    taskAllotPersonPostService.insertBatch(insertPostList);
                }
            }
        }else{
            logger.error("setTaskAllotDataIncrement入参错误：入参列表为空!");
            throw new RuntimeException("入参错误：入参列表为空!");
        }
        return result;
    }

    //根据入参中的派工包实体组织数据库映射实体
    public void organizeAllData(List<TaskAllotPacketEntity> taskAllotPacketList,List<PostModel> personPostList,List<TaskAllotPacket> insertPacketList,
                             List<TaskAllotDept> insertDeptList, List<TaskCarPart> insertCarPartList,
                             List<TaskAllotPerson> insertPersonList, List<TaskAllotPersonPost> insertPostList){
        if(!CollectionUtils.isEmpty(taskAllotPacketList)){
            for(TaskAllotPacketEntity packetEntity:taskAllotPacketList){
                String dayPlanId = packetEntity.getDayPlanId();//日计划
                String unitCode = packetEntity.getUnitCode();//运用所编码
                String unitName = packetEntity.getUnitName();
                String trainsetId = packetEntity.getTrainsetId();
                String trainsetName = packetEntity.getTrainsetName();
                String insertPacketId = UUID.randomUUID().toString();//要插入的派工包表主键id
                String deptCode = packetEntity.getDeptCode();//要插入的部门编码
                String deptName = packetEntity.getDeptName();//要插入的部门名称
                String publishCode = dayPlanId+unitCode+deptCode;//要插入的派工唯一编码
                //1.组织包表实体对象
                TaskAllotPacket insertPacket = new TaskAllotPacket();
                BeanUtils.copyProperties(packetEntity,insertPacket);
                insertPacket.setTaskAllotPacketId(insertPacketId);
                insertPacket.setPublishCode(publishCode);
                insertPacketList.add(insertPacket);
                //2.组织部门表实体对象
                String insertDeptId = UUID.randomUUID().toString();//要插入的派工部门表主键id
                TaskAllotDept insertDept = new TaskAllotDept();
                insertDept.setTaskAllotDeptId(insertDeptId);
                insertDept.setDeptCode(deptCode);
                insertDept.setDeptName(deptName);
                insertDept.setPublishCode(publishCode);
                insertDeptList.add(insertDept);
                //3.组织辆序表实体对象
                List<TaskAllotItemEntity> taskAllotItemEntityList = packetEntity.getTaskAllotItemEntityList();
                if(!CollectionUtils.isEmpty(taskAllotItemEntityList)){
                    for(TaskAllotItemEntity itemEntity:taskAllotItemEntityList){
                        String processId = UUID.randomUUID().toString();//辆序表主键
                        TaskCarPart insertCarPart = new TaskCarPart();
                        BeanUtils.copyProperties(itemEntity,insertCarPart);
                        insertCarPart.setProcessId(processId);
                        insertCarPart.setPublishCode(publishCode);
                        insertCarPart.setTaskAllotPacketId(insertPacketId);
                        insertCarPart.setTaskAllotDeptId(insertDeptId);
                        insertCarPart.setUnitCode(unitCode);
                        insertCarPart.setUnitName(unitName);
                        insertCarPart.setTrainsetId(trainsetId);
                        insertCarPart.setTrainsetName(trainsetName);
                        insertCarPart.setDayPlanId(dayPlanId);
                        insertCarPart.setRepairType(itemEntity.getItemType());
                        insertCarPart.setMainCyc(packetEntity.getMainCyc());
                        insertCarPart.setRecorderCode(packetEntity.getRecordCode());
                        insertCarPart.setRecorderName(packetEntity.getRecordName());
                        insertCarPart.setRecordTime(packetEntity.getRecordTime());
                        insertCarPartList.add(insertCarPart);
                        //4.组织人员表实体对象
                        List<TaskAllotPersonEntity> taskAllotPersonEntityList = itemEntity.getTaskAllotPersonEntityList();
                        if(!CollectionUtils.isEmpty(taskAllotPersonEntityList)){
                            this.organizePersonAndPostData(processId,publishCode,taskAllotPersonEntityList,personPostList,insertPersonList,insertPostList);
                        }else{
                            logger.error("organizeData方法入参错误：["+packetEntity.getPacketName()+"]包下["+itemEntity.getItemName()+"]包项目下人员集合为空!");
                            throw new RuntimeException("入参错误：["+packetEntity.getPacketName()+"]包下["+itemEntity.getItemName()+"]包项目下人员集合为空!");
                        }
                    }
                }else{
                    logger.error("organizeData方法入参错误：["+packetEntity.getPacketName()+"]包实体下项目集合为空!");
                    throw new RuntimeException("入参错误：["+packetEntity.getPacketName()+"]包实体下项目集合为空!");
                }
            }
        }
    }



    //组织人员及岗位数据实体
    public void organizePersonAndPostData(String processId,String publishCode,List<TaskAllotPersonEntity> taskAllotPersonEntityList,
                                          List<PostModel> personPostList,List<TaskAllotPerson> insertPersonList,List<TaskAllotPersonPost> insertPostList){
        for(TaskAllotPersonEntity personEntity:taskAllotPersonEntityList){
            String insertPersonId = UUID.randomUUID().toString();//人员表主键
            TaskAllotPerson insertPerson = new TaskAllotPerson();
            BeanUtils.copyProperties(personEntity,insertPerson);
            insertPerson.setTaskAllotPersonId(insertPersonId);
            insertPerson.setProcessId(processId);
            insertPerson.setPublishCode(publishCode);
            insertPersonList.add(insertPerson);
            //5.组织人员岗位实体对象
            //5.1根据该人员id和分组编码获取岗位集合
            String workerId = personEntity.getWorkerId();
            String branchCode = personEntity.getBranchCode();
            //5.2从所有人员-岗位集合中获取岗位集合
            List<PostModel> postList = new ArrayList<>();
            if(StringUtils.isNotBlank(branchCode)){
                postList = Optional.ofNullable(personPostList).orElseGet(ArrayList::new).stream().filter(t->workerId.equals(t.getStaffId())&&branchCode.equals(t.getBranchCode())).collect(Collectors.toList());
            }else{
                postList = Optional.ofNullable(personPostList).orElseGet(ArrayList::new).stream().filter(t->workerId.equals(t.getStaffId())).collect(Collectors.toList());
            }
            if(!CollectionUtils.isEmpty(postList)){
                for(PostModel postModel:postList){
                    String insertPostId = UUID.randomUUID().toString();//岗位表主键
                    TaskAllotPersonPost insertPost = new TaskAllotPersonPost();
                    insertPost.setTaskAllotPersonId(insertPersonId);
                    insertPost.setId(insertPostId);
                    insertPost.setPublishCode(publishCode);
                    insertPost.setPostId(postModel.getPostId());
                    insertPost.setPostName(postModel.getPostName());
                    insertPostList.add(insertPost);
                }
            }
        }
    }



    /**
     * 删除派工数据—根据id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteTaskAllotDataById(List<TaskCarPart> delTaskCarPartList){
        boolean delRes = true;
        try {
            if(!CollectionUtils.isEmpty(delTaskCarPartList)){
                //1需要删除的辆序表组件集合
                List<String> delCarPartIdList = delTaskCarPartList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
                //2.需要删除的作业包表主键集合
                List<String> delPacketIdList = delTaskCarPartList.stream().map(t -> t.getTaskAllotPacketId()).collect(Collectors.toList());
                //3.需要删除的部门表主键
                List<String> delDeptIdList = delTaskCarPartList.stream().map(t -> t.getTaskAllotDeptId()).collect(Collectors.toList());
                //4.需要删除的派工人员表主键集合
                List<String> delPersonIdList = xzyMTaskAllotMapper.getDelPersonIdList(delCarPartIdList);
                //5.需要删除的派工人员岗位表主键集合
                List<String> delPersonPostIdList = null;
                if (delPersonIdList != null && delPersonIdList.size() > 0) {
                    delPersonPostIdList = xzyMTaskAllotMapper.getDelPersonPostIdList(delPersonIdList);
                }
                //6.删除相关表数据
                if (delPacketIdList != null && delPacketIdList.size() > 0) {
                    delPacketIdList = delPacketIdList.stream().distinct().collect(Collectors.toList());
                    if (delPacketIdList.size() > 0) {
                        //删除派工包表
                        xzyMTaskAllotMapper.delPacketList(delPacketIdList);
                    }
                }
                if (delDeptIdList != null && delDeptIdList.size() > 0) {
                    delDeptIdList = delDeptIdList.stream().distinct().collect(Collectors.toList());
                    if (delDeptIdList.size() > 0) {
                        //删除派工部门表
                        xzyMTaskAllotMapper.delDeptList(delDeptIdList);
                    }
                }
                if (delCarPartIdList != null && delCarPartIdList.size() > 0) {
                    //删除辆序表
                    xzyMTaskAllotMapper.delCarParList(delCarPartIdList);
                }
                if (delPersonIdList != null && delPersonIdList.size() > 0) {
                    //删除人员表
                    xzyMTaskAllotMapper.delPersonList(delPersonIdList);
                }
                if (delPersonPostIdList != null && delPersonPostIdList.size() > 0) {
                    //删除岗位表
                    xzyMTaskAllotMapper.delPersonPostList(delPersonPostIdList);
                }
            }
        }catch (Exception ex){
            logger.error("deleteTaskAllotDataById方法报错："+ex);
            delRes = false;
        }
        return delRes;
    }

    /**
     * 删除派工数据—根据派工唯一编码
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteTaskAllotDataByPublishCode(List<String> publishCodeList){
        boolean delRes = true;
        try {
            if(!CollectionUtils.isEmpty(publishCodeList)){
                //1.删除派工包
                MybatisPlusUtils.delete(
                    taskAllotPacketService,
                    inParam(TaskAllotPacket::getPublishCode, publishCodeList)
                );
                //2.删除派工部门
                MybatisPlusUtils.delete(
                    taskAllotDeptService,
                    inParam(TaskAllotDept::getPublishCode, publishCodeList)
                );
                //3.删除辆序
                MybatisPlusUtils.delete(
                    taskCarPartService,
                    inParam(TaskCarPart::getPublishCode, publishCodeList)
                );
                //4.删除人员
                MybatisPlusUtils.delete(
                    taskAllotPersonService,
                    inParam(TaskAllotPerson::getPublishCode, publishCodeList)
                );
                //5.删除岗位
                MybatisPlusUtils.delete(
                    taskAllotPersonPostService,
                    inParam(TaskAllotPersonPost::getPublishCode, publishCodeList)
                );
            }
        }catch (Exception ex){
            logger.error("deleteTaskAllotDataByPublishCode报错："+ex);
            delRes = false;
        }
        return delRes;
    }



    /**------------------------------------------------------------------------------------------------------------------*/

    /** 过时方法 暂时注释掉
    @Data
    public static class GroupKey {
        private String packetId;
        private String deptId;
        private String dayPlanId;
        private String trainsetId;
        private String trainsetName;
        private String unitCode;
        private String unitName;
        private String deptCode;
        private String mainCyc;
    }

    //获取派工数据（过时）
    @Deprecated
    public List<TaskAllotPacketEntity> getTaskAllotDataOverdue(String unitCode, String dayPlanId, String startDate, String endDate, String repairType, List<String> trainsetTypeList, List<String> trainsetIdList, List<String> itemCodeList, List<String> branchCodeList, List<String> workIdList, String packetName) {
        //返回结果
        List<TaskAllotPacketEntity> result = new ArrayList<>();
        List<TaskCarPart> taskCarPartList = new ArrayList<>();
        List<TaskAllotDept> taskAllotDeptList = new ArrayList<>();
        List<TaskAllotPacket> taskAllotPacketList = new ArrayList<>();
        List<TaskAllotPerson> taskAllotPersonList = new ArrayList<>();
        List<TaskAllotPersonPost> taskAllotPersonPostList = new ArrayList<>();
        List<String> publishCodeList = new ArrayList<>();

        //1.查询辆序表，获取派工唯一标识
        List<ColumnParam<TaskCarPart, ?>> columnParamList = new ArrayList<>();
        if(StringUtils.isNotBlank(unitCode)){
            columnParamList.add(eqParam(TaskCarPart::getUnitCode,unitCode));//运用所编码
        }
        if(StringUtils.isNotBlank(dayPlanId)){
            columnParamList.add(eqParam(TaskCarPart::getDayPlanId,dayPlanId));//日计划id
        }
        if(StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)){//开始结束时间
            List<String> betweenDate = DateUtils.getBetweenDate(startDate, endDate);
            if(!CollectionUtils.isEmpty(betweenDate)){
                List<String> dayPlanIdList = new ArrayList<>();
                betweenDate.forEach(v->{
                    dayPlanIdList.add(v+"-00");
                    dayPlanIdList.add(v+"-01");
                });
                columnParamList.add(inParam(TaskCarPart::getDayPlanId,dayPlanIdList));
            }
        }
        if(StringUtils.isNotBlank(repairType)){
            columnParamList.add(eqParam(TaskCarPart::getRepairType,repairType));//检修类型（项目类型）
        }
        if(!CollectionUtils.isEmpty(trainsetTypeList)){//车型
            columnParamList.add(inParam(TaskCarPart::getTrainsetType,trainsetTypeList));
        }
        if(!CollectionUtils.isEmpty(trainsetIdList)){//车组
            columnParamList.add(inParam(TaskCarPart::getTrainsetId,trainsetIdList));
        }
        if(!CollectionUtils.isEmpty(itemCodeList)){//项目编码
            columnParamList.add(inParam(TaskCarPart::getItemCode,itemCodeList));
        }
        Date beginDate = new Date();
        taskCarPartList = MybatisPlusUtils.selectList(
            taskCarPartService,
            columnParamList
        );
        Date time5 = new Date();
        System.out.println("time5："+(time5.getTime()-beginDate.getTime()));
        //2.组织其它表查询条件
        if(!ObjectUtils.isEmpty(taskCarPartList)){
            publishCodeList = taskCarPartList.stream().map(t->t.getPublishCode()).distinct().collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(publishCodeList)){
                //3.查询作业包表
                taskAllotDeptList = MybatisPlusUtils.selectList(
                    taskAllotDeptService,
                    inParam(TaskAllotDept::getPublishCode,publishCodeList)
                );

                Date time6= new Date();
                System.out.println("time6："+(time6.getTime()-time5.getTime()));
                //4.查询部门表
                taskAllotPacketList = MybatisPlusUtils.selectList(
                    taskAllotPacketService,
                    inParam(TaskAllotPacket::getPublishCode,publishCodeList)
                );
                Date time7= new Date();
                System.out.println("time7："+(time7.getTime()-time6.getTime()));
                //5.查询人员表
                taskAllotPersonList = MybatisPlusUtils.selectList(
                    taskAllotPersonService,
                    inParam(TaskAllotPerson::getPublishCode,publishCodeList)
                );
                Date time8= new Date();
                System.out.println("time8："+(time8.getTime()-time7.getTime()));
                //5.查询人员岗位表
                taskAllotPersonPostList = MybatisPlusUtils.selectList(
                    taskAllotPersonPostService,
                    eqParam(TaskAllotPersonPost::getPublishCode,publishCodeList.get(0))
                );

                Date time9= new Date();
                System.out.println("time9："+(time9.getTime()-time8.getTime()));


                Date time1 = new Date();
                System.out.println("getTaskAllotDataPlus查询数据消耗时间："+(time1.getTime()-beginDate.getTime()));
                //6.根据查询出来的数据组织最终返回结果
                //6.1把辆序数据按照派工包、派工部门、日计划、车组、运用所分组
                if(!CollectionUtils.isEmpty(taskCarPartList)){
                    Map<GroupKey,List<TaskCarPart>> groupMap = taskCarPartList.stream().collect(Collectors.groupingBy(v->{
                        GroupKey key = new GroupKey();
                        key.setPacketId(v.getTaskAllotPacketId());
                        key.setDeptId(v.getTaskAllotDeptId());
                        key.setDayPlanId(v.getDayPlanId());
                        key.setTrainsetId(v.getTrainsetId());
                        key.setTrainsetName(v.getTrainsetName());
                        key.setUnitCode(v.getUnitCode());
                        key.setUnitName(v.getUnitName());
                        key.setMainCyc(v.getMainCyc());
                        return key;
                    }));
                    Date time2 = new Date();
                    System.out.println("getTaskAllotDataPlus分组消耗时间："+(time2.getTime()-time1.getTime()));
                    if(!ObjectUtils.isEmpty(groupMap)){
                        for(Map.Entry<GroupKey,List<TaskCarPart>> entry:groupMap.entrySet()){
                            GroupKey groupKey = entry.getKey();
                            TaskAllotPacketEntity packetEntity = new TaskAllotPacketEntity();
                            packetEntity.setUnitCode(groupKey.getUnitCode());
                            packetEntity.setUnitName(groupKey.getUnitName());
                            packetEntity.setMainCyc(groupKey.getMainCyc());
                            packetEntity.setTrainsetId(groupKey.getTrainsetId());
                            packetEntity.setTrainsetName(groupKey.getTrainsetName());
                            packetEntity.setDayPlanId(groupKey.getDayPlanId());
                            packetEntity.setTaskAllotPacketId(groupKey.getPacketId());
                            //根据作业包id去包集合中获取作业包信息
                            if(!CollectionUtils.isEmpty(taskAllotPacketList)){
                                TaskAllotPacket taskAllotPacket = taskAllotPacketList.stream().filter(t -> groupKey.getPacketId().equals(t.getTaskAllotPacketId())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(taskAllotPacket)){
                                    packetEntity.setPacketCode(taskAllotPacket.getPacketCode());
                                    packetEntity.setPacketName(taskAllotPacket.getPacketName());
                                    packetEntity.setPacketType(taskAllotPacket.getPacketType());
                                }
                                //根据作业包id去包集合中获取部门信息
                                if(!CollectionUtils.isEmpty(taskAllotDeptList)){
                                    TaskAllotDept taskAllotDept = taskAllotDeptList.stream().filter(t -> groupKey.getDeptId().equals(t.getTaskAllotDeptId())).findFirst().orElse(null);
                                    if(!ObjectUtils.isEmpty(taskAllotDept)){
                                        packetEntity.setDeptCode(taskAllotDept.getDeptCode());
                                        packetEntity.setDeptName(taskAllotDept.getDeptName());
                                    }
                                    //把项目挂上去
                                    List<TaskAllotItemEntity> taskAllotItemEntityList = new ArrayList<>();
                                    List<TaskCarPart> groupCarPartList = entry.getValue();
                                    if(!CollectionUtils.isEmpty(groupCarPartList)){
                                        for(TaskCarPart taskCarPart:groupCarPartList){
                                            TaskAllotItemEntity itemEntity = new TaskAllotItemEntity();
                                            BeanUtils.copyProperties(taskCarPart,itemEntity);
                                            //项目下挂人
                                            if(!CollectionUtils.isEmpty(taskAllotPersonList)){
                                                List<TaskAllotPerson> personList = taskAllotPersonList.stream().filter(t -> taskCarPart.getProcessId().equals(t.getProcessId())).collect(Collectors.toList());
                                                List<TaskAllotPersonEntity> personEntityList = new ArrayList<>();
                                                if(!CollectionUtils.isEmpty(personList)){
                                                    for(TaskAllotPerson person:personList){
                                                        TaskAllotPersonEntity personEntity = new TaskAllotPersonEntity();
                                                        BeanUtils.copyProperties(person,personEntity);
                                                        //人下边挂岗位
                                                        if(!CollectionUtils.isEmpty(taskAllotPersonPostList)){
                                                            List<TaskAllotPersonPost> postList = taskAllotPersonPostList.stream().filter(t -> person.getTaskAllotPersonId().equals(t.getTaskAllotPersonId())).collect(Collectors.toList());
                                                            List<TaskAllotPersonPostEntity> postEntityList = new ArrayList<>();
                                                            if(!CollectionUtils.isEmpty(postList)){
                                                                for(TaskAllotPersonPost post:postList){
                                                                    TaskAllotPersonPostEntity postEntity = new TaskAllotPersonPostEntity();
                                                                    BeanUtils.copyProperties(post,postEntity);
                                                                    postEntityList.add(postEntity);
                                                                }
                                                            }
                                                            personEntity.setTaskAllotPersonPostEntityList(postEntityList);
                                                            //把人员实体添加到集合中
                                                            personEntityList.add(personEntity);
                                                        }
                                                    }
                                                }
                                                //将人挂在项目上
                                                itemEntity.setTaskAllotPersonEntityList(personEntityList);
                                                //把项目添加到项目集合中
                                                taskAllotItemEntityList.add(itemEntity);
                                            }
                                        }
                                        //把项目集合挂到包下边
                                        packetEntity.setTaskAllotItemEntityList(taskAllotItemEntityList);
                                    }
                                    //把包添加到返回结果中
                                    result.add(packetEntity);
                                }
                            }
                        }
                    }
                }

                Date time3 = new Date();
                System.out.println("getTaskAllotDataPlus处理转换数据消耗时间："+(time3.getTime()-time1.getTime()));
            }
        }
        Date endTime = new Date();
        System.out.println("getTaskAllotDataPlus总消耗时间："+(endTime.getTime()-beginDate.getTime()));
        if(!CollectionUtils.isEmpty(branchCodeList)){
            result = result.stream().filter(t->branchCodeList.contains(t.getDeptCode())).collect(Collectors.toList());
        }
        return result;
    }

    //获取派工数据（过时）
    @Deprecated
    public List<TaskAllotPacketEntity> getTaskAllotDataOverdueToPerson(String unitCode, String dayPlanId, String startDate, String endDate, String repairType, List<String> trainsetTypeList, List<String> trainsetIdList, List<String> itemCodeList, List<String> branchCodeList, List<String> workIdList, String packetName) {
        //返回结果
        List<TaskAllotPacketEntity> result = new ArrayList<>();
        List<TaskCarPart> taskCarPartList = new ArrayList<>();
        List<TaskAllotDept> taskAllotDeptList = new ArrayList<>();
        List<TaskAllotPacket> taskAllotPacketList = new ArrayList<>();
        List<TaskAllotPerson> taskAllotPersonList = new ArrayList<>();
        // List<TaskAllotPersonPost> taskAllotPersonPostList = new ArrayList<>();
        List<String> publishCodeList = new ArrayList<>();

        //1.查询辆序表，获取派工唯一标识
        List<ColumnParam<TaskCarPart, ?>> columnParamList = new ArrayList<>();
        if(StringUtils.isNotBlank(unitCode)){
            columnParamList.add(eqParam(TaskCarPart::getUnitCode,unitCode));//运用所编码
        }
        if(StringUtils.isNotBlank(dayPlanId)){
            columnParamList.add(eqParam(TaskCarPart::getDayPlanId,dayPlanId));//日计划id
        }
        if(StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)){//开始结束时间
            List<String> betweenDate = DateUtils.getBetweenDate(startDate, endDate);
            if(!CollectionUtils.isEmpty(betweenDate)){
                List<String> dayPlanIdList = new ArrayList<>();
                betweenDate.forEach(v->{
                    dayPlanIdList.add(v+"-00");
                    dayPlanIdList.add(v+"-01");
                });
                columnParamList.add(inParam(TaskCarPart::getDayPlanId,dayPlanIdList));
            }
        }
        if(StringUtils.isNotBlank(repairType)){
            columnParamList.add(eqParam(TaskCarPart::getRepairType,repairType));//检修类型（项目类型）
        }
        if(!CollectionUtils.isEmpty(trainsetTypeList)){//车型
            columnParamList.add(inParam(TaskCarPart::getTrainsetType,trainsetTypeList));
        }
        if(!CollectionUtils.isEmpty(trainsetIdList)){//车组
            columnParamList.add(inParam(TaskCarPart::getTrainsetId,trainsetIdList));
        }
        if(!CollectionUtils.isEmpty(itemCodeList)){//项目编码
            columnParamList.add(inParam(TaskCarPart::getItemCode,itemCodeList));
        }
        Date beginDate = new Date();
        taskCarPartList = MybatisPlusUtils.selectList(
            taskCarPartService,
            columnParamList
        );
        Date time5 = new Date();
        System.out.println("time5："+(time5.getTime()-beginDate.getTime()));
        //2.组织其它表查询条件
        if(!ObjectUtils.isEmpty(taskCarPartList)){
            publishCodeList = taskCarPartList.stream().map(t->t.getPublishCode()).distinct().collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(publishCodeList)){
                //3.查询作业包表
                taskAllotDeptList = MybatisPlusUtils.selectList(
                    taskAllotDeptService,
                    inParam(TaskAllotDept::getPublishCode,publishCodeList)
                );

                Date time6= new Date();
                System.out.println("time6："+(time6.getTime()-time5.getTime()));
                //4.查询部门表
                taskAllotPacketList = MybatisPlusUtils.selectList(
                    taskAllotPacketService,
                    inParam(TaskAllotPacket::getPublishCode,publishCodeList)
                );
                Date time7= new Date();
                System.out.println("time7："+(time7.getTime()-time6.getTime()));
                //5.查询人员表
                taskAllotPersonList = MybatisPlusUtils.selectList(
                    taskAllotPersonService,
                    inParam(TaskAllotPerson::getPublishCode,publishCodeList)
                );
                Date time8= new Date();
                System.out.println("time8："+(time8.getTime()-time7.getTime()));
                // //5.查询人员岗位表
                // taskAllotPersonPostList = MybatisPlusUtils.selectList(
                //     taskAllotPersonPostService,
                //     eqParam(TaskAllotPersonPost::getPublishCode,publishCodeList.get(0))
                // );
                //
                // Date time9= new Date();
                // System.out.println("time9："+(time9.getTime()-time8.getTime()));


                Date time1 = new Date();
                System.out.println("getTaskAllotDataPlus查询数据消耗时间："+(time1.getTime()-beginDate.getTime()));
                //6.根据查询出来的数据组织最终返回结果
                //6.1把辆序数据按照派工包、派工部门、日计划、车组、运用所分组
                if(!CollectionUtils.isEmpty(taskCarPartList)){
                    Map<GroupKey,List<TaskCarPart>> groupMap = taskCarPartList.stream().collect(Collectors.groupingBy(v->{
                        GroupKey key = new GroupKey();
                        key.setPacketId(v.getTaskAllotPacketId());
                        key.setDeptId(v.getTaskAllotDeptId());
                        key.setDayPlanId(v.getDayPlanId());
                        key.setTrainsetId(v.getTrainsetId());
                        key.setTrainsetName(v.getTrainsetName());
                        key.setUnitCode(v.getUnitCode());
                        key.setUnitName(v.getUnitName());
                        key.setMainCyc(v.getMainCyc());
                        return key;
                    }));
                    Date time2 = new Date();
                    System.out.println("getTaskAllotDataPlus分组消耗时间："+(time2.getTime()-time1.getTime()));
                    if(!ObjectUtils.isEmpty(groupMap)){
                        for(Map.Entry<GroupKey,List<TaskCarPart>> entry:groupMap.entrySet()){
                            GroupKey groupKey = entry.getKey();
                            TaskAllotPacketEntity packetEntity = new TaskAllotPacketEntity();
                            packetEntity.setUnitCode(groupKey.getUnitCode());
                            packetEntity.setUnitName(groupKey.getUnitName());
                            packetEntity.setMainCyc(groupKey.getMainCyc());
                            packetEntity.setTrainsetId(groupKey.getTrainsetId());
                            packetEntity.setTrainsetName(groupKey.getTrainsetName());
                            packetEntity.setDayPlanId(groupKey.getDayPlanId());
                            packetEntity.setTaskAllotPacketId(groupKey.getPacketId());
                            //根据作业包id去包集合中获取作业包信息
                            if(!CollectionUtils.isEmpty(taskAllotPacketList)){
                                TaskAllotPacket taskAllotPacket = taskAllotPacketList.stream().filter(t -> groupKey.getPacketId().equals(t.getTaskAllotPacketId())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(taskAllotPacket)){
                                    packetEntity.setPacketCode(taskAllotPacket.getPacketCode());
                                    packetEntity.setPacketName(taskAllotPacket.getPacketName());
                                    packetEntity.setPacketType(taskAllotPacket.getPacketType());
                                }
                                //根据作业包id去包集合中获取部门信息
                                if(!CollectionUtils.isEmpty(taskAllotDeptList)){
                                    TaskAllotDept taskAllotDept = taskAllotDeptList.stream().filter(t -> groupKey.getDeptId().equals(t.getTaskAllotDeptId())).findFirst().orElse(null);
                                    if(!ObjectUtils.isEmpty(taskAllotDept)){
                                        packetEntity.setDeptCode(taskAllotDept.getDeptCode());
                                        packetEntity.setDeptName(taskAllotDept.getDeptName());
                                    }
                                    //把项目挂上去
                                    List<TaskAllotItemEntity> taskAllotItemEntityList = new ArrayList<>();
                                    List<TaskCarPart> groupCarPartList = entry.getValue();
                                    if(!CollectionUtils.isEmpty(groupCarPartList)){
                                        for(TaskCarPart taskCarPart:groupCarPartList){
                                            TaskAllotItemEntity itemEntity = new TaskAllotItemEntity();
                                            BeanUtils.copyProperties(taskCarPart,itemEntity);
                                            //项目下挂人
                                            if(!CollectionUtils.isEmpty(taskAllotPersonList)){
                                                List<TaskAllotPerson> personList = taskAllotPersonList.stream().filter(t -> taskCarPart.getProcessId().equals(t.getProcessId())).collect(Collectors.toList());
                                                List<TaskAllotPersonEntity> personEntityList = new ArrayList<>();
                                                if(!CollectionUtils.isEmpty(personList)){
                                                    for(TaskAllotPerson person:personList){
                                                        TaskAllotPersonEntity personEntity = new TaskAllotPersonEntity();
                                                        BeanUtils.copyProperties(person,personEntity);
                                                        personEntityList.add(personEntity);
                                                    }
                                                }
                                                //将人挂在项目上
                                                itemEntity.setTaskAllotPersonEntityList(personEntityList);
                                                //把项目添加到项目集合中
                                                taskAllotItemEntityList.add(itemEntity);
                                            }
                                        }
                                        //把项目集合挂到包下边
                                        packetEntity.setTaskAllotItemEntityList(taskAllotItemEntityList);
                                    }
                                    //把包添加到返回结果中
                                    result.add(packetEntity);
                                }
                            }
                        }
                    }
                }

                Date time3 = new Date();
                System.out.println("getTaskAllotDataPlus处理转换数据消耗时间："+(time3.getTime()-time1.getTime()));
            }
        }
        Date endTime = new Date();
        System.out.println("getTaskAllotDataPlus总消耗时间："+(endTime.getTime()-beginDate.getTime()));
        if(!CollectionUtils.isEmpty(branchCodeList)){
            result = result.stream().filter(t->branchCodeList.contains(t.getDeptCode())).collect(Collectors.toList());
        }
        return result;
    }


    //插入派工数据（过时）
    @Deprecated
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean setTaskAllotDataOverdue(List<TaskAllotPacketEntity> taskAllotPacketList) {
        //1.删除已有数据
        //1.1获取日计划ID、运用所编码
        String dayPlanId = taskAllotPacketList.stream().findFirst().get().getDayPlanId();
        String unitCode = taskAllotPacketList.stream().findFirst().get().getUnitCode();
        String deptCode = taskAllotPacketList.stream().findFirst().get().getDeptCode();
        List<String> deptCodeList = new ArrayList<>();
        deptCodeList.add(deptCode);
        //1.2将要删除的辆序表数据查询出来（根据运用所编码、日计划ID、班组编码、班组名称）
        Date beginTime = new Date();
        List<TaskCarPart> delTaskCarPartList = xzyMTaskAllotMapper.getCarPartList(dayPlanId, unitCode, deptCodeList);
        if (delTaskCarPartList != null && delTaskCarPartList.size() > 0) {
            //1.3需要删除的辆序表组件集合
            List<String> delCarPartIdList = delTaskCarPartList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
            //1.4需要删除的作业包表主键集合
            List<String> delPacketIdList = delTaskCarPartList.stream().map(t -> t.getTaskAllotPacketId()).collect(Collectors.toList());
            //1.5需要删除的部门表主键
            List<String> delDeptIdList = delTaskCarPartList.stream().map(t -> t.getTaskAllotDeptId()).collect(Collectors.toList());
            //1.6需要删除的派工人员表主键集合
            List<String> delPersonIdList = xzyMTaskAllotMapper.getDelPersonIdList(delCarPartIdList);
            //1.7需要删除的派工人员岗位表主键集合
            List<String> delPersonPostIdList = null;
            if (delPersonIdList != null && delPersonIdList.size() > 0) {
                delPersonPostIdList = xzyMTaskAllotMapper.getDelPersonPostIdList(delPersonIdList);
            }
            //1.8删除相关表数据
            if (delPacketIdList != null && delPacketIdList.size() > 0) {
                delPacketIdList = delPacketIdList.stream().distinct().collect(Collectors.toList());
                if (delPacketIdList.size() > 0) {
                    //删除派工包表
                    xzyMTaskAllotMapper.delPacketList(delPacketIdList);
                }
            }
            if (delDeptIdList != null && delDeptIdList.size() > 0) {
                delDeptIdList = delDeptIdList.stream().distinct().collect(Collectors.toList());
                if (delDeptIdList.size() > 0) {
                    //删除派工部门表
                    xzyMTaskAllotMapper.delDeptList(delDeptIdList);
                }
            }
            if (delCarPartIdList != null && delCarPartIdList.size() > 0) {
                //删除辆序表
                xzyMTaskAllotMapper.delCarParList(delCarPartIdList);
//                taskCarPartService.deleteBatchIds(delCarPartIdList);
            }
            if (delPersonIdList != null && delPersonIdList.size() > 0) {
                //删除人员表
                xzyMTaskAllotMapper.delPersonList(delPersonIdList);
            }
            if (delPersonPostIdList != null && delPersonPostIdList.size() > 0) {
                //删除岗位表
                xzyMTaskAllotMapper.delPersonPostList(delPersonPostIdList);
            }
        }
        Date time1 = new Date();
        System.out.println("setTaskAllotData删除数据消耗的时间为："+(time1.getTime()-beginTime.getTime()));

        //2.组织要插入的数据
        List<TaskAllotPacket> insertPacketList = new ArrayList<>();//插入派工包数据集合
        List<TaskCarPart> insertCarPartList = new ArrayList<>();//插入辆序表数据集合
        List<TaskAllotPerson> insertPersonList = new ArrayList<>();//插入派工人员表数据集合
        List<TaskAllotPersonPost> insertPostList = new ArrayList<>();//插入派工岗位表数据集合
        List<TaskAllotDept> insertDeptList = new ArrayList<>();//插入派工部门表数据集合

        List<TaskAllotPacketHis> insertPacketHisList = new ArrayList<>();//插入派工包历史表数据集合
        List<TaskCarPartHis> insertCarPartHisList = new ArrayList<>();//插入辆序历史表数据集合
        List<TaskAllotPersonHis> insertPersonHisList = new ArrayList<>();//插入派工人员历史表数据集合
        List<TaskAllotPersonPostHis> insertPostHisList = new ArrayList<>();//插入派工岗位历史表数据集合
        List<TaskAllotDeptHis> insertDeptHisList = new ArrayList<>();//插入派工部门历史表数据集合
        String publishCode = dayPlanId+unitCode+deptCode;
        if (taskAllotPacketList != null && taskAllotPacketList.size() > 0) {
            for (TaskAllotPacketEntity packetItem : taskAllotPacketList) {
                //2.1派工包数据
                TaskAllotPacket insertPacketItem = new TaskAllotPacket();
                String insertPacketId = UUID.randomUUID().toString();
                insertPacketItem.setTaskAllotPacketId(insertPacketId);
                insertPacketItem.setTaskId(packetItem.getTaskId());
                insertPacketItem.setPacketCode(packetItem.getPacketCode());
                insertPacketItem.setPacketName(packetItem.getPacketName());
                insertPacketItem.setPacketType(packetItem.getPacketType());
                insertPacketItem.setPublishCode(publishCode);
                insertPacketList.add(insertPacketItem);
                //派工包历史表
                TaskAllotPacketHis taskAllotPacketHis = new TaskAllotPacketHis();
                BeanUtils.copyProperties(insertPacketItem, taskAllotPacketHis);
                insertPacketHisList.add(taskAllotPacketHis);
                //2.2派工部门数据
                TaskAllotDept insertDeptItem = new TaskAllotDept();
                String insertDeptId = UUID.randomUUID().toString();
                insertDeptItem.setTaskAllotDeptId(insertDeptId);
                insertDeptItem.setDeptCode(packetItem.getDeptCode());
                insertDeptItem.setDeptName(packetItem.getDeptName());
                insertDeptItem.setPublishCode(publishCode);
                insertDeptList.add(insertDeptItem);
                //派工部门历史表
                TaskAllotDeptHis taskAllotDeptHis = new TaskAllotDeptHis();
                BeanUtils.copyProperties(insertDeptItem, taskAllotDeptHis);
                insertDeptHisList.add(taskAllotDeptHis);
                //2.3派工辆序表数据
                List<TaskAllotItemEntity> taskAllotItemEntityList = packetItem.getTaskAllotItemEntityList();
                if (taskAllotItemEntityList != null && taskAllotItemEntityList.size() > 0) {
                    for (TaskAllotItemEntity allotItem : taskAllotItemEntityList) {
                        TaskCarPart insertCarPart = new TaskCarPart();
                        String insertProcessId = UUID.randomUUID().toString();
                        insertCarPart.setProcessId(insertProcessId);
                        insertCarPart.setTaskAllotPacketId(insertPacketId);
                        insertCarPart.setTaskAllotDeptId(insertDeptId);
                        insertCarPart.setDayPlanId(packetItem.getDayPlanId());
                        insertCarPart.setTrainsetId(packetItem.getTrainsetId());
                        insertCarPart.setTrainsetName(packetItem.getTrainsetName());
                        insertCarPart.setUnitCode(packetItem.getUnitCode());
                        insertCarPart.setUnitName(packetItem.getUnitName());
                        insertCarPart.setItemCode(allotItem.getItemCode());
                        insertCarPart.setItemName(allotItem.getItemName());
                        insertCarPart.setRepairType(allotItem.getItemType());
                        insertCarPart.setArrageType(allotItem.getArrageType());
                        insertCarPart.setCarNo(allotItem.getCarNo());
                        insertCarPart.setTrainsetType(allotItem.getTrainsetType());
                        insertCarPart.setTaskItemId(allotItem.getTaskItemId());
                        insertCarPart.setMainCyc(packetItem.getMainCyc());
                        insertCarPart.setPartPosition(allotItem.getPartPosition());
                        insertCarPart.setPartName(allotItem.getPartName());
                        insertCarPart.setPartType(allotItem.getPartType());
                        insertCarPart.setDataSource(allotItem.getDataSource());
                        insertCarPart.setRemark(allotItem.getRemark());
                        insertCarPart.setRepairMode(allotItem.getRepairMode());
                        insertCarPart.setRecordTime(packetItem.getRecordTime());
                        insertCarPart.setRecorderCode(packetItem.getRecordCode());
                        insertCarPart.setRecorderName(packetItem.getRecordName());
                        insertCarPart.setPublishCode(allotItem.getPublishCode());
                        insertCarPart.setPublishCode(publishCode);
                        insertCarPartList.add(insertCarPart);
                        //派工辆序历史表
                        TaskCarPartHis taskCarPartHis = new TaskCarPartHis();
                        BeanUtils.copyProperties(insertCarPart, taskCarPartHis);
                        insertCarPartHisList.add(taskCarPartHis);
                        //2.4派工人员数据
                        List<TaskAllotPersonEntity> taskAllotPersonEntityList = allotItem.getTaskAllotPersonEntityList();
                        if (taskAllotPersonEntityList != null && taskAllotPersonEntityList.size() > 0) {
                            for (TaskAllotPersonEntity personItem : taskAllotPersonEntityList) {
                                TaskAllotPerson insertPersonItem = new TaskAllotPerson();
                                String isnertPersonId = UUID.randomUUID().toString();
                                insertPersonItem.setProcessId(insertProcessId);
                                insertPersonItem.setTaskAllotPersonId(isnertPersonId);
                                insertPersonItem.setWorkerId(personItem.getWorkerId());
                                insertPersonItem.setWorkerName(personItem.getWorkerName());
                                insertPersonItem.setWorkerType(personItem.getWorkerType());
                                insertPersonItem.setPublishCode(publishCode);
                                insertPersonList.add(insertPersonItem);
                                //派工人员历史表
                                TaskAllotPersonHis taskAllotPersonHis = new TaskAllotPersonHis();
                                BeanUtils.copyProperties(insertPersonItem, taskAllotPersonHis);
                                insertPersonHisList.add(taskAllotPersonHis);
                                //2.5派工岗位数据
                                List<TaskAllotPersonPostEntity> taskAllotPersonPostEntityList = personItem.getTaskAllotPersonPostEntityList();
                                if (taskAllotPersonPostEntityList != null && taskAllotPersonPostEntityList.size() > 0) {
                                    for (TaskAllotPersonPostEntity postItem : taskAllotPersonPostEntityList) {
                                        TaskAllotPersonPost insertPost = new TaskAllotPersonPost();
                                        String id = UUID.randomUUID().toString();
                                        insertPost.setId(id);
                                        insertPost.setTaskAllotPersonId(isnertPersonId);
                                        insertPost.setPostId(postItem.getPostId());
                                        insertPost.setPostName(postItem.getPostName());
                                        insertPost.setPublishCode(publishCode);
                                        insertPostList.add(insertPost);
                                        //派工人员岗位历史表
                                        TaskAllotPersonPostHis taskAllotPersonPostHis = new TaskAllotPersonPostHis();
                                        BeanUtils.copyProperties(insertPost, taskAllotPersonPostHis);
                                        insertPostHisList.add(taskAllotPersonPostHis);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Date time2 = new Date();
        System.out.println("setTaskAllotData组织数据消耗的时间为："+(time2.getTime()-time1.getTime()));
        //3.插入数据
        if (insertPacketList.size() > 0) {
            //3.1插入派工包数据
            taskAllotPacketService.insertBatch(insertPacketList);
            //3.2插入派工包历史表数据
            taskAllotPacketHisService.insertBatch(insertPacketHisList);
        }
        if (insertDeptList.size() > 0) {
            //3.3插入派工部门数据
            taskAllotDeptService.insertBatch(insertDeptList);
            //3.4插入派工部门历史表数据
            taskAllotDeptHisService.insertBatch(insertDeptHisList);
        }
        if (insertCarPartList.size() > 0) {
            //3.5插入派工辆序数据
            taskCarPartService.insertBatch(insertCarPartList);
            //3.6插入派工辆序历史数据
            taskCarPartHisService.insertBatch(insertCarPartHisList);
        }
        if (insertPersonList.size() > 0) {
            //3.7插入派工人员数据
            taskAllotPersonService.insertBatch(insertPersonList);
            //3.8插入派工人员历史表数据
            taskAllotPersonHisService.insertBatch(insertPersonHisList);
        }
        if (insertPostList.size() > 0) {
            //3.9插入派工岗位数据
            taskAllotPersonPostService.insertBatch(insertPostList);
            //3.10插入派工岗位历史表数据
            taskAllotPersonPostHisService.insertBatch(insertPostHisList);
        }

        Date time3 = new Date();
        System.out.println("setTaskAllotData插入数据消耗的时间为："+(time3.getTime()-time2.getTime()));
        return true;
    }


    //将一批派工数据的id转换
    @Transactional
    public boolean taskAllotConstId(String unitCode,String dayPlanId,String startDate,String endDate,String repairType,List<String> trainsetTypeList,List<String> trainsetIdList,List<String> itemCodeList,List<String> branchCodeList,List<String> workIdList,String packetName){
        //返回结果
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
        List<TaskCarPart> taskCarPartList = new ArrayList<>();
        List<String> processIdList = new ArrayList<>();
        List<TaskAllotDept> taskAllotDeptList = new ArrayList<>();
        List<String> deptIdList = new ArrayList<>();
        List<TaskAllotPacket> taskAllotPacketList = new ArrayList<>();
        List<String> packIdList = new ArrayList<>();
        List<TaskAllotPerson> taskAllotPersonList = new ArrayList<>();
        List<String> taskAllotPersonIdList = new ArrayList<>();
        List<TaskAllotPersonPost> taskAllotPersonPostList = new ArrayList<>();

        //1.查询carpart表获取辆序级别的数据
        List<ColumnParam<TaskCarPart, ?>> columnParamList = new ArrayList<>();
        if(StringUtils.isNotBlank(unitCode)){
            columnParamList.add(eqParam(TaskCarPart::getUnitCode,unitCode));
        }
        if(StringUtils.isNotBlank(dayPlanId)){
            columnParamList.add(eqParam(TaskCarPart::getDayPlanId,dayPlanId));
        }
        if(StringUtils.isNotBlank(repairType)){
            columnParamList.add(eqParam(TaskCarPart::getRepairType,repairType));
        }
        if(StringUtils.isNotBlank(dayPlanId)){
            columnParamList.add(eqParam(TaskCarPart::getDayPlanId,dayPlanId));
        }
        if(!CollectionUtils.isEmpty(trainsetTypeList)){
            columnParamList.add(inParam(TaskCarPart::getTrainsetType,trainsetTypeList));
        }
        if(!CollectionUtils.isEmpty(trainsetIdList)){
            columnParamList.add(inParam(TaskCarPart::getTrainsetId,trainsetIdList));
        }
        Date beginTime = new Date();
        taskCarPartList = MybatisPlusUtils.selectList(
            taskCarPartService,
            columnParamList
        );
        Date time1 = new Date();
        System.out.println("time1："+(time1.getTime()-beginTime.getTime()));
        //2.组织其它表查询条件
        if(!ObjectUtils.isEmpty(taskCarPartList)){
            processIdList = taskCarPartList.stream().map(t -> t.getProcessId()).distinct().collect(Collectors.toList());
            deptIdList = taskCarPartList.stream().map(t -> t.getTaskAllotDeptId()).distinct().collect(Collectors.toList());
            packIdList = taskCarPartList.stream().map(t -> t.getTaskAllotPacketId()).distinct().collect(Collectors.toList());
            //3.查询作业包表
            if(!CollectionUtils.isEmpty(deptIdList)){
                taskAllotDeptList = MybatisPlusUtils.selectList(
                    taskAllotDeptService,
                    inParam(TaskAllotDept::getTaskAllotDeptId,deptIdList)
                );
            }
            Date time2 = new Date();
            System.out.println("time2："+(time2.getTime()-time1.getTime()));
            //4.查询部门表
            if(!CollectionUtils.isEmpty(packIdList)){
                taskAllotPacketList = MybatisPlusUtils.selectList(
                    taskAllotPacketService,
                    inParam(TaskAllotPacket::getTaskAllotPacketId,packIdList)
                );
            }
            //5.查询人员表
            Date time3 = new Date();
            System.out.println("time3："+(time3.getTime()-time2.getTime()));
            if(!CollectionUtils.isEmpty(processIdList)){
                taskAllotPersonList = MybatisPlusUtils.selectList(
                    taskAllotPersonService,
                    inParam(TaskAllotPerson::getProcessId,processIdList)
                );
            }
            Date time4 = new Date();
            System.out.println("time4："+(time4.getTime()-time3.getTime()));
            //5.查询人员岗位表
            if(!CollectionUtils.isEmpty(taskAllotPersonList)){
                taskAllotPersonIdList = taskAllotPersonList.stream().map(t->t.getTaskAllotPersonId()).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(taskAllotPersonIdList)){
                    taskAllotPersonPostList = MybatisPlusUtils.selectList(
                        taskAllotPersonPostService,
                        inParam(TaskAllotPersonPost::getTaskAllotPersonId,taskAllotPersonIdList)
                    );
                }
            }
            Date time5 = new Date();
            System.out.println("time5："+(time5.getTime()-time4.getTime()));
        }
        Date endTime = new Date();
        long diffTime = endTime.getTime()-beginTime.getTime();
        System.out.println("执行所有sql并返回消耗的时间为："+diffTime);
        System.out.println("--------------------------------------------------------");
        Function<String, String> getId = getIdMapper();
        taskAllotDeptList.forEach(taskAllotDept -> {
            String deptId = getId.apply(taskAllotDept.getTaskAllotDeptId());
            taskAllotDept.setTaskAllotDeptId(deptId);
        });
        taskAllotPacketList.forEach(taskAllotPacket -> {
            String packetId = getId.apply(taskAllotPacket.getTaskAllotPacketId());
            taskAllotPacket.setTaskAllotPacketId(packetId);
            String taskId = getId.apply(taskAllotPacket.getTaskId());
            taskAllotPacket.setTaskId(taskId);
        });
        taskCarPartList.forEach(taskCarPart -> {
            String processId = getId.apply(taskCarPart.getProcessId());
            taskCarPart.setProcessId(processId);
            String deptId = getId.apply(taskCarPart.getTaskAllotDeptId());
            taskCarPart.setTaskAllotDeptId(deptId);
            String packetId = getId.apply(taskCarPart.getTaskAllotPacketId());
            taskCarPart.setTaskAllotPacketId(packetId);
        });
        taskAllotPersonList.forEach(taskAllotPerson -> {
            String personId = getId.apply(taskAllotPerson.getTaskAllotPersonId());
            taskAllotPerson.setTaskAllotPersonId(personId);
            String processId = getId.apply(taskAllotPerson.getProcessId());
            taskAllotPerson.setProcessId(processId);
        });
        taskAllotPersonPostList.forEach(taskAllotPersonPost -> {
            String personPostId = getId.apply(taskAllotPersonPost.getId());
            taskAllotPersonPost.setId(personPostId);
            String personId = getId.apply(taskAllotPersonPost.getTaskAllotPersonId());
            taskAllotPersonPost.setTaskAllotPersonId(personId);
        });
        //3.插入数据
        if (taskAllotPacketList.size() > 0) {
            //3.1插入派工包数据
            taskAllotPacketService.insertBatch(taskAllotPacketList);
        }
        if (taskAllotDeptList.size() > 0) {
            //3.3插入派工部门数据
            taskAllotDeptService.insertBatch(taskAllotDeptList);
        }
        if (taskCarPartList.size() > 0) {
            //3.5插入派工辆序数据
            taskCarPartService.insertBatch(taskCarPartList);
        }
        if (taskAllotPersonList.size() > 0) {
            //3.7插入派工人员数据
            taskAllotPersonService.insertBatch(taskAllotPersonList);
        }
        if (taskAllotPersonPostList.size() > 0) {
            //3.9插入派工岗位数据
            taskAllotPersonPostService.insertBatch(taskAllotPersonPostList);
        }
        return true;
    }

    private Function<String, String> getIdMapper() {
        Map<String, String> contentIdMap = new LinkedHashMap<>();
        return (oldId) -> {
            if (!contentIdMap.containsKey(oldId)) {
                contentIdMap.put(oldId, getNewId());
            }
            return contentIdMap.get(oldId);
        };
    }

    private String getNewId() {
        return String.valueOf(id++);
    }
    */

}
