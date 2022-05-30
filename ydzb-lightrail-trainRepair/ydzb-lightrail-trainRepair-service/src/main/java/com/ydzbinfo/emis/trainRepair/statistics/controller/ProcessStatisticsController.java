package com.ydzbinfo.emis.trainRepair.statistics.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.statistics.model.DurationEntity;
import com.ydzbinfo.emis.trainRepair.statistics.model.DurationStatistics;
import com.ydzbinfo.emis.trainRepair.statistics.model.WarningStatistics;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationDetail;
import com.ydzbinfo.emis.trainRepair.statistics.service.ProcessStatisticsService;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 作业过程统计控制器
 * Author: 吴跃常
 * Create Date Time: 2021/6/22 10:53
 * Update Date Time: 2021/6/22 10:53
 *
 * @see
 */
@RestController
@RequestMapping("/processStatistics")
@Slf4j
public class ProcessStatisticsController {

    @Autowired
    private ProcessStatisticsService processStatisticsService;

    
    /**
     * @author: 吴跃常
     * @Description: 获取统计时长
     * @date: 2021/6/22 11:09
     */
    @GetMapping("/duration")
    @ApiOperation("获取统计时长")
    @BussinessLog(value = "获取统计时长", key = "/processStatistics/duration", type = "04")
    public JSONObject getStatisticsDuration(DurationEntity durationEntity) {
        JSONObject result = new JSONObject();
        try {
            DurationStatistics statisticsDuration = processStatisticsService.getStatisticsDuration(durationEntity);
            result.put("msg", "成功");
            result.put("data", statisticsDuration);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "获取统计时长失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return result;
    }

    /**
     * @author: 吴跃常
     * @Description: 作业时长明细
     * @date: 2021/6/23 16:13
     */
    @GetMapping("/durationDetail")
    @ApiOperation("作业时长明细")
    @BussinessLog(value = "durationDetail", key = "/processStatistics/durationDetail", type = "04")
    public JSONObject getDurationDetail(@RequestParam("trainsetType") String trainsetType,
                                        @RequestParam("trainsetId") String trainsetId,
                                        @RequestParam("dayPlanId") String dayPlanId,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                        @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        JSONObject result = new JSONObject();
        try {
            Page<DurationDetail> durationDetailPage = processStatisticsService.getDurationDetail(trainsetType, trainsetId, dayPlanId, pageSize, pageNum);
            result.put("msg", "成功");
            result.put("data", durationDetailPage);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "获取作业时长明细失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return result;
    }

    /**
     * @author: 吴跃常
     * @Description: 作业预警统计分析
     * @date: 2021/6/25 15:40
     */
    @GetMapping("/warning")
    @ApiOperation("作业预警统计分析")
    @BussinessLog(value = "warning", key = "/processStatistics/warning", type = "04")
    public JSONObject getWorkWarning(DurationEntity durationEntity) {
        JSONObject result = new JSONObject();
        try {
            WarningStatistics warningStatistics = processStatisticsService.getWorkWarning(durationEntity);
            result.put("msg", "成功");
            result.put("data", warningStatistics);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "获取作业预警统计分析失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return result;
    }
}
