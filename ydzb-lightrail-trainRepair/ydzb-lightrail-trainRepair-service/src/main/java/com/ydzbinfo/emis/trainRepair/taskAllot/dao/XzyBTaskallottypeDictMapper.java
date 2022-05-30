package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyBTaskallottypeDict;

import java.util.List;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public interface XzyBTaskallottypeDictMapper extends BaseMapper<XzyBTaskallottypeDict> {

    List<XzyBTaskallottypeDict> getTaskAllotTypeDict();

    XzyBTaskallottypeDict getTaskAllotTypeByCode(String code);


}
