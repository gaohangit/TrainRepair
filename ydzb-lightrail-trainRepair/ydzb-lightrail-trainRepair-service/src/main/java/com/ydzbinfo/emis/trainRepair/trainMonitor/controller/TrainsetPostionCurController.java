package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetPostionCurWithNextTrack;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gaohan
 * @description 车组位置
 * @createDate 2021/3/1 10:48
 **/
@RestController
@RequestMapping("/trainsetPostIonCur")
public class TrainsetPostionCurController {
    protected static final Logger logger = LoggerFactory.getLogger(TrainsetPostionCurController.class);

    @Autowired
    private IRepairMidGroundService repairMidGroundService;

    @ApiOperation("根据运用所和股道集合获取车组位置信息")
    @GetMapping(value = "/getTrainsetPostIon")
    //unitCode 单位编码 trackCodesJsonStr股道编码 trainsetNameStr车组名称
    public Object getTrainsetPostIon(String unitCode, String trackCodesJsonStr, String trainsetNameStr, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10000") int pageSize) {
        try {
            List<TrainsetPostionCurWithNextTrack> trainsetPostionCurList = repairMidGroundService.getTrainsetPosition(unitCode, trackCodesJsonStr, trainsetNameStr);
            return RestResult.success().setData(trainsetPostionCurList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "调用中台-获取车组位置信息错误");
        }
    }

    @ApiOperation("修改车组位置信息")
    @PostMapping(value = "/setTrainsetPostIon")
    public Object setTrainsetPostIon(@RequestBody TrainsetPostionCur positionList) {
        try {
            String result = repairMidGroundService.setTrainsetPostIon(positionList);
            return RestResult.success().setData(result);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "调用中台-修改车组位置信息错误");
        }
    }


    @ApiOperation("修改车组状态")
    @PostMapping(value = "/setTrainsetState")
    public Object setTrainsetPostIon(@RequestBody TrainsetIsConnect trainsetIsConnect) {
        try {
            repairMidGroundService.setTrainsetState(trainsetIsConnect);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "调用中台-修改车组状态错误");
        }
    }

    @ApiOperation("车组转线")
    @PostMapping(value = "/updTrackCode")
    public Object updTrackCode(@RequestBody List<TrainsetPostionCur> trainsetIsConnects) {
        try {
            String result = repairMidGroundService.updTrackCode(trainsetIsConnects);
            return RestResult.success().setData(result);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "调用中台-车组转线错误");
        }
    }
}
