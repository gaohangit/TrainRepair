package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateCurService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description股道供断电信息
 * @createDate 2021/3/1 10:50
 **/
@RestController
@RequestMapping({"trackPowerStateCur", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/trackPowerStateCur"})
public class TrackPowerStateCurController {
    protected static final Logger logger = LoggerFactory.getLogger(TrackPowerStateCurController.class);

    
    @Resource
    TrackPowerStateCurService trackpowerStateCurService;
    @Resource
    IRemoteService iRemoteService;
    @Autowired
    private IRepairMidGroundService repairMidGroundService;

    @Autowired
    private TrackPowerStateCurService trackPowerStateCurService;

    @ApiOperation("获取显示股道")
    @GetMapping(value = "/getAllTrackArea")
    public Object getAllTrackArea() {
        try {
            User userInfo = UserUtil.getUserInfo();
            String unitCode = ContextUtils.getUnitCode();
            List<TrackPowerStateCur> trackPowerEntities = new ArrayList<>();
            //获取本单位下的股道
            List<ZtTrackAreaEntity> trackAreaEntities = trackPowerStateCurService.getTrackAreaByDept(unitCode);
            if (trackAreaEntities != null && trackAreaEntities.size() > 0) {
                List<ZtTrackPositionEntity> trackPositions = trackAreaEntities.stream().flatMap(v -> v.getLstTrackInfo().stream()).flatMap(v -> v.getLstTrackPositionInfo().stream()).collect(Collectors.toList());
                //获取库里已又的股道供断电信息
                trackPowerEntities = trackpowerStateCurService.selectTrackPowersFromTrackPositions(CommonUtils.getDistinctList(trackPositions, v -> v.getTrackPositionCode()));
            }
            //不存在库里的股道供断点信息
            trackpowerStateCurService.getAllTrackPowers(unitCode);

            Map<String, Object> data = new HashMap<>();
            data.put("trackAreas", trackAreaEntities);
            data.put("trackPowers", trackPowerEntities);
            data.put("date", new Date());
            return RestResult.success().setData(data);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取显示股道错误");
        }
    }

    @ApiOperation("获取股道供断电信息")
    @GetMapping(value = "/getTrackPowerInfo")
    //trackCodeList 股道code  unitCodeList单位编码(多个用,间隔)
    public Object getTrackPowerInfo(String trackCodeList, String unitCodeList) {
        List<TrackPowerStateCur> trackPowerStateCurs = repairMidGroundService.getTrackPowerInfo(trackCodeList, unitCodeList);
        return RestResult.success().setData(trackPowerStateCurs);
    }

    @ApiOperation("更新股道供断电信息")
    @PostMapping(value = "/setTrackPowerInfo")
    public Object setSTrackPowerInfo(@RequestBody List<TrackPowerStateCur> trackPowerEntityList) {
        repairMidGroundService.setTrackPowerInfo(trackPowerEntityList);
        return RestResult.success();
    }
}
