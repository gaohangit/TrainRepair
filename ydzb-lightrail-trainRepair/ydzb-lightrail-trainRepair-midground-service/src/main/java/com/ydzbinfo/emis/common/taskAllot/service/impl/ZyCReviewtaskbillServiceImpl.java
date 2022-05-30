package com.ydzbinfo.emis.common.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.taskAllot.dao.ZyCReviewtaskbillMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyCReviewtaskbill;
import com.ydzbinfo.emis.common.taskAllot.service.IZyCReviewtaskbillService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 获取复核任务配置字典
 * @date: 2022/01/18
 * @author: 史艳涛
 */
@Service
public class ZyCReviewtaskbillServiceImpl   extends ServiceImpl<ZyCReviewtaskbillMapper, ZyCReviewtaskbill> implements IZyCReviewtaskbillService {

    @Resource
    ZyCReviewtaskbillMapper zyCReviewtaskbillMapper;

    @Override
    public List<ZyCReviewtaskbill> selectByFlag(String flag){
        return zyCReviewtaskbillMapper.selectByFlag("1");
    }
}
