package com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration;

import lombok.Data;

import java.util.Date;

@Data
public class InterationQueryCondition {
    //申请人编码
    private String stuffId;
    //申请人名称
    private String stuffName;
    //申请单位编码
    private String deptCode;
    //申请单位名称
    private String deptName;
    //申请作业开始时间
    private Date applyBeginTime;
    //申请作业结束时间
    private Date applyEndTime;
    //状态
    private String state;
    //运用所编码
    private String unitCode;
    //车组id
    private String trainsetId;
    //每页数据大小
    private Integer pageSize;
    //页码
    private Integer pageNum;
}
