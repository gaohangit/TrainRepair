package com.ydzbinfo.emis.trainRepair.trainMonitor.controller;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetImageDict;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetImageDictService;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description 车组图片字典
 * @createDate 2021/3/2 9:04
 **/
@RestController
@RequestMapping("/trainsetImageDict")
public class TrainsetImageDictController {

    protected static final Logger logger = LoggerFactory.getLogger(TrainsetImageDictController.class);

    
    @Resource
    TrainsetImageDictService trainsetImageDictService;

    @ApiOperation(value = "获取所有车组字典")
    @GetMapping("/getTrainsetImageDictList")
    public Object getTrainsetImageDictList(String trainType){
        try{
            List<TrainsetImageDict> trainsetImageDictList = trainsetImageDictService.getTrainsetImageDictList(trainType);
            return RestResult.success().setData(trainsetImageDictList);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取所有车组字典");
        }
    }


    @ApiOperation(value = "获取车组字典")
    @GetMapping("/getTrainsetImageDict")
    public Object getTrainsetImageDict(TrainsetImageDict trainsetImageDict){
        try{
            TrainsetImageDict trainsetImage = trainsetImageDictService.getTrainsetImageDict(trainsetImageDict);
            return RestResult.success().setData(trainsetImage);
        }catch (Exception e){
            return RestResult.fromException(e, logger, "获取车组字典");
        }
    }

    @ApiOperation(value = "新增车组字典")
    @PostMapping("/addTrainsetImageDict")
    public Object addTrainsetImageDict(@RequestBody TrainsetImageDict trainsetImageDict){
        try{
            trainsetImageDictService.addTrainsetImageDict(trainsetImageDict);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "新增车组字典");
        }
    }

    @ApiOperation(value = "修改车组字典")
    @PostMapping("/updTrainsetImageDict")
    public Object updTrainsetImageDict(@RequestBody TrainsetImageDict trainsetImageDict){
        try{
            trainsetImageDictService.updTrainsetImageDict(trainsetImageDict);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "修改车组字典");

        }
    }

    @ApiOperation(value = "删除车组字典")
    @PostMapping("/delTrainsetImageDict")
    public Object delTrainsetImageDict(@RequestBody TrainsetImageDict trainsetImageDict){
        try{
            trainsetImageDictService.delTrainsetImageDict(trainsetImageDict);
            return RestResult.success();
        }catch (Exception e){
            return RestResult.fromException(e, logger, "删除车组字典");

        }
    }


}
