package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.MonitorNoteContent;

import java.util.List;

public interface MonitorNoteContentService {
    List<MonitorNoteContent> getMonitornotecontentsById();
    MonitorNoteContent getMonitornotecontentById(MonitorNoteContent monitornotecontent);
    int addMonitornotecontent(MonitorNoteContent monitornotecontent);
    int updMonitornotecontent(MonitorNoteContent monitornotecontent);
    int delMonitornotecontent(MonitorNoteContent Monitornotecontent);
}
