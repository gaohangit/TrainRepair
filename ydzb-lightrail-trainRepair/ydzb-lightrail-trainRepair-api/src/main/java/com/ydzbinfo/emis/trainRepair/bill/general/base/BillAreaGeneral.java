package com.ydzbinfo.emis.trainRepair.bill.general.base;


import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

@Data
public class BillAreaGeneral implements IBillAreaGeneral {

    /**
     * 左上角坐标
     */
    @TableField("I_LEFTUP")
    private String leftUp;

    /**
     * 左下角坐标 X,Y
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
     * 类型   1--数值区域 2--隔离区域 3--扩展区域 4--属性编辑区域
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 区域编号
     */
    @TableField("I_NUMBER")
    private Integer number;
}
