package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author: gaoHan
 * @date: 2021/7/21 9:41
 * @description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TrainsetPostionCurWithNextTrack extends TrainsetPostionCur {
    //下一个股道名称
    private String nextTrackName;
    //下一个孤岛code
    private String nextTrackCode;
    //下一个列位名称
    private String nextTrackPositionName;
    //下一个列为code
    private String nextTrackPositionCode;
    //进入下一个股道时间
    private Date nextInTime;
}