package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.AggItemConfigModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfig;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-03-17
 */
public interface IAggItemConfigService extends IService<AggItemConfig> {

    List<AggItemConfigModel> getAggItemConfigList( AggItemConfigModel aggItemConfig);

    void addAggItemConfig(AggItemConfigModel aggItemConfig);
    boolean addAggConfigModel(AggItemConfigModel aggItemConfig);
    boolean sendTwoCreateData(AggItemConfigModel aggItemConfig);

    boolean delAggItemConfig(AggItemConfig aggItemConfig);
    boolean sendTwoDeleteData(AggItemConfig aggItemConfig);

    void updAggItemConfig(AggItemConfigModel aggItemConfig);
    boolean updateAggConfigModel(AggItemConfigModel aggItemConfigModel);
    boolean sendTwoUpdateData(AggItemConfigModel aggItemConfigModel);

}
