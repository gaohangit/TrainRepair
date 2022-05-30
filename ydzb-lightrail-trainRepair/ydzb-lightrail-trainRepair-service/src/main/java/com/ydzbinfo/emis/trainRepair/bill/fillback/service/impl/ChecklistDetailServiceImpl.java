package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistDetailMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistDetailService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 记录单详细信息表 服务实现类
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Service
public class ChecklistDetailServiceImpl extends ServiceImpl<ChecklistDetailMapper, ChecklistDetail> implements IChecklistDetailService {

    @Resource
    ChecklistDetailMapper checklistDetailMapper;

    @Override
    @Transactional
    public void updateByPrimaryKey(List<ChecklistDetail> ds) {
        for(ChecklistDetail d : ds){
            checklistDetailMapper.updatePrimaryKey(d);
        }
    }
}
