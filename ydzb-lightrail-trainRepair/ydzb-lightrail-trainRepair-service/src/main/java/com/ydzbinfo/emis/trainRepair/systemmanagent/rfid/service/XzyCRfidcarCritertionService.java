package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidCritertion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarCritertion;

public interface XzyCRfidcarCritertionService extends IService<XzyCRfidcarCritertion> {

    Page<RfidCritertion> selectRfIdCriterion(Integer pageNum, Integer pageSize, String itemName, String trainsetSubType, String trainsetType, String repairPlaceCode);
}
