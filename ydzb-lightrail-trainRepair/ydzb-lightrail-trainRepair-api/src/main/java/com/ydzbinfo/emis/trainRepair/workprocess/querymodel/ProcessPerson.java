package com.ydzbinfo.emis.trainRepair.workprocess.querymodel;

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
 * @since 2021-05-08
 */
@TableName("XZY_M_PROCESSPERSON")
@Data
public class ProcessPerson implements Serializable,Cloneable {


    /**
     * 主键
     */
    @TableId("S_PROCESSPERSONID")
    private String processPersonId;

    /**
     * 作业人员ID
     */
    @TableField("S_WORKERID")
    private String workerId;

    /**
     * 作业人员姓名
     */
    @TableField("S_WORKERNAME")
    private String workerName;

    /**
     * 作业人员类型（1--检修，2--质检，3-无修程任务完成，4-无修程任务确认）
     */
    @TableField("S_WORKERTYPE")
    private String workerType;

    /**
     * 项目时间唯一标识
     */
    @TableField("S_ITEMPUBLCISHED")
    private String itemPublcished;

    /**
     * 作业过程辆序部件表主键
     */
    @TableField("S_PROCESSID")
    private String processId;

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
     * 删除人编码
     */
    @TableField("S_DELUSERCODE")
    private String delUserCode;

    /**
     * 删除人名称
     */
    @TableField("S_DELUSERNAME")
    private String delUserName;

    /**
     * 记录时间
     */
    @TableField("S_RECORDTIME")
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


    @Override
    public String toString() {
        return "ProcessPerson{" +
                "processPersonId=" + processPersonId +
                ", workerId=" + workerId +
                ", workerName=" + workerName +
                ", workerType=" + workerType +
                ", itemPublcished=" + itemPublcished +
                ", processId=" + processId +
                ", flag=" + flag +
                ", delTime=" + delTime +
                ", delUserCode=" + delUserCode +
                ", delUserName=" + delUserName +
                ", recordTime=" + recordTime +
                ", recorderName=" + recorderName +
                ", recorderCode=" + recorderCode +
                "}";
    }

    @Override
    public ProcessPerson clone(){
        ProcessPerson processPerson = null;
        try {
            processPerson=(ProcessPerson)super.clone();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return processPerson;
    }
}
