package com.ydzbinfo.emis.trainRepair.workProcess.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationDetail;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
public interface RfidCardSummaryMapper extends BaseMapper<RfidCardSummary> {

    List<DurationDetail> selectWorkerDetail(@Param("trainsetType") String trainsetType,
                                            @Param("trainsetId") String trainsetId,
                                            @Param("dayPlanId") String dayPlanId,
                                            @Param("durationDetailPage") Page<DurationDetail> durationDetailPage);
}
