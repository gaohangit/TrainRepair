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
 * 对应于车组号的检修记录单（包括一级修，特殊单据）
 * </p>
 *
 * @author 韩旭
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ZY_M_CHKLSTSUMMARY_LV1")
public class ChklstsummaryLv1Old implements Serializable {


    /**
     * 记录单总表ID(主键ID)
     */
    @TableId("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 动车组编号
     */
    @TableField("S_TRAINSETID")
    private String trainsetId;

    /**
     * 入所车次
     */
    @TableField("S_INTRAINNO")
    private String intrainno;

    /**
     * 出所车次
     */
    @TableField("S_OUTTRAINNO")
    private String outtrainno;

    /**
     * 作业包CODE
     */
    @TableField("S_SPPACKETCODE")
    private String sppacketcode;

    /**
     * 作业包名称
     */
    @TableField("S_SPPACKETNAME")
    private String sppacketname;

    /**
     * 模版文档适用条目ID
     */
    @TableField("S_DOCITEMID")
    private String docItemId;

    /**
     * 对应的模版编号
     */
    @TableField("S_DOCCODE")
    private String docCode;

    /**
     * 模版文档类型：3--一级修检修记录单；4--一级修试验单
     */
    @TableField("S_DOCTYPE")
    private String doctype;

    /**
     * 修程CODE
     */
    @TableField("S_MAINTCYCCODE")
    private String mainCycCode;

    /**
     * 检修时走行公里
     */
    @TableField("I_REPAIRACCUMILE")
    private Integer repairaccumile;

    /**
     * 辆序标识：1--前八辆；2--后八辆；3--全列；
     */
    @TableField("I_CARNOFLAG")
    private Integer carnoflag;

    /**
     * 记录单编号
     */
    @TableField("S_CHECKLISTNO")
    private String checklistno;

    /**
     * 检修班组描述
     */
    @TableField("S_REPAIRDEPTNAMES")
    private String repairdeptnames;

    /**
     * 检修人员及辆序描述
     */
    @TableField("S_REPAIRSTAFFNAMES")
    private String repairstaffnames;

    /**
     * 是否打印派工单
     */
    @TableField("C_PRINTSTATE")
    private String printstate;

    /**
     * 检修时间，生成记录单时间
     */
    @TableField("D_REPAIRTIME")
    private Date repairtime;

    /**
     * 回填状态：0--未回填；1--部分回填；2--完全回填；
     */
    @TableField("C_BACKFILLSTATE")
    private String backfillstate;

    /**
     * 质检员签字CODE
     */
    @TableField("S_INSPECTORSIGNCODES")
    private String inspectorsigncodes;

    /**
     * 质检员签字NAMES
     */
    @TableField("S_INSPECTORSIGNNAMES")
    private String inspectorsignnames;

    /**
     * 质检员签字时间
     */
    @TableField("D_INSPECTORSIGNTIME")
    private Date inspectorsigntime;

    /**
     * 检修工长签字CODE
     */
    @TableField("S_FOREMANSIGNCODES")
    private String foremansigncodes;

    /**
     * 检修工长签字NAMES
     */
    @TableField("S_FOREMANSIGNNAMES")
    private String foremansignnames;

    /**
     * 工长签字时间
     */
    @TableField("D_FOREMANSIGNTIME")
    private Date foremansigntime;

    /**
     * 记录单标识（位）：0--未签字（默认）；1--工长已经签字；2--质检员已经签字；4--已经回填检修实绩
     */
    @TableField("I_CHECKLISTFLAG")
    private Integer checklistflag;

    /**
     * 批次号：年月日时分秒字符串（例：20130613112000）
     */
    @TableField("S_BATCHNO")
    private String batchno;

    /**
     * 文件保存路径
     */
    @TableField("S_FILEPATH")
    private String filepath;

    /**
     * 文件名称
     */
    @TableField("S_FILENAME")
    private String filename;

    /**
     * 上传标识：0--未上传；1--派工上传；2--质检签字上传；4--检修工长签字上传；
     */
    @TableField("I_UPLOADSUCCESS")
    private Integer uploadsuccess;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 锁定时间：默认为3分钟
     */
    @TableField("D_LOCKTIME")
    private Date locktime;


}
