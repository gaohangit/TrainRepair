package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/5/25 19:54
 * @Description:
 */
@Data
public class WorkPacket implements Serializable {

    /**
     * 作业包编码
     */
    private String packetCode;

    /**
     * 作业包名称
     */
    private String packetName;

    /**
     * 作业包项目总数
     */
    private Integer packetTotalCount;

    /**
     * 作业包完成项目数
     */
    private Integer packetEndCount;

    public Integer getPacketTotalCount() {
        return packetTotalCount==null?0:packetTotalCount;
    }

    public void setPacketTotalCount(Integer packetTotalCount) {
        this.packetTotalCount = packetTotalCount;
    }

    public Integer getPacketEndCount() {
        return packetEndCount==null?0:packetEndCount;
    }

    public void setPacketEndCount(Integer packetEndCount) {
        this.packetEndCount = packetEndCount;
    }
}
