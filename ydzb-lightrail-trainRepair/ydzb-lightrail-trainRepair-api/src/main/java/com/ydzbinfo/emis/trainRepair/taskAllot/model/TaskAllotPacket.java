package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-05
 */
@Data
@TableName("XZY_M_TASKALLOTPACKET")
public class TaskAllotPacket implements Serializable {


    /**
     * 主键
     */
    @TableId("S_TASKALLOTPACKETID")
    private String taskAllotPacketId;

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
     * 派工唯一编码
     */
    @TableField("S_PUBLISHCODE")
    private String publishCode;
}
