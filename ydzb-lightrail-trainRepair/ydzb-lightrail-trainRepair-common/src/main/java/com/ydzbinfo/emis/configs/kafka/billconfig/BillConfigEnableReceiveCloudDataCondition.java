package com.ydzbinfo.emis.configs.kafka.billconfig;

import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;

/**
 * @author 张天可
 * @since 2021/11/25
 */
public class BillConfigEnableReceiveCloudDataCondition extends SpringCloudStreamUtil.EnableReceiveCloudDataConditionBase {
    @Override
    protected SpringCloudStreamModuleEnum getModule() {
        return SpringCloudStreamModuleEnum.BILL_CONFIG;
    }
}
