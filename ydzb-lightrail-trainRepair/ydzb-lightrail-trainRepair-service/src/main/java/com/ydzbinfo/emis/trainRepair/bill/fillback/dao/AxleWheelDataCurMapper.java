package com.ydzbinfo.emis.trainRepair.bill.fillback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.AxleWheelDataCur;

public interface AxleWheelDataCurMapper extends BaseMapper<AxleWheelDataCur> {

    boolean del(AxleWheelDataCur axleWheelDataCur);

    //void deleteList(List<AxleWheelDataCur> cs);
}
