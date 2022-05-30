package com.ydzbinfo.emis.trainRepair.mobile.taskpandect.service;

import com.ydzbinfo.emis.trainRepair.mobile.model.MobileTaskPandect;

import java.text.ParseException;
import java.util.List;

/**
 * Description: 任务总单业务类
 * Author: wuyuechang
 * Create Date Time: 2021/4/26 14:10
 * Update Date Time: 2021/4/26 14:10
 *
 * @see
 */
public interface IPhoneTaskPandectService {

    /**
     * @author: wuyuechang
     * @Description: 手持机获取任务总单
     * @date: 2021/5/18 14:40
     */
    List<MobileTaskPandect> getPhoneTaskPandect(String unitCode, String workerId, String dayPlanID, List<String> deptCodeList, String isSelf) throws ParseException;

    /**
     * @author: wuyuechang
     * @Description: 手持机获取检修计划
     * @date: 2021/5/18 14:40
     */
    List<MobileTaskPandect> getMobileRepairPlan(String unitCode, String dayPlanID, String deptCode);
}
