package com.ydzbinfo.emis.trainRepair.taskAllot.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.RecheckTaskProperties;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntityLYOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntitySJOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.JsonRootBean;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyCReviewtaskbill;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.HttpUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class TaskCommon {

    protected static final Logger logger = getLogger(BillCommon.class);
    @Autowired
    private IRemoteService iRemoteService;
    @Autowired
    RecheckTaskProperties recheckTaskProperties;
    /**
     * 获取车组信息
     * @param trainsetId 车组ID
     * @return
     */
    public TrainsetBaseInfo getTrainsetInfo(String trainsetId) {
        try {
            List<String> strList = new ArrayList<>();
            strList.add(trainsetId);
            return CacheUtil.getDataUseThreadCache("TaskCommon.getTrainsetInfo" + strList, () -> {
                List<TrainsetBaseInfo> infos = iRemoteService.getTrainsetBaseInfoByIds(strList);
                if (infos.size() > 0) {
                    return infos.get(0);
                }
                return new TrainsetBaseInfo();
            });
        } catch (Exception ex) {
            logger.error("获取车组信息", ex);
            return null;
        }
    }
    /**
     * 根据车组ID获取车组详细编组信息
     * @param trainsetId 车组ID
     * @return 编组集合
     */
    public List<String> getTrainMarshlType(String trainsetId) {
        List<String> lstCarNos = new ArrayList<String>();
        try {
            return CacheUtil.getDataUseThreadCache("TaskCommon.getTrainMarshlType_" + trainsetId, () -> {
                TrainsetInfo trainsetInfo = iRemoteService.getTrainsetDetialInfo(trainsetId);
                int marshalCount = Integer.parseInt(trainsetInfo.getIMarshalcount());
                for (int i = 1; i < marshalCount; i++) {
                    String carNo = String.valueOf(i);
                    if (carNo.length() == 1)
                        carNo = "0" + carNo;
                    lstCarNos.add(carNo);
                }
                lstCarNos.add("00");
                return lstCarNos;
            });
        } catch (Exception e) {
            logger.error("根据车组ID获取车组详细编组信息", e);
        }
        return lstCarNos;
    }
}
