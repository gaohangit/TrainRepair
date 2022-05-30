package com.ydzbinfo.emis.trainRepair.workProcess.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationDetail;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
public interface IRfidCardSummaryService extends IService<RfidCardSummary> {

    /**
     * 查询作业时长明显
     *
     * @author: 吴跃常
     * @param trainsetType          车型
     * @param trainsetId            车组id
     * @param dayPlanId             日计划id
     */
    Page<DurationDetail> selectWorkerDetail(String trainsetType, String trainsetId, String dayPlanId, Integer pageNum, Integer pageSize);
}
