package com.ydzbinfo.emis.trainRepair.workProcess.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationDetail;
import com.ydzbinfo.emis.trainRepair.workProcess.dao.RfidCardSummaryMapper;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IRfidCardSummaryService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
@Service
public class RfidCardSummaryServiceImpl extends ServiceImpl<RfidCardSummaryMapper, RfidCardSummary> implements IRfidCardSummaryService {

    @Override
    public Page<DurationDetail> selectWorkerDetail(String trainsetType, String trainsetId, String dayPlanId, Integer pageNum, Integer pageSize) {
        Page<DurationDetail> durationDetailPage = new Page<>(pageNum, pageSize);
        List<DurationDetail> durationDetails = baseMapper.  selectWorkerDetail(trainsetType, trainsetId, dayPlanId, durationDetailPage);
        return durationDetailPage.setRecords(durationDetails);
    }
}
