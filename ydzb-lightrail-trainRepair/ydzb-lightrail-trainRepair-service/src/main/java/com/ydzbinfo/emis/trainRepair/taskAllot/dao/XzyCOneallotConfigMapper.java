package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotCarConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotTemplate;
import org.apache.ibatis.annotations.Param;

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
public interface XzyCOneallotConfigMapper extends BaseMapper<XzyCOneallotConfig> {

    List<XzyCOneallotConfig> getOneAllotConfig(Map<String,Object> map);

    int setOneAllotConfig(XzyCOneallotConfig oneallotConfig);

    int updateOneAllotConfig(XzyCOneallotConfig oneallotConfig);
    
    
    List<XzyCOneallotCarConfig> getOneAllotConfigs(Map<String,String> map);

    //一级修派工配置  派工列表的时候用
    List<XzyCOneallotCarConfig> getOneAllotConfigList(@Param("unitCode") String unitCode, @Param("deptCode") String deptCode);
    
    List<XzyCOneallotTemplate> getOneAllotTemplates(Map<String,String> map);

}
