package com.ydzbinfo.emis.common.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.taskAllot.dao.ZyMPlaceparttaskMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyMPlaceparttask;
import com.ydzbinfo.emis.common.taskAllot.service.IZyMPlaceparttaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: EMIS派工记录子表
 * @date: 2022/01/14
 * @author: 史艳涛
 */
@Service
public class ZyMPlaceparttaskServiceImpl  extends ServiceImpl<ZyMPlaceparttaskMapper, ZyMPlaceparttask> implements IZyMPlaceparttaskService {

    @Resource
    ZyMPlaceparttaskMapper zyMPlaceparttaskMapper;

    @Override
    public void deleteByParam(String dayplanid, String trainsetid, String deptCode, String packetCode, String itemCode){
        zyMPlaceparttaskMapper.deleteByParam(dayplanid, trainsetid, deptCode, packetCode, itemCode);
    }

    @Override
   public void deleteByTaskId(String dayplanid, String taskItemAllotId){
        zyMPlaceparttaskMapper.deleteByTaskId(dayplanid, taskItemAllotId);
    }
}
