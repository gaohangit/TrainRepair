package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotTemplate;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public interface XzyCOneallotTemplateMapper extends BaseMapper<XzyCOneallotTemplate> {

    List<XzyCOneallotTemplate> getTemplateListByParam(Map<String,String> map);

}
