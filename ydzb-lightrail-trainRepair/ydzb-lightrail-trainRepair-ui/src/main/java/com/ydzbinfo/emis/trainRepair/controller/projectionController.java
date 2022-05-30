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
@RequestMapping("/projection")
@Api(description = "一、二级修作业监控")
public class projectionController {
    final String baseUrl = "/emis/trainRepair/projection";

    @ApiOperation("一级修作业过程监控")
    @RequiresPermissions("workRepairmonitor:view")
    @GetMapping("workRepairmonitor")
    public String workRepairmonitor() {
        return baseUrl + "/workRepairmonitor/workRepairmonitor.html";
    }

}
