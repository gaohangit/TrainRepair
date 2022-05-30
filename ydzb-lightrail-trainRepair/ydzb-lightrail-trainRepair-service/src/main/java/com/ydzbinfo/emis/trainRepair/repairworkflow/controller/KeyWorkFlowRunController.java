package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultSearchWithKeyWork;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultWithKeyWork;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkFlowRunInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkFlowRunWithFault;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.TrainsetBaseInfoWithTrack;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowRunService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.result.RestResultGeneric;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 高晗
 * @description 关键作业流程
 * @createDate 2021/6/24 11:16
 **/
@RestController
@RequestMapping({"KeyWorkFlowRun", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/KeyWorkFlowRun"})
public class KeyWorkFlowRunController extends BaseController {
    @Autowired
    IFlowRunService flowRunService;

    @ApiOperation("关键作业录入")
    @PostMapping(value = "/setKeyWorkFlowRun")
    public RestResult setKeyWorkFlowRun(MultipartHttpServletRequest multipartHttpServletRequest) {
        try {
            String id = flowRunService.setKeyWorkFlow(multipartHttpServletRequest);
            return RestResult.success().setData(id);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "关键作业录入错误");
        }
    }

    @ApiOperation("查询关键作业流程")
    @GetMapping(value = "/getKeyWorkFlowRunList")
    public RestResult getKeyWorkFlowRunList(String trainType, String trainTemplate, String trainsetId, String startTime, String endTime, String flowName, String flowRunId, @RequestParam String unitCode, @RequestParam(value = "checkPower", defaultValue = "true") Boolean checkPower, String flowRunState, String content, @RequestParam(value = "queryPastFlow", defaultValue = "true") boolean queryPastFlow) {
        try {
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotBlank(startTime)) {
                startDate = simpleDateFormat.parse(startTime);
            }
            if (StringUtils.isNotBlank(endTime)) {
                endDate = simpleDateFormat.parse(endTime);
            }
            List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList = flowRunService.getKeyWorkFlowRunInfoList(null, trainType, trainTemplate, trainsetId, startDate, endDate, flowName, flowRunId, unitCode, checkPower, flowRunState, content, queryPastFlow, true, false);
            return RestResult.success().setData(keyWorkFlowRunInfoList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询关键作业流程错误");
        }
    }


    @ApiOperation("故障数据结构转关键作业数据结构")
    @PostMapping(value = "/getKeyWorkFlowRunByFault")
    public RestResultGeneric<List<KeyWorkFlowRunWithFault>> getKeyWorkFlowRunList(@RequestBody List<FaultWithKeyWork> faultWithKeyWorks) {
        try {
            List<KeyWorkFlowRunWithFault> keyWorkFlowRunInfoList = flowRunService.getKeyWorkFlowByFault(faultWithKeyWorks);
            return RestResultGeneric.success(keyWorkFlowRunInfoList);
        } catch (Exception e) {
            return RestResultGeneric.fromException(e, logger, "故障数据结构转关键作业数据结构错误");
        }
    }

    @ApiOperation("故障关键作业录入")
    @PostMapping(value = "/setKeyWorkFlowRunByFault")
    public RestResult setKeyWorkFlowRunByFault(@RequestBody List<KeyWorkFlowRunInfo> keyWorkFlowRunInfos) {
        try {
            flowRunService.setKeyWorkFlowByFault(keyWorkFlowRunInfos);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "故障关键作业录入错误");
        }
    }

    @ApiOperation("获取在股道上的车组")
    @GetMapping(value = "/getTrainsetListByTrack")
    public RestResult getTrainsetListByTrack(String unitCode) {
        try {
            List<TrainsetBaseInfoWithTrack> trainsetBaseInfoWithTracks = flowRunService.getTrainsetListByTrack(unitCode);
           return RestResult.success().setData(trainsetBaseInfoWithTracks);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取在股道上的车组错误");
        }
    }
    @ApiOperation(value = "获取故障列表(转关键作业)")
    @GetMapping("/getCenterFaultInfo")
    public RestResult getCenterFaultInfo(FaultSearchWithKeyWork faultSearchWithKeyWork, int pageNum, int pageSize) {
        try {
            List<FaultWithKeyWork> faultWithKeyWorks = flowRunService.getCenterFaultInfo(faultSearchWithKeyWork);
            return RestResult.success().setData(CommonUtils.getPage(faultWithKeyWorks, pageNum, pageSize));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取故障列表(转关键作业)");
        }
    }
}
