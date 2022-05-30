package com.ydzbinfo.emis.trainRepair.mobile.fault.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.mobile.fault.service.IPhoneFaultService;
import com.ydzbinfo.emis.trainRepair.mobile.model.FaultSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * Author: wuyuechang
 * Create Date Time: 2021/4/29 8:52
 * Update Date Time: 2021/4/29 8:52
 *
 * @see
 */
@Service
@Slf4j
public class PhoneFaultServiceImpl implements IPhoneFaultService {

    @Autowired
    private IRemoteService remoteService;

    @Override
    public void addFault(JSONObject jsonObject) {
        try {
            remoteService.addFault(jsonObject);
        } catch (Exception e) {
            throw new RuntimeException("调用添加故障接口异常", e);
        }
    }

    @Override
    public Page<JSONObject> faultSearch(FaultSearch faultSearch) {
        return remoteService.getFaultInfoPage(faultSearch);
    }

    @Override
    public void addFaultFind(JSONObject param) {
        try {
            remoteService.addFaultFind(param);
        } catch (Exception e) {
            throw new RuntimeException("调用添加已存在故障的故障发现信息接口异常", e);
        }
    }

    @Override
    public void addFaultDeal(JSONObject param) {
        try {
            remoteService.addFaultDeal(param);
        } catch (Exception e) {
            throw new RuntimeException("调用添加故障处理（故障回填）异常", e);
        }
    }
}
