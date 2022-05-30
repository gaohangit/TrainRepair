package com.ydzbinfo.emis.trainRepair.taskAllot.controller;


import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyBTaskallotshowDict;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyBTaskallotshowDictService;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/xzyBTaskallotshowDict")
public class TaskAllotShowDictController {

    protected static final Logger logger = LoggerFactory.getLogger(TaskAllotShowDictController.class);

    @Autowired
    IXzyBTaskallotshowDictService xzyBTaskallotshowDictService;

    /**
     * @author: duanzefan
     * @Date: 2020/9/17 20:15
     * @Description: 查询派工显示字典
     */
    @ApiOperation("查询派工显示字典")
    @RequestMapping(value = "/getShowDictByTaskAllotType")
    @ResponseBody
    @BussinessLog(value = "查询派工显示字典", key = "/xzyBTaskallotshowDict/getShowDictByTaskAllotType", type = "04")
    public List<XzyBTaskallotshowDict> getShowDictByTaskAllotType(String ItemType){
        List<XzyBTaskallotshowDict> xzyBTaskallotshowDictList = xzyBTaskallotshowDictService.getShowDictByTaskAllotType(ItemType);
        return xzyBTaskallotshowDictList;
    }

}

