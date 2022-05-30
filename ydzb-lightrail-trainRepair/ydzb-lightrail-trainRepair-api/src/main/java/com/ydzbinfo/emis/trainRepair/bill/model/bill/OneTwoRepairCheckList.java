package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import lombok.Data;

@Data
public class OneTwoRepairCheckList {
    //主键
    private String checklistSummaryId;
    //日计划ID
    private String dayPlanId;
    //车组ID
    private String trainsetId;
    //车组名称
    private String trainsetName;
    //部门CODE
    private String deptCode;
    //部门名称
    private String deptName;
    //项目CODE
    private String itemCode;
    //项目名称
    private String itemName;
    //项目类型
    private String itemType;
    //修程
    private String mainCyc;
    //单位CODE
    private String unitCode;
    //单位名称
    private String unitName;
    //模板ID
    private String templateId;
    //模板NO
    private String templateNo;
    //模板类型CODE
    private String templateType;
    //模板类型名称
    private String templateTypeName;
    //模板配置情况
    private String templateExisit;
    //编组类型
    private String marshallingType;
    //回填状态CODE
    private String fillStateCode;
    //回填状态名称
    private String fillStateName;
    //回填人员CODE
    private String fillWorkerCodes;
    //回填人员名称
    private String fillWorkers;
    //检修工长名称
    private String foremanNames;
    //质检名称
    private String qualityNames;
    //派工人员名称
    private String taskAllotWorkers;
}

