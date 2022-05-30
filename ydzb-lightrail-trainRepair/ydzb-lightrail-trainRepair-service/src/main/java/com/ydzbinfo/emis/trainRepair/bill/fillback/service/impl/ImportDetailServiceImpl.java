package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ImportDetailMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IImportDetailService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ImportDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ImportDetailServiceImpl extends ServiceImpl<ImportDetailMapper, ImportDetail> implements IImportDetailService {

    @Resource
    ImportDetailMapper importDetailMapper;

    @Transactional
    public void updateImportDetail(List<ImportDetail> importDetailList){
        for(ImportDetail detail : importDetailList){
            importDetailMapper.del(detail);
        }
        this.insertBatch(importDetailList);
    }
}
