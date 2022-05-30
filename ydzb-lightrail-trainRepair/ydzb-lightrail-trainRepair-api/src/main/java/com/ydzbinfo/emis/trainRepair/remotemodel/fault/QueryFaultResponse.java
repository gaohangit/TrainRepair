package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 故障查询响应实体
 */
@Data
public class QueryFaultResponse {
    /**
     * 故障id
     */
    private String faultId;
    private String trainsetId;
    private String carNo;
    private String trainNo;
    private String faultUnifyCode;
    private String faultName;
    private String faultDescription;
    private String faultGrade;
    private String quality;
    @ApiModelProperty("功能分类id")
    private String sysFunctionId;
    @ApiModelProperty("功能分类名称")
    private String sysFunctionName;
    @ApiModelProperty("结构分类id")
    private String rcdSysTypeId;
    @ApiModelProperty("结构分类名称")
    private String rcdSysTypeName;

    private QueryFaultFind faultFind;
    private QueryFaultDeal faultDeal;

}
