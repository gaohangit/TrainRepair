package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.ConnectTrainTypeMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.ConnectTrainType;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConnectTrainTypeService;
import com.ydzbinfo.emis.utils.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/1 16:11
 **/
@Service
@Transactional
public class ConnectTrainTypeServiceImpl implements ConnectTrainTypeService {
    @Resource
    ConnectTrainTypeMapper trainTypeMapper;
    @Override
    public List<ConnectTrainType> getConnectTrainTypes(Page page) {
        return trainTypeMapper.getConnectTrainTypes(page);
    }

    @Override
    public ConnectTrainType getConnectTrainType(ConnectTrainType connectTrainType) {
        return trainTypeMapper.getConnectTrainType(connectTrainType);
    }

    @Override
    public int addConnectTrainType(ConnectTrainType connectTrainType) {
        connectTrainType.setCreateUserName(UserUtil.getUserInfo().getName());
        connectTrainType.setCreateTime(new Date());
        return trainTypeMapper.addConnectTrainType(connectTrainType);
    }

    @Override
    public int updConnectTrainType(ConnectTrainType connectTrainType) {
        return trainTypeMapper.updConnectTrainType(connectTrainType);
    }

    @Override
    public int delConnectTrainType(ConnectTrainType connectTrainType) {
        return trainTypeMapper.delConnectTrainType(connectTrainType);
    }
}
