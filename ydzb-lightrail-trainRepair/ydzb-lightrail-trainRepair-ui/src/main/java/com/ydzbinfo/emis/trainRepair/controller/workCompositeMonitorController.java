package com.ydzbinfo.emis.trainRepair.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 郝明智
 */
@Controller
@RequestMapping("/workComposite")
@Api(description = "作业综合监控")
public class workCompositeMonitorController {
    final String baseUrl = "/emis/trainRepair/workComposite";

    @ApiOperation("作业综合监控")
    @RequiresPermissions("workCompositeMonitor:view")
    @GetMapping("workCompositeMonitor")
    public String workCompositeMonitor() {
        return baseUrl + "/workCompositeMonitor/workCompositeMonitor.html";
    }
}
