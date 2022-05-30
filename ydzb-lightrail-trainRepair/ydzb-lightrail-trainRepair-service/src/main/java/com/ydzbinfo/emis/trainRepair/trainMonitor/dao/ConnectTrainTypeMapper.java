package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.ConnectTrainType;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-03-01
 */
public interface ConnectTrainTypeMapper extends BaseMapper<ConnectTrainType> {

    List<ConnectTrainType> getConnectTrainTypes(Page page);

    ConnectTrainType getConnectTrainType(ConnectTrainType connectTrainType);

    int addConnectTrainType(ConnectTrainType connectTrainType);

    int updConnectTrainType(ConnectTrainType connectTrainType);

    int delConnectTrainType(ConnectTrainType connectTrainType);
}
