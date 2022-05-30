package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntitySJOverRunRecord;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @date: 2022/1/17
 * @author: 冯帅
 */
public interface IReCheckTaskService {
    List<EntitySJOverRunRecord> getReCheckTaskList(Map map);
}
