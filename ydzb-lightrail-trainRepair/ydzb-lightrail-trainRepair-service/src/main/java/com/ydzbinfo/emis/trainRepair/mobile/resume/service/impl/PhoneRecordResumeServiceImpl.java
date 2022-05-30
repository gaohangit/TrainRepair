package com.ydzbinfo.emis.trainRepair.mobile.resume.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.mobile.resume.service.IPhoneRecordResumeService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 履历业务实现
 * Author: wuyuechang
 * Create Date Time: 2021/4/29 8:52
 * Update Date Time: 2021/4/29 8:52
 *
 * @see
 */
@Service
public class PhoneRecordResumeServiceImpl implements IPhoneRecordResumeService {

    @Autowired
    private IRemoteService remoteService;

    @Override
    public List<TrainsetBaseInfo> getTrainsetListReceived(String unitCode) {
        return remoteService.getTrainsetListReceived(unitCode);
    }

    @Override
    public List<String> getCarno(String trainsetId) {
        return remoteService.getCarno(trainsetId);
    }

    @Override
    public int getTrainsetAccMile(String trainsetId, String querydate) {
        return remoteService.getTrainsetAccMile(trainsetId, querydate);
    }

    @Override
    public JSONArray getPartListOnTrainset(String trainsetId) {
        return remoteService.getPartListOnTrainset(trainsetId);
    }

    @Override
    public JSONArray getBatchBomNodeListByCarNo(String trainsetId, String sCarNo) {
        return remoteService.getBatchBomNodeListByCarNo(trainsetId, sCarNo);
    }

    @Override
    public JSONArray partsTypeByName(String partsTypeName) {
        return remoteService.partsTypeByName(partsTypeName);
    }
}
