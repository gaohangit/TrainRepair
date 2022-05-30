package com.ydzbinfo.emis.common.trainMonitor.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrainsetPostIonCurService;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrainsetPostionHisService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetPostionCurWithNextTrack;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaohan
 * @description 车组位置
 * @createDate 2021/3/1 10:48
 **/
@RestController
@RequestMapping("/iTrainsetPostIonCur")
public class ITrainsetPostionCurController {
    protected static final Logger logger = LoggerFactory.getLogger(ITrainsetPostionCurController.class);

        @Resource
    ITrainsetPostIonCurService trainsetPostIonCurService;
    @Resource
    ITrainsetPostionHisService trainsetPostionHisService;

    @ApiOperation("根据运用所和股道集合获取车组位置信息")
    @GetMapping(value = "/getTrainsetPostIon")
    //unitCode 单位编码 trackCodesJsonStr股道编码 trainsetNameStr车组名称
    public RestResult getTrainsetPostIon(String unitCode, String trackCodesJsonStr, String trainsetNameStr, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10000") int pageSize,boolean showShuntPlan) {
        //unitCode= UserShiroUtil.getUserInfo().getDeptCode();
        List<String> trackCodes = new ArrayList<>();
        List<String> trainsetNames = new ArrayList<>();
        List<String> unitCodes;
        //判断股道CODE是否为空
        if (trackCodesJsonStr != null && !trackCodesJsonStr.equals("")) {
            trackCodes = Arrays.asList(trackCodesJsonStr.split(","));
        }
        //判断车组号集合是否为空
        if (trainsetNameStr != null && !trainsetNameStr.equals("")) {
            trainsetNames = Arrays.asList(trainsetNameStr.split(","));
        }
        if (unitCode != null && !unitCode.equals("")) {
            unitCodes = Arrays.asList(unitCode.split(","));
        } else {
            throw RestRequestException.normalFail("unitCode不能为空");
        }
        try {
            Page<TrainsetPostionCurWithNextTrack> page = new Page<>(pageNum, pageSize);
            List<TrainsetPostionCurWithNextTrack> trainsetPostionCurs = trainsetPostIonCurService.getTrainsetPostion(page, trackCodes, trainsetNames, unitCodes,showShuntPlan);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("trainsetPostIonCurs", trainsetPostionCurs);
            jsonObject.put("count", page.getTotal());
            return RestResult.success().setData(jsonObject);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "根据运用所和股道集合获取车组位置信息错误");
        }
    }

    @ApiOperation("修改车组位置信息")
    @PostMapping(value = "/setTrainsetPostIon")
    public Object setTrainsetPostIon(@RequestBody TrainsetPostionCur positionList) {
        try {
            positionList.setUnitCode(ContextUtils.getUnitCode());
            positionList.setRecordUserName(ContextUtils.getUnitName());
            positionList.setRecordUserCode(ContextUtils.getUnitCode());
            String hasTrackByTrainsetId = trainsetPostIonCurService.setTrainsetPosition(positionList);
            return RestResult.success().setData(hasTrackByTrainsetId);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改车组位置信息失败");
        }
    }


    @ApiOperation("修改车组状态")
    @PostMapping(value = "/setTrainsetState")
    public Object setTrainsetPostIon(@RequestBody TrainsetIsConnect trainsetIsConnect) {
        try {
            trainsetPostionHisService.setTrainsetState(trainsetIsConnect);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改车组状态失败");
        }
    }

    @ApiOperation("车组转线")
    @PostMapping(value = "/updTrackCode")
    public Object updTrackCode(@RequestBody List<TrainsetPostionCur> trainsetIsConnects) {
        try {
            String hasTrackTrainsetId = trainsetPostIonCurService.updTrackCode(trainsetIsConnects);
            return RestResult.success().setData(hasTrackTrainsetId);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "车组转线失败");
        }
    }
}
