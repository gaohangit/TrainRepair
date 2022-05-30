package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.TrainsetImageDictMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetImageDict;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetImageDictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/2 9:03
 **/
@Service
@Transactional
public class TrainsetImageDictServiceImpl implements TrainsetImageDictService  {
    @Resource
    TrainsetImageDictMapper trainsetImageDictMapper;

    @Override
    public List<TrainsetImageDict> getTrainsetImageDictList(String trainTyp) {
        return trainsetImageDictMapper.getTrainsetImageDictList(trainTyp);
    }

    @Override
    public TrainsetImageDict getTrainsetImageDict(TrainsetImageDict trainsetImageDict) {
        return trainsetImageDictMapper.getTrainsetImageDict(trainsetImageDict);
    }

    @Override
    public int addTrainsetImageDict(TrainsetImageDict trainsetImageDict) {
        return trainsetImageDictMapper.addTrainsetImageDict(trainsetImageDict);
    }

    @Override
    public int updTrainsetImageDict(TrainsetImageDict trainsetImageDict) {
        return trainsetImageDictMapper.updTrainsetImageDict(trainsetImageDict);
    }

    @Override
    public int delTrainsetImageDict(TrainsetImageDict trainsetImageDict) {
        return trainsetImageDictMapper.delTrainsetImageDict(trainsetImageDict);
    }
}
