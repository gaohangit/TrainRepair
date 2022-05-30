package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.param.NodeDispose;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowRunService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.*;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConfigService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.upload.UpLoadFileUtils;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfoWithPayload;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gaohan
 * @description 流程处理信息
 * @createDate 2021/5/14 8:57
 **/
@RestController
@RequestMapping({"flowRun", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/flowRun"})
public class FlowRunController extends BaseController {
    @Autowired
    IFlowRunService flowRunService;
    @Autowired
    IFlowService flowService;
    @Autowired
    IRemoteService remoteService;
    @Autowired
    IRepairMidGroundService midGroundService;
    @Autowired
    ConfigService configService;

    /**
     * @param flowPageCode 页面流程code
     * @param unitCode     单位编码
     * @param dayPlanId    日计划id
     * @param trainsetId   车组id
     * @param personal     是否根据当前登录个人查询
     * @return
     */
    @ApiOperation("根据车组获取流程处理信息")
    @GetMapping(value = "/getFlowRunInfos")
    public Object getFlowRunInfos(String flowPageCode, @RequestParam String unitCode, @RequestParam String dayPlanId, String trainsetId, @RequestParam(value = "personal", defaultValue = "false") boolean personal) {
        try {
            String staffId = UserUtil.getUserInfo().getStaffId();
            List<RuntimeRole> runtimeRoles = new ArrayList<>();
            if (personal) {
                //根据日计划、单位编码、当前人id查派工信息中的岗位信息
                runtimeRoles = flowRunService.getTaskPostList(dayPlanId, unitCode, staffId, null);
            }
            FlowRunInfo flowRunInfo = flowRunService.getFlowRunInfoByTrainset(flowPageCode, unitCode, dayPlanId, trainsetId, staffId, runtimeRoles);
            return RestResult.success().setData(flowRunInfo);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "根据车组获取流程处理信息错误");
        }
    }

    /**
     * @param flowPageCode      页面流程code
     * @param unitCode          单位编码
     * @param dayPlanId         日计划id
     * @param trainsetId        车组id
     * @param showDayRepairTask 是否只展示本班次计划
     * @return
     */
    @ApiOperation("根据车组获取流程处理信息作业监控大屏用")
    @GetMapping(value = "/getFlowRunInfosByTrainMonitor")
    public Object getFlowRunInfosByTrainMonitor(String flowPageCode, @RequestParam String unitCode, @RequestParam String dayPlanId, String trainsetId, @RequestParam(value = "showDayRepairTask", defaultValue = "false") boolean showDayRepairTask) {
        try {
            FlowRunInfo flowRunInfo = flowRunService.getFlowRunInfosByTrainMonitor(flowPageCode, unitCode, dayPlanId, trainsetId, showDayRepairTask);
            return RestResult.success().setData(flowRunInfo);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "根据车组获取流程处理信息作业监控大屏用");
        }
    }

    @ApiOperation("无视权限根据车组获取流程处理信息")
    @GetMapping(value = "/getFlowRunInfosNotCheck")
    public Object getFlowRunInfosNotCheck(String flowPageCode, @RequestParam String unitCode, @RequestParam String dayPlanId, String trainsetId) {
        try {
            String staffId = UserUtil.getUserInfo().getStaffId();
            FlowRunInfo flowRunInfo = flowRunService.getFlowRunInfoByTrainset(flowPageCode, unitCode, dayPlanId, trainsetId, staffId, new ArrayList<>());
            return RestResult.success().setData(flowRunInfo);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "无视权限根据车组获取流程处理信息错误");
        }
    }

    @ApiOperation("查看流程处理情况-流程图")
    @GetMapping(value = "/getFlowDisposeGraph")
    public Object getFlowDisposeGraph(@RequestParam String flowRunId) {
        try {
            FlowRunInfoForGraphShow flowRunInfoForGraphShows = flowRunService.getFlowDisposeGraph(flowRunId);
            return RestResult.success().setData(flowRunInfoForGraphShows);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查看流程处理情况-流程图错误");
        }
    }

    @ApiOperation("获取可切换流程")
    @GetMapping(value = "/getSwitchoverFlow")
    public Object getSwitchoverFlow(String flowRunId, String flowId, String unitCode) {
        try {
            List<FlowInfo> flowInfo = flowRunService.getSwitchoverFlowInfos(flowRunId, flowId, unitCode);
            return RestResult.success().setData(flowInfo);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取可切换流程错误");
        }
    }

    @ApiOperation("切换流程")
    @PostMapping(value = "/setSwitchoverFlow")
    public Object setSwitchoverFlow(@RequestBody FlowRunSwitchParam flowRunSwitchParam) {
        try {
            flowRunService.setSwitchoverFlow(flowRunSwitchParam, FlowRunSwitchTypeEnum.NORMAL.getValue());
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "切换流程错误");
        }
    }

    @ApiOperation("获取可切换流程(强制)")
    @GetMapping(value = "/getForceSwitchoverFlow")
    public Object getForceSwitchoverFlow(String flowRunId, String flowId, String unitCode) {
        try {
            List<FlowInfo> flowInfo = flowRunService.getForceSwitchoverFlow(flowRunId, flowId, unitCode);
            return RestResult.success().setData(flowInfo);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取可切换流程错误");
        }
    }

    @ApiOperation("切换流程(强制)")
    @PostMapping(value = "/setForceSwitchoverFlow")
    public Object setForceSwitchoverFlow(@RequestBody FlowRunSwitchParam flowRunSwitchParam) {
        try {
            flowRunService.setSwitchoverFlow(flowRunSwitchParam, FlowRunSwitchTypeEnum.FORCE.getValue());
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "切换流程错误");
        }
    }


    @ApiOperation("强制结束流程")
    @PostMapping(value = "/forceEndFlowRun")
    public Object forceEndFlowRun(@RequestBody FlowRunRecord flowRunRecord) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("remark", flowRunRecord.getRemark());
            flowRunRecord.setRemark(JSONObject.toJSONString(jsonObject));
            flowRunService.forceEndFlowRun(flowRunRecord, FlowRunRecordTypeEnum.FORCE_END.getValue());
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "强制结束流程错误");
        }
    }

    @ApiOperation("驳回关键作业")
    @PostMapping(value = "/rejectEndKeyWorkFlowRun")
    public Object forceEndKeyFlowRun(@RequestBody FlowRunRecordInfo flowRunRecordInfo) {
        try {
            //备注字段+必传字段(约6个)在返回时候也用
            FlowRunRecord flowRunRecord = new FlowRunRecord();
            BeanUtils.copyProperties(flowRunRecordInfo, flowRunRecord);

            //临时组织备注详情信息
            FlowRunRecordRemarkInfo flowRunRecordRemarkInfo = new FlowRunRecordRemarkInfo();
            BeanUtils.copyProperties(flowRunRecordInfo, flowRunRecordRemarkInfo);
            String remarkJsonStr = JSONObject.toJSONString(flowRunRecordRemarkInfo);
            flowRunRecord.setRemark(remarkJsonStr);

            flowRunService.forceEndFlowRun(flowRunRecord, FlowRunRecordTypeEnum.REJECT_END.getValue());
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "驳回关键作业错误");
        }
    }

    @ApiOperation("撤销关键作业")
    @PostMapping(value = "/revokeKeyWorkFlowRun")
    public Object revokeKeyWorkFlowRun(@RequestBody FlowRunRecord flowRunRecord) {
        try {
            flowRunService.forceEndFlowRun(flowRunRecord, FlowRunRecordTypeEnum.REVOKE_END.getValue());
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "驳回关键作业错误");
        }
    }

    @Data
    private static class FlowRunRecordRemarkInfo {
        private String nodeId;
        private String nodeName;
        private String remark;
    }

    @ApiOperation("节点处理")
    @PostMapping(value = "/setNodeDispose")
    public Object setNodeDispose(@RequestParam String nodeDisposeJsonStr, HttpServletRequest httpServletRequest) {
        try {
            NodeDispose nodeDispose = JSON.parseObject(nodeDisposeJsonStr, NodeDispose.class);
            NodeDisposeResult nodeDisposeResult = flowRunService.setNodeDispose(nodeDispose, setNodeDisposeUploadFiles(httpServletRequest), true);
            return RestResult.success().setData(nodeDisposeResult);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "节点处理错误");
        }
    }

    @ApiOperation("无视权限校验进行节点处理")
    @PostMapping(value = "/setNodeDisposeNotCheck")
    public Object setNodeDisposeNotCheck(@RequestParam String nodeDisposeJsonStr, HttpServletRequest httpServletRequest) {
        try {
            NodeDispose nodeDispose = JSON.parseObject(nodeDisposeJsonStr, NodeDispose.class);
            NodeDisposeResult nodeDisposeResult = flowRunService.setNodeDispose(nodeDispose, setNodeDisposeUploadFiles(httpServletRequest), false);
            return RestResult.success().setData(nodeDisposeResult);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "无视权限校验进行节点处理错误");
        }
    }

    @ApiOperation("文件上传")
    @PostMapping(value = "/uploadedFileInfo")
    public Object uploadedFileInfo(HttpServletRequest httpServletRequest) {
        try {
            List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoList = setNodeDisposeUploadFiles(httpServletRequest);
            return RestResult.success().setData(uploadedFileInfoList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "文件上传错误");
        }
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

    @ApiOperation("查看流程处理情况-简版")
    @GetMapping(value = "/getFlowDisposeSimple")
    public Object getFlowDisposeSimple(String flowRunId) {
        try {
            List<NodeInfoForSimpleShow> nodeWithRecords = flowRunService.getFlowDisposeSimple(flowRunId);
            return RestResult.success().setData(nodeWithRecords);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查看流程处理情况-简版错误");
        }
    }

    @ApiOperation("关键作业流程监控")
    @GetMapping(value = "/getKeyWorkFlowRunInfoWithTrainsetList")
    public Object getKeyWorkFlowRunInfoWithTrainsetList(@RequestParam String unitCode, String flowId,  @RequestParam(value = "showForceEndFlowRun", defaultValue = "false")Boolean showForceEndFlowRun) {
        try {
            List<String> dayPlanIds = FlowUtil.getQueryDayPlanIds(
                DayPlanUtil.getDayPlanId(unitCode),
                FlowDatabaseConfigUtil.getKeyWorkMonitorShowDayPlanCount()
            );
            List<KeyWorkFlowRunWithTrainset> keyWorkFlowRunWithTrainsetList = flowRunService.getKeyWorkFlowRunWithTrainsetList(dayPlanIds, unitCode, flowId, showForceEndFlowRun);
            return RestResult.success().setData(keyWorkFlowRunWithTrainsetList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取关键作业流程监控错误");
        }
    }

    @ApiOperation("临修作业流程监控")
    @GetMapping(value = "/getTemporaryFlowRunInfoWithTrainsetList")
    public Object getTemporaryFlowRunInfoWithTrainsetList(@RequestParam List<String> dayPlanIds, @RequestParam String unitCode) {
        try {
            List<TemporaryFlowRunWithTrainset> temporaryFlowRunWithTrainsetArrayList = flowRunService.getTemporaryFlowRunWithTrainsetList(dayPlanIds, unitCode);
            return RestResult.success().setData(temporaryFlowRunWithTrainsetArrayList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取临修作业流程监控错误");
        }
    }

    @ApiOperation("作业流程处理记录查询")
    @GetMapping(value = "/getFlowRunRecordList")
    public Object getKeyWorkFlowRunRecordList(@RequestParam String unitCode, String trainsetId, String flowTypeCode, String teamCode, String workId, String flowRunState, String startDate, String endDate, String flowName, @RequestParam(value = "queryPastFlow", defaultValue = "true") boolean queryPastFlow, String flowPageCode, String dayPlanCode, @RequestParam(value = "nodeAllRecord", defaultValue = "false") boolean nodeAllRecord) {
        try {
            FlowRunInfoWithStatistics flowRunInfoWithKeyWorkBases = flowRunService.getFlowRunRecordList(unitCode, trainsetId, flowTypeCode, teamCode, workId, flowRunState, startDate, endDate, flowName, queryPastFlow, flowPageCode, dayPlanCode, nodeAllRecord);
            return RestResult.success().setData(flowRunInfoWithKeyWorkBases);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "作业流程处理记录查询错误");
        }
    }

    @ApiOperation("根据时间和流程类型获取运行车组")
    @GetMapping(value = "/getTrainsetByDateAndFlowTypeCode")
    public Object getTrainsetByDateAndFlowTypeCode(@RequestParam String unitCode, String flowTypeCode, String startDate, String endDate, String flowPageCode) {
        try {
            List<TrainsetBaseInfo> trainsetBaseInfos = flowRunService.getTrainsetByDateAndFlowTypeCode(unitCode, flowTypeCode, startDate, endDate, flowPageCode);
            return RestResult.success().setData(trainsetBaseInfos);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "关键作业流程处理记录查询错误");
        }
    }
}
