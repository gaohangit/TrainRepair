package com.ydzbinfo.emis.trainRepair.taskAllot.controller;


import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyBTaskallottypeDict;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyBTaskallottypeDictService;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
@Controller
@RequestMapping("/xzyBTaskallottypeDict")
@Api(description = "派工类型字典管理")
public class TaskAllotTypeDictController {

    protected static final Logger logger = LoggerFactory.getLogger(TaskAllotTypeDictController.class);

    @Autowired
    IXzyBTaskallottypeDictService taskallottypeDictService;

    /**
     * @author: duanzefan
     * @Date: 2020/9/21 14:57
     * @Description: 获取全部任务类型字典
     */
    @ApiOperation("获取全部任务类型字典")
    @RequestMapping(value = "/getTaskAllotTypeDict")
    @ResponseBody
    @BussinessLog(value = "获取全部任务类型字典", key = "/xzyBTaskallottypeDict/getTaskAllotTypeDict", type = "04")
    @Transactional
    public List<XzyBTaskallottypeDict> getTaskAllotTypeDict(){
        return taskallottypeDictService.getTaskAllotTypeDict();
    }

    /**
     * @author: duanzefan
     * @Date: 2020/9/27 16:30
     * @Description: 根据code获取类型
     */
    @ApiOperation("根据code获取类型")
    @RequestMapping(value = "/getTaskAllotTypeByCode")
    @ResponseBody
    @BussinessLog(value = "根据code获取类型", key = "/xzyBTaskallottypeDict/getTaskAllotTypeByCode", type = "04")
    public XzyBTaskallottypeDict getTaskAllotTypeByCode(String itemTypeCode){
        return taskallottypeDictService.getTaskAllotTypeByCode(itemTypeCode);
    }

}

