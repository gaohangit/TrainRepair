package com.ydzbinfo.emis.trainRepair.mobile.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 * Author: 吴跃常
 * Create Date Time: 2021/6/18 17:52
 * Update Date Time: 2021/6/18 17:52
 *
 * @see
 */
@Data
public class FaultSearch {
    @ApiModelProperty("故障等级")
    private String faultGrade;
    @ApiModelProperty("发现起始时间")
    private String startTime;
    @ApiModelProperty("发现终止时间")
    private String endTime;
    @ApiModelProperty("车组id")
    private String  trainsetId;
    @ApiModelProperty("车号")
    private String  carNo;
    @ApiModelProperty("故障来源编码")
    private String faultSourceCode;
    @ApiModelProperty("功能分类节点ID")
    private String subFunctionClassId;
    @ApiModelProperty("故障部件id")
    private String faultPartId;
    @ApiModelProperty("位数")
    private String locatetionNum;
    @ApiModelProperty("故障配件序列号")
    private String  serialNum;
    @ApiModelProperty("故障配件类型Id")
    private String partTypeId;
    @ApiModelProperty("发现人")
    private String  findFaultMan;
    @ApiModelProperty("发现单位")
    private String faultFindUnitCode;
    @ApiModelProperty("单位")
    private String orgCode;
    @ApiModelProperty("发现班组编码")
    private String faultFindBranchCode;
    @ApiModelProperty("处理结果编码")
    private String dealWithDescCode;
    @ApiModelProperty("处理方法编码")
    private String dealMethodCode;
    @ApiModelProperty("处理人 编码")
    private String repairManCode;
    @ApiModelProperty("处理班组编码")
    private String dealBranchCode;
    @ApiModelProperty("处理开始时间")
    private String dealStartTime;
    @ApiModelProperty("处理结束时间")
    private String dealEndTime;
    @ApiModelProperty("故障树id 对应故障表象，模式主键id")
    private String faultTreeId;
    private String dealDeptName;
    @ApiModelProperty("页码")
    private Integer pageNum;
    @ApiModelProperty("每页大小")
    private Integer pageSize;
}
