package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;


import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowMarkConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowMarkConfigService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 流程标记配置表，目前仅支持临修作业的关键字配置 前端控制器
 * </p>
 *
 * @author gaohan
 * @since 2021-04-30
 */
@RestController
@RequestMapping({"flowMarkConfig", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/flowMarkConfig"})
public class FlowMarkConfigController extends BaseController {
    @Resource
    IFlowMarkConfigService flowMarkConfigService;

    @ApiOperation("查询关键字")
    @GetMapping(value = "getFlowMarkConfigList")
    public Object getTaskFlowConfigList(String unitCode) {
        try {
            List<FlowMarkConfig> flowMarkConfigs=flowMarkConfigService.getFlowMarkConfigs(unitCode);
            return RestResult.success().setData(flowMarkConfigs);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询关键字错误");
        }
    }
    @ApiOperation("删除关键字")
    @GetMapping(value = "delFlowMarkConfigById")
    public Object delFlowMarkConfigById(String id) {
        try {
            flowMarkConfigService.delFlowMarkConfigById(id);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "删除关键字错误");
        }
    }
    @ApiOperation("新增关键字")
    @PostMapping(value = "addFlowMarkConfig")
    public Object addFlowMarkConfig(@RequestBody FlowMarkConfig flowMarkConfig) {
        try {
            String result = flowMarkConfigService.addFlowMarkConfig(flowMarkConfig);
            return RestResult.success().setData(result);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "新增关键字错误");
        }
    }

}
