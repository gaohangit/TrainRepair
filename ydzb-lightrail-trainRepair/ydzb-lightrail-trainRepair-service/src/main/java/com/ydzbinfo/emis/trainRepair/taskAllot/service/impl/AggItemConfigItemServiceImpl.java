package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.AggItemConfigItemMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfigItem;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAggItemConfigItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-17
 */
@Service
public class AggItemConfigItemServiceImpl extends ServiceImpl<AggItemConfigItemMapper, AggItemConfigItem> implements IAggItemConfigItemService {
    @Resource
    private AggItemConfigItemMapper aggItemConfigItemMapper;
    @Override
    public void delAggItemConfigItem(String id) {
        aggItemConfigItemMapper.delAggItemConfigItem(id);
    }
}
