package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.TrackPowerStateCurMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateHis;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConfigService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateCurService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateHisService;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.UserUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/2/26 14:34
 **/
@Transactional
@Service
public class TrackPowerStateCurServiceImpl extends ServiceImpl<TrackPowerStateCurMapper, TrackPowerStateCur> implements TrackPowerStateCurService {
    
    @Resource
    TrackPowerStateCurMapper trackpowerStateCurMapper;

    @Resource
    TrackPowerStateHisService trackpowerStateHisService;
    @Resource
    IRemoteService remoteService;

    @Resource
    TrackPowerStateCurService trackpowerStateCurService;

    @Resource
    ConfigService configService;

    @Override
    public List<TrackPowerStateCur> getTrackPowerInfo(Set<String> trackCodeList, String unitCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("trackCodeList", trackCodeList);
        map.put("unitCode", unitCode);
        return trackpowerStateCurMapper.getTrackPowerInfo(map);
    }

    @Override
    public List<TrackPowerStateCur> selectTrackPowersFromTrackPositions(List<ZtTrackPositionEntity> trackPositions) {
        return trackpowerStateCurMapper.selectTrackPowersFromTrackPositions(trackPositions);
    }

    @Override
    public List<TrackPowerStateCur> getTrackPowerInfoByTrackPower(TrackPowerStateCur trackPowerEntity) {
        return trackpowerStateCurMapper.getTrackPowerInfoByTrackPower(trackPowerEntity);
    }

    @Override
    public int deleteByTrackPower(TrackPowerStateCur trackPowerEntity) {
        return trackpowerStateCurMapper.deleteByTrackPower(trackPowerEntity);
    }

    @Override
    public int addTrackPowerInfo(TrackPowerStateCur trackPowerEntity) {
        trackPowerEntity.setId(UUID.randomUUID().toString());
        return trackpowerStateCurMapper.addTrackPowerInfo(trackPowerEntity);
    }

    @Override
    public void setTrackPowerInfo(TrackPowerStateCur trackPowerInfo) {
        trackPowerInfo.setUnitCode(ContextUtils.getUnitCode());
        trackPowerInfo.setUnitName(ContextUtils.getUnitName());
        trackpowerStateCurMapper.updTrackPowerInfo(trackPowerInfo);

        List<TrackPowerStateCur> trackPowerEntityWillDelete = this.getTrackPowerInfoByTrackPower(trackPowerInfo);
        for (TrackPowerStateCur trackPowerStateCur : trackPowerEntityWillDelete) {
            TrackPowerStateHis trackpowerStateHis = new TrackPowerStateHis();
            BeanUtils.copyProperties(trackPowerStateCur, trackpowerStateHis);
            trackpowerStateHis.setState(trackPowerInfo.getState());
            trackpowerStateHis.setId(UUID.randomUUID().toString());
            trackpowerStateHis.setRecordTime(new Date());
            trackpowerStateHisService.addTrackPowerHisInfo(trackpowerStateHis);
        }
    }

    @Override
    public List<TrackPowerStateCur> getAllTrackPowers(String unitCode) {
        //库里已有的股道供断点想信息
        List<TrackPowerStateCur> trackPowerEntities = new ArrayList<>();
        //获取本单位下的股道
        List<ZtTrackAreaEntity> trackAreaEntities = this.getTrackAreaByDept(unitCode);
        if (trackAreaEntities != null && trackAreaEntities.size() > 0) {
            List<ZtTrackPositionEntity> trackPositions = trackAreaEntities.stream().flatMap(v -> v.getLstTrackInfo().stream()).flatMap(v -> v.getLstTrackPositionInfo().stream()).collect(Collectors.toList());
            //获取库里已又的股道供断电信息
            trackPowerEntities = trackpowerStateCurService.selectTrackPowersFromTrackPositions(CommonUtils.getDistinctList(trackPositions, v -> v.getTrackPositionCode()));
        }
        //需要初始化的股道供断电信息
        List<ZtTrackEntity> ztTrackEntityList = new ArrayList<>();
        for (ZtTrackAreaEntity trackAreaEntity : trackAreaEntities) {
            for (ZtTrackEntity ztTrackEntity : trackAreaEntity.getLstTrackInfo()) {
                for (ZtTrackPositionEntity ztTrackPositionEntity : ztTrackEntity.getLstTrackPositionInfo()) {
                    String trackCode = String.valueOf(ztTrackPositionEntity.getTrackCode());
                    String trackPositionCode = String.valueOf(ztTrackPositionEntity.getTrackPositionCode());
                    boolean hasTrack = trackPowerEntities.stream().anyMatch(v -> v.getTrackCode().equals(trackCode) && v.getTrackPlaCode().equals(trackPositionCode));
                    if (!hasTrack) {
                        TrackPowerStateCur trackPowerStateCur = new TrackPowerStateCur();
                        trackPowerStateCur.setTrackCode(String.valueOf(ztTrackEntity.getTrackCode()));
                        trackPowerStateCur.setTrackName(ztTrackEntity.getTrackName());
                        trackPowerStateCur.setTrackPlaCode(String.valueOf(ztTrackPositionEntity.getTrackPositionCode()));
                        trackPowerStateCur.setTrackPlaName(ztTrackPositionEntity.getTrackPostionName());
                        trackPowerStateCur.setState("2");
                        trackPowerStateCur.setRecordTime(new Date());
                        User user = UserUtil.getUserInfo();
                        trackPowerStateCur.setUnitName(ContextUtils.getUnitName());
                        trackPowerStateCur.setUnitCode(ContextUtils.getUnitCode());
                        trackpowerStateCurService.addTrackPowerInfo(trackPowerStateCur);
                        trackPowerEntities.add(trackPowerStateCur);
                    }
                }
            }
        }

        return trackPowerEntities;
    }

    @Override
    public List<ZtTrackAreaEntity> getTrackAreaByDept(String deptCode) {
        List<ZtTrackAreaEntity> trackAreaEntityList = CacheUtil.getDataUseThreadCache(
            "remoteService.getTrackAreaByDept_" + deptCode,
            () -> remoteService.getTrackAreaByDept(deptCode)
        );
        String monitorTrackPlaceShowType = ConfigUtil.getMonitorTrackPlaceShowType();
        if (monitorTrackPlaceShowType != null && monitorTrackPlaceShowType.equals("2")) {
            //显示方向名称还是列位编码
            trackAreaEntityList.forEach(trackAreaEntity -> {
                trackAreaEntity.getLstTrackInfo().forEach(trackInfo -> {
                    trackInfo.getLstTrackPositionInfo().forEach(trackPositionInfo -> {
                        trackPositionInfo.setTrackPostionName(trackPositionInfo.getDirectionCode());
                    });
                });
            });
        }
        return trackAreaEntityList;
    }
}
