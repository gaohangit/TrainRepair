package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
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
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ZY_M_CHECKLISTSUMMARY")
public class ChecklistSummaryOld implements Serializable {


    @TableField("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

    @TableField("S_DAYPLANID")
    private String dayPlanId;

    @TableField("S_TRAINSETID")
    private String trainsetId;

    @TableField("S_SPREPAIRITEMCODE")
    private String sprepairitemcode;

    @TableField("S_SPREPAIRITEMNAME")
    private String sprepairitemname;

    @TableField("S_SPPACKETCODE")
    private String sppacketcode;

    @TableField("S_SPPACKETNAME")
    private String sppacketname;

    @TableField("C_CHECKLISTTYPE")
    private String checklistType;

    @TableField("S_REPAIRCYCLE")
    private String repaircycle;

    @TableField("S_MAINTCYCCODE")
    private String mainCycCode;

    @TableField("S_MAINTCYCDISPLAY")
    private String maintcycdisplay;

    @TableField("I_REPAIRACCUMILE")
    private Integer repairaccumile;

    @TableField("S_CHECKLISTID")
    private String checklistId;

    @TableField("I_CARNOFLAG")
    private Integer carnoflag;

    @TableField("S_CHECKLISTNO")
    private String checklistno;

    @TableField("S_REPAIRDEPTNAMES")
    private String repairdeptnames;

    @TableField("S_REPAIRSTAFFNAMES")
    private String repairstaffnames;

    @TableField("C_PRINTSTATE")
    private String printstate;

    @TableField("D_REPAIRTIME")
    private Date repairtime;

    @TableField("C_BACKFILLSTATE")
    private String backfillstate;

    @TableField("S_INSPECTORSIGNCODES")
    private String inspectorsigncodes;

    @TableField("S_INSPECTORSIGNNAMES")
    private String inspectorsignnames;

    @TableField("S_FOREMANSIGNCODES")
    private String foremansigncodes;

    @TableField("S_FOREMANSIGNNAMES")
    private String foremansignnames;

    @TableField("S_BATCHNO")
    private String batchno;

    @TableField("S_FILEPATH")
    private String filepath;

    @TableField("S_FILENAME")
    private String filename;

    @TableField("I_UPLOADSUCCESS")
    private Integer uploadsuccess;

    @TableField("S_REPAIRREMARK")
    private String repairremark;

    @TableField("S_REMARK")
    private String remark;

    @TableField("S_FILLOUTREMARK")
    private String filloutremark;

    @TableField("C_ITEMTYPE")
    private String itemType;

    @TableField("S_CHANGEREASON")
    private String changereason;

    @TableField("S_COMPONENTPOSITION")
    private String componentposition;

    @TableField("I_PARTCHANGESTATE")
    private Integer partchangestate;

    @TableField("I_CHECKLISTFLAG")
    private Integer checklistflag;

    @TableField("D_FOREMANSIGN")
    private Date foremansign;

    @TableField("D_INSPECTORSIGN")
    private Date inspectorsign;

    @TableField("S_PRINTBATCH")
    private String printbatch;

    @TableField("S_PRINTPERSON")
    private String printperson;

    @TableField("C_PRINTSUCCESS")
    private String printsuccess;

    @TableField("S_INTRAINNO")
    private String intrainno;

    @TableField("S_OUTTRAINNO")
    private String outtrainno;

    @TableField("S_DOCCODE")
    private String docCode;

    @TableField("S_DOCTYPE")
    private String doctype;

    @TableField("S_REPAIRCARD")
    private String repaircard;

    @TableField("S_QUALITYCODE")
    private String qualitycode;

    @TableField("S_QUALITYNAME")
    private String qualityname;

    @TableField("S_SYSNAME")
    private String sysname;

    @TableField("S_COMPONENTNAME")
    private String componentname;

    @TableField("D_LOCKTIME")
    private Date locktime;


}
