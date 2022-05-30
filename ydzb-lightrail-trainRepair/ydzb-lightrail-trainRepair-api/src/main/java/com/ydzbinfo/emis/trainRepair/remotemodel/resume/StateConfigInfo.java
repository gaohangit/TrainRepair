package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

import lombok.Data;

/**
 * @author 史艳涛
 * @description 镟修状态确认值
 * @createDate 2021/10/22 14:34
 **/
@Data
public class StateConfigInfo {

    /**
     * 同一轴轮径差最大值（所有车型）
     */
    private String tyz;

    /**
     * 同一转向架轮径差最大值（CRH380A(L)/CRH5/CRH2）
     */
    private String tyzxj;

    /**
     * 同一车辆轮径差最大值（CRH380A(L)/CRH2）
     */
    private String tycllj;

    /**
     * 拖车同一转向架轮径差最大值（CRH380CL/CRH380B(L)/CRH3/CRH1）
     */
    private String ttyzxj;

    /**
     * 动车同一转向架轮径差最大值（CRH380CL/CRH380B(L)/CRH3/CRH1）
     */
    private String dtyzxj;

    /**
     * 动车同一车辆轮径差最大值（CRH380CL/CRH380B(L)/CRH3）
     */
    private String dtycllj;

    /**
     * 拖车同一车辆轮径差最大值（CRH380CL/CRH380B(L)/CRH3）
     */
    private String ttycllj;

    /**
     * 同一车辆单元内车辆间轮径差最大值(CRH380A(L)/CRH2)
     */
    private String tycldynclj;

    /**
     * 相邻车厢间轮径差最大值(CRH380A(L)/CRH2)
     */
    private String xlcxjlj;

    /**
     * 同一动车内两条动力轮对轮径差最大值(CRH5)
     */
    private String tydcnltdlldlj;

    /**
     * 动车同一轮对轮径差最大值
     */
    private String dtyldlj;

    /**
     * 拖车同一轮对轮径差最大值
     */
    private String ttyldlj;


    /**
     * 同一轴径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-1-2]（所有车型）
     */
    private String tyz_source;

    /**
     * 同一转向架轮径差最大值所位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-2-2]（CRH380A(L)/CRH5/CRH2）
     */
    private String tyzxj_source;

    /**
     * 同一车辆轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-4-2]（CRH380A(L)/CRH2）
     */
    private String tycllj_source;

    /**
     * 拖车同一转向架轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-1-2]（CRH380CL/CRH380B(L)/CRH3/CRH1）
     */
    private String ttyzxj_source;

    /**
     * 动车同一转向架轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-1-2]（CRH380CL/CRH380B(L)/CRH3/CRH1）
     */
    private String dtyzxj_source;

    /**
     * 动车同一车辆轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-1-2]（CRH380CL/CRH380B(L)/CRH3）
     */
    private String dtycllj_source;

    /**
     * 拖车同一车辆轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-1-2]（CRH380CL/CRH380B(L)/CRH3）
     */
    private String ttycllj_source;

    /**
     * 同一车辆单元内车辆间轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202504-1-2](CRH380A(L)/CRH2)
     */
    private String tycldynclj_source;

    /**
     * 相邻车厢间轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202504-1-2](CRH380A(L)/CRH2)
     */
    private String xlcxjlj_source;

    /**
     * 同一动车内两条动力轮对轮径差最大值所在位置[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-3-2](CRH5)
     */
    private String tydcnltdlldlj_source;

    /**
     * 动车同一轮对轮径差最大值所在位置（qyz20200313）[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-1-2]（CRH380CL/CRH380B(L)/CRH3）
     */
    private String dtyldlj_source;

    /**
     * 拖车同一轮对轮径差最大值所在位置（qyz20200313）[车号1-轴位1-端位1/车号2-轴位2-端位2，例如：202503-1-1/202503-1-2]（CRH380CL/CRH380B(L)/CRH3）
     */
    private String ttyldlj_source;
}
