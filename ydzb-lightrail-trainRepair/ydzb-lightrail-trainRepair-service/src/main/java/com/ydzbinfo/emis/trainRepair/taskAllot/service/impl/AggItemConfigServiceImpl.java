package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.configs.kafka.workcriterion.WorkCriterionMqSource;
import com.ydzbinfo.emis.trainRepair.taskAllot.constant.TaskAllotConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.AggItemConfigItemMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.AggItemConfigMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.AggItemConfigModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfigItem;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAggItemConfigItemService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAggItemConfigService;
import com.ydzbinfo.emis.utils.LockUtil;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-03-17
 */
@Transactional
@Service
public class AggItemConfigServiceImpl extends ServiceImpl<AggItemConfigMapper, AggItemConfig> implements IAggItemConfigService {

    protected static final Logger logger = LoggerFactory.getLogger(AggItemConfigServiceImpl.class);

    private final LockUtil lockUtil = LockUtil.newInstance();//加锁工具类

    @Resource
    private AggItemConfigMapper aggItemConfigMapper;
    @Resource
    private AggItemConfigItemMapper aggItemConfigItemMapper;
    @Resource
    private IAggItemConfigItemService iAggItemConfigItemService;

    @Autowired(required = false)
    private WorkCriterionMqSource workCriterionSource;

    @Override
    public List<AggItemConfigModel> getAggItemConfigList(AggItemConfigModel aggItemConfig) {
        return aggItemConfigMapper.getAggItemConfigList(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(aggItemConfig, AggItemConfigModel::getAggPacketName, AggItemConfigModel::getItemName));
    }

    @Override
    public void addAggItemConfig(AggItemConfigModel aggItemConfigModel) {
        Set<Object> lockKeys = new HashSet<>();
        try{
            //0.给参数资源加锁，防止重复入库
            lockKeys.add(aggItemConfigModel);
            lockUtil.getDoLock(aggItemConfigModel).lock();
            //1.根据车型、批次、所编码查询数据
            AggItemConfigModel queryAggItemConfigModel = new AggItemConfigModel();
            queryAggItemConfigModel.setTrainType(aggItemConfigModel.getTrainType());
            queryAggItemConfigModel.setStageId(aggItemConfigModel.getStageId());
            queryAggItemConfigModel.setUnitCode(aggItemConfigModel.getUnitCode());
            List<AggItemConfigModel> queryDataAggItemConfigModelList = this.getAggItemConfigList(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(queryAggItemConfigModel, AggItemConfigModel::getAggPacketName, AggItemConfigModel::getItemName));

            /**验证交集要验证到辆序级别，暂时将此段代码注释掉
            List<String> paramItemCodes = aggItemConfigModel.getAggItemConfigItems().stream().map(v -> v.getItemCode()).collect(Collectors.toList());
            queryDataAggItemConfigModelList.forEach(n -> {
                List<String> dataParams = n.getAggItemConfigItems().stream().map(v -> v.getItemCode()).collect(Collectors.toList());
                boolean hasExists = dataParams.stream().anyMatch(v -> paramItemCodes.contains(v));
                if (hasExists) {
                    lockUtil.unlock(aggItemConfigModel);
                    throw new RuntimeException("已添加项目和["+n.getAggPacketName()+"]专业分工中的项目存在冲突，无法保存");
                }
            });*/


            //因不确定同一个车型批次是否能重复配置同一个项目的专业分工，暂时将此段代码注释掉此段代码先不提交
            //统一车型批次能重复配置同一个项目的专业分工，只要是辆序没有交集就可以
            //2.验证是否存在交集
            //2.1获取新添加的项目集合及项目编码集合
            //新增配置的辆序集合
            String addCarNos = aggItemConfigModel.getAggPacketCar();
            List<String> addCarNoList = new ArrayList<>();
            if (!ObjectUtils.isEmpty(addCarNos)) {
                addCarNoList = Arrays.asList(addCarNos.split(","));
            }
            List<AggItemConfigItem> addItems = aggItemConfigModel.getAggItemConfigItems();
            if (CollectionUtils.isEmpty(addItems)) {
                throw new RuntimeException("请至少选择一个项目!");
            }
            //2.2循环查询出来的数据
            List<String> finalAddCarNoList = addCarNoList;
            queryDataAggItemConfigModelList.forEach(n -> {
                //获取数据库中已存在的辆序集合
                String exitCarNos = n.getAggPacketCar();
                List<String> exitCarNoList = new ArrayList<>();
                if (!ObjectUtils.isEmpty(exitCarNos)) {
                    exitCarNoList = Arrays.asList(exitCarNos.split(","));
                }
                //2.3获取已存在的项目集合
                List<AggItemConfigItem> exitItems = n.getAggItemConfigItems();
                //2.4循环已存在的项目集合,判断是否存在交集
                if (!CollectionUtils.isEmpty(exitItems)) {
                    for (AggItemConfigItem exitItem : exitItems) {
                        String exitItemCode = exitItem.getItemCode();
                        AggItemConfigItem retainAllItem = addItems.stream().filter(t -> exitItemCode.equals(t.getItemCode())).findFirst().orElse(null);
                        if (!ObjectUtils.isEmpty(retainAllItem)) {
                            List<String> finalExitCarNoList = exitCarNoList;
                            boolean exist = finalAddCarNoList.stream().anyMatch(v -> finalExitCarNoList.contains(v));
                            if (exist) {
                                String msg="“"+retainAllItem.getItemName()+"“项目辆序和“"+n.getAggPacketName()+"”专业分工存在冲突，无法保存";
                                throw new RuntimeException(msg);
                            }
                        }
                    }
                }
            });
            //获取当前登录用户
            ShiroUser currentUser = ShiroKit.getUser();
            //获取当前时间
            Date currentDate = new Date();
            //组织主表数据
            aggItemConfigModel.setPacketId(UUID.randomUUID().toString());
            aggItemConfigModel.setFlag("0");
            aggItemConfigModel.setAggPacketCode(UUID.randomUUID().toString());
            aggItemConfigModel.setCreateUserCode(currentUser.getStaffId());
            aggItemConfigModel.setCreateUserName(currentUser.getName());
            aggItemConfigModel.setCreateTime(currentDate);
            aggItemConfigModel.setTaskType("2");
            //组织项目表集合数据
            if (!CollectionUtils.isEmpty(aggItemConfigModel.getAggItemConfigItems())) {
                aggItemConfigModel.getAggItemConfigItems().forEach(configItem -> {
                    configItem.setPitemId(UUID.randomUUID().toString());
                    configItem.setFlag("0");
                    configItem.setPacketId(aggItemConfigModel.getPacketId());
                });
            }
            boolean insertFlag = this.addAggConfigModel(aggItemConfigModel);
            if (SpringCloudStreamUtil.enableSendCloudData(WorkCriterionMqSource.class)) {
                this.sendTwoCreateData(aggItemConfigModel);
            }
            lockUtil.unlock(aggItemConfigModel);
        }finally {
            for (Object lockKey : lockKeys) {
                lockUtil.unlockAll(lockKey);
            }
        }
    }

    @Override
    public boolean addAggConfigModel(AggItemConfigModel aggItemConfigModel) {
        //插入专业分工配置表
        AggItemConfig aggItemConfig = new AggItemConfig();
        BeanUtils.copyProperties(aggItemConfigModel, aggItemConfig);
        Integer insertCount = aggItemConfigMapper.insert(aggItemConfig);
        //批量插入专业分工项目表
        if (!CollectionUtils.isEmpty(aggItemConfigModel.getAggItemConfigItems())) {
            boolean insertFlag = iAggItemConfigItemService.insertBatch(aggItemConfigModel.getAggItemConfigItems());
        }
        return true;
    }

    @Override
    public boolean sendTwoCreateData(AggItemConfigModel aggItemConfigModel) {
        boolean sendFlag = false;
        try {
            //将数据集合推送到kafka中
            MessageWrapper<AggItemConfigModel> messageWrapper = new MessageWrapper<>(TaskAllotConfigHeaderConstant.class, TaskAllotConfigHeaderConstant.TWODELETE, aggItemConfigModel);
            sendFlag = MessageUtil.sendMessage(workCriterionSource.workcritertiononeChannel(), messageWrapper);
            if (sendFlag) {
                logger.info("二级修派工配置往kafka推送新增数据成功");
            } else {
                logger.info("二级修派工配置往kafka推送新增数据失败");
            }
        } catch (Exception ex) {
            sendFlag = false;
            logger.error("二级修派工配置往kafka推送新增数据出错" + ex);
        }
        return sendFlag;
    }

    @Override
    public boolean delAggItemConfig(AggItemConfig aggItemConfig) {
        aggItemConfig.setFlag("1");
        aggItemConfigMapper.updateById(aggItemConfig);
        iAggItemConfigItemService.delAggItemConfigItem(aggItemConfig.getPacketId());
        return true;
    }

    //向kafka推送删除数据
    @Override
    public boolean sendTwoDeleteData(AggItemConfig aggItemConfig) {
        boolean sendFlag = false;
        try {
            //将数据集合推送到kafka中
            MessageWrapper<AggItemConfig> messageWrapper = new MessageWrapper<>(TaskAllotConfigHeaderConstant.class, TaskAllotConfigHeaderConstant.TWODELETE, aggItemConfig);
            sendFlag = MessageUtil.sendMessage(workCriterionSource.workcritertiononeChannel(), messageWrapper);
            if (sendFlag) {
                logger.info("二级修派工配置往kafka推送删除数据成功");
            } else {
                logger.info("二级修派工配置往kafka推送删除数据失败");
            }
        } catch (Exception ex) {
            sendFlag = false;
            logger.error("二级修派工配置往kafka推送删除数据出错" + ex);
        }
        return sendFlag;
    }


    @Override
    public void updAggItemConfig(AggItemConfigModel aggItemConfigModel) {
        //组织要插入的专业分工项目对象集合
        List<AggItemConfigItem> aggItemConfigItemList = new ArrayList<>();
        for (AggItemConfigItem itemConfigItem : aggItemConfigModel.getAggItemConfigItems()) {
            itemConfigItem.setPitemId(UUID.randomUUID().toString());
            itemConfigItem.setPacketId(aggItemConfigModel.getPacketId());
            itemConfigItem.setFlag("0");
            aggItemConfigItemList.add(itemConfigItem);
        }
        boolean insertItemFlag = this.updateAggConfigModel(aggItemConfigModel);
        //向kafka中推送修改数据
        if (insertItemFlag && SpringCloudStreamUtil.enableSendCloudData(WorkCriterionMqSource.class)) {
            this.sendTwoUpdateData(aggItemConfigModel);
        }
    }

    //批量插入专业分工项目
    @Override
    public boolean updateAggConfigModel(AggItemConfigModel aggItemConfigModel) {
        //1.删除
        MybatisPlusUtils.delete(
            aggItemConfigItemMapper,
            eqParam(AggItemConfigItem::getPacketId, aggItemConfigModel.getPacketId())
        );
        //2.插入
        List<AggItemConfigItem> aggItemConfigItemList = aggItemConfigModel.getAggItemConfigItems();
        if (!CollectionUtils.isEmpty(aggItemConfigItemList)) {
            iAggItemConfigItemService.insertBatch(aggItemConfigModel.getAggItemConfigItems());
        }
        return true;
    }

    //向kafka推送修改数据
    @Override
    public boolean sendTwoUpdateData(AggItemConfigModel aggItemConfigModel) {
        boolean sendFlag = false;
        try {
            //将数据集合推送到kafka中
            MessageWrapper<AggItemConfigModel> messageWrapper = new MessageWrapper<>(TaskAllotConfigHeaderConstant.class, TaskAllotConfigHeaderConstant.TWOUPDATE, aggItemConfigModel);
            sendFlag = MessageUtil.sendMessage(workCriterionSource.workcritertiononeChannel(), messageWrapper);
            if (sendFlag) {
                logger.info("二级修派工配置往kafka推送修改数据成功");
            } else {
                logger.info("二级修派工配置往kafka推送修改数据失败");
            }
        } catch (Exception ex) {
            sendFlag = false;
            logger.error("二级修派工配置往kafka推送修改数据出错" + ex);
        }
        return sendFlag;
    }
}
