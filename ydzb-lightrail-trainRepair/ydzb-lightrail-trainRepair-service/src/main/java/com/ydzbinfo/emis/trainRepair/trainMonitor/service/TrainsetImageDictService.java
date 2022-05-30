package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetImageDict;

import java.util.List;

public interface TrainsetImageDictService {

    List<TrainsetImageDict> getTrainsetImageDictList(String trainType);

    TrainsetImageDict getTrainsetImageDict(TrainsetImageDict trainsetImageDict);

    int addTrainsetImageDict(TrainsetImageDict trainsetImageDict);

    int updTrainsetImageDict(TrainsetImageDict trainsetImageDict);

    int delTrainsetImageDict(TrainsetImageDict trainsetImageDict);
}
