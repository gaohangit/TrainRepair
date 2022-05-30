package com.ydzbinfo.emis.trainRepair.remotemodel.device;

import lombok.Data;

/**
 * @author 史艳涛
 * @description 设备上传的轮辋轮辐探伤数据实体
 * @createDate 2021/11/4 9:04
 */
@Data
public class EntityLUDevice {

    /**
     * 动拖类型
     */
    private String ALXEDRIVER;

    /**
     * 轴号
     */
    private String ALXENO;

    /**
     *轴位
     */
    private String ALXEPOSITION;

    /**
     *车辆号
     */
    private String CARRIAGENO;

    /**
     *检测类型
     */
    private String CHECKTYPE;

    /**
     *检测时间
     */
    private String DETECTTIME;

    /**
     *设备编码
     */
    private String DEVICECODE;

    /**
     *一位轮号
     */
    private String FIRSTWHEELNO;

    /**
     *一位轮检测结果
     */
    private String FIRSTWHEELRESULT;

    /**
     *检测记录唯一ID
     */
    private String LUCHECKRECORDID;

    /**
     *结果备注
     */
    private String MEMO;

    /**
     *班组
     */
    private String OPERATORBRANCH;

    /**
     *检测人
     */
    private String OPERATORNAME;

    /**
     *走行公里
     */
    private String RUNNINGKM;

    /**
     *二位轮号
     */
    private String SECONDWHEELNO;

    /**
     *二位轮检测结果
     */
    private String SECONDWHEELRESULT;

    /**
     *车组号
     */
    private String TRAINSETNO;

    /**
     *轮型
     */
    private String YBLWHEELNO;
}
