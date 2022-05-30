package com.ydzbinfo.emis.trainRepair.remotemodel.device;

import lombok.Data;

import java.util.Date;

/**
 * @author 史艳涛
 * @description 设备上传的空心轴探伤数据实体
 * @createDate 2021/10/25 9:04
 */
@Data
public class EntityAxleDevice {

    /**
     * 端位，A端和B端
     */
    private String ADAPTSIDE;

    /**
     * 轴端防锈油实施者
     */
    private String ANTIRUST;

    /**
     * 轴端安装人A端
     */
    private String ASIDELOADER;

    /**
     * 轴端拆卸人A端
     */
    private String ASIDEUNLOADER;

    /**
     * 轴号
     */
    private String AXLENO;

    /**
     * 轴位，1、2、3或4
     */
    private short AXLEPOS;

    /**
     * 分为动轴和拖轴
     */
    private String AXLETYPE;

    /**
     * 轴端安装人B端
     */
    private String BSIDELOADER;

    /**
     * 轴端拆卸人B端
     */
    private String BSIDEUNLOADER;

    /**
     * 5位的车辆号，如204801
     */
    private String CARNO;

    /**
     * 轴端安装人，电务
     */
    private String ELECTRICLOADER;

    /**
     * 轴端拆卸人，电务
     */
    private String ELECTRICUNLOADER;

    /**
     * 主键目前未用
     */
    private String ID;

    /**
     * 唯一ID
     */
    private String INSPECTID;

    /**
     * 正常和异常
     */
    private String INSPECTIONRESLUT;

    /**
     * 累计走行
     */
    private int INSPECTMILEAGE;

    /**
     * 探伤工
     */
    private String INSPECTOR;

    /**
     * 检测开始时间
     */
    private Date INSPECTSTARTTIME;

    /**
     * 探伤时间
     */
    private Date INSPECTTIME;

    /**
     * 0实车车轴探伤，1试样轴日常校验，2试样轴季度校验
     */
    private short INSPECTTYPE;

    /**
     *质检人
     */
    private String QUALITYINSPECTOR;

    /**
     *备注
     */
    private String REMARK;

    /**
     *未发送和已发送
     */
    private String REPORTSTATE;

    /**
     *车组号
     */
    private String TRAINSETID;
}
