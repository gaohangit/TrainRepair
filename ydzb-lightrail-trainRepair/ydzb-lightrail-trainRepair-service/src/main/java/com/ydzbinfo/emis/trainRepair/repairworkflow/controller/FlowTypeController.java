package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowTypeService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/4/9 15:56
 **/
@RestController
@RequestMapping({"flowType", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/flowType"})
public class FlowTypeController extends BaseController {
    @Resource
    IFlowTypeService flowTypeService;

    @ApiOperation("查询流程类型")
    @GetMapping(value = "getFlowTypeList")
    public Object getFlowTypeList(String unitCode, @RequestParam(value = "config", defaultValue = "false") Boolean config) {
        try {
            List<BaseFlowType> flowTypeList = flowTypeService.getFlowTypeList(unitCode, config);
            return RestResult.success().setData(flowTypeList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询流程类型错误");
        }
    }

    @ApiOperation("获取流程类型和作业包配置")
    @GetMapping(value = "getFlowTypeAndPacket")
    public Object getFlowTypeAndPacket(String unitCode, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        try {
            List<FlowTypeInfoWithPackets> flowTypeAndPacketList = flowTypeService.getFlowTypeAndPacket(unitCode);
            return RestResult.success().setData(CommonUtils.getPage(flowTypeAndPacketList, pageNum, pageSize));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取流程类型和作业包配置错误");
        }
    }

    @ApiOperation("根据页面code查询流程类型")
    @GetMapping(value = "getFlowTypeListByFlowPageCode")
    public Object getFlowTypeList(String flowPageCode,@RequestParam String unitCode) {
        try {
            List<BaseFlowType> flowTypeList = flowTypeService.getFlowTypesByFlowPageCode(flowPageCode, unitCode);
            return RestResult.success().setData(flowTypeList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "根据页面code查询流程类型错误");
        }
    }
}
