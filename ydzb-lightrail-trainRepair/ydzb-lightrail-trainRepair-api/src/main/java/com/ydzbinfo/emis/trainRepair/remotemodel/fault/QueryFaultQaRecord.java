package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import lombok.Data;

/**
 * @description: 质检结果查询参数实体
 * @date: 2021/12/13
 * @author: 冯帅
 */
@Data
public class QueryFaultQaRecord {
    //单位
    private String deptCode;
    //车组号
    private String trainSetNo;
    //开始发现时间
    private String findStartDate;
    //结束发现时间
    private String findEndDate;
    //开始处理时间
    private String dealStartDate;
    //结束处理时间
    private String dealEndDate;
    //开始质检时间
    private String qaStartDate;
    //结束质检时间
    private String qaEndDate;
    //质检人编码
    private String qaManCode;
    //质检班组编码
    private String qaBranchCode;
    //质检结果编码
    private String qaOutComeCode;
    //故障id
    private String faultId;
    private String pageNum;
    private String pageSize;
}
