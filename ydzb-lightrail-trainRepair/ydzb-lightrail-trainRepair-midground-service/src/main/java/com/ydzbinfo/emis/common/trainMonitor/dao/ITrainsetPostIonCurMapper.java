package com.ydzbinfo.emis.common.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-02-26
 */
public interface ITrainsetPostIonCurMapper extends BaseMapper<TrainsetPostionCur> {
    List<TrainsetPostionCur> getTrainsetPostion(Page page, @Param("trackCodes") List<String> trackCodes, @Param("trainsetNames") List<String> trainsetNames, @Param("unitCodes") List<String> unitCodes);

    List<TrainsetPostionCur> getTrainsetPostionByTrackCode(String trackCode);
    int addTrainsetPostion(TrainsetPostionCur trainsetPositionEntity);
    int updateTrainsetPostion(TrainsetPostionCur trainsetPositionEntity);
    TrainsetPostionCur getTrainsetPositionById(String trainsetId);
    int deleteTrainsetPostion(String trainsetId);
    void setTrainsetState(TrainsetIsConnect trainsetIsConnect);
    Date getOutTime(@Param("nowDate") String nowDate,@Param("trackCode") String trackCode);

    void updTrackCode(TrainsetPostionCur trainsetPostIonCur);

}
