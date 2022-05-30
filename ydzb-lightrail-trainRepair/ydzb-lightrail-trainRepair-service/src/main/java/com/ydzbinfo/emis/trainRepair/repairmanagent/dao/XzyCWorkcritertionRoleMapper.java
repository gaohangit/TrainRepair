package com.ydzbinfo.emis.trainRepair.repairmanagent.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertionRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2020-07-21
 */
public interface XzyCWorkcritertionRoleMapper extends BaseMapper<XzyCWorkcritertionRole> {

    List<XzyCWorkcritertionRole> getByCritertionId(String critertionId);

    int add(XzyCWorkcritertionRole xzyCWorkcritertionRole);

    int updateByEntity(XzyCWorkcritertionRole xzyCWorkcritertionRole);

    int deleteByWorkcritertionId(@Param("xzyCWorkcritertionId") String xzyCWorkcritertionId);
}
