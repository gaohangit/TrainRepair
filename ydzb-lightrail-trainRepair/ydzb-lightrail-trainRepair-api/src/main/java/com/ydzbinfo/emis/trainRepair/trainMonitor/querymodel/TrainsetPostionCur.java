package com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gaohan
 * @since 2021-02-26
 */
@TableName("XZY_M_TRAINSETPOSTION_CUR")
@Data
public class TrainsetPostionCur implements Serializable {


    /**
     * 主键
     */
    @TableId("S_TRAINSETPOSTIONID")
    private String trainsetPostionId;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

    /**
     * 股道CODE
     */
    @TableField("S_TRACKCODE")
    private String trackCode;

    /**
     * 股道名称
     */
    @TableField("S_TRACKNAME")
    private String trackName;

    /**
     * 是否长编  1--是  0--否
     */
    @TableField("C_ISLONG")
    private String isLong;

    /**
     * 是否重联  1--是  0--否
     */
    @TableField("C_ISCONNECT")
    private String isConnect;

    /**
     * 进入时间
     */
    @TableField("D_INTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date inTime;

    /**
     * D_OUTTIME
     */
    @TableField("D_OUTTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date outTime;

    /**
     * 数据来源
     */
    @TableField("S_DATASOURCE")
    private String dataSource;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 车头方向   01--车头在左侧   00--车头在右侧
     */
    @TableField("S_HEADDIRECTION")
    private String headDirection;

    /**
     *车头所在列位CODE
     */
    @TableField("S_HEADDIRECTIONPLACODE")
    private String headDirectionPlaCode;

    /**
     * 车头所在列位
     */
    @TableField("S_HEADDIRECTIONPLA")
    private String headDirectionPla;

    /**
     * 车尾方向    01--车尾在左侧   00--车尾在右侧
     */
    @TableField("S_TAILDIRECTION")
    private String tailDirection;

    /**
     * 车尾所在列位 CODE
     */
    @TableField("S_TAILDIRECTIONPLACODE")
    private String tailDirectionPlaCode;

    /**
     * 车尾所在列位
     */
    @TableField("S_TAILDIRECTIONPLA")
    private String tailDirectionPla;

    /**
     * 记录时间
     */
    @TableField("D_RECORDTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /**
     * 操作人CODE
     */
    @TableField("S_RECORDUSERCODE")
    private String recordUserCode;

    /**
     * 操作人名称
     */
    @TableField("S_RECORDUSERNAME")
    private String recordUserName;

}
