package com.ydzbinfo.emis.depot.controller;


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
@RequestMapping("/workManagement")
@Api(description = "检修配置管理")
public class WorkManagementController {
    final String baseUrl = "/emis/depot/workManagement";

    @ApiOperation("一级修作业过程管理")
    @RequiresPermissions("oneWorkManagementDepot:view")
    @GetMapping("oneWorkManagementDepot")
    public String oneWorkManagementDepot() {
        return baseUrl + "/oneWorkManagementDepot/oneWorkManagementDepot.html";
    }

    @ApiOperation("二级修作业过程管理")
    @RequiresPermissions("twoWorkManagementDepot:view")
    @GetMapping("twoWorkManagementDepot")
    public String twoWorkManagementDepot() {
        return baseUrl + "/twoWorkManagementDepot/twoWorkManagementDepot.html";
    }

    @ApiOperation("无修程作业管理")
    @RequiresPermissions("unRepairManagentDepot:view")
    @GetMapping("unRepairManagentDepot")
    public String unRepairManagentDepot() {
        return baseUrl + "/unRepairManagentDepot/unRepairManagentDepot.html";
    }
}
