package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

import java.util.List;

@Data
public class TaskAllotPersonQueryParamModel {

    private List<String> processIds;

    private String dayPlanId;

    private String unitCode;

    private String deptCode;
}
