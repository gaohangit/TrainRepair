package com.ydzbinfo.emis.trainRepair.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jobStatistics")
@Api(description = "作业统计")
public class JobStatisticsController {

    final String baseUrl = "/emis/trainRepair/jobStatistics";

    @ApiOperation("一级修作业统计")
    @RequiresPermissions("oneJobStatistics:view")
    @GetMapping("oneJobStatistics")
    public String oneJobStatistics() {
        return baseUrl + "/oneJobStatistics/oneJobStatistics.html";
    }


}
