package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.ydzbinfo.emis.trainRepair.taskAllot.model.GroupModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.PerSonNelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/22 17:54
 **/
@RestController
@RequestMapping("perSonNel")
public class PerSonNelController {

    @Resource
    private PerSonNelService perSonNelService;
    @ApiOperation("人员选择查询")
    @GetMapping(value = "/geterSonNelList")
    public Object geterSonNelList(String deptCode) {
        List<GroupModel> groupModels =perSonNelService.getPerSonNelList(deptCode,null);
        //test +++
        return groupModels;
    }
}
