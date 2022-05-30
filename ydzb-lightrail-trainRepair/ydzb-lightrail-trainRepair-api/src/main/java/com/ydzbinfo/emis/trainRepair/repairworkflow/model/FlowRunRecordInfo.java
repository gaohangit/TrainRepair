package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;
import org.apache.shiro.dao.DataAccessException;

import java.util.Date;

/**
 * @author 高晗
 * @description 关键作业驳回
 * @createDate 2021/9/1 16:21
 **/
@Data
public class FlowRunRecordInfo {
    /**
     * 流程运行id
     */
    private String flowRunId;

    /**
     * 操作人id
     */
    private String workerId;

    /**
     * 操作人id
     */
    private String workerName;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 备注/驳回理由
     */
    private String remark;

    /**
     * 时间
     */
    private Date recordTime;

    /**
     * 结束类型
     */
    private String type;
}
