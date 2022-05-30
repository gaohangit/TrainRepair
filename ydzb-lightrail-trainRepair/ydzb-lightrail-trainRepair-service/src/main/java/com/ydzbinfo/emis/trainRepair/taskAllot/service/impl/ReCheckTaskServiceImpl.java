package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.RecheckTaskProperties;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntityLYOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntitySJOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.JsonRootBean;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IReCheckTaskService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskcarpartService;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.DateTimeUtil;
import com.ydzbinfo.emis.utils.HttpUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @date: 2022/1/17
 * @author: 冯帅
 */
@Service
public class ReCheckTaskServiceImpl implements IReCheckTaskService {

    protected static final Logger logger = LoggerFactory.getLogger(ReCheckTaskServiceImpl.class);
    @Autowired
    private RecheckTaskProperties recheckTaskProperties;

    @Autowired
    XzyMTaskcarpartService taskcarpartService;

    @Autowired
    IRemoteService remoteService;

    @Override
    public List<EntitySJOverRunRecord> getReCheckTaskList(Map map) {
        String dayPlanId = map.get("dayPlanId").toString();
        if(StringUtils.isBlank(dayPlanId)){
            throw new RuntimeException("日计划参数错误，获取失败");
        }
        String endDateStr = dayPlanId.substring(0,dayPlanId.length()-3);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = DateTimeUtil.parse(endDateStr,"yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        String startDate = simpleDateFormat.format(calendar.getTime());
        map.put("StartDate", startDate);
        map.put("EndDate", endDate);
        JSONObject jsonObject = new JSONObject(map);
        Map requestMap = new HashMap();
        requestMap.put("QueryPar", jsonObject.toString());

        JsonRootBean jsonRootBean = HttpUtil.getToken(recheckTaskProperties);
        JSONObject lyJson = HttpUtil.doPost(recheckTaskProperties.getLyGetOverRunRecordUrl(), requestMap, recheckTaskProperties, jsonRootBean);
        JSONObject sjJson = HttpUtil.doPost(recheckTaskProperties.getSjGetOverRunRecordUrl(), requestMap, recheckTaskProperties, jsonRootBean);

        List<EntityLYOverRunRecord> entityLYOverRunRecords = JSON.parseArray(lyJson.getString("Data"), EntityLYOverRunRecord.class);
        List<EntitySJOverRunRecord> entitySJOverRunRecords = JSON.parseArray(sjJson.getString("Data"), EntitySJOverRunRecord.class);
        logger.info("复核任务ly接口获取到:"+entityLYOverRunRecords.size()+"条"+",复核任务sj接口获取到:"+entitySJOverRunRecords.size());


        for (EntityLYOverRunRecord entityLYOverRunRecord : entityLYOverRunRecords) {
            EntitySJOverRunRecord entitySJOverRunRecord = new EntitySJOverRunRecord();
            BeanUtils.copyProperties(entityLYOverRunRecord, entitySJOverRunRecord);
            entitySJOverRunRecord.setPantoCode(entityLYOverRunRecord.getWheelSetPosition());
            entitySJOverRunRecord.setSkaCode(entityLYOverRunRecord.getWheelPosition());
            entitySJOverRunRecord.setRecheckType("轮对");
            entitySJOverRunRecords.add(entitySJOverRunRecord);
        }
        //将已经派工了和计划中的的复核任务过滤掉
        List<EntitySJOverRunRecord> resultRunRecordList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(entitySJOverRunRecords)){
            List<String> overRunIds = entitySJOverRunRecords.stream().map(t -> t.getId()).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(overRunIds)){
                //1.获取计划中的故障数据
                String unitCode = ContextUtils.getUnitCode();
                List<ZtTaskItemEntity> taskItemList = new ArrayList<>();
                if(StringUtils.isNotBlank(dayPlanId)){
                    List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, null, "5", "", unitCode);
                    taskItemList = Optional.ofNullable(taskPacketEntityList).orElseGet(ArrayList::new).stream().filter(t -> !CollectionUtils.isEmpty(t.getLstTaskItemInfo())).flatMap(v -> v.getLstTaskItemInfo().stream()).collect(Collectors.toList());
                }
                //2.获取派工中的故障数据
                List<XzyMTaskcarpart> taskcarpartList = taskcarpartService.getCarPartListByItemCodeList(overRunIds);
                if(!CollectionUtils.isEmpty(taskcarpartList)||!CollectionUtils.isEmpty(taskItemList)){
                    for(EntitySJOverRunRecord sjOverRunRecord:entitySJOverRunRecords){
                        boolean exitAllot = Optional.ofNullable(taskcarpartList).orElseGet(ArrayList::new).stream().anyMatch(t -> t.getItemCode().equals(sjOverRunRecord.getId()));
                        boolean exitTask = Optional.ofNullable(taskItemList).orElseGet(ArrayList::new).stream().anyMatch(v->v.getItemCode().equals(sjOverRunRecord.getId()));
                        if(!(exitAllot||exitTask)){
                            resultRunRecordList.add(sjOverRunRecord);
                        }
                    }
                }else{
                    resultRunRecordList = entitySJOverRunRecords;
                }
            }
        }
        return resultRunRecordList;
    }
}
