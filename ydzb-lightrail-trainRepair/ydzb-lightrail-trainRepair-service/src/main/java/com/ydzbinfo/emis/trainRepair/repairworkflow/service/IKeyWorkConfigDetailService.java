package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfigDetail;

/**
 * <p>
 * 关键作业配置详情表 服务类
 * </p>
 *
 * @author 高晗
 * @since 2021-06-18
 */
public interface IKeyWorkConfigDetailService extends IService<KeyWorkConfigDetail> {

    void delKeyWorkConfigDetail(String id);

    void addKeyWorkConfigDetail(KeyWorkConfigDetail keyWorkConfigDetail);

}
