package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * PHM任务源部位信息表
 */
@Data
@TableName("PZ_M_TASKAPPLY_POSITION")
public class PzMTaskApplyPosition {

    /**
     * 主键ID
     */
    @TableId("S_ID")
    private String id;

    /**
     * 源编码
     */
    @TableField("S_APPLYID")
    private String applyid;

    /**
     * 车辆号
     */
    @TableField("S_CAR")
    private String car;

    /**
     * 功能位编码
     */
    @TableField("S_POSITIONCODE")
    private String positioncode;

    /**
     * 功能位名称
     */
    @TableField("S_POSITIONNAME")
    private String positionname;

    /**
     * 位置编码
     */
    @TableField("S_LOCATIONCODE")
    private String locationcode;

    /**
     * 位置名称
     */
    @TableField("S_LOCATIONNAME")
    private String locationname;

    /**
     * 实体件ID
     */
    @TableField("S_KEYPARTTYPEID")
    private String keyparttypeid;

    /**
     *
     */
    @TableField("S_MODELID")
    private String modelid;

    /**
     * 实体件名称
     */
    @TableField("S_MODELNAME")
    private String modelname;

    /**
     * 状态（0：未安排，1：已安排，2：已处理）
     */
    @TableField("C_STATUS")
    private String status;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 传输状态（0：未传输，1：已传输）
     */
    @TableField("S_TRANSPORTSTATUS")
    private String transportstatus;

    /**
     * 传输时间
     */
    @TableField("S_TRANSPORTDATE")
    private Date transportdate;

    /**
     * 关联项目编码
     */
    @TableField("S_RELATEITEMCODE")
    private String relateitemcode;

    /**
     * 关联项目名称
     */
    @TableField("S_RELATEITEMNAME")
    private String relateitemname;
}
