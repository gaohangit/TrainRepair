package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * EMIS系统派工记录（辆序部件表）子表
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
@Data
@TableName("ZY_M_PLACEPARTTASK")
public class ZyMPlaceparttask {

    /**
     * 辆序部件表主键ID
     */
    @TableField("S_PLACEPARTID")
 private String placepartid;
    /**
     * 辆序
     */
    @TableField("S_TRAINSETCAR")
    private String trainsetcar;
    /**
     * 位置
     */
    @TableField("S_PARTPOSITION")
    private String partposition;

    /**
     * 位置说明
     */
    @TableField("S_PARTPOSITIONNOTE")
    private String partpositionnote;

    /**
     * 关于dict_trainsetstructure，标识哪个功能位
     */
    @TableField("S_TRAINSETSTRUCTUREID")
    private String trainsetstructureid;

    /**
     * 功能位名称
     */
    @TableField("S_TRAINSETSTRUCTURETEXT")
    private String trainsetstructuretext;

    /**
     * 动车组配件类型编码，关联dict_keypartstype
     */
    @TableField("S_KEYPARTTYPEID")
    private String keyparttypeid;

    /**
     * 关键配件名称
     */
    @TableField("S_PARTTYPENAME")
    private String parttypename;

    /**
     * 产品序列号
     */
    @TableField("S_SERIALNUM")
    private String serialnum;

    /**
     * 部产品标识代码
     */
    @TableField("S_BUREAUNUMNO")
    private String  bureaunumno;

    /**
     * 日计划ID，编码规则：YYYY-MM-DD-XX。其中XX为00功01，分别为白班和夜班
     */
    @TableField("S_DAYPLANID")
    private String  dayplanid;

    /**
     * 发布号
     */
    @TableField("S_PUBLISHCODE")
    private String  publishcode;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * EMIS系统派工记录主表【ZY_M_TASKITEMALLOT】主键ID
     */
    @TableField("S_TASKITEMALLOTID")
    private String taskitemallotid;

    /**
     * 班组类别:0-检修，1-质检
     */
    @TableField("C_BRANCHTYPE")
    private String branchtype;

    /**
     * 派工任务状态：1--未分配；2--已分配；4--已派工；8--已生成派工单；16--已经回填；
     */
    @TableField("I_ASSIGNTASKSTATE")
    private int assigntaskstate;

    /**
     * 指派任务时间
     */
    @TableField("D_TASKALLOTTIME")
    private Date taskallottime;

    /**
     * 检修工长CODE
     */
    @TableField("S_WORKERSTUFFCODE")
    private String workerstuffcode;

    /**
     * 检修工长
     */
    @TableField("S_WORDERSTUFFNAME")
    private String worderstuffname;

    /**
     * 分配人员名称
     */
    @TableField("S_PERSONNAME")
    private String personname;

    /**
     * 分配人员CODE
     */
    @TableField("S_PERSONNAMECODE")
    private String personnamecode;

    /**
     * 分配类型:0-项目；1-辆序；2-部件
     */
    @TableField("S_ALLOTMODE")
    private String allotmode ;
}
