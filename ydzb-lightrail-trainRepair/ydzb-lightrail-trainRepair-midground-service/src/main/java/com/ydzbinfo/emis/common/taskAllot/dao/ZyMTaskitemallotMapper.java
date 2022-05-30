package com.ydzbinfo.emis.common.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyMTaskitemallot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
public interface ZyMTaskitemallotMapper  extends BaseMapper<ZyMTaskitemallot> {

    void deleteByParam(@Param("dayPlanId") String dayplanid, @Param("trainSetId") String trainsetid, @Param("deptCode") String deptCode, @Param("packetCode") String packetCode, @Param("itemCode") String itemCode);

    List<ZyMTaskitemallot> selectByCondition(@Param("dayPlanId") String dayplanid, @Param("deptCode") String deptCode);
}
