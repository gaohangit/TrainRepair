package com.ydzbinfo.emis.trainRepair.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workRepairFlow")
@Api(description = "作业流程设计")
public class WorkRepairFlowController {

    final String baseUrl = "/emis/trainRepair/workRepairFlow";

    @ApiOperation("流程类型配置")
    @RequiresPermissions("flowTypeConfig:view")
    @GetMapping("flowTypeConfig")
    public String flowTypeConfig() {
        return baseUrl + "/flowTypeConfig/flowTypeConfig.html";
    }

    @ApiOperation("作业流程配置")
    @RequiresPermissions("workRepairFlowConfig:view")
    @GetMapping("workRepairFlowConfig")
    public String workRepairFlowConfig() {
        return baseUrl + "/workRepairFlowConfig/workRepairFlowConfig.html";
    }

    @ApiOperation("作业流程处理")
    // @RequiresPermissions("workRepairFlowHandle:view")
    @GetMapping("workRepairFlowHandle/{flowPageCode}")
    public String workRepairFlowHandle(@PathVariable String flowPageCode, Model model) {
        model.addAttribute("flowPageCode", flowPageCode);
        return baseUrl + "/workRepairFlowHandle/workRepairFlowHandle.html";
    }

    @ApiOperation("关键作业配置")
    @RequiresPermissions("keyWorkConfig:view")
    @GetMapping("keyWorkConfig")
    public String keyWorkConfig() {
        return baseUrl + "/keyWorkConfig/keyWorkConfig.html";
    }

    @ApiOperation("关键作业流程处理")
    @RequiresPermissions("keyWorkFlowHandle:view")
    @GetMapping("keyWorkFlowHandle")
    public String keyWorkFlowHandle() {
        return baseUrl + "/keyWorkFlowHandle/keyWorkFlowHandle.html";
    }

    @ApiOperation("故障转关键作业")
    @RequiresPermissions("faultToKeyWork:view")
    @GetMapping("faultToKeyWork")
    public String faultToKeyWork() {
        return baseUrl + "/faultToKeyWork/faultToKeyWork.html";
    }

    @ApiOperation("关键作业流程监控")
    @RequiresPermissions("keyWorkFlowMonitor:view")
    @GetMapping("keyWorkFlowMonitor")
    public String keyWorkFlowMonitor() {
        return baseUrl + "/keyWorkFlowMonitor/keyWorkFlowMonitor.html";
    }

    @ApiOperation("临修作业流程监控")
    @RequiresPermissions("temporaryRepairOperationFlowMonitor:view")
    @GetMapping("temporaryRepairOperationFlowMonitor")
    public String temporaryRepairOperationFlowMonitor() {
        return baseUrl + "/temporaryRepairOperationFlowMonitor/temporaryRepairOperationFlowMonitor.html";
    }

    @ApiOperation("作业流程处理记录查询")
    @RequiresPermissions("workRepairFlowRecord:view")
    @GetMapping("workRepairFlowRecord/{flowPageCode}")
    public String workRepairFlowRecord(@PathVariable("flowPageCode") String flowPageCode, Model model) {
        model.addAttribute("flowPageCode", flowPageCode);
        return baseUrl + "/workRepairFlowRecord/workRepairFlowRecord.html";
    }
}
