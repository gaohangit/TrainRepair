package com.ydzbinfo.emis.trainRepair.workProcess.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeQueryParamModel;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCycPersonInfo;
import com.ydzbinfo.emis.trainRepair.statistics.model.DurationEntity;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationInfo;
import com.ydzbinfo.emis.trainRepair.workProcess.dao.ProcessCarPartMapper;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IProcessCarPartService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessCarPart;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 冯帅
 * @since 2021-05-08
 */
@Service
public class ProcessCarPartServiceImpl extends ServiceImpl<ProcessCarPartMapper, ProcessCarPart> implements IProcessCarPartService {

    @Override
    public List<NoMainCycPersonInfo> selectCarPartItemList(Set<String> trainsetIds, String packetTypeCode, String dayPlanID, String workerType, List<String> itemCodes) {
        return baseMapper.selectCarPartItemList(trainsetIds, packetTypeCode, dayPlanID, workerType, itemCodes);
    }

    @Override
    public List<NoMainCycPersonInfo> selectCarPartEndItemList(String trainsetId,  String dayPlanID, String workerType, String itemCode, String staffId) {
        return baseMapper.selectCarPartEndItemList(trainsetId, dayPlanID, workerType, itemCode, staffId);

    }

    @Override
    public List<DurationInfo> selectStatisticsDuration(DurationEntity durationEntity) {
        return baseMapper.selectStatisticsDuration(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(durationEntity, DurationEntity::getItemName));
    }
}
