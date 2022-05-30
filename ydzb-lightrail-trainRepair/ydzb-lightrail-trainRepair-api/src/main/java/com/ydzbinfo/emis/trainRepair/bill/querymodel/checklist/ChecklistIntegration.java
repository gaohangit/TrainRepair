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
 * 一体化作业申请单总表
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHECKLISTINTEGRATION")
public class ChecklistIntegration implements Serializable {


    /**
     * 主键
     */
    @TableId("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

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
     * 申请单位CODE
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 申请单位名称
     */
    @TableField("S_DEPTNAME")
    private String deptName;

    /**
     * 申请人ID
     */
    @TableField("S_STUFFID")
    private String stuffId;

    /**
     * 申请人名称
     */
    @TableField("S_STUFFNAME")
    private String stuffName;

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

    /**
     * 模板ID
     */
    @TableField("S_TEMPLATEID")
    private String templateId;

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
     * 调度签字
     */
    @TableField("S_DISPATCHSIGN")
    private String dispatchSign;

    /**
     * 技术签字
     */
    @TableField("S_TECHNIQUESIGN")
    private String techniqueSign;

    /**
     * 所长签字
     */
    @TableField("S_DIRECTORSIGN")
    private String directorSign;

    /**
     * 作业负责人签字
     */
    @TableField("S_FINISHWORKPERSONSIGN")
    private String finishWorkPersonSign;

    /**
     * 调度销记签字
     */
    @TableField("S_FINISHDISPATCHSIGN")
    private String finishDispatchSign;

    /**
     * 单据最后一次回填时间
     */
    @TableField("D_LASTFILLTIME")
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


    public static final String S_APPLYBEGINTIME = "S_APPLYBEGINTIME";
    /**
     * 申请开始时间
     */
    @TableField(S_APPLYBEGINTIME)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date applyBeginTime;


    public static final String S_APPLYENDTIME = "S_APPLYENDTIME";

    /**
     * 申请结束时间
     */
    @TableField(S_APPLYENDTIME)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date applyEndTime;

    /**
     * 作业内容
     */
    @TableField("S_APPLYCONTENT")
    private String applyContent;
}
