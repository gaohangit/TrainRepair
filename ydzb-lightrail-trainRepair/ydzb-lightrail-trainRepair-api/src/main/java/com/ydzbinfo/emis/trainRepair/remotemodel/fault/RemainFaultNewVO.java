package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.utils.entity.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("故障回填列表实体")
public class RemainFaultNewVO {
    @ApiModelProperty("故障id")
    private String  faultId;

    @ApiModelProperty("主表中的故障模式名称")
    private String faultName;

    @ApiModelProperty("车组id")
    private String trainsetId;
    @ApiModelProperty("车组名称")
    private String trainsetName;
    @ApiModelProperty("车号")
    private String carNo;
    @ApiModelProperty("车次")
    private String trainNo;
    @ApiModelProperty("功能分类")
    private String sysFunctionName;
    @ApiModelProperty("功能分类code")
    private String sysFunctionCode;

    @ApiModelProperty("配件id")
    private String faultPartId;

    private String nodeName;
    private String nodeCode;

    private String subFunctionClassId;

    @ApiModelProperty("配件名称")
    private String partName;

    @ApiModelProperty("配件类型名称")
    private String faultPosName;

    @ApiModelProperty("序列号")
    private String serialNum;
    @ApiModelProperty("故障等级")
    private String faultGrade;
    @ApiModelProperty("故障现象描述")
    private String faultDescription;
    @ApiModelProperty("发现人")
    private String findFaultMan;
    @ApiModelProperty("发现故障时间")
    private Date findFaultTime;
    @ApiModelProperty("最晚发现故障时间")
    private Date lastFindFaultTime;
    @ApiModelProperty("发现班组")
    private String faultFindBranchName;
    @ApiModelProperty("故障处理结果（未处理，待处理，已处理）")
    private String dealWithDesc;
    @ApiModelProperty("处理日期")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date dealDate;
    @ApiModelProperty("末次日期")
    private Date lastDealDate;
    @ApiModelProperty("处理方法")
    private String dealMethod;
    /**
     * 故障模式处显示 故障表象名称
     * 不显示故障模式了
     */
    @ApiModelProperty("故障表象名称")
    private String faultAppName;

    @ApiModelProperty("发现单位")
    private String findDeptName;

    @ApiModelProperty("处理班组")
    private String dealBranchName;

    @ApiModelProperty("处理人")
    private String repairMan;

    @ApiModelProperty("故障来源编码")
    private String faultSourceCode;

    @ApiModelProperty("故障来源名称")
    private String faultSourceName;

    private String locatetionNum;

    private String dealDeptName;

    @ApiModelProperty("部件id")
    private String faultPosid;

}
