package com.ydzbinfo.emis.trainRepair.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/repairWarning")
@Api(description = "检修预警")
public class RepairWarningController {

    final String baseUrl = "/emis/trainRepair/repairWarning";

    @ApiOperation("检修预警管理")
    @RequiresPermissions("repairWarningManage:view")
    @GetMapping("repairWarningManage")
    public String flowTypeConfig() {
        return baseUrl + "/repairWarningManage/repairWarningManage.html";
    }


}
