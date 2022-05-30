package com.ydzbinfo.huhekezhengsuo.trainRepair.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 呼和客整所流程页面
 *
 * @author 张天可
 */
@Controller
@RequestMapping("/huhekezhengsuo/workRepairFlow")
public class HuhekezhengsuoWorkRepairFlowController {

    final String baseUrl = "/huhekezhengsuo/trainRepair/workRepairFlow";

    @ApiOperation("作业流程配置")
    @GetMapping("workRepairFlowConfig")
    public String workRepairFlowConfig() {
        return baseUrl + "/workRepairFlowConfig/workRepairFlowConfig.html";
    }

}
