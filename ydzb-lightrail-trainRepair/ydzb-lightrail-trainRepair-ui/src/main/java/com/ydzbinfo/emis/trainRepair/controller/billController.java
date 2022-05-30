package com.ydzbinfo.emis.trainRepair.controller;


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
public class billController {
    final String baseUrl = "/emis/trainRepair/bill";

    @ApiOperation("单据属性配置-一二级修单据回填")
    @RequiresPermissions("repairBillBackfill:view")
    @GetMapping("repairBillBackfill")
    public String repairBillBackfill() {
        return baseUrl + "/repairBillBackfill/repairBillBackfill.html";
    }

    @ApiOperation("单据属性配置-一体化作业申请单管理")
    @RequiresPermissions("unifyWorkerApply:view")
    @GetMapping("unifyWorkerApply")
    public String unifyWorkerApply() {
        return baseUrl + "/unifyWorkerApply/unifyWorkerApply.html";
    }

    @ApiOperation("单据属性配置-出所联检单管理")
    @RequiresPermissions("outStationJointIns:view")
    @GetMapping("outStationJointIns")
    public String outStationJointIns() {
        return baseUrl + "/outStationJointIns/outStationJointIns.html";
    }

    @ApiOperation("单据属性配置-一级修机检记录单管理")
    @RequiresPermissions("oneWorkerMachineInspectionBill:view")
    @GetMapping("oneWorkerMachineInspectionBill")
    public String oneWorkerMachineInspectionBill() {
        return baseUrl + "/oneWorkerMachineInspectionBill/oneWorkerMachineInspectionBill.html";
    }
}
