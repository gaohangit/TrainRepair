package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotdept;

import java.util.List;
import java.util.Map;

public interface XzyMTaskAllotDeptService {

    List<XzyMTaskallotdept> getTaskAllotDeptByCarPart(String processId);
    
    int deleteDepts(Map<String,String> map);
    
    List<XzyMTaskallotdept> getTaskAllotDepts(Map<String, String> map);

    XzyMTaskallotdept getOneDeptById(String id);
}
