package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotBranchConfig;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-03-23
 */
public interface IAllotBranchConfigService extends IService<AllotBranchConfig> {
    Map getGroup(String deptCode);
}
