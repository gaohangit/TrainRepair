package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ImportRecordMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IImportRecordService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ImportRecord;
import org.springframework.stereotype.Service;

@Service
public class ImportRecordServiceImpl extends ServiceImpl<ImportRecordMapper, ImportRecord> implements IImportRecordService {
}
