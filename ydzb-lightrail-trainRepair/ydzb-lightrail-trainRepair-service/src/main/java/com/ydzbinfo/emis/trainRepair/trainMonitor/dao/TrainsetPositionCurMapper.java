package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-02-26
 */
public interface TrainsetPositionCurMapper extends BaseMapper<TrainsetPostionCur> {
    List<TrainsetPostionCur> getTrainsetPostion(@Param("trackCodes") Set<String> trackCodes, @Param("unitCode") String unitCodes);

    List<TrainsetPostionCur> getTrainsetPostionByTrackCode(String trackCode);
    int addTrainsetPostion(TrainsetPostionCur trainsetPositionEntity);
    int updateTrainsetPostion(TrainsetPostionCur trainsetPositionEntity);
    TrainsetPostionCur getTrainsetPositionById(String trainsetId);
    int deleteTrainsetPostion(String trainsetId);
    void setTrainsetState(TrainsetIsConnect trainsetIsConnect);
    Date getOutTime(@Param("nowDate") String nowDate,@Param("trackCode") String trackCode);

    void updTrackCode(TrainsetPostionCur trainsetPostIonCur);

    List<TrainsetPostionCur> getByTrainsetIds(@Param("trainsetIds") String[] trainsetIds);
}
