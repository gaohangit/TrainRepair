package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHECKLISTSUMMARY")
public class ChecklistSummary implements Serializable {


    /**
     * 主键
     */
    @TableId("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 检修班组CODE
     */
    @TableField("S_DEPTCODE")
    private String deptCode;

    /**
     * 检修班组名称
     */
    @TableField("S_DEPTNAME")
    private String deptName;

    /**
     * 项目编码
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目名称
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 修程
     */
    @TableField("S_MAINCYC")
    private String mainCyc;

    /**
     * 项目类型
     */
    @TableField("S_ITEMTYPE")
    private String itemType;

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
     * 模板版本
     */
    @TableField("S_VERSION")
    private String version;

    /**
     * 编组形式  1--前   2--后   3--全部
     */
    @TableField("I_MARSHALLINGTYPE")
    private Integer marshallingType;

    /**
     * 单据回填状态(0:未完全回填，1:完全回填)
     */
    @TableField("C_BACKFILLTYPE")
    private String backFillType;

    /**
     * 单据最后一次回填时间
     */
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
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

    /**
     * 单据所有回填人名称，用逗号区分
     */
    @TableField("S_FILLWORKNAME")
    private String fillWorkName;

    /**
     * 备注信息
     */
    @TableField("S_REMARKS")
    private String remarks;

    /**
     * 作业包CODE
     */
    @TableField("S_PACKETCODE")
    private String packetCode;

    /**
     * 作业包NAME
     */
    @TableField("S_PACKETNAME")
    private String packetName;

    /**
     * 作业包类型
     */
    @TableField("S_PACKETTYPE")
    private String packetType;

    /**
     * 作业包任务ID
     */
    @TableField("S_TASKID")
    private String taskId;

    /**
     * 作业项目任务ID
     */
    @TableField("S_ITEMTASKID")
    private String itemTaskId;

    /**
     * 部件信息
     */
    @TableField("S_PART")
    private String part;

    /**
     * 辆序信息
     */
    @TableField("S_CAR")
    private String car;

    /**
     * 走行公里
     */
    @TableField("S_ACCUMILE")
    private String accuMile;

    /**
     * 单据所有回填人编码，用逗号区分
     */
    @TableField("S_FILLWORKCODE")
    private String fillWorkCode;

    /**
     * 单据所有工长编码，用逗号区分
     */
    @TableField("S_FOREMANSIGNCODE")
    private String foremanSignCode;

    /**
     * 单据所有工长名称，用逗号区分
     */
    @TableField("S_FOREMANSIGNNAME")
    private String foremanSignName;

    /**
     * 单据所有质检编码，用逗号区分
     */
    @TableField("S_QUALITYSIGNCODE")
    private String qualitySignCode;

    /**
     * 单据所有质检名称，用逗号区分
     */
    @TableField("S_QUALITYSIGNNAME")
    private String qualitySignName;

    /**
     * 回填备注人CODE
     */
    @TableField("S_FILLREMARKCODE")
    private String fillRemarkCode;

    /**
     * 回填备注人名称
     */
    @TableField("S_FILLREMARKNAME")
    private String fillRemarkName;

    /**
     * 回填备注时间
     */
    @TableField("D_FILLREMARKTIME")
    private Date fillRemarkTime;

    /**
     * 单据创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 单据创建职工姓名
     */
    @TableField("S_CREATESTAFFCODE")
    private String createStaffCode;

    /**
     * 单据创建职工姓名
     */
    @TableField("S_CREATESTAFFNAME")
    private String createStaffName;
}
