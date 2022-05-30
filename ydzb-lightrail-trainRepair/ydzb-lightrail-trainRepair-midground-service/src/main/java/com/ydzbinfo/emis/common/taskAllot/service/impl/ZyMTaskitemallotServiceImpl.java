package com.ydzbinfo.emis.common.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.taskAllot.dao.ZyMTaskitemallotMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyMTaskitemallot;
import com.ydzbinfo.emis.common.taskAllot.service.IZyMTaskitemallotService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: EMIS派工记录主表
 * @date: 2022/01/14
 * @author: 史艳涛
 */
@Service
public class ZyMTaskitemallotServiceImpl   extends ServiceImpl<ZyMTaskitemallotMapper, ZyMTaskitemallot>  implements IZyMTaskitemallotService {

    @Resource
    ZyMTaskitemallotMapper zyMTaskitemallotMapper;

    @Override
    public void deleteByParam(List<ZyMTaskitemallot> lstTaskItems){
        for(ZyMTaskitemallot t : lstTaskItems){
            zyMTaskitemallotMapper.deleteByParam(t.getDayplanid(), t.getTrainsetid(), t.getDeptcode(), t.getSppacketcode(), t.getSplpacketitemcode());
        }
    }
    @Override
    public List<ZyMTaskitemallot> selectByCondition(String dayplanid, String deptCode){
        return zyMTaskitemallotMapper.selectByCondition(dayplanid, deptCode);
    }
}
