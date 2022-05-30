package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfigDetail;
import lombok.Data;

import java.util.List;

/**
 * @author 高晗
 * @description 组织关键作业配置数据结构推送
 * @createDate 2021/11/16 16:00
 **/
@Data
public class KeyWorkWithDetail {

    private KeyWorkConfig keyWorkConfig;

    List<KeyWorkConfigDetail> keyWorkConfigDetails;
}
