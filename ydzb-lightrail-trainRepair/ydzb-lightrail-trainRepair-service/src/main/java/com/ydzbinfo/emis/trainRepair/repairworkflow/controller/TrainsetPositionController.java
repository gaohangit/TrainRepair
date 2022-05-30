package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetPostIonCurService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author gaohan
 * @description
 * @createDate 2021/5/18 9:55
 **/
@RestController
@RequestMapping({"flowTrainsetPosition", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/flowTrainsetPosition"})
public class TrainsetPositionController extends BaseController {
    @Autowired
    TrainsetPostIonCurService trainsetPostIonCurService;

    @ApiOperation("获取作业流程处理车组和供断电信息")
    @GetMapping(value = "/getTrainsetAndTrackPowerInfo")
    public Object getTrainsetAndTrackPowerInfo(String dayPlanId, String flowPageCode, @RequestParam String unitCode, String startTime, String endTime) {
        try {
            Date startDateTime = null;
            Date endDateTime = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotBlank(startTime)) {
                startDateTime = simpleDateFormat.parse(startTime);
            }
            if (StringUtils.isNotBlank(endTime)) {
                endDateTime = simpleDateFormat.parse(endTime);
            }
            Map map = trainsetPostIonCurService.getTrainsetAndTrackPowerInfo(dayPlanId, flowPageCode, unitCode, startDateTime, endDateTime);
            return RestResult.success().setData(map);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取作业流程处理车组和供断电信息错误");
        }
    }

}
