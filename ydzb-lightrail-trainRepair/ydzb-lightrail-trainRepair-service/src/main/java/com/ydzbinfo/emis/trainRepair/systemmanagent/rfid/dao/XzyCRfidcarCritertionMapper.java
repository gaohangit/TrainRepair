package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidCritertion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarCritertion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XzyCRfidcarCritertionMapper extends BaseMapper<XzyCRfidcarCritertion> {

    List<RfidCritertion> selectRfIdCriterion(@Param("itemName") String itemName,
                                             @Param("trainsetSubType")String trainsetSubType,
                                             @Param("trainsetType")String trainsetType,
                                             @Param("repairPlaceCode")String repairPlaceCode,
                                             @Param("page")Page page);
}
