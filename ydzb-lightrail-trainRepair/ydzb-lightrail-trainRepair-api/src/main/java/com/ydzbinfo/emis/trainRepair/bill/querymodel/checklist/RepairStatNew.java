package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("JC_M_REPAIR_STAT_NEW")
public class RepairStatNew implements Serializable {


    /**
     * 动车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 检修日期，yyyymmdd
     */
    @TableId("S_DATE")
    private String date;

    /**
     * 检修类型编码，1：一级修，2：二级修
     */
    @TableField("S_REPAIRCODE")
    private String repairCode;

    /**
     * 动车组检修时的走行公里
     */
    @TableField("S_ACCMILE")
    private String accmile;

    /**
     * 检修单位编码
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 同步标志
     */
    @TableField("C_SYNFLAG")
    private String synFlag;

    /**
     * 同步时间
     */
    @TableField("D_SYNTIME")
    private Date synTime;

    /**
     * 数据插入时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;


}
