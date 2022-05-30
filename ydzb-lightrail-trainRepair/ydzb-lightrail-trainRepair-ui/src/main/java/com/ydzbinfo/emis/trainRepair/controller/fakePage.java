package com.ydzbinfo.emis.trainRepair.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 郝明智
 */
@Controller
@RequestMapping("/fakePage")
@Api(description = "假页面")
public class fakePage {
    final String baseUrl = "/emis/trainRepair/fakePage";

    @ApiOperation("一级修监控")
    @RequiresPermissions("firstRepairMonitor:view")
    @GetMapping("firstRepairMonitor")
    public String firstRepairMonitor() {
        return baseUrl + "/firstRepairMonitor/index.html";
    }

    @ApiOperation("二级修监控")
    @RequiresPermissions("secondRepairMonitor:view")
    @GetMapping("secondRepairMonitor")
    public String secondRepairMonitor() {
        return baseUrl + "/secondRepairMonitor/index.html";
    }

    @ApiOperation("作业过程监控")
    @RequiresPermissions("trainMonitor:view")
    @GetMapping("trainMonitor")
    public String trainMonitor() {
        return baseUrl + "/trainMonitor/index.html";
    }
}
