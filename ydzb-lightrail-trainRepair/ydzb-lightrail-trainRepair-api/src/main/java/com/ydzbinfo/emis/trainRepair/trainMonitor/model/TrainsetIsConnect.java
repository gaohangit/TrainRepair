package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import lombok.Data;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/9 9:31
 **/
@Data
public class TrainsetIsConnect {
    private String status;
    private List<String> trainsetPostIonIds;
    private String trackCode;
}
