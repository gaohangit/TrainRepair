package com.ydzbinfo.emis.common.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.taskAllot.dao.ZyMReviewtasksummaryMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyMReviewtasksummary;
import com.ydzbinfo.emis.common.taskAllot.service.IZyMReviewtasksummaryService;
import org.springframework.stereotype.Service;

/**
 * @description: EMIS复核记录单主表
 * @date: 2022/01/18
 * @author: 史艳涛
 */
@Service
public class ZyMReviewtasksummaryServiceImpl  extends ServiceImpl<ZyMReviewtasksummaryMapper, ZyMReviewtasksummary> implements IZyMReviewtasksummaryService {
}
