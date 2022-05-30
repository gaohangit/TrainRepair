package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * EMIS系统派工记录主表
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
@Data
@TableName("ZY_M_TASKITEMALLOT")
public class ZyMTaskitemallot {

    /**
     * 主键ID
     */
    @TableField("S_TASKITEMALLOTID")
    private String taskitemallotid;
    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayplanid;
    /**
     * 动车组名称
     */
    @TableField("S_TRAINSETID")
    private String trainsetid;
    /**
     * 检修班组编号
     */
    @TableField("S_DEPTCODE")
    private String deptcode;
    /**
     * 项目聚合名称（作业包名称或系统名称）
     */
    @TableField("S_ITEMAGGREGATIONNAME")
    private String itemaggregationname;
    /**
     * 项目聚合编号（作业包CODE或系统CODE）
     */
    @TableField("S_ITEMAGGREGATIONCODE")
    private String itemaggregationcode;
    /**
     * 项目聚合类型 0--作业包，1--系统
     */
    @TableField("S_ITEMAGGREGATIONTYPE")
    private String itemaggregationtype;
    /**
     * 派工模式0--作业包，1--系统
     */
    @TableField("S_ALLOTTASKMODE")
    private String allottaskmode;
    /**
     * 修程
     */
    @TableField("S_MAINCYC")
    private String maincyc;
    /**
     * 辆序顺序(0--全部,1--前八辆,2--后八辆)
     */
    @TableField("S_DISTINCTCARS")
    private String distinctcars;
    /**
     * 检修项目Code
     */
    @TableField("S_SPLPACKETITEMCODE")
    private String splpacketitemcode;
    /**
     * 检修项目Name
     */
    @TableField("S_SPLPACKETITEMNAME")
    private String splpacketitemname;
    /**
     * 作业包类型
     */
    @TableField("S_PACKETTYPECODE")
    private String packettypecode;
    /**
     * 作业包编码
     */
    @TableField("S_SPPACKETCODE")
    private String sppacketcode;
    /**
     * 作业包名称
     */
    @TableField("S_SPPACKETNAME")
    private String sppacketname;
    /**
     * 班组类别:0-检修，1-质检
     */
    @TableField("C_BRANCHTYPE")
    private String branchtype;
    /**
     * 发布号
     */
    @TableField("S_PUBLISHCODE")
    private String publishcode;
    /**
     * 是否有辆序:1--有辆序；2--无辆序；
     */
    @TableField("C_CARNOFLAG")
    private String carnoflag;
    /**
     * 派工任务状态：1--全部未分配；2--全部已分配；3--部分分配；4--全部派工；（5-7）--部分派工；8--全部生成派工单；（9-15）--部分生成派工单；16--全部回填；（>16）--部分回填；
     */
    @TableField("I_ASSIGNTASKSTATE")
    private int assigntaskstate;
    /**
     * 标志位写入状态：0--修改；1--查询
     */
    @TableField("S_SYNCFLAG")
    private String syncflag;
    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;
    /**
     * 指派任务时间
     */
    @TableField("D_TASKALLOTTIME")
    private Date taskallottime;

}
