package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyCReviewtaskbill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZyCReviewtaskbillMapper extends BaseMapper<ZyCReviewtaskbill> {
    List<ZyCReviewtaskbill> selectByFlag(@Param("flag")String flag);
}
