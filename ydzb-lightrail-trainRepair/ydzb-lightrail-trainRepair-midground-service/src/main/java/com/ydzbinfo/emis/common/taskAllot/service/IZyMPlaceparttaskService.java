package com.ydzbinfo.emis.common.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ZyMPlaceparttask;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
public interface IZyMPlaceparttaskService extends IService<ZyMPlaceparttask> {
    void deleteByParam(String dayplanid, String trainsetid, String deptCode, String packetCode, String itemCode);

    void deleteByTaskId(String dayplanid, String taskItemAllotId);
}
