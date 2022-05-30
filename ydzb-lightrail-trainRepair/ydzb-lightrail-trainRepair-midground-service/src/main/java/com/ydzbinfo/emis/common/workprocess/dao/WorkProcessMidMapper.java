package com.ydzbinfo.emis.common.workprocess.dao;

import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.base.BaseProcessData;

import java.util.List;

/**
 * 冯帅
 */
public interface WorkProcessMidMapper {

    /**
     * 获取作业过程
     */
    List<ProcessPacketEntity> getWorkProcessList(BaseProcessData baseProcessData);
}
