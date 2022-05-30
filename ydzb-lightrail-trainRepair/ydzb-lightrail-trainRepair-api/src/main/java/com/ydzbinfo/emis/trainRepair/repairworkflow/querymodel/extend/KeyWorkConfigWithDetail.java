package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfigDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 高晗
 * @description
 * @createDate 2021/6/19 15:53
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyWorkConfigWithDetail extends KeyWorkConfig {
    private List<KeyWorkConfigDetail> keyWorkConfigDetails;
}
