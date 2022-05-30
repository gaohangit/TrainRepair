package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyMPlaceparttask;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
public interface ZyMPlaceparttaskMapper extends BaseMapper<ZyMPlaceparttask> {

    void deleteByParam(@Param("dayPlanId") String dayplanid, @Param("trainSetId") String trainsetid, @Param("deptCode") String deptCode, @Param("packetCode") String packetCode, @Param("itemCode") String itemCode);
}
