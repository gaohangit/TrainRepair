package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: gaoHan
 * @date: 2021/8/7 15:12
 * @description:
 */
@Data
public class MonitorBase {
    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 车型
     */
    @TableField("S_TRAINTYPE")
    private String trainType;

    /**
     * 批次
     */
    @TableField("S_BATCHCODE")
    private String batchCode;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 创建人CODE
     */
    @TableField("S_CREATEUSERCODE")
    private String createUserCode;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String createUserName;
    //作业包
    List<MonitorWithPackets> monitorWithPacketsList;
}