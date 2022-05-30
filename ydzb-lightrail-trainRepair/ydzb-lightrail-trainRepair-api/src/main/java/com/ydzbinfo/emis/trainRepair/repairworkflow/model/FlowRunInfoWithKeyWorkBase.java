package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description
 * @createDate 2021/8/17 9:34
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FlowRunInfoWithKeyWorkBase extends FlowRunInfoForSimpleShow implements IKeyWorkBase{
    /**
     * 作业内容
     */
    private String content;

    /**
     * 功能分类
     */
    private String functionClass;

    /**
     * 部件(构型)节点编码
     */
    private String batchBomNodeCode;

    /**
     * 类型
     */
    private String keyWorkType;

    /**
     * 辆序
     */
    private List<String> carNoList;

    /**
     * 位置
     */
    private String position;

    /**
     * 作业条件
     */
    private String workEnv;

    /**
     * 备注
     */
    private String remark;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 班组code
     */
    private String teamCode;
    /**
     * 班组名称
     */
    private String teamName;


    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 故障id
     */
    private String faultId;
}
