package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/25 09:25
 * @Description: 一级修作业过程监控实体（到列位）
 */
@Data
public class QueryOneProcessMonitorPla extends QueryProcessMonitorPla implements Serializable {

    /**
     * 作业人员集合
     */
    List<OneWorker> oneWorkerList;

    /**
     * 车组是否配置了作业标准 0-未配置 1-已配置
     */
    private String isCriterion;
}
