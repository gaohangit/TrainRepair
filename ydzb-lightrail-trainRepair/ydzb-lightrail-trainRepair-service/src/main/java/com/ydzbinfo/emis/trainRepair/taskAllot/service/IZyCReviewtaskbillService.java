package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyCReviewtaskbill;

import java.util.List;

public interface IZyCReviewtaskbillService extends IService<ZyCReviewtaskbill> {
    List<ZyCReviewtaskbill> selectByFlag(String flag);
}
