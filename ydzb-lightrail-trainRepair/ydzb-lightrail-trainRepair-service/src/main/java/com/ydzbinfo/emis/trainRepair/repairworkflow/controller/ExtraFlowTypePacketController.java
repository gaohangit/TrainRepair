package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypePacketService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/5/8 17:53
 **/
@RestController
@RequestMapping({"extraFlowTypePacket", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/extraFlowTypePacket"})
public class ExtraFlowTypePacketController extends BaseController {
    @Resource
    IExtraFlowTypePacketService extraFlowTypePacketService;

    @ApiOperation("修改额外流程类型作业包")
    @PostMapping(value = "setExtraFlowTypePacket")
    public Object setExtraFlowTypePacket(@RequestBody FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        try {
            extraFlowTypePacketService.setExtraFlowTypePacket(flowTypeInfoWithPackets);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改额外流程类型作业包错误");
        }
    }
    @ApiOperation(value = "获取作业包选择")
    @GetMapping("/getPackets")
    public RestResult getPacketsByFlowType(String unitCode,String flowTypeCode, String parentFlowTypeCode,@RequestParam(value = "flag", defaultValue = "false") boolean flag) {
        try {
            return RestResult.success().setData(extraFlowTypePacketService.getPacketsByFlowType(flowTypeCode,parentFlowTypeCode,unitCode,flag));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取作业包选择错误");

        }
    }
}
