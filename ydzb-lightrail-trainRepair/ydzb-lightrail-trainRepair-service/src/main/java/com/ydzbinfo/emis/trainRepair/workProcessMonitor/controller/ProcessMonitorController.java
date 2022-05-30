package com.ydzbinfo.emis.trainRepair.workProcessMonitor.controller;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntityBase;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.FaultItem;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.QueryProcessMonitorTrack;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.TwoItem;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.service.IProcessMonitorService;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/24 14:28
 * @Description: 一级修作业过程监控
 */
@Controller
@RequestMapping("/processMonitor")
public class ProcessMonitorController {
    //日志记录
    protected static final Logger logger = LoggerFactory.getLogger(ProcessMonitorController.class);

    @Autowired
    IProcessMonitorService processMonitorService;

    @ApiOperation(value = "获取一级修监控集合", notes = "获取一级修监控集合")
    @PostMapping(value = "/getOneWorkProcessMonitorList")
    @ResponseBody
    public Object getOneWorkProcessMonitorList(@RequestBody JSONObject jsonObject) {
        logger.info("/oneWorkProcessMonitor/getOneWorkProcessMonitorList----开始调用获取一级修监控集合接口...");
        RestResult result = RestResult.success();
        try {
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String trackCodesJsonStr = jsonObject.getString("trackCodesJsonStr");
            String trainsetNameStr = jsonObject.getString("trainsetNameStr");
            List<QueryProcessMonitorTrack> resList = processMonitorService.getOneWorkProcessMonitorList(unitCode, dayPlanId, trackCodesJsonStr, trainsetNameStr);
            result.setData(resList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/oneWorkProcessMonitor/getOneWorkProcessMonitorList----调用获取一级修监控集合出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/oneWorkProcessMonitor/getOneWorkProcessMonitorList----调用获取一级修监控集合接口正常结束...");
        return result;
    }

    @ApiOperation(value = "获取二级修监控集合", notes = "获取二级修监控集合")
    @PostMapping(value = "/getTwoWorkProcessMonitorList")
    @ResponseBody
    public Object getTwoWorkProcessMonitorList(@RequestBody JSONObject jsonObject) {
        logger.info("/processMonitor/getTwoWorkProcessMonitorList----开始调用获取二级修监控集合接口...");
        RestResult result = RestResult.success();
        try {
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String trackCodesJsonStr = jsonObject.getString("trackCodesJsonStr");
            String trainsetNameStr = jsonObject.getString("trainsetNameStr");
            List<QueryProcessMonitorTrack> resList = processMonitorService.getTwoWorkProcessMonitorList(unitCode, dayPlanId, trackCodesJsonStr, trainsetNameStr);
            result.setData(resList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/processMonitor/getTwoWorkProcessMonitorList----调用获取二级修监控集合出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/processMonitor/getTwoWorkProcessMonitorList----调用获取二级修监控集合接口正常结束...");
        return result;
    }

    @ApiOperation(value = "获取二级项目列表", notes = "获取二级项目列表")
    @PostMapping(value = "/getTwoItemList")
    @ResponseBody
    public RestResult getTwoItemList(@RequestBody JSONObject jsonObject) {
        logger.info("/processMonitor/getTwoItemList----开始调用获取二级项目列表接口...");
        RestResult result = RestResult.success();
        try {
            String unitCode = jsonObject.getString("unitCode");
            String dayPlanId = jsonObject.getString("dayPlanId");
            String trainsetId = jsonObject.getString("trainsetId");
            String packetCode = jsonObject.getString("packetCode");
            String packetName = jsonObject.getString("packetName");
            List<TwoItem> twoItemList = processMonitorService.getTwoItemList(unitCode, dayPlanId, trainsetId, packetCode, packetName);
            result.setData(twoItemList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/processMonitor/getTwoItemList----调用获取二级项目列表出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/processMonitor/getTwoItemList----调用获取二级项目列表接口正常结束...");
        return result;
    }

    @ApiOperation(value = "获取故障列表", notes = "获取故障列表")
    @GetMapping(value = "/getFaultList")
    @ResponseBody
    public RestResult getFaultList(@RequestParam("unitCode") String unitCode, @RequestParam("dayPlanId") String dayPlanId, @RequestParam("trainsetId") String trainsetId, @RequestParam("carNo") String carNo) {
        logger.info("/processMonitor/getFaultList----开始调用获取故障列表接口...");
        RestResult result = RestResult.success();
        try {
            List<FaultItem> faultItemList = processMonitorService.getFaultList(unitCode, dayPlanId, trainsetId, carNo);
            result.setData(faultItemList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/processMonitor/getFaultList----调用获取故障列表出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/processMonitor/getFaultList----调用获取故障列表接口正常结束...");
        return result;
    }

    @ApiOperation(value = "作业监控大屏车组股道配置", notes = "获取一级修作业监控大屏车组股道配置")
    @PostMapping(value = "/getOneWorkProcessMonitorConfig")
    @ResponseBody
    public RestResult getOneWorkProcessMonitorConfig(@RequestBody Integer[] trackCode) {
        logger.info("/processMonitor/getOneWorkProcessMonitorConfig----开始调用获取作业监控大屏车组股道配置接口...");
        RestResult result = RestResult.success();
        try {
            List<Integer> trackCodes = new ArrayList<>();
            if (trackCode != null) {
                trackCodes = Arrays.asList(trackCode);
            }
            List<TrainsetPositionEntityBase> OneWorkProcessMonitorList = processMonitorService.getOneWorkProcessMonitorConfig(trackCodes);
            result.setData(OneWorkProcessMonitorList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/processMonitor/getOneWorkProcessMonitorConfig----调用获取作业监控大屏车组股道配置出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/processMonitor/getOneWorkProcessMonitorConfig----调用获取作业监控大屏车组股道配置接口正常结束...");
        return result;
    }

}
