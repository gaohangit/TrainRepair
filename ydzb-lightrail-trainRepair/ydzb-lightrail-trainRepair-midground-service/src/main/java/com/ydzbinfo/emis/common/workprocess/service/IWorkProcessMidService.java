package com.ydzbinfo.emis.common.workprocess.service;

import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessSummaryEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.base.BaseProcessData;

import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021-07-01
 * @Description:
 */
public interface IWorkProcessMidService {


    /**
     * 查询作业过程操作总表
     */
    List<ProcessSummaryEntity> getSummaryList(ProcessSummaryEntity processSummaryEntity);

    /**
     * 添加作业过程操作总表
     */
    boolean addSummary(List<ProcessSummaryEntity> processSummaryEntityList);

    /**
     * 添加或者更新作业过程操作总表
     */
    boolean addOrUpdateSummary(List<ProcessSummaryEntity> processSummaryEntityList);

    /**
     * 删除作业过程操作总表
     */
    boolean delSummary(ProcessSummaryEntity processSummaryEntity);

    /**
     * 获取作业过程
     */
    List<ProcessPacketEntity> getWorkProcessList(BaseProcessData baseProcessData);

    /**
     * 添加作业过程
     */
    boolean addWorkProcess(List<ProcessPacketEntity> processPacketEntityList);

}
