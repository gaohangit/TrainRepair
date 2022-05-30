package com.ydzbinfo.emis.trainRepair.remotemodel.fault;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @description: 质检记录实体
 * @date: 2021/12/13
 * @author: 冯帅
 */
@Data
public class FaultQaRecord {
    //车号
    private String carNo;
    //处理时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date dealDate;
    //处理单位名称
    private String dealDeptName;
    //处理方法
    private String dealMethod;
    //故障处理结果
    private String dealWithDesc;
    //处理id
    private String dealWithId;
    //故障现象描述
    private String faultDescription;
    //故障id
    private String faultId;
    //故障来源名称
    private String faultSourceName;
    //质检附件
    private String fileName;
    //位数
    private Integer locatetionNum;
    //故障节点名称
    private String nodeName;
    //质检班组名称
    private String qaBranchName;
    //质检备注
    private String qaComment;
    //质检人
    private String qaMan;
    //质检结果
    private String qaOutComeDesc;
    //质检时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date qaTime;
    //处理人
    private String repairMan;
    //详细处理方法
    private String repairMethod;
    //处理故障单位代码
    private String resolveDeptCode;
    //序列号
    private String serialNum;
    //系统功能分类名称
    private String sysFunctionName;
    //车组号
    private String trainSetNo;
}
