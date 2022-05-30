package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.TrainsetPostIonHisMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetPostionHisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service
public class TrainsetPostionHisServiceImpl implements TrainsetPostionHisService {

    @Resource
    TrainsetPostIonHisMapper trainsetPostionHisMapper;

    @Override
    public int addTrainsetPostionHis(TrainsetPostionHis trainsetPositionHisEntity) {
        trainsetPositionHisEntity.setId(UUID.randomUUID().toString());
        return trainsetPostionHisMapper.addTrainsetPostionHis(trainsetPositionHisEntity);
    }

    @Override
    public void setTrainsetState(TrainsetIsConnect trainsetIsConnect) {
        trainsetPostionHisMapper.setTrainsetState(trainsetIsConnect);
    }
}
