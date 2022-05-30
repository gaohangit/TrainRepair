package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotTemplate;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public interface IXzyCOneallotTemplateService extends IService<XzyCOneallotTemplate> {

    List<XzyCOneallotTemplate> getTemplateListByParam(Map<String,String> map);

    List<XzyCOneallotTemplate> getAllTemplateByMarshalNum(String marshalNum);

}
