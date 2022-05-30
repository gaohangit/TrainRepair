package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class QueryFaultQuality {
    @ApiModelProperty("质检ID")
    private String id; //
    @ApiModelProperty("故障记录ID")
    private String faultId;
    @ApiModelProperty("处理ID")
    private String dealWithId;
    @ApiModelProperty("质检人")
    private String qaMan;  //
    @ApiModelProperty("质检人编码")
    private String qaManCode;  
    @ApiModelProperty("质检情况")
    private String qaResult;
/*    @ApiModelProperty("质检运用所编码")
    private String resolveDeptCode;    */
    @ApiModelProperty("质检时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date qaTime;
    @ApiModelProperty("质检结果")
    private String qaOutComeDesc;
    @ApiModelProperty("质检结果编码")
    private String qaOutComeCode;  
}
