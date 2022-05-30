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
@RequestMapping("/overhaulmanagent")
@Api(description = "检修配置管理")
public class overhaulmanagentController {
    final String baseUrl = "/emis/trainRepair/overhaulmanagent";

    @ApiOperation("故障配置查询")
    @RequiresPermissions("faultconfig:view")
    @GetMapping("faultconfig")
    public String faultconfig() {
        return baseUrl + "/faultconfig/faultconfig.html";
    }

    @ApiOperation("检修作业标准配置")
    @RequiresPermissions("repairManagent:view")
    @GetMapping("repairManagent")
    public String repairManagent() {
        return baseUrl + "/repairManagent/repairManagent.html";
    }


    @Value("${ydzb.uhfServerPort:8085}")
    private String uhfServerPort;

    @ApiOperation("RFID标签配置")
    @RequiresPermissions("rfidRegist:view")
    @GetMapping("rfidRegist")
    public String rfidRegist(Model model) {
        model.addAttribute("uhfServerPort", uhfServerPort);
        return baseUrl + "/rfidRegist/rfidRegist.html";
    }

    @ApiOperation("一级修作业过程管理")
    @RequiresPermissions("oneWorkManagement:view")
    @GetMapping("oneWorkManagement")
    public String oneWorkManagement() {
        return baseUrl + "/oneWorkManagement/oneWorkManagement.html";
    }

    @ApiOperation("二级修作业过程管理")
    @RequiresPermissions("twoWorkManagement:view")
    @GetMapping("twoWorkManagement")
    public String twoWorkManagement() {
        return baseUrl + "/twoWorkManagement/twoWorkManagement.html";
    }

    @ApiOperation("无修程作业管理")
    @RequiresPermissions("unRepairManagent:view")
    @GetMapping("unRepairManagent")
    public String unRepairManagent() {
        return baseUrl + "/unRepairManagent/unRepairManagent.html";
    }
}
