package com.ydzbinfo.emis.trainRepair.mobile.workprocess.service;

import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCyc;

import java.util.List;

/**
 * Description: 无修程作业过程业务类
 * Author: wuyuechang
 * Create Date Time: 2021/5/13 10:52
 * Update Date Time: 2021/5/13 10:52
 *
 * @see
 */
public interface IPhoneNoMainCycWorkService {

    /**
     * @author: wuyuechang
     * @Description: 获取无修程作业过程
     * @date: 2021/5/18 14:40
     */
    List<NoMainCyc> getWorkList(String unitCode, String dayPlanID, String workerType);

    /**
     * @author: wuyuechang
     * @Description: 保存无修程信息
     * @date: 2021/5/18 14:40
     */
    void setNoManCycInfo(NoMainCyc rfidProcessCarPartInfo);
}
