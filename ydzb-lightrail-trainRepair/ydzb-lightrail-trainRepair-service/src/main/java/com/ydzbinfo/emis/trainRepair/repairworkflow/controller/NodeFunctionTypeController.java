package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeFunctionTypeService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author gaohan
 * @description
 * @createDate 2021/4/27 16:47
 **/
@RestController
@RequestMapping({"nodeFunctionType", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/nodeFunctionType"})
public class NodeFunctionTypeController extends BaseController {
    @Resource
    INodeFunctionTypeService nodeFunctionTypeService;

    @ApiOperation("查询节点业务类型")
    @GetMapping(value = "getTaskFlowConfigList")
    public Object getTaskFlowConfigList() {
        try {
            return RestResult.success().setData(nodeFunctionTypeService.getNodeFunctionTypeList());
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询节点业务类型错误");
        }
    }
}
