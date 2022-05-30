package com.ydzbinfo.midground.trainRepair.controller;


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
@RequestMapping("/bill")
@Api(description = "单据属性配置")
public class billController {
    final String baseUrl = "/emis/trainRepair/bill";

    @ApiOperation("单据属性配置-属性管理")
    @RequiresPermissions("dataAdministration:view")
    @GetMapping("dataAdministration")
    public String dataAdministration() {
        return baseUrl + "/dataAdministration/dataAdministration.html";
    }

    @ApiOperation("单据属性配置-类型配置")
    @RequiresPermissions("typeConfig:view")
    @GetMapping("typeConfig")
    public String typeConfig() {
        return baseUrl + "/typeConfig/typeConfig.html";
    }

    @ApiOperation("单据属性配置-单据配置")
    @RequiresPermissions("billConfig:view")
    @GetMapping("billConfig")
    public String billConfig() {
        return baseUrl + "/billConfig/billConfig.html";
    }

}
