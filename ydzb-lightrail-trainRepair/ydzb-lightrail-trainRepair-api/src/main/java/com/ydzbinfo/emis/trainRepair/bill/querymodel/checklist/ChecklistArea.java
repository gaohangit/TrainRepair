package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillAreaGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.BillAreaGeneral;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHECKLISTAREA")
public class ChecklistArea {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * XZY_M_CHECKLISTSUMMARY表主键
     */
    @TableField("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;


    /**
     * 左上角坐标
     */
    @TableField("I_LEFTUP")
    private String leftUp;

    /**
     * 左下角坐标
     */
    @TableField("I_LEFTDOWN")
    private String leftDown;

    /**
     * 右上角坐标
     */
    @TableField("I_RIGHTUP")
    private String rightUp;

    /**
     * 右下角坐标
     */
    @TableField("I_RIGHTDOWN")
    private String rightDown;

    /**
     * 类型   1 数据区域, 2 隔离区域
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 区域编号
     */
    @TableField("I_NUMBER")
    private Integer number;

}
