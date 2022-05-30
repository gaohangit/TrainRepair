package com.ydzbinfo.emis.trainRepair.bill.fillback.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistLiveCheckService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck.LiveCheckQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck.LiveCheckSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistIntegration;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/liveChecklist")
public class LiveCheckSummaryController {
    //日志服务
    protected static final Logger logger = LoggerFactory.getLogger(LiveCheckSummaryController.class);

    //出所联检单服务
    @Autowired
    IChecklistLiveCheckService checklistLiveCheckService;

    @ApiOperation(value = "根据动车组、交接股道、交接时间区间获取出所联检单列表")
    @PostMapping(value = "/getLiveCheckSummaryList")
    @ResponseBody
    public RestResult getChecklistSummaryList(@RequestBody LiveCheckQueryCondition queryCheckListSummary) {
        RestResult result = RestResult.success();
        try{
            Page<LiveCheckSummary> resultPage = checklistLiveCheckService.getLiveCheckSummaryList(queryCheckListSummary);
            result.setMsg("获取成功");
            result.setData(resultPage);
        }catch (Exception e){
            result = RestResult.fromException(e, logger, "获取出所联检单列表数据");
        }
        return result;
    }


    @ApiOperation(value = "变更车组后获取出入所信息",response = ChecklistIntegration.class)
    @PostMapping(value = "/changeTrainnoByTrainset")
    @ResponseBody
    public RestResult changeTrainnoByTrainset(@RequestBody SummaryInfoForSave summaryInfoForSave){
        RestResult result = RestResult.success();
        try{
            ChecklistTriggerUrlCallResult checklistTriggerUrlCallResult= checklistLiveCheckService.changeTrainnoByTrainset(summaryInfoForSave);
            result.setData(checklistTriggerUrlCallResult);
            result.setMsg("保存成功");
        }catch (Exception ex){
            result = RestResult.fromException(ex, logger, "变更车组后获取出入所信息接口出现异常");
        }
        return result;
    }
}
