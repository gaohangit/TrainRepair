package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.MonitorNoteContent;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-02-26
 */
public interface MonitorNoteContentMapper extends BaseMapper<MonitorNoteContent> {
    List<MonitorNoteContent> getMonitornotecontentsById();
    MonitorNoteContent getMonitornotecontentById(MonitorNoteContent monitornotecontent);
    int addMonitornotecontent(MonitorNoteContent monitornotecontent);
    int updMonitornotecontent(MonitorNoteContent monitornotecontent);
    int delMonitornotecontent(MonitorNoteContent Monitornotecontent);

}
