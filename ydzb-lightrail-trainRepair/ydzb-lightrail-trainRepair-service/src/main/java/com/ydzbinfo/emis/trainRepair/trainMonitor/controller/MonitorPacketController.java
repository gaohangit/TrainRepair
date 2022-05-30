package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;


import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.LevelRepairInfo;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.MonitorBase;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.MonitorPacketService;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description 作业包
 * @createDate 2021/3/1 17:46
 **/
@RestController
@RequestMapping("/monitorPacket")
public class MonitorPacketController {

    protected static final Logger logger = LoggerFactory.getLogger(MonitorNoteContentController.class);

    
    @Resource
    MonitorPacketService monitorPacketService;

    @ApiOperation(value = "获取所有作业包配置")
    @GetMapping("/getMonitorPackets")
    public Object getMonitorPackets(String packetCode,String suitModel,String suitBatch,@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        try{
            List<MonitorBase> monitorBases=monitorPacketService.getMonitorBase(packetCode,suitModel,suitBatch);
            return RestResult.success().setData(CommonUtils.getPage(monitorBases,pageNum,pageSize));
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有作业包配置错误");
        }
    }



    @ApiOperation(value = "修改作业包")
    @PostMapping("/updMonitorPacket")
    public Object updMonitorPacket(@RequestBody List<MonitorBase> monitorPackets){
        try{
            monitorPacketService.updMonitorBase(monitorPackets);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "修改作业包");

        }
    }


    @ApiOperation(value = "获取检修列表")
    @GetMapping("/getPgMPacketrecordList")
    public Object getPgMPacketrecordList(String trainsetName, String trainsetId, String unitCode) {
        try {
            LevelRepairInfo levelRepairInfo = monitorPacketService.getPacketRecordList(trainsetName, trainsetId ,unitCode);
            return RestResult.success().setData(levelRepairInfo);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取检修列表错误");
        }
    }

    @ApiOperation(value = "获取作业包选择")
    @GetMapping("/getPacketList")
    public RestResult getPacketList(String packetCode, String trainType, String batchCode) {
        try {
            List<PacketInfo> packetInfoList = monitorPacketService.getPacketList(packetCode, trainType, batchCode);
            return RestResult.success().setData(packetInfoList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取作业包选择错误");
        }
    }


}
