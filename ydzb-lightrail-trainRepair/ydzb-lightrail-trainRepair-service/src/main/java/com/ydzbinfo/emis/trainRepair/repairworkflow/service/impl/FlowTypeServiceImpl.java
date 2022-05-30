package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ExtraFlowTypeMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowTypeMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowTypePacket;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ExtraFlowTypeWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowTypeWithComments;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.BasicFlowTypeEnum;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowDatabaseConfigUtil;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowPageCodeEnum;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程类型表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Service
public class FlowTypeServiceImpl extends ServiceImpl<FlowTypeMapper, FlowType> implements IFlowTypeService {
    @Resource
    FlowTypeMapper flowTypeMapper;

    @Resource
    IExtraFlowTypeService extraFlowTypeService;

    @Resource
    ExtraFlowTypeMapper extraFlowTypeMapper;

    @Override
    public List<BaseFlowType> getFlowTypeList(String unitCode) {
        return this.getFlowTypeList(unitCode, false);
    }

    @Override
    public List<BaseFlowType> getFlowTypeList(String unitCode, boolean config) {
        return this.getFlowTypeList(unitCode, config, true);
    }

    @Override
    public List<BaseFlowType> getFlowTypeList(String unitCode, boolean config, boolean extraFlowType) {

        //查出基本流程类型
        List<FlowType> flowTypes = this.getFlowTypeInfos();
        // 定义所有流程类型
        List<BaseFlowType> allFlowTypes = new ArrayList<>();

        boolean useDefaultHostLingFlowType = FlowDatabaseConfigUtil.isUseDefaultHostlingFlowType();

        Consumer<FlowType> addExtraFlowType = (flowType) -> {
            if (extraFlowType) {
                // 根据基本类型查出额外流程类型
                List<ExtraFlowType> extraFlowTypes = extraFlowTypeService.getExtraFlowTypeList(flowType.getCode(), unitCode);
                allFlowTypes.addAll(extraFlowTypes);
            }
        };
        Set<String> flowTypeCodesOnlyInConfig = new HashSet<>(Collections.singletonList(
            "TEMPLATE"
        ));
        Set<String> hiddenFlowTypeCodes = new HashSet<>(Collections.singletonList(
            "SUBFLOW"
        ));

        for (FlowType flowType : flowTypes) {
            String flowTypeCode = flowType.getCode();
            if (hiddenFlowTypeCodes.contains(flowTypeCode)) {
                continue;
            }
            if (flowTypeCodesOnlyInConfig.contains(flowTypeCode)) {
                if (config) {
                    allFlowTypes.add(flowType);
                }
            } else {
                BasicFlowTypeEnum currentFlowTypeEnum = EnumUtils.findEnum(BasicFlowTypeEnum.class, BasicFlowTypeEnum::getValue, flowTypeCode);
                // 判断是否可以匹配到基础流程类型
                switch (currentFlowTypeEnum) {
                    case THE_OTHERS:
                        addExtraFlowType.accept(flowType);
                        break;
                    case HOSTLING:
                        if (useDefaultHostLingFlowType) {
                            allFlowTypes.add(flowType);
                        } else {
                            addExtraFlowType.accept(flowType);
                        }
                        break;
                    default:
                        allFlowTypes.add(flowType);
                        addExtraFlowType.accept(flowType);
                        break;
                }
            }

        }
        Set<String> allowFlowTypes = FlowDatabaseConfigUtil.getAllowFlowTypes();
        return allFlowTypes.stream().filter(v -> allowFlowTypes.contains(v.getCode()) || flowTypeCodesOnlyInConfig.contains(v.getCode())).collect(Collectors.toList());
    }

    @Override
    public List<FlowTypeInfoWithPackets> getFlowTypeAndPacket(String unitCode) {
        //得到流程code过滤
        List<String> codeList = this.getFlowTypeCodes(unitCode);

        //转换
        List<FlowTypeInfoWithPackets> flowTypeInfoWithPacketList = new ArrayList<>();

        if (codeList.size() > 0) {
            //流程类型和流程类型描述
            List<FlowTypeWithComments> flowTypeWithComments = this.getFlowTypeWithComment(unitCode, codeList);

            //额外流程类型信息
            List<ExtraFlowTypeWithPackets> extraFlowTypeWithPackets = extraFlowTypeService.getExtraFlowTypeWithPacket(unitCode, codeList, null);
            for (FlowTypeWithComments flowTypeWithComment : flowTypeWithComments) {
                FlowTypeInfoWithPackets flowTypeInfoWithPacket = new FlowTypeInfoWithPackets();
                BeanUtils.copyProperties(flowTypeWithComment, flowTypeInfoWithPacket);
                flowTypeInfoWithPacket.setComment(flowTypeWithComment.getFlowTypeComments() != null && flowTypeWithComment.getFlowTypeComments().size() > 0 ? flowTypeWithComment.getFlowTypeComments().get(0).getComment() : null);
                flowTypeInfoWithPacket.setUnitCode(flowTypeWithComment.getFlowTypeComments() != null && flowTypeWithComment.getFlowTypeComments().size() > 0 ? flowTypeWithComment.getFlowTypeComments().get(0).getUnitCode() : null);
                flowTypeInfoWithPacketList.add(flowTypeInfoWithPacket);
            }

            for (ExtraFlowTypeWithPackets extraFlowTypeWithPacket : extraFlowTypeWithPackets) {
                Set<String> packetCode = new HashSet<>();
                FlowTypeInfoWithPackets flowTypeInfoWithPacket = new FlowTypeInfoWithPackets();
                BeanUtils.copyProperties(extraFlowTypeWithPacket, flowTypeInfoWithPacket);
                flowTypeInfoWithPacket.setComment(extraFlowTypeWithPacket.getComment());
                flowTypeInfoWithPacket.setUnitCode(extraFlowTypeWithPacket.getUnitCode());
                for (ExtraFlowTypePacket extraFlowTypePacket : extraFlowTypeWithPacket.getExtraFlowTypePackets()) {
                    packetCode.add(extraFlowTypePacket.getPacketCode());
                }
                flowTypeInfoWithPacket.setPacketCodes(packetCode);
                flowTypeInfoWithPacketList.add(flowTypeInfoWithPacket);
            }
        }

        return flowTypeInfoWithPacketList;
    }

    @Override
    public List<FlowType> getFlowTypeInfos() {
        return flowTypeMapper.getFlowTypeInfos();
    }

    @Override
    public List<FlowTypeWithComments> getFlowTypeWithComment(String unitCode, List<String> codeList) {
        return flowTypeMapper.getFlowTypeWithComment(unitCode, codeList);
    }

    private List<String> getFlowTypeCodes(String unitCode) {
        List<BaseFlowType> flowTypes = this.getFlowTypeList(unitCode);
        List<String> codeList = new ArrayList<>();
        for (BaseFlowType flowType : flowTypes) {
            codeList.add(flowType.getCode());
        }
        return codeList;
    }

    @Override
    public List<BaseFlowType> getFlowTypesByFlowPageCode(String flowPageCode, String unitCode) {
        Supplier<List<BaseFlowType>> filteredFlowTypesGetter = () -> {
            // 获取所有流程类型
            List<BaseFlowType> allAllowedFlowTypes = this.getFlowTypeList(unitCode);
            if (StringUtils.isBlank(flowPageCode)) {
                return new ArrayList<>(allAllowedFlowTypes);
            } else {
                Predicate<BaseFlowType> filter;
                Optional<FlowPageCodeEnum> flowPageCodeEnumOptional = Arrays.stream(FlowPageCodeEnum.values()).filter(v -> v.getValue().equals(flowPageCode)).findAny();

                if (flowPageCodeEnumOptional.isPresent()) {
                    // 如果页面类型为既有规定的类型，获取其对应的基本流程类型并设置过滤方法
                    FlowPageCodeEnum flowPageCodeEnum = flowPageCodeEnumOptional.get();
                    filter = baseFlowType -> {
                        // 根据流程类型是基本类型还是额外类型判断其是否属于当前flowPageCode
                        if (baseFlowType instanceof FlowType) {
                            FlowType flowType = (FlowType) baseFlowType;
                            return flowPageCodeEnum.containsFlowType(flowType);
                        } else if (baseFlowType instanceof ExtraFlowType) {
                            ExtraFlowType extraFlowType = (ExtraFlowType) baseFlowType;
                            return flowPageCodeEnum.containsExtraFlowType(extraFlowType);
                        } else {
                            return false;
                        }
                    };
                } else {
                    // 如果不是既有规定的类型，则根据配置获取其对应的流程类型并设置过滤方法
                    Set<String> extraFlowPageFlowTypes = FlowDatabaseConfigUtil.getExtraFlowPageFlowTypes(flowPageCode);
                    filter = baseFlowType -> extraFlowPageFlowTypes.contains(baseFlowType.getCode());
                }
                return allAllowedFlowTypes.stream().filter(filter).collect(Collectors.toList());
            }
        };
        // 获取过滤过的类型
        List<BaseFlowType> filteredFlowTypes = filteredFlowTypesGetter.get();
        // 排序
        filteredFlowTypes.sort(Comparator.comparing(BaseFlowType::getSort));
        return filteredFlowTypes;
    }
}
