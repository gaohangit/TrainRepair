package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import lombok.Data;

/**
 * @author 高晗
 * @description
 * @createDate 2022/2/28 9:39
 **/
@Data
public class TrainsetBaseInfoWithTrack extends TrainsetBaseInfo {
    boolean hasTrack;
}
