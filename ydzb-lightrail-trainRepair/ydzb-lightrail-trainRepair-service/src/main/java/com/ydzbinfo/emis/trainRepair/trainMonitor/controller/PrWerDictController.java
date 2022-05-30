package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.PowerDict;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.PrWerDictService;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description 供断电字典
 * @createDate 2021/3/1 18:35
 **/
@RestController
@RequestMapping("/prWerDict")
public class PrWerDictController {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectTrainTypeController.class);

        @Resource
    PrWerDictService prWerDictService;

    @ApiOperation(value = "获取所有供断电字典")
    @GetMapping("/getPrWerDicts")
    public Object getPrWerDicts(){
        try{
            List<PowerDict> prWerDicts = prWerDictService.getPrWerDicts();
            return RestResult.success().setData(prWerDicts);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有供断电字典");

        }
    }

    @ApiOperation(value = "获取供断电字典")
    @GetMapping("/getPrWerDict")
    public Object getPrWerDict(PowerDict prWerDict){
        try{
            PowerDict prWerDicts = prWerDictService.getPrWerDict(prWerDict);
            return RestResult.success().setData(prWerDicts);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取供断电字典");

        }
    }

    @ApiOperation(value = "新增供断电字典")
    @PostMapping("/addPrWerDict")
    public Object addPrWerDict(@RequestBody PowerDict prWerDict){
        try{
            prWerDictService.addPrWerDict(prWerDict);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "新增供断电字典");

        }
    }



    @ApiOperation(value = "修改供断电字典")
    @PostMapping("/updPrWerDict")
    public Object updPrWerDict(@RequestBody PowerDict prWerDict){
        try{
            prWerDictService.updPrWerDict(prWerDict);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "修改供断电字典");

        }
    }

    @ApiOperation(value = "删除供断电字典")
    @PostMapping("/delPrWerDict")
    public Object delPrWerDict(@RequestBody PowerDict prWerDict){
        try{
            prWerDictService.delPrWerDict(prWerDict);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "删除供断电字典");

        }
    }
}
