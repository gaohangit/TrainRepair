package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.ConnectTrainType;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConnectTrainTypeService;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gaohan
 * @description 重联车型配置
 * @createDate 2021/3/1 16:12
 **/
@RestController
@RequestMapping("/connectTrainType")
public class ConnectTrainTypeController {

    protected static final Logger logger = LoggerFactory.getLogger(ConnectTrainTypeController.class);

    
    @Resource
    ConnectTrainTypeService connectTrainTypeService;

    @Resource
    IRemoteService iRemoteService;

    @ApiOperation(value = "获取所有重联车型配置")
    @GetMapping("/getConnectTrainTypes")
    public Object getConnectTrainTypes(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        try{
            JSONObject trainTypes = iRemoteService.getTrainTypeList();
            List<String> list=JSONArray.parseArray(trainTypes.get("data").toString(),String.class);
            Page page1=new Page(1,10000);
            List<ConnectTrainType> connectTrainTypes=connectTrainTypeService.getConnectTrainTypes(page1);
            List<ConnectTrainType> result=new ArrayList<>();
            for (String s : list) {
                ConnectTrainType query=new ConnectTrainType();
                query.setTrainType(s);
                if(connectTrainTypeService.getConnectTrainType(query)==null){
                    ConnectTrainType c=new ConnectTrainType();
                    c.setTrainType(s);
                    c.setCreateUserName(ContextUtils.getUnitName());
                    c.setCreateUserCode(ContextUtils.getUnitCode());
                    c.setCreateTime(new Date());
                    connectTrainTypeService.addConnectTrainType(c);
                }

                if(!list.contains(s)&& connectTrainTypes.contains(s)){
                    ConnectTrainType del=new ConnectTrainType();
                    del.setTrainType(s);
                    connectTrainTypeService.delConnectTrainType(del);
                }
            }
            Page page=new Page(pageNum,pageSize);
            List<ConnectTrainType> resultList=connectTrainTypeService.getConnectTrainTypes(page);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("monitorNoteContents",resultList);
            jsonObject.put("count",connectTrainTypes.size());
            return RestResult.success().setData(jsonObject);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有重联车型配置");
        }
    }
    @ApiOperation(value = "获取重联车型配置")
    @GetMapping("/getConnectTrainType")
    public Object getConnectTrainType(ConnectTrainType connectTrainType){
        try{
            ConnectTrainType monitorNoteContents = connectTrainTypeService.getConnectTrainType(connectTrainType);
            return RestResult.success().setData(monitorNoteContents);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取重联车型配置");

        }
    }
    @ApiOperation(value = "新增重联车型配置")
    @PostMapping("/addConnectTrainType")
    public Object addConnectTrainType(@RequestBody ConnectTrainType connectTrainType){
        connectTrainType.setCreateUserCode(ContextUtils.getUnitCode());
        connectTrainType.setCreateUserName(ContextUtils.getUnitName());
        try{
            connectTrainType.setCreateTime(new Date());
            connectTrainTypeService.addConnectTrainType(connectTrainType);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "新增重联车型配置");

        }
    }

    @ApiOperation(value = "修改重联车型配置")
    @PostMapping("/updConnectTrainType")
    public Object upConnectTrainType(@RequestBody List<ConnectTrainType> connectTrainTypes){
        try{
            for (ConnectTrainType connectTrainType : connectTrainTypes) {
                connectTrainTypeService.updConnectTrainType(connectTrainType);
            }
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "修改重联车型配置");

        }
    }

    @ApiOperation(value = "删除重联车型配置")
    @PostMapping("/delConnectTrainType")
    public Object delConnectTrainType(@RequestBody ConnectTrainType connectTrainType){
        try{
            connectTrainTypeService.delConnectTrainType(connectTrainType);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有调车记事");

        }
    }

    public Date getMaxDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String a=df.format(date);
        Calendar c=new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.SECOND,-5);
        date=c.getTime();
        String time=df.format(date);
        return date;

    }



}
