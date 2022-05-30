package com.ydzbinfo.emis.trainRepair.workprocess.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
@TableName("XZY_M_PROCESSCARPART")
@Data
public class ProcessCarPart implements Serializable {


    /**
     * 主键
     */
    @TableId("S_PROCESSID")
    private String processId;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目NAME
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 检修类型（项目类型）
     */
    @TableField("S_REPAIRTYPE")
    private String repairType;

    /**
     * 项目作业类型   1--部件  0--辆序
     */
    @TableField("S_ARRAGETYPE")
    private String arrageType;

    /**
     * 数据来源
     */
    @TableField("S_DATASOURCE")
    private String dataSource;

    /**
     * 部件类型名称
     */
    @TableField("S_PARTTYPE")
    private String partType;

    /**
     * 部件位置
     */
    @TableField("S_PARTPOSITION")
    private String partPosition;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 检修方式0--人工检查1--机器人2--机检
     */
    @TableField("S_REPAIRMODE")
    private String repairMode;

    /**
     * 部件名称
     */
    @TableField("S_PARTNAME")
    private String partName;

    /**
     * 记录时间
     */
    @TableField("S_RECORDTIME")
    private Date recordTime;

    /**
     * 记录人姓名
     */
    @TableField("S_RECORDERNAME")
    private String recorderName;

    /**
     * 记录人编码
     */
    @TableField("S_RECORDERCODE")
    private String recorderCode;

    /**
     * 作业过程部门表主键
     */
    @TableField("S_PROCESSDEPTID")
    private String processDeptId;

    /**
     * 作业过程包记录主键
     */
    @TableField("S_PROCESSPACKETID")
    private String processPacketId;

    /**
     * 辆序
     */
    @TableField("S_CARNO")
    private String carNo;

    /**
     * 车型
     */
    @TableField("S_TRAINSETTYPE")
    private String trainsetType;

    /**
     * 是否可用  1--可用 0--不可用
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 删除人编码
     */
    @TableField("S_DELUSERCODE")
    private String delUserCode;

    /**
     * 删除人姓名
     */
    @TableField("S_DELUSERNAME")
    private String delUserName;

    /**
     * 删除时间
     */
    @TableField("D_DELTIME")
    private Date delTime;

    /**
     * 项目时间唯一标识
     */
    @TableField("S_ITEMPUBLCISHED")
    private String itemPublcished;

    @Override
    public String toString() {
        return "ProcessCarPart{" +
            "processId=" + processId +
            ", dayPlanId=" + dayPlanId +
            ", trainsetName=" + trainsetName +
            ", trainsetId=" + trainsetId +
            ", unitCode=" + unitCode +
            ", unitName=" + unitName +
            ", itemCode=" + itemCode +
            ", itemName=" + itemName +
            ", repairType=" + repairType +
            ", arrageType=" + arrageType +
            ", dataSource=" + dataSource +
            ", partType=" + partType +
            ", partPosition=" + partPosition +
            ", remark=" + remark +
            ", repairMode=" + repairMode +
            ", partName=" + partName +
            ", recordTime=" + recordTime +
            ", recorderName=" + recorderName +
            ", recorderCode=" + recorderCode +
            ", processDeptId=" + processDeptId +
            ", processPacketId=" + processPacketId +
            ", carNo=" + carNo +
            ", trainsetType=" + trainsetType +
            ", flag=" + flag +
            ", delUserCode=" + delUserCode +
            ", delUserName=" + delUserName +
            ", delTime=" + delTime +
            ", itemPublcished=" + itemPublcished +
        "}";
    }
}
