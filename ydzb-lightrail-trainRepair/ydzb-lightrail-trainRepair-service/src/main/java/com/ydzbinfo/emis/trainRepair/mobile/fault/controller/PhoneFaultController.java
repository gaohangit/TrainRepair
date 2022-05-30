package com.ydzbinfo.emis.trainRepair.mobile.fault.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.common.userutil.UserUtil;
import com.ydzbinfo.emis.guns.config.TrainRepairMobileProperties;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.impl.RemoteServiceImpl;
import com.ydzbinfo.emis.trainRepair.faultconfig.controller.FaultInputDictController;
import com.ydzbinfo.emis.trainRepair.faultconfig.querymodel.FaultInputDict;
import com.ydzbinfo.emis.trainRepair.faultconfig.service.IFaultInputDictService;
import com.ydzbinfo.emis.trainRepair.faultconfig.util.FaultUtil;
import com.ydzbinfo.emis.trainRepair.mobile.fault.service.IPhoneFaultService;
import com.ydzbinfo.emis.trainRepair.mobile.model.FaultSearch;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.*;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.DESUtil;
import com.ydzbinfo.emis.utils.RestResultUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.OrderBy;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.upload.UpLoadFileUtils;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import com.ydzbinfo.hussar.core.exception.BizExceptionEnum;
import com.ydzbinfo.hussar.core.exception.HussarException;
import com.ydzbinfo.hussar.system.bsp.organ.SysStaff;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.orderBy;

/**
 * Description: 故障控制器
 * Author: wuyuechang
 * Create Date Time: 2021/4/29 8:51
 * Update Date Time: 2021/4/29 8:51
 *
 * @see
 */
@RestController
@RequestMapping("/mobileFault")
@Slf4j
public class PhoneFaultController {

    @Autowired
    private TrainRepairMobileProperties trainRepairMobileProperties;

    //日志记录
    protected static final Logger logger = LoggerFactory.getLogger(FaultInputDictController.class);


    @Autowired
    private IPhoneFaultService phoneFaultService;

    //故障快速录入配置服务
    @Autowired
    IFaultInputDictService faultInputDictService;

    @Autowired
    IRemoteService remoteService;

    /**
     * @author: wuyuechang
     * @Description: 新增故障
     * @param: unitCode    所编码
     * @param: dayPlanID    日计划id
     * @param: workerId    人员编码
     * @param: deptCode    班组code
     * @date: 2021/4/29 15:15
     */
    @PostMapping(value = "/add", produces = "text/plain")
    @ApiOperation("手持机新增故障")
    @BussinessLog(value = "手持机新增故障", key = "/mobileFault/add", type = "04")
    public String addFault(@RequestParam String RWMT, HttpServletRequest httpServletRequest) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            if (httpServletRequest instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;
                List<MultipartFile> multipartFileList = multipartHttpServletRequest.getFiles("image");
                JSONArray jsonArray = new JSONArray();
                for (MultipartFile multipartFile : multipartFileList) {
                    String base64EncoderImg = "data:" + multipartFile.getContentType() + ";base64," + Base64.getEncoder().encodeToString(multipartFile.getBytes());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("data", base64EncoderImg);
                    jsonObject.put("fileName", multipartFile.getOriginalFilename());
                    jsonArray.add(jsonObject);
                }
                param.put("multipartFile", jsonArray);
            }
            phoneFaultService.addFault(param);
            result.put("msg", "成功");
            result.put("data", "");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "手持机新增故障失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @return 故障列表
     * @author: wuyuechang
     * @Description: 手持机故障查询
     * @param: unitCode    所编码
     * @param: dayPlanID    日计划id
     * @param: workerId    人员编码
     * @param: deptCode    班组code
     * @date: 2021/4/29 15:15
     */
    @GetMapping(value = "/faultSearch", produces = "text/plain")
    @ApiOperation("手持机故障查询")
    @BussinessLog(value = "手持机故障查询", key = "/mobileFault/faultSearch", type = "04")
    public String faultSearch(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            FaultSearch faultSearch = new FaultSearch();
            faultSearch.setTrainsetId(param.getString("trainsetId"));
            faultSearch.setStartTime(param.getString("startTime"));
            faultSearch.setEndTime(param.getString("endTime"));
            faultSearch.setFaultGrade(param.getString("faultGrade"));
            faultSearch.setFaultSourceCode(param.getString("faultSourceCode"));
            faultSearch.setSubFunctionClassId(param.getString("subFunctionClassId"));
            faultSearch.setFaultPartId(param.getString("faultPartId"));
            faultSearch.setLocatetionNum(param.getString("locatetionNum"));
            faultSearch.setSerialNum(param.getString("serialNum"));
            faultSearch.setCarNo(param.getString("carNo"));
            faultSearch.setPartTypeId(param.getString("partTypeId"));
            faultSearch.setFindFaultMan(param.getString("findFaultMan"));
            faultSearch.setFaultFindUnitCode(param.getString("faultFindUnitCode"));
            faultSearch.setOrgCode(param.getString("orgCode"));
            faultSearch.setFaultFindBranchCode(param.getString("faultFindBranchCode"));
            faultSearch.setDealWithDescCode(param.getString("dealWithDescCode"));
            faultSearch.setDealMethodCode(param.getString("dealMethodCode"));
            faultSearch.setRepairManCode(param.getString("repairManCode"));
            faultSearch.setDealBranchCode(param.getString("dealBranchCode"));
            faultSearch.setDealStartTime(param.getString("dealStartTime"));
            faultSearch.setDealEndTime(param.getString("dealEndTime"));
            faultSearch.setFaultTreeId(param.getString("faultTreeId"));
            faultSearch.setDealDeptName(param.getString("dealDeptName"));
            faultSearch.setPageNum(param.getInteger("pageNum"));
            faultSearch.setPageSize(param.getInteger("pageSize"));
            Page<JSONObject> data = phoneFaultService.faultSearch(faultSearch);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "手持机故障查询获取数据失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @return 故障列表
     * @author: wuyuechang
     * @Description: 添加已存在故障的故障发现信息
     * @date: 2021/4/29 15:15
     */
    @PostMapping(value = "/addFaultFind", produces = "text/plain")
    @ApiOperation("添加已存在故障的故障发现信息")
    @BussinessLog(value = "手持机添加已存在故障的故障发现信息", key = "/mobileFault/addFaultFind", type = "04")
    public String addFaultFind(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            phoneFaultService.addFaultFind(param);
            result.put("msg", "成功");
            result.put("data", "");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "添加已存在故障的故障发现信息失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @PostMapping(value = "/addFaultQa", produces = "text/plain")
    @ApiOperation("添加故障质检")
    @BussinessLog(value = "添加故障质检", key = "/mobileFault/addFaultQa", type = "04")
    public String addFaultQa(@RequestParam String RWMT) {
        RestResult restResult;
        try {
            String jsonStringParam = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(jsonStringParam);
            if (param != null) {
                AddQaVo addQaVo = param.toJavaObject(AddQaVo.class);
                String message = remoteService.addFaultQa(addQaVo);
                restResult = RestResult.success().setMsg(message);
            } else {
                throw RestRequestException.normalFail("失败,参数为空");
            }
        } catch (Exception e) {
            logger.error("/mobileFault/addFaultQa----调用添加故障质检接口出错...", e);
            restResult = RestResult.fromException(e, logger, "添加故障质检失败");
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSON.toJSONString(restResult));
    }

    /**
     * @author: wuyuechang
     * @Description: 添加故障处理（故障回填）
     * @date: 2021/4/29 15:15
     */
    @PostMapping(value = "/addFaultDeal", produces = "text/plain")
    @ApiOperation("添加故障处理（故障回填）")
    @BussinessLog(value = "添加故障处理（故障回填）", key = "/mobileFault/addFaultDeal", type = "04")
    public String addFaultDeal(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            phoneFaultService.addFaultDeal(param);
            result.put("msg", "回填成功");
            result.put("data", "");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "添加故障处理（故障回填）失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /***
     * @author: 冯帅
     * @desc: 获取故障快速录入配置接口
     * @date: 2021/8/30
     * @param: [RWMT]
     * @return: java.lang.String
     */
    @PostMapping(value = "/getFaultInputDictList", produces = "text/plain")
    @ApiOperation("获取故障快速录入配置接口")
    @BussinessLog(value = "获取故障快速录入配置接口", key = "/mobileFault/getFaultInputDictList", type = "04")
    public String getFaultInputDictList(@RequestParam String RWMT) {
        logger.info("/mobileFault/getFaultInputDictList----开始调用手持机获取故障快速录入配置接口接口...");
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            FaultInputDict queryModel = JSON.parseObject(key, FaultInputDict.class);
            Objects.requireNonNull(queryModel).setFlag("1");
            List<ColumnParam<FaultInputDict>> columnParamList = FaultUtil.toColumnParamList(queryModel);
            List<OrderBy<FaultInputDict>> orderByList = Collections.singletonList(
                orderBy(FaultInputDict::getRecordTime, false)
            );
            List<FaultInputDict> faultInputDictList = MybatisPlusUtils.selectList(
                faultInputDictService,
                columnParamList,
                orderByList
            );
            result.put("msg", "成功");
            result.put("data", faultInputDictList);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultInputDictList----调用手持机获取故障快速录入配置接口接口出错..." + e.getMessage());
            RestResult restResult = RestResult.fromException(e, log, "调用手持机获取故障快速录入配置接口接口出错");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        logger.info("/mobileFault/getFaultInputDictList----调用手持机获取故障快速录入配置接口接口结束...");
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @PostMapping(value = "/searchQaPage", produces = "text/plain")
    @ApiOperation("故障质检分页查询")
    @BussinessLog(value = "故障质检分页查询", key = "/mobileFault/searchQaPage", type = "04")
    public String searchQaPage(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            // JSONObject param = JSONObject.parseObject(key);
            QueryFaultQA queryFaultQA = JSONObject.parseObject(key).toJavaObject(QueryFaultQA.class);
            RemoteServiceImpl.ResponseForFaultService<Page<JSONObject>> response = remoteService.searchQaPage(queryFaultQA);
            result.put("msg", "成功");
            result.put("data", response);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/searchQaPage----调用故障质检分页查询接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "故障质检分页查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getFaultSource", produces = "text/plain")
    @ApiOperation("获取故障来源")
    @BussinessLog(value = "获取故障来源", key = "/mobileFault/getFaultSource", type = "04")
    public String getFaultSource() {
        JSONObject result = new JSONObject();
        try {
            List<FaultSource> res = remoteService.getFaultSource();
            result.put("msg", "成功");
            result.put("data", res);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultSource----调用获取故障来源接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "获取故障来源查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getFaultSourceNew", produces = "text/plain")
    @ApiOperation("获取故障来源(新)")
    @BussinessLog(value = "获取故障来源(新)", key = "/mobileFault/getFaultSourceNew", type = "04")
    public String getFaultSourceNew() {
        JSONObject result = new JSONObject();
        try {
            JSONArray data = remoteService.getFaultSourceNew();
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultSourceNew----调用获取故障来源(新)接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "获取故障来源(新)查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getDealWith", produces = "text/plain")
    @ApiOperation("获取处理方法字典")
    @BussinessLog(value = "获取处理方法字典", key = "/mobileFault/getDealWith", type = "04")
    public String getDealWith(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String dealWithType = param.getString("dealWithType");
            JSONArray data = remoteService.getDealWith(dealWithType);
            result.put("msg", "成功");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getDealWith----获取处理方法字典接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "获取处理方法字典查询失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/selectQaRecordPage", produces = "text/plain")
    @ApiOperation("查询质检记录")
    @BussinessLog(value = "查询质检记录", key = "/mobileFault/selectQaRecordPage", type = "04")
    public String selectQaRecordPage(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            QueryFaultQaRecord queryModel = JSON.parseObject(key, QueryFaultQaRecord.class);
            Page<JSONObject> qaRecordPage = remoteService.selectQaRecordPage(queryModel);
            result.put("data", qaRecordPage);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/selectQaRecordPage----调用查询质检记录接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "查询质检记录失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getPersonByDept", produces = "text/plain")
    @ApiOperation("根据班组获取班组下所有人员")
    @BussinessLog(value = "根据班组获取班组下所有人员", key = "/mobileFault/getPersonByDept", type = "04")
    public String getPersonByDept(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String organCode = param.getString("organCode");
            List<Map<String, String>> resList = new ArrayList<>();
            if (!ObjectUtils.isEmpty(key)) {
                List<SysStaff> workerList = UserUtil.getStaffListStruByDept(organCode);
                if (workerList != null && workerList.size() > 0) {
                    workerList = workerList.stream().filter(t -> (!StringUtils.isBlank(t.getStaffId())) || (!StringUtils.isBlank(t.getName()))).collect(Collectors.toList());
                    workerList = CommonUtils.getDistinctList(workerList, t -> t.getStaffId());
                }
                resList = workerList.stream().map(entity -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("workId", entity.getStaffId());
                    item.put("workName", entity.getName());
                    return item;
                }).collect(Collectors.toList());
            }
            result.put("data", resList);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getPersonByDept----调用根据班组获取班组下所有人员接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "根据班组获取班组下所有人员失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getFaultDataByFaultId", produces = "text/plain")
    @ApiOperation("查询附件信息")
    @BussinessLog(value = "查询附件信息", key = "/mobileFault/getFaultDataByFaultId", type = "04")
    public String getFaultDataByFaultId(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            JSONArray faultData = remoteService.getFaultDataByFaultId(param.getString("faultId"));
            result.put("data", faultData);
            result.put("msg", "成功");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultDataByFaultId----查询附件信息接口出错...", e);
            RestResult restResult = RestResult.fromException(e, logger, "查询附件信息失败");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @PostMapping(value = "/filedownload", produces = "text/plain")
    @ApiOperation("下载附件信息")
    @BussinessLog(value = "下载附件信息", key = "/mobileFault/filedownload", type = "04")
    public void filedownload(@RequestParam String RWMT, HttpServletResponse response) {
        InputStream bis = null;
        BufferedOutputStream out = null;
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String faultId = param.getString("faultId");
            String fileName = param.getString("fileName");
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            Boolean isImageType = UpLoadFileUtils.isImageType(fileSuffix);
            Boolean isVideoType = UpLoadFileUtils.isVideoType(fileSuffix);
            Boolean isAudioType = UpLoadFileUtils.isVideoType(fileSuffix);
            String fileType = "";
            if (isImageType) {
                fileType = "image";
            } else if (isVideoType) {
                fileType = "video";
            } else if (isAudioType) {
                fileType = "audio";
            } else {
                fileType = "document";
            }
            JSONArray jsonArray = remoteService.filedownload(faultId, fileName);
            if (jsonArray.size() > 0) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                byte[] file = jsonObject.getBytes("file");
                bis = new BufferedInputStream(new ByteArrayInputStream(file), 1024 * 10);
                fileName = URLEncoder.encode(fileName, "UTF-8");
                response.addHeader("fileName", fileName);
                response.addHeader("fileType", fileType);
                response.setContentType("multipart/form-data");
                out = new BufferedOutputStream(response.getOutputStream());
                int len = 0;
                int i = bis.available();
                byte[] buff = new byte[i];
                while ((len = bis.read(buff)) > 0) {
                    out.write(buff, 0, len);
                    out.flush();
                }
                out.write(file);
                out.flush();
            }
        } catch (FileNotFoundException e) {
            logger.error("/mobileFault/filedownload----文件未找到...", e);
            throw new HussarException(BizExceptionEnum.FILE_NOT_FOUND);
        } catch (UnsupportedEncodingException e) {
            logger.error("/mobileFault/filedownload----不支持的编码异常...", e);
            throw new HussarException(BizExceptionEnum.DOWNLOAD_ERROR);
        } catch (IOException e) {
            logger.error("/mobileFault/filedownload----IO异常...", e);
            throw new HussarException(BizExceptionEnum.DOWNLOAD_ERROR);
        }
    }

}
