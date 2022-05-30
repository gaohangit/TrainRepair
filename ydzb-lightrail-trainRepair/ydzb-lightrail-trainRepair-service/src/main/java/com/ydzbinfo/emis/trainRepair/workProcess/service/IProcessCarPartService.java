package com.ydzbinfo.emis.trainRepair.workProcess.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCycPersonInfo;
import com.ydzbinfo.emis.trainRepair.statistics.model.DurationEntity;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationInfo;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessCarPart;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
public interface IProcessCarPartService extends IService<ProcessCarPart> {

    /**
      * @author: wuyuechang
      * @Description:   查询无修程项目确认完成时间
      * @date: 2021/05/14 11:03
      */
    List<NoMainCycPersonInfo> selectCarPartItemList(Set<String> trainsetIds, String packetTypeCode, String dayPlanID, String workerType, List<String> itemCodes);

    /**
      * @author: wuyuechang
      * @Description:   查询无修程项目确认完成时间
      * @date: 2021/05/14 11:03
      */
    List<NoMainCycPersonInfo> selectCarPartEndItemList(String trainsetId, String dayPlanID, String workerType, String itemCode, String staffId);

    /**
      * @author: 吴跃常
      * @Description: 查询作业时长统计项目
      * @date: 2021/06/23 11:10
      */
    List<DurationInfo> selectStatisticsDuration(DurationEntity durationEntity);
}
