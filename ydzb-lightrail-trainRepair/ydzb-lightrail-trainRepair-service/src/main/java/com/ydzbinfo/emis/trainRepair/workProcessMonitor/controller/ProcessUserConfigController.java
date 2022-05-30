package com.ydzbinfo.emis.trainRepair.workProcessMonitor.controller;


import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.querymodel.ProcessUserConfig;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.service.IProcessUserconfigService;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 作业过程监控
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-24
 */
@Controller
@RequestMapping("/processUserConfig")
public class ProcessUserConfigController {
    //日志记录
    protected static final Logger logger = LoggerFactory.getLogger(ProcessUserConfigController.class);

    @Autowired
    IProcessUserconfigService processUserconfigService;

    @ApiOperation(value = "获取作业过程监控配置", notes = "获取作业过程监控配置")
    @GetMapping(value = "/getWorkProcessMonitorConfigList")
    @ResponseBody
    public RestResult getWorkProcessMonitorConfigList(@RequestParam("unitCode") String unitCode) {
        logger.info("/processUserConfig/getWorkProcessMonitorConfigList----开始获取作业过程监控配置接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前登录用户
            ShiroUser currentUser = ShiroKit.getUser();

            List<ProcessUserConfig> processUserConfigList = MybatisPlusUtils.selectList(
                processUserconfigService,
                eqParam(ProcessUserConfig::getUnitCode, unitCode)
            );

            if (processUserConfigList.size() > 0) {
                List<ProcessUserConfig> trackRefreshTimeList = processUserConfigList.stream().filter(t -> t.getParmName().equals("TrackRefreshTime")).collect(Collectors.toList());
                if (trackRefreshTimeList.size() == 0) {
                    ProcessUserConfig trackRefreshTimeConfig = new ProcessUserConfig();
                    trackRefreshTimeConfig.setConfigId(UUID.randomUUID().toString());
                    trackRefreshTimeConfig.setStaffId(currentUser.getStaffId());
                    trackRefreshTimeConfig.setParmName("TrackRefreshTime");
                    trackRefreshTimeConfig.setParmValue("5");
                    trackRefreshTimeConfig.setUnitCode(unitCode);
                    processUserconfigService.insert(trackRefreshTimeConfig);
                    processUserConfigList.add(trackRefreshTimeConfig);
                }
            }

            result.setData(processUserConfigList);
            result.setMsg("获取成功");
        } catch (Exception ex) {
            logger.error("/processUserConfig/getWorkProcessMonitorConfigList----调用获取作业过程监控配置出错...", ex);
            result = RestResult.fromException(ex, logger, "获取失败");
        }
        logger.info("/processUserConfig/getWorkProcessMonitorConfigList----获取作业过程监控配置接口结束");
        return result;
    }

    @ApiOperation(value = "修改作业过程监控配置", notes = "修改作业过程监控配置")
    @PostMapping(value = "/updateWorkProcessMonitorConfigList")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult updateWorkProcessMonitorConfigList(@RequestBody JSONObject jsonObject) {
        logger.info("/processUserConfig/updateWorkProcessMonitorConfigList----开始修改作业过程监控配置接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前登录用户
            ShiroUser currentUser = ShiroKit.getUser();
            List<ProcessUserConfig> processUserConfigList = jsonObject.getJSONArray("processUserConfigList").toJavaList(ProcessUserConfig.class);
            if (processUserConfigList != null && processUserConfigList.size() > 0) {
                processUserConfigList = processUserConfigList.stream().map(configItem -> {
                    if (configItem.getConfigId() == null || configItem.getConfigId().equals("")) {
                        configItem.setConfigId(UUID.randomUUID().toString());
                    }
                    configItem.setStaffId(currentUser.getStaffId());
                    return configItem;
                }).collect(Collectors.toList());
                //全删
                boolean del = MybatisPlusUtils.delete(
                    processUserconfigService,
                    eqParam(ProcessUserConfig::getUnitCode, processUserConfigList.get(0).getUnitCode()),
                    eqParam(ProcessUserConfig::getParmName, "TrackCode")
                );
                //全插
                boolean add = processUserconfigService.insertOrUpdateBatch(processUserConfigList);
            } else {
                throw new RuntimeException("无修改数据");
            }
            result.setMsg("修改成功");
        } catch (Exception ex) {
            logger.error("/processUserConfig/updateWorkProcessMonitorConfigList----调用修改作业过程监控配置出错...", ex);
            result = RestResult.fromException(ex, logger, "修改失败");
        }
        logger.info("/processUserConfig/updateWorkProcessMonitorConfigList----修改作业过程监控配置结束");
        return result;
    }
}
