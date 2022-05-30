package com.ydzbinfo.emis.trainRepair.mobile.fault.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.mobile.model.FaultSearch;

/**
 * Description:
 * Author: wuyuechang
 * Create Date Time: 2021/4/29 8:51
 * Update Date Time: 2021/4/29 8:51
 *
 * @see
 */
public interface IPhoneFaultService {
    void addFault(JSONObject jsonObject);

    Page<JSONObject> faultSearch(FaultSearch faultSearch);

    void addFaultFind(JSONObject param);

    void addFaultDeal(JSONObject param);
}
