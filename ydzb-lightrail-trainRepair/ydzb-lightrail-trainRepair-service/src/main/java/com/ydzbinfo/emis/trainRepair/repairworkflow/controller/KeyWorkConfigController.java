package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkConfigInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkExtraColumnOption;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkConfigService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowDatabaseConfigUtil;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.KeyWorkExtraColumnEnum;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConfigService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 高晗
 * @description 关键作业配置
 * @createDate 2021/6/18 17:08
 **/
@RestController
@RequestMapping({"KeyWorkConfig", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/KeyWorkConfig"})
public class KeyWorkConfigController extends BaseController {
    @Autowired
    ConfigService configService;

    @Autowired
    IKeyWorkConfigService keyWorkConfigService;


    @ApiOperation("查询关键作业配置")
    @GetMapping(value = "/getKeyWorkConfigList")
    public Object getKeyWorkConfigList(String unitCode, String content, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        try {
            List<KeyWorkConfigInfo> keyWorkConfigInfoList = keyWorkConfigService.getKeyWorkConfigInfoForCarNo(unitCode, content);
            return RestResult.success().setData(CommonUtils.getPage(keyWorkConfigInfoList, pageNum, pageSize));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询关键作业配置错误");
        }
    }

    @ApiOperation("根据车型查询关键作业配置")
    @GetMapping(value = "/getKeyWorkConfigListByTrainModel")
    public Object getKeyWorkConfigListByTrainModel(String unitCode, String trainModel) {
        try {
            List<KeyWorkConfigInfo> keyWorkConfigInfoList = keyWorkConfigService.getKeyWorkConfigInfoByTrainModel(unitCode, trainModel);
            return RestResult.success().setData(keyWorkConfigInfoList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "根据车型查询关键作业配置错误");
        }
    }

    /**
     * @param config 关键作业快速录入配置时，禁止选择构型(部件)
     * @return
     */
    @ApiOperation("查询关键作业额外列")
    @GetMapping(value = "/getKeyWorkExtraColumnList")
    public Object getKeyWorkExtraColumnList(@RequestParam(value = "config", defaultValue = "false") Boolean config) {
        try {
            List<KeyWorkExtraColumnEnum> keyWorkExtraColumns = FlowDatabaseConfigUtil.getKeyWorkExtraColumns();
            // 如果为关键作业快速录入配置，则过滤掉构型
            if (config) {
                keyWorkExtraColumns = keyWorkExtraColumns.stream().filter(v -> KeyWorkExtraColumnEnum.BatchBomNodeCode != v).collect(Collectors.toList());
            }
            List<Map<String, Object>> keyWorkExtraColumnList = keyWorkExtraColumns.stream().map(EnumUtils::toMap).collect(Collectors.toList());
            return RestResult.success().setData(keyWorkExtraColumnList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询关键作业额外列错误");
        }
    }

    @ApiOperation("查询关键作业额外列对应值")
    @GetMapping(value = "/getKeyWorkExtraColumnValueList")
    public Object getKeyWorkExtraColumnValueList(String columnKey, String trainModel,@RequestParam(value = "showDelete", defaultValue = "true") boolean showDelete) {
        try {
            List<KeyWorkExtraColumnOption> keyWorkExtraColumnOptions = keyWorkConfigService.getKeyWorkExtraColumnValueList(columnKey, trainModel,showDelete);
            return RestResult.success().setData(keyWorkExtraColumnOptions);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询关键作业额外列对应值错误");
        }
    }

    @ApiOperation("修改关键作业配置")
    @PostMapping(value = "/setKeyWorkConfig")
    public Object setKeyWorkConfig(@RequestBody KeyWorkConfigInfo keyWorkConfigInfo) {
        try {
            keyWorkConfigService.setKeyWorkConfig(keyWorkConfigInfo);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改关键作业配置错误");
        }
    }

    @ApiOperation("删除关键作业配置")
    @PostMapping(value = "/delKeyWorkConfig")
    public Object delKeyWorkConfig(@RequestBody KeyWorkConfigInfo keyWorkConfigInfo) {
        try {
            keyWorkConfigService.delKeyWorkConfig(keyWorkConfigInfo.getId());
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "删除关键作业配置错误");
        }
    }


}
