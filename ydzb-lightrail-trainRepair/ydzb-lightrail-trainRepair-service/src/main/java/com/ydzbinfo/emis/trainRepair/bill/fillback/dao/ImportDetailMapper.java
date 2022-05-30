package com.ydzbinfo.emis.trainRepair.bill.fillback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ImportDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportDetailMapper  extends BaseMapper<ImportDetail> {

    boolean del(ImportDetail detail);
}
