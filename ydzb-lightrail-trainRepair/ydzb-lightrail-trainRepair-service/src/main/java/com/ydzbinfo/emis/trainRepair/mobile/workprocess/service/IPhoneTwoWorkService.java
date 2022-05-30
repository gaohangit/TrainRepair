package com.ydzbinfo.emis.trainRepair.mobile.workprocess.service;

import com.ydzbinfo.emis.trainRepair.mobile.model.RfidProcessCarPartInfo;
import com.ydzbinfo.emis.trainRepair.mobile.model.TwoWork;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessLocation;

import java.util.List;

/**
 * Description:
 * Author: wuyuechang
 * Create Date Time: 2021/5/6 9:52
 * Update Date Time: 2021/5/6 9:52
 *
 */
public interface IPhoneTwoWorkService {

    /**
     * @author: wuyuechang
     * @Description: 获取当前车组的二级修作业过程
     * @date: 2021/5/18 14:40
     */
    TwoWork getWorkList(String unitCode, String dayPlanID, String deptCode, List<String> staffIDs, List<String> trainsetId);

    /**
     * @author: wuyuechang
     * @Description: 获取二级修作业过程车组
     * @date: 2021/5/18 14:40
     */
    List<TwoWork> getWorkTrainsetList(String unitCode, String dayPlanID, String deptCode, List<String> staffIDs);

    /**
     * @author: wuyuechang
     * @Description: 设置标签信息
     * @date: 2021/5/18 14:40
     */
    void setPacketItemInfo(RfidProcessCarPartInfo rfidProcessCarPartInfo);

    /**
     * @author: wuyuechang
     * @Description: 结束项目
     * @date: 2021/5/18 14:40
     */
    void setItemEnd(RfidProcessCarPartInfo rfidProcessCarPartInfo);

    /**
     * @author: wuyuechang
     * @Description: 二级修保存标签
     * @date: 2021/5/18 14:40
     */
    void setRfIdInfo(List<ProcessLocation> processLocations);
}
