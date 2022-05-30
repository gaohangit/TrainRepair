package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidPosition;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarPoistion;

public interface XzyCRfidcarPoistionService extends IService<XzyCRfidcarPoistion> {
    Page<RfidPosition> selectRfIdPosition(Integer pageNum, Integer pageSize, String tid, String trackCode, String placeCode, Integer carCount, String unitCode);
}
