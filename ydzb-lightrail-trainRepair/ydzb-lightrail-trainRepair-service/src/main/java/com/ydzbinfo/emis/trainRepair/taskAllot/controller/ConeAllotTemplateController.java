package com.ydzbinfo.emis.trainRepair.taskAllot.controller;



import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotTemplate;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyCOneallotTemplateService;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.Api;
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
 * <p>
 * 前端控制器
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
@Controller
@RequestMapping("/xzyCOneallotTemplate")
@Api("派工配置模板管理")
public class ConeAllotTemplateController {

    protected final static Logger logger = LoggerFactory.getLogger(ConeAllotTemplateController.class);

    @Autowired
    IXzyCOneallotTemplateService oneallotTemplateService;

    /**
     * @author: duanzefan
     * @Date: 2020/9/24 15:54
     * @Description: 根据编组数量获取配置模板
     */
    @ApiOperation("根据编组数量获取配置模板")
    @RequestMapping(value = "/getAllTemplateByMarshalNum")
    @ResponseBody
    public Map getAllTemplateByMarshalNum(String marshalNum) {
        Map<String, Object> result = new HashMap<>();
        List<XzyCOneallotTemplate> oneallotTemplates = oneallotTemplateService.getAllTemplateByMarshalNum(marshalNum);
        result.put("data", oneallotTemplates);
        return result;
    }
    /**
     * @author: duanzefan
     * @Date: 2020/9/22 10:42
     * @Description: 根据条件获取配置模板
     */
    @ApiOperation("根据条件获取配置模板")
    @RequestMapping(value = "/getOneAllotTemplate")
    @ResponseBody
    @BussinessLog(value = "根据条件获取配置模板", key = "/xzyCOneallotTemplate/getOneAllotTemplate", type = "04")
    public List<XzyCOneallotTemplate> getOneAllotTemplate(@RequestBody Map<String,String> map){
        return oneallotTemplateService.getTemplateListByParam(map);
    }

}

