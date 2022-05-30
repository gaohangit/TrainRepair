package com.ydzbinfo.emis.trainRepair.repairworkflow.controller;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkTypeService;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.Constants;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 高晗
 * @description 关键作业类型
 * @createDate 2021/6/21 11:16
 **/
@RestController
@RequestMapping({"keyWorkType", Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/keyWorkType"})
public class keyWorkTypeController extends BaseController {
    @Autowired
    IKeyWorkTypeService keyWorkTypeService;

    @ApiOperation("查询关键作业类型")
    @GetMapping(value = "/getKeyWorkTypeList")
    public Object getKeyWorkTypeList(String unitCode,@RequestParam(value = "showDelete", defaultValue = "false") boolean showDelete) {
        try {
            List<KeyWorkType> keyWorkTypes = keyWorkTypeService.getKeyWorkTypeList(unitCode,showDelete);
            return RestResult.success().setData(keyWorkTypes);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询关键作业类型错误");
        }
    }

    @ApiOperation("修改关键作业类型")
    @PostMapping(value = "/setKeyWorkType")
    public Object setKeyWorkType(@RequestBody List<KeyWorkType> keyWorkTypes) {
        try {
            keyWorkTypeService.setKeyWorkType(keyWorkTypes);
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改关键作业类型错误");
        }
    }
}
