package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetLocationConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-03-01
 */
public interface TrainsetLocationConfigMapper extends BaseMapper<TrainsetLocationConfig> {
    List<TrainsetLocationConfig> getTrainsetlocationConfigs(@Param("unitCode")String unitCode);
    TrainsetLocationConfig getTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
    int addTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
    int delTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
    int updTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
}
