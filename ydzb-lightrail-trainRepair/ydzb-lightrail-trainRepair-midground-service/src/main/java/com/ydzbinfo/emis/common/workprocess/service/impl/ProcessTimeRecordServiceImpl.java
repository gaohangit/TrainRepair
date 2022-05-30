package com.ydzbinfo.emis.common.workprocess.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.workprocess.dao.ProcessTimeRecordMapper;
import com.ydzbinfo.emis.common.workprocess.service.IProcessTimeRecordService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessTimeRecord;
import org.springframework.stereotype.Service;
@Service
public class ProcessTimeRecordServiceImpl extends ServiceImpl<ProcessTimeRecordMapper, ProcessTimeRecord> implements IProcessTimeRecordService {

}
