package com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class IntegrationSummary {
    //单据主键
    private String checklistSummaryId;
    //申请人姓名
    private String stuffName;
    //申请部门
    private String deptName;
    //车组名称
    private String trainsetName;
    //申请作业开始时间
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date applyBeginTime;
    //申请作业结束时间
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date applyEndTime;
    //调度签字
    private String dispatchSign;
    //技术签字
    private String techniqueSign;
    //所长签字
    private String directorSign;
    //作业负责人签字
    private String finishWorkPersonSign;
    //调度销记签字
    private String finishDispatchSign;
    //模板ID
    private String templateId;
    //模板NO
    private String templateNo;
    //模板类型ID
    private String templateType;
    //模板类型名称
    private String templateTypeName;
    //运用所名称
    private String unitName;
    //作业内容
    private String applyContent;
}
