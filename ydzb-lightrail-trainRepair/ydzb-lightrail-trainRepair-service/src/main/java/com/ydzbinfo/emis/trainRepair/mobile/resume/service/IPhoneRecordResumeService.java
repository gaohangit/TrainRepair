package com.ydzbinfo.emis.trainRepair.mobile.resume.service;

import com.alibaba.fastjson.JSONArray;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;

import java.util.List;

/**
 * Description: 履历业务接口
 * Author: wuyuechang
 * Create Date Time: 2021/4/29 8:51
 * Update Date Time: 2021/4/29 8:51
 *
 * @see
 */
public interface IPhoneRecordResumeService {

    List<TrainsetBaseInfo> getTrainsetListReceived(String unitCode);

    List<String> getCarno(String trainsetId);

    int getTrainsetAccMile(String trainsetId, String querydate);

    JSONArray getPartListOnTrainset(String trainsetId);

    JSONArray getBatchBomNodeListByCarNo(String trainsetId, String sCarNo);

    JSONArray partsTypeByName(String partsTypeName);
}
