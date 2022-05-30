package com.ydzbinfo.emis.trainRepair.workProcessMonitor.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.FaultSearch;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCycPersonInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.RemainFaultNewVO;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ShuntPlanModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertionPost;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertionRole;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkritertionPriRole;
import com.ydzbinfo.emis.trainRepair.repairmanagent.dao.XzyCWorkcritertionMapper;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.PersonPost;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPersonPostService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetPostionCurWithNextTrack;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateCurService;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntityBase;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IProcessCarPartService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IRfidCardSummaryService;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.dao.ProcessMonitorMapper;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.*;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.service.IProcessMonitorService;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessData;
import com.ydzbinfo.emis.trainRepair.workprocess.model.WorkTime;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessCarPart;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPerson;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.hussar.core.util.ToolUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * @author: 冯帅
 * @Date: 2021/5/24 14:57
 * @Description:
 */
@Service
public class ProcessMonitorServiceImpl implements IProcessMonitorService {

    //中台服务Id
    private final String midGroundId = CustomServiceNameEnum.MidGroundService.getId();

    //日志记录
    protected static final Logger logger = LoggerFactory.getLogger(ProcessMonitorServiceImpl.class);

    @Autowired
    private IXzyCWorkcritertionService xzyCWorkcritertionService;

    @Autowired
    ProcessMonitorMapper processMonitorMapper;

    //远程调用服务
    @Autowired
    IRemoteService remoteService;

    @Autowired
    IRepairMidGroundService repairMidGroundService;

    //岗位配置服务
    @Autowired
    IPersonPostService personpostService;

    //作业过程总表服务
    @Autowired
    IRfidCardSummaryService rfidCardSummaryService;

    //作业过程辆序表服务
    @Autowired
    IProcessCarPartService processCarPartService;
    @Autowired
    private TrackPowerStateCurService trackPowerStateCurService;

    @Autowired
    private XzyCWorkcritertionMapper xzyCWorkcritertionMapper;

    /**
     * @author: 冯帅
     * @Date: 2021/5/12
     * @Description: 作业过程监控分组key
     */
    @Data
    public static class ProcessMonitorGroupKey {
        private String unitCode;
        private String unitName;
        private String trackCode;
        private String trackName;
    }

    /**
     * 二级修作业包分组key
     */
    @Data
    public static class TaskPacketGroupKey {
        private String repairDeptCode;
        private String repairDeptName;
    }

    /**
     * 二级修作业项目分组key
     */
    @Data
    public static class TwoItemGroupKey {
        private String packetName;
        private String packetCode;
        private String itemCode;
        private String itemName;
    }

    /**
     * 岗位人员分组
     */
    @Data
    public static class PersonPostGroupKey {
        private String staffId;
        private String staffName;
    }

    /**
     * 获取一级修监控列表
     */
    @Override
    public List<QueryProcessMonitorTrack> getOneWorkProcessMonitorList(String unitCode, String dayPlanId, String trackCodesJsonStr, String trainsetNameStr) {
        //0.参数验证
        if (ObjectUtils.isEmpty(unitCode)) {
            throw new RuntimeException("运用所编码不能为空!");
        }
        if (ObjectUtils.isEmpty(dayPlanId)) {
            throw new RuntimeException("日计划不能为空!");
        }
        //1.获取股道基本信息
        List<QueryProcessMonitorTrack> queryProcessMonitorTrackList = this.getTrainsetInfo(unitCode, dayPlanId, trackCodesJsonStr, trainsetNameStr, "1");
        //强制转换对象
        if (queryProcessMonitorTrackList != null && queryProcessMonitorTrackList.size() > 0) {
            queryProcessMonitorTrackList.stream().map(trackItem -> {
                trackItem.getQueryProcessMonitorPlaList().stream().map(plaItem -> {
                    return (QueryOneProcessMonitorPla) plaItem;
                }).collect(Collectors.toList());
                return trackItem;
            }).collect(Collectors.toList());
        }
        //2.从中台获取一级修下发计划信息
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("1");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, "", packetTypeStrs, "", unitCode).stream().filter(t -> t.getTaskRepairCode().equals("1")).collect(Collectors.toList());
        //3.获取作业过程数据
        //总表
        List<RfidCardSummary> summaryList = MybatisPlusUtils.selectList(
                rfidCardSummaryService,
                eqParam(RfidCardSummary::getDayPlanId, dayPlanId),
                eqParam(RfidCardSummary::getFlag, "1")
        );
        //辆序表
        List<ProcessCarPart> carPartList = MybatisPlusUtils.selectList(
                processCarPartService,
                eqParam(ProcessCarPart::getUnitCode, unitCode),
                eqParam(ProcessCarPart::getDayPlanId, dayPlanId)
        );
        //作业人员表
        List<ProcessPerson> processPersonList = processMonitorMapper.getPersonByDayPlanIdAndPersonId(unitCode, dayPlanId);
        //4.循环股道列位，组装数据
        //循环股道
        queryProcessMonitorTrackList.stream().map(resItem -> {
            //循环列位
            resItem.getQueryProcessMonitorPlaList().stream().map(plaItem -> {
                String curTrainsetId = plaItem.getTrainsetId();
                if (curTrainsetId != null && !curTrainsetId.equals("")) {
                    //获取该车组所有的辆序信息集合
                    List<String> carNoList = CacheUtil.getDataUseThreadCache(
                            "remoteService.getCarNo_" + curTrainsetId,
                            () -> remoteService.getCarno(curTrainsetId)
                    );
                    //作业包信息拼接
                    List<ZtTaskPacketEntity> packetList = ztTaskPacketEntityList.stream().filter(t -> t.getTrainsetId().equals(curTrainsetId)).collect(Collectors.toList());
                    Map<TaskPacketGroupKey, List<ZtTaskPacketEntity>> packetGroupList = packetList.stream().collect(Collectors.groupingBy(v -> {
                        TaskPacketGroupKey key = new TaskPacketGroupKey();
                        key.setRepairDeptCode(v.getRepairDeptCode());
                        key.setRepairDeptName(v.getRepairDeptName());
                        return key;
                    }));
                    List<String> itemInfos = new ArrayList<>();
                    if (packetGroupList != null && packetGroupList.size() > 0) {
                        packetGroupList.forEach((groupKey, groupItem) -> {
                            String itemStr = groupKey.getRepairDeptName() + "：";
                            for (ZtTaskPacketEntity packItem : groupItem) {
                                itemStr += packItem.getPacketName() + "、";
                            }
                            if (itemStr.lastIndexOf("、") > 0) {
                                itemStr = itemStr.substring(0, itemStr.length() - 1);
                            }
                            itemInfos.add(itemStr);
                        });
                        if (itemInfos.size() > 0) {
                            plaItem.setItemInfos(itemInfos);
                        }
                    }
                    if (packetGroupList != null && packetGroupList.size() > 0) {
                        //根据车组获取作业标准
                        //根据车组ID获取作业标准数据集合
                        List<XzyCWorkcritertion> criterionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetIdOne(curTrainsetId);
                        //再加上已经新增的一级修作业过程，但是又被删除的作业标准配置
                        //调用履历接口获取车组基本信息
                        TrainsetBaseInfo trainsetBaseInfo = RemoteServiceCachedDataUtil.getTrainsetBaseinfoByID(curTrainsetId);
                        //根据车型和批次获取作业标准
                        //查询作业标准配置表
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("trainsetType", trainsetBaseInfo.getTraintype());
                        paramMap.put("trainsetSubType", trainsetBaseInfo.getTraintempid());
                        paramMap.put("trainsetId",curTrainsetId);
                        List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionMapper.getWorkcritertionListDelete(paramMap);
                        List<String> sCritertionidList = xzyCWorkcritertionList.stream().map(t -> t.getsCritertionid()).collect(Collectors.toList());
                        String sCritertionids = "('" + String.join("','", sCritertionidList) + "')";
                        List<XzyCWorkcritertionPost> xzyCWorkcritertionPostList = xzyCWorkcritertionMapper.getWorkcritertionPostList1(sCritertionids);
                        for (XzyCWorkcritertion item : xzyCWorkcritertionList) {
                            String critertionid = item.getsCritertionid();
                            List<XzyCWorkcritertionPost> itemPostList = xzyCWorkcritertionPostList.stream().filter(t -> t.getSCritertionid().equals(critertionid)).collect(Collectors.toList());
                            item.setXzyCWorkcritertionPostList(itemPostList);
                        }
                        criterionList.addAll(xzyCWorkcritertionList);
                        List<String> postIds = new ArrayList<>();
                        if (criterionList != null && criterionList.size() > 0) {
                            //获取作业标准中的POSTID
                            criterionList.stream().map(criterionItem -> {
                                List<String> postId = criterionItem.getXzyCWorkcritertionPostList().stream().map(t -> t.getPostCode()).collect(Collectors.toList());
                                postIds.addAll(postId);
                                return criterionItem;
                            }).collect(Collectors.toList());
                            //根据作业标准中的岗位集合获取配置岗位所对应的人员
//                            List<PersonPost> personPosts = processMonitorMapper.getPersonPostList(postIds);
                            //从派工数据中获取该项目已派工了的人员及其岗位
                            List<PersonPost> personPosts = processMonitorMapper.selectPersonPostList(unitCode, curTrainsetId, postIds, dayPlanId);
                            List<PersonPost> tempPersonPosts = new ArrayList<>();
                            //循环作业过程总表，将作业过程总表中有的人员数据且作业标准配置中没有此人员岗位的数据添加到作业标准配置集合中（解决没有配置此人但是此人也进行了作业的情况）
                            summaryList.stream().filter(t -> t.getTrainsetId().equals(curTrainsetId) && t.getTimeTag().equals("1")).map(summaryItem -> {
                                boolean isAdd = false;
                                if (personPosts != null && personPosts.size() > 0) {
                                    List<String> personList = personPosts.stream().filter(t -> StringUtils.isNotBlank(t.getDeptCode()) && t.getDeptCode().equals(summaryItem.getDeptCode())).map(PersonPost::getStaffId).collect(Collectors.toList());
                                    if (!personList.contains(summaryItem.getStuffId())) {
                                        isAdd = true;
                                    }else{
                                        //获取作业过程总表中当前项目所属的作业标准
                                        XzyCWorkcritertion currentItemCritertion = criterionList.stream().filter(v -> v.getsItemcode().equals(summaryItem.getItemCode())).findFirst().orElse(null);
                                        if(!ObjectUtils.isEmpty(currentItemCritertion)){
                                            //获取所属作业标准配置的岗位集合
                                            List<String> currentItemCritertionPostIdList = Optional.ofNullable(currentItemCritertion.getXzyCWorkcritertionPostList()).orElseGet(ArrayList::new).stream().map(v -> v.getPostCode()).collect(Collectors.toList());
                                            //获取当前人的派工岗位集合
                                            List<String> currentItemUserPostIds = Optional.ofNullable(personPosts).orElseGet(ArrayList::new).stream().filter(v->v.getStaffId().equals(summaryItem.getStuffId())).map(v -> v.getPostId()).collect(Collectors.toList());
                                            if(!CollectionUtils.isEmpty(currentItemCritertionPostIdList)&&!CollectionUtils.isEmpty(currentItemUserPostIds)){
                                                currentItemCritertionPostIdList.retainAll(currentItemUserPostIds);
                                                if(CollectionUtils.isEmpty(currentItemCritertionPostIdList)){
                                                    isAdd = true;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    isAdd = true;
                                }
                                if (isAdd) {
                                    //设置一个临时岗位ID
                                    String tempPostId = UUID.randomUUID().toString();
                                    //将此临时ID添加到岗位集合中
                                    postIds.add(tempPostId);
                                    //将此临时岗位ID添加到该项目所属的作业标准中的岗位集合中
                                    if (criterionList != null && criterionList.size() > 0) {
                                        Optional<XzyCWorkcritertion> first = criterionList.stream().filter(t -> t.getsItemcode().equals(summaryItem.getItemCode())).findFirst();
                                        if (first.isPresent()) {
                                            XzyCWorkcritertion tempCritertion = first.get();
                                            XzyCWorkcritertionPost tempCritertionPost = new XzyCWorkcritertionPost();
                                            tempCritertionPost.setId(UUID.randomUUID().toString());
                                            tempCritertionPost.setSCritertionid(tempCritertion.getsCritertionid());
                                            tempCritertionPost.setPostCode(tempPostId);
                                            tempCritertionPost.setPostName("临时岗位名称");
                                            tempCritertionPost.setFlag("1");
                                            tempCritertionPost.setType("1");
                                            tempCritertion.getXzyCWorkcritertionPostList().add(tempCritertionPost);
                                        }
                                    }
                                    //将此人添加到岗位集合配置集合中
                                    PersonPost tempPersonPost = new PersonPost();
                                    tempPersonPost.setId(UUID.randomUUID().toString());
                                    tempPersonPost.setPostId(tempPostId);
                                    tempPersonPost.setStaffId(summaryItem.getStuffId());
                                    tempPersonPost.setStaffName(summaryItem.getStuffName());
                                    tempPersonPost.setFlag("1");
                                    tempPersonPost.setUnitCode(unitCode);
                                    tempPersonPosts.add(tempPersonPost);
                                }
                                return summaryItem;
                            }).collect(Collectors.toList());
                            if (personPosts != null && personPosts.size() > 0) {
                                tempPersonPosts.addAll(personPosts);
                            }
                            //循环岗位配置，往返回人员集合中添加数据
                            List<OneWorker> oneWorkerList = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(tempPersonPosts)) {
                                //将人员按照staffId和staffName分组，得出分组之后每个人的岗位集合
                                Map<PersonPostGroupKey, List<PersonPost>> personPostGroupKeyListMap = tempPersonPosts.stream().collect(Collectors.groupingBy(v -> {
                                    PersonPostGroupKey key = new PersonPostGroupKey();
                                    key.setStaffId(v.getStaffId());
                                    key.setStaffName(v.getStaffName());
                                    return key;
                                }));
                                //循环分组
                                personPostGroupKeyListMap.forEach((personPostKey, personPostListItem) -> {
                                    //获取该人的岗位集合
                                    List<String> currentPostList = personPostListItem.stream().map(t -> t.getPostId()).collect(Collectors.toList());
                                    OneWorker oneWorker = new OneWorker();
                                    oneWorker.setWorkerId(personPostKey.getStaffId());
                                    oneWorker.setWorkerName(personPostKey.getStaffName());
                                    List<OneItem> oneItemList = new ArrayList<>();
                                    //循环作业标准配置，将人员所对应的项目添加进去
                                    //需要把原来删除的那条作业标准配置找回来。
                                    criterionList.stream().map(criterionItem -> {
                                        OneItem oneItem = new OneItem();
                                        String stuffId = criterionItem.getStuffId();
                                        if( (ToolUtil.isNotEmpty(criterionItem.getStuffId()) && personPostKey.getStaffId().equals(stuffId)) || ToolUtil.isEmpty(criterionItem.getStuffId()) ){
                                            //获取标准配置的岗位编码集合
                                            List<String> criPostIdList = criterionItem.getXzyCWorkcritertionPostList().stream().map(t -> t.getPostCode()).collect(Collectors.toList());
                                            //如果该人的岗位集合和标准配置中的岗位集合有交集，则把该项目挂到该人下边
                                            boolean contain = false;
                                            for (String currentPost : currentPostList) {
                                                for (String cirPost : criPostIdList) {
                                                    if (currentPost.equals(cirPost)) {
                                                        contain = true;
                                                    }
                                                }
                                            }
                                            if (contain) {
                                                oneItem.setOneItemCode(criterionItem.getsItemcode());
                                                oneItem.setOneItemName(criterionItem.getsItemname());
                                                //设置项目辆序完成状态
                                                List<OneItem.OneItemCarNoState> oneItemCarNoStateList = new ArrayList<>();
                                                //获取总表开始记录
                                                List<RfidCardSummary> startSummaryList = summaryList.stream().filter(t -> t.getStuffId().equals(personPostKey.getStaffId()) && t.getTimeTag().equals("1") && t.getTrainsetId().equals(curTrainsetId) && String.valueOf(t.getItemCode()).equals(criterionItem.getsItemcode())).collect(Collectors.toList());
                                                RfidCardSummary startSummary = new RfidCardSummary();
                                                if (startSummaryList != null && startSummaryList.size() > 0) {
                                                    startSummary = startSummaryList.get(0);
                                                }
                                                //获取总表结束记录
                                                List<RfidCardSummary> endSummaryList = summaryList.stream().filter(t -> t.getStuffId().equals(personPostKey.getStaffId()) && t.getTimeTag().equals("4") && t.getTrainsetId().equals(curTrainsetId) && String.valueOf(t.getItemCode()).equals(criterionItem.getsItemcode())).collect(Collectors.toList());
                                                RfidCardSummary endSummary = new RfidCardSummary();
                                                if (endSummaryList != null && endSummaryList.size() > 0) {
                                                    endSummary = endSummaryList.get(0);
                                                }
                                                if (startSummaryList == null || startSummaryList.size() == 0) {//未开始
                                                    for (int i = 0; i < carNoList.size(); i++) {
                                                        OneItem.OneItemCarNoState oneItemCarNoState = new OneItem().new OneItemCarNoState();
                                                        String carNoItem = carNoList.get(i).substring(carNoList.get(i).length() - 2, carNoList.get(i).length());
                                                        oneItemCarNoState.setCarNo(carNoItem);
                                                        oneItemCarNoState.setCarNoState("0");
                                                        oneItemCarNoStateList.add(oneItemCarNoState);
                                                    }
                                                    //设置项目的完成状态
                                                    oneItem.setOneItemState("0");
                                                } else if (endSummaryList == null || endSummaryList.size() == 0) {//已开始未结束
                                                    for (int i = 0; i < carNoList.size(); i++) {
                                                        OneItem.OneItemCarNoState oneItemCarNoState = new OneItem().new OneItemCarNoState();
                                                        String carNoItem = carNoList.get(i).substring(carNoList.get(i).length() - 2, carNoList.get(i).length());
                                                        //找到他在作业过程中是否作业了此辆序
                                                        long count = 0;
                                                        List<ProcessPerson> carPersonList = processPersonList.stream().filter(t -> t.getWorkerId().equals(personPostKey.getStaffId())).collect(Collectors.toList());
                                                        if (carPersonList != null) {
                                                            List<String> processIdList = carPersonList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
                                                            count = carPartList.stream().filter(t -> t.getTrainsetId().equals(plaItem.getTrainsetId()) && String.valueOf(t.getCarNo()).equals(carNoItem) && processIdList.contains(t.getProcessId())).count();
                                                        }
                                                        if (count > 0) {
                                                            oneItemCarNoState.setCarNoState("2");
                                                            oneItemCarNoState.setCarNo(carNoItem);
                                                            oneItemCarNoStateList.add(oneItemCarNoState);
                                                        } else {
                                                            if (i < carNoList.size() - 1) {
                                                                //String nextCarNoItem = carNoList.get(i + 1).substring(carNoList.get(i + 1).length() - 2, carNoList.get(i + 1).length());
                                                                oneItemCarNoState.setCarNoState("1");
                                                            } else {
                                                                oneItemCarNoState.setCarNoState("0");
                                                            }
                                                            oneItemCarNoState.setCarNo(carNoItem);
                                                            oneItemCarNoStateList.add(oneItemCarNoState);
                                                        }
                                                    }
                                                } else {//已开始且已结束
                                                    for (int i = 0; i < carNoList.size(); i++) {
                                                        OneItem.OneItemCarNoState oneItemCarNoState = new OneItem().new OneItemCarNoState();
                                                        //从履历获取的辆序截取最后两位
                                                        String carNoItem = carNoList.get(i).substring(carNoList.get(i).length() - 2, carNoList.get(i).length());
                                                        //找到他在作业过程中是否作业了此辆序
                                                        long count = 0;
                                                        List<ProcessPerson> carPersonList = processPersonList.stream().filter(t -> t.getWorkerId().equals(personPostKey.getStaffId())).collect(Collectors.toList());
                                                        if (carPersonList != null) {
                                                            List<String> processIdList = carPersonList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
                                                            count = carPartList.stream().filter(t -> t.getTrainsetId().equals(plaItem.getTrainsetId()) && String.valueOf(t.getCarNo()).equals(carNoItem) && processIdList.contains(t.getProcessId())).count();
                                                        }
                                                        if (count > 0) {
                                                            oneItemCarNoState.setCarNoState("2");
                                                        } else {
                                                            oneItemCarNoState.setCarNoState("0");
                                                        }
                                                        oneItemCarNoState.setCarNo(carNoItem);
                                                        oneItemCarNoStateList.add(oneItemCarNoState);
                                                    }
                                                    //作业时长
                                                    long totalTime = (endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 / 60;
                                                    //计算超欠时状态
                                                    if (criterionItem.getiMinworktime() != null && totalTime < criterionItem.getiMinworktime()) {
                                                        oneItem.setOneItemState("1");
                                                    } else if (criterionItem.getiMinworktime() != null && totalTime > criterionItem.getiMaxworktime()) {
                                                        oneItem.setOneItemState("2");
                                                    }
                                                }
                                                oneItem.setOneItemCarNoStateList(oneItemCarNoStateList);
                                                oneItemList.add(oneItem);
                                            }
                                        }
                                        return oneItem;
                                    }).collect(Collectors.toList());
                                    oneWorker.setOneItemList(oneItemList);
                                    oneWorkerList.add(oneWorker);
                                });
                            }
                            /** 暂时注释掉
                             oneWorkerList = tempPersonPosts.stream().map(personItem -> {
                             OneWorker oneWorker = new OneWorker();
                             oneWorker.setWorkerId(personItem.getStaffId());
                             oneWorker.setWorkerName(personItem.getStaffName());
                             List<OneItem> oneItemList = new ArrayList<>();
                             //循环作业标准配置，将人员所对应的项目添加进去
                             criterionList.stream().map(criterionItem -> {
                             OneItem oneItem = new OneItem();
                             List<String> criterionPostList = criterionItem.getXzyCWorkcritertionPostList().stream().filter(t -> t.getPostCode().equals(personItem.getPostId())).map(t -> t.getPostCode()).collect(Collectors.toList());
                             if (criterionPostList.contains(personItem.getPostId())) {
                             oneItem.setOneItemCode(criterionItem.getsItemcode());
                             oneItem.setOneItemName(criterionItem.getsItemname());
                             //设置项目辆序完成状态
                             List<OneItem.OneItemCarNoState> oneItemCarNoStateList = new ArrayList<>();
                             //获取总表开始记录
                             List<RfidCardSummary> startSummaryList = summaryList.stream().filter(t -> t.getStuffId().equals(personItem.getStaffId()) && t.getTimeTag().equals("1") && t.getTrainsetId().equals(curTrainsetId) && String.valueOf(t.getItemCode()).equals(criterionItem.getsItemcode())).collect(Collectors.toList());
                             RfidCardSummary startSummary = new RfidCardSummary();
                             if (startSummaryList != null && startSummaryList.size() > 0) {
                             startSummary = startSummaryList.get(0);
                             }
                             //获取总表结束记录
                             List<RfidCardSummary> endSummaryList = summaryList.stream().filter(t -> t.getStuffId().equals(personItem.getStaffId()) && t.getTimeTag().equals("4") && t.getTrainsetId().equals(curTrainsetId) && String.valueOf(t.getItemCode()).equals(criterionItem.getsItemcode())).collect(Collectors.toList());
                             RfidCardSummary endSummary = new RfidCardSummary();
                             if (endSummaryList != null && endSummaryList.size() > 0) {
                             endSummary = endSummaryList.get(0);
                             }
                             if (startSummaryList == null || startSummaryList.size() == 0) {//未开始
                             for (int i = 0; i < carNoList.size(); i++) {
                             OneItem.OneItemCarNoState oneItemCarNoState = new OneItem().new OneItemCarNoState();
                             String carNoItem = carNoList.get(i).substring(carNoList.get(i).length() - 2, carNoList.get(i).length());
                             oneItemCarNoState.setCarNo(carNoItem);
                             oneItemCarNoState.setCarNoState("0");
                             oneItemCarNoStateList.add(oneItemCarNoState);
                             }
                             //设置项目的完成状态
                             oneItem.setOneItemState("0");
                             } else if (endSummaryList == null || endSummaryList.size() == 0) {//已开始未结束
                             for (int i = 0; i < carNoList.size(); i++) {
                             OneItem.OneItemCarNoState oneItemCarNoState = new OneItem().new OneItemCarNoState();
                             String carNoItem = carNoList.get(i).substring(carNoList.get(i).length() - 2, carNoList.get(i).length());
                             //找到他在作业过程中是否作业了此辆序
                             long count = 0;
                             List<ProcessPerson> carPersonList = processPersonList.stream().filter(t -> t.getWorkerId().equals(personItem.getStaffId())).collect(Collectors.toList());
                             if (carPersonList != null) {
                             List<String> processIdList = carPersonList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
                             count = carPartList.stream().filter(t -> t.getTrainsetId().equals(plaItem.getTrainsetId()) && String.valueOf(t.getCarNo()).equals(carNoItem) && processIdList.contains(t.getProcessId())).count();
                             }
                             if (count > 0) {
                             oneItemCarNoState.setCarNoState("2");
                             oneItemCarNoState.setCarNo(carNoItem);
                             oneItemCarNoStateList.add(oneItemCarNoState);
                             } else {
                             if (i < carNoList.size() - 1) {
                             //String nextCarNoItem = carNoList.get(i + 1).substring(carNoList.get(i + 1).length() - 2, carNoList.get(i + 1).length());
                             oneItemCarNoState.setCarNoState("1");
                             } else {
                             oneItemCarNoState.setCarNoState("0");
                             }
                             oneItemCarNoState.setCarNo(carNoItem);
                             oneItemCarNoStateList.add(oneItemCarNoState);
                             }
                             }
                             } else {//已开始且已结束
                             for (int i = 0; i < carNoList.size(); i++) {
                             OneItem.OneItemCarNoState oneItemCarNoState = new OneItem().new OneItemCarNoState();
                             //从履历获取的辆序截取最后两位
                             String carNoItem = carNoList.get(i).substring(carNoList.get(i).length() - 2, carNoList.get(i).length());
                             //找到他在作业过程中是否作业了此辆序
                             long count = 0;
                             List<ProcessPerson> carPersonList = processPersonList.stream().filter(t -> t.getWorkerId().equals(personItem.getStaffId())).collect(Collectors.toList());
                             if (carPersonList != null) {
                             List<String> processIdList = carPersonList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
                             count = carPartList.stream().filter(t -> t.getTrainsetId().equals(plaItem.getTrainsetId()) && String.valueOf(t.getCarNo()).equals(carNoItem) && processIdList.contains(t.getProcessId())).count();
                             }
                             if (count > 0) {
                             oneItemCarNoState.setCarNoState("2");
                             } else {
                             oneItemCarNoState.setCarNoState("0");
                             }
                             oneItemCarNoState.setCarNo(carNoItem);
                             oneItemCarNoStateList.add(oneItemCarNoState);
                             }
                             //作业时长
                             long totalTime = (endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 / 60;
                             //计算超欠时状态
                             if (criterionItem.getiMinworktime() != null && totalTime < criterionItem.getiMinworktime()) {
                             oneItem.setOneItemState("1");
                             } else if (criterionItem.getiMinworktime() != null && totalTime > criterionItem.getiMaxworktime()) {
                             oneItem.setOneItemState("2");
                             }
                             }
                             oneItem.setOneItemCarNoStateList(oneItemCarNoStateList);
                             oneItemList.add(oneItem);
                             }
                             return oneItem;
                             }).collect(Collectors.toList());
                             oneWorker.setOneItemList(oneItemList);
                             return oneWorker;
                             }).collect(Collectors.toList());
                             */
                            ((QueryOneProcessMonitorPla) plaItem).setOneWorkerList(oneWorkerList);
                            ((QueryOneProcessMonitorPla) plaItem).setIsCriterion("1");
                        } else {
                            ((QueryOneProcessMonitorPla) plaItem).setIsCriterion("0");
                        }
                    }
                }
                return plaItem;
            }).collect(Collectors.toList());
            return resItem;
        }).collect(Collectors.toList());
        List<QueryProcessMonitorTrack> processMonitorTracks = queryProcessMonitorTrackList.stream().sorted(Comparator.comparing(QueryProcessMonitorTrack::getSort)).collect(Collectors.toList());
        return processMonitorTracks;
    }

    /**
     * 获取二级修监控列表
     */
    @Override
    public List<QueryProcessMonitorTrack> getTwoWorkProcessMonitorList(String unitCode, String dayPlanId, String trackCodesJsonStr, String trainsetNameStr) {
        //0.参数验证
        if (ObjectUtils.isEmpty(unitCode)) {
            throw new RuntimeException("运用所编码不能为空!");
        }
        if (ObjectUtils.isEmpty(dayPlanId)) {
            throw new RuntimeException("日计划不能为空!");
        }
        //1.获取股道基本信息
        List<QueryProcessMonitorTrack> queryProcessMonitorTrackList = this.getTrainsetInfo(unitCode, dayPlanId, trackCodesJsonStr, trainsetNameStr, "2");
        //强制转换对象
        if (queryProcessMonitorTrackList != null && queryProcessMonitorTrackList.size() > 0) {
            queryProcessMonitorTrackList.stream().map(trackItem -> {
                trackItem.getQueryProcessMonitorPlaList().stream().map(plaItem -> {
                    return (QueryTwoProcessMonitorPla) plaItem;
                }).collect(Collectors.toList());
                return trackItem;
            }).collect(Collectors.toList());
        }
        //2.从中台获取二级修下发计划信息
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("1");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, "", packetTypeStrs, "", unitCode).stream().filter(t -> t.getTaskRepairCode().equals("2")).collect(Collectors.toList());
        //3.从数据库获取二级修作业过程数据
        //4.组装数据
        queryProcessMonitorTrackList.stream().map(resItem -> {
            resItem.getQueryProcessMonitorPlaList().stream().map(plaItem -> {
                String curTrainsetId = plaItem.getTrainsetId();
                if (curTrainsetId != null && !curTrainsetId.equals("")) {
                    //根据车组过滤作业包信息
                    List<ZtTaskPacketEntity> packetList = ztTaskPacketEntityList.stream().filter(t -> t.getTrainsetId().equals(curTrainsetId)).collect(Collectors.toList());
                    //获取作业包中的项目详细信息
                    List<TwoItem> twoItemList = this.getTwoItemList(unitCode, dayPlanId, curTrainsetId, "", "");
                    //作业包信息
                    List<WorkPacket> workPacketList = packetList.stream().map(taskPacket -> {
                        WorkPacket packet = new WorkPacket();
                        packet.setPacketCode(taskPacket.getPacketCode());
                        packet.setPacketName(taskPacket.getPacketName());
                        packet.setPacketTotalCount(taskPacket.getLstTaskItemInfo().stream().filter(distinctByKey(ZtTaskItemEntity::getItemCode)).collect(Collectors.toList()).size());
                        List<TwoItem> curTwoItemList = twoItemList.stream().filter(
                                t -> t.getTwoPacketCode().equals(taskPacket.getPacketCode()) &&
                                        t.getTwoPacketName().equals(taskPacket.getPacketName()) &&
                                        t.getTwoItemState().equals("1"))
                                .collect(Collectors.toList());
                        if (curTwoItemList != null && curTwoItemList.size() > 0) {
                            packet.setPacketEndCount(curTwoItemList.size());
                        }
                        return packet;
                    }).collect(Collectors.toList());
                    ((QueryTwoProcessMonitorPla) plaItem).setWorkPacketList(workPacketList);
                    //作业包信息拼接
                    Map<TaskPacketGroupKey, List<ZtTaskPacketEntity>> packetGroupList = packetList.stream().collect(Collectors.groupingBy(v -> {
                        TaskPacketGroupKey key = new TaskPacketGroupKey();
                        key.setRepairDeptCode(v.getRepairDeptCode());
                        key.setRepairDeptName(v.getRepairDeptName());
                        return key;
                    }));
                    List<String> itemInfos = new ArrayList<>();
                    if (packetGroupList != null && packetGroupList.size() > 0) {
                        packetGroupList.forEach((groupKey, groupItem) -> {
                            String itemStr = groupKey.getRepairDeptName() + "：";
                            for (ZtTaskPacketEntity packItem : groupItem) {
                                itemStr += packItem.getPacketName() + "、";
                            }
                            if (itemStr.lastIndexOf("、") > 0) {
                                itemStr = itemStr.substring(0, itemStr.length() - 1);
                            }
                            itemInfos.add(itemStr);
                        });
                        if (itemInfos.size() > 0) {
                            plaItem.setItemInfos(itemInfos);
                        }
                    }
                }
                return plaItem;
            }).collect(Collectors.toList());
            return resItem;
        }).collect(Collectors.toList());
        return queryProcessMonitorTrackList;
    }

    //根据指定字段去重过滤器
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return object -> seen.putIfAbsent(keyExtractor.apply(object), Boolean.TRUE) == null;
    }

    /**
     * 获取二级修项目列表
     */
    @Override
    public List<TwoItem> getTwoItemList(String unitCode, String dayPlanId, String trainsetId, String packetCode, String packetName) {
        List<TwoItem> twoItemList = new ArrayList<>();
        //1.从中台获取二级修下发计划信息
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("1");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        //获取计划全部数据
        List<ZtTaskPacketEntity> packetList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, packetTypeStrs, "", unitCode).stream().filter(t -> t.getTaskRepairCode().equals("2")).collect(Collectors.toList());
        //2.根据作业包编码、名称过滤数据
        if (packetCode != null && !packetCode.equals("")) {
            packetList = packetList.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
        }
        if (packetName != null && !packetName.equals("")) {
            packetList = packetList.stream().filter(t -> t.getPacketName().equals(packetName)).collect(Collectors.toList());
        }
        //3.从数据库查询二级修的作业过程数据
        List<ProcessData> twoProcess = processMonitorMapper.getTwoProcess(unitCode, dayPlanId, trainsetId, packetCode);

        //4.组织数据
        if (packetList != null && packetList.size() > 0) {
            for (ZtTaskPacketEntity packetItem : packetList) {
                List<ZtTaskItemEntity> itemList = packetItem.getLstTaskItemInfo();
                if (itemList != null && itemList.size() > 0) {
                    Map<TwoItemGroupKey, List<ZtTaskItemEntity>> groupList = itemList.stream().collect(Collectors.groupingBy(v -> {
                        TwoItemGroupKey key = new TwoItemGroupKey();
//                        key.setPacketCode(packetItem.getPacketCode());
//                        key.setPacketName(packetItem.getPacketName());
                        key.setItemCode(v.getItemCode());
                        key.setItemName(v.getItemName());
                        return key;
                    }));
                    groupList.forEach((groupKey, groupItem) -> {
                        TwoItem twoItem = new TwoItem();
                        twoItem.setTwoPacketName(packetItem.getPacketName());
                        twoItem.setTwoPacketCode(packetItem.getPacketCode());
                        twoItem.setTwoItemCode(groupKey.getItemCode());
                        twoItem.setTwoItemName(groupKey.getItemName());
                        List<String> planCarNo = groupItem.stream().map(t -> t.getCarNo()).distinct().collect(Collectors.toList());

                        //查询已结束的项目
                        List<NoMainCycPersonInfo> noMainCycPersonInfos = processCarPartService.selectCarPartEndItemList(trainsetId, dayPlanId, "1", groupKey.getItemCode(), "");
                        if (noMainCycPersonInfos.size() > 0) {
                            noMainCycPersonInfos = noMainCycPersonInfos.stream().filter(t -> t.getItemTimeState().equals("4")).collect(Collectors.toList());
                            if (noMainCycPersonInfos.size() > 0) {
                                //取出其中carNo
                                List<String> planCarNoList = noMainCycPersonInfos.stream().map(NoMainCycPersonInfo::getCarNo).distinct().collect(Collectors.toList());
                                if (planCarNoList.size() > 0) {
                                    if (planCarNoList.containsAll(planCarNo)) {
                                        twoItem.setTwoItemState("1");
                                    } else {
                                        twoItem.setTwoItemState("0");
                                    }
                                } else {
                                    twoItem.setTwoItemState("0");
                                }
                            } else {
                                twoItem.setTwoItemState("0");
                            }
                        } else {
                            twoItem.setTwoItemState("0");
                        }
                        //List<String> processCarNo = twoProcess.stream().filter(t -> t.getItemCode().equals(groupKey.getItemCode())).map(t -> t.getCarNo()).collect(Collectors.toList());
                        List<String> processPerson = twoProcess.stream().filter(t -> t.getItemCode().equals(groupKey.getItemCode())).map(t -> t.getWorkerName()).distinct().collect(Collectors.toList());
                        twoItem.setTwoItemCarNos(planCarNo);
                        twoItem.setTwoItemPerson(processPerson);
                        twoItemList.add(twoItem);
                    });
                }
            }
        }
        return twoItemList;
    }

    /**
     * 获取车组、故障、外协信息
     */
    public List<QueryProcessMonitorTrack> getTrainsetInfo(String unitCode, String dayPlanId, String trackCodesJsonStr, String trainsetNameStr, String repairType) {
        List<TrainsetBaseInfo> trainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
        Map<String, TrainsetBaseInfo> trainsetBaseInfoByTrainsetId = CommonUtils.collectionToMap(trainsetBaseInfos, TrainsetBaseInfo::getTrainsetid);
        //返回数据集合
        List<QueryProcessMonitorTrack> resList = new ArrayList<>();
        //1.获取该运用所下班所有的股道信息
        List<String> trackCodeList = Arrays.asList(trackCodesJsonStr.split(","));
        List<ZtTrackAreaEntity> trackAreaEntities = trackPowerStateCurService.getTrackAreaByDept(unitCode);
        List<ZtTrackEntity> ztTrackEntities = new ArrayList<>();
        if (trackAreaEntities != null && trackAreaEntities.size() > 0) {
            for (ZtTrackAreaEntity areaItem : trackAreaEntities) {
                ztTrackEntities.addAll(areaItem.getLstTrackInfo());
            }
        }
        ztTrackEntities = ztTrackEntities.stream().filter(t -> trackCodeList.contains(String.valueOf(t.getTrackCode()))).collect(Collectors.toList());

        // JSONObject trainsetPostion = new RestRequestKitUseLogger<JSONObject>(midGroundId, logger) {
        // }.getObject("/iTrainsetPostIonCur/getTrainsetPostIon?unitCode=" + unitCode + "&trackCodesJsonStr=" + trackCodesJsonStr + "&trainsetNameStr=" + trainsetNameStr + "&pageNum=" + 0 + "&pageSize=" + 10000);
        // List<TrainsetPostionCur> trainsetPostionCurList = trainsetPostion.getJSONObject("data").getJSONArray("trainsetPostIonCurs").toJavaList(TrainsetPostionCur.class);

        //2.从中台获取车组所在股道位置信息
        List<TrainsetPostionCurWithNextTrack> trainsetPostionCurList = repairMidGroundService.getTrainsetPosition(unitCode, trackCodesJsonStr, trainsetNameStr);
        //1.2根据前台传过来的股道集合CODE，将没有车的股道也添加到位置信息集合中
        List<String> postionCodeList = trainsetPostionCurList.stream().map(t -> t.getTrackCode()).collect(Collectors.toList());
        trackCodeList.stream().map(codeItem -> {
            if (!postionCodeList.contains(codeItem)) {
                TrainsetPostionCurWithNextTrack trainsetPostionCur = new TrainsetPostionCurWithNextTrack();
                trainsetPostionCur.setUnitCode(unitCode);
                trainsetPostionCur.setTrackCode(codeItem);
                trainsetPostionCurList.add(trainsetPostionCur);
            }
            return codeItem;
        }).collect(Collectors.toList());
        //3.从中台获取股道供断电信息
        JSONObject trackPower = new RestRequestKitUseLogger<JSONObject>(midGroundId, logger) {
        }.getObject("/iTrackPowerStateCur/getTrackPowerInfo?trackCodeList=" + trackCodesJsonStr + "&unitCodeList=" + unitCode);
        List<TrackPowerStateCur> trackPowerStateCurList = trackPower.getJSONArray("data").toJavaList(TrackPowerStateCur.class);
        //4.从中台获取外协、故障任务信息
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("6");
        packetTypeCodeList.add("5");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, "", packetTypeStrs, "", unitCode);
        //5.从数据库获取确认过的外协任务信息
        List<ProcessData> integrationProcessList = processMonitorMapper.getIntegrationProcess(unitCode, dayPlanId, "");
        String faultTrainsetId = StringUtils.join(trainsetPostionCurList.stream().map(v -> v.getTrainsetId()).collect(Collectors.toList()), ",");
        //获取故障信息
        // List<FaultItem> faultItems = new ArrayList<>();
        List<FaultItem> faultItems = this.getFaultList(unitCode, dayPlanId, faultTrainsetId, "");
        Map<String, List<FaultItem>> groupFaultItemByTrainsetId = faultItems.stream().collect(Collectors.groupingBy(FaultItem::getTrainsetId));

        //6.组织数据
        //6.1循环股道
        ztTrackEntities.stream().map(trackItem -> {
            //6.2股道基本信息
            String currentTrackCode = String.valueOf(trackItem.getTrackCode());
            QueryProcessMonitorTrack monitorTrack = new QueryProcessMonitorTrack();
            monitorTrack.setUnitCode(trackItem.getDeptCode());
            monitorTrack.setUnitName(trackItem.getDeptName());
            monitorTrack.setTrackCode(currentTrackCode);
            monitorTrack.setTrackName(trackItem.getTrackName());
            monitorTrack.setSort(trackItem.getSort());
            //6.2列位集合
            List<QueryProcessMonitorPla> monitorPlaList = new ArrayList<>();
            //6.3列位
            QueryProcessMonitorPla monitorPla;
            //6.4获取该股道上的车辆情况
            List<ZtTrackPositionEntity> lstTrackPositionInfo = trackItem.getLstTrackPositionInfo();
            if (lstTrackPositionInfo != null && lstTrackPositionInfo.size() > 0) {
                int forCount = lstTrackPositionInfo.size();
                for (int i = 0; i < forCount; i++) {
                    ZtTrackPositionEntity plaItem = lstTrackPositionInfo.get(i);
                    List<TrainsetPostionCur> plaCurs = trainsetPostionCurList.stream().filter(t -> t.getTrackCode().equals(currentTrackCode) && (t.getHeadDirectionPlaCode()).equals(String.valueOf(plaItem.getTrackPositionCode()))||t.getTailDirectionPlaCode().equals(String.valueOf(plaItem.getTrackPositionCode()))).collect(Collectors.toList());
                    String currentTrackPlaCode = String.valueOf(plaItem.getTrackPositionCode());
                    if (repairType.equals("1")) {
                        monitorPla = new QueryOneProcessMonitorPla();
                    } else {
                        monitorPla = new QueryTwoProcessMonitorPla();
                    }
                    //列位基本信息
                    monitorPla.setTrackPlaCode(currentTrackPlaCode);
                    monitorPla.setTrackPlaName(plaItem.getTrackPostionName());
                    //列位供断电信息
                    List<TrackPowerStateCur> stateCurs = trackPowerStateCurList.stream().filter(t -> t.getTrackCode().equals(currentTrackCode) && t.getTrackPlaCode().equals(currentTrackPlaCode)).collect(Collectors.toList());
                    if (stateCurs != null && stateCurs.size() > 0) {
                        monitorPla.setPowerState(stateCurs.get(0).getState());
                    }
                    //列位上车辆信息
                    if (plaCurs != null && plaCurs.size() > 0) {
                        TrainsetPostionCur plaCur = plaCurs.get(0);
                        if (plaCur != null) {
                            String curTrainsetId = plaCur.getTrainsetId();
                            if (curTrainsetId != null && !curTrainsetId.equals("")) {
                                monitorPla.setTrainsetId(curTrainsetId);
                                //如果当前车组ID不为空，则取调用履历接口获取该车组的车型
                                TrainsetBaseInfo trainsetBaseInfo = trainsetBaseInfoByTrainsetId.get(curTrainsetId);
                                if (trainsetBaseInfo != null) {
                                    monitorPla.setTrainsetType(trainsetBaseInfo.getTraintype());
                                }
                                monitorPla.setTrainsetName(plaCur.getTrainsetName());
                                monitorPla.setHeadDirection(plaCur.getHeadDirection());
                                monitorPla.setHeadDirectionPla(plaCur.getHeadDirectionPla());
                                monitorPla.setHeadDirectionPlaCode(plaCur.getHeadDirectionPlaCode());
                                monitorPla.setTailDirection(plaCur.getTailDirection());
                                monitorPla.setTailDirectionPla(plaCur.getTailDirectionPla());
                                monitorPla.setTailDirectionPlaCode(plaCur.getTailDirectionPlaCode());
                                //获取车组离开时间
                                TrainsetPostionCurWithNextTrack nextTrack = trainsetPostionCurList.stream().filter(t -> curTrainsetId.equals(t.getTrainsetId())).findFirst().orElse(null);
                                if (!ObjectUtils.isEmpty(nextTrack)) {
                                    monitorPla.setNextInTime(nextTrack.getNextInTime());
                                    monitorPla.setNextTrackCode(nextTrack.getNextTrackCode());
                                    monitorPla.setNextTrackName(nextTrack.getNextTrackName());
                                    monitorPla.setNextTrackPositionCode(nextTrack.getNextTrackPositionCode());
                                    monitorPla.setNextTrackPositionName(nextTrack.getNextTrackPositionName());
                                }
                                // Date outTime = this.getOutTime(dayPlanId, unitCode, curTrainsetId);
                                // if (outTime != null) {
                                //     monitorPla.setOutTime(outTime);
                                // }
                                monitorPla.setIsLong(plaCur.getIsLong());
                                // //获取该车组的所有辆序集合
                                List<String> carNoList = CacheUtil.getDataUseThreadCache(
                                        "remoteService.getCarNo_" + curTrainsetId,
                                        () -> remoteService.getCarno(curTrainsetId)
                                );
                                //外协任务信息
                                List<Integration> integrationList = new ArrayList<>();
                                integrationList = ztTaskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("6") && t.getTrainsetId().equals(curTrainsetId)).map(taskPacket -> {
                                    Integration integrationItem = new Integration();
                                    integrationItem.setIntegrationName(taskPacket.getPacketName());
                                    List<ProcessData> qrList = integrationProcessList.stream().filter(t -> t.getTrainsetId().equals(curTrainsetId)).filter(t -> t.getItemCode().equals(taskPacket.getPacketCode())).collect(Collectors.toList());
                                    if (qrList != null && qrList.size() > 0) {
                                        integrationItem.setIntegrationState("1");
                                    } else {
                                        integrationItem.setIntegrationState("0");
                                    }
                                    return integrationItem;
                                }).collect(Collectors.toList());
                                if (integrationList != null && integrationList.size() > 0) {//外协任务根据名称排下序
                                    integrationList.sort(Comparator.comparing(Integration::getIntegrationName));
                                }
                                monitorPla.setIntegrationList(integrationList);
                                //故障任务信息
                                //List<ZtTaskPacketEntity> faultTaskList = ztTaskPacketEntityList.stream().filter(t->t.getTrainsetId().equals(curTrainsetId)&&t.getPacketTypeCode().equals("5")).collect(Collectors.toList());
                                FaultData faultData = new FaultData();
                                List<FaultItem> faultItemList = groupFaultItemByTrainsetId.get(curTrainsetId);
                                if (faultItemList == null) {
                                    faultItemList = new ArrayList<>();
                                }
                                List<FaultData.FaultState> faultStateList = new ArrayList<>();
                                for (String carNo : carNoList) {
                                    String carNoItem = carNo.substring(carNo.length() - 2, carNo.length());
                                    FaultData.FaultState faultState = new FaultData().new FaultState();
                                    //该辆序故障集合
                                    List<FaultItem> carFaultList = new ArrayList<>();
                                    if (faultItemList != null) {
                                        carFaultList = faultItemList.stream().filter(t -> carNoItem.equals(t.getFaultCarNo()) || "全列".equals(t.getFaultCarNo())).collect(Collectors.toList());
                                    }
                                    if (!CollectionUtils.isEmpty(carFaultList)) {
                                        //该辆序已处理的故障数量
                                        long dealCount = carFaultList.stream().filter(t -> "1".equals(t.getDealWithCode()) || "已处理".equals(t.getDealWithDesc())).count();
                                        if (dealCount > 0 && dealCount == carFaultList.size()) {
                                            faultState.setFaultState("1");
                                        } else {
                                            faultState.setFaultState("2");
                                        }
                                    } else {
                                        faultState.setFaultState("0");
                                    }
                                    // long falultCount = faultItemList.stream().filter(t -> carNoItem.equals(t.getFaultCarNo())||"全列".equals(t.getFaultCarNo())).count();
                                    // if (falultCount > 0) {
                                    //     faultState.setFaultState("1");
                                    // } else {
                                    //     faultState.setFaultState("0");
                                    // }
                                    faultState.setCarNo(carNoItem);
                                    faultStateList.add(faultState);
                                }
                                faultData.setFaultTotalCount(faultItemList.size());
                                List<FaultItem> dealFaultList = faultItemList.stream().filter(t -> "1".equals(t.getDealWithCode()) || "已处理".equals(t.getDealWithDesc())).collect(Collectors.toList());
                                if (dealFaultList != null && dealFaultList.size() > 0) {
                                    faultData.setFaultDealCount(dealFaultList.size());
                                }
                                faultData.setFaultStateList(faultStateList);
                                monitorPla.setFaultData(faultData);
                            }
                        }
                    }
                    monitorPlaList.add(monitorPla);
                    if (monitorPla.getIsLong() != null && monitorPla.getIsLong().equals("1")) {
                        break;
                    }
                }
            }
            monitorTrack.setQueryProcessMonitorPlaList(monitorPlaList);
            resList.add(monitorTrack);
            return trackItem;
        }).collect(Collectors.toList());
        //6.2如果前台传过来的车组名称不为空，则根据车组名称参数过滤掉不需要的数据
        if (trainsetNameStr != null && !trainsetNameStr.equals("")) {
            List<String> trainsetNameList = Arrays.asList(trainsetNameStr.split(","));
            if (trainsetNameList != null && trainsetNameList.size() > 0) {
                for (QueryProcessMonitorTrack filterItem : resList) {
                    List<QueryProcessMonitorPla> filterPla = filterItem.getQueryProcessMonitorPlaList();
                    filterPla.removeIf(t -> !trainsetNameList.contains(t.getTrainsetName()));
                }
            }
        }
        return resList;
    }

    /**
     * 获取故障列表
     */
    @Override
    public List<FaultItem> getFaultList(String unitCode, String dayPlanId, String trainsetId, String carNo) {
        List<String> queryTrainsetIdList = Arrays.asList(trainsetId.split(","));
        //监控传多个车组
        if (queryTrainsetIdList.size() > 0) {
            trainsetId = null;
        }

        //返回数据集合
        List<FaultItem> faultItemList = new ArrayList<>();
        //2.从中台获取计划下发故障信息
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("5");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        //计划下发的故障，90条
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, packetTypeStrs, "", unitCode);
        //3.从派工表中取故障派工信息
        List<XzyMTaskcarpart> faultAllotList = processMonitorMapper.getFaultAllot(unitCode, dayPlanId, trainsetId);


        //计划和故障派工数据按车组分组
        Map<String, List<ZtTaskPacketEntity>> groupTaskPacketByTrainsetId = ztTaskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
        //0条，派工中为0
        Map<String, List<XzyMTaskcarpart>> groupTaskCarPartByTrainSetId = faultAllotList.stream().collect(Collectors.groupingBy(XzyMTaskcarpart::getTrainsetId));


        //先全部查出来项目code,后边过滤(减少重复调接口)
        List<String> allItemCodeList = new ArrayList<>();
        Map<String, List<String>> groupItemCodeListByTrainsetId = new HashMap<>();
        for (String queryTrainsetId : queryTrainsetIdList) {
            //4.将  计划下发和派工中 的故障 涉及到的项目编码组合到一块，并去重
            List<String> itemCodeList = new ArrayList<>();
            List<ZtTaskPacketEntity> taskPacketEntityByTrainSetId = groupTaskPacketByTrainsetId.get(queryTrainsetId);
            if (taskPacketEntityByTrainSetId != null && taskPacketEntityByTrainSetId.size() > 0) {
                for (ZtTaskPacketEntity taskItem : taskPacketEntityByTrainSetId) {
                    List<ZtTaskItemEntity> taskItemList = taskItem.getLstTaskItemInfo();
                    if (taskItemList != null && taskItemList.size() > 0) {
                        List<String> taskItemCode = taskItemList.stream().map(t -> t.getItemCode()).collect(Collectors.toList());
                        if (taskItemCode != null && taskItemCode.size() > 0) {
                            itemCodeList.addAll(taskItemCode);
                        }
                    }
                }
            }
            List<XzyMTaskcarpart> taskCarPartByTrainsetId = groupTaskCarPartByTrainSetId.get(queryTrainsetId);
            if (taskCarPartByTrainsetId != null && taskCarPartByTrainsetId.size() > 0) {
                List<String> allotItemCode = taskCarPartByTrainsetId.stream().map(t -> t.getItemCode()).collect(Collectors.toList());
                if (allotItemCode != null && allotItemCode.size() > 0) {
                    itemCodeList.addAll(allotItemCode);
                }
            }
            allItemCodeList.addAll(itemCodeList);
            groupItemCodeListByTrainsetId.put(queryTrainsetId, itemCodeList);
        }
        //5.获取计划中的故障
        List<RemainFaultNewVO> allRepairFaultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(allItemCodeList)){
            allRepairFaultList = remoteService.getFaultDataByIdList(allItemCodeList);
        }

        //6.获取工长认领的故障（在计划中包含了）
        //7.获取新增故障
        //查询故障信息先全查出来,后边靠车组过滤(减少重复调接口)
        List<RemainFaultNewVO> addFaultList = new ArrayList<>();
        FaultSearch faultSearch = new FaultSearch();
        String day = dayPlanId.substring(0, dayPlanId.length() - 3);
        faultSearch.setStartTime(day + " 00:00:00");
        faultSearch.setEndTime(day + " 23:59:59");
        Page<JSONObject> faultInfoPage = remoteService.getFaultInfoPage(faultSearch);
        if (faultInfoPage != null) {
            List<JSONObject> jsonArray = faultInfoPage.getRecords();
            if (jsonArray != null && jsonArray.size() > 0) {
                //8条
                addFaultList = jsonArray.stream().map(v -> v.toJavaObject(RemainFaultNewVO.class)).collect(Collectors.toList());
            }
        }

        //5.根据项目编码 调用故障中台接口 获取故障信息
        /**
         if(itemCodeList.size()>0){
         //项目编码去重
         itemCodeList = itemCodeList.stream().distinct().collect(Collectors.toList());
         //调用故障中台接口
         //组装自己的实体数据
         if(itemCodeList!=null&&itemCodeList.size()>0){
         for(int i = 0;i<itemCodeList.size();i++){
         //调用故障中台获取故障处理人（获取处理时间最大的那个处理人）
         JSONObject delaJsonObject = new RestRequestKitUseLogger<JSONObject>(repairFaultTestServiceId,logger) {
         }.getObject("/faultData/getFaultById?faultId="+String.valueOf(itemCodeList.get(i)));
         JSONArray dealJsonArray = delaJsonObject.getJSONObject("data").getJSONArray("dealList");
         JSONObject maxDeal = new JSONObject();
         for(int j = 0;j<dealJsonArray.size();j++){
         maxDeal = dealJsonArray.getJSONObject(j);
         if(j<dealJsonArray.size()-1){
         JSONObject nextDeal = dealJsonArray.getJSONObject(j+1);
         if(nextDeal.getDate("dealDate")!=null&&maxDeal.getDate("dealDate")!=null){
         if(nextDeal.getDate("dealDate").after(maxDeal.getDate("dealDate"))){
         maxDeal = nextDeal;
         }
         }
         }
         }
         //组装数据
         JSONObject jsObj = delaJsonObject.getJSONObject("data").getJSONObject("faultData");
         FaultItem faultItem = new FaultItem();
         if(jsObj.getString("carNo").equals("全列")){
         for(String carNoItem:carNoList){
         carNoItem = carNoItem.substring(carNoItem.length()-2,carNoItem.length());
         faultItem = new FaultItem();
         faultItem.setTrainsetNo(jsObj.getString("trainsetNo"));
         faultItem.setFaultName(jsObj.getString("faultName"));
         faultItem.setFaultDescription(jsObj.getString("faultDescription"));
         faultItem.setDealWithCode(jsObj.getString("dealWithCode"));
         faultItem.setDealWithDesc(jsObj.getString("dealWithDesc"));
         faultItem.setFaultGrade(jsObj.getString("faultGrade"));
         faultItem.setFindFaultMan(jsObj.getString("findFaultMan"));
         faultItem.setFindFaultTime(jsObj.getDate("findFaultTime"));
         //                            faultItem.setDealDate(jsObj.getDate("dealDate"));
         faultItem.setFaultCarNo(carNoItem);
         if(maxDeal!=null){
         faultItem.setDealFaultMan(maxDeal.getString("repairMan"));
         }
         faultItemList.add(faultItem);
         }
         }else{
         faultItem = new FaultItem();
         faultItem.setTrainsetNo(jsObj.getString("trainsetNo"));
         faultItem.setFaultName(jsObj.getString("faultName"));
         faultItem.setFaultDescription(jsObj.getString("faultDescription"));
         faultItem.setDealWithCode(jsObj.getString("dealWithCode"));
         faultItem.setDealWithDesc(jsObj.getString("dealWithDesc"));
         faultItem.setFaultGrade(jsObj.getString("faultGrade"));
         faultItem.setFindFaultMan(jsObj.getString("findFaultMan"));
         faultItem.setFindFaultTime(jsObj.getDate("findFaultTime"));
         //                        faultItem.setDealDate(jsObj.getDate("dealDate"));
         String faultCarNo = jsObj.getString("carNo");
         if(faultCarNo!=null&&!faultCarNo.equals("")){
         faultCarNo = faultCarNo.substring(faultCarNo.length()-2,faultCarNo.length());
         }
         faultItem.setFaultCarNo(faultCarNo);
         if(maxDeal!=null){
         faultItem.setDealFaultMan(maxDeal.getString("repairMan"));
         }
         faultItemList.add(faultItem);
         }
         }
         }
         }
         */
        for (String setFaultByTrainsetId : queryTrainsetIdList) {
            List<RemainFaultNewVO> repairFaultList = new ArrayList<>();
            //拿到本车组项目code过滤故障信息
            List<String> itemCodeListByTrainsetId = groupItemCodeListByTrainsetId.get(setFaultByTrainsetId);
            if (itemCodeListByTrainsetId != null && itemCodeListByTrainsetId.size() > 0) {
                //过滤5的故障信息
                //itemCodeListByTrainsetId 90条，allRepairFaultList45条,得出repairFaultList45条
                repairFaultList = allRepairFaultList.stream().filter(v -> itemCodeListByTrainsetId.contains(v.getFaultId())).collect(Collectors.toList());
            }
            //过滤6的故障信息
            //再加上5条，是50条
            repairFaultList.addAll(addFaultList.stream().filter(v -> v.getTrainsetId().equals(setFaultByTrainsetId)).collect(Collectors.toList()));
            if (repairFaultList != null && repairFaultList.size() > 0) {
                for (RemainFaultNewVO faultData : repairFaultList) {
                    FaultItem faultItem = new FaultItem();
                    String faultCarNo = faultData.getCarNo();
                    if (!"全列".equals(faultCarNo)) {
                        faultCarNo = faultCarNo.substring(faultCarNo.length() - 2, faultCarNo.length());
                    }
                    faultItem.setFaultCarNo(faultCarNo);
                    faultItem.setFaultId(faultData.getFaultId());
                    faultItem.setTrainNo(faultData.getTrainNo());
                    faultItem.setTrainsetId(faultData.getTrainsetId());
                    faultItem.setTrainsetName(faultData.getTrainsetName());
                    faultItem.setFaultName(faultData.getFaultName());
                    faultItem.setFaultDescription(faultData.getFaultDescription());
                    faultItem.setDealWithCode("");//故障处理结果编码
                    faultItem.setDealWithDesc(faultData.getDealWithDesc());
                    faultItem.setFaultGrade(faultData.getFaultGrade());
                    faultItem.setFindFaultMan(faultData.getFindFaultMan());
                    faultItem.setFindFaultTime(faultData.getFindFaultTime());
                    faultItem.setDealFaultMan(faultData.getRepairMan());
                    faultItem.setDealDate(faultData.getDealDate());
                    faultItemList.add(faultItem);

                    /** 全列故障算是一条故障，所以先将这里注释掉，改为上边的逻辑
                     if (faultData.getCarNo().equals("全列")) {
                     for (String carNoItem : carNoList) {
                     carNoItem = carNoItem.substring(carNoItem.length() - 2, carNoItem.length());
                     faultItem = new FaultItem();
                     faultItem.setFaultId(faultData.getFaultId());
                     faultItem.setTrainsetNo(faultData.getTrainsetId());
                     faultItem.setFaultName(faultData.getFaultName());
                     faultItem.setFaultDescription(faultData.getFaultDescription());
                     faultItem.setDealWithCode("");//故障处理结果编码
                     faultItem.setDealWithDesc(faultData.getDealWithDesc());
                     faultItem.setFaultGrade(faultData.getFaultGrade());
                     faultItem.setFindFaultMan(faultData.getFindFaultMan());
                     faultItem.setFindFaultTime(faultData.getFindFaultTime());
                     faultItem.setFaultCarNo(carNoItem);
                     faultItem.setDealFaultMan(faultData.getRepairMan());
                     faultItem.setDealDate(faultData.getDealDate());
                     faultItemList.add(faultItem);
                     }
                     } else {
                     faultItem = new FaultItem();
                     faultCarNo = faultData.getCarNo();
                     if (faultCarNo != null && !faultCarNo.equals("")) {
                     faultCarNo = faultCarNo.substring(faultCarNo.length() - 2, faultCarNo.length());
                     }
                     faultItem.setFaultCarNo(faultCarNo);
                     faultItem.setFaultId(faultData.getFaultId());
                     faultItem.setTrainsetNo(faultData.getTrainsetId());
                     faultItem.setFaultName(faultData.getFaultName());
                     faultItem.setFaultDescription(faultData.getFaultDescription());
                     faultItem.setDealWithCode("");//故障处理结果编码
                     faultItem.setDealWithDesc(faultData.getDealWithDesc());
                     faultItem.setFaultGrade(faultData.getFaultGrade());
                     faultItem.setFindFaultMan(faultData.getFindFaultMan());
                     faultItem.setFindFaultTime(faultData.getFindFaultTime());
                     faultItem.setDealFaultMan(faultData.getRepairMan());
                     faultItem.setDealDate(faultData.getDealDate());
                     faultItemList.add(faultItem);
                     }*/
                }
            }

            //根据辆序过滤
            if (StringUtils.isNotBlank(carNo)) {
                faultItemList = faultItemList.stream().filter(t -> carNo.equals(t.getFaultCarNo()) || "全列".equals(t.getFaultCarNo())).collect(Collectors.toList());
            }
            //根据故障id过滤
            if (!CollectionUtils.isEmpty(faultItemList)) {
                faultItemList = faultItemList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FaultItem::getFaultId))), ArrayList::new));
            }
            //根据故障等级排序
            if (!CollectionUtils.isEmpty(faultItemList)) {
                faultItemList = faultItemList.stream().sorted(Comparator.comparing(FaultItem::getFaultGrade)).collect(Collectors.toList());
            }
        }
        return faultItemList;
    }

    /**
     * 获取车组离开时间
     */
    @Override
    public Date getOutTime(String dayPlanId, String unitCode, String trainsetId) {
        Date result = null;
        WorkTime workTime = DayPlanUtil.getWorkTimeByDayPlanId(dayPlanId);
        //当前时间
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ShuntPlanModel> shuntPlanModelList = remoteService.getShuntingPlanByCondition(unitCode, trainsetId, dateFormat.format(workTime.getStartTime()), dateFormat.format(workTime.getEndTime()));
        if (shuntPlanModelList != null && shuntPlanModelList.size() > 0) {
            List<ShuntPlanModel> filterList = shuntPlanModelList.stream().filter(t -> currentDate.getTime() > t.getInTime().getTime() && currentDate.getTime() < t.getOutTime().getTime()).collect(Collectors.toList());
            if (filterList.size() > 0) {
                result = filterList.get(0).getOutTime();
            }
        }
        return result;
    }

    @Override
    public List<TrainsetPositionEntityBase> getOneWorkProcessMonitorConfig(List<Integer> trackCode) {
        return processMonitorMapper.getOneWorkProcessMonitorConfig(trackCode);
    }
}
