package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.KeyWorkMonitorConfigMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkMonitorConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkMonitorConfigService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateCurService;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.UserUtil;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 关键作业大屏配置 服务实现类
 * </p>
 *
 * @author 张天可
 * @since 2021-08-09
 */
@Service
public class KeyWorkMonitorConfigServiceImpl extends ServiceImpl<KeyWorkMonitorConfigMapper, KeyWorkMonitorConfig> implements IKeyWorkMonitorConfigService {

    @Autowired
    TrackPowerStateCurService trackPowerStateCurService;

    @Autowired
    KeyWorkMonitorConfigMapper keyWorkMonitorConfigMapper;

    @Override
    public List<KeyWorkMonitorConfig> getKeyWorkMonitorConfigs() {
        String unitCode = ContextUtils.getUnitCode();
        //查询库中已有的股道配置
        List<KeyWorkMonitorConfig> keyWorkMonitorConfigs = MybatisPlusUtils.selectList(
            keyWorkMonitorConfigMapper,
            eqParam(KeyWorkMonitorConfig::getUnitCode, unitCode)
        );
        keyWorkMonitorConfigs.sort(Comparator.comparing(KeyWorkMonitorConfig::getSortId));
        return keyWorkMonitorConfigs;
    }

    @Override
    public void setKeyWorkMonitorConfigs(List<KeyWorkMonitorConfig> keyWorkMonitorConfigs) {
        String unitCode = ContextUtils.getUnitCode();
        String staffId = UserUtil.getUserInfo().getStaffId();
        int sort = 0;
        for (KeyWorkMonitorConfig keyWorkMonitorConfig : keyWorkMonitorConfigs) {
            keyWorkMonitorConfig.setUnitCode(unitCode);
            keyWorkMonitorConfig.setStaffId(staffId);
            keyWorkMonitorConfig.setSortId(sort);
            keyWorkMonitorConfig.setParamValue(StringUtils.isBlank(keyWorkMonitorConfig.getParamValue()) ? "0" : keyWorkMonitorConfig.getParamValue());
            if (StringUtils.isNotBlank(keyWorkMonitorConfig.getId())) {
                this.updateById(keyWorkMonitorConfig);
            } else {
                this.insert(keyWorkMonitorConfig);
            }
            sort++;
        }
    }
}
