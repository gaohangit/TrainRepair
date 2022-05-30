package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyMTaskAllotDeptMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotdept;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class XzyMTaskAllotDeptServiceImpl implements XzyMTaskAllotDeptService {

    @Autowired
    XzyMTaskAllotDeptMapper taskAllotDeptMapper;

    @Override
    public List<XzyMTaskallotdept> getTaskAllotDeptByCarPart(String processId) {
        return taskAllotDeptMapper.getTaskAllotDeptByCarPart(processId);
    }

	@Override
	public int deleteDepts(Map<String, String> map) {
		return taskAllotDeptMapper.deleteDepts(map);
	}

	@Override
	public List<XzyMTaskallotdept> getTaskAllotDepts(Map<String, String> map) {
		return taskAllotDeptMapper.getTaskAllotDepts(map);
	}

	@Override
	public XzyMTaskallotdept getOneDeptById(String id) {
		return taskAllotDeptMapper.getOneDeptById(id);
	}
}
