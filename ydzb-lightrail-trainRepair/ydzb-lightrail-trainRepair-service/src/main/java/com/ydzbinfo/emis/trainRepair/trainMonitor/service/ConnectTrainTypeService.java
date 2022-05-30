package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.ConnectTrainType;

import java.util.List;

public interface ConnectTrainTypeService {
    List<ConnectTrainType> getConnectTrainTypes(Page page);

    ConnectTrainType getConnectTrainType(ConnectTrainType connectTrainType);

    int addConnectTrainType(ConnectTrainType connectTrainType);

    int updConnectTrainType(ConnectTrainType connectTrainType);

    int delConnectTrainType(ConnectTrainType connectTrainType);
}
