package com.ydzbinfo.emis.trainRepair.workprocess.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
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
@TableName("XZY_M_RFIDCARDSUMMARY")
@Data
public class RfidCardSummary implements Serializable,Cloneable {


    /**
     * 主键
     */
    @TableId("S_RECORDID")
    private String recordId;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 车组号ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 股道CODE
     */
    @TableField("S_TRACKCODE")
    private String trackCode;

    /**
     * 作业位置名称CODE（列位CODE）
     */
    @TableField("S_REPAIRPLACECODE")
    private String repairPlaceCode;

    /**
     * 作业位置名称NAME（列位名称）
     */
    @TableField("S_REPAIRPLACENAME")
    private String repairPlaceName;

    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目名称
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 工人ID
     */
    @TableField("S_STUFFID")
    private String stuffId;

    /**
     * 工人名称
     */
    @TableField("S_STUFFNAME")
    private String stuffName;

    /**
     * 检修时间
     */
    @TableField("D_REPAIRTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date repairTime;

    /**
     * 记录时间
     */
    @TableField("D_RECORDTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /**
     * 时间标识  1--开始时间   2--结束时间
     */
    @TableField("S_TIMETAG")
    private String timeTag;

    /**
     * 部门CODE
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 部门名称
     */
    @TableField("S_DEPTNAME")
    private String deptName;

    /**
     * 检修类型（项目类型）
     */
    @TableField("S_REPAIRTYPE")
    private String repairType;

    /**
     * 创建人
     */
    @TableField("S_CREATEUSERCODE")
    private String createUserCode;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String createUserName;

    /**
     * 是否可用  1--可用 0--不可用
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 删除时间
     */
    @TableField("D_DELTIME")
    private Date delTime;

    /**
     * 删除人
     */
    @TableField("S_DELUSERCODE")
    private String delUserCode;

    /**
     * 删除人名称
     */
    @TableField("S_DELUSERNAME")
    private String delUserName;

    /**
     * 股道名称
     */
    @TableField("S_TRACKNAME")
    private String trackName;

    /**
     * 作业标准主键，表XZY_C_WORKCRITERTION
     */
    @TableField("S_CRITERTIONID")
    private String critertionId;


    /**
     * 运用所CODE，表XZY_C_WORKCRITERTION
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 车型，表XZY_C_WORKCRITERTION
     */
    @TableField("S_TRAINSETTYPE")
    private String trainsetType;

    /**
     * 数据来源(1,手持，2，pc)，表XZY_C_WORKCRITERTION
     */
    @TableField("S_DATASOURCE")
    private String dataSource;

    @Override
    public String toString() {
        return "RfidCardSummary{" +
            "recordId=" + recordId +
            ", dayPlanId=" + dayPlanId +
            ", trainsetId=" + trainsetId +
            ", trackCode=" + trackCode +
            ", repairPlaceCode=" + repairPlaceCode +
            ", repairPlaceName=" + repairPlaceName +
            ", itemCode=" + itemCode +
            ", itemName=" + itemName +
            ", stuffId=" + stuffId +
            ", stuffName=" + stuffName +
            ", repairTime=" + repairTime +
            ", recordTime=" + recordTime +
            ", timeTag=" + timeTag +
            ", deptCode=" + deptCode +
            ", deptName=" + deptName +
            ", repairType=" + repairType +
            ", createUserCode=" + createUserCode +
            ", createUserName=" + createUserName +
            ", flag=" + flag +
            ", delTime=" + delTime +
            ", delUserCode=" + delUserCode +
            ", delUserName=" + delUserName +
            ", trackName=" + trackName +
            ", critertionId=" + critertionId +
        "}";
    }

    @Override
    public RfidCardSummary clone(){
        RfidCardSummary rfidCardSummary = null;
        try {
            rfidCardSummary=(RfidCardSummary)super.clone();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return rfidCardSummary;
    }
}
