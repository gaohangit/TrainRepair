package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetLocationConfig;

import java.util.List;

public interface TrainsetLocationConfigService {
    List<TrainsetLocationConfig> getTrainsetlocationConfigs(String unitCode);
    TrainsetLocationConfig getTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
    void addTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
    int delTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
    int updTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig);
}
