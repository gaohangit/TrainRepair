package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskAllotPersonQueryParamModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonPostEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotperson;

import java.util.List;
import java.util.Map;

public interface XzyMTaskAllotPersonService {

    List<XzyMTaskallotperson> getTaskAllotPersonListByProcessId(String processId);
    int deletePerson(Map<String, String> map);
    
	List<XzyMTaskallotperson> getPersons(TaskAllotPersonQueryParamModel taskAllotPersonQueryParamModel);

    List<TaskAllotPersonPostEntity> getPersonPosts(List<String> personids);

    List<XzyMTaskallotperson> getTaskAllotPerson(Map<String,String> map);

    List<XzyMTaskallotperson> getPersonByPacket(String packetCode,String dayPlanId,String trainsetId);

    List<XzyMTaskallotperson> getTaskAllotPersonListByProcessIds(List<String> processIds);
}
