package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypeService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author gaohan
 * @description
 * @createDate 2021/5/10 10:28
 **/
@RestController
@RequestMapping({"extraFlowType", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/extraFlowType"})
public class ExtraFlowTypeController extends BaseController {
    @Resource
    IExtraFlowTypeService extraFlowTypeService;

    @ApiOperation("修改额外流程类型")
    @PostMapping(value = "setExtraFlowType")
    public Object setExtraFlowType(@RequestBody FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        try {
            extraFlowTypeService.setExtraFlowType(flowTypeInfoWithPackets);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改额外流程类型错误");
        }
    }
}
