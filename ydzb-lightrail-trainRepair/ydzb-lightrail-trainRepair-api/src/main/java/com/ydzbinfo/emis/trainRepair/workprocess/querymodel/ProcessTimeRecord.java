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
@TableName("XZY_M_PROCESSTIMERECORD")
@Data
public class ProcessTimeRecord implements Serializable,Cloneable {


    /**
     * 主键
     */
    @TableId("S_PROCESSID")
    private String processId;

    /**
     * 项目状态（1-开始，3-暂停，4-继续，2-结束）
     */
    @TableField("D_ITEMTIMESTATE")
    private String itemTimeState;

    /**
     * 时间
     */
    @TableField("D_TIME")
    private Date time;

    /**
     * 作业过程人员表主键
     */
    @TableField("S_PROCESSPERSONID")
    private String processPersonId;

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
     * 删除人
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
        return "ProcessTimeRecord{" +
                "processId=" + processId +
                ", itemTimeState=" + itemTimeState +
                ", time=" + time +
                ", processPersonId=" + processPersonId +
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
    public ProcessTimeRecord clone(){
        ProcessTimeRecord processTimeRecord = null;
        try {
            processTimeRecord=(ProcessTimeRecord)super.clone();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return processTimeRecord;
    }
}