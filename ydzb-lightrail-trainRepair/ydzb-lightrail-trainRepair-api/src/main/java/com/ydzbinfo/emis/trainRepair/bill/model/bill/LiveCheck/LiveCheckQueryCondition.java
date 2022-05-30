package com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck;

import lombok.Data;

import java.util.Date;

@Data
public class LiveCheckQueryCondition {
    //车组号ID
    private String trainsetId;
    //交接股道
    private String track;
    //交接时间
    private String connectTime;
    //下发开始时间
    private Date createBeginTime;
    //下发结束时间
    private Date createEndTime;
    //运用所编码
    private String unitCode;
    //每页数据大小
    private Integer pageSize;
    //页码
    private Integer pageNum;
}
