package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/6/1 18:20
 **/
@Data
public class FlowCondition {
    /**
     * 条件id
     */
    private String conditionId;
    /**
     * 车组条件
     */
    private TrainConditionValue trainConditionValue;
    /**
     * 关键字条件
     */
    private List<String> keyWords;

    /**
     * 检修类型
     */
    private String repairType;

    /**
     * 对应默认流程的创建时间
     */
    private Date defaultFlowCreateTime;

    /**
     * 当前条件默认流程id
     **/
    private String defaultFlowId;

}
