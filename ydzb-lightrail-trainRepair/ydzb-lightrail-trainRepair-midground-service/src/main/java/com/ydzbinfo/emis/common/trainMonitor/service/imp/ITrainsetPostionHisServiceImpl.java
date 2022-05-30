package com.ydzbinfo.emis.common.trainMonitor.service.imp;

import com.ydzbinfo.emis.common.trainMonitor.dao.ITrainsetPostIonCurMapper;
import com.ydzbinfo.emis.common.trainMonitor.dao.ITrainsetPostIonHisMapper;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrainsetPostionHisService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;
import com.ydzbinfo.emis.utils.LockUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class ITrainsetPostionHisServiceImpl implements ITrainsetPostionHisService {
    private final LockUtil lockUtil = LockUtil.newInstance();
    @Resource
    ITrainsetPostIonHisMapper trainsetPostionHisMapper;
    @Resource
    ITrainsetPostIonCurMapper trainsetPostIonCurMapper;

    @Override
    public int addTrainsetPostionHis(TrainsetPostionHis trainsetPositionHisEntity) {
        trainsetPositionHisEntity.setId(UUID.randomUUID().toString());
        return trainsetPostionHisMapper.addTrainsetPostionHis(trainsetPositionHisEntity);
    }

    @Override
    public void setTrainsetState(TrainsetIsConnect trainsetIsConnect) {
        Set<Object> lockKeys = new HashSet<>();
        try {
            TrainsetPostionCur trainsetPostIonCur = trainsetPostIonCurMapper.selectById(trainsetIsConnect.getTrainsetPostIonIds().get(0));
            if (trainsetPostIonCur == null || trainsetPostIonCurMapper.selectById(trainsetIsConnect.getTrainsetPostIonIds().get(1)) == null) {
                throw new RuntimeException("请刷新检查车组信息");
            }
            trainsetIsConnect.setTrackCode(trainsetPostIonCur.getTrackCode());
            lockKeys.add(trainsetIsConnect);
            lockUtil.getDoLock(trainsetIsConnect).lock();

            trainsetPostIonCurMapper.setTrainsetState(trainsetIsConnect);
            //历史表数据
            for (String trainsetPostIonId : trainsetIsConnect.getTrainsetPostIonIds()) {
                TrainsetPostionCur trainsetPostionCur = trainsetPostIonCurMapper.selectById(trainsetPostIonId);
                TrainsetPostionHis trainsetPostionHis = new TrainsetPostionHis();
                BeanUtils.copyProperties(trainsetPostionCur, trainsetPostionHis);
                this.addTrainsetPostionHis(trainsetPostionHis);
            }
            lockUtil.unlock(trainsetIsConnect);
        } finally {
            // 如果有未解锁的，全部解锁
            for (Object lockKey : lockKeys) {
                lockUtil.unlockAll(lockKey);
            }
        }
    }
}
