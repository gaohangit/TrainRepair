package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description 临修作业大屏展示
 * @createDate 2021/7/5 9:54
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TemporaryFlowRunWithTrainset extends TrainsetPostionCur {
    /**
     * 计划作业包信息
     */
    private List<ZtTaskPacketEntity> taskPacketEntities;
    /**
     * 节点信息
     */
    List<NodeInfoForSimpleShow> nodeInfoForSimpleShows;
}
