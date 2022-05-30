package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;


@Data
public class PlanWorker {


    /**
     * 人员id
     */
    private String workerId;


    /**
     * 人员名称
     */
    private String workerName;

    /**
     * 人员分组id
     */
    private String branchCode;


}
