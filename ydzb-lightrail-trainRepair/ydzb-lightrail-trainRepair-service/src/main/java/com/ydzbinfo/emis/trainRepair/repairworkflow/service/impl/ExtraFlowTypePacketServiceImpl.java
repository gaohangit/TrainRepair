package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSource;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.FlowTypeConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ExtraFlowTypePacketMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowTypePacket;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypePacketService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.BasicFlowTypeEnum;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 额外流程类型作业包配置表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-08
 */
@Transactional
@Service
public class ExtraFlowTypePacketServiceImpl extends ServiceImpl<ExtraFlowTypePacketMapper, ExtraFlowTypePacket> implements IExtraFlowTypePacketService {
    @Resource
    ExtraFlowTypePacketMapper extraFlowTypePacketMapper;

    @Resource
    IRemoteService remoteService;

    @Autowired(required = false)
    private RepairWorkflowConfigMqSource repairWorkflowConfigSource;

    @Autowired
    private IExtraFlowTypeService extraFlowTypeService;

    @Override
    public void setExtraFlowTypePacket(FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        this.sendSetExtraFlowTypePacket(flowTypeInfoWithPackets);
        //发送同步数据
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<FlowTypeInfoWithPackets> messageWrapper = new MessageWrapper<>(FlowTypeConfigHeaderConstant.class, FlowTypeConfigHeaderConstant.UPDATE, flowTypeInfoWithPackets);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.flowTypeConfigChannel(), messageWrapper);
        }
    }

    @Override
    public void sendSetExtraFlowTypePacket(FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        boolean repairOneAndTowIsTwo = false;
        if (flowTypeInfoWithPackets.getParentFlowTypeCode().equals("REPAIR_ONE_AND_TWO")) {
            //一二级修没有二级修抛异常
            List<PacketInfo> packetInfos = remoteService.getPacketList();
            for (String packetCode : flowTypeInfoWithPackets.getPacketCodes()) {
                for (PacketInfo packetInfo : packetInfos) {
                    if ((packetInfo.getPacketType().equals("1") && packetInfo.getRepairCode().equals("2")) && packetCode.equals(packetInfo.getPacketCode())) {
                        repairOneAndTowIsTwo = true;
                    }
                }
            }
            if (repairOneAndTowIsTwo == false && flowTypeInfoWithPackets.getPacketCodes().size() > 0) {
                throw new RuntimeException("该流程没有配置二级修作业包");
            }
        }
        MybatisPlusUtils.delete(
            extraFlowTypePacketMapper,
            eqParam(ExtraFlowTypePacket::getExtraFlowTypeCode, flowTypeInfoWithPackets.getCode())
        );

        for (String code : flowTypeInfoWithPackets.getPacketCodes()) {
            ExtraFlowTypePacket addExtraFlowTypePacket = new ExtraFlowTypePacket();
            addExtraFlowTypePacket.setExtraFlowTypeCode(flowTypeInfoWithPackets.getCode());
            addExtraFlowTypePacket.setPacketCode(code);
            extraFlowTypePacketMapper.insert(addExtraFlowTypePacket);
        }
    }

    @Override
    public List<PacketInfo> getPacketsByFlowType(String flowTypeCode, String parentFlowTypeCode, String unitCode, Boolean flag) {
        List<PacketInfo> packetInfos = remoteService.getPacketList();
        if (flag) {
            return packetInfos;
        }

        BasicFlowTypeEnum parentFlowTypeEnum = EnumUtils.findEnum(
            BasicFlowTypeEnum.class,
            BasicFlowTypeEnum::getValue,
            parentFlowTypeCode
        );
        // 按包类型、包编码过滤
        List<PacketInfo> packetInfoList = packetInfos.stream().filter(packetInfo -> {
            if (parentFlowTypeEnum == null) {
                return false;
            } else {
                return parentFlowTypeEnum.judgePacket(packetInfo);
            }
        }).collect(Collectors.toList());

        //排除同父类型下(除自己本身)的包和独立作业包
        Set<String> codes = extraFlowTypeService.getPacketCodeForConfigurablePacketList(unitCode, parentFlowTypeCode, flowTypeCode);
        List<PacketInfo> packetInfoListResult = packetInfoList.stream().filter(packetInfo -> {
            if (!codes.contains(packetInfo.getPacketCode())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        return packetInfoListResult;

    }
}
