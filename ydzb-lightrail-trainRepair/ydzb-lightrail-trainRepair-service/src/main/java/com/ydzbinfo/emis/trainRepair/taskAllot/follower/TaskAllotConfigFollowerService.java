package com.ydzbinfo.emis.trainRepair.taskAllot.follower;

import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigEnableReceiveCloudDataCondition;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSink;
import com.ydzbinfo.emis.trainRepair.taskAllot.constant.TaskAllotConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.AggItemConfigModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAggItemConfigService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyCOneallotConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @Description:
 * @Data: 2021/10/8
 * @Author: 冯帅
 */
@Component
@Conditional(TaskAllotConfigEnableReceiveCloudDataCondition.class)
public class TaskAllotConfigFollowerService {

    //日志服务
    protected static final Logger logger = LoggerFactory.getLogger(TaskAllotConfigFollowerService.class);

    //一级修派工配置服务
    @Autowired
    IXzyCOneallotConfigService xzyCOneallotConfigService;

    //二级修派工配置服务
    @Autowired
    IAggItemConfigService iAggItemConfigService;


    //一级修派工配置修改
    @Transactional
    @StreamListener(target = TaskAllotConfigMqSink.TASKALLOTONECONFIG_INPUT, condition = "headers['operateType']=='" + TaskAllotConfigHeaderConstant.ONECREATE + "'")
    public void receiveOneConfigData(XzyCOneallotConfig oneallotConfig) {
        //更新数据库中的数据
        xzyCOneallotConfigService.setOneAllotConfig(oneallotConfig);
    }

    //二级修派工配置新增
    @Transactional
    @StreamListener(target = TaskAllotConfigMqSink.TASKALLOTTWOCINFIG_INPUT, condition = "headers['operateType']=='" + TaskAllotConfigHeaderConstant.TWOCREATE + "'")
    public void receiveCreateTwoConfigData(AggItemConfigModel aggItemConfigModel) {
        try {
            if (!ObjectUtils.isEmpty(aggItemConfigModel)) {
                //调用删除接口
                boolean updFlag = iAggItemConfigService.addAggConfigModel(aggItemConfigModel);
                if (updFlag) {
                    logger.info("二级修派工配置从kafka同步新增数据成功");
                } else {
                    logger.info("一级修派工配置从kafka同步新增数据失败");
                }
            }
        } catch (Exception ex) {
            logger.error("一级修派工配置从kafka同步新增数据出错：" + ex);
        }
    }

    //二级修派工配置修改
    @Transactional
    @StreamListener(target = TaskAllotConfigMqSink.TASKALLOTTWOCINFIG_INPUT, condition = "headers['operateType']=='" + TaskAllotConfigHeaderConstant.TWOUPDATE + "'")
    public void receiveUpdateConfigData(AggItemConfigModel aggItemConfig) {
        try {
            if (!ObjectUtils.isEmpty(aggItemConfig)) {
                //调用删除接口
                boolean updFlag = iAggItemConfigService.updateAggConfigModel(aggItemConfig);
                if (updFlag) {
                    logger.info("二级修派工配置从kafka同步修改数据成功");
                } else {
                    logger.info("一级修派工配置从kafka同步修改数据失败");
                }
            }
        } catch (Exception ex) {
            logger.error("一级修派工配置从kafka同步修改数据出错：" + ex);
        }
    }

    //二级修派工配置删除
    @Transactional
    @StreamListener(target = TaskAllotConfigMqSink.TASKALLOTTWOCINFIG_INPUT, condition = "headers['operateType']=='" + TaskAllotConfigHeaderConstant.TWODELETE + "'")
    public void receiveDeleteTwoConfigData(AggItemConfig aggItemConfig) {
        try {
            if (!ObjectUtils.isEmpty(aggItemConfig)) {
                //调用删除接口
                boolean deleteFlag = iAggItemConfigService.delAggItemConfig(aggItemConfig);
                if (deleteFlag) {
                    logger.info("二级修派工配置从kafka同步删除数据成功");
                } else {
                    logger.info("一级修派工配置从kafka同步删除数据失败");
                }
            }
        } catch (Exception ex) {
            logger.error("一级修派工配置从kafka同步删除数据出错：" + ex);
        }
    }
}
