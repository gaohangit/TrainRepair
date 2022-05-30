package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskAllotPersonQueryParamModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonPostEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotperson;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface XzyMTaskAllotPersonMapper {

	List<XzyMTaskallotperson> getTaskAllotPersonListByProcessId(String processId);

	int setTaskAllotPerson(XzyMTaskallotperson taskallotperson);

	int deletePerson(Map<String, String> map);
	
	
	List<XzyMTaskallotperson> getPersons(TaskAllotPersonQueryParamModel taskAllotPersonQueryParamModel);

	/**
	 * 获取人员岗位
	 */
	List<TaskAllotPersonPostEntity> getPersonPosts(@Param("list") List<String> personIds);

	List<XzyMTaskallotperson> getTaskAllotPerson(Map<String, String> map);

	List<XzyMTaskallotperson> getPersonByPacket(@Param("packetCode") String packetCode, @Param("dayPlanId")String dayPlanId, @Param("trainsetId")String trainsetId);

    List<XzyMTaskallotperson> getTaskAllotPersonListByProcessIds(List<String> processIds);
}
