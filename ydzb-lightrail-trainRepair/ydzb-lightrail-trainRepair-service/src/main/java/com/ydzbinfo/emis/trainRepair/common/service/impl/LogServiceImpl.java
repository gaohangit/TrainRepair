package com.ydzbinfo.emis.trainRepair.common.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.common.dao.LogMapper;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.common.querymodel.Log;
import com.ydzbinfo.emis.trainRepair.common.service.ILogService;
import com.ydzbinfo.emis.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 日志表 服务实现类
 * </p>
 *
 * @author 高晗
 * @since 2021-09-17
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

    @Autowired
    LogMapper logMapper;

    @Override
    public void addLog(Log log) {
        User user = UserUtil.getUserInfo();
        log.setCreateTime(new Date());
        log.setCreateStaffId(user.getStaffId());
        log.setCreateStaffName(user.getName());
        log.setAddressIp(user.getIpAddress());
        logMapper.insert(log);
    }
}
