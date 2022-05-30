package com.ydzbinfo.emis.trainRepair.workProcess.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.mobile.model.ProcessPersonInfo;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPerson;
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
public interface ProcessPersonMapper extends BaseMapper<ProcessPerson> {

    List<ProcessPersonInfo> selectByTrainset(@Param("stuffId") String stuffId,
                                             @Param("trainsetId") String trainsetId,
                                             @Param("repairType") String repairType,
                                             @Param("dayplanId") String dayplanId);

    /**
     * 根据辆序表主键集合查询要删除的人员表集合
     */
    List<ProcessPerson> getDelPersonList(List<String> processIdList);

}
