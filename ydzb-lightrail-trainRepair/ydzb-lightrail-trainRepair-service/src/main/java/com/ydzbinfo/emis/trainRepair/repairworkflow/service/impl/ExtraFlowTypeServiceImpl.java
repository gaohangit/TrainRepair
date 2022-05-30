package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ExtraFlowTypeMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ExtraFlowTypePacketMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowTypePacket;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ExtraFlowTypeWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypeService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 额外流程类型表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-28
 */
@Transactional
@Service
public class ExtraFlowTypeServiceImpl extends ServiceImpl<ExtraFlowTypeMapper, ExtraFlowType> implements IExtraFlowTypeService {
    @Resource
    ExtraFlowTypeMapper extraFlowTypeMapper;

    @Resource
    ExtraFlowTypePacketMapper extraFlowTypePacketMapper;

    @Override
    public List<ExtraFlowTypeWithPackets> getExtraFlowTypeWithPacket(String unitCode, List<String> codeList, String parentFlowTypeCode) {
        return extraFlowTypeMapper.getExtraFlowTypeWithPacket(unitCode, codeList, parentFlowTypeCode);
    }

    @Override
    public void setExtraFlowType(FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        //根据标识判断修改还是新增
        ExtraFlowType extraFlowType = extraFlowTypeMapper.getExtraFlowTypeByCode(flowTypeInfoWithPackets.getCode());
        if (extraFlowType != null) {
            delFlowTypePacket(flowTypeInfoWithPackets);
        }
        addFlowTypePacket(flowTypeInfoWithPackets);
    }

    public void addFlowTypePacket(FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        //新增额外流程类型
        ExtraFlowType extraFlowType = new ExtraFlowType();
        BeanUtils.copyProperties(flowTypeInfoWithPackets, extraFlowType);
        extraFlowType.setComment(flowTypeInfoWithPackets.getComment());
        //extraFlowType.setUnitCode(TaskAllotUserUtil.getUserInfo().getUnitType());
        extraFlowTypeMapper.insert(extraFlowType);
        //新增额外流程类型作业包
        for (String packetCode : flowTypeInfoWithPackets.getPacketCodes()) {
            ExtraFlowTypePacket extraFlowTypePacket = new ExtraFlowTypePacket();
            extraFlowTypePacket.setPacketCode(packetCode);
            extraFlowTypePacket.setExtraFlowTypeCode(flowTypeInfoWithPackets.getCode());
            extraFlowTypePacketMapper.insert(extraFlowTypePacket);
        }
    }

    public void delFlowTypePacket(FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        //删除额外流程类型
        MybatisPlusUtils.delete(
            extraFlowTypeMapper,
            eqParam(ExtraFlowType::getCode, flowTypeInfoWithPackets.getCode())
        );
        //删除额外流程类型作业包
        MybatisPlusUtils.delete(
            extraFlowTypePacketMapper,
            eqParam(ExtraFlowTypePacket::getExtraFlowTypeCode, flowTypeInfoWithPackets.getCode())
        );

    }

    @Override
    public Set<String> getPacketCodeForConfigurablePacketList(String unitCode, String parentFlowTypeCode, String flowTypeCode) {
        return extraFlowTypeMapper.getPacketCodeForConfigurablePacketList(unitCode, parentFlowTypeCode, flowTypeCode);
    }

    @Override
    public List<ExtraFlowType> getExtraFlowTypeList(String code, String unitCode) {
        return extraFlowTypeMapper.getExtraFlowTypeList(code, unitCode);
    }

    @Override
    public List<ExtraFlowType> getPacketIndependent(String packetCode, String unitCode) {
        return extraFlowTypeMapper.getPacketIndependent(packetCode, unitCode);
    }

    @Override
    public List<ExtraFlowTypeWithPackets> getPacketNarrow(String unitCode) {
        return extraFlowTypeMapper.getPacketNarrow(unitCode);
    }

    @Override
    public List<String> getExtraFlowTypeListByFlow(String unitCode, List<String> parentFlowTypeCode) {
        //查出本单位下的额外流程类型
        List<ExtraFlowType> extraFlowTypeList = extraFlowTypeMapper.getExtraFlowTypeByUnitCode(unitCode);
        List<String> codeList = extraFlowTypeList.stream().filter(extraFlowType -> parentFlowTypeCode.contains(extraFlowType.getParentFlowTypeCode())).map(v -> v.getCode()).collect(Collectors.toList());
        return codeList;
    }
}
