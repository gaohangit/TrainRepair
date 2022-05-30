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
@TableName("XZY_M_PROCESSLOCATION")
@Data
public class ProcessLocation implements Serializable {

    /**
     * 主键
     */
    @TableId("S_LOCATIONID")
    private String locationId;

    /**
     * 定位地点
     */
    @TableField("S_LOCATION")
    private String location;

    /**
     * 定位设备
     */
    @TableField("S_TID")
    private String tId;

    /**
     * 定位开始时间
     */
    @TableField("D_STARTTIME")
    private Date starttime;

    /**
     * 定位结束时间
     */
    @TableField("D_ENDTIME")
    private Date endTime;

    /**
     * 作业类型1--1级修2--2级修3--出所联检4--协同
     */
    @TableField("S_WORKTYPE")
    private String workType;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayplanId;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetname;

    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 部件位置
     */
    @TableField("S_PARTPOSITION")
    private String partposition;

    /**
     * 作业人员ID
     */
    @TableField("S_WORKERID")
    private String workerId;

    /**
     * 作业辆序
     */
    @TableField("S_CARNO")
    private String carNo;

    /**
     * 排序ID
     */
    @TableField("S_SORTID")
    private String sortId;

    /**
     * 部件类型名称
     */
    @TableField("S_PARTTYPE")
    private String parttype;

    /**
     * 部件名称
     */
    @TableField("S_PARTNAME")
    private String partname;

    @Override
    public String toString() {
        return "ProcessLocation{" +
            "locationId=" + locationId +
            ", location=" + location +
            ", tId=" + tId +
            ", starttime=" + starttime +
            ", endTime=" + endTime +
            ", workType=" + workType +
            ", dayplanId=" + dayplanId +
            ", createTime=" + createTime +
            ", trainsetId=" + trainsetId +
            ", trainsetname=" + trainsetname +
            ", unitCode=" + unitCode +
            ", itemCode=" + itemCode +
            ", partposition=" + partposition +
            ", workerId=" + workerId +
            ", carNo=" + carNo +
            ", sortId=" + sortId +
            ", parttype=" + parttype +
            ", partname=" + partname +
        "}";
    }
}
