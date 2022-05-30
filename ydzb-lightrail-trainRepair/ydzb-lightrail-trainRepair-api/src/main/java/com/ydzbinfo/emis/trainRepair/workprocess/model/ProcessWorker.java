package com.ydzbinfo.emis.trainRepair.workprocess.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/5/19 11:10
 * @Description: 作业人员实体
 */
@Data
public class ProcessWorker implements Serializable {

    /**
     * 作业人员ID
     */
    private String workerId;

    /**
     * 作业人员名称
     */
    private String workerName;
}
