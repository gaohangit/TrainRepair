package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

import java.io.Serializable;

/***
 * @author: 冯帅
 * @date: 2021/9/6
 */
@Data
public class TaskAllotPhoneTrainSetState implements Serializable {

    //车组ID
    private String trainSetId;

    //派工状态
    private String state;
}
