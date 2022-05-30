package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkMonitorConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkMonitorConfigService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author: gaoHan
 * @date: 2021/8/9 10:05
 * @description: 关键作业显示股道配置(1显示,0不显示)
 */
@RestController
@RequestMapping({"keyWorkMonitorConfig", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/keyWorkMonitorConfig"})
public class KeyWorkMonitorConfigController extends BaseController {

    @Autowired
    IKeyWorkMonitorConfigService keyWorkMonitorConfigService;

    @ApiOperation("获取本单位下股道显示配置")
    @GetMapping(value = "/getKeyWorkMonitorConfigs")
    public Object getKeyWorkMonitorConfigs() {
        try {
            List<KeyWorkMonitorConfig> keyWorkMonitorConfigs = keyWorkMonitorConfigService.getKeyWorkMonitorConfigs();
            return RestResult.success().setData(keyWorkMonitorConfigs);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取本单位下股道显示配置错误");
        }
    }

    @ApiOperation("修改本单位下股道显示配置")
    @PostMapping(value = "/setKeyWorkMonitorConfigs")
    public Object setKeyWorkMonitorConfigs(@RequestBody List<KeyWorkMonitorConfig> keyWorkMonitorConfigs) {
        try {
            keyWorkMonitorConfigService.setKeyWorkMonitorConfigs(keyWorkMonitorConfigs);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改本单位下股道显示配置错误");
        }
    }
}