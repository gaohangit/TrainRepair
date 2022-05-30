package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.AxleWheelDataCurMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IAxleWheelDataCurService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.AxleWheelDataCur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AxleWheelDataCurServiceImpl extends ServiceImpl<AxleWheelDataCurMapper, AxleWheelDataCur> implements IAxleWheelDataCurService {

    @Resource
    AxleWheelDataCurMapper axleWheelDataCurMapper;

    /**
     * 更新数据集合
     * @param ds 数据集合
     */
    @Transactional
    public void update(List<AxleWheelDataCur> ds) {
        for (AxleWheelDataCur cur : ds) {
            axleWheelDataCurMapper.del(cur);
        }
        this.insertBatch(ds);
    }
}
