package com.ydzbinfo.emis.common.trainMonitor.controller;

import com.ydzbinfo.emis.common.trainMonitor.service.ITrackPowerStateCurService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.utils.ContextUtils;
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
 * @description股道供断电信息
 * @createDate 2021/3/1 10:50
 **/
@RestController
@RequestMapping("/iTrackPowerStateCur")
public class ITrackPowerStateCurController {
    protected static final Logger logger = LoggerFactory.getLogger(ITrackPowerStateCurController.class);

    
    @Resource
    ITrackPowerStateCurService trackpowerStateCurService;


    @ApiOperation("获取股道供断电信息")
    @GetMapping(value = "/getTrackPowerInfo")
    //trackCodeList 股道code  unitCodeList单位编码(多个用,间隔)
    public Object getTrackPowerInfo(String trackCodeList, String unitCodeList) {

        unitCodeList = ContextUtils.getUnitCode();

        List<String> trackCodes = new ArrayList<>();
        List<String> unitCodes = new ArrayList<>();
        if (trackCodeList != null && !trackCodeList.equals("")) {
            trackCodes = Arrays.asList(trackCodeList.split(","));
        }
        unitCodes = Arrays.asList(unitCodeList.split(","));
        try {
            List<TrackPowerStateCur> trackPowerEntityList = trackpowerStateCurService.getTrackPowerInfo(trackCodes, unitCodes);
            return RestResult.success().setData(trackPowerEntityList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取股道供断电信息失败");
        }
    }

    @ApiOperation("更新股道供断电信息")
    @PostMapping(value = "/setTrackPowerInfo")
    public Object setSTrackPowerInfo(@RequestBody List<TrackPowerStateCur> trackPowerEntityList) {
        try {
            trackpowerStateCurService.setTrackPowerInfo(trackPowerEntityList);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "更新股道供断电信息失败");
        }
    }
}
