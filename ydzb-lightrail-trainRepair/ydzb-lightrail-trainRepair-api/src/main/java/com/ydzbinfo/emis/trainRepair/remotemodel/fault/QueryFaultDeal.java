package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class QueryFaultDeal {
    //处理
    private String dealWithId;
    private String dealWithDesc;
    private String dealWithCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date dealDate;
    private String resolveDeptCode;     //处理运用所，（自动获得单位编码）

    private String dealMethodCode;     //处理方法编码
    private String dealMethod;     //处理方法名称
    private String repairMethod;     //详细处理方法
    private String dealWithDept;     //处理部门

    private String repairManCode;     //处理人 编码
    private String repairMan;     //处理人
    private String dealBranchName;     //处理班组名称
    private String dealBranchCode;     //处理班组编码

    private String resolveDeptName;
    private String dealDeptTypeCode;
    private String dealSegmentName;
    private String dealBureaCode;
    private String dealBureaName;

    private QueryFaultQuality faultQuality;

}
