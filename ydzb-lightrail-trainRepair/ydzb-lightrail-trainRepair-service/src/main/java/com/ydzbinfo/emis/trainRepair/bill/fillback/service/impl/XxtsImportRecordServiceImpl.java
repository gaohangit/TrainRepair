package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.XxtsImportRecordMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IXxtsImportRecordService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.XxtsImportRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class XxtsImportRecordServiceImpl extends ServiceImpl<XxtsImportRecordMapper, XxtsImportRecord> implements IXxtsImportRecordService {

    @Resource
    XxtsImportRecordMapper xxtsImportRecordMapper;
    /**
     * 更新导入信息
     * @param importRecord 导入记录
     */
    @Transactional
    void updateAxleEntity(XxtsImportRecord importRecord){
        xxtsImportRecordMapper.del(importRecord);
        this.insert(importRecord);
    }
}
