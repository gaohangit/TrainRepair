package com.ydzbinfo.emis.trainRepair.repairmanagent.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertionRole;
import com.ydzbinfo.emis.trainRepair.repairmanagent.dao.XzyCWorkcritertionRoleMapper;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2020-07-21
 */
@Service
public class XzyCWorkcritertionRoleServiceImpl extends ServiceImpl<XzyCWorkcritertionRoleMapper, XzyCWorkcritertionRole> implements IXzyCWorkcritertionRoleService {

    @Autowired
    XzyCWorkcritertionRoleMapper xzyCWorkcritertionRoleMapper;

    @Override
    public List<XzyCWorkcritertionRole> getByCritertionId(String critertionId) {
        return xzyCWorkcritertionRoleMapper.getByCritertionId(critertionId);
    }

    @Override
    public int add(XzyCWorkcritertionRole xzyCWorkcritertionRole) {
        return xzyCWorkcritertionRoleMapper.add(xzyCWorkcritertionRole);
    }

    @Override
    public int updateByEntity(XzyCWorkcritertionRole xzyCWorkcritertionRole) {
        if(StringUtils.isNotEmpty(xzyCWorkcritertionRole.getsId())){
            return xzyCWorkcritertionRoleMapper.updateByEntity(xzyCWorkcritertionRole);
        }else{
            return xzyCWorkcritertionRoleMapper.add(xzyCWorkcritertionRole);
        }
    }

    @Override
    public int deleteByWorkcritertionId(String xzyCWorkcritertionId) {
        return xzyCWorkcritertionRoleMapper.deleteByWorkcritertionId(xzyCWorkcritertionId);
    }
}
