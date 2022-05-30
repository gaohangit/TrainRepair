package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.trainMonitor.model.RepairTask;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetPostIonCurService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: gaoHan
 * @date: 2021/7/21 17:48
 * @description:
 */
@RestController
@RequestMapping({"monitorCommon", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/monitorCommon"})
public class MonitorCommonController extends BaseController {
    @Autowired
    TrainsetPostIonCurService trainsetPostIonCurService;


    @ApiOperation(value = "获取修程任务数量")
    @GetMapping("/getRepairTask")
    public Object getRepairTask(String dayPlanId, String trainsetIdStr, String unitCode, @RequestParam(value = "showDayRepairTask", defaultValue = "false") boolean showDayRepairTask,@RequestParam(value = "showForceEndFlowRun", defaultValue = "false")Boolean showForceEndFlowRun) {
        try {
            List<RepairTask> repairTaskList = trainsetPostIonCurService.getRepairTask(dayPlanId, trainsetIdStr, unitCode,showDayRepairTask, showForceEndFlowRun);
            return RestResult.success().setData(repairTaskList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取修程任务数量报错");
        }
    }
}