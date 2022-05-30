package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkConfigInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkExtraColumnOption;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfig;

import java.util.List;

/**
 * <p>
 * 关键作业配置表 服务类
 * </p>
 *
 * @author 高晗
 * @since 2021-06-18
 */
public interface IKeyWorkConfigService extends IService<KeyWorkConfig> {
    /**
     * 查询关键作业配置(操作辆序)
     * @param unitCode
     * @param content
     * @return
     */
    List<KeyWorkConfigInfo> getKeyWorkConfigInfoForCarNo(String unitCode, String content);
    /**
     * 根据车型查关键作业配置
     */
    List<KeyWorkConfigInfo> getKeyWorkConfigInfoByTrainModel(String unitCode,String trainModel);

    /**
     *查询关键作业配置
     */
    List<KeyWorkConfigInfo> getKeyWorkConfigInfo(String unitCode, String content, String trainModel);

    /**
     * 修改关键作业配置
     */
    void setKeyWorkConfig(KeyWorkConfigInfo keyWorkConfig);

    /**
     * 删除关键作业配置
     */
    void delKeyWorkConfig(String id);

    void sendDelKeyWorkConfig(String id);

    /**
     * 新增关键作业配置
     */
    void addKeyWorkConfig(KeyWorkConfig keyWorkConfig);

    /**
     * 额外列对应的值
     */
    List<KeyWorkExtraColumnOption> getKeyWorkExtraColumnValueList(String columnKey,String trainModel,boolean showDelete);

    KeyWorkConfig getKeyWorkConfig(String trainModel,String content);


}
