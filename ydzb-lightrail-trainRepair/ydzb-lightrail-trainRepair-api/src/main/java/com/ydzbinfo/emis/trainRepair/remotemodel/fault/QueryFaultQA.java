package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import lombok.Data;

/**
 * @description: 故障质检查询参数实体
 * @date: 2021/10/25
 * @author: 冯帅
 */
@Data
public class QueryFaultQA {

    String carNo;

    String dealDate;

    String dealWithDesc;

    String dealWithId;

    String dfindFaultTime;

    String faultDescription;

    String faultId;

    String faultSourceName;

    String locatetionNum;

    String nodeName;

    String repairMan;

    String repairMethod;

    String resolveDeptCode;

    String serialNum;

    String sysFunctionName;

    String trainSetName;

    String trainSetNo;
}
