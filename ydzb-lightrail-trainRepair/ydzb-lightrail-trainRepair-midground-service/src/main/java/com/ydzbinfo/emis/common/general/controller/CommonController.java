package com.ydzbinfo.emis.common.general.controller;

import com.alibaba.fastjson.JSON;
import com.jxdinfo.hussar.common.organutil.OrganUtil;
import com.ydzbinfo.emis.common.bill.billconfig.service.ITemplateSummaryService;
import com.ydzbinfo.emis.common.bill.utils.SsjsonFileUtils;
import com.ydzbinfo.emis.common.general.service.ICommonService;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeParamModel;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummary;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.model.Unit;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据配置
 */
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ICommonService commonService;

    @Autowired
    private IRemoteService remoteService;

    /**
     * 获取单据类型
     */
    @GetMapping(value = "/queryBillTypes")
    public RestResult queryBillTypes(TemplateTypeParamModel templateTypeParamModel) {
        try {
            return RestResult.success().setData(commonService.getBillTypes(templateTypeParamModel));
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 获取单据属性（父子级关系）
     */
    @RequestMapping(value = "/getConfigAttr")
    public RestResult getConfigAttr(String billTypeCode) {
        try {
            List<TemplateAttributeTypeInfo> resultList = commonService.getTemplateAttributeTypes(billTypeCode);
            return RestResult.success().setData(resultList);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 记录单据配置新增获取查询条件
     */
    @RequestMapping(value = "/getQueryCondition")
    public RestResult getQueryCondition(String billTypeCode) {
        try {
            List<TemplateQuery> list = commonService.getTemplateQueryList(billTypeCode);
            return RestResult.success().setData(list);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取条件列表失败");
        }
    }

    /**
     * 单据模板属性取值范围表
     */
    @RequestMapping(value = "/getTemplateValues")
    public RestResult getTemplateValues(String billTypeCode) {
        try {
            List<TemplateValue> list = commonService.getTemplateValues(billTypeCode);
            return RestResult.success().setData(list);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 获取XzyCConfigs配置
     */
    @PostMapping(value = "/getXzyCConfigs")
    public RestResult getXzyCConfigs(@RequestBody ConfigParamsModel configParamsModel) {
        try {
            return RestResult.success().setData(commonService.getXzyCConfigs(configParamsModel));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取XzyCConfigs配置失败");
        }
    }

    /**
     * 获取日计划id
     */
    @GetMapping(value = "/getDayPlanId")
    public RestResult getDayPlanId(String unitCode, String dateTime) {
        try {
            Date dateTimeObj;
            if (dateTime == null) {
                dateTimeObj = new Date();
            } else {
                dateTimeObj = DateTimeUtil.parse(dateTime);
            }
            return RestResult.success().setData(DayPlanUtil.getDayPlanId(unitCode, dateTimeObj));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取获取日计划id失败");
        }
    }


    /**
     * 根据班次获取工作时间
     *
     * @param dayPlanId 班次
     * @return
     */
    @GetMapping(value = "/getWorkTimeByDayPlanId")
    public RestResult getWorkTimeByDayPlanId(@RequestParam String dayPlanId) {
        try {
            return RestResult.success().setData(DayPlanUtil.getWorkTimeByDayPlanId(dayPlanId));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "根据班次获取工作时间失败");
        }
    }

    /**
     * 获取本段下所有运用所
     */
    @GetMapping("/getUnits")
    @ApiOperation("获取本段下所有运用所")
    public Object getUnits(String depotCode) {
        try {
            List<SysOrgan> list = OrganUtil.getOranListByParent(depotCode, "08");
            List<Unit> units = new ArrayList<>();
            for (SysOrgan sysUnit : list) {
                Unit unit = new Unit();
                unit.setUnitCode(sysUnit.getOrganCode());
                unit.setUnitName(sysUnit.getOrganName());
                unit.setUnitAbbr(sysUnit.getShortName());
                units.add(unit);
            }
            return RestResult.success().setData(units);
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取本段下所有运用所失败");
        }
    }

    @GetMapping("/getUser")
    @ApiOperation("获取用户信息")
    public RestResult getUser() {
        try {
            return RestResult.success().setData(UserUtil.getUserInfo());
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取用户失败");
        }
    }


    @Autowired
    private ITemplateSummaryService templateSummaryService;

    /**
     * 获取SSJsonFile 文件的内容
     */
    @GetMapping(value = "/getSSJsonFileContentByTemplateId")
    public RestResult getSSJsonFileContentByTemplateId(String templateId) {
        try {
            if (templateId == null || templateId.equals("")) {
                throw new RuntimeException("模板id为空");
            }
            TemplateSummary templateSummary = templateSummaryService.selectById(templateId);
            if (templateSummary == null) {
                throw new RuntimeException("模板不存在");
            }
            return RestResult.success().setData(SsjsonFileUtils.getSsjsonFileContent(templateSummary.getTemplatePath()));
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取ssjson文件内容失败");
        }
    }


    /**
     * 创建压缩文件
     */
    @PostMapping("/buildZip")
    public void buildZip(MultipartHttpServletRequest request, HttpServletResponse response) {
        try {
            CompressUtils.ZipBuilder zipBuilder = CompressUtils.zipBuilder();
            for (Part part : request.getParts()) {
                zipBuilder.putFile(part.getName(), part.getInputStream());
            }
            response.setContentType("application/zip;" + MediaType.APPLICATION_OCTET_STREAM_VALUE);
            zipBuilder.output(response.getOutputStream());
        } catch (Exception e) {
            try {
                response.reset();// 这步可能会抛出异常
                ServletOutputStream outputStream = response.getOutputStream();
                RestResult fromException = RestResult.fromException(e, logger, null);
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                JSON.writeJSONString(outputStream, fromException);
            } catch (Exception e1) {
                logger.error("压缩失败", e);
            }
        }
    }

    @ApiOperation(value = "获取辆序")
    @GetMapping("/getMarshalCountByTrainType")
    public RestResult getMarshalCountByTrainType(String trainType) {
        try {
            return RestResult.success().setData(remoteService.getMarshalCountByTrainType(trainType));
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取辆序错误");
        }
    }

}
