package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyCOneallotConfigMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyCOneallotTemplateMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotCarConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotTemplate;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyCOneallotConfigService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
@Service
public class XzyCOneallotConfigServiceImpl extends ServiceImpl<XzyCOneallotConfigMapper, XzyCOneallotConfig> implements IXzyCOneallotConfigService {

    @Autowired
    XzyCOneallotConfigMapper xzyCOneallotConfigMapper;
    @Autowired
    XzyCOneallotTemplateMapper xzyCOneallotTemplateMapper;

    @Override
    public List<XzyCOneallotConfig> getOneAllotConfig(Map<String, Object> map) {
        List<XzyCOneallotConfig> xzyCOneallotConfigList = xzyCOneallotConfigMapper.getOneAllotConfig(map);
        return xzyCOneallotConfigList;
    }

    @Override
    public int setOneAllotConfig(XzyCOneallotConfig oneallotConfig) {
        //先根据congfigId查询数据库是否有数据，有数据更新，无数据则插入
        int count = MybatisPlusUtils.selectCount(
            xzyCOneallotConfigMapper,
            eqParam(XzyCOneallotConfig::getsConfigid, oneallotConfig.getsConfigid())
        );
        if (!ObjectUtils.isEmpty(count) && count > 0) {
            return xzyCOneallotConfigMapper.updateOneAllotConfig(oneallotConfig);
        } else {
            return xzyCOneallotConfigMapper.setOneAllotConfig(oneallotConfig);
        }
        // if(StringUtils.isEmpty(oneallotConfig.getsConfigid())){
        //     oneallotConfig.setsConfigid(UUID.randomUUID().toString());
        //     return xzyCOneallotConfigMapper.setOneAllotConfig(oneallotConfig);
        // }else {
        //     return xzyCOneallotConfigMapper.updateOneAllotConfig(oneallotConfig);
        // }
    }

    @Override
    public List<XzyCOneallotCarConfig> getOneAllotConfigs(Map<String, String> map) {
        return xzyCOneallotConfigMapper.getOneAllotConfigs(map);
    }

    //一级修派工配置  派工列表的时候用
    @Override
    public List<XzyCOneallotCarConfig> getOneAllotConfigList(String unitCode, String deptCode) {
        return xzyCOneallotConfigMapper.getOneAllotConfigList(unitCode, deptCode);
    }

    @Override
    public List<XzyCOneallotTemplate> getOneAllotTemplates(Map<String, String> map) {
        return xzyCOneallotConfigMapper.getOneAllotTemplates(map);
    }
}
