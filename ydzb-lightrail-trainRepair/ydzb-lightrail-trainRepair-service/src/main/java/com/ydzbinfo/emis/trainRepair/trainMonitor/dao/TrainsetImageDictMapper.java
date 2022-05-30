package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetImageDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-03-02
 */
public interface TrainsetImageDictMapper extends BaseMapper<TrainsetImageDict> {
    List<TrainsetImageDict> getTrainsetImageDictList(@Param("trainType") String trainTyp);

    TrainsetImageDict getTrainsetImageDict(TrainsetImageDict trainsetImageDict);

    int addTrainsetImageDict(TrainsetImageDict trainsetImageDict);

    int updTrainsetImageDict(TrainsetImageDict trainsetImageDict);

    int delTrainsetImageDict(TrainsetImageDict trainsetImageDict);



}
