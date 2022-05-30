package com.ydzbinfo.emis.trainRepair.workProcessMonitor.dao;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.PersonPost;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntityBase;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessData;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPerson;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/24 14:53
 * @Description:
 */
public interface ProcessMonitorMapper {

    List<PersonPost> getPersonPostList(@Param("list") List<String> postIds);

    List<ProcessData> getIntegrationProcess(@Param("unitCode") String unitCode, @Param("dayPlanId") String dayPlanId, @Param("trainsetId") String trainsetId);

    List<ProcessData> getTwoProcess(@Param("unitCode") String unitCode, @Param("dayPlanId") String dayPlanId,@Param("trainsetId") String trainsetId,@Param("packetCode") String packetCode);

    List<XzyMTaskcarpart> getFaultAllot(@Param("unitCode") String unitCode,@Param("dayPlanId") String dayPlanId,@Param("trainsetId") String trainsetId);

    List<ProcessPerson> getPersonByDayPlanIdAndPersonId(@Param("unitCode") String unitCode, @Param("dayPlanId") String dayPlanId);

    List<PersonPost> selectPersonPostList(@Param("unitCode") String unitCode,@Param("trainsetId") String trainsetId,@Param("postIds") List<String> postIds, @Param("dayPlanId") String dayPlanId);

    List<TrainsetPositionEntityBase> getOneWorkProcessMonitorConfig(@Param("trackCode") List<Integer> trackCode);

}
