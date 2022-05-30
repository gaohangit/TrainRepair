package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

/**
 * @Description:
 * @Data: 2021/8/26
 * @Author: 冯帅
 */
@Data
public class RepairPacketStatu {

    //车组ID
    private String sTrainsetid;

    //班次
    private String sDayplanid;

    //项目编码(源编码)
    private String sItemcode;

    //所属单位编码
    private String sDeptcode;

    //包编码
    private String sPacketcode;

    //项目当前状态：0 未下发 1 未做 2  部分完成  3  完成   4 已派工
    private String cStatue;

}
