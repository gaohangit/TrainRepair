package com.ydzbinfo.emis.trainRepair.workprocess.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/7/2 09:47
 * @Description: 作业过程人员实体—中台
 */
@Data
public class ProcessPersonEntity implements Serializable {
    /**
     * 主键
     */
    private String processPersonId;

    /**
     * 辆序ID
     */
    private String processId;

    /**
     * 作业人员
     */
    private String workerId;

    /**
     * 作业人员类型（1--检修，2--质检，3-无修程任务完成，4-无修程任务确认）
     */
    private String workerType;

    /**
     * 作业人员姓名
     */
    private String workerName;

    /**
     * 确认辆序数量
     */
    private String carNoCount;

    /**
     * 作业过程时间记录集合
     */
    private List<ProcessTimeRecordEntity> processTimeRecordEntityList;

}
