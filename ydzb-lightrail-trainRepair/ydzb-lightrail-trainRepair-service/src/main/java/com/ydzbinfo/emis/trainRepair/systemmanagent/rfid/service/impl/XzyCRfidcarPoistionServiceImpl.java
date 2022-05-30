package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPositionEntity;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.dao.XzyCRfidcarPoistionMapper;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidPosition;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarPoistion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.XzyCRfidcarPoistionService;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XzyCRfidcarPoistionServiceImpl extends ServiceImpl<XzyCRfidcarPoistionMapper, XzyCRfidcarPoistion> implements XzyCRfidcarPoistionService {

    @Autowired
    private IRemoteService remoteService;

    @Override
    public Page<RfidPosition> selectRfIdPosition(Integer pageNum, Integer pageSize, String tid, String trackCode, String placeCode, Integer carCount, String unitCode) {
        Page<RfidPosition> page = new Page<>(pageNum, pageSize);
        List<RfidPosition> rfidPosition = baseMapper.selectRfIdPosition("%" + MybatisOgnlUtils.replaceWildcardChars(tid) + "%", trackCode, placeCode, carCount, page);
        Map<String, List<ZtTaskPositionEntity>> map = new HashMap<>();
        for (RfidPosition position : rfidPosition) {
            List<ZtTaskPositionEntity> ztTaskPositionEntity = map.get(position.getTrackCode());
            if (CollectionUtils.isEmpty(ztTaskPositionEntity)){
                if (!StringUtils.isEmpty(position.getTrackCode())){
                    ztTaskPositionEntity = remoteService.getTrackPositionByTrackCode(position.getTrackCode(), unitCode);
                    map.put(position.getTrackCode(), ztTaskPositionEntity);
                }
            }
            for (ZtTaskPositionEntity taskPositionEntity : ztTaskPositionEntity) {
                if ("0".equals(taskPositionEntity.getDirectionCode())){
                    position.setHeadDirectionName(taskPositionEntity.getTrackPostionName());
                }else {
                    position.setTailDirectionName(taskPositionEntity.getTrackPostionName());
                }
            }
        }
        page.setRecords(rfidPosition);
        return page;
    }
}
