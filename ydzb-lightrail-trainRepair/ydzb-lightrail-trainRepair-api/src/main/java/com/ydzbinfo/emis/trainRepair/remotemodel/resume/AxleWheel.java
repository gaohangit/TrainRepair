package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

import lombok.Data;

/**
 * @author 史艳涛
 * @description 轮轴数据
 * @createDate 2021/10/20 14:34
 **/
@Data
public class AxleWheel {

    /**
     * 辆序
     */
    private String carNo;
    /**
     *  动拖类型
     */
    private String dtType;
    /**
     *  轴位
     */
    private String locatetionNum;
    /**
     *  车轴累计走行公里
     */
    private String runmiles;
    /**
     *  轴号
     */
    private String serialNum;
    /**
     *  右轮号
     */
    private String ySerialnum;
    /**
     *  右轮累计走行公里
     */
    private String yrunmiles;
    /**
     *  左轮号
     */
    private String zRunmiles;
    /**
     *  左轮走行公里
     */
    private String zSerialNum;
}
