package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.KeyWorkConfigDetailMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfigDetail;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkConfigDetailService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 关键作业配置详情表 服务实现类
 * </p>
 *
 * @author 高晗
 * @since 2021-06-18
 */
@Service
public class KeyWorkConfigDetailServiceImpl extends ServiceImpl<KeyWorkConfigDetailMapper, KeyWorkConfigDetail> implements IKeyWorkConfigDetailService {
    @Autowired
    KeyWorkConfigDetailMapper keyWorkConfigDetailMapper;

    @Override
    public void delKeyWorkConfigDetail(String id) {
        MybatisPlusUtils.delete(
            keyWorkConfigDetailMapper,
            eqParam(KeyWorkConfigDetail::getKeyWorkConfigId, id)
        );
    }

    @Override
    public void addKeyWorkConfigDetail(KeyWorkConfigDetail keyWorkConfigDetail) {
        keyWorkConfigDetailMapper.insert(keyWorkConfigDetail);
    }
}
