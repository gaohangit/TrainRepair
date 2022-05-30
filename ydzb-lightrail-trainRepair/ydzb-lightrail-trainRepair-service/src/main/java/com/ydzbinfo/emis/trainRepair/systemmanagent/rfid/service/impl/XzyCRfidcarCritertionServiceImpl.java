package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.dao.XzyCRfidcarCritertionMapper;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidCritertion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarCritertion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.XzyCRfidcarCritertionService;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XzyCRfidcarCritertionServiceImpl extends ServiceImpl<XzyCRfidcarCritertionMapper, XzyCRfidcarCritertion> implements XzyCRfidcarCritertionService {

    @Override
    public Page<RfidCritertion> selectRfIdCriterion(Integer pageNum, Integer pageSize, String itemName, String trainsetSubType, String trainsetType, String repairPlaceCode) {
        Page<RfidCritertion> page = new Page<>(pageNum, pageSize);
        List<RfidCritertion> rfidCritertions = baseMapper.selectRfIdCriterion(MybatisOgnlUtils.replaceWildcardChars(itemName), trainsetSubType, trainsetType, repairPlaceCode, page);
        page.setRecords(rfidCritertions);
        return page;
    }
}
