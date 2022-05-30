package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.PrWerDictMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.PowerDict;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.PrWerDictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/1 18:33
 **/
@Service
@Transactional
public class PrWerDictServiceImpl implements PrWerDictService {
    @Resource
    PrWerDictMapper prWerDictMapper;

    @Override
    public List<PowerDict> getPrWerDicts() {
        return prWerDictMapper.getPrWerDicts();
    }

    @Override
    public PowerDict getPrWerDict(PowerDict prWerDict) {
        return prWerDictMapper.getPrWerDict(prWerDict);
    }

    @Override
    public int addPrWerDict(PowerDict prWerDict) {
        return prWerDictMapper.addPrWerDict(prWerDict);
    }

    @Override
    public int delPrWerDict(PowerDict prWerDict) {
        return prWerDictMapper.delPrWerDict(prWerDict);
    }

    @Override
    public int updPrWerDict(PowerDict prWerDict) {
        return prWerDictMapper.updPrWerDict(prWerDict);
    }
}
