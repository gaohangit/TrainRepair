package com.ydzbinfo.emis.trainRepair.workProcess.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessTimeRecord;

import java.util.List;

public interface IProcessTimeRecordService extends IService<ProcessTimeRecord> {

    List<String> selectPersonId(String workerId, String trainsetId,
                                                      String repairType, String itemCode,
                                                      String dayplanId);

    int addTimeRecord(ProcessTimeRecord processTimeRecord);


    /**
     * 根据人员表主键集合查询要删除的时间记录表集合
     */
    List<ProcessTimeRecord> getDelTimeRecordList(List<String> personIdList);
}
