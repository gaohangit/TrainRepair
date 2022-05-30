package com.ydzbinfo.huhekezhengsuo.trainRepair.controller;


import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 呼和客整所单据回填页面
 *
 * @author 张天可
 */
@Controller
@RequestMapping("/huhekezhengsuo/bill")
public class HuhekezhengsuoBillController {
    final String baseUrl = "/huhekezhengsuo/trainRepair/bill";

    @ApiOperation("一二级修单据回填")
    @GetMapping("repairBillBackfill")
    public String repairBillBackfill() {
        return baseUrl + "/repairBillBackfill/repairBillBackfill.html";
    }

}
