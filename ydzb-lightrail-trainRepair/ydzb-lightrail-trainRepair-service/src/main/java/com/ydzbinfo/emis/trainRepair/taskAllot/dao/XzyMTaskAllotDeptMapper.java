package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotdept;

import java.util.List;
import java.util.Map;

public interface XzyMTaskAllotDeptMapper {

	List<XzyMTaskallotdept> getTaskAllotDeptByCarPart(String processId);

	int setTaskAllotDept(XzyMTaskallotdept taskallotdept);

	int deleteDepts(Map<String, String> map);

	List<XzyMTaskallotdept> getTaskAllotDepts(Map<String, String> map);

	XzyMTaskallotdept getOneDeptById(String id);
}
