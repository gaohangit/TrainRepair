package com.ydzbinfo.emis.common.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.taskAllot.dao.PzMTaskApplyMapper;
import com.ydzbinfo.emis.common.taskAllot.service.IPzMTaskApplyService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.PzMTaskApply;
import org.springframework.stereotype.Service;

@Service
public class PzMTaskApplyServiceImpl extends ServiceImpl<PzMTaskApplyMapper, PzMTaskApply> implements IPzMTaskApplyService {
}
