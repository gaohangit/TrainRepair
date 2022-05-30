package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowRunRecordMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowRunRecordInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowRunRecordService;
import com.ydzbinfo.emis.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 流程运行记录表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
@Service
public class FlowRunRecordServiceImpl extends ServiceImpl<FlowRunRecordMapper, FlowRunRecord> implements IFlowRunRecordService {
    @Autowired
    FlowRunRecordMapper flowRunRecordMapper;

    @Override
    public List<FlowRunRecordInfo> getFlowRunRecordsByForceEnd(String unitCode, String dayPlanId, String flowRunId, Date startDate, Date endDate) {
        //获取到流程强制结束/驳回信息并转换
        List<FlowRunRecord> flowRunRecords = flowRunRecordMapper.getFlowRunRecordsByForceEnd(unitCode, dayPlanId,flowRunId, startDate, endDate);
        return parseFlowRunRecord(flowRunRecords);
    }

    public List<FlowRunRecordInfo> parseFlowRunRecord(List<FlowRunRecord> flowRunRecords) {
        List<FlowRunRecordInfo> flowRunRecordInfos = new ArrayList<>();
        flowRunRecords.forEach(flowRunRecord -> {
            FlowRunRecordInfo flowRunRecordInfo = new FlowRunRecordInfo();
            BeanUtils.copyProperties(flowRunRecord, flowRunRecordInfo);
            if(StringUtils.isNotBlank(flowRunRecordInfo.getRemark())){
                JSONObject jsonObject = JSON.parseObject(flowRunRecordInfo.getRemark());
                flowRunRecordInfo.setNodeId(jsonObject.getString("nodeId"));
                flowRunRecordInfo.setNodeName(jsonObject.getString("nodeName"));
                flowRunRecordInfo.setRemark(jsonObject.getString("remark"));
                flowRunRecordInfo.setRecordTime(flowRunRecord.getRecordTime());
            }
            flowRunRecordInfos.add(flowRunRecordInfo);
        });
        return flowRunRecordInfos;
    }
}
