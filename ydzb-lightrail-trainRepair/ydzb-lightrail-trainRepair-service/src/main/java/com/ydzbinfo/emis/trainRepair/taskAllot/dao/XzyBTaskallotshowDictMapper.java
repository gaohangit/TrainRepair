package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyBTaskallotshowDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public interface XzyBTaskallotshowDictMapper extends BaseMapper<XzyBTaskallotshowDict> {

    List<XzyBTaskallotshowDict> getShowDictByTaskAllotType(@Param("taskAllotType") String taskAllotType);

}
