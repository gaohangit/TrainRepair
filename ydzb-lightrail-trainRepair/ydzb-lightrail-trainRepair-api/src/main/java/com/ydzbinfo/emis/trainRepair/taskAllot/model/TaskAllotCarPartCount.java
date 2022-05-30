package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

/**
 * @Description:车组派工状态
 * @Data: 2021/9/3
 * @Author: 冯帅
 */
@Data
public class TaskAllotCarPartCount {
    //车组ID
    String trainSetId;
    //派工辆序数量
    long carPartCount;
}
