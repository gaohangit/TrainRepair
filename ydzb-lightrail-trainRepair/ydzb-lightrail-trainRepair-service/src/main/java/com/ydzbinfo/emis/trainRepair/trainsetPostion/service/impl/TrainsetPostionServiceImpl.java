package com.ydzbinfo.emis.trainRepair.trainsetPostion.service.impl;

import com.ydzbinfo.emis.trainRepair.taskAllot.dao.TrainsetPostionMapper;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.service.TrainsetPostionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainsetPostionServiceImpl implements TrainsetPostionService {

    @Resource
    private TrainsetPostionMapper trainsetPositionMapper;

    @Override
    public List<TrainsetPositionEntity> getTrainsetPostion(List<String> trackCodes, List<String> trainsetNames,
                                                           List<String> unitCodes) {
        Map<String, Object> map = new HashMap<>();
        map.put("trackCodes", trackCodes);
        map.put("trainsetNames", trainsetNames);
        map.put("unitCodes", unitCodes);
        return trainsetPositionMapper.getTrainsetPostion(map);
    }

    @Override
    public TrainsetPositionEntity getTrainsetPositionById(String trainsetId) {
        return trainsetPositionMapper.getTrainsetPositionById(trainsetId);
    }

    // 取得一个车组位置 根据 股道code 车组名称集合 运用所
    @Override
    public TrainsetPositionEntity getOneTrainsetPostion(String trackCode, String trainsetName, String unitCode) {
        return trainsetPositionMapper.getOneTrainsetPostion(trackCode, trainsetName, unitCode);
    }
}
