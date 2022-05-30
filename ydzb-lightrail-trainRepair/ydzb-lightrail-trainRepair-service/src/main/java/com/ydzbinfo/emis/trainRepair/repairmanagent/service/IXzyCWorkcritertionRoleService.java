package com.ydzbinfo.emis.trainRepair.repairmanagent.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertionRole;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2020-07-21
 */
public interface IXzyCWorkcritertionRoleService extends IService<XzyCWorkcritertionRole> {

    List<XzyCWorkcritertionRole> getByCritertionId(String critertionId);

    int add(XzyCWorkcritertionRole xzyCWorkcritertionRole);

    int updateByEntity(XzyCWorkcritertionRole xzyCWorkcritertionRole);

    int deleteByWorkcritertionId(String deleteByWorkcritertionId);

}
