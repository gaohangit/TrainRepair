package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description 关键作业大屏展示
 * @createDate 2021/7/5 9:46
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyWorkFlowRunWithTrainset extends TrainsetPostionCur {
    /**
     * 关键作业数据结构
     */
    List<KeyWorkFlowRunInfo> keyWorkFlowRunInfos;

}
