package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.common.model.WorkTeam;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultSearchWithKeyWork;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultWithKeyWork;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.param.NodeDispose;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRun;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowRunWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.NodeDisposeResult;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfoWithPayload;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 流程运行表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
public interface IFlowRunService extends IService<FlowRun> {
    /**
     * 根据车组获取流程处理信息
     */
    FlowRunInfo getFlowRunInfoByTrainset(String flowPageCode, String unitCode, String dayPlanId, String trainsetIdListStr, String staffId, List<RuntimeRole> runtimeRoles);


    /**
     * 查看流程处理情况--流程图
     */
    FlowRunInfoForGraphShow getFlowDisposeGraph(String flowRunId);

    /**
     * 获取可切换流程
     *
     * @param flowRunId
     * @param flowId
     * @param unitCode
     * @return
     */
    List<FlowInfo> getSwitchoverFlowInfos(String flowRunId, String flowId, String unitCode);

    /**
     * 获取可切换流程(强制)
     *
     * @param flowRunId
     * @param flowId
     * @param unitCode
     * @return
     */
    List<FlowInfo> getForceSwitchoverFlow(String flowRunId, String flowId, String unitCode);


    /**
     * 切换流程
     * @param flowRunSwitchParam
     * @param flowRunSwitchType
     */
    void setSwitchoverFlow(FlowRunSwitchParam flowRunSwitchParam, String flowRunSwitchType);

    /**
     * 因为兼职班组的存在，需要根据运用所、日计划、车组获取人员实际的班组列表
     *
     * @param unitCode
     * @param dayPlanId
     * @param trainsetIds
     * @param staffId
     * @return
     * @author 张天可
     */
    List<WorkTeam> getActuallyWorkTeams(String unitCode, String dayPlanId, String[] trainsetIds, String staffId);

    /**
     * 节点处理
     *
     * @param nodeDispose
     * @param uploadedFileInfos
     * @param checkPower
     * @return
     */
    NodeDisposeResult setNodeDispose(NodeDispose nodeDispose, List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfos, boolean checkPower);

    /**
     * 查看流程图处理情况--简版
     *
     * @param flowRunId
     * @return
     */
    List<NodeInfoForSimpleShow> getFlowDisposeSimple(String flowRunId);


    /**
     * 流程运行和流程运行额外信息列表
     */
    List<FlowRunWithExtraInfo> getFlowRunWithExtraInfos(List<String> dayPlanIds, List<String> flowTypeCodes, String flowRunId, String trainsetId, String flowRunState, Date startDate, Date endDate, String flowId, String[] flowRunIds, String unitCode, String dayPlanCode);

    /**
     * 根据日计划获取流程运行信息
     */
    List<FlowRun> getFlowRunInfoListByDayPlanId(String dayPlanId, String trainsetId);

    /**
     * 查询派工岗位(作业流程处理使用)
     *
     * @return
     */
    List<RuntimeRole> getTaskPostList(String dayPlanId, String unitCode, String staffId, String trainsetId);

    /**
     * 关键作业流程录入
     *
     * @return
     */
    String setKeyWorkFlow(MultipartHttpServletRequest multipartHttpServletRequest) throws ParseException;

    /**
     * 获取关键作业流程处理
     */
    List<KeyWorkFlowRunInfo> getKeyWorkFlowRunInfoList(List<String> dayPlanIds, String trainType, String trainTemplate, String trainsetId, Date startTime, Date endTime, String flowName, String flowRunId, String unitCode, Boolean checkPower, String flowRunState, String content, boolean queryPastFlow, boolean monitoringShowForceEndFlowRun, boolean keyWorkMonitor);

    /**
     * 故障数据结构转关键作业数据结构
     */
    List<KeyWorkFlowRunWithFault> getKeyWorkFlowByFault( List<FaultWithKeyWork> faultWithKeyWorks);

    /**
     * 故障转关键作业录入(不带图片)
     */
    void setKeyWorkFlowByFault(List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList) throws ParseException;

    /**
     * 获取故障转关键作业故障id
     */
    Set<String> getFaultIdList();

    /**
     * 关键作业流程监控大屏展示
     */
    List<KeyWorkFlowRunWithTrainset> getKeyWorkFlowRunWithTrainsetList(List<String> dayPlanIds, String unitCode, String flowId, Boolean showForceEndFlowRun);

    /**
     * 临修作业流程监控大屏展示
     */
    List<TemporaryFlowRunWithTrainset> getTemporaryFlowRunWithTrainsetList(List<String> dayPlanIds, String unitCode);

    /**
     * 驳回/强制结束/撤销流程
     *
     * @param flowRunRecord
     */
    void forceEndFlowRun(FlowRunRecord flowRunRecord, String recordType);


    /**
     * 作业记录查询
     *
     * @return
     */
    FlowRunInfoWithStatistics getFlowRunRecordList(String unitCode, String trainsetId, String flowTypeCode, String teamCode, String workId, String flowRunState, String startDate, String endDate, String flowName, boolean queryPastFlow, String flowPageCode, String dayPlanCode, boolean nodeAllRecord) throws ParseException;

    /**
     * 根据时间和流程类型获取运行车组
     *
     * @return
     */
    List<TrainsetBaseInfo> getTrainsetByDateAndFlowTypeCode(String unitCode, String flowTypeCode, String startDate, String endDate, String flowPageCode) throws ParseException;

    /**
     * 根据车组获取流程处理信息作业监控大屏用
     */
    FlowRunInfo getFlowRunInfosByTrainMonitor(String flowPageCode, String unitCode, String dayPlanId, String trainsetIdListStr,  boolean showDayRepairTask);


    /**
     * 某段时间里的流程运行车组id
     * @return
     */
    List<String> getFlowRunList(Date startTime, Date endTime, String unitCode, String flowCode);

    /**
     * 获取在股道上的车组
     * @param unitCode
     * @return
     */
    List<TrainsetBaseInfoWithTrack> getTrainsetListByTrack(String unitCode);

    List<FaultWithKeyWork> getCenterFaultInfo(FaultSearchWithKeyWork faultSearchWithKeyWork);

}
