package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowTypeWithComments;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 流程类型表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface FlowTypeMapper extends BaseMapper<FlowType> {
    /**
     * 获取流程类型
     * @return
     */

    List<FlowType> getFlowTypeInfos();

    /**
     * 获取流程类型配置和作业包配置
     * @param unitCode
     * @return
     */
    List<FlowTypeWithComments>  getFlowTypeWithComment(@Param("unitCode") String unitCode, @Param("codeList") List<String> codeList);

}
