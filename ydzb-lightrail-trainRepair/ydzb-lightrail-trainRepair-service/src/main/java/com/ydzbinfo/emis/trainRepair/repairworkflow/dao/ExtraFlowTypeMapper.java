package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ExtraFlowTypeWithPackets;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 额外流程类型表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-05-08
 */
public interface ExtraFlowTypeMapper extends BaseMapper<ExtraFlowType> {

    List<ExtraFlowTypeWithPackets> getExtraFlowTypeWithPacket(@Param("unitCode") String unitCode,@Param("codeList") List<String> codeList,@Param("parentFlowTypeCode")String parentFlowTypeCode);

    ExtraFlowType getExtraFlowTypeByCode(@Param("code") String code);

    Set<String> getPacketCodeForConfigurablePacketList(@Param("unitCode") String unitCode, @Param("parentFlowTypeCode") String parentFlowTypeCode, @Param("flowTypeCode") String flowTypeCode);

    List<ExtraFlowType> getExtraFlowTypeList(@Param("code")String code,@Param("unitCode")String unitCode);

    List<ExtraFlowType> getPacketIndependent(@Param("packetCode") String packetCode, @Param("unitCode")String unitCode);

    List<ExtraFlowTypeWithPackets> getPacketNarrow(@Param("unitCode") String unitCode);

    List<ExtraFlowType> getExtraFlowTypeByUnitCode(@Param("unitCode") String unitCode);
}
