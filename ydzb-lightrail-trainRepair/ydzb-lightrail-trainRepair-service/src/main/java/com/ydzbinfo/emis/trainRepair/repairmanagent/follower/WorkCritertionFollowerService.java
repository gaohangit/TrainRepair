package com.ydzbinfo.emis.trainRepair.repairmanagent.follower;

import com.ydzbinfo.emis.configs.kafka.workcriterion.WorkCriterionEnableReceiveCloudDataCondition;
import com.ydzbinfo.emis.configs.kafka.workcriterion.WorkCriterionMqSink;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.repairmanagent.constant.WorkCritertionHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/10/9
 * @Author: 冯帅
 */
@Component
@Conditional(WorkCriterionEnableReceiveCloudDataCondition.class)
public class WorkCritertionFollowerService {

    protected static final Logger logger = LoggerFactory.getLogger(WorkCritertionFollowerService.class);

    @Autowired
    private IXzyCWorkcritertionService xzyCWorkcritertionService;

    //接收一级修新增数据
    @Transactional
    @StreamListener(target = WorkCriterionMqSink.WORKCRITERTIONONE_INPUT, condition = "headers['operateType']=='" + WorkCritertionHeaderConstant.CREATE_ONE + "'")
    public void receiveOneCreateData(XzyCWorkcritertion xzyCWorkcritertion) {
        try {
            if(!ObjectUtils.isEmpty(xzyCWorkcritertion)){
                //2.调用新增接口
                boolean insertFlag = xzyCWorkcritertionService.addModel(xzyCWorkcritertion);
                if(insertFlag){
                    logger.info("一级修作业标准配置从kafka接收新增数据成功");
                }else{
                    logger.info("一级修作业标准配置从kafka接收新增数据失败");
                }
            }
        }catch (Exception ex){
            logger.error("一级修作业标准配置从kafka接收数据插入出错"+ex);
        }
    }

    //接收一级修删除数据
    @Transactional
    @StreamListener(target = WorkCriterionMqSink.WORKCRITERTIONONE_INPUT, condition = "headers['operateType']=='" + WorkCritertionHeaderConstant.DELETE_ONE + "'")
    public void receiveOneDeleteData(List<XzyCWorkcritertion> xzyCWorkcritertionList) {
        try {
            if(!CollectionUtils.isEmpty(xzyCWorkcritertionList)){
                boolean delFlag = xzyCWorkcritertionService.delModel(xzyCWorkcritertionList);
                if(delFlag){
                    logger.info("一级修作业标准配置从kafka接收删除数据成功");
                }else{
                    logger.info("一级修作业标准配置从kafka接收删除数据失败");
                }
            }
        }catch (Exception ex){
            logger.error("一级修作业标准配置从kafka接收删除数据出错"+ex);
        }
    }
}
