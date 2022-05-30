package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 韩旭
 * @since 2021-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHECKLISTFAULTRELATION")
public class ChecklistFaultRelation implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 记录单总表ID
     */
    @TableField("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

    /**
     * 记录单明细表ID
     */
    @TableField("S_DETAILID")
    private String detailId;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 故障ID
     */
    @TableField("S_FAULTID")
    private String faultId;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("S_STUFFID")
    private String stuffId;


}
