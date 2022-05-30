package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.ydzbinfo.emis.guns.config.HighLevelRepairProperties;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.MonitorPacketMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.*;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.MonitorPacket;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConfigService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.MonitorPacketService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.utils.NumberToStringUtils;
import com.ydzbinfo.emis.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/1 17:49
 **/
@Service
@Transactional
public class MonitorPacketServiceImpl implements MonitorPacketService {
    @Resource
    MonitorPacketMapper monitorPacketMapper;

    @Resource
    IRemoteService remoteService;

    @Resource
    ConfigService configService;
    @Autowired
    HighLevelRepairProperties highLevelRepairProperties;

    public Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<MonitorPacket> getMonitorPackets() {
        return monitorPacketMapper.getMonitorPackets();
    }

    @Override
    public List<MonitorPacket> getMonitorPacket(MonitorPacket monitorPacket) {
        return monitorPacketMapper.getMonitorPacket(monitorPacket);
    }

    @Override
    public int addMonitorPacket(MonitorPacket monitorPacket) {
        return monitorPacketMapper.addMonitorPacket(monitorPacket);
    }

    @Override
    public int updMonitorPacket(MonitorPacket monitorPacket) {
        return monitorPacketMapper.updMonitorPacket(monitorPacket);
    }

    @Override
    public int delMonitorPacket(MonitorPacket monitorPacket) {
        return monitorPacketMapper.delMonitorPacket(monitorPacket);
    }

    @Override
    public LevelRepairInfo getPacketRecordList(String trainsetName, String trainsetId, String unitCode) throws Exception {
        List<UnitLevelRepairInfo> unitLevelRepairInfos = remoteService.getPgMPacketrecordList(trainsetName, trainsetId);
        //根据车组id查到车型和批次
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
        //根据车型查询作业包
        MonitorPacket monitorPacket = new MonitorPacket();
        monitorPacket.setTrainType(trainsetBaseInfo.getTraintype());
        monitorPacket.setBatchCode(trainsetBaseInfo.getTraintempid());
        //获取本车型批次作业包,已作业包为准进行过滤
        // List<PacketInfo> packetInfoList = this.getPacketList(null, trainsetBaseInfo.getTraintype(), trainsetBaseInfo.getTraintempid());
        // Set<String> packetCodes = packetInfoList.stream().map(v -> v.getPacketCode()).collect(Collectors.toSet());
        // unitLevelRepairInfos = unitLevelRepairInfos.stream().filter(v -> packetCodes.contains(v.getPacketCode())).collect(Collectors.toList());
        List<MonitorPacket> getMonitorPacket = this.getMonitorPacket(monitorPacket);
        if (getMonitorPacket.size() > 0) {
            List<String> packetCodeList = getMonitorPacket.stream().map(v -> v.getPacketCode()).collect(Collectors.toList());
            unitLevelRepairInfos = unitLevelRepairInfos.stream().filter(v -> packetCodeList.contains(v.getPacketCode())).collect(Collectors.toList());
        }

        unitLevelRepairInfos.sort(Comparator.comparing(UnitLevelRepairInfo::getPacketName));
        List<HighLevelRepairInfo> highLevelRepairInfoList = new ArrayList<>();
        try {
            highLevelRepairInfoList = HttpUtil.getHighLevelRepairInfo(highLevelRepairProperties, unitCode);
        } catch (Exception e) {
            logger.error("调用高级修信息失败", e);
        }
        if (StringUtils.isNotBlank(trainsetId)) {
            highLevelRepairInfoList = highLevelRepairInfoList.stream().filter(v -> v.getTrainsetId().equals(trainsetId)).collect(Collectors.toList());
        }
        if (highLevelRepairInfoList != null) {
            highLevelRepairInfoList.forEach(highLevelRepairInfo -> {
                String level = highLevelRepairInfo.getLevel().replace(".0", "");
                highLevelRepairInfo.setTime(highLevelRepairInfo.getTime().replace("00:00:00", ""));
                level = NumberToStringUtils.numberToString(Integer.parseInt(level));
                highLevelRepairInfo.setLevel(level + "级修");
            });
        }
        unitLevelRepairInfos.sort(Comparator.comparing(UnitLevelRepairInfo::getMainCycCode));
        LevelRepairInfo levelRepairInfo = new LevelRepairInfo();
        levelRepairInfo.setTrainsetId(trainsetId);
        levelRepairInfo.setHighLevelRepairInfos(highLevelRepairInfoList);
        levelRepairInfo.setUnitLevelRepairInfos(unitLevelRepairInfos);
        return levelRepairInfo;
    }

    @Override
    public List<MonitorBase> getMonitorBase(String packetCode, String suitModel, String suitBatch) {
        String departmentCode = ContextUtils.getUnitCode();
        List<MonitorBase> monitorBaseList = new ArrayList<>();
        //获取到车型
        List<String> trainTypes = remoteService.getTraintypeListLocal(departmentCode);
        //获取到批次
        List<String> templateId = remoteService.getTrainTemplateListLocal(departmentCode);

        for (int i = 0; i < trainTypes.size(); i++) {
            if (trainTypes.get(i) != null) {
                MonitorBase monitorbase = new MonitorBase();
                monitorbase.setTrainType(trainTypes.get(i));
                monitorbase.setBatchCode(templateId.get(i));

                MonitorPacket monitorPacket = new MonitorPacket();
                monitorPacket.setTrainType(trainTypes.get(i));
                monitorPacket.setBatchCode(templateId.get(i));
                List<MonitorPacket> monitorPackets = this.getMonitorPacket(monitorPacket);
                if (monitorPackets.size() > 0) {
                    List<MonitorWithPackets> monitorWithPackets = new ArrayList<>();
                    for (MonitorPacket packet : monitorPackets) {
                        MonitorWithPackets monitorWithPacket = new MonitorWithPackets();
                        monitorWithPacket.setPacketCode(packet.getPacketCode());
                        monitorWithPacket.setPacketName(packet.getPacketName());
                        monitorWithPackets.add(monitorWithPacket);
                    }
                    monitorbase.setMonitorWithPacketsList(monitorWithPackets);
                    monitorbase.setId(monitorPackets.get(0).getId());
                }
                monitorBaseList.add(monitorbase);
            }
        }

        return monitorBaseList;
    }

    @Override
    public void updMonitorBase(List<MonitorBase> monitorPackets) {
        for (MonitorBase monitorPacket : monitorPackets) {
            if (StringUtils.isNotBlank(monitorPacket.getId())) {
                MonitorPacket delMonitorPacket = new MonitorPacket();
                delMonitorPacket.setTrainType(monitorPacket.getTrainType());
                delMonitorPacket.setBatchCode(monitorPacket.getBatchCode());
                this.delMonitorPacket(delMonitorPacket);
            }
            for (MonitorWithPackets monitorWithPackets : monitorPacket.getMonitorWithPacketsList()) {
                MonitorPacket addMonitorPacket = new MonitorPacket();
                BeanUtils.copyProperties(monitorPacket, addMonitorPacket);
                addMonitorPacket.setCreateTime(new Date());
                addMonitorPacket.setCreateUserCode(UserUtil.getUserInfo().getStaffId());
                addMonitorPacket.setCreateUserName(UserUtil.getUserInfo().getName());
                addMonitorPacket.setId(UUID.randomUUID().toString());
                addMonitorPacket.setPacketCode(monitorWithPackets.getPacketCode());
                addMonitorPacket.setPacketName(monitorWithPackets.getPacketName());
                this.addMonitorPacket(addMonitorPacket);
            }
        }
    }

    @Override
    public List<PacketInfo> getPacketList(String packetCode, String trainsetType, String batch) {
        List<PacketInfo> packetInfos = remoteService.getPacketList();
        return packetInfos.stream().filter(v -> PacketInfoUtils.isPacketSuitTrainset(v, trainsetType, batch)).collect(Collectors.toList());
    }
}
