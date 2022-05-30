package com.ydzbinfo.emis.trainRepair.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 张天可
 */
@Controller
@RequestMapping("/trainMonitor")
@Api(description = "车组监控")
public class TrainMonitorController {
    final String baseUrl = "/emis/trainRepair/trainMonitor";

    @ApiOperation("车组监控-作业过程监控")
    @RequiresPermissions("trainMonitor:view")
    @GetMapping("")
    public String viewBillBack() {
        return baseUrl + "/index.html";
    }

}
