package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class QueryFaultFind {
    //发现信息
    private String findId;
    private String faultFindUnitCode;
    private String findUnitName;
    private String findFaultMan;
    private String findFaultManCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date findFaultTime;
    private String faultSourceCode;
    private String faultSourceName;
    private String faultFindBranchName;
    private String faultFindBranchCode;
    private Double accuMile;

    private String findDeptTypeCode;
    private String findSegmentName;
    private String findBureaCode;
    private String findBureaName;
}
