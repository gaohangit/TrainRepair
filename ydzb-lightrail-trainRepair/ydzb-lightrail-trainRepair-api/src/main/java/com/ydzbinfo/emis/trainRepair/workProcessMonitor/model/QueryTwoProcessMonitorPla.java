package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/25 19:53
 * @Description: 二级修作业过程监控实体（到列位）
 */
@Data
public class QueryTwoProcessMonitorPla extends QueryProcessMonitorPla implements Serializable {

    /**
     * 作业包集合
     */
    List<WorkPacket> workPacketList;
}
