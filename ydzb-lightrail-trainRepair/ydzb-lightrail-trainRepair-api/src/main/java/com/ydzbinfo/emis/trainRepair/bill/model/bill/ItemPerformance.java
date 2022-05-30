package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.util.Date;

@Data
public class ItemPerformance {
    //主键 GUID
    private String sItemrecordid;
    //日计划时间  YYYY-MM-DD
    private String sDayplandate;
    //日计划班次  0--白班  1--夜班
    private String cDayornight;
    //车组ID
    private String sTrainsetid;
    //车组名称
    private String sTrainsetname;
    //修程
    private String sMaintcyccode;
    //作业包CODE
    private String sSppacketcode;
    //作业包名称
    private String sSppacketname;
    //检修项目CODE
    private String sSprepairitemcode;
    //检修项目名称
    private String sSprepairitemname;
    //辆序
    private String sRepaircar;
    //检修结束时间 班次结束时间
    private Date dRepairendtime;
    //检修开始走行
    private String sRepairbeginaccumile;
    //检修开始时间  班次开始时间
    private Date dRepairbegintime;
    //检修结束走行
    private String sRepairendaccumile;
    //作业人员CODE
    private String sRepairmancode;
    //作业人员名称
    private String sRepairmanname;
    //部门CODE
    private String sRepairdeptcode;
    //部门名称
    private String sRepairdeptname;
    //项目CODE
    private String sRepairunitcode;
    //IP地址
    private String sSource;


}
