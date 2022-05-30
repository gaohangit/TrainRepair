package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import lombok.Data;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/11 13:58
 **/
@Data
public class PacketTask {
    private String trainsetId;
    //一级修
    private int oneNumBer;
    //二级修
    private int twoNumber;
    //故障
    private int faultNumber;
    //临修
    private int temporaryNumber;
    //滤网
    private int filterNumber;
    //关键作业
    private int planLessKeyNumber;
    private List<String> itemId;


}
