package com.ydzbinfo.emis.trainRepair.workProcess.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.mobile.model.ProcessPersonInfo;
import com.ydzbinfo.emis.trainRepair.workProcess.dao.ProcessPersonMapper;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IProcessPersonService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPerson;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class ProcessPersonServiceImpl extends ServiceImpl<ProcessPersonMapper, ProcessPerson> implements IProcessPersonService {

    @Resource
    private ProcessPersonMapper processPersonMapper;

    @Override
    public List<ProcessPersonInfo> selectByTrainset(String stuffId, String trainsetId, String repairType, String dayplanId) {
        return processPersonMapper.selectByTrainset(stuffId, trainsetId, repairType, dayplanId);
    }

    /**
     * 根据辆序表主键集合查询要删除的人员表集合
     */
    @Override
    public List<ProcessPerson> getDelPersonList(List<String> processIdList){
        return processPersonMapper.getDelPersonList(processIdList);
    }
}
