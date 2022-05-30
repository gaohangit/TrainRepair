package com.ydzbinfo.emis.trainRepair.workProcess.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.workProcess.dao.ProcessTimeRecordMapper;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IProcessTimeRecordService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessTimeRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProcessTimeRecordServiceImpl extends ServiceImpl<ProcessTimeRecordMapper, ProcessTimeRecord> implements IProcessTimeRecordService  {

    @Resource
    ProcessTimeRecordMapper processTimeRecordMapper;

    @Override
    public List<String> selectPersonId(String workerId, String trainsetId, String repairType,
                                                             String itemCode, String dayplanId) {
        return processTimeRecordMapper.selectPersonId(workerId,trainsetId,repairType,itemCode,dayplanId);
    }

    @Override
    public int addTimeRecord(ProcessTimeRecord processTimeRecord) {
        return processTimeRecordMapper.addTimeRecord(processTimeRecord);
    }

    /**
     * 根据人员表主键集合查询要删除的时间记录表集合
     */
    @Override
    public List<ProcessTimeRecord> getDelTimeRecordList(List<String> personIdList){
        return processTimeRecordMapper.getDelTimeRecordList(personIdList);
    }
}
