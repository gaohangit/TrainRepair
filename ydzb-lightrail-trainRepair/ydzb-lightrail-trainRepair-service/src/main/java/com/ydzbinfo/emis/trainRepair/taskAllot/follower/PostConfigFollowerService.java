package com.ydzbinfo.emis.trainRepair.taskAllot.follower;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigEnableReceiveCloudDataCondition;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSink;
import com.ydzbinfo.emis.trainRepair.taskAllot.constant.PostConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPostService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/10/8
 * @Author: 冯帅
 */
@Component
@Conditional(TaskAllotConfigEnableReceiveCloudDataCondition.class)
public class PostConfigFollowerService {

    protected static final Logger logger = LoggerFactory.getLogger(PostConfigFollowerService.class);

    @Autowired
    private IPostService iPostService;

    @Transactional
    @StreamListener(target = TaskAllotConfigMqSink.POSTCONFIG_INPUT, condition = "headers['operateType']=='" + PostConfigHeaderConstant.CREATE + "'")
    public void receivePostConfigData(List<Post> postList) {
        try {
            if (!CollectionUtils.isEmpty(postList)) {
                //1.删除岗位表所有数据
                Post delModel = new Post();
                delModel.setFlag("0");
                boolean updateFlag = MybatisPlusUtils.update(
                    iPostService,
                    delModel
                );
                if (updateFlag) {
                    //2.插入岗位表数据
                    boolean insertFlag = iPostService.insertBatch(postList);
                }
            }
        } catch (Exception ex) {
            logger.error("岗位配置同步数据出错：" + ex);
        }
    }
}
