package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPacket;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/12
 * @Description: 二级修作业实体
 */
@Data
public class TwoWorkProcessData extends ProcessData implements Serializable {

    /**
     * 作业包集合
     */
    List<ProcessPacket> processPacketList;

    /**
     * 项目数量
     */
    Integer itemCount;
}
