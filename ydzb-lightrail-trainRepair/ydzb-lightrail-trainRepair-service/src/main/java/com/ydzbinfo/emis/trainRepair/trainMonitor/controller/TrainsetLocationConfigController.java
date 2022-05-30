package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetLocationConfig;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetLocationConfigService;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description 动态监控配置
 * @createDate 2021/3/1 14:48
 **/
@RestController
@RequestMapping("/trainsetLocationConfig")
public class TrainsetLocationConfigController {
    protected static final Logger logger = LoggerFactory.getLogger(TrainsetLocationConfigController.class);

    @Resource
    TrainsetLocationConfigService trainsetlocationConfigService;

    @ApiOperation(value = "获取所有动态监控配置")
    @GetMapping("/getTrainsetLocationConfigs")
    public Object getTrainsetLocationConfigs() {
        try {
            String unitCode = ContextUtils.getUnitCode();
            List<TrainsetLocationConfig> monitornotecontents = trainsetlocationConfigService.getTrainsetlocationConfigs(unitCode);
            return RestResult.success().setData(monitornotecontents);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取所有动态监控配置错误");
        }
    }

    @ApiOperation(value = "获取动态监控配置")
    @GetMapping("/getTrainsetLocationConfig")
    public Object getTrainsetLocationConfig(TrainsetLocationConfig trainsetlocationConfig) {
        try {
            TrainsetLocationConfig monitornotecontents = trainsetlocationConfigService.getTrainsetlocationConfig(trainsetlocationConfig);
            return RestResult.success().setData(monitornotecontents);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取动态监控配置错误");
        }
    }

    @ApiOperation(value = "新增动态监控配置")
    @PostMapping("/addTrainsetLocationConfig")
    public Object addTrainsetLocationConfig(@RequestBody TrainsetLocationConfig trainsetlocationConfig) {
        trainsetlocationConfig.setUnitCode(ContextUtils.getUnitCode());
        trainsetlocationConfig.setUnitName(ContextUtils.getUnitName());
        try {
            trainsetlocationConfigService.addTrainsetlocationConfig(trainsetlocationConfig);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "新增动态监控配置错误");
        }
    }

    @ApiOperation(value = "新增动态监控配置")
    @PostMapping("/addTrainsetLocationConfigs")
    public Object addTrainsetLocationConfigs(@RequestBody List<TrainsetLocationConfig> trainsetlocationConfigs) {
        for (TrainsetLocationConfig trainsetlocationConfig : trainsetlocationConfigs) {
            trainsetlocationConfig.setUnitCode(ContextUtils.getUnitCode());
            trainsetlocationConfig.setUnitName(ContextUtils.getUnitName());
            try {
                trainsetlocationConfigService.addTrainsetlocationConfig(trainsetlocationConfig);
            } catch (Exception e) {
                return RestResult.fromException(e, logger, "新增动态监控配置s错误");
            }
        }
        return RestResult.success();
    }

    @ApiOperation(value = "删除动态监控配置")
    @PostMapping("/delTrainsetLocationConfig")
    public Object delTrainsetLocationConfig(@RequestBody TrainsetLocationConfig trainsetlocationConfig) {
        try {
            trainsetlocationConfigService.delTrainsetlocationConfig(trainsetlocationConfig);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "删除动态监控配置错误");
        }
    }

    @ApiOperation(value = "修改动态监控配置")
    @PostMapping("/updTrainsetLocationConfig")
    public Object updTrainsetLocationConfig(@RequestBody List<TrainsetLocationConfig> trainsetlocationConfigs) {
        try {
            for (TrainsetLocationConfig trainsetlocationConfig : trainsetlocationConfigs) {
                trainsetlocationConfigService.updTrainsetlocationConfig(trainsetlocationConfig);
            }
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改动态监控配置错误");
        }
    }

    @ApiOperation(value = "新增股道监控配置")
    @PostMapping("/addTrackConfig")
    public Object addTrackConfig(@RequestBody List<TrainsetLocationConfig> trainsetlocationConfigs) {
        try {
            for (TrainsetLocationConfig trainsetlocationConfig : trainsetlocationConfigs) {
                if (trainsetlocationConfigService.getTrainsetlocationConfig(trainsetlocationConfig) == null) {
                    trainsetlocationConfig.setParamValue("1");
                    trainsetlocationConfigService.addTrainsetlocationConfig(trainsetlocationConfig);
                }
            }
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "新增股道监控配置错误");
        }
    }
}
