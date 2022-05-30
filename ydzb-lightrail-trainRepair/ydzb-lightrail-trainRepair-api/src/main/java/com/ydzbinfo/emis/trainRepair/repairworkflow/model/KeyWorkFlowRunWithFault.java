package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.remotemodel.fault.RemainFaultNewVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description
 * @createDate 2021/7/1 9:37
 * @modified 张天可 2021年9月7日16:05:21
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("故障转关键作业实体")
public class KeyWorkFlowRunWithFault extends RemainFaultNewVO implements IKeyWorkBase{
    /**
     * 作业内容
     */
    @ApiModelProperty(value = "作业内容")
    private String content;

    /**
     * 功能分类
     */
    @ApiModelProperty(value = "功能分类")
    private String functionClass;

    /**
     * 部件(构型)节点编码
     */
    @ApiModelProperty(value = "部件(构型)节点编码")
    private String batchBomNodeCode;

    /**
     * 类型
     */
    @ApiModelProperty(value = "关键作业类型")
    private String keyWorkType;

    /**
     * 辆序
     */
    @ApiModelProperty(value = "辆序列表")
    private List<String> carNoList;

    /**
     * 位置
     */
    @ApiModelProperty(value = "位置")
    private String position;

    /**
     * 作业条件
     */
    @ApiModelProperty(value = "作业条件")
    private String workEnv;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 流程名称
     */
    @ApiModelProperty(value = "流程名称")
    private String flowName;

    /**
     * 班组code
     */
    @ApiModelProperty(value = "班组code")
    private String teamCode;
    /**
     * 班组名称
     */
    @ApiModelProperty(value = "班组名称")
    private String teamName;

    /**
     * 数据来源
     */
    @ApiModelProperty(value = "数据来源")
    private String dataSource;

    /**
     * 故障id
     */
    @ApiModelProperty(value = "故障id")
    private String faultId;


}
