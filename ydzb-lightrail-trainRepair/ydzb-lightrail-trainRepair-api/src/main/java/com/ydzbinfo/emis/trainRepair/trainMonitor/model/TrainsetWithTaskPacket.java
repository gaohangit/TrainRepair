package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description
 * @createDate 2021/7/9 11:00
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TrainsetWithTaskPacket extends TrainsetPostionCur {
    List<ZtTaskPacketEntity> ztTaskPacketEntities;
}
