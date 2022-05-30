package com.ydzbinfo.emis.common.workprocess.controller;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.common.workprocess.service.IWorkProcessMidService;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessSummaryEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.base.BaseProcessData;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/7/2 09:28
 * @Description:
 */
@Api(description = "作业过程中台服务")
@Controller
@RequestMapping("/workProcessMid")
public class WorkProcessMidController extends BaseController {

    @Autowired
    IWorkProcessMidService workProcessMidService;


    @ApiOperation(value = "查询作业过程操作总表", notes = "查询作业过程操作总表")
    @RequestMapping(value = "/getSummaryList")
    @ResponseBody
    public RestResult getSummaryList(@RequestBody ProcessSummaryEntity processSummaryEntity) {
        logger.info("/workProcessMid/getSummaryList----开始调用中台查询作业过程操作总表接口...");
        RestResult result = RestResult.success();
        try {
            List<ProcessSummaryEntity> summaryList = workProcessMidService.getSummaryList(processSummaryEntity);
            result.setData(summaryList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcessMid/getSummaryList----调用中台查询作业过程操作总表接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/workProcessMid/getSummaryList----调用中台查询作业过程操作总表接口结束...");
        return result;
    }

    @ApiOperation(value = "添加作业过程操作总表", notes = "添加作业过程操作总表")
    @RequestMapping(value = "/addSummary")
    @ResponseBody
    public RestResult addSummary(@RequestBody List<ProcessSummaryEntity> processSummaryEntityList) {
        logger.info("/workProcessMid/addSummary----开始调用中台添加作业过程操作总表接口...");
        RestResult result = RestResult.success();
        try {
            boolean flag = workProcessMidService.addSummary(processSummaryEntityList);
            result.setMsg("添加成功");
        } catch (Exception ex) {
            logger.error("/workProcessMid/addSummary----调用中台添加作业过程操作总表接口出错...", ex);
            result = RestResult.fromException(ex, logger, "添加失败");
        }
        logger.info("/workProcessMid/addSummary----调用中台添加作业过程操作总表接口结束...");
        return result;
    }

    @ApiOperation(value = "添加或更新作业过程操作总表", notes = "添加或更新作业过程操作总表")
    @RequestMapping(value = "/addOrUpdateSummary")
    @ResponseBody
    public RestResult addOrUpdateSummary(@RequestBody List<ProcessSummaryEntity> processSummaryEntityList) {
        logger.info("/workProcessMid/addOrUpdateSummary----开始调用中台添加或更新作业过程操作总表接口...");
        RestResult result = RestResult.success();
        try {
            boolean flag = workProcessMidService.addOrUpdateSummary(processSummaryEntityList);
            result.setMsg("操作成功");
        } catch (Exception ex) {
            logger.error("/workProcessMid/addOrUpdateSummary----调用中台添加或更新作业过程操作总表接口出错...", ex);
            result = RestResult.fromException(ex, logger, "操作失败");
        }
        logger.info("/workProcessMid/addOrUpdateSummary----调用中台添加或更新作业过程操作总表接口结束...");
        return result;
    }

    @ApiOperation(value = "删除作业过程操作总表", notes = "删除作业过程操作总表")
    @RequestMapping(value = "/delSummary")
    @ResponseBody
    public RestResult delSummary(@RequestBody ProcessSummaryEntity processSummaryEntity) {
        logger.info("/workProcessMid/delSummary----开始调用中台删除作业过程操作总表接口...");
        RestResult result = RestResult.success();
        try {
            boolean flag = workProcessMidService.delSummary(processSummaryEntity);
            result.setMsg("删除成功");
        } catch (Exception ex) {
            logger.error("/workProcessMid/delSummary----调用中台删除作业过程操作总表接口出错...", ex);
            result = RestResult.fromException(ex, logger, "删除失败");
        }
        logger.info("/workProcessMid/delSummary----调用中台删除作业过程操作总表接口结束...");
        return result;
    }


    @ApiOperation(value = "获取作业过程", notes = "获取作业过程")
    @RequestMapping(value = "/getWorkProcessList")
    @ResponseBody
    public RestResult getWorkProcessList(@RequestBody BaseProcessData baseProcessData) {
        logger.info("/workProcessMid/getWorkProcessList----开始调用中台获取作业过程接口...");
        RestResult result = RestResult.success();
        try {
            List<ProcessPacketEntity> packetEntityList = workProcessMidService.getWorkProcessList(baseProcessData);
            result.setData(packetEntityList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/workProcessMid/getWorkProcessList----调用中台获取作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/workProcessMid/getWorkProcessList----调用中台获取作业过程接口结束...");
        return result;
    }

    @ApiOperation(value = "添加作业过程", notes = "添加作业过程")
    @RequestMapping(value = "/addWorkProcess")
    @ResponseBody
    public RestResult addWorkProcess(@RequestBody List<ProcessPacketEntity> processPacketEntityList) {
        logger.info("/workProcessMid/addWorkProcess----开始调用中台添加作业过程接口...");
        RestResult result = RestResult.success();
        try {
            boolean flag = workProcessMidService.addWorkProcess(processPacketEntityList);
            result.setMsg("添加成功");
        } catch (Exception ex) {
            logger.error("/workProcessMid/addWorkProcess----调用中台添加作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "添加失败");
        }
        logger.info("/workProcessMid/addWorkProcess----调用中台添加作业过程接口结束...");
        return result;
    }

    @ApiOperation(value = "删除作业过程", notes = "删除作业过程")
    @RequestMapping(value = "/delWorkProcess")
    @ResponseBody
    public RestResult delWorkProcess(@RequestBody JSONObject jsonObject) {
        logger.info("/workProcessMid/delWorkProcess----开始调用中台删除作业过程接口...");
        RestResult result = RestResult.success();
        try {
            result.setMsg("删除成功");
        } catch (Exception ex) {
            logger.error("/workProcessMid/delWorkProcess----调用中台删除作业过程接口出错...", ex);
            result = RestResult.fromException(ex, logger, "删除失败");
        }
        logger.info("/workProcessMid/delWorkProcess----调用中台删除作业过程接口结束...");
        return result;
    }

}
