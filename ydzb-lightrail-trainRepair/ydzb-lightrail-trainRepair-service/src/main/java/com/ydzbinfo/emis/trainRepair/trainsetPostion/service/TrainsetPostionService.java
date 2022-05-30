package com.ydzbinfo.emis.trainRepair.trainsetPostion.service;


import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;

import java.util.List;

public interface TrainsetPostionService {

    List<TrainsetPositionEntity> getTrainsetPostion(List<String> trackCodes, List<String> trainsetNames, List<String> unitCodes);

    TrainsetPositionEntity getTrainsetPositionById(String trainsetId);

    //取得一个车组位置 根据 股道code 车组名称 运用所
    TrainsetPositionEntity  getOneTrainsetPostion(String trackCode, String trainsetName, String unitCode);
}
