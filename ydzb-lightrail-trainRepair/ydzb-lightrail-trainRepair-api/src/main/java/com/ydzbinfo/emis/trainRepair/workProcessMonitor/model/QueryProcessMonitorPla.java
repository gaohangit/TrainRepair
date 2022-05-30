package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/31 10:30
 * @Description:  监控查询实体（到列位）
 */
@Data
public class QueryProcessMonitorPla {

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 列位编码
     */
   private String trackPlaCode;

    /**
     * 列位名称
     */
   private String trackPlaName;

    /**
     * 是否长编  1--是  0--否
     */
    private String isLong;

    /**
     * 离开时间
     */
    // @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    // private Date outTime;

    /**
     * 车头方向   01--车头在左侧   00--车头在右侧
     */
    private String headDirection;

    /**
     *车头所在列位编码
     */
    private String headDirectionPlaCode;

    /**
     * 车头所在列位
     */
    private String headDirectionPla;

    /**
     * 车尾方向    01--车尾在左侧   00--车尾在右侧
     */
    private String tailDirection;

    /**
     * 车尾所在列位编码
     */
    private String tailDirectionPlaCode;

    /**
     * 车尾所在列位
     */
    private String tailDirectionPla;

    /**
     * 供断电状态   1--有电  2--无电
     */
    private String powerState;

    /**
     * 外协任务集合
     */
    List<Integration> integrationList;

    /**
     * 故障信息
     */
    private FaultData faultData;

    /**
     * 项目信息字符串拼接集合
     */
    private List<String> itemInfos;

    //下一个股道名称
    private String nextTrackName;
    //下一个孤岛code
    private String nextTrackCode;
    //下一个列位名称
    private String nextTrackPositionName;
    //下一个列为code
    private String nextTrackPositionCode;
    //进入下一个股道时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nextInTime;
}
