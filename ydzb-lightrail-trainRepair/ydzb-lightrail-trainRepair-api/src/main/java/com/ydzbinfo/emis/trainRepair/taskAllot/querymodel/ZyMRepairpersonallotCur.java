package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * EMIS系统当前人员分配表
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
@Data
@TableName("ZY_M_REPAIRPERSONALLOT_CUR")
public class ZyMRepairpersonallotCur {

    /**
     * 主键ID
     */
    @TableField("S_REPAIRPERSONALLOTCURID")
    private String repairpersonallotcurid;

    /**
     * 检修班编码
     */
    @TableField("S_DEPTCODE")
    private String deptcode;

    /**
     * 检修班组名称
     */
    @TableField("S_DEPTNAME")
    private String deptname;

    /**
     * 检修小组编码
     */
    @TableField("S_BRANCHCODE")
    private String branchcode;

    /**
     * 检修小组名称
     */
    @TableField("S_BRANCHNAME")
    private String branchname;

    /**
     * 角色编号
     */
    @TableField("S_ROLECODE")
    private String rolecode;

    /**
     * 角色名称
     */
    @TableField("S_ROLENAME")
    private String rolename;

    /**
     * 岗位ID，对应ZY_B_DEPTPOSTCONFIG表主键
     */
    @TableField("S_POSTID")
    private String postid;

    /**
     * 岗位名称
     */
    @TableField("S_POSTNAME")
    private String postname;

    /**
     * 检修小组类别1为一级修；2为专项修；3为三级修；4为四级修；5为五级修
     */
    @TableField("I_BRANCH_TYPE")
    private int branch_type;

    /**
     * 职工编码
     */
    @TableField("S_STUFFCODE")
    private String stuffcode;

    /**
     * 职工姓名
     */
    @TableField("S_STUFFNAME")
    private String stuffname;

    /**
     * 分配时间
     */
    @TableField("D_ALLOTDATE")
    private Date allotdate;

    /**
     * 班组类别:0-检修，1-质检
     */
    @TableField("C_BRANCHTYPE")
    private String branchtype;
}
