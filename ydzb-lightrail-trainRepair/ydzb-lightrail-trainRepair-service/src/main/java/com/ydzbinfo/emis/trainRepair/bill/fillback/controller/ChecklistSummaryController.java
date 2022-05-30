package com.ydzbinfo.emis.trainRepair.bill.fillback.controller;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistSummaryService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.OneTwoRepairCheckList;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.QueryCheckListSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.QueryChecklistQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: hanxu
 * @Date: 2021/7/28
 * @Description: 一二级修记录单回填查询界面数据
 */
@RestController
@RequestMapping("/checklist")

public class ChecklistSummaryController {

    protected static final Logger logger = LoggerFactory.getLogger(ChecklistSummaryController.class);
    @Autowired
    IChecklistSummaryService iChecklistSummaryService;

    @ApiOperation(value = "根据日计划编号、修程、动车组、作业班组、作业人员、回填状态查询记录单回填", response = ChecklistSummary.class)
    @PostMapping(value = "/getChecklistSummaryList")
    @ResponseBody
    public RestResult getChecklistSummaryList(@RequestBody QueryCheckListSummary queryCheckListSummary) {
        try {
            List<OneTwoRepairCheckList> listChecklist = iChecklistSummaryService.getChecklistSummaryList(queryCheckListSummary);
            JSONObject resultData = new JSONObject();
            resultData.put("getChecklistSummaryList", listChecklist);
            return RestResult.success().setData(resultData);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取一二级修列表数据");
        }
    }

    @ApiOperation(value = "获取一二级修记录单数据来源", response = ChecklistSummary.class)
    @GetMapping(value = "/getChecklistSource")
    public RestResult getChecklistSource() {
        try {
            String source = iChecklistSummaryService.getChecklistSource();
            return RestResult.success().setData(source);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取一二级修记录单数据来源");
        }
    }

    @ApiOperation(value = "获取一二级修记录单查询条件", response = ChecklistSummary.class)
    @PostMapping(value = "/getChecklistQueryCondition")
    public RestResult getChecklistQueryCondition(@RequestBody QueryCheckListSummary queryCheckListSummary) {
        try {
            QueryChecklistQueryCondition checklistQueryCondition = iChecklistSummaryService.getChecklistQueryCondition(queryCheckListSummary,"");
            return RestResult.success().setData(checklistQueryCondition);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取一二级修记录单查询条件");
        }
    }

    @ApiOperation(value = "获取一级修机检记录单查询条件",response = ChecklistSummary.class)
    @PostMapping(value ="/getChecklistEquipmentQueryCondition")
    public RestResult getChecklistEquipmentQueryCondition(@RequestBody QueryCheckListSummary queryCheckListSummary) {
        try {
            QueryChecklistQueryCondition checklistQueryCondition = iChecklistSummaryService.getChecklistQueryCondition(queryCheckListSummary,"equipment");
            return RestResult.success().setData(checklistQueryCondition);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取一二级修记录单查询条件");
        }
    }
}
