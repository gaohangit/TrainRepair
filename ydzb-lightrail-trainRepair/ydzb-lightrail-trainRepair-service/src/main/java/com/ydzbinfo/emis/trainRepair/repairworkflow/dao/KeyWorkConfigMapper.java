package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.KeyWorkConfigWithDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 关键作业配置表 Mapper 接口
 * </p>
 *
 * @author 高晗
 * @since 2021-06-18
 */
public interface KeyWorkConfigMapper extends BaseMapper<KeyWorkConfig> {
    List<KeyWorkConfigWithDetail> getKeyWorkConfigWithDetails(@Param("unitCode") String unitCode, @Param("content") String content,@Param("trainModel")String trainModel);

}
