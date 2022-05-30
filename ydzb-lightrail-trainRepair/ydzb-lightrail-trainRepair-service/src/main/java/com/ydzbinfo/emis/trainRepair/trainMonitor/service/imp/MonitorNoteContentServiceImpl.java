package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.MonitorNoteContentMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.MonitorNoteContent;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.MonitorNoteContentService;
import com.ydzbinfo.emis.utils.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/1 10:05
 **/
@Service
@Transactional
public class MonitorNoteContentServiceImpl implements MonitorNoteContentService {
    @Resource
    MonitorNoteContentMapper monitornotecontentMapper;

    @Override
    public List<MonitorNoteContent> getMonitornotecontentsById() {
        return monitornotecontentMapper.getMonitornotecontentsById();
    }

    @Override
    public MonitorNoteContent getMonitornotecontentById(MonitorNoteContent monitornotecontent) {
        return monitornotecontentMapper.getMonitornotecontentById(monitornotecontent);
    }

    @Override
    public int addMonitornotecontent(MonitorNoteContent monitornotecontent) {
        monitornotecontent.setCreateTime(new Date());
        monitornotecontent.setCreateUserCode(UserUtil.getUserInfo().getStaffId());
        monitornotecontent.setCreateUserName(UserUtil.getUserInfo().getName());
        return monitornotecontentMapper.addMonitornotecontent(monitornotecontent);
    }

    @Override
    public int updMonitornotecontent(MonitorNoteContent monitornotecontent) {
        return monitornotecontentMapper.updMonitornotecontent(monitornotecontent);
    }

    @Override
    public int delMonitornotecontent(MonitorNoteContent Monitornotecontent) {
        return monitornotecontentMapper.delMonitornotecontent(Monitornotecontent);
    }
}
