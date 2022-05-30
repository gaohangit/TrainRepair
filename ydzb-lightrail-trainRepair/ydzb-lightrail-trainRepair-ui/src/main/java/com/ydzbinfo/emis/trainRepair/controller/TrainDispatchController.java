package com.ydzbinfo.emis.trainRepair.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trainDispatch")
@Api(description = "派工")
public class TrainDispatchController {

    final String baseUrl = "/emis/trainRepair/trainDispatch";

    @ApiOperation("班组岗位类型配置")
    @RequiresPermissions("teamPostTypeConfig:view")
    @GetMapping("teamPostTypeConfig")
    public String teamPostTypeConfig() {
        return baseUrl + "/teamPostTypeConfig/teamPostTypeConfig.html";
    }

    @ApiOperation("检修派工配置")
    @RequiresPermissions("repairDispatchConfig:view")
    @GetMapping("repairDispatchConfig")
    public String repairDispatchConfig() {
        return baseUrl + "/repairDispatchConfig/repairDispatchConfig.html";
    }

    @ApiOperation("检修派工管理")
    @RequiresPermissions("repairDispatchManage:view")
    @GetMapping("repairDispatchManage")
    public String repairDispatchManage() {
        return baseUrl + "/repairDispatchManage/repairDispatchManage.html";
    }

    @ApiOperation("派工记录查询")
    @RequiresPermissions("repairDispatchRecordQuery:view")
    @GetMapping("repairDispatchRecordQuery")
    public String repairDispatchRecordQuery() {
        return baseUrl + "/repairDispatchRecordQuery/repairDispatchRecordQuery.html";
    }
}
