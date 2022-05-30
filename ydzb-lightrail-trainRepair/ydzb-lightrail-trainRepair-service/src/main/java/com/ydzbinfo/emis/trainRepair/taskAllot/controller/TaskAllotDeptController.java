package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotdept;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotDeptService;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: duanzefan
 * @Date: 2020/9/22 19:17
 * @Description: 派工部门管理
 */
@Controller
@RequestMapping("/xzyMTaskAllotDept")
public class TaskAllotDeptController {
    protected static final Logger logger = LoggerFactory.getLogger(TaskAllotDeptController.class);

    @Autowired
    XzyMTaskAllotDeptService taskAllotDeptService;

    /**
     * @author: duanzefan
     * @Date: 2020/9/22 19:20
     * @Description: 获取派工部门集合
     */
    @ApiOperation("获取派工部门集合")
    @RequestMapping(value = "/getTaskAllotDeptByCarPart")
    @ResponseBody
    @BussinessLog(value = "获取派工部门集合", key = "/xzyMTaskAllotDept/getTaskAllotDeptByCarPart", type = "04")
    public List<XzyMTaskallotdept> getTaskAllotDeptByCarPart(@RequestBody String processId){
        return taskAllotDeptService.getTaskAllotDeptByCarPart(processId);
    }

    /**
     * @author: duanzefan
     * @Date: 2020/10/16 17:17
     * @Description: 根据条件获取派工部门
     */
    @ApiOperation("根据条件获取派工部门")
    @RequestMapping(value = "/getDeptByDeptCode")
    @ResponseBody
    @BussinessLog(value = "根据条件获取派工部门", key = "/xzyMTaskAllotDept/getDeptByDeptCode", type = "04")
    public List<XzyMTaskallotdept> getDeptByDeptCode(String deptCode){
        Map<String,String> param = new HashMap<>();
        param.put("deptCode",deptCode);
        return taskAllotDeptService.getTaskAllotDepts(param);
    }
}
