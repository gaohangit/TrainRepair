package com.ydzbinfo.emis.trainRepair.bill.fillback.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistIntegrationService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration.IntegrationSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration.InterationQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistIntegration;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integrationChecklist")
public class IntegrationSummaryController {
    @Autowired
    IChecklistIntegrationService iChecklistIntegrationService;

    //状态码

    //日志服务
    protected static final Logger logger = LoggerFactory.getLogger(IntegrationSummaryController.class);

    @ApiOperation(value = "根据申请单位、申请人、申请作业时间区间、车组号、状态获取一体化作业申请单列表", response = ChecklistIntegration.class)
    @PostMapping(value = "/getChecklistSummaryList")
    @ResponseBody
    public RestResult getChecklistSummaryList(@RequestBody InterationQueryCondition queryCheckListSummary) {
        RestResult result = RestResult.success();
        try {
            Page<IntegrationSummary> page = iChecklistIntegrationService.getChecklistSummaryList(queryCheckListSummary);
            result.setMsg("获取成功");
            result.setData(page);
        } catch (Exception e) {
            result = RestResult.fromException(e, logger, "获取一体化作业申请单列表数据失败");
        }
        return result;
    }
}
