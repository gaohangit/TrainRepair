package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.AllotPersonConfigMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotPersonConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAllotPersonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-23
 */
@Service
public class AllotPersonConfigServiceImpl extends ServiceImpl<AllotPersonConfigMapper, AllotPersonConfig> implements IAllotPersonConfigService {
    @Autowired
    AllotPersonConfigMapper allotpersonConfigMapper;
    @Override
    public void updAllotPersonConfig(AllotPersonConfig allotPersonConfig) {
        allotpersonConfigMapper.updAllotPersonConfig(allotPersonConfig);
    }

}
