package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotCarConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotConfig;
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
public interface IXzyCOneallotConfigService extends IService<XzyCOneallotConfig> {

    List<XzyCOneallotConfig> getOneAllotConfig(Map<String,Object> map);

    int setOneAllotConfig(XzyCOneallotConfig oneallotConfig);
    
    
    List<XzyCOneallotCarConfig> getOneAllotConfigs(Map<String,String> map);

    //一级修派工配置  派工列表的时候用
    List<XzyCOneallotCarConfig> getOneAllotConfigList(String unitCode,String deptCode);
    
    List<XzyCOneallotTemplate> getOneAllotTemplates(Map<String,String> map);

}
