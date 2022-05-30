package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import lombok.Data;

/**
 * @description: 故障来源实体
 * @date: 2021/12/9
 * @author: 冯帅
 */
@Data
public class FaultSource {

    private String sFaultSourceCode;

    private String sFaultSourceName;

    private String sFaultSourceDesc;

    private String sFaultSourceAbbr;

    private String iSortId;
}
