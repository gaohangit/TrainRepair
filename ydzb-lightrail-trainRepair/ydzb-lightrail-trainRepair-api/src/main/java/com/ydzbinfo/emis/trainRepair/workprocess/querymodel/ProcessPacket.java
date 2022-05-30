package com.ydzbinfo.emis.trainRepair.workprocess.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
@TableName("XZY_M_PROCESSPACKET")
@Data
public class ProcessPacket implements Serializable {


    /**
     * 主键
     */
    @TableId("S_PROCESSPACKETID")
    private String processPacketId;

    /**
     * 作业包CODE
     */
    @TableField("S_PACKETCODE")
    private String packetCode;

    /**
     * 作业包NAME
     */
    @TableField("S_PACKETNAME")
    public String packetName;

    /**
     * 作业包类型
     */
    @TableField("S_PACKETTYPE")
    private String packetType;

    /**
     * 项目集合
     */
    @TableField(exist = false)
    private List<ProcessItem> processItemList;
}
