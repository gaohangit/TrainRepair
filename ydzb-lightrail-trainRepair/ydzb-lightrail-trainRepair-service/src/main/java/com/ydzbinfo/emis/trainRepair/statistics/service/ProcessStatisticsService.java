package com.ydzbinfo.emis.trainRepair.statistics.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.statistics.model.DurationEntity;
import com.ydzbinfo.emis.trainRepair.statistics.model.DurationStatistics;
import com.ydzbinfo.emis.trainRepair.statistics.model.WarningStatistics;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationDetail;

/**
 * Description:     作业过程统计service
 * Author: 吴跃常
 * Create Date Time: 2021/6/22 11:03
 * Update Date Time: 2021/6/22 11:03
 *
 * @see
 */
public interface ProcessStatisticsService {

    /**
      * @author: 吴跃常
      * @Description: 查询作业时长统计
      * @date: 2021/6/22 14:27
      */
    DurationStatistics getStatisticsDuration(DurationEntity durationEntity);

    /**
     * 查询作业时长明显
     *
     * @author 吴跃常
     * @param trainsetType          车型
     * @param trainsetId            车组id
     * @param dayPlanId             日计划id
     */
    Page<DurationDetail> getDurationDetail(String trainsetType, String trainsetId, String dayPlanId, Integer pageSize, Integer pageNum);

    /**
     * 作业预警统计分析
     *
     * @author 吴跃常
     * @param durationEntity    统计查询字段
     */
    WarningStatistics getWorkWarning(DurationEntity durationEntity);
}
