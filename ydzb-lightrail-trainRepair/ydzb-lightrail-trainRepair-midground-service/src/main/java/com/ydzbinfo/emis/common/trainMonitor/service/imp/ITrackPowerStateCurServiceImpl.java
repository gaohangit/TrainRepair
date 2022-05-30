package com.ydzbinfo.emis.common.trainMonitor.service.imp;

import com.ydzbinfo.emis.common.trainMonitor.dao.ITrackPowerStateCurMapper;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrackPowerStateCurService;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrackPowerStateHisService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateHis;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gaohan
 * @description
 * @createDate 2021/2/26 14:34
 **/
@Transactional
@Service
public class ITrackPowerStateCurServiceImpl implements ITrackPowerStateCurService {
    
    @Resource
    ITrackPowerStateCurMapper trackpowerStateCurMapperI;

    @Resource
    ITrackPowerStateHisService trackpowerStateHisServiceI;

    @Override
    public List<TrackPowerStateCur> getTrackPowerInfo(List<String> trackCodeList, List<String> unitCodeList) {
        Map<String, Object> map = new HashMap<>();
        map.put("trackCodeList", trackCodeList);
        map.put("unitCodeList", unitCodeList);
        return trackpowerStateCurMapperI.getTrackPowerInfo(map);
    }

    @Override
    public List<TrackPowerStateCur> selectTrackPowersFromTrackPositions(List<ZtTrackPositionEntity> trackPositions) {
        return trackpowerStateCurMapperI.selectTrackPowersFromTrackPositions(trackPositions);
    }

    @Override
    public List<TrackPowerStateCur> getTrackPowerInfoByTrackPower(TrackPowerStateCur trackPowerEntity) {
        return trackpowerStateCurMapperI.getTrackPowerInfoByTrackPower(trackPowerEntity);
    }

    @Override
    public int deleteByTrackPower(TrackPowerStateCur trackPowerEntity) {
        return trackpowerStateCurMapperI.deleteByTrackPower(trackPowerEntity);
    }

    @Override
    public int addTrackPowerInfo(TrackPowerStateCur trackPowerEntity) {
        trackPowerEntity.setId(UUID.randomUUID().toString());
        return trackpowerStateCurMapperI.addTrackPowerInfo(trackPowerEntity);
    }

    @Override
    public void setTrackPowerInfo(List<TrackPowerStateCur> trackPowerStateCurList) {
        for (TrackPowerStateCur trackPowerInfo : trackPowerStateCurList) {
            if(StringUtils.isBlank(trackPowerInfo.getId())){
                this.addTrackPowerInfo(trackPowerInfo);
            }else{
                trackPowerInfo.setUnitCode(ContextUtils.getUnitCode());
                trackPowerInfo.setUnitName(ContextUtils.getUnitName());
                trackpowerStateCurMapperI.updTrackPowerInfo(trackPowerInfo);
            }
                TrackPowerStateHis trackpowerStateHis=new TrackPowerStateHis();
                BeanUtils.copyProperties(trackPowerInfo, trackpowerStateHis);
                trackpowerStateHis.setState(trackPowerInfo.getState());
                trackpowerStateHis.setId(UUID.randomUUID().toString());
                trackpowerStateHis.setRecordTime(new Date());
                trackpowerStateHisServiceI.addTrackPowerHisInfo(trackpowerStateHis);
        }


    }
}
