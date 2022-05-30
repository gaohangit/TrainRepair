package com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.controller;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model.*;
import com.ydzbinfo.emis.utils.DateUtils;
import com.ydzbinfo.emis.utils.RestRequestKitUseLogger;
import com.ydzbinfo.emis.utils.ServiceNameEnum;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import com.ydzbinfo.hussar.core.reqres.response.ResponseData;
import com.ydzbinfo.hussar.core.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Description: 消息管理控制器
 * Author: wuyuechang
 * Create Date Time: 2021/4/20 16:23
 * Update Date Time: 2021/4/20 16:23
 *
 * @see
 */
@Controller
@RequestMapping("/messagemanagent")
public class messagemanagentEndpoint {

    protected static final Logger logger = LoggerFactory.getLogger(messagemanagentEndpoint.class);

    private final String messageService = ServiceNameEnum.MessageService.getId();

    /**
     * 消息通知-获取列表
     */
    @BussinessLog(value = "消息通知-获取列表", key = "/messagemanagent/getList", type = "04")
    @GetMapping(value = "/getList")
    @ApiOperation(value = "消息通知-获取列表", notes = "消息通知-获取列表")
    @ResponseBody
    public Object getList(queryMessage queryMessage) {
        logger.info("开始时间："+queryMessage.getStartdate()+",结束时间："+queryMessage.getEnddate()+",用户名："+queryMessage.getUserAccount()
                +",消息类型："+queryMessage.getType()+",消息状态："+queryMessage.getState());
        Map<String, Object> result = new HashMap<>();
        try {
            String account = queryMessage.getUserAccount();
            String startdate = queryMessage.getStartdate();
            String enddate = queryMessage.getEnddate();
            //如果开始时间为空，则默认为7天前
            if(StringUtils.isEmpty(startdate)){
                Date now = new Date();
                Date sevenDaysAgo = DateUtils.getDateBeforeOrAfterDays(now,7);
                startdate = DateUtils.dateTime24ToStr(sevenDaysAgo);
            }
            //如果结束时间为空，则默认为当前时间
            if(StringUtils.isEmpty(enddate)){
                Date now = new Date();
                enddate = DateUtils.dateTime24ToStr(now);
            }
            String type = queryMessage.getType();// 消息类型
            // 2 全部,0已读,1 未读
            String state = queryMessage.getState();// 消息状态
            int limit = (int) queryMessage.getLimit();
            int Page = (int) queryMessage.getPage();
            String orderInfo = queryMessage.getOrderInfo();
            String orderAscOrDesc = queryMessage.getOrderAscOrDesc();
            MessageInfoPar queryPar = new MessageInfoPar();
            queryPar.setUserId(account);
            queryPar.setStartDate(startdate);
            queryPar.setEndDate(enddate);
            queryPar.setType(type);
            queryPar.setLimit(limit);
            queryPar.setPage(Page);
            ResponseData<Object> result12 = null;
            if (state == null || state.equals("") || state.equals("2")) {
                if (type.equals("-1")) {
                    String url = "/message/getMyMessageList?limit={limit}&page={page}&startDate={startDate}&endDate={endDate}&userId={userId}";
                    result12 = new RestRequestKitUseLogger<ResponseData<Object>>(messageService, logger) {
                    }.getObject(url, queryPar.getLimit(), queryPar.getPage(), queryPar.getStartDate(),
                            queryPar.getEndDate(), queryPar.getUserId());
                } else {
                    String url = "/message/getMyMessageList?limit={limit}&page={page}&startDate={startDate}&endDate={endDate}&userId={userId}&type={type}";
                    result12 = new RestRequestKitUseLogger<ResponseData<Object>>(messageService, logger) {
                    }.getObject(url, queryPar.getLimit(), queryPar.getPage(), queryPar.getStartDate(),
                            queryPar.getEndDate(), queryPar.getUserId(), queryPar.getType());
                }
            } else if (state.equals("0") || state.equals("1")) {// 已读//未读
                queryPar.setRecFlag(state);
                if (type.equals("-1")) {
                    String url = "/message/getMyMessageList?limit={limit}&page={page}&type={type}&subject={subject}&startDate={startDate}&endDate={endDate}&recFlag={recFlag}&userId={userId}";
                    result12 = new RestRequestKitUseLogger<ResponseData<Object>>(messageService, logger) {
                    }.getObject(url, queryPar.getLimit(), queryPar.getPage(), queryPar.getType(), queryPar.getSubject(),
                            queryPar.getStartDate(), queryPar.getEndDate(), queryPar.getRecFlag(),
                            queryPar.getUserId());
                } else {
                    String url = "/message/getMyMessageList?limit={limit}&page={page}&type={type}&subject={subject}&startDate={startDate}&endDate={endDate}&recFlag={recFlag}&userId={userId}";
                    result12 = new RestRequestKitUseLogger<ResponseData<Object>>(messageService, logger) {
                    }.getObject(url, queryPar.getLimit(), queryPar.getPage(), queryPar.getType(), queryPar.getSubject(),
                            queryPar.getStartDate(), queryPar.getEndDate(), queryPar.getRecFlag(),
                            queryPar.getUserId());
                }
            }
            List<message> list = new ArrayList<message>();
            logger.error(result12.getData().toString());
            Object object = result12.getData();
            Integer total = 0;
            if (object != null) {
                JSONObject jSONObject = (JSONObject) object;
                List<JSONObject> jsonlist = (List<JSONObject>) jSONObject.get("rows");
                total = (Integer) jSONObject.get("total");
                for (JSONObject json : jsonlist) {
                    MessageInfo msg = json.parseObject(json.toString(), MessageInfo.class);
                    String subject = msg.getSubject();
                    String content = msg.getContent();
                    String id = msg.getMessageId();
                    String typeStr = msg.getType();// 消息类型
                    String userName = msg.getUserName();// 发送人
                    String sendTime = msg.getAddDate();// 发送时间
                    String recflag = msg.getRecFlag();// 已读未读
                    message message = new message();
                    message.setId(id);
                    message.setContent(content);
                    message.setState(recflag);
                    message.setSender(userName);
                    message.setTime(sendTime);
                    message.setType(typeStr);
                    list.add(message);
                }
            }
            result.put("total", total);
            result.put("rows", list);
            result.put("msg", "成功");
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result.put("total", null);
            result.put("rows", null);
            result.put("msg", "失败");
        }
        return result;
    }

    /**
     * 消息通知-删除一条
     */
    @BussinessLog(value = "消息通知-删除一条", key = "/messagemanagent/delete", type = "04")
    @GetMapping(value = "/delete")
    @ApiOperation(value = "消息通知-删除一条", notes = "消息通知-删除一条")
    @ResponseBody
    public Object delete(String id, String userAccount, String workName) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (id == null || id.equals("")) {
                result.put("total", null);
                result.put("rows", null);
                result.put("msg", "失败");
            }
            if (id == null || id.equals("")) {
                result.put("total", null);
                result.put("rows", null);
                result.put("code", 0);
                result.put("msg", "失败");
            }
            List<MessageRec> msgRecList = new ArrayList<>();
            MessageRec msg = new MessageRec();
            msg.setUserId(userAccount);
            msg.setUserName(workName);
            msg.setMessageId(id);
            msgRecList.add(msg);
            ResponseData<String> result12 = null;
            String url = "/message/deleteMyMessage";
            result12 = new RestRequestKitUseLogger<ResponseData<String>>(messageService, logger) {
            }.postObject(url, msgRecList);
            result.put("msg", msg);
            result.put("code", 1);
            result.put("msg", "成功");
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    /**
     * 消息通知-批量更新已读状态列表
     */
    @BussinessLog(value = "消息通知-批量更新已读状态列表", key = "/messagemanagent/updateState", type = "04")
    @PostMapping(value = "/updateState")
    @ApiOperation(value = "消息通知-批量更新已读状态列表", notes = "消息通知-批量更新已读状态列表")
    @ResponseBody
    public Object updateState(String ids, String userAccount, String workName) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (ids == null || ids.equals("")) {
                result.put("total", null);
                result.put("rows", null);
                result.put("code", 0);
                result.put("msg", "失败");
            }
            String[] array = ids.split(",");
            for (String id : array) {
                MessageRec msg = new MessageRec();
                msg.setUserId(userAccount);
                msg.setUserName(workName);
                msg.setMessageId(id);
                String url = "/message/updateRecFlag";
                ResponseData<String> result12 = new RestRequestKitUseLogger<ResponseData<String>>(messageService, logger) {
                }.postObject(url, msg);
                String msg1 = result12.getMessage();
            }
            //result.put("msg", msg);
            result.put("code", 1);
            result.put("msg", "成功");
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result.put("total", null);
            result.put("rows", null);
            result.put("code", 0);
            result.put("msg", "失败");
        }
        return result;
    }
}
