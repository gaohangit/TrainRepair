package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TrainsetPostionMapper {

    List<TrainsetPositionEntity> getTrainsetPostion(Map<String, Object> map);

    TrainsetPositionEntity getTrainsetPositionById(String trainsetId);

    //取得一个车组位置 根据 股道code 车组名称 运用所
    TrainsetPositionEntity  getOneTrainsetPostion(@Param("trackCode") String trackCode, @Param("trainsetName") String trainsetName, @Param("unitCode") String unitCode);
}
