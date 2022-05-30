package com.ydzbinfo.emis.trainRepair.mobile.oftenfunction.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.mobile.model.HomeAllotTask;
import com.ydzbinfo.emis.trainRepair.mobile.querymodel.PhoneOftenFunction;

import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 韩旭
 * @since 2021-04-07
 */
public interface IPhoneOftenFunctionService extends IService<PhoneOftenFunction> {

    /**
     * @author: wuyuechang
     * @Description: 查询登录人的检修任务
     * @date: 2021/5/18 14:40
     */
    HomeAllotTask getMobileLoginUserRepairTask(String unitCode, String dayPlanID, String repairType, List<String> trainsetIDList, String deptCode, String staffID);

    /**
     * @author: wuyuechang
     * @Description: 获取常用功能
     * @date: 2021/5/18 14:40
     */
   List<String> getOftenFunction(String staffId,String type);

    /**
     * @author: wuyuechang
     * @Description: 设置常用功能
     * @date: 2021/5/18 14:40
     */
   void setOftenFunction(String staffId, String staffName, String code, List<String> moduleIDList);

}
