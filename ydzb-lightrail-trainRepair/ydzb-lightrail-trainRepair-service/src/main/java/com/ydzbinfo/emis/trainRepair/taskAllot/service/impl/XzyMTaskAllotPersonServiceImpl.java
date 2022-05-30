package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyMTaskAllotPersonMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskAllotPersonQueryParamModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonPostEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotperson;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class XzyMTaskAllotPersonServiceImpl implements XzyMTaskAllotPersonService {

    @Autowired
    XzyMTaskAllotPersonMapper taskAllotPersonMapper;

    @Override
    public List<XzyMTaskallotperson> getTaskAllotPersonListByProcessId(String processId) {
        return taskAllotPersonMapper.getTaskAllotPersonListByProcessId(processId);
    }

	@Override
	public int deletePerson(Map<String, String> map) {
		return taskAllotPersonMapper.deletePerson(map);
	}

	@Override
	public List<XzyMTaskallotperson> getPersons(TaskAllotPersonQueryParamModel taskAllotPersonQueryParamModel) {
		return taskAllotPersonMapper.getPersons(taskAllotPersonQueryParamModel);
	}

	@Override
	public List<TaskAllotPersonPostEntity> getPersonPosts(List<String> personids) {
		return taskAllotPersonMapper.getPersonPosts(personids);
	}

	@Override
	public List<XzyMTaskallotperson> getTaskAllotPerson(Map<String,String> map) {
		return taskAllotPersonMapper.getTaskAllotPerson(map);
	}

	@Override
	public List<XzyMTaskallotperson> getPersonByPacket(String packetCode, String dayPlanId, String trainsetId) {
		return taskAllotPersonMapper.getPersonByPacket(packetCode,dayPlanId,trainsetId);
	}

	@Override
	public List<XzyMTaskallotperson> getTaskAllotPersonListByProcessIds(List<String> processIds) {
		return taskAllotPersonMapper.getTaskAllotPersonListByProcessIds(processIds);
	}
}
