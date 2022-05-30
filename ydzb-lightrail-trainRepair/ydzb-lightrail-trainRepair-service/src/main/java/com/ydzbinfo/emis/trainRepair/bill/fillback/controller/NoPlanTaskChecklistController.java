package com.ydzbinfo.emis.trainRepair.bill.fillback.controller;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.INoPlanTaskCheckService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForShow;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/noPlanTaskChecklist")
public class NoPlanTaskChecklistController {

    //日志服务
    protected static final Logger logger = LoggerFactory.getLogger(NoPlanTaskChecklistController.class);
    //状态码

    @Autowired
    INoPlanTaskCheckService noPlanTaskCheckService;

    @ApiOperation(value = "获取没有计划任务的单据详细信息")
    @GetMapping(value = "/getNoPlanTaskSummaryInfo")
    public RestResult getNoPlanTaskSummaryInfo(@RequestParam(value = "summaryId", required = true) String summaryId, @RequestParam(value = "templateTypeCode", required = true) String templateTypeCode) {
        RestResult result = RestResult.success();
        try {
            SummaryInfoForShow res = noPlanTaskCheckService.getNoPlanTaskSummaryInfo(summaryId, templateTypeCode);
            result.setData(res);
        } catch (Exception e) {
            result = RestResult.fromException(e, logger, "创建数据失败!");
        }
        return result;
    }

    @ApiOperation(value = "保存无计划任务的记录单", notes = "保存无计划任务的记录单")
    @PostMapping(value = {"/saveNoPlanTaskRepairCell", "/saveNoPlanTaskRepairCell"})
    @ResponseBody
    public RestResult saveNoPlanTaskRepairCell(@RequestBody SummaryInfoForSave saveInfo) {
        RestResult result = RestResult.success();
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = noPlanTaskCheckService.saveNoPlanTaskRepairCell(saveInfo);
            if (!ObjectUtils.isEmpty(checklistTriggerUrlCallResult)) {
                result.setData(checklistTriggerUrlCallResult);
                if(checklistTriggerUrlCallResult.getAllowChange()){
                    result.setMsg("保存成功");
                }else{
                    throw new RuntimeException(checklistTriggerUrlCallResult.getOperationResultMessage());
                    // result.setMsg(checklistTriggerUrlCallResult.getOperationResultMessage());
                }
            }
        } catch (Exception ex) {
            logger.error("/saveNoPlanTaskRepairCell/saveNoPlanTaskRepairCell---保存无计划任务的记录单接口出错...", ex);
            result = RestResult.fromException(ex, logger, "保存失败");
        }
        return result;
    }

    @ApiOperation(value = "无计划任务的记录单签字", notes = "无计划任务的记录单签字")
    @PostMapping(value = {"/noPlanTaskRepairSign", "/noPlanTaskRepairSign"})
    @ResponseBody
    public RestResult noPlanTaskRepairSign(@RequestBody SummaryInfoForSave saveInfo) {
        RestResult result = RestResult.success();
        try {
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult = noPlanTaskCheckService.saveNoPlanTaskRepairCell(saveInfo);
            if (!ObjectUtils.isEmpty(checklistTriggerUrlCallResult)) {
                result.setData(checklistTriggerUrlCallResult);
                if(checklistTriggerUrlCallResult.getAllowChange()){
                    result.setMsg("签字成功");
                }else{
                    throw new RuntimeException(checklistTriggerUrlCallResult.getOperationResultMessage());
                    // result.setMsg(checklistTriggerUrlCallResult.getOperationResultMessage());
                }
            }
        } catch (Exception ex) {
            logger.error("/saveNoPlanTaskRepairCell/noPlanTaskRepairSign---无计划任务的记录单签字接口出错...", ex);
            result = RestResult.fromException(ex, logger, "签字失败");
        }
        return result;
    }
}
