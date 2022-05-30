package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.RecheckTaskProperties;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntityLYOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntitySJOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.JsonRootBean;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IReCheckTaskService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gaohan
 * @description 复核任务
 * @createDate 2021/3/23 16:45
 **/
@RestController
@RequestMapping("reCheckTask")
public class ReCheckTaskController {
    protected static final Logger logger = LoggerFactory.getLogger(ReCheckTaskController.class);

    @Autowired
    private RecheckTaskProperties recheckTaskProperties;

    @Autowired
    IReCheckTaskService reCheckTaskService;

    @PostMapping("/getOverRunRecordList")
    public Object getReCheckTaskList(@RequestBody Map map) {
        try {
            /** 将逻辑放到service中
             SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
             Date now = new Date();
             String endDate = simpleDateFormat.format(now);
             Calendar calendar = Calendar.getInstance();
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
             }*/
            List<EntitySJOverRunRecord> entitySJOverRunRecords = reCheckTaskService.getReCheckTaskList(map);
            return RestResult.success().setData(entitySJOverRunRecords);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询复核任务失败");
        }
    }

    @GetMapping("getLyOverRunRecordList")
    public Object getLyOverRunRecordList(String pageNum, String pageSize, String trainsetNameStr) {
        try {
            Map requestMap = getOverRunRecordParams(trainsetNameStr);

            JsonRootBean jsonRootBean = HttpUtil.getToken(recheckTaskProperties);

            JSONObject lyJson = HttpUtil.doPost(recheckTaskProperties.getLyGetOverRunRecordUrl(), requestMap, recheckTaskProperties, jsonRootBean);
            List<EntityLYOverRunRecord> entityLYOverRunRecords = JSON.parseArray(lyJson.getString("Data"), EntityLYOverRunRecord.class);
            //进行分页
            if (StringUtils.isNotBlank(pageNum)) {
                return RestResult.success().setData(CommonUtils.getPage(entityLYOverRunRecords, Integer.parseInt(pageNum), Integer.parseInt(pageSize)));
            } else {
                return RestResult.success().setData(entityLYOverRunRecords);
            }
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询Ly复核任务失败");
        }
    }

    @GetMapping("getSjOverRunRecordList")
    public Object getSjOverRunRecordList(String pageNum, String pageSize, String trainsetNameStr) {
        try {
            Map requestMap = getOverRunRecordParams(trainsetNameStr);

            JsonRootBean jsonRootBean = HttpUtil.getToken(recheckTaskProperties);
            JSONObject sjJson = HttpUtil.doPost(recheckTaskProperties.getSjGetOverRunRecordUrl(), requestMap, recheckTaskProperties, jsonRootBean);

            List<EntitySJOverRunRecord> entitySJOverRunRecords = JSON.parseArray(sjJson.getString("Data"), EntitySJOverRunRecord.class);
            //进行分页
            if (StringUtils.isNotBlank(pageNum)) {
                return RestResult.success().setData(CommonUtils.getPage(entitySJOverRunRecords, Integer.parseInt(pageNum), Integer.parseInt(pageSize)));
            } else {
                return RestResult.success().setData(entitySJOverRunRecords);
            }
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询Sj复核任务失败");
        }
    }

    public Map getOverRunRecordParams(String trainsetNameStr) {
        List<String> trainsetNameList = new ArrayList<>();
        if (StringUtils.isNotBlank(trainsetNameStr)) {
            trainsetNameList = Arrays.asList(trainsetNameStr.split(","));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String endDate = simpleDateFormat.format(now);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        String startDate = simpleDateFormat.format(calendar.getTime());
        Map map = new HashMap();
        map.put("StartDate", startDate);
        map.put("EndDate", endDate);
        map.put("TrainsetNameList", trainsetNameList);
        JSONObject jsonObject = new JSONObject(map);
        Map requestMap = new HashMap();
        requestMap.put("QueryPar", jsonObject.toString());
        return requestMap;
    }
}
