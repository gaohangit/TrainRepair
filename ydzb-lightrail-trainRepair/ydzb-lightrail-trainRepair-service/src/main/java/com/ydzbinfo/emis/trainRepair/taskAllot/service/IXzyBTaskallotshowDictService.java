package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyBTaskallotshowDict;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public interface IXzyBTaskallotshowDictService extends IService<XzyBTaskallotshowDict> {

    List<XzyBTaskallotshowDict> getShowDictByTaskAllotType(String taskAllotType);

}
