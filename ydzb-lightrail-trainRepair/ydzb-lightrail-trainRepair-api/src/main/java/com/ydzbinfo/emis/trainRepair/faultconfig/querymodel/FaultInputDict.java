package com.ydzbinfo.emis.trainRepair.faultconfig.querymodel;

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
 * @since 2021-06-07
 */
@TableName("XZY_C_FAULTINPUT_DICT")
@Data
public class FaultInputDict implements Serializable {


    /**
     * 主键
     */
    @TableId("S_FAULTINPUTID")
    private String faultInputId;

    /**
     * 类型名称
     */
    @TableField("S_KEY")
    private String key;

    /**
     * 功能分类编码
     */
    @TableField("S_FUNCTIONTYPECODE")
    private String functionTypeCode;

    /**
     * 功能分类名称
     */
    @TableField("S_FUNCTIONTYPENAME")
    private String functionTypeName;

    /**
     * 故障部件编码
     */
    @TableField("S_PARTCODE")
    private String partCode;

    /**
     * 故障部件名称
     */
    @TableField("S_PARTNAME")
    private String partName;

    /**
     * 故障部件位置ID
     */
    @TableField("S_PARTPOSTION")
    private String partPostion;

    /**
     * 故障部件类型
     */
    @TableField("S_PARTTYPE")
    private String partType;

    /**
     * 故障等级编码
     */
    @TableField("S_FAULTLEVELCDOE")
    private String faultLevelCdoe;

    /**
     * 故障等级名称
     */
    @TableField("S_FAULTLEVELNAME")
    private String faultLevelName;

    /**
     * 故障模式编码
     */
    @TableField("S_FAULTMODECODE")
    private String faultModeCode;

    /**
     * 故障模式名称
     */
    @TableField("S_FAULTMODENAME")
    private String faultModeName;

    //排序字段
    public static final String S_RECORDTIME = "S_RECORDTIME";

    /**
     * 记录时间
     */
    @TableField(S_RECORDTIME)
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
}
