package com.ydzbinfo.emis.trainRepair.workProcess.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCycPersonInfo;
import com.ydzbinfo.emis.trainRepair.statistics.model.DurationEntity;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationInfo;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessCarPart;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
public interface ProcessCarPartMapper extends BaseMapper<ProcessCarPart> {

    List<NoMainCycPersonInfo> selectCarPartItemList(@Param("trainsetIds") Set<String> trainsetIds,
                                                    @Param("packetTypeCode") String packetTypeCode,
                                                    @Param("dayPlanID") String dayPlanID,
                                                    @Param("workerType") String workerType,
                                                    @Param("itemCodes") List<String> itemCodes);

    List<NoMainCycPersonInfo> selectCarPartEndItemList(@Param("trainsetId") String trainsetId,
                                                       @Param("dayPlanID") String dayPlanID,
                                                       @Param("workerType") String workerType,
                                                       @Param("itemCode") String itemCode,
                                                       @Param("staffId") String staffId);

    List<DurationInfo> selectStatisticsDuration(@Param("durationEntity") DurationEntity durationEntity);
}
