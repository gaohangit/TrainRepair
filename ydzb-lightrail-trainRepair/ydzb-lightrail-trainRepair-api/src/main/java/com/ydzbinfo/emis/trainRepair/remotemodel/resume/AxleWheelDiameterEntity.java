package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

import lombok.Data;

/**
 * @author 史艳涛
 * @description 镟修数据
 * @createDate 2021/10/26 14:42
 **/
@Data
public class AxleWheelDiameterEntity {

    /**
     * 车组ID
     */
    private String trainSetID;

    /**
     * 辆序（两位）
     */
    private String carNo;

    /**
     * 轴位
     */
    private String locatetionnum;

    /**
     * 轴号
     */
    private String serialnum_axle;

    /**
     * 轴走行
     */
    private String runMiles;

    /**
     * 轴动拖类型 拖/动
     */
    private String dtType;

    /**
     * 1位镟修前轮径值
     */
    private String xxqlj_1;

    /**
     * 2位镟修前轮径值
     */
    private String xxqlj_2;

    /**
     * 1位镟修后轮径值
     */
    private String xxhlj_1;

    /**
     * 2位镟修后轮径值
     */
    private String xxhlj_2;

    /**
     * 镟修日期 20160509
     */
    private String generatetime;
}
