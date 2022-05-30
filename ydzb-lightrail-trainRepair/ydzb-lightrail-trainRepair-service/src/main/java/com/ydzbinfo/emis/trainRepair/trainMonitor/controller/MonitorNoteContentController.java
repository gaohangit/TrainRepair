package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.MonitorNoteContent;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.MonitorNoteContentService;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description调车记事
 * @createDate 2021/3/1 9:47
 **/
@RestController
@RequestMapping("/monitorNoteContent")
public class MonitorNoteContentController {
    protected static final Logger logger = LoggerFactory.getLogger(MonitorNoteContentController.class);

        @Resource
    MonitorNoteContentService monitornotecontentService;

    @ApiOperation(value = "获取所有调车记事")
    @GetMapping("/getMonitornotecontents")
    public Object getMonitornotecontents(){
        try{
            List<MonitorNoteContent> monitorNoteContents = monitornotecontentService.getMonitornotecontentsById();
            return RestResult.success().setData(monitorNoteContents);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有调车记事");

        }
    }
    @ApiOperation(value = "获取调车记事")
    @GetMapping("/getMonitornotecontentById")
    public Object getMonitornotecontentById(MonitorNoteContent monitornotecontent){
        try{
            MonitorNoteContent monitornotecontents= monitornotecontentService.getMonitornotecontentById(monitornotecontent);
            return RestResult.success().setData(monitornotecontents);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取调车记事");

        }
    }

    @ApiOperation(value = "新增调车记事")
    @PostMapping("/addMonitornotecontent")
    public Object addMonitornotecontent(@RequestBody MonitorNoteContent monitornotecontent){
        try{
            monitornotecontentService.addMonitornotecontent(monitornotecontent);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "新增调车记事");

        }
    }
    @ApiOperation(value = "修改调车记事")
    @PostMapping("/updMonitornotecontent")
    public Object updMonitornotecontent(@RequestBody MonitorNoteContent monitornotecontent){
        try{
            monitornotecontentService.updMonitornotecontent(monitornotecontent);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "修改调车记事");

        }
    }

    @ApiOperation(value = "删除调车记事")
    @PostMapping("/delMonitornotecontent")
    public Object delMonitornotecontent(@RequestBody MonitorNoteContent monitornotecontent){
        try{
            monitornotecontentService.delMonitornotecontent(monitornotecontent);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "删除调车记事");

        }
    }


}
