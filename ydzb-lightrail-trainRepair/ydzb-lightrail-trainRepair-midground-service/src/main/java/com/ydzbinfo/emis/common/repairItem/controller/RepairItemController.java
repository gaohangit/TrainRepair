package com.ydzbinfo.emis.common.repairItem.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.common.bill.billconfig.service.ITemplateProcessService;
import com.ydzbinfo.emis.common.repairItem.service.IRepairItemService;
import com.ydzbinfo.emis.common.repairItem.utils.ItemKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcess;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkQuery;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkVo;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.extend.RepairItemQueryModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * @description: 项目管理
 * @date: 2021/11/5
 * @author: 冯帅
 */
@RestController
@RequestMapping("/repairItem")
public class RepairItemController {

    protected static final Logger logger = LoggerFactory.getLogger(RepairItemController.class);


    @Resource
    IRemoteService remoteService;

    @Autowired
    IRepairItemService repairItemService;

    @Autowired
    ITemplateProcessService templateProcessService;

    /**
     * 查询是否使用新项目
     *
     * @author 张天可
     */
    @GetMapping("/useNewItem")
    @ResponseBody
    @ApiOperation(value = "查询是否使用新项目", notes = "查询是否使用新项目")
    @BussinessLog(value = "查询是否使用新项目", key = "/repairItem/useNewItem", type = "04")
    public RestResult useNewItem() {
        try {
            return RestResult.success().setData(repairItemService.useNewItem());
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "方法描述失败");
        }
    }

    private int getItemPageNum(int itemIndex, int pageSize) {
        return itemIndex / pageSize + 1;
    }

    @ApiOperation(value = "获取每个批次对应的检修项目列表")
    @PostMapping("/selectRepairItemListByWorkParam")
    public RestResult selectRepairItemListByWorkParam(@RequestBody RepairItemQueryModel repairItemWorkQuery) {
        RestResult restResult = RestResult.success();
        try {
            if (StringUtils.isBlank(repairItemWorkQuery.getTrainType())) {
                throw RestRequestException.normalFail("车型不能为空");
            }
            if (StringUtils.isBlank(repairItemWorkQuery.getCurrentTemplateTypeCode())) {
                throw RestRequestException.normalFail("单据类型不能为空");
            }
            StopWatch stopWatch_ = new StopWatch();
            stopWatch_.start("调用前");
            //获取派工配置二级修是否启用新项目

            Page<RepairItemWorkVo> resPage;
            // 查询所有已配置过的项目
            Set<ItemKey> existItemKeys = MybatisPlusUtils.selectList(
                templateProcessService,
                eqParam(TemplateProcess::getTemplateTypeCode, repairItemWorkQuery.getCurrentTemplateTypeCode())
            ).stream().map(
                v -> new ItemKey(v.getItemCode(), v.getTrainsetType(), v.getBatch())
            ).collect(Collectors.toSet());

            String allocatedTempStatus = repairItemWorkQuery.getAllocatedTempStatus();

            if (repairItemService.useNewItem()) {
                RepairItemWorkQuery innerRepairItemWorkQuery = new RepairItemWorkQuery();
                CommonUtils.copyPropertiesToOther(
                    repairItemWorkQuery, innerRepairItemWorkQuery, RepairItemWorkQuery.class
                );
                innerRepairItemWorkQuery.setPage(1);
                innerRepairItemWorkQuery.setLimit(1000000);
                stopWatch_.stop();
                stopWatch_.start("远程调用项目获取接口");
                List<RepairItemWorkVo> resList = remoteService.selectRepairItemListByWorkParam(innerRepairItemWorkQuery).getRecords();
                stopWatch_.stop();
                stopWatch_.start("调用后");

                resList.forEach(v -> {
                    if (existItemKeys.contains(new ItemKey(v.getItemCode(), v.getTrainType(), v.getTrainBatch()))) {
                        v.setAllocatedTempStatus("1");
                    } else {
                        v.setAllocatedTempStatus("0");
                    }
                });
                Predicate<RepairItemWorkVo> filter;
                if (StringUtils.isNotBlank(allocatedTempStatus)) {
                    filter = v -> v.getAllocatedTempStatus().equals(allocatedTempStatus);
                } else {
                    filter = v -> true;
                }
                List<RepairItemWorkVo> filteredList = resList.stream().filter(filter).collect(Collectors.toList());
                String positionItemCode = repairItemWorkQuery.getPositionItemCode();
                String positionItemBatch = repairItemWorkQuery.getPositionItemBatch();
                Integer pageNum = repairItemWorkQuery.getPage();
                Integer pageSize = repairItemWorkQuery.getLimit();
                if (StringUtils.isNotBlank(positionItemCode)) {
                    int positionItemIndex = CommonUtils.findIndex(
                        filteredList,
                        v -> {
                            if (v.getItemCode().equals(positionItemCode)) {
                                String trainBatch = v.getTrainBatch();
                                if (StringUtils.isBlank(trainBatch) && StringUtils.isBlank(positionItemBatch)) {
                                    return true;
                                } else {
                                    return Objects.equals(trainBatch, positionItemBatch);
                                }
                            } else {
                                return false;
                            }
                        }
                    );
                    if (positionItemIndex != -1) {
                        pageNum = getItemPageNum(positionItemIndex, pageSize);
                    }
                }
                resPage = CommonUtils.getPage(filteredList, pageNum, pageSize);
            } else {
                Page<RepairItemVo> page;
                String positionItemCode = repairItemWorkQuery.getPositionItemCode();
                if (StringUtils.isNotBlank(positionItemCode)) {
                    Integer pageNum = repairItemWorkQuery.getPage();
                    Integer pageSize = repairItemWorkQuery.getLimit();
                    List<RepairItemVo> allRepairItemVos = repairItemService.selectRepairItemList(
                        repairItemWorkQuery.getTrainType(),
                        repairItemWorkQuery.getItemName(),
                        repairItemWorkQuery.getItemCode(),
                        StringUtils.isNotBlank(allocatedTempStatus) ? existItemKeys : null,
                        StringUtils.isBlank(allocatedTempStatus) || allocatedTempStatus.equals("1"),
                        new Page<>(1, 1000000)
                    );
                    int positionItemIndex = CommonUtils.findIndex(allRepairItemVos, v -> v.getItemCode().equals(positionItemCode));
                    if (positionItemIndex != -1) {
                        pageNum = getItemPageNum(positionItemIndex, pageSize);
                    }
                    page = CommonUtils.getPage(allRepairItemVos, pageNum, pageSize);
                } else {
                    Integer pageNum = repairItemWorkQuery.getPage();
                    Integer pageSize = repairItemWorkQuery.getLimit();
                    page = new Page<>(pageNum, pageSize);
                    List<RepairItemVo> repairItemVos = repairItemService.selectRepairItemList(
                        repairItemWorkQuery.getTrainType(),
                        repairItemWorkQuery.getItemName(),
                        repairItemWorkQuery.getItemCode(),
                        StringUtils.isNotBlank(allocatedTempStatus) ? existItemKeys : null,
                        StringUtils.isBlank(allocatedTempStatus) || allocatedTempStatus.equals("1"),
                        page
                    );
                    page.setRecords(repairItemVos);
                }

                List<RepairItemWorkVo> resList = page.getRecords().stream().map(item -> {
                    RepairItemWorkVo resItem = new RepairItemWorkVo();
                    resItem.setItemId(item.getItemId());
                    resItem.setItemCode(item.getItemCode());
                    resItem.setItemName(item.getItemName());
                    resItem.setTrainType(item.getTrainsetType());
                    if (existItemKeys.contains(new ItemKey(item.getItemCode(), item.getTrainsetType(), null))) {
                        resItem.setAllocatedTempStatus("1");
                    } else {
                        resItem.setAllocatedTempStatus("0");
                    }
                    return resItem;
                }).collect(Collectors.toList());
                resPage = CommonUtils.convertToPage(resList, page.getTotal(), page.getCurrent(), page.getSize());
            }
            restResult.setMsg("获取成功");
            restResult.setData(resPage);
            stopWatch_.stop();
            if (logger.isDebugEnabled()) {
                logger.debug(stopWatch_.prettyPrint());
            }
            return restResult;
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取检修项目错误");
        }
    }
}
