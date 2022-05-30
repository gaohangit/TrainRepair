package com.ydzbinfo.emis.trainRepair.mobile.workprocess.service;

import com.ydzbinfo.emis.trainRepair.mobile.model.OneWork;
import com.ydzbinfo.emis.trainRepair.mobile.model.RfidProcessCarPartInfo;
import com.ydzbinfo.emis.trainRepair.warnmanagent.querymodel.WorkWorning;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description:
 * Author: wuyuechang
 * Create Date Time: 2021/5/6 9:52
 * Update Date Time: 2021/5/6 9:52
 *
 * @see
 */
public interface IPhoneOneWorkService {

    /**
      * @author: wuyuechang
      * @Description: 根据车组获取一级修作业过程
      * @date: 2021/5/18 14:40
      */
    OneWork getWorkList(String unitCode, String dayPlanID, String deptCode, List<String> staffIDs, List<String> trainsetIds, List<String> roleNames,String itemCode);

    /**
     * @author: wuyuechang
     * @Description: 根据车组获取一级修作业过程时间记录
     * @date: 2021/5/18 14:40
     */
    List<RfidCardSummary> getRfidCardSummaryTimeInfo(String staffId, String trainsetId, String repairType, String itemCode, String dayplanId);

    /**
     * @author: wuyuechang
     * @Description: 设置标签信息
     * @date: 2021/5/18 14:40
     */
    void setRfIdInfo(RfidProcessCarPartInfo rfidProcessCarPartInfo);

    /**
     * @author: wuyuechang
     * @Description: 手持机一级修开始/暂停/继续/结束
     * @date: 2021/5/18 14:40
     */
    void updateTime(RfidProcessCarPartInfo rfidProcessCarPartInfo);

    /**
     * @author: wuyuechang
     * @Description: 上传图片
     * @date: 2021/5/18 14:40
     */
    void uploadImage(ProcessPic processPic, HttpServletRequest multipartHttpServletRequest);

    /**
     * @author: wuyuechang
     * @Description: 删除图片
     * @date: 2021/5/18 14:40
     */
    void removeImage(String picId);

    /**
     * @author: wuyuechang
     * @Description: 获取一级修车组信息
     * @date: 2021/5/18 14:40
     */
    List<OneWork> getWorkTrainsetList(String unitCode, String dayPlanID, String deptCode, List<String> workerIds);

    /**
      * @author: 吴跃常
      * @Description: 保存预警记录
      * @date: 2021/8/2 9:40
      */
    void earlyWarning(WorkWorning workWorning);
}
