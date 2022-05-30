package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 出所联检单总表
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHECKLISTLIVECHECK")
public class ChecklistLiveCheck implements Serializable {


    /**
     * 主键
     */
    @TableId("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

    /**
     * 模板ID
     */
    @TableField("S_TEMPLATEID")
    private String templateId;


    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

    /**
     * 模板编码
     */
    @TableField("S_TEMPLATENO")
    private String templateNo;

    /**
     * 模板类型编码
     */
    @TableField("S_TEMPLATETYPE")
    private String templateType;

    /**
     * 单据最后一次回填时间
     */
    @TableField("D_LASTFILLTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date lastFillTime;

    /**
     * 单据最后一次回填人编码
     */
    @TableField("S_LASTFILLWORKCODE")
    private String lastFillWorkCode;

    /**
     * 单据最后一次回填人名称
     */
    @TableField("S_LASTFILLWORKNAME")
    private String lastFillWorkName;

    /**
     * 单据最后一次回填设备IP地址
     */
    @TableField("S_LASTIPADDRESS")
    private String lastIpAddress;

    /**
     * 单据最后一次回填设备名称
     */
    @TableField("S_LASTHOSTNAME")
    private String lastHostName;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 交接股道
     */
    @TableField("S_TRACK")
    private String track;

    /**
     * 交接时间
     */
    @TableField("D_CONNECTTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date connectTime;

    /**
     * 创建时间
     */
    @TableField("S_CREATETIME")
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date createTime;

    /**
     * 创建人CODE
     */
    @TableField("S_CREATESTUFFID")
    private String createstuffid;

    /**
     * 创建人名称
     */
    @TableField("S_CREATESTUFFNAME")
    private String createstuffname;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 运用所编码
     */
    @TableField("S_UNITCODE")
    private String unitCode;


}
