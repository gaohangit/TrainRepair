package com.ydzbinfo.emis.trainRepair.trainMonitor.utils;

/**
 * @author: gaoHan
 * @date: 2021/8/3 14:59
 * @description:
 */
public enum TrainSetLocationConfigEnum {
    ShowTrackRefresh("ShowTrackRefresh", "1"),
    DataRefreshTime("DataRefreshTime", "60"),
    MovePlan("MovePlan", "0"),
    ShowTrackMenu("ShowTrackMenu", "0"),
    TrackCode("TrackCode", "0"),
    ShowNoTrainsetTrack("ShowNoTrainsetTrack", "0"),
    TrackPowerManualInput("TrackPowerManualInput", "1"),
    ManualInput("ManualInput","1"),
    TrackRefreshTime("TrackRefreshTime", "60"),
    ShowRepair("ShowRepair", "0"),
    AutoMoveTrain("AutoMoveTrain", "0"),
    IsMoveTrainPlanWarning("IsMoveTrainPlanWarning", "0"),
    MoveTrainPlanWarningTime("MoveTrainPlanWarningTime", "0"),
    ShowMoveTrainPlan("ShowMoveTrainPlan", "0"),
    ShowTrainsetTrackcode("ShowTrainsetTrackcode", "0"),
    HeadDirection("HeadDirection", "0"),
    NoteName("NoteName", "Note"),
    ShowRunningPlan("ShowRunningPlan", "0"),
    TrackPowerChangedState("TrackPowerChangedState", "0"),
    ShowLastRepairTask("ShowLastRepairTask", "0"),
    ShowShuntPlan("ShowShuntPlan", "0");


    String value;
    String label;

    TrainSetLocationConfigEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    public String getValue() {
        return value;
    }
}
