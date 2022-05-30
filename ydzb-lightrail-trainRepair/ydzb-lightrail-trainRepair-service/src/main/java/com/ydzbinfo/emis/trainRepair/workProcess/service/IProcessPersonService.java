package com.ydzbinfo.emis.trainRepair.workProcess.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.mobile.model.ProcessPersonInfo;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPerson;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
public interface IProcessPersonService extends IService<ProcessPerson> {

    /**
      * @author: wuyuechang
      * @Description:       查询车组下作业过程人员
      * @param: 员工id， 车组id， 检修类型， 日计划
      * @return 作业人员集合
      * @date: 2021/5/12 16:26
      */
    List<ProcessPersonInfo> selectByTrainset(String stuffId, String trainsetId, String repairType, String dayplanId);

    /**
     * 根据辆序表主键集合查询要删除的人员表集合
     */
    List<ProcessPerson> getDelPersonList(List<String> processIdList);
}
