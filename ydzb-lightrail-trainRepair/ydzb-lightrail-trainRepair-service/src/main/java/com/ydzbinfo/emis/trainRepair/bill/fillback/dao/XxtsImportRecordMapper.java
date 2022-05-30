package com.ydzbinfo.emis.trainRepair.bill.fillback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.XxtsImportRecord;

public interface XxtsImportRecordMapper extends BaseMapper<XxtsImportRecord> {
    boolean del(XxtsImportRecord importRecord);
}
