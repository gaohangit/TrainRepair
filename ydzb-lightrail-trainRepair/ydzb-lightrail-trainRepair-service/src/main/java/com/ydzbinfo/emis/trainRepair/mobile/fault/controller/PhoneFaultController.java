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
 * Description: ???????????????
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

    //????????????
    protected static final Logger logger = LoggerFactory.getLogger(FaultInputDictController.class);


    @Autowired
    private IPhoneFaultService phoneFaultService;

    //??????????????????????????????
    @Autowired
    IFaultInputDictService faultInputDictService;

    @Autowired
    IRemoteService remoteService;

    /**
     * @author: wuyuechang
     * @Description: ????????????
     * @param: unitCode    ?????????
     * @param: dayPlanID    ?????????id
     * @param: workerId    ????????????
     * @param: deptCode    ??????code
     * @date: 2021/4/29 15:15
     */
    @PostMapping(value = "/add", produces = "text/plain")
    @ApiOperation("?????????????????????")
    @BussinessLog(value = "?????????????????????", key = "/mobileFault/add", type = "04")
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
            result.put("msg", "??????");
            result.put("data", "");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "???????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @return ????????????
     * @author: wuyuechang
     * @Description: ?????????????????????
     * @param: unitCode    ?????????
     * @param: dayPlanID    ?????????id
     * @param: workerId    ????????????
     * @param: deptCode    ??????code
     * @date: 2021/4/29 15:15
     */
    @GetMapping(value = "/faultSearch", produces = "text/plain")
    @ApiOperation("?????????????????????")
    @BussinessLog(value = "?????????????????????", key = "/mobileFault/faultSearch", type = "04")
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
            result.put("msg", "??????");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "???????????????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /**
     * @return ????????????
     * @author: wuyuechang
     * @Description: ??????????????????????????????????????????
     * @date: 2021/4/29 15:15
     */
    @PostMapping(value = "/addFaultFind", produces = "text/plain")
    @ApiOperation("??????????????????????????????????????????")
    @BussinessLog(value = "???????????????????????????????????????????????????", key = "/mobileFault/addFaultFind", type = "04")
    public String addFaultFind(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            phoneFaultService.addFaultFind(param);
            result.put("msg", "??????");
            result.put("data", "");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "????????????????????????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @PostMapping(value = "/addFaultQa", produces = "text/plain")
    @ApiOperation("??????????????????")
    @BussinessLog(value = "??????????????????", key = "/mobileFault/addFaultQa", type = "04")
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
                throw RestRequestException.normalFail("??????,????????????");
            }
        } catch (Exception e) {
            logger.error("/mobileFault/addFaultQa----????????????????????????????????????...", e);
            restResult = RestResult.fromException(e, logger, "????????????????????????");
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), JSON.toJSONString(restResult));
    }

    /**
     * @author: wuyuechang
     * @Description: ????????????????????????????????????
     * @date: 2021/4/29 15:15
     */
    @PostMapping(value = "/addFaultDeal", produces = "text/plain")
    @ApiOperation("????????????????????????????????????")
    @BussinessLog(value = "????????????????????????????????????", key = "/mobileFault/addFaultDeal", type = "04")
    public String addFaultDeal(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            phoneFaultService.addFaultDeal(param);
            result.put("msg", "????????????");
            result.put("data", "");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            RestResult restResult = RestResult.fromException(e, log, "??????????????????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    /***
     * @author: ??????
     * @desc: ????????????????????????????????????
     * @date: 2021/8/30
     * @param: [RWMT]
     * @return: java.lang.String
     */
    @PostMapping(value = "/getFaultInputDictList", produces = "text/plain")
    @ApiOperation("????????????????????????????????????")
    @BussinessLog(value = "????????????????????????????????????", key = "/mobileFault/getFaultInputDictList", type = "04")
    public String getFaultInputDictList(@RequestParam String RWMT) {
        logger.info("/mobileFault/getFaultInputDictList----???????????????????????????????????????????????????????????????...");
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
            result.put("msg", "??????");
            result.put("data", faultInputDictList);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultInputDictList----???????????????????????????????????????????????????????????????..." + e.getMessage());
            RestResult restResult = RestResult.fromException(e, log, "???????????????????????????????????????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        logger.info("/mobileFault/getFaultInputDictList----???????????????????????????????????????????????????????????????...");
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @PostMapping(value = "/searchQaPage", produces = "text/plain")
    @ApiOperation("????????????????????????")
    @BussinessLog(value = "????????????????????????", key = "/mobileFault/searchQaPage", type = "04")
    public String searchQaPage(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            // JSONObject param = JSONObject.parseObject(key);
            QueryFaultQA queryFaultQA = JSONObject.parseObject(key).toJavaObject(QueryFaultQA.class);
            RemoteServiceImpl.ResponseForFaultService<Page<JSONObject>> response = remoteService.searchQaPage(queryFaultQA);
            result.put("msg", "??????");
            result.put("data", response);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/searchQaPage----??????????????????????????????????????????...", e);
            RestResult restResult = RestResult.fromException(e, logger, "??????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getFaultSource", produces = "text/plain")
    @ApiOperation("??????????????????")
    @BussinessLog(value = "??????????????????", key = "/mobileFault/getFaultSource", type = "04")
    public String getFaultSource() {
        JSONObject result = new JSONObject();
        try {
            List<FaultSource> res = remoteService.getFaultSource();
            result.put("msg", "??????");
            result.put("data", res);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultSource----????????????????????????????????????...", e);
            RestResult restResult = RestResult.fromException(e, logger, "??????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getFaultSourceNew", produces = "text/plain")
    @ApiOperation("??????????????????(???)")
    @BussinessLog(value = "??????????????????(???)", key = "/mobileFault/getFaultSourceNew", type = "04")
    public String getFaultSourceNew() {
        JSONObject result = new JSONObject();
        try {
            JSONArray data = remoteService.getFaultSourceNew();
            result.put("msg", "??????");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultSourceNew----????????????????????????(???)????????????...", e);
            RestResult restResult = RestResult.fromException(e, logger, "??????????????????(???)????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getDealWith", produces = "text/plain")
    @ApiOperation("????????????????????????")
    @BussinessLog(value = "????????????????????????", key = "/mobileFault/getDealWith", type = "04")
    public String getDealWith(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            String dealWithType = param.getString("dealWithType");
            JSONArray data = remoteService.getDealWith(dealWithType);
            result.put("msg", "??????");
            result.put("data", data);
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getDealWith----????????????????????????????????????...", e);
            RestResult restResult = RestResult.fromException(e, logger, "????????????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/selectQaRecordPage", produces = "text/plain")
    @ApiOperation("??????????????????")
    @BussinessLog(value = "??????????????????", key = "/mobileFault/selectQaRecordPage", type = "04")
    public String selectQaRecordPage(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            QueryFaultQaRecord queryModel = JSON.parseObject(key, QueryFaultQaRecord.class);
            Page<JSONObject> qaRecordPage = remoteService.selectQaRecordPage(queryModel);
            result.put("data", qaRecordPage);
            result.put("msg", "??????");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/selectQaRecordPage----????????????????????????????????????...", e);
            RestResult restResult = RestResult.fromException(e, logger, "????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getPersonByDept", produces = "text/plain")
    @ApiOperation("???????????????????????????????????????")
    @BussinessLog(value = "???????????????????????????????????????", key = "/mobileFault/getPersonByDept", type = "04")
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
            result.put("msg", "??????");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getPersonByDept----?????????????????????????????????????????????????????????...", e);
            RestResult restResult = RestResult.fromException(e, logger, "?????????????????????????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @GetMapping(value = "/getFaultDataByFaultId", produces = "text/plain")
    @ApiOperation("??????????????????")
    @BussinessLog(value = "??????????????????", key = "/mobileFault/getFaultDataByFaultId", type = "04")
    public String getFaultDataByFaultId(@RequestParam String RWMT) {
        JSONObject result = new JSONObject();
        try {
            String key = DESUtil.decode(trainRepairMobileProperties.getDeskey(), RWMT);
            JSONObject param = JSONObject.parseObject(key);
            JSONArray faultData = remoteService.getFaultDataByFaultId(param.getString("faultId"));
            result.put("data", faultData);
            result.put("msg", "??????");
            result.put("code", RestResponseCodeEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("/mobileFault/getFaultDataByFaultId----??????????????????????????????...", e);
            RestResult restResult = RestResult.fromException(e, logger, "????????????????????????");
            result = RestResultUtils.RestResultChangeJSONObject(restResult);
        }
        return DESUtil.encode(trainRepairMobileProperties.getDeskey(), result.toJSONString());
    }

    @PostMapping(value = "/filedownload", produces = "text/plain")
    @ApiOperation("??????????????????")
    @BussinessLog(value = "??????????????????", key = "/mobileFault/filedownload", type = "04")
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
            logger.error("/mobileFault/filedownload----???????????????...", e);
            throw new HussarException(BizExceptionEnum.FILE_NOT_FOUND);
        } catch (UnsupportedEncodingException e) {
            logger.error("/mobileFault/filedownload----????????????????????????...", e);
            throw new HussarException(BizExceptionEnum.DOWNLOAD_ERROR);
        } catch (IOException e) {
            logger.error("/mobileFault/filedownload----IO??????...", e);
            throw new HussarException(BizExceptionEnum.DOWNLOAD_ERROR);
        }
    }

}
