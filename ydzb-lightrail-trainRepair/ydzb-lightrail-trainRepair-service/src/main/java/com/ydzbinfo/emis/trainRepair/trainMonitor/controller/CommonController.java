//package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;
//
///**
// * @author gaohan
// * @description
// * @createDate 2021/3/1 10:46
// **/
//
//import com.alibaba.fastjson.JSONObject;
//import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
//import com.ydzbinfo.emis.utils.ServiceNameEnum;
//import com.ydzbinfo.emis.trainRepair.model.trainMonitor.track.*;
//import com.ydzbinfo.emis.trainRepair.vo.common.Temple;
//import com.ydzbinfo.emis.utils.*;
//import io.swagger.annotations.ApiOperation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import java.util.*;
//
///**
// * @author: gaohan
// * @description: 调用其他服务
// * @createDate: 2021/2/23 14:54
// **/
//@RestController
//@RequestMapping("/common")
//public class CommonController {
//
//    protected static final Logger logger = LoggerFactory.getLogger(CommonController.class);
//
//    //
//    private final String Resumeid = ServiceNameEnum.ResumeService.getId();
//
//    @Resource
//    IRemoteService iRemoteService;
//
//
//    @ApiOperation(value = "获取出入所车组")
//    @GetMapping("/getTrainsetListReceived")
//    //unitCode 单位编码
//    public Object getTrainsetListReceived(String unitCode){
//        try{
//            unitCode=UserShiroUtil.getUserInfo().getDeptCode();
//            List<TrainsetBaseInfo> processCarPartEntityList=iRemoteService.getTrainsetListReceived(unitCode);
//            return RestResult.success().setData(processCarPartEntityList);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取出入所车辆信息错误");
//        }
//    }
//    @ApiOperation(value = "获取其他车组(热备车)")
//    @GetMapping("/getTrainsetHotSpareInfo")
//    public Object getTrainsetHotSpareInfo(){
//        try{
//            JSONObject jsonObject=iRemoteService.getTrainsetHotSpareInfo();
//            return RestResult.success().setData(jsonObject);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取其他车组(热备车)");
//        }
//    }
//
//
//    @ApiOperation(value = "获取故障状态及数量")
//    @GetMapping("/getFaultDataByIdList")
//    public Object getFaultDataByIdList(String  itemCode){
//        try{
//            List<String> itemCodes=new ArrayList<>();
//            if (itemCode != null && !itemCode.equals("")) {
//                itemCodes = Arrays.asList(itemCode.split(","));
//            }
//            JSONObject faultList = iRemoteService.getFaultDataByIdList(itemCodes);
//            return RestResult.success().setData(faultList);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取故障状态及数量");
//        }
//    }
//
//    @ApiOperation(value = "重联车型选择")
//    @GetMapping("/getTrainTypeList")
//    public Object getTrainTypeList(){
//        try{
//            JSONObject trainTypes = iRemoteService.getTrainsetList();
//            return RestResult.success().setData(trainTypes);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "重联车型选择");
//
//        }
//    }
//
//    @ApiOperation(value = "获取开行信息")
//    @GetMapping("/getRunRoutingDataByDate")
//    public Object getRunRoutingDataByDate(String date,String trainNo,String planFlag,@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
//        try{
//            List<PriData> priDataList=new ArrayList<>();
//            List<PriData> trainTypes = iRemoteService.getRunRoutingDataByDate(date,trainNo,planFlag);
//            for(int i=0;i<trainTypes.size();i++){
//                if((pageNum-1)*pageSize<=i&&i<pageNum*pageSize){
//                    priDataList.add(trainTypes.get(i));
//                }
//            }
//            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("result",priDataList);
//            jsonObject.put("count",trainTypes.size());
//            return RestResult.success().setData(jsonObject);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取开行信息");
//
//        }
//    }
//
//    @ApiOperation(value = "获取作业包选择")
//    @GetMapping("/getPacketList")
//    //packetCode 作业包编码,suitModel适用车型,suitBatch适用批次
//    public Object getPacketList(String packetCode,String suitModel,String suitBatch){
//        try{
//            JSONObject trainTypes = iRemoteService.getPacketList(packetCode,suitModel,suitBatch);
//            return RestResult.success().setData(trainTypes);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取作业包选择");
//
//        }
//    }
//    @ApiOperation(value = "获取调车计划")
//    @PostMapping("/getShuntingPlanByCondition")
//    //beginDate,endDate, deptCode运用所编码, emuId车组ID
//    public Object getShuntingPlanByCondition(@RequestBody  JSONObject jsonObject){
//        try{
//            JSONObject trainTypes = iRemoteService.getShuntingPlanByCondition(jsonObject);
//            return RestResult.success().setData(trainTypes);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取调车计划");
//        }
//    }
//
//
//
//    @ApiOperation(value = "获取检修列表")
//    @GetMapping("/getPgMPacketrecordList")
//    public Object getPgMPacketrecordList(String trainsetName,String trainsetId){
//        try{
//            List<PacketRecord> packetRecords = iRemoteService.getPgMPacketrecordList(trainsetName,trainsetId);
//            return RestResult.success().setData(packetRecords);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取检修列表");
//
//        }
//    }
//
//    @ApiOperation(value = "获取车组详情(长编短编)")
//    @GetMapping("/getTrainsetDetialInfo")
//    //0短编 1长编
//    public Object getTrainsetDetialInfo(String trainsetId){
//        try{
//            int marshalCount=0;
//            TrainsetInfo trainsetInfo = iRemoteService.getTrainsetDetialInfo(trainsetId);
//            if (Integer.parseInt(trainsetInfo.getIMarshalcount())>8){
//                marshalCount=1;
//            }else{
//                marshalCount=0;
//            }
//            return RestResult.success().setData(marshalCount);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取车组详情");
//
//        }
//    }
//
//
//    @ApiOperation(value = "获取日计划编号")
//    @GetMapping(value = "/getDay")
//    public Object getDay(String unitCode) {
//        //unitCode=UserShiroUtil.getUserInfo().getDeptCode();
//        unitCode="A001";
//        Map<String, Object> result = new HashMap<>();
//        try {
//            String dayplanId = iRemoteService.getDayPlanId(unitCode);
//            //获取用户班组
//            Collection<Temple> workTeamlist = new ArrayList<Temple>();
////            ShiroUser ShiroUser = ShiroKit.getUser();
////            Map<String, SysOrgan> mapTree = ShiroUser.getOrganMap();
////            SysOrgan sysTeam = mapTree.get("BD_06");// 用户班组
//            Temple t1 = new Temple();
////            t1.setId(sysTeam.getOrganCode1());
////            t1.setName(sysTeam.getOrganName());
//
//            t1.setId("1");
//            t1.setName("test");
//            workTeamlist.add(t1);
//            result.put("dayPlanId", dayplanId);
//            result.put("workGroup", workTeamlist);
//            result.put("code", 1);
//            result.put("msg", "成功");
//            return result;
//        } catch (Exception ex) {
//            logger.error(ex.getMessage());
//            result.put("code", 0);
//            result.put("rows", null);
//            result.put("msg", "失败");
//        }
//        return result;
//    }
//
//    @ApiOperation(value = "获取修成任务")
//    @GetMapping("/getPacketTaskByDayplanIdAndTrainsetId")
//    public Object getPacketTaskByDayplanIdAndTrainsetId(String dayPlanId, String trainsetIdStr,String deptCode){
//        try{
//            List<String> trainsetIds=new ArrayList<>();
//            if (trainsetIdStr != null && !trainsetIdStr.equals("")) {
//                trainsetIds = Arrays.asList(trainsetIdStr.split(","));
//            }
//            List<PacketTask> packetTasks=new ArrayList<>();
//            deptCode=UserShiroUtil.getUserInfo().getDeptCode();
//            dayPlanId=iRemoteService.getDayPlanId(deptCode);
//            for (String trainsetId : trainsetIds) {
//                PacketTask packetTask=iRemoteService.getPacketTaskByDayplanIdAndTrainsetId(dayPlanId,trainsetId,deptCode);
//                packetTasks.add(packetTask);
//            }
//            PacketTask p=new PacketTask();
//            p.setTrainsetId("CRH2198A");
//            p.setOneCyc(1);
//            p.setTwoCyc(2);
//            p.setFaultNumber("6");
//            List<String> l=new ArrayList<>();
//            l.add("123");
//            l.add("321");
//            p.setItemId(l);
//            packetTasks.add(p);
//            return RestResult.success().setData(packetTasks);
//        }catch (Exception e){
//            return RestResult.fromException(e, logger, "获取修成任务");
//        }
//    }
//
//
//
//
//}
