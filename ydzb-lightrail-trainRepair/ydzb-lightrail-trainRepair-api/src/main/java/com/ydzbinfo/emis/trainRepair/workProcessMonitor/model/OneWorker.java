package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/6/2 15:23
 * @Description: 一级修作业人员实体
 */
@Data
public class OneWorker implements Serializable {

    /**
     * 作业人员ID
     */
    private String workerId;

    /**
     * 作业人员名称
     */
    private String workerName;

    /**
     * 作业项目集合
     */
    private List<OneItem> oneItemList;

}
